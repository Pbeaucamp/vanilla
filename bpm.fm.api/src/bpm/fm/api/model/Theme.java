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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "fm_theme")
public class Theme implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "theme_name")
	private String name;
	
	@Column (name = "creation_date")
	private Date creationDate = new Date();
	
	@Transient
	List<Metric> metrics = new ArrayList<Metric>();
	
	@Transient
	List<Axis> axis = new ArrayList<Axis>();

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

	public List<Metric> getMetrics() {
		if(metrics == null) {
			metrics = new ArrayList<Metric>();
		}
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public List<Axis> getAxis() {
		if(axis == null) {
			axis = new ArrayList<Axis>();
		}
		return axis;
	}

	public void setAxis(List<Axis> axis) {
		this.axis = axis;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public void addMetric(Metric m) {
		if(!metrics.contains(m)) {
			metrics.add(m);
		}
	}
	
	public void addAxis(Axis a) {
		if(!axis.contains(a)) {
			axis.add(a);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Theme)obj).getId();
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
