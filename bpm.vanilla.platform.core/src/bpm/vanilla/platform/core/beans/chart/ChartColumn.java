package bpm.vanilla.platform.core.beans.chart;

import java.io.Serializable;

public class ChartColumn<T extends IChartColumn> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String label;
	private T item;
	
	public ChartColumn() {
	}
	
	public ChartColumn(String label, T item) {
		this.label = label;
		this.item = item;
	}
	
	public String getLabel() {
		return label;
	}
	
	public T getItem() {
		return item;
	}
	
	@Override
	public String toString() {
		return label;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartColumn<T> other = (ChartColumn<T>) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		}
		else if (!item.equals(other.item))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		}
		else if (!label.equals(other.label))
			return false;
		return true;
	}
}
