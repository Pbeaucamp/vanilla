package bpm.faweb.client.projection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Projection implements IsSerializable {

	public static final String TYPE_EXTRAPOLATION = "Extrapolation";
	public static final String TYPE_WHATIF = "What if";
	
	public static final String[] TYPES = {TYPE_EXTRAPOLATION, TYPE_WHATIF};
	
	public static String EXTRAPOLATION_NONE_LBL = "";
	public static String EXTRAPOLATION_LAGRANGE_LBL = "";
	public static String EXTRAPOLATION_LINEAR_LBL = "";
	public static final String EXTRAPOLATION_NONE = "NONE";
	public static final String EXTRAPOLATION_LAGRANGE = "Lagrange";
	public static final String EXTRAPOLATION_LINEAR = "Linear regeression";
	
	public static final int EXTRAPOLATION_NONE_INDEX = 0;
	public static final int EXTRAPOLATION_LAGRANGE_INDEX = 2;
	public static final int EXTRAPOLATION_LINEAR_INDEX = 1;
	
	public static final String EXTRAPOLATION_END_MEMBER = "EndMember";
	public static final String EXTRAPOLATION_START_MEMBER = "StartMember";
	
	public static final String[] EXTRAPOLATION_TYPES = {EXTRAPOLATION_NONE, EXTRAPOLATION_LINEAR, EXTRAPOLATION_LAGRANGE};
	public static String[] EXTRAPOLATION_TYPES_LBL = {EXTRAPOLATION_NONE_LBL, EXTRAPOLATION_LINEAR_LBL, EXTRAPOLATION_LAGRANGE_LBL};
	
	private String name;
	private Date startDate;
	private Date endDate;
	private String projectionLevel;
	
	private List<ProjectionMeasure> measureFormulas = new ArrayList<ProjectionMeasure>();
	
	private String type;
	
	private String comment;
	
	public static void init() {
		try {
			EXTRAPOLATION_NONE_LBL = FreeAnalysisWeb.LBL.ExtrapolationNone();
			EXTRAPOLATION_LAGRANGE_LBL = FreeAnalysisWeb.LBL.ExtrapolationLagrange();
			EXTRAPOLATION_LINEAR_LBL = FreeAnalysisWeb.LBL.ExtrapolationLinear();
			
			EXTRAPOLATION_TYPES_LBL = new String[]{EXTRAPOLATION_NONE_LBL, EXTRAPOLATION_LINEAR_LBL, EXTRAPOLATION_LAGRANGE_LBL};
		} catch(Exception e) {
			
		}
	}
	
	public Projection(){}
	
	public Projection(String name, Date enddate, Date startDate) {
		this.name = name;
		this.endDate = enddate;
		this.startDate = startDate;
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

	public void setEndDate(Date date) {
		this.endDate = date;
	}
	
	

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setMeasureFormulas(List<ProjectionMeasure> measureFormulas) {
		this.measureFormulas = measureFormulas;
	}

	public List<ProjectionMeasure> getMeasureFormulas() {
		return measureFormulas;
	}
	
	public void addMeasureFormula(ProjectionMeasure pm) {
		if(measureFormulas == null) {
			measureFormulas = new ArrayList<ProjectionMeasure>();
		}
		measureFormulas.add(pm);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setProjectionLevel(String projectionLevel) {
		this.projectionLevel = projectionLevel;
	}

	public String getProjectionLevel() {
		return projectionLevel;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
	
	
}
