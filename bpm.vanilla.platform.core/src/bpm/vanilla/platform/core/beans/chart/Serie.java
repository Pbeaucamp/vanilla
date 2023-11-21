package bpm.vanilla.platform.core.beans.chart;

import java.io.Serializable;

public class Serie<T extends IChartColumn> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private ChartColumn<T> data;
	private String aggregation;
	private String color;
	
	public Serie() {
	}

	public Serie(String name, ChartColumn<T> data, String aggregation, String color) {
		this.name = name;
		this.data = data;
		this.aggregation = aggregation;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public String getDataLabel() {
		return data.getLabel();
	}

	public T getData() {
		return data.getItem();
	}
	
	public ChartColumn<T> getChartColumn() {
		return data;
	}
	
	public void setData(ChartColumn<T> data) {
		this.data = data;
	}

	public String getAggregation() {
		return aggregation;
	}

	public String getColor() {
		return color;
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
		Serie<T> other = (Serie<T>) obj;
		if (aggregation == null) {
			if (other.aggregation != null)
				return false;
		}
		else if (!aggregation.equals(other.aggregation))
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		}
		else if (!color.equals(other.color))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		}
		else if (!data.equals(other.data))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}
}