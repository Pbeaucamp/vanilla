package bpm.fm.api.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "fm_fact_table_calc")
public class CalculatedFactTable extends AbstractFactTable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "metric_id")
	private int metricId;
	
	@Column (name = "calculation")
	private String calculation;
	
	@Transient
	private List<CalculatedFactTableMetric> metrics;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public List<CalculatedFactTableMetric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<CalculatedFactTableMetric> metrics) {
		this.metrics = metrics;
	}

	public String getCalculation() {
		return calculation;
	}

	public void setCalculation(String calculation) {
		this.calculation = calculation;
	}
	
}
