package bpm.fm.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "fm_metric_action")
public class MetricAction implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String ELEMENT_VALUE = "{Value}";
	public static final String ELEMENT_OBJECTIVE = "{Objective}";
	public static final String ELEMENT_MAX = "{Max}";
	public static final String ELEMENT_MIN = "{Min}";
	
	public static List<String> formulaElements;
	static {
		formulaElements = new ArrayList<String>();
		
		formulaElements.add(ELEMENT_VALUE);
		formulaElements.add(ELEMENT_OBJECTIVE);
		formulaElements.add(ELEMENT_MAX);
		formulaElements.add(ELEMENT_MIN);
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "action_name")
	private String name;

	@Column(name = "action_description")
	private String description;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "action_formula")
	private String formula;
	
	@Column(name = "metric_id")
	private int metricId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

}
