package tictactoe;

import java.util.LinkedHashMap;

import cn.nukkit.plugin.PluginBase;

public class Plugin extends PluginBase {
	
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

	@Override
	public void onLoad() {
		this.getLogger().info("TicTacToe plugin loaded!");
	}
	
	@Override
	public void onEnable() {
		this.addConfigDefaults();
		
		this.getLogger().info("TicTacToe plugin enabled!");
		this.getLogger().info("Max dimension size: " + this.getMaxDimensionSize());
	}
	
}
