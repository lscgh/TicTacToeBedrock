package tictactoe.util;

public class Direction {

	public int dx;
	public int dy;
	public int dz;
	
	public Direction(int dx, int dy, int dz) {
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}
	
	public Direction oppositeDirection() {
		return new Direction(-this.dx, -this.dy, -this.dz);
	}
	
	public Direction adding(int dx, int dy, int dz) {
		return new Direction(this.dx + dx, this.dy + dy, this.dz + dz);
	}
	
}
