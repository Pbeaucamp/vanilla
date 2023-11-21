package bpm.metadata.misc;

public class Type {
	private String label = "";
	
	public Type() {
		super();
	}

	public Type(String lbl) {
		this.label = lbl;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof Type) {
			return this.label.equalsIgnoreCase(((Type) obj).getLabel());
		}
		return false;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<customtype>\n");
		buf.append("    <label>" + getLabel() + "</label>\n");
		buf.append("</customtype>\n");
		
		return buf.toString();
	}
	
	

}
