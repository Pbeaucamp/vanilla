package bpm.gwt.commons.client.charts;

public abstract class ChartValue {

	protected String category;
	protected String color=null;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
