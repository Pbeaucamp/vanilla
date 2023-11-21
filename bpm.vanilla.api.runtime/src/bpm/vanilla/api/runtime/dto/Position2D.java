package bpm.vanilla.api.runtime.dto;

public class Position2D {
	private int x;
	private int y;

	public Position2D(int _x, int _y) {
		x = _x;
		y = _y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void incrementX(int increment) {
		this.x += increment;
	}

	public void incrementY(int increment) {
		this.y += increment;
	}
}
