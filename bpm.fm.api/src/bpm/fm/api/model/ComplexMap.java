package bpm.fm.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapLayer;

@Entity
@Table (name = "fm_complex_map")
public class ComplexMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "name")
	private String name;
	
	@Transient
	private List<ComplexMapAxis> axes;
	
	@Transient
	private List<ComplexMapMetric> metrics;
	
	@Transient
	private List<ComplexMapLevel> levels;

	@Column(name = "mapModel", length = 10000000)
	private String mapModel;
	
	@Transient
	private MapInformation mapInformation;
	
	public ComplexMap() {
		super();
	}
	
	public ComplexMap(String name, List<ComplexMapMetric> metrics, List<ComplexMapAxis> axes, List<ComplexMapLevel> levels) {
		super();
		
		this.name = name;
		this.metrics = metrics; 
		this.axes = axes;
		this.levels = levels;
		
	}

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

	public List<ComplexMapAxis> getComplexAxes() {
		return axes;
	}

	public void setComplexAxes(List<ComplexMapAxis> axes) {
		this.axes = axes;
	}

	public List<ComplexMapMetric> getComplexMetrics() {
		return metrics;
	}

	public void setComplexMetrics(List<ComplexMapMetric> metrics) {
		this.metrics = metrics;
	}
	
	public List<ComplexMapLevel> getComplexLevels() {
		return levels;
	}

	public void setComplexLevels(List<ComplexMapLevel> levels) {
		this.levels = levels;
	}
	
	public List<Metric> getMetrics(){
		List<Metric> resultList = new ArrayList<Metric>();
		for(ComplexMapMetric cpx : metrics){
			resultList.add(cpx.getMetric());
		}
		
		return resultList;
	}
	
	public List<Axis> getAxis(){
		List<Axis> resultList = new ArrayList<Axis>();
		for(ComplexMapAxis cpx : axes){
			resultList.add(cpx.getAxis());
		}
		
		return resultList;
	}
	
	public List<String> getMetricsName(){
		List<String> resultList = new ArrayList<String>();
		for(Metric m : getMetrics()){
			resultList.add(m.getName());
		}
		
		return resultList;
	}
	
	public List<String> getAxisName(){
		List<String> resultList = new ArrayList<String>();
		for(Axis a : getAxis()){
			resultList.add(a.getName());
		}
		
		return resultList;
	}
	
	public MapInformation getMapInformation() {
		return mapInformation;
	}
	
	public void setMapInformation(MapInformation mapInformation) {
		this.mapInformation = mapInformation;
	}
	
	public String getMapModel() {
		return mapModel;
	}
	
	public void setMapModel(String mapModel) {
		this.mapModel = mapModel;
	}
}
