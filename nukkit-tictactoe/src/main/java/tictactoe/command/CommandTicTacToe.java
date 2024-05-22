package tictactoe.command;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import tictactoe.Plugin;
import tictactoe.game.Game;
import tictactoe.game.Game.GameEndCause;
import tictactoe.game.GameConfig;
import tictactoe.util.Localization;
import tictactoe.util.Size;

public class CommandTicTacToe {

	public static int MIN_VALID_ARG_COUNT = 1;
	public static int MAX_VALID_ARG_COUNT = 5;
	
	public static int OPPONENT_ARG_INDEX = 0;
	public static int SIZE_X_ARG_INDEX = 1;
	public static int SIZE_Y_ARG_INDEX = 2;
	public static int SIZE_Z_ARG_INDEX = 3;
	public static int WIN_REQUIRED_AMOUNT_ARG_INDEX = 4;
	
	public static String CANCEL_KEYWORD = "cancel";
	public static String REQUEST_RETURN_MATCH_KEYWORD = "requestReturnMatch";
	
	
	public static int NO_AVAILABLE_PLAYERS_MIN_ARG_COUNT = 3;
	public static String NO_AVAILABLE_PLAYERS_ARGS[] = {"(no", "available", "players)"};
	
	public static boolean handleCommand(Player sender, String[] args) {
		
		if(args.length < CommandTicTacToe.MIN_VALID_ARG_COUNT) {
			return false;
		}
		
		if(args.length > CommandTicTacToe.MAX_VALID_ARG_COUNT) {
			sender.sendMessage(Localization.localizedString("ttt.error.too_many_args"));
			return false;
		}
		
		if(CommandTicTacToe.isNoAvailablePlayersPlaceholder(args)) {
			return true;
		}
		
		boolean playerIsCurrentlyInAGame = Game.runningGames.containsKey((Player)sender);
		boolean playerProvidedCancelKeyword = args[CommandTicTacToe.OPPONENT_ARG_INDEX].equals(CommandTicTacToe.CANCEL_KEYWORD); 
		if(playerIsCurrentlyInAGame && playerProvidedCancelKeyword) {
			Game gameToCancel = Game.runningGames.get((Player)sender); 
			gameToCancel.end(GameEndCause.CANCEL);
			return true;
		}
		
		if(args[CommandTicTacToe.OPPONENT_ARG_INDEX].equals(CommandTicTacToe.REQUEST_RETURN_MATCH_KEYWORD)) {
			
			GameConfig configOfReturnMatch = Game.lostGames.get((Player)sender);
			
			if(configOfReturnMatch != null) {
				
				// Show the confirmation to the player
				sender.sendMessage(Localization.localizedString("ttt.returnRequest.confirm", configOfReturnMatch.opponentPlayer.getName()));
				
				// Remove config from list!
				Game.lostGames.remove((Player)sender);
				Game.lostGames.remove(configOfReturnMatch.opponentPlayer);
				
				new Game(configOfReturnMatch).queue(true);
				return true;
			}
		}
		
		
		// Create the game's config from the command's args
		GameConfig config;
			
		try {
			
			config = CommandTicTacToe.createGameConfigFromCommand(sender, args);
			
		} catch(InvalidArgCountException | OpponentPlayerNotFoundException | OpponentIsMainPlayerException e) {
			sender.sendMessage(TextFormat.RED + e.getMessage() + TextFormat.RESET);
			return true;
		} catch(NumberFormatException e) {
			String nonNumberString = e.getMessage().substring(19, e.getMessage().length() - 1);
			sender.sendMessage(Localization.localizedString("ttt.error.expected_number", nonNumberString));
			return true;
		}
		
		// Check for errors in the game's config
		List<String> configErrors = config.validateReturningErrors();
		if(!configErrors.isEmpty()) {
			for(String error: configErrors) {
				sender.sendMessage(TextFormat.RED + error + TextFormat.RESET);
			}
			
			// Don't continue on error
			return true;
		}
		
		CommandTicTacToe.tellAffectedPlayersThatPlayerChangedTheirRequest(config);
		
		// Show the confirmation to the player
		sender.sendMessage(Localization.localizedString("ttt.invitation.confirm", config.opponentPlayer.getName()));
		
		new Game(config).queue(false);
		
		return true;
	}
	
	
	private static boolean isNoAvailablePlayersPlaceholder(String[] args) {
		if(args.length < CommandTicTacToe.NO_AVAILABLE_PLAYERS_MIN_ARG_COUNT) {
			return false;
		}
		
		boolean firstArgumentIsPlaceholder = args[0].equals(CommandTicTacToe.NO_AVAILABLE_PLAYERS_ARGS[0]);
		boolean secondArgumentIsPlaceholder = args[1].equals(CommandTicTacToe.NO_AVAILABLE_PLAYERS_ARGS[1]);
		boolean thirdArgumentIsPlaceholder = args[2].equals(CommandTicTacToe.NO_AVAILABLE_PLAYERS_ARGS[2]);
		boolean argumentsArePlaceholder = firstArgumentIsPlaceholder && secondArgumentIsPlaceholder && thirdArgumentIsPlaceholder;
		
		return argumentsArePlaceholder;
	}
	
	
	private static void tellAffectedPlayersThatPlayerChangedTheirRequest(GameConfig config) {
		ArrayList<Game> queuedGames = new ArrayList<Game>();
		queuedGames.addAll(Game.queuedGames.values());
		
		for(Game queuedGame: queuedGames) {
			if(queuedGame.config.mainPlayer == config.mainPlayer) {
				Game.queuedGames.remove(queuedGame.uuid);
				
				String revokeMessage;
				if(queuedGame.config.opponentPlayer == config.opponentPlayer) {
					revokeMessage = Localization.localizedString("ttt.invitation.updated", config.mainPlayer.getName());
				} else {
					revokeMessage = Localization.localizedString("ttt.invitation.revoked",  config.mainPlayer.getName());
				}
				
				queuedGame.config.opponentPlayer.sendMessage(revokeMessage);
			}
		}
	}
	
	
	public static GameConfig createGameConfigFromCommand(Player mainPlayer, String args[]) throws InvalidArgCountException, OpponentPlayerNotFoundException, OpponentIsMainPlayerException, NumberFormatException {
		
		if(args.length < CommandTicTacToe.MIN_VALID_ARG_COUNT && args.length > CommandTicTacToe.MAX_VALID_ARG_COUNT) {
			throw new InvalidArgCountException("CommandTicTacToe.createGameConfigFromCommand was called with " + args.length + "arguments (min = " + CommandTicTacToe.MIN_VALID_ARG_COUNT + "; max = " + CommandTicTacToe.MAX_VALID_ARG_COUNT + ")!");
		}
		
		String opponentPlayerName = args[CommandTicTacToe.OPPONENT_ARG_INDEX];
		Player opponentPlayer = null;
		for(Player player: Plugin.instance.getServer().getOnlinePlayers().values()) {
			if(opponentPlayerName.equals(player.getName())) {
				opponentPlayer = player;
				break;
			}
		}
		
		if(opponentPlayer == null) {
			throw new OpponentPlayerNotFoundException(Localization.localizedString("ttt.invitation.opponent_not_found", opponentPlayerName));
		}
		
		if(opponentPlayer == mainPlayer) {
			throw new OpponentIsMainPlayerException(Localization.localizedString("ttt.invitation.main_is_opponent", opponentPlayerName));
		}
		
		
		int integerArguments[] = CommandTicTacToe.extractIntegerArgs(args);
		
		return new GameConfig(mainPlayer, opponentPlayer, new Size(integerArguments[CommandTicTacToe.SIZE_X_ARG_INDEX - 1], integerArguments[CommandTicTacToe.SIZE_Y_ARG_INDEX - 1], integerArguments[CommandTicTacToe.SIZE_Z_ARG_INDEX - 1]), integerArguments[CommandTicTacToe.WIN_REQUIRED_AMOUNT_ARG_INDEX - 1]);
	}
	
	
	protected static int[] extractIntegerArgs(String args[]) {
		int integerArguments[] = new int[4];
		
		for(int i = 1; i < 5; i++) {
			try {
				if(args.length <= i || args[i].isEmpty()) {
					if(i == CommandTicTacToe.WIN_REQUIRED_AMOUNT_ARG_INDEX) {
						integerArguments[i - 1] = Math.max(integerArguments[0], Math.max(integerArguments[1], integerArguments[2]));
					} else {
						integerArguments[i - 1] = i == (CommandTicTacToe.SIZE_Y_ARG_INDEX) ? 1 : 3;
					}
				} else {
					integerArguments[i - 1] = Integer.parseInt(args[i]);
				}
			} catch(NumberFormatException e) {
				throw e;
			}
		}
		
		return integerArguments;
	}
	
}


class InvalidArgCountException extends Exception {
	private static final long serialVersionUID = 5946362337911270663L;
	
	public InvalidArgCountException(String message) {
		super(message);
	}
}

class OpponentPlayerNotFoundException extends Exception {
	private static final long serialVersionUID = -3046415677251307939L;
	
	public OpponentPlayerNotFoundException(String message) {
		super(message);
	}
}

 class OpponentIsMainPlayerException extends Exception {
	private static final long serialVersionUID = 3470965440570763560L;

	public OpponentIsMainPlayerException(String message) {
		super(message);
	}
}