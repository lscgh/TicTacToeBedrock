package tictactoe.game;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.level.Location;
import tictactoe.Plugin;

public class GameListener implements Listener {

	Game game;
	
	boolean allowMarkingFields = true;
	
	public GameListener(Game game) {
		this.game = game;
	}
	
	public void activate() {
		Plugin.instance.getServer().getPluginManager().registerEvents(this, Plugin.instance);
	}
	
	public void deactivate() {
		HandlerList.unregisterAll(this);
	}
	
	private boolean isAuthorizedPlayer(Player player) {
		return player == this.game.config.mainPlayer || player == this.game.config.opponentPlayer;
	}
	
	private boolean isProtectedLocation(Location location) {
		return this.game.gameArea.contains(location);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(this.isProtectedLocation(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(this.isProtectedLocation(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntitySummon(EntitySpawnEvent event) {
		if(this.isProtectedLocation(event.getEntity().getLocation())) {
			event.getEntity().kill();
			Plugin.instance.getLogger().info("Killed entity that spawned in a game. Entity = " + event.getEntity().getName());
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(this.isProtectedLocation(event.getEntity().getLocation())) {
			event.setCancelled(true);
		}
		
		event.getBlockList().removeIf((block) -> this.isProtectedLocation(block.getLocation()));
	}
	
	@EventHandler
	public void onBlockExplode(BlockIgniteEvent event) {
		if(this.isProtectedLocation(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLightningStrike(LightningStrikeEvent event) {
		Entity entity = (Entity)event.getLightning();
		
		if(this.isProtectedLocation(entity.getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(this.isProtectedLocation(event.getTo())) {
			event.setCancelled(!this.isAuthorizedPlayer(event.getPlayer()));
		}
	}
		
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		if(this.isProtectedLocation(player.getLocation())) {
			event.setCancelled(this.isAuthorizedPlayer(player));
		}
	}
	
	@EventHandler
	public void onBlockMove(BlockFromToEvent event) {
		if(this.isProtectedLocation(event.getTo().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		
			
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && this.game.getPlayerInTurn() == event.getPlayer()) {
			
			if(!this.allowMarkingFields) {
				event.setCancelled(true);
				return;
			}
			
			if(this.isProtectedLocation(event.getBlock().getLocation())) {
				
				try {
					FieldPoint locationAsFieldPoint = this.game.state.blockLocationToFieldPoint(this.game.location, event.getBlock().getLocation());
					
					if(this.game.state.fieldPointIsValid(locationAsFieldPoint)) {
						this.game.placeAt(locationAsFieldPoint);
					}
				} catch(IllegalArgumentException e) {}
				
			}
			
			event.setCancelled(true);
		}
	}
	
}
