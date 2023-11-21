package bpm.faweb.shared.infoscube;

public class ItemComplex extends ItemCube {
	private int colSpan = -1;
	private int rowSpan = -1 ;
	private int spanBefore = 0;
	
	
	public ItemComplex() {
		super();
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

}
