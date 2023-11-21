package bpm.birt.fusioncharts.ui.model;

public class Serie {
	private String name;
	private String color;
	private String expr;
	private String agg;
	
	public Serie(){}
	
	public Serie(String name, String color, String expr, String agg){
		this.setColor(color);
		this.setExpr(expr);
		this.setAgg(agg);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getExpr() {
		return expr;
	}

	public void setAgg(String agg) {
		this.agg = agg;
	}

	public String getAgg() {
		return agg;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}
}
