package bpm.fwr.api.beans.components;

import java.io.Serializable;

public class ChartType implements Serializable {
	
	private static final long serialVersionUID = 8004525347555675087L;

	public static ChartType[] CHART_TYPES_CLASSIC = {
		new ChartType("Bar Chart", ChartTypes.BAR, true, true, true, true),
		new ChartType("Line Chart", ChartTypes.LINE, true, false, false, false),
		new ChartType("Pie Chart", ChartTypes.PIE, true, true, false, false),
		new ChartType("Scatter Chart", ChartTypes.SCATTER, true, false, false, false),
		new ChartType("Area Chart", ChartTypes.AREA, true, false, false, false)
	};
	
	public static ChartType[] CHART_TYPES_FUSION = {
		new ChartType("Column Chart", ChartTypes.COLUMN, false, true, true, true),
		new ChartType("Bar Chart", ChartTypes.BAR, false, true, true, true),
		new ChartType("Line Chart", ChartTypes.LINE, false, false, false, false),
		new ChartType("Pie Chart", ChartTypes.PIE, false, true, false, false),
		new ChartType("Doughtnut Chart", ChartTypes.DOUGHNUT, false, true, false, false),
		new ChartType("Radar Chart", ChartTypes.RADAR, false, false, false, false)
	};
	
	private String name;
	private ChartTypes type;
	private boolean isClassic;
	private boolean is3D;
	private boolean isGlassEnabled;
	private boolean isStacked;
	
	public ChartType(){ }
	
	public ChartType(String name, ChartTypes type, boolean isClassic, boolean is3D, boolean isGlassEnabled, boolean isStacked){
		this.name = name;
		this.type = type;
		this.isClassic = isClassic;
		this.is3D = is3D;
		this.isGlassEnabled = isGlassEnabled;
		this.isStacked = isStacked;
	}

	public String getName() {
		return name;
	}

	public ChartTypes getType() {
		return type;
	}

	public boolean isClassic() {
		return isClassic;
	}

	public boolean isIs3D() {
		return is3D;
	}

	public boolean isGlassEnabled() {
		return isGlassEnabled;
	}

	public boolean isStacked() {
		return isStacked;
	}

	public int getTypeNumber() {
		if(type.getType().equals(ChartTypes.COLUMN.getType())){
			return 0;
		}
		else if(type.getType().equals(ChartTypes.BAR.getType())){
			return 1;
		}
		else if(type.getType().equals(ChartTypes.LINE.getType())){
			return 2;
		}
		else if(type.getType().equals(ChartTypes.PIE.getType())){
			return 3;
		}
		else if(type.getType().equals(ChartTypes.DOUGHNUT.getType())){
			return 4;
		}
		else if(type.getType().equals(ChartTypes.RADAR.getType())){
			return 5;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}

//	public int getTypeNumber() {
//		switch (type) {
//		case COLUMN:
//			return 0;
//		case BAR:
//			return 1;
//		case LINE:
//			return 2;
//		case PIE:
//			return 3;
//		case DOUGHNUT:
//			return 4;
//		case RADAR:
//			return 5;
//			
//		default:
//			break;
//		}
//		return 0;
//	}
}
