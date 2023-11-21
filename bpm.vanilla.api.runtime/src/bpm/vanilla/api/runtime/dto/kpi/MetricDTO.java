package bpm.vanilla.api.runtime.dto.kpi;

import bpm.fm.api.model.Metric;

public class MetricDTO {
	
	private int KpiID;
	private String name;
	
	public MetricDTO(Metric metric) {
		this.KpiID = metric.getId();
		this.name = metric.getName();
	}

	@Override
	public String toString() {
		return "MetricDTO [KpiID=" + KpiID + ", name=" + name + "]";
	}

	public int getKpiID() {
		return KpiID;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + KpiID;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricDTO other = (MetricDTO) obj;
		if (KpiID != other.KpiID)
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
