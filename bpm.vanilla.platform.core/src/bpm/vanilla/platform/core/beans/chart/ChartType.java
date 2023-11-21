package bpm.vanilla.platform.core.beans.chart;

import java.io.Serializable;

public class ChartType implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String label;
	private String value;
	private String subType;
	
	public ChartType() {
	}

	public ChartType(String label, String value, String subType) {
		this.label = label;
		this.value = value;
		this.subType = subType;
	}

	public String getValue() {
		return value;
	}

	public String getSubType() {
		return subType;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartType other = (ChartType) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		}
		else if (!label.equals(other.label))
			return false;
		if (subType == null) {
			if (other.subType != null)
				return false;
		}
		else if (!subType.equals(other.subType))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}
}