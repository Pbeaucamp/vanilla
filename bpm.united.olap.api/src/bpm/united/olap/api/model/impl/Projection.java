package bpm.united.olap.api.model.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 
 * Represents a projection. Can either be a what if or an extrapolation.
 * 
 * @author Marc Lanquetin
 *
 */
public class Projection implements Serializable {
	
	public static final String TYPE_WHATIF = "What if";
	public static final String TYPE_EXTRAPOLATION = "Extrapolation";
	
	public static final String EXTRAPOLATION_NONE = "NONE";
	public static final String EXTRAPOLATION_LAGRANGE = "Lagrange";
	public static final String EXTRAPOLATION_LINEAR = "Linear regeression";
	
	public static final String EXTRAPOLATION_START_MEMBER = "StartMember";
	public static final String EXTRAPOLATION_END_MEMBER = "EndMember";

	private List<ProjectionMeasure> projectionMeasures;
	private String name;
	private Date endDate;
	private Date startDate;
	private String oldFactName;
	private String newFactName;
	
	private String projectionLevel;
	
	private String type;

	public List<ProjectionMeasure> getProjectionMeasures() {
		return projectionMeasures;
	}

	public void setProjectionMeasures(List<ProjectionMeasure> projectionMeasures) {
		this.projectionMeasures = projectionMeasures;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getOldFactName() {
		return oldFactName;
	}

	public void setOldFactName(String oldFactName) {
		this.oldFactName = oldFactName;
	}

	public String getNewFactName() {
		return newFactName;
	}

	public void setNewFactName(String newFactName) {
		this.newFactName = newFactName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setProjectionLevel(String projectionLevel) {
		this.projectionLevel = projectionLevel;
	}

	public String getProjectionLevel() {
		return projectionLevel;
	}

	public String getKey() {
		SimpleDateFormat form = new SimpleDateFormat("dd-MM-yyyy");
		StringBuilder buf = new StringBuilder();
		
		buf.append(name);
//		buf.append(form.format(endDate));
//		buf.append(form.format(startDate));
		buf.append(projectionLevel);
		buf.append(type);
		
		return buf.toString();
	}
	
}
