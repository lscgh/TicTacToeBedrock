package tictactoe.game;

import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import tictactoe.Plugin;
import tictactoe.util.Localization;
import tictactoe.util.Size;

public class GameConfig {
	
	public static int MIN_DIMENSION_SIZE = 1;
	public static int MIN_X_Z_SIZE = 2;
	public static int MIN_Y_SIZE = 1;
	public static int MIN_WIN_REQUIRED_AMOUNT = 2;

	/**
	 * The player who started the game
	 */
	public final Player mainPlayer;
	
	/**
	 * The player who was invited to the game
	 */
	public final Player opponentPlayer;
	
	/**
	 * The size of the game.
	 */
	public final Size size;
	
	/**
	 * The number of same-player-marked fields required in a row for that player to win.
	 */
	public final int winRequiredAmount;
	
	public GameConfig(Player mainPlayer, Player opponentPlayer, Size size, int winRequiredAmount) {
		this.mainPlayer = mainPlayer;
		this.opponentPlayer = opponentPlayer;
		this.size = size;
		this.winRequiredAmount = winRequiredAmount;
	}
	
	public List<String> validateReturningErrors() {
		
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.mainPlayer == null) {
			errors.add(Localization.localizedString("config.error.main_player_null"));
			return errors;
		}
		
		if(this.opponentPlayer == null) {
			errors.add(Localization.localizedString("config.error.opponent_player_null"));
			return errors;
		}
		
		if(Game.runningGames.containsKey(this.mainPlayer)) {
			errors.add(Localization.localizedString("config.error.main_already_in_a_game"));
			return errors;
		}
		
		if(Game.runningGames.containsKey(this.opponentPlayer)) {
			errors.add(String.format(Localization.localizedString("config.error.opponent_already_in_a_game", this.opponentPlayer.getName(), Game.runningGames.get(this.opponentPlayer).config.mainPlayer.getName())));
			return errors;
		}
		
		errors.addAll(this.validateNumbersReturningErrors());
		
		return errors;
	}
	
	private List<String> validateNumbersReturningErrors() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.size.getSmallestDimension() < GameConfig.MIN_DIMENSION_SIZE) {
			errors.add("No dimension of the game can be smaller than " + GameConfig.MIN_DIMENSION_SIZE + ". The smallest possible game is (" + GameConfig.MIN_X_Z_SIZE + ", " + GameConfig.MIN_Y_SIZE + ", " + GameConfig.MIN_X_Z_SIZE + ").");
		}
		
		if(this.size.getLargestDimension() > Plugin.instance.getMaxDimensionSize()) {
			errors.add("No dimension of the game can be larger than " + Plugin.instance.getMaxDimensionSize() + ".");
		}
		
		if(Math.min(this.size.x, this.size.z) < GameConfig.MIN_X_Z_SIZE) {
			errors.add("The X and Z size of the game must not be smaller than " + GameConfig.MIN_X_Z_SIZE + ".");
		}
		
		if(this.winRequiredAmount > this.size.getLargestDimension()) {
			errors.add(Localization.localizedString("config.error.win_required_amount_too_large", this.size.getLargestDimension()));
		}
		
		if(this.winRequiredAmount < GameConfig.MIN_WIN_REQUIRED_AMOUNT) {
			errors.add(Localization.localizedString("config.error.win_required_amount_too_small", GameConfig.MIN_WIN_REQUIRED_AMOUNT));
		}
		
		return errors;
	}
	
}
