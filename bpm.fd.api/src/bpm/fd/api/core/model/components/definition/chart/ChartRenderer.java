package bpm.fd.api.core.model.components.definition.chart;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentRenderer;

public class ChartRenderer implements IComponentRenderer{

	//only fusion chart is available for perfomrance and removed flash liitation by
	//using html 5  since the xt version
	
	public static final int FUSION_CHART = 0;
	public static final int HIGHCHART = 1;
	
	//For R chart 
	public static final int RCHART = 2;
	
//	public static final int OPEN_FLASH_CHART = 1;
//	public static final int JFREE_CHART = 2;
//	public static final int FUSION_CHART_FREE = 3;
	
	public static final String[] RENDERER_NAMES = new String[]{
		"FusionChart", "HighCharts" , "RChart"};
//	"Open Flash Chart", "JFree Chart", "FusionChart Free"
//	};
	
	private static final ChartRenderer[] renderers = new ChartRenderer[]{
		new ChartRenderer(FUSION_CHART,  new int[]{
				ChartNature.BAR, ChartNature.BULLET, 
				ChartNature.COLUMN, ChartNature.COLUMN_3D, ChartNature.FUNNEL,
				ChartNature.GAUGE, ChartNature.LINE, ChartNature.PIE, 
				ChartNature.PIE_3D, ChartNature.PYRAMID, ChartNature.PYRAMID,
				ChartNature.RADAR, ChartNature.STACKED_BAR, ChartNature.STACKED_BAR_3D,
				ChartNature.STACKED_COLUMN, ChartNature.STACKED_COLUMN_3D, ChartNature.COLUMN_3D_MULTI,
				ChartNature.COLUMN_MULTI, ChartNature.LINE_MULTI,
				ChartNature.COLUMN_3D_LINE, ChartNature.COLUMN_3D_LINE_DUAL,
				ChartNature.COLUMN_LINE_DUAL, ChartNature.STACKED_COLUMN_3D_LINE_DUAL,
				ChartNature.BAR_3D,ChartNature.STACKED_AREA_2D, 
				ChartNature.MARIMEKO, ChartNature.SINGLE_Y_2D_COMBINATION, 
				ChartNature.SINGLE_Y_3D_COMBINATION, ChartNature.STACKED_2D_LINE_DUAL_Y, 
				ChartNature.DUAL_Y_2D_COMBINATION, ChartNature.SCATTER, ChartNature.BOX, ChartNature.SPARK, ChartNature.HEAT_MAP}), 
				
			new ChartRenderer(HIGHCHART,  new int[]{
					ChartNature.BAR, 
					ChartNature.COLUMN, ChartNature.FUNNEL,
					ChartNature.GAUGE, ChartNature.LINE, ChartNature.PIE, 
					ChartNature.SINGLE_Y_2D_COMBINATION, 
					ChartNature.DUAL_Y_2D_COMBINATION}),
					
			new ChartRenderer(RCHART, RChartNature.getAllTypeOfRChart() )
				
//		new ChartRenderer(OPEN_FLASH_CHART,  new int[]{
//				ChartNature.COLUMN, ChartNature.LINE, ChartNature.PIE, ChartNature.BAR,
//				ChartNature.COLUMN_MULTI, ChartNature.COLUMN_3D_LINE, ChartNature.LINE_MULTI}),
//				
//		new ChartRenderer(JFREE_CHART,  new int[]{
//				ChartNature.PIE, ChartNature.PIE_3D,
//				ChartNature.BAR, ChartNature.BAR_3D,
//				ChartNature.COLUMN, ChartNature.COLUMN_3D,
//				ChartNature.LINE,
//				ChartNature.COLUMN_3D_MULTI, ChartNature.COLUMN_MULTI,
//				ChartNature.LINE_MULTI, ChartNature.STACKED_BAR,
//				ChartNature.STACKED_BAR_3D, ChartNature.STACKED_COLUMN,
//				ChartNature.STACKED_COLUMN_3D, ChartNature.COLUMN_3D_LINE,
//				ChartNature.COLUMN_3D_LINE_DUAL, ChartNature.COLUMN_LINE_DUAL,
//				ChartNature.STACKED_COLUMN_3D_LINE_DUAL}),
//		
//		new ChartRenderer(FUSION_CHART_FREE, new int[]{
//				 ChartNature.BAR, ChartNature.COLUMN, ChartNature.COLUMN_3D,ChartNature.FUNNEL,
//				 ChartNature.LINE, ChartNature.PIE, ChartNature.PIE_3D, ChartNature.STACKED_BAR,
//				 ChartNature.STACKED_COLUMN, ChartNature.STACKED_COLUMN_3D, ChartNature.COLUMN_MULTI,
//				 ChartNature.LINE_MULTI, ChartNature.COLUMN_3D_LINE_DUAL})
	};
	
	private int style = 0;
	private int[] supportedNature;
	
	private ChartRenderer(int type, int[] supportedNature){
		this.style = type;
		this.supportedNature = supportedNature;
	}
	
	

	public int getRendererStyle() {
		return style;
	}
	
	public void setRendererStyle(int rendererStyle) {
		this.style = rendererStyle;
	}

	public Element getElement(){
		Element e = DocumentHelper.createElement("chartRenderer");
		e.addAttribute("rendererStyle", ""  + getRendererStyle());
		return e;
	}

	public static ChartRenderer getRenderer(int i) /*throws Exception*/{
//		if (i >= renderers.length){
//			throw new Exception("Unkown Chart renderer with Style=" + i);
//		}
		/*int a = i ;
		int taille = renderers.length;
		ChartRenderer r =  renderers[i] ;*/
		//System.out.println( " *********taillle de tableau renderer : "  + renderers.length +"   ************");
		return renderers[i];
	}

	public boolean supportNature(int nature){
		for(int i = 0; i < supportedNature.length; i++){
			if (supportedNature[i] == nature){
				return true;
			}
		}
		return false;
	}
	
}
