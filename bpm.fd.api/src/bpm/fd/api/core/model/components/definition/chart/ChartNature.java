package bpm.fd.api.core.model.components.definition.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChartNature implements Serializable {

	private static final long serialVersionUID = 1L;

	public static ChartNature[] getMonoSeries() {
		List<ChartNature> l = new ArrayList<ChartNature>();
		for (ChartNature n : natures) {
			if (n.isMonoSerie()) {
				l.add(n);
			}
		}
		return l.toArray(new ChartNature[l.size()]);
	}

	public static ChartNature[] getMultiSeries() {
		List<ChartNature> l = new ArrayList<ChartNature>();
		for (ChartNature n : natures) {
			if (n.isMultiSerie()) {
				l.add(n);
			}
		}
		return l.toArray(new ChartNature[l.size()]);
	}

	public static ChartNature getNature(String natureName) {
		for (ChartNature n : natures) {
			if (NATURE_NAMES[n.getNature()].equals(natureName)) {
				return n;
			}
		}
		return null;
	}

	/*
	 * monoseries
	 */
	public final static int PIE = 0;
	public final static int PIE_3D = 1;
	public final static int COLUMN = 2;
	public final static int COLUMN_3D = 3;
	public final static int BAR = 4;
	public final static int LINE = 9;
	public final static int PYRAMID = 12;
	public final static int FUNNEL = 11;

	/*
	 * multiseries
	 */
	public final static int COLUMN_MULTI = 15;
	public final static int COLUMN_3D_MULTI = 16;
	public final static int LINE_MULTI = 17;
	public final static int STACKED_COLUMN = 5;
	public final static int STACKED_COLUMN_3D = 6;
	public final static int STACKED_BAR = 7;
	public final static int STACKED_BAR_3D = 8;
	public final static int COLUMN_LINE_DUAL = 18;
	public final static int COLUMN_3D_LINE = 19;
	public final static int COLUMN_3D_LINE_DUAL = 20;
	public final static int STACKED_COLUMN_3D_LINE_DUAL = 21;
	public final static int BAR_3D = 22;
	public final static int STACKED_AREA_2D = 23;
	public final static int MARIMEKO = 24;
	public final static int SINGLE_Y_2D_COMBINATION = 25;
	public final static int SINGLE_Y_3D_COMBINATION = 26;
	public final static int STACKED_2D_LINE_DUAL_Y = 27;
	public final static int DUAL_Y_2D_COMBINATION = 28;
	public final static int SPARK  = 29;
	public final static int SCATTER = 30;
	public final static int BOX = 31;
	public final static int HEAT_MAP  = 32;

	public final static int RADAR = 10;
	public final static int GAUGE = 13;
	public final static int BULLET = 14;
	
	public final static int MSBAR2D = 33;

	public final static String[] NATURE_NAMES = new String[] { "Pie", "Pie 3D", "Column", "Column 3D", "Bar", "Stacked Column", "Stacked Column 3D", "Stacked Bar", "Stacked Bar 3D", "Line", "Radar", "Funnel", "Pyramid", "Gauge", "Bullet", "Column", "Column 3D", "Line", "Column + Line DualY", "Column 3D + Line", "Column 3D + Line DualY", "Stacked Column 3D + Line DualY", "Bar3D", "Stacked Area", "Marimeko", "Combination 2D Single Y", "Combination 3D Single Y", "Stacked 2D + Line Dual Y", "Combination 2D Dual Y" , "Spark", "Scatter", "Box", "Heat Map", "Ms bar2D"};

	private static ChartNature[] natures = new ChartNature[NATURE_NAMES.length];
	static {
		for (int i = 0; i < NATURE_NAMES.length; i++) {
			natures[i] = new ChartNature(i);
		}
	}

	private int nature = PIE;

	private ChartNature() {
	}

	private ChartNature(int nature) {
		this.nature = nature;
	}

	public int getNature() {
		return nature;
	}

	public boolean isMultiSerie() {
		switch (nature) {
		case STACKED_BAR:
		case STACKED_BAR_3D:
		case STACKED_COLUMN:
		case STACKED_COLUMN_3D:
		case RADAR:
		case COLUMN_MULTI:
		case LINE_MULTI:
		case COLUMN_3D_MULTI:

		case COLUMN_3D_LINE:
		case COLUMN_3D_LINE_DUAL:
		case COLUMN_LINE_DUAL:
		case STACKED_COLUMN_3D_LINE_DUAL:
		case BAR_3D:
		case STACKED_AREA_2D:
		case MARIMEKO:
		case SINGLE_Y_2D_COMBINATION:
		case SINGLE_Y_3D_COMBINATION:
		case STACKED_2D_LINE_DUAL_Y:
		case DUAL_Y_2D_COMBINATION:
		case SCATTER:
		case BOX:
		case HEAT_MAP:
			return true;

		}

		return false;
	}

	public boolean is3D() {
		switch (nature) {
		case PIE_3D:
		case COLUMN_3D:
		case COLUMN_3D_MULTI:
		case STACKED_COLUMN_3D:
		case STACKED_BAR_3D:
		case COLUMN_3D_LINE:
		case COLUMN_3D_LINE_DUAL:
		case STACKED_COLUMN_3D_LINE_DUAL:
		case BAR_3D:
		case SINGLE_Y_3D_COMBINATION:
			return true;
		}

		return false;
	}

	public boolean isDualY() {
		switch (nature) {

		case COLUMN_3D_LINE_DUAL:
		case COLUMN_LINE_DUAL:
		case STACKED_COLUMN_3D_LINE_DUAL:
		case DUAL_Y_2D_COMBINATION:
		case STACKED_2D_LINE_DUAL_Y:
		case HEAT_MAP:
			return true;

		}

		return false;
	}

	public boolean isSepcial() {
		switch (nature) {
		case BULLET:
		case GAUGE:
			return true;
		}
		return false;
	}

	public boolean isStacked() {
		switch (nature) {
		case STACKED_COLUMN:
		case STACKED_COLUMN_3D:
		case STACKED_BAR:
		case STACKED_BAR_3D:
		case STACKED_COLUMN_3D_LINE_DUAL:
		case STACKED_AREA_2D:
		case STACKED_2D_LINE_DUAL_Y:
			return true;
		}
		return false;
	}

	public boolean isMonoSerie() {
		return !isMultiSerie() && !isSepcial();
	}

	public static ChartNature getNature(int nature) {
		return natures[nature];
	}

	public boolean isLineCombnation() {
		switch (nature) {

		case COLUMN_3D_LINE:
		case COLUMN_3D_LINE_DUAL:
		case COLUMN_LINE_DUAL:
		case STACKED_COLUMN_3D_LINE_DUAL:
		case STACKED_2D_LINE_DUAL_Y:
			return true;

		}

		return false;
	}

	public boolean isCombination() {
		switch (nature) {
		case SINGLE_Y_2D_COMBINATION:
		case SINGLE_Y_3D_COMBINATION:
		case DUAL_Y_2D_COMBINATION:
			return true;

		}

		return false;
	}

	public String getChartJsName() {
		
		switch (nature) {
			case BAR:
			case BAR_3D:
			case MSBAR2D:
				return "horizontalBar";
			case BOX :
			case BULLET:
			case FUNNEL:
			case GAUGE:
			case HEAT_MAP:
				return "bar";
			case COLUMN:
			case COLUMN_3D:
			case COLUMN_3D_LINE:
			case COLUMN_3D_LINE_DUAL:
			case COLUMN_MULTI:
			case DUAL_Y_2D_COMBINATION:
				return "bar";		
			case LINE:
			case LINE_MULTI:
				return "line";
			case PIE: 
			case PIE_3D:
				return "pie";
			case RADAR:
				return "radar";
			case STACKED_2D_LINE_DUAL_Y:
			case STACKED_BAR:
			case STACKED_BAR_3D:
			case STACKED_COLUMN:
			case STACKED_COLUMN_3D:
			case STACKED_COLUMN_3D_LINE_DUAL:
				return "bar";
			
			default: break;
				
		}
		return "bar";
	}
}
