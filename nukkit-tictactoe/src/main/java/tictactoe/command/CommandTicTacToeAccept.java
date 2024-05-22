package tictactoe.command;

import cn.nukkit.Player;
import tictactoe.game.Game;
import tictactoe.util.Localization;

public class CommandTicTacToeAccept {
	
	public static int ARG_COUNT = 1;
	public static int PLAYER_NAME_ARG_INDEX = 0;
	
	public static boolean handleCommand(Player sender, String[] args) {
		
		if(args.length != CommandTicTacToeAccept.ARG_COUNT) {
			return false;
		}
		
		String playerName = args[CommandTicTacToeAccept.PLAYER_NAME_ARG_INDEX];
		
		Game targetGame = Game.getQueuedGameWithPlayers(playerName, (Player)sender);
		
		
		if(targetGame == null) {
			try {
				targetGame = Game.getQueuedGameByUUID(args[CommandTicTacToeAccept.PLAYER_NAME_ARG_INDEX]);
				
				if(targetGame == null) {
					sender.sendMessage(Localization.localizedString("ttta.uuid_not_found"));
					return true;
				}
				
			} catch(IllegalArgumentException e) {
				sender.sendMessage(Localization.localizedString("ttta.request_not_found", playerName));
				return true;
			}
		}
		
		targetGame.start();
		
		targetGame.playGameSoundToPlayer(Game.GAME_ACCEPT_SOUND, Game.GAME_ACCEPT_SOUND_PITCH, sender);
		
		return true;
	}
	
}
