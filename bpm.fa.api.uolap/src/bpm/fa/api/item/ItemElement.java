package bpm.fa.api.item;

import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.unitedolap.UnitedOlapMember;
import bpm.fa.api.olap.xmla.XMLAMember;

/**
 * Query Element
 * @author ereynard
 * TODO : isCol/isRow
 */
public class ItemElement implements Item {
	//need initialize
	private OLAPMember mb = null;
	private boolean isCol;
	private boolean isHidden;
	
	//runtime
	private boolean isDrilled;
	
	/**
	 * Create a "fake" element to use on cube.
	 * Uses :
	 * 	- called from client code to add a new element to the cube (and query)
	 * 	- ...
	 * @param uname Unique Name of element
	 * @param isCol drop on columns/rows...
	 */
	public ItemElement(String uname, boolean isCol) {
		//hack
		this.mb = new UnitedOlapMember(uname);
		this.isCol = isCol;
	}
	/**
	 * Create a "fake" element to use on cube.
	 * Uses :
	 * 	- called from client code to add a new element to the cube (and query)
	 * 	- ...
	 * @param uname Unique Name of element
	 * @param isCol drop on columns/rows...
	 */
	public ItemElement(String uname, boolean isCol, boolean isUolap) {
		//hack
		if (isUolap){
//			this.mb = new MondrianMember(uname);
			this.mb = new UnitedOlapMember(uname);
		}
		else{
			this.mb = new XMLAMember();
			((XMLAMember)this.mb).setUname(uname);
		}
		
		this.isCol = isCol;
	}
	
	/**
	 * @param mb
	 * @param isCol
	 * @param isHidden
	 */
	public ItemElement(OLAPMember mb, boolean isCol, boolean isHidden) {
		this.mb = mb;
		
		this.isCol = isCol;
		this.isHidden = isHidden;
	}

	public String getLabel() {
		if (isHidden())
			return "";
		else {
			if(mb.getCaption() != null && ! mb.getCaption().equals("")) {
				return mb.getCaption();
			}
			return mb.getName();
		}
	}
	
	public OLAPMember getDataMember() {
		return mb;
	}
	
	public boolean isCol() {
		return isCol;
	}
	
	public boolean isRow() {
		return !isCol;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public boolean isDrilled() {
		return isDrilled;
	}

	public void setDrilled(boolean isDrilled) {
		this.isDrilled = isDrilled;
	}
	
	public int getLevelDepth() {
		return mb.getLevelDepth();
	}
	
	
}
