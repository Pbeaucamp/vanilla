package bpm.fa.api.item;

public class ItemDependent {
	private String hierarchy;
	private String level;
	private String uname;
	private String label;
	private String value;
	
	public ItemDependent(String hierarchy, String level, String uname, String label, String value) {
		this.hierarchy = hierarchy;
		this.level = level;
		this.uname = uname;
		this.label = label;
		this.value = value;
	}

	public String getHierarchy() {
		return hierarchy;
	}

	public String getLabel() {
		return label;
	}

	public String getLevel() {
		return level;
	}

	public String getUname() {
		return uname;
	}
	
	public String getValue() {
		return value;
	}
}
