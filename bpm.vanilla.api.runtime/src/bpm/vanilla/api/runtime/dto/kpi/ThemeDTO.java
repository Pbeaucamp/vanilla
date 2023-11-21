package bpm.vanilla.api.runtime.dto.kpi;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Theme;

public class ThemeDTO {

	private int id;
	private String name;
	private List<MetricDTO> metrics;
	
	public ThemeDTO(Theme theme) {
		this.id = theme.getId();
		this.name = theme.getName();
		this.metrics = new ArrayList<>();
		List<Metric> metrics = theme.getMetrics();
		for (Metric metric : metrics) {
			this.metrics.add(new MetricDTO(metric));
		}
	}

	@Override
	public String toString() {
		return "ThemeDTO [id=" + id + ", name=" + name + ", metrics=" + metrics + "]";
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<MetricDTO> getMetrics() {
		return metrics;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
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
		ThemeDTO other = (ThemeDTO) obj;
		if (id != other.id)
			return false;
		if (metrics == null) {
			if (other.metrics != null)
				return false;
		}
		else if (!metrics.equals(other.metrics))
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
