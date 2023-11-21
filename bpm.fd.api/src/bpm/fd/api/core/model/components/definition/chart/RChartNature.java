package bpm.fd.api.core.model.components.definition.chart;

import java.io.Serializable;

public class RChartNature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public final static int POINT = 0; // Nuage de point
	public final static int LINE = 1;  //
	public final static int BAR_V = 2;
	public final static int BAR_H = 3;
	public final static int HIST= 4;
	public final static int PIE = 5;
	public final static int BOXPLOT = 6;
	public final static int HEATMAP = 7;
	public final static int TREEMAP = 8;
	public final static int CORRELATION = 9;
	public final static int ACP = 10;



	

	
	public final static String[] NATURE_NAMES = new String[]{"Nuage", "Ligne", "Bar_V","Bar_H","Histogramme","Pie","Bloxplot","HeatMap","TreeMap","Correlation","ACP"};
	
	private static RChartNature[] natures = new RChartNature[NATURE_NAMES.length];
	static{
		for(int i=0; i<NATURE_NAMES.length; i++){
			natures[i] = new RChartNature(i);
		}
	}
	
	private int nature = POINT;

	private RChartNature() {
	}

	private RChartNature(int nature) {
		this.nature = nature;
	}

	public int getNature() {
		return nature;
	}
	
	public String getNatureName(){
		
		return NATURE_NAMES[this.nature];
	}
	public static RChartNature getNature(int nature) {
		return natures[nature];
	}
	
	
	public static int[] getAllTypeOfRChart(){
		
		
		return new int[]{POINT, LINE , BAR_V ,BAR_H ,HIST,PIE,BOXPLOT,HEATMAP,TREEMAP,CORRELATION,ACP} ;
		
	}
	
	
}
