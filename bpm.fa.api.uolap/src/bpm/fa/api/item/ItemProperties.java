package bpm.fa.api.item;

public class ItemProperties implements Item {
	private String label;
	private String name;
	private String origin;
	
	public ItemProperties(String label, String name, String origin) {
		this.label = label;
		this.name = name;
		this.origin = origin;
	}
	
	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public String getOrigin() {
		return origin;
	}
}
