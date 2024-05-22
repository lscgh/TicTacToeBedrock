package tictactoe.util;

public class Size {
	public int x;
	public int y;
	public int z;
	
	public Size(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getLargestDimension() {
		return Math.max(this.x, Math.max(this.y, this.z));
	}
	
	public int getSmallestDimension() {
		return Math.min(this.x, Math.min(this.y, this.z));	
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}