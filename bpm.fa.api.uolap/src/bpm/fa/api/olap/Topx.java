package bpm.fa.api.olap;

import java.io.Serializable;

public class Topx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String elementName;
	private String elementTarget;
	private int count;
	private boolean onRow;
	
	public Topx() {
		
	}
	
	public Topx(String elementName, String elementTarget, int count, boolean onRow) {
		super();
		this.elementName = elementName;
		this.elementTarget = elementTarget;
		this.count = count;
		this.setOnRow(onRow);
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementTarget() {
		return elementTarget;
	}

	public void setElementTarget(String elementTarget) {
		this.elementTarget = elementTarget;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setOnRow(boolean onRow) {
		this.onRow = onRow;
	}

	public boolean isOnRow() {
		return onRow;
	}
}
