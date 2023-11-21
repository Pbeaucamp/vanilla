package bpm.fm.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "fm_complex_metric")
public class ComplexMapMetric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idMap")
	private int idMap;
	
	@Column (name = "idMetric")
	private int idMetric;

	@Transient
	private Metric metric;
	
	@Column (name = "icon")
	private String iconUrl = "";
	
	@Column (name = "color")
	private String color = "";
	
	@Column (name = "representation")
	private String representation = "";
	
	public ComplexMapMetric() {
		super();
	}
	
	public ComplexMapMetric(Metric metric) {
		super();
		this.metric = metric;
		this.idMetric = metric.getId();
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
		this.idMetric = metric.getId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdMap() {
		return idMap;
	}

	public void setIdMap(int idMap) {
		this.idMap = idMap;
	}

	public int getIdMetric() {
		return idMetric;
	}

	public void setIdMetric(int idMetric) {
		this.idMetric = idMetric;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getRepresentation() {
		return representation;
	}

	public void setRepresentation(String representation) {
		this.representation = representation;
	}
	
	
}
