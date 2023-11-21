package bpm.vanilla.platform.core.beans.fmdt;

import bpm.vanilla.platform.core.beans.data.D4CTypes;



public class FmdtColumn extends FmdtData {

	private static final long serialVersionUID = 1L;

	private String tableName = null;
	private String tableOriginName = null;
	private String originName = null;
	private String formula = null;
	private D4CTypes d4ctypes;
	
	private ColumnType type;

	public FmdtColumn() {
		super();
	}

	public FmdtColumn(String name, String label, String description, String tableName) {
		super(name, label, description);
		this.tableName = tableName;
	}

	public FmdtColumn(String name, String label, String description, String tableName, String tableOrigin) {
		super(name, label, description);
		this.tableName = tableName;
		this.tableOriginName = tableOrigin;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getTableOriginName() {
		return tableOriginName;
	}

	public void setTableOriginName(String tableOriginName) {
		this.tableOriginName = tableOriginName;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public ColumnType getType() {
		return type;
	}
	
	public void setType(ColumnType type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtColumn other = (FmdtColumn) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		}
		else if (!formula.equals(other.formula))
			return false;
		if (originName == null) {
			if (other.originName != null)
				return false;
		}
		else if (!originName.equals(other.originName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		}
		else if (!tableName.equals(other.tableName))
			return false;
		if (tableOriginName == null) {
			if (other.tableOriginName != null)
				return false;
		}
		else if (!tableOriginName.equals(other.tableOriginName))
			return false;
		return true;
	}

	public D4CTypes getD4ctypes() {
		return d4ctypes;
	}

	public void setD4ctypes(D4CTypes d4ctypes) {
		this.d4ctypes = d4ctypes;
	}
}
