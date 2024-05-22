package tictactoe.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockConcrete;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.TextFormat;
import tictactoe.Plugin;
import tictactoe.game.GameState.FieldState;
import tictactoe.util.Localization;

public class Game {
	
	
	// Constants for Block types used to place the game
	public static int GAME_BLOCK_TYPE = Block.CONCRETE;
	public static BlockColor BASE_PLATE_BLOCK_COLOR = BlockColor.BLACK_BLOCK_COLOR;
	public static BlockColor NEUTRAL_BLOCK_COLOR = BlockColor.WHITE_BLOCK_COLOR;
	public static BlockColor MAIN_PLAYER_BLOCK_COLOR = BlockColor.RED_BLOCK_COLOR;
	public static BlockColor OPPONENT_PLAYER_BLOCK_COLOR = BlockColor.LIGHT_BLUE_BLOCK_COLOR;
	public static int BASE_PLATE_BLOCK_META = 15;
	public static int NEUTRAL_BLOCK_META = 0;
	public static int MAIN_PLAYER_BLOCK_META = 14;
	public static int OPPONENT_PLAYER_BLOCK_META = 3;
	public static int WINCAUSE_BLOCK_META = 4;
	
	// Constants for the sounds played by the game
	public static Sound MARK_FIELD_SOUND = Sound.NOTE_BELL;
	public static float MARK_FIELD_SOUND_PITCH = 0.5f;
	public static Sound WIN_BEEP_SOUND = Sound.NOTE_BIT; // no pitch because it varies
	public static Sound WIN_SOUND = Sound.RANDOM_LEVELUP;
	public static Sound LOSE_SOUND = Sound.MOB_WITHER_HURT;
	public static Sound TIE_SOUND = Sound.NOTE_COW_BELL;
	public static float TIE_SOUND_PITCH = 0.5f;
	public static Sound FIELD_FALL_SOUND = Sound.HIT_STONE;
	public static float FIELD_FALL_SOUND_PITCH = 0.5f;
	
	public static Sound GAME_ACCEPT_SOUND = Sound.NOTE_BANJO;
	public static float GAME_ACCEPT_SOUND_PITCH = 0.5f;

	public static HashMap<UUID, Game> queuedGames = new HashMap<UUID, Game>();
	
	public static List<Game> getRequestsTo(Player opponentPlayer) {
		ArrayList<Game> result = new ArrayList<Game>();
		
		for(Game queuedGame: Game.queuedGames.values()) {
			if(queuedGame.config.opponentPlayer == opponentPlayer) {
				result.add(queuedGame);
			}
		}
		
		return result;
	}
	
	public static Game getQueuedGameWithPlayers(String mainPlayerName, Player opponentPlayer) {
		for(Game queuedGame: Game.queuedGames.values()) {
			if(queuedGame.config.opponentPlayer != opponentPlayer) continue;
			if(queuedGame.config.mainPlayer.getName().equals(mainPlayerName)) {
				return queuedGame;
			}
		}
		
		return null;
	}
	
	public static Game getQueuedGameByUUID(String uuidString) throws IllegalArgumentException {
		UUID gameUUID = UUID.fromString(uuidString);
		return Game.queuedGames.get(gameUUID);
	}
	
	public static HashMap<Player, Game> runningGames = new HashMap<Player, Game>();
	
	public static void cancelAllGames() {
		for(Game runningGame: Set.copyOf(Game.runningGames.values())) {
			runningGame.end(GameEndCause.CANCEL);
		}
	}
	
	public static HashMap<Player, GameConfig> lostGames = new HashMap<Player, GameConfig>();
	
	
	public UUID uuid = UUID.randomUUID();
	
	public GameConfig config;
	public GameListener listener;
	
	public GameState state;
	private boolean didCompletePlace = true;
	public boolean opponentPlayersTurn = true;
	
	public Player getPlayerInTurn() {
		return this.opponentPlayersTurn ? this.config.opponentPlayer : this.config.mainPlayer;
	}
	
	public Location location;
	public CubicBlockArea gameArea;
	
	public PluginTask<Plugin> gravityTask;
	
	private HashMap<Location, Block> beforeGameBlocks = new HashMap<Location, Block>();
	
	public Game(GameConfig config) {
		this.config = config;
		this.location = this.generateGameLocation();
		
		this.listener = new GameListener(this);
		this.state = new GameState(this.config.size);
		
		this.gameArea = this.generateGameArea();
		
		this.gravityTask = new PluginTask<Plugin>(Plugin.instance) {

			@Override
			public void onRun(int arg0) {
				boolean didApplyAnyChange = state.applyGravityTick();
				
				if(didApplyAnyChange) {
					state.applyVisually(location);
					playGameSound(Game.FIELD_FALL_SOUND, Game.FIELD_FALL_SOUND_PITCH);
				}
				
				boolean blockFinishedFalling = !didApplyAnyChange && !didCompletePlace; 
				if(blockFinishedFalling) {
					checkForWin();
					didCompletePlace = true;
				}
			}
			
		};
	}
	
	public void queue(boolean isReturnMatch) {
		this.registerQueued();
		this.inviteOpponent(isReturnMatch);
	}
	
	private void registerQueued() {
		Game.queuedGames.put(this.uuid, this);
	}
	
	
	private Location generateGameLocation() {
		// double type to get rid of casting in the switch statement!
		double gameWidthInBlocks = (double)this.config.size.x * 2 - 1;
		double gameDepthInBlocks = (double)this.config.size.z * 2 - 1;
		
		double offsetX = 0, offsetZ = 0;
		
		switch(this.config.mainPlayer.getDirection()) {
		case NORTH: // towards negative Z
			offsetX = -Math.floor(gameWidthInBlocks / 2);
			offsetZ = -gameDepthInBlocks - 2;
			break;
		case EAST: // towards positive X
			offsetX = 2;
			offsetZ = -Math.floor(gameDepthInBlocks / 2);
			break;
		case SOUTH: // towards positive Z
			offsetX = -Math.floor(gameWidthInBlocks / 2);
			offsetZ = 2;
			break;
		case WEST: // towards negative X
			offsetX = -gameWidthInBlocks - 2;
			offsetZ = -Math.floor(gameDepthInBlocks / 2);
			break;
		default:
				break;
		}
		
		
		Location playerLocation = this.config.mainPlayer.getLocation();
		return new Location(playerLocation.getFloorX() + offsetX, playerLocation.getFloorY(), playerLocation.getFloorZ() + offsetZ, playerLocation.getLevel());
	}
	
	private CubicBlockArea generateGameArea() {
		Location startBlock = new Location(this.location.getFloorX() - 2, this.location.getFloorY() - 1, this.location.getFloorZ() - 2, this.location.getLevel());
		Location endBlock = new Location(this.location.getFloorX() + this.config.size.x * 2, this.location.getFloorY() + this.config.size.y * 2, this.location.getFloorZ() + this.config.size.z * 2, this.location.getLevel());
		return new CubicBlockArea(startBlock, endBlock);
	}
	
	private void inviteOpponent(boolean isReturnMatch) {
		if(isReturnMatch) {
			this.config.opponentPlayer.sendMessage(Localization.localizedString("game.invitation.return", this.config.opponentPlayer.getName(), this.config.mainPlayer.getName()));
		} else {
			this.config.opponentPlayer.sendMessage(Localization.localizedString("game.invitation.normal", this.config.opponentPlayer.getName(), this.config.mainPlayer.getName()));
		}
		
		this.config.opponentPlayer.sendMessage(Localization.localizedString("game.invitation.info", this.config.size.x, this.config.size.y, this.config.size.z, this.config.winRequiredAmount));
		
		this.config.opponentPlayer.sendMessage(Localization.localizedString("game.invitation.accept", this.config.mainPlayer.getName()));
	}
	
	public void start() {
		this.listener.activate();
		
		this.storeCurrentBlocksInGameArea();
		this.placeGameIntoWorld();
		
		Plugin.instance.getServer().getScheduler().scheduleRepeatingTask(this.gravityTask, 10);
		
		this.config.mainPlayer.sendMessage(Localization.localizedString("game.invitation.accepted", this.config.opponentPlayer.getName()));
		
		this.registerStarted();
	}
	
	private void registerStarted() {
		// Mark this game as running
		Game.queuedGames.remove(this.uuid);
		Game.runningGames.put(this.config.mainPlayer, this);
		Game.runningGames.put(this.config.opponentPlayer, this);
		
		// Tell players who have requested a game with either mainPlayer or
		// opponentPlayer that they are not available anymore
		for (Game queuedGame: Game.queuedGames.values()) {
			if (queuedGame.config.opponentPlayer == this.config.opponentPlayer) {
				queuedGame.config.mainPlayer.sendMessage(Localization.localizedString("game.invitatino.not_available_anymore", this.config.opponentPlayer.getName()));
			} else if(queuedGame.config.opponentPlayer == this.config.mainPlayer) {
				queuedGame.config.mainPlayer.sendMessage(Localization.localizedString("game.invitation.not_available_anymore_hosted_own", this.config.mainPlayer.getName()));
			}
		}

		// Remove redundant games:
		Game.queuedGames.entrySet().removeIf(e -> (e.getValue().config.opponentPlayer == this.config.opponentPlayer || e.getValue().config.opponentPlayer == this.config.mainPlayer));
	}
	
	/**
	 * Describes the cause for a tic-tac-toe games ending.
	 */
	public enum GameEndCause {
		MAIN_WIN,
		OPPONENT_WIN,
		TIE,
		CANCEL
	}
	
	public void end(GameEndCause cause) {
		this.listener.deactivate();
		
		this.restoreOldBlocksFromBeforeGame();
		
		// Send cause-specific message
		switch(cause) {
		case CANCEL:
			String message = Localization.localizedString("game.cancelled");
			this.config.mainPlayer.sendMessage(message);
			this.config.opponentPlayer.sendMessage(message);
			break;
		case MAIN_WIN:
			this.config.mainPlayer.sendTitle(Localization.localizedString("game.win.title"),Localization.localizedString("game.win.subtitle"), 10, 100, 10);
			this.playGameSoundToPlayer(Game.WIN_SOUND, 1.0f, this.config.mainPlayer);
			this.config.opponentPlayer.sendTitle(Localization.localizedString("game.lose.title"), Localization.localizedString("game.lose.subtitle"), 10, 100, 10);
			this.playGameSoundToPlayer(Game.LOSE_SOUND, 1.0f, this.config.opponentPlayer);
			this.config.opponentPlayer.sendMessage(Localization.localizedString("game.lose.returnRequestHelp"));
			Game.lostGames.put(this.config.opponentPlayer, new GameConfig(this.config.opponentPlayer, this.config.mainPlayer, this.config.size, this.config.winRequiredAmount));
			break;
		case OPPONENT_WIN:
			this.config.opponentPlayer.sendTitle(Localization.localizedString("game.win.title"), Localization.localizedString("game.win.subtitle"), 10, 100, 10);
			this.playGameSoundToPlayer(Game.WIN_SOUND, 1.0f, this.config.opponentPlayer);
			this.config.mainPlayer.sendTitle(Localization.localizedString("game.lose.title"), Localization.localizedString("game.lose.subtitle"), 10, 100, 10);
			this.playGameSoundToPlayer(Game.LOSE_SOUND, 1.0f, this.config.mainPlayer);
			this.config.mainPlayer.sendMessage(Localization.localizedString("game.lose.returnRequestHelp"));
			Game.lostGames.put(this.config.mainPlayer, new GameConfig(this.config.mainPlayer, this.config.opponentPlayer, this.config.size, this.config.winRequiredAmount));
			break;
		case TIE:
			String tieTitle = Localization.localizedString("game.tie.title");
			String tieMessage = Localization.localizedString("game.tie.subtitle");
			this.config.mainPlayer.sendTitle(tieTitle, tieMessage, 10, 50, 10);
			this.config.opponentPlayer.sendTitle(tieTitle, tieMessage, 10, 50, 10);
			
			this.playGameSound(Game.TIE_SOUND, Game.TIE_SOUND_PITCH);
			
			String returnMatchMessage = Localization.localizedString("game.tie.returnRequestHelp");
			
			this.config.mainPlayer.sendMessage(returnMatchMessage);
			this.config.opponentPlayer.sendMessage(returnMatchMessage);
			
			Game.lostGames.put(this.config.mainPlayer, new GameConfig(this.config.mainPlayer, this.config.opponentPlayer, this.config.size, this.config.winRequiredAmount));
			Game.lostGames.put(this.config.opponentPlayer, new GameConfig(this.config.opponentPlayer, this.config.mainPlayer, this.config.size, this.config.winRequiredAmount));
			break;
		}
		
		this.gravityTask.cancel();
		
		this.registerEnded();
	}
	
	private void registerEnded() {
		Game.runningGames.remove(this.config.mainPlayer);
		Game.runningGames.remove(this.config.opponentPlayer);
	}
	
	private void restoreOldBlocksFromBeforeGame() {
		this.gameArea.forEach((location -> location.getLevel().setBlock(location, this.beforeGameBlocks.get(location))));
	}
	
	
	/**
	 * The current player in turn marks the field at *position*.
	 * @param position
	 */
	public void placeAt(FieldPoint position) {
		if(!this.didCompletePlace) return;
		
		this.didCompletePlace = false;
		
		if(this.state.getStateAt(position) != FieldState.NEUTRAL) return;
		
		this.state.setStateAt(position, this.opponentPlayersTurn ? FieldState.OPPONENT : FieldState.MAIN);
		
		
		Location inWorldLocation = this.state.fieldPointToBlockLocation(this.location, position);
		this.location.getLevel().setBlock(inWorldLocation, new BlockConcrete(this.opponentPlayersTurn ? Game.OPPONENT_PLAYER_BLOCK_META : Game.MAIN_PLAYER_BLOCK_META));
		
		
		this.opponentPlayersTurn = !this.opponentPlayersTurn;
		
		this.playGameSound(Game.MARK_FIELD_SOUND, Game.MARK_FIELD_SOUND_PITCH);
	}
	
	public void checkForWin() {
		
		FieldState potentialWinner = this.state.getWinnerIfAny(this.config.winRequiredAmount);
		if(potentialWinner != FieldState.NEUTRAL) {
			
			this.listener.allowMarkingFields = false;
			this.playEndAnimation();
			
			return;
		}
		
		if(!this.state.winIsPossible()) {
			this.end(GameEndCause.TIE);
		}
	}
	
	public void playEndAnimation() {
		Plugin.instance.getServer().getScheduler().scheduleDelayedRepeatingTask(new Task() {
			
			public int SHOW_CYCLE_COUNT = 10;

			int currentCycle = -1;
			ArrayList<Location> blockLocations = state.getWinRowBlockLocations(config.winRequiredAmount, location);
			
			private boolean isInWaitCycle() {
				return this.currentCycle < 0;
			}
			
			private boolean isInShowCycle() {
				return this.currentCycle >= config.winRequiredAmount && this.currentCycle < (config.winRequiredAmount + this.SHOW_CYCLE_COUNT);
			}
			
			private boolean isInMessageCycle() {
				return this.currentCycle == config.winRequiredAmount;
			}
			
			private boolean didFinishAllCycles() {
				return this.currentCycle >= (config.winRequiredAmount + this.SHOW_CYCLE_COUNT);
			}
			
			private void highlightBlockAt(Location location) {
				/*Location middleLocationOfBlock = new Location(location.getFloorX() + 0.5, location.getFloorY() + 0.5, location.getFloorZ() + 0.5, location.getLevel());
				
				DestroyBlockParticle particle = new DestroyBlockParticle(middleLocationOfBlock, middleLocationOfBlock.getLevelBlock());
				particle.setComponents(1.0f, 1.0f, 1.0f);
				
				for(int i = 0; i < 50; i++) {
					location.getLevel().addParticle(particle);
				}*/
				
				location.getLevel().setBlock(location, new BlockConcrete(Game.WINCAUSE_BLOCK_META));
				
				float currentPitch = 1.0f + (1.0f / ((float)config.winRequiredAmount - 1.0f)) * (float)this.currentCycle;
				playGameSound(Game.WIN_BEEP_SOUND, currentPitch);
			}
			
			@Override
			public void onRun(int currentTick) {
				
				if(this.isInWaitCycle()) {
					this.currentCycle++;
					return;
				}
				
				if(this.isInShowCycle()) {
					if(this.isInMessageCycle()) {
						// opponentPlayersTurn is not yet swapped (see didFinishAllCycles() below)
						Player winningPlayer = opponentPlayersTurn ? config.mainPlayer : config.opponentPlayer;
						Player losingPlayer = opponentPlayersTurn ? config.opponentPlayer : config.mainPlayer;
						
						winningPlayer.sendMessage(Localization.localizedString("game.win.title") + TextFormat.RESET);
						losingPlayer.sendMessage(Localization.localizedString("game.lose.title") + TextFormat.RESET);
					}
					
					this.currentCycle++;
					return;
				}
				
				if(this.didFinishAllCycles()) {
					opponentPlayersTurn = !opponentPlayersTurn;
					
					end(opponentPlayersTurn ? GameEndCause.OPPONENT_WIN : GameEndCause.MAIN_WIN);
					
					this.cancel();
					return;
				}
				
				Location locationOfCurrentBlock = this.blockLocations.get(this.currentCycle);
				this.highlightBlockAt(locationOfCurrentBlock);
				
				currentCycle++;
			}
			
		}, 10, 10);
	}
	
	
	//////////////////BUILDING THE GAME //////////////////
	private void storeCurrentBlocksInGameArea() {
	this.beforeGameBlocks.clear();
	this.gameArea.forEach((location) -> this.beforeGameBlocks.put(location, location.getLevelBlock()));
	}
	
	private void placeGameIntoWorld() {
	this.fillGameAreaWithAir();
	this.placeBasePlateIntoWorld();
	this.placeFieldBlocksIntoWorld();
	}
	
	private void fillGameAreaWithAir() {
	this.gameArea.forEach((block) -> {
		if(block.getLocation().getFloorY() != this.gameArea.startBlock.getFloorY()) {
			block.getLevel().setBlock(block.getLocation(), new BlockAir());
		}
	});
	}
	
	private void placeBasePlateIntoWorld() {
	for(int x = 0; x < this.config.size.x * 2 - 1; x++) {
		for(int z = 0; z < this.config.size.z * 2 - 1; z++) {
			Location currentBlockLocation = new Location(this.location.getFloorX() + x, this.location.getFloorY(), this.location.getFloorZ() + z, this.location.getLevel());
			this.location.getLevel().setBlock(currentBlockLocation, new BlockConcrete(Game.BASE_PLATE_BLOCK_META));
		}
	}
	}
	
	private void placeFieldBlocksIntoWorld() {
	for(int x = 0; x < this.config.size.x; x++) {
		for(int y = 0; y < this.config.size.y; y++) {
			for(int z = 0; z < this.config.size.z; z++) {
				Location currentBlockLocation = new Location(this.location.getFloorX() + x * 2, this.location.getFloorY() + 1 + y * 2, this.location.getFloorZ() + z * 2, this.location.getLevel());
				this.location.getLevel().setBlock(currentBlockLocation, new BlockConcrete(Game.NEUTRAL_BLOCK_META));
			}
		}
	}
	}
	
	
	public void playGameSound(Sound sound, float pitch) {
		this.playGameSoundToPlayer(sound, pitch, this.config.mainPlayer);
		this.playGameSoundToPlayer(sound, pitch, this.config.opponentPlayer);
	}
	
	public void playGameSoundToPlayer(Sound sound, float pitch, Player player) {
		PlaySoundPacket packet = new PlaySoundPacket();
		packet.name = sound.getSound();
		packet.volume = 1.0f;
		packet.pitch = pitch;
		packet.x = player.getLocation().getFloorX();
		packet.y = player.getLocation().getFloorY();
		packet.z = player.getLocation().getFloorZ();
		player.dataPacket(packet);
	}
	
}
