package bpm.vanilla.platform.core.beans.fmdt;

import bpm.vanilla.platform.core.beans.chart.IChartColumn;


public class FmdtData implements IChartColumn {

	private static final long serialVersionUID = 1L;

	private int id;

	private String name;
	private String label;
	private String description;

	private String sqlType = "VARCHAR";
	private String javaType = "java.lang.String";

	private String measOp;

	public FmdtData() {
		id = this.hashCode();
	}

	public FmdtData(String name, String label, String description) {
		this();
		this.name = name;
		this.label = label;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getMeasOp() {
		return measOp;
	}

	public void setMeasOp(String measOp) {
		this.measOp = measOp;
	}

	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description != null ? description : "";
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtData other = (FmdtData) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (javaType == null) {
			if (other.javaType != null)
				return false;
		}
		else if (!javaType.equals(other.javaType))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		}
		else if (!label.equals(other.label))
			return false;
		if (measOp == null) {
			if (other.measOp != null)
				return false;
		}
		else if (!measOp.equals(other.measOp))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (sqlType == null) {
			if (other.sqlType != null)
				return false;
		}
		else if (!sqlType.equals(other.sqlType))
			return false;
		return true;
	}
}
