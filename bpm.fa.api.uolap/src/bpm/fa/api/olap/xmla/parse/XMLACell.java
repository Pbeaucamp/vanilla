package bpm.fa.api.olap.xmla.parse;

public class XMLACell {
	private String ordinal;
	private String value;
	private String fvalue; //formatted value
	
	public XMLACell() {
	}
	
	public XMLACell(String ord, String val, String fval) {
		ordinal = ord;
		value  = val;
		fvalue = fval; 
	}
	
	public String getFormattedValue() {
		return fvalue;
	}
	
	public void setFvalue(String fvalue) {
		this.fvalue = fvalue;
	}
	
	public int getNb() {
		return new Integer(ordinal.replace("\"",""));
	}
	
	public String getOrdinal() {
		return ordinal;
	}
	
	public void setOrdinal(String ordinal) {
		this.ordinal = ordinal;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
