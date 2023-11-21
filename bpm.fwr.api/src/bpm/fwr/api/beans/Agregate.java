package bpm.fwr.api.beans;

import java.io.Serializable;


public class Agregate implements Serializable {
	private String onColumn;
	private String formula;
	
	public Agregate() {
		super();
	}
	
	public Agregate(String o, String f) {
		super();
		this.onColumn = o;
		this.formula = f;
	}

	public String getOnColumn() {
		return onColumn;
	}

	public void setOnColumn(String onColumn) {
		this.onColumn = onColumn;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * 
	 * @deprecated Use {@link #getXmlCustomColumnName(String)} instead
	 *
	 */
	@Deprecated
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<agregate>\n"); 
		buf.append("   <oncolumn>"+this.onColumn+"</oncolumn>\n");
		buf.append("   <formula>"+this.formula+"</formula>\n");
		buf.append("</agregate>\n");		
		
		return buf.toString();
	}
	
	/**
	 * 
	 * This method allows to get a cleaner xml with respect of indentation
	 *
	 * @param spaceBefore (to respect indentation)
	 * @return the xml to send
	 */
	public String getXml(String spaceBefore) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<agregate>\n"); 
		buf.append("   <oncolumn>"+this.onColumn+"</oncolumn>\n");
		buf.append("   <formula>"+this.formula+"</formula>\n");
		buf.append("</agregate>\n");		
		
		return buf.toString();
	}

}
