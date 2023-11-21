package bpm.fd.runtime.engine.chart.fusion.generator;

import java.util.Properties;


public class FusionChartXmlGenerator extends AbstractFusionXmlGenerator{
	
	public FusionChartXmlGenerator(boolean oldWay){
		super(oldWay);
		ROOT = "chart";
		SET = "set";
		SET_LABEL = "label";
		SET_VALUE = "value";
		SET_COLOR = "color";

		DRILL = "link";
	}

	@Override
	protected void overrideProperties(Properties prop) {
				
	}
}
