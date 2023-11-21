package bpm.vanilla.api.runtime.dto;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.olap.OLAPCube;

public class CubeElementCell extends CubeCell {
	private boolean isRow;
	private boolean isCol;
	private boolean isDrilled;
	private boolean hasMember;
	private CubeOLAPMember cubeMember;

	public CubeElementCell(OLAPCube cube, ItemElement itElem, boolean _hasMember) throws Exception {
		super(itElem, itElem.getDataMember().getUniqueName(), "ItemElement");
		cubeMember = CubeOLAPMember.loadCubeOLAPMember(cube, itElem.getDataMember(), false);
		isRow = itElem.isRow();
		isCol = itElem.isCol();
		isDrilled = itElem.isDrilled();
		hasMember = _hasMember;
	}

	public boolean isRow() {
		return isRow;
	}

	public boolean isCol() {
		return isCol;
	}

	public boolean isDrilled() {
		return isDrilled;
	}

	public boolean isHasMember() {
		return hasMember;
	}

	public CubeOLAPMember getCubeMember() {
		return cubeMember;
	}

}
