package bpm.united.olap.wrapper.fa.html;

import java.util.Map;
import java.util.Set;

public class ItemCube {
	private String label;
	private String uname;
	private String type;
	private int colSpan = -1;
	private int rowSpan = -1 ;
	private int spanBefore = 0 ;
	private float value = 0.0f;
	
	
	
	/**
	   * This field is a Set that must always contain Strings.
	   * 
	   * @gwt.typeArgs <java.lang.String>
	   */
	  public Set setOfStrings;

	  /**
	   * This field is a Map that must always contain Strings as its keys and
	   * values.
	   * 
	   * @gwt.typeArgs <java.lang.String,java.lang.String>
	   */
	  public Map mapOfStringToString;

	  /**
	   * Default Constructor. The Default Constructor's explicit declaration
	   * is required for a serializable class.
	   */  
	  public ItemCube(){	
		  super();
	  }

	public ItemCube(String lbl, String u, String type) {
		this.label = lbl;
		this.uname = u;
		this.type = type;
	}
	
	public ItemCube(String lbl, String type) {
		this.label = lbl;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String t) {
		this.type = t;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public int getColSpan() {
		return colSpan;
	}

	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	public int getSpanBefore() {
		return spanBefore;
	}

	public void setSpanBefore(int spanBefore) {
		this.spanBefore = spanBefore;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}


}
