package bpm.fa.api.olap;

public class Measure extends Element {
	
	private String type;
	
	public Measure(String name, String uname, String caption, String type) {
		super(name, uname, caption);
		this.type = type;
	}
	
	public String getCalculationType() {
		return type;
	}
}
