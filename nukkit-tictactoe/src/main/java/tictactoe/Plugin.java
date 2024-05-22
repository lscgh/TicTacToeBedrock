package tictactoe;

import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import tictactoe.command.CommandTicTacToe;
import tictactoe.command.CommandTicTacToeAccept;
import tictactoe.game.Game;
import tictactoe.util.Localization;

public class Plugin extends PluginBase {
	
	public static Plugin instance;
	
	
	private static String MAX_DIMENSION_SIZE_KEY_NAME = "max_dimension_size";
	private static int MAX_DIMENSION_SIZE_DEFAULT_VALUE = 15;
	
	public int getMaxDimensionSize() {
		return this.getConfig().getInt(Plugin.MAX_DIMENSION_SIZE_KEY_NAME);
	}
	
	private void addConfigDefaults() {
		this.getConfig().setDefault(new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = 3381765812192827898L;

			{
				put(Plugin.MAX_DIMENSION_SIZE_KEY_NAME, Plugin.MAX_DIMENSION_SIZE_DEFAULT_VALUE);
			}
		});
		
		this.saveConfig();
	}
	
	private void setCommandParameters() {
		this.setTicTacToeCommandParameters();
		this.setTicTacToeAcceptCommandParameters();
	}
	
	private void setTicTacToeCommandParameters() {
		HashMap<String, CommandParameter[]> parameters = new HashMap<String, CommandParameter[]>();
		parameters.put("default", new CommandParameter[]{
				CommandParameter.newType("opponent", CommandParamType.TARGET),
				CommandParameter.newType("sizeX", true, CommandParamType.INT),
				CommandParameter.newType("sizeY", true, CommandParamType.INT),
				CommandParameter.newType("sizeZ", true, CommandParamType.INT),
				CommandParameter.newType("winRequiredAmount", true, CommandParamType.INT)
			});
		
		parameters.put("cancel", new CommandParameter[]{
				CommandParameter.newEnum("action", new String[]{"cancel", "requestReturnMatch"})
		});
		
		Command ticTacToeCommand = (Command)this.getCommand("tictactoe");
		ticTacToeCommand.setCommandParameters(parameters);
	}
	
	private void setTicTacToeAcceptCommandParameters() {
		HashMap<String, CommandParameter[]> parameters = new HashMap<String, CommandParameter[]>();
		parameters.put("default", new CommandParameter[]{
				CommandParameter.newType("requestingPlayerName", CommandParamType.TARGET)
			});
		
		Command ticTacToeCommand = (Command)this.getCommand("tictactoeaccept");
		ticTacToeCommand.setCommandParameters(parameters);
	}

	@Override
	public void onLoad() {
		Plugin.instance = this;
		this.getLogger().info(Localization.localizedString("misc.loaded"));
	}
	
	@Override
	public void onEnable() {
		this.addConfigDefaults();
		
		this.setCommandParameters();
		
		this.getLogger().info(Localization.localizedString("misc.enabled"));
		this.getLogger().info(Localization.localizedString("misc.max_dimension_size", this.getMaxDimensionSize()));
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info(Localization.localizedString("misc.disabled"));
		Game.cancelAllGames();
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch(command.getName()) {
		case "tictactoe":
			if(!(sender instanceof Player)) {
				sender.sendMessage(TextFormat.RED + Localization.localizedString("misc.error.command_execution_only_by_players") + TextFormat.RESET);
				return true;
			}
			
			return CommandTicTacToe.handleCommand((Player)sender, args);
		case "tictactoeaccept":
			if(!(sender instanceof Player)) {
				sender.sendMessage(TextFormat.RED + Localization.localizedString("misc.error.command_execution_only_by_players") + TextFormat.RESET);
				return true;
			}
			
			return CommandTicTacToeAccept.handleCommand((Player)sender, args);
		}
		
		return true;
	}
	
}
