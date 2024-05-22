package tictactoe.game;

import java.util.function.Consumer;
import cn.nukkit.level.Location;
import tictactoe.util.Size;

public class CubicBlockArea {

	private static String ERROR_CONTRUCTION_WITH_DIFFERENT_WORLDS = "Attempted to create a CubicBlockArea with two Locations in different worlds. Have '%s' and '%s'!";
	
	public Location startBlock;
	public Location endBlock;
	
	public CubicBlockArea(Location startBlock, Location endBlock) {
		if(startBlock.getLevel() != endBlock.getLevel()) {
			throw new IllegalArgumentException(String.format(CubicBlockArea.ERROR_CONTRUCTION_WITH_DIFFERENT_WORLDS, startBlock.getLevel().getName(), endBlock.getLevel().getName()));
		}
		
		this.startBlock = startBlock;
		this.endBlock = endBlock;
	}
	
	public boolean contains(Location block) {
		if(block.getLevel() != this.startBlock.getLevel()) {
			throw new IllegalArgumentException("Attempted to execute contains() on a CubicBlockArea in world '" + this.startBlock.getLevel().getName() + "' using a location in world '" + block.getLevel().getName() + "'");
		}
		
		boolean containedOnXAxis = block.getFloorX() >= this.startBlock.getFloorX() && block.getFloorX() <= this.endBlock.getFloorX();
		boolean containedOnYAxis = block.getFloorY() >= this.startBlock.getFloorY() && block.getFloorY() <= this.endBlock.getFloorY();
		boolean containedOnZAxis = block.getFloorZ() >= this.startBlock.getFloorZ() && block.getFloorZ() <= this.endBlock.getFloorZ();
		
		return containedOnXAxis && containedOnYAxis && containedOnZAxis;
	}
	
	
	private int getPositiveDifference(int a, int b) {
		if(a > b) {
			return a - b;
		} else {
			return b - a;
		}
	}
	
	public Size size() {
		int width = this.getPositiveDifference(this.startBlock.getFloorX(), this.endBlock.getFloorX()) + 1;
		int height = this.getPositiveDifference(this.startBlock.getFloorY(), this.endBlock.getFloorY()) + 1;
		int depth = this.getPositiveDifference(this.startBlock.getFloorZ(), this.endBlock.getFloorZ()) + 1;
		
		return new Size(width, height, depth);
	}
	
	
	public void forEach(Consumer<Location> action) {
		
		Size size = this.size();
		for(int x = 0; x < size.x; x++) {
			for(int y = 0; y < size.y; y++) {
				for(int z = 0; z < size.z; z++) {
					
					Location currentLocation = new Location(this.startBlock.getFloorX() + x, this.startBlock.getFloorY() + y, this.startBlock.getFloorZ() + z, this.startBlock.getLevel());
					action.accept(currentLocation);
					
				}
			}
		}
		
	}
	
}
