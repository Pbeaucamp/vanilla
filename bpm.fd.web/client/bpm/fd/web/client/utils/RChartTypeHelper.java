package bpm.fd.web.client.utils;

import com.google.gwt.resources.client.DataResource;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.web.client.images.DashboardImage;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeChart;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeDisplay;
import bpm.fd.web.client.panels.properties.RChartTypeProperties.TypeRChart;

public class RChartTypeHelper {
	
	
	
	public static RChartNature getRChartNature( RChartComponent component, TypeRChart typeChart){
		
		switch (typeChart) {
		
		case POINT:
			return RChartNature.getNature( RChartNature.POINT );
		case LINE:
			return RChartNature.getNature( RChartNature.LINE );
		case BAR_V:
			return RChartNature.getNature( RChartNature.BAR_V );
		case BAR_H:
			return RChartNature.getNature( RChartNature.BAR_H );
		case HIST:
			return RChartNature.getNature( RChartNature.HIST);
		case PIE:
			return RChartNature.getNature( RChartNature.PIE);
		case BOXPLOT:
			return RChartNature.getNature( RChartNature.BOXPLOT);
		case HEATMAP:
			return RChartNature.getNature( RChartNature.HEATMAP);
		case TREEMAP:
			return RChartNature.getNature( RChartNature.TREEMAP);
		case CORRELATION:
			return RChartNature.getNature( RChartNature.CORRELATION);
		case ACP:
			return RChartNature.getNature( RChartNature.ACP);
			
		default:
			 return RChartNature.getNature( RChartNature.POINT );
		}
		
	}
	
	public static TypeRChart findType( RChartNature nature ){
		
		switch( nature.getNature() ){
		case RChartNature.POINT:
			return TypeRChart.POINT;
		case RChartNature.LINE:
			return TypeRChart.LINE;
		case RChartNature.BAR_H:
			return TypeRChart.BAR_H;
		case RChartNature.BAR_V:
			return TypeRChart.BAR_V;
		case RChartNature.HIST:
			return TypeRChart.HIST;
		case RChartNature.PIE:
			return TypeRChart.PIE;
		case RChartNature.BOXPLOT:
			return TypeRChart.BOXPLOT;
		case RChartNature.HEATMAP:
			return TypeRChart.HEATMAP;
		case RChartNature.TREEMAP:
			return TypeRChart.TREEMAP;
		case RChartNature.ACP:
			return TypeRChart.ACP;
		case RChartNature.CORRELATION:
			return TypeRChart.CORRELATION;
			
		}
		return TypeRChart.POINT;
	}

	
	public static DataResource findImage( TypeRChart typeChart ){
		
		switch( typeChart){
		case POINT:
			return DashboardImage.INSTANCE.point();
		case LINE:
			return DashboardImage.INSTANCE.line();
		case BAR_V:
			return DashboardImage.INSTANCE.barVerticale();
		case BAR_H:
			return DashboardImage.INSTANCE.barHorizontale(); 
		case HIST:
			return DashboardImage.INSTANCE.histogramme();
		case PIE:
			return DashboardImage.INSTANCE.pieR();
		case BOXPLOT:
			return DashboardImage.INSTANCE.boxplot();
		case HEATMAP:
			return DashboardImage.INSTANCE.heatmap();
		case TREEMAP:
			return DashboardImage.INSTANCE.treemap();
		case CORRELATION:
			return DashboardImage.INSTANCE.correlation();
		case ACP:
			return DashboardImage.INSTANCE.acp();
			
		default:
			break;
		}
		
		return  null;
	}
	
	public static DataResource findImage(RChartNature nature) {
		TypeRChart typeChart = findType(nature);	
		return findImage(typeChart);
	}
}
