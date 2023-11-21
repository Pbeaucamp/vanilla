package bpm.gwt.commons.client.charts;

public class Aggregation {

	private String label;
	private String value;

	public Aggregation(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return label;
	}
}