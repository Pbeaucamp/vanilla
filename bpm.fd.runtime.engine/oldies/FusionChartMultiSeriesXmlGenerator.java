package bpm.fd.runtime.engine.chart.fusion.generator;

import java.util.Properties;


public class FusionChartMultiSeriesXmlGenerator extends AsbtractFusionMSXmlGenerator{
	public FusionChartMultiSeriesXmlGenerator(boolean oldWay){
		super(oldWay);
		ROOT = "chart";
		CATEGORIES = "categories";
		CATEGORIE = "category";
		CATEGORIE_LABEL = "label";
		DATA_SET = "dataset";
		DATA_SET_SERIESNAME = "seriesName";
		DATA_SET_RENDER_AS = "renderAs";
		DATA_SET_PARENTYAXIS = "parentYAxis";
		DATA_SET_COLOR = "color";
		
		DATA_SET_SET = "set";
		DATA_SET_SET_VALUE = "value";
		
		DRILL = "link";
	}

	@Override
	protected void overrideProperties(Properties prop) {
		
		
	}
	
}
