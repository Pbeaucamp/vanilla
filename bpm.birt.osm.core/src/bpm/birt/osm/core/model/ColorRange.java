package bpm.birt.osm.core.model;

public class ColorRange {

	private String name;
	private String color;
	private int min;
	private int max;

	public ColorRange(String name, String color, int min, int max) {
		super();
		this.name = name;
		this.color = color;
		this.min = min;
		this.max = max;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

}
