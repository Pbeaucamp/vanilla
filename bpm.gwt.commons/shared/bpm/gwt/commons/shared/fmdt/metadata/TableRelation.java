package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import bpm.vanilla.platform.core.beans.data.DatabaseTable;

public class TableRelation implements IsSerializable {

	private DatabaseTable leftTable, rightTable;
	private List<ColumnJoin> joins;
	
	public TableRelation() { }
	
	public TableRelation(DatabaseTable leftTable, DatabaseTable righTable) {
		this.leftTable = leftTable;
		this.rightTable = righTable;
	}

	public DatabaseTable getLeftTable() {
		return leftTable;
	}

	public void setLeftTable(DatabaseTable leftTable) {
		this.leftTable = leftTable;
	}

	public DatabaseTable getRightTable() {
		return rightTable;
	}

	public void setRightTable(DatabaseTable rightTable) {
		this.rightTable = rightTable;
	}

	public List<ColumnJoin> getJoins() {
		return joins;
	}

	public void addJoin(ColumnJoin join) {
		if (joins == null) {
			joins = new ArrayList<ColumnJoin>();
		}
		this.joins.add(join);
	}

	public void setJoins(List<ColumnJoin> joins) {
		this.joins = joins;
	}

	public void removeJoin(ColumnJoin join) {
		this.joins.remove(join);
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(leftTable != null ? leftTable.getName() : "Unknown");
		buf.append(" --> ");
		buf.append(rightTable != null ? rightTable.getName() : "Unknown");
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TableRelation) {
			TableRelation toCompare = (TableRelation) obj;
			if (leftTable != null && rightTable != null && toCompare.getLeftTable() != null && toCompare.getRightTable() != null) {
				return leftTable.equals(toCompare.getLeftTable()) && rightTable.equals(toCompare.getRightTable());
			}
		}
		return false;
	}
}
