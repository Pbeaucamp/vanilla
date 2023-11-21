package bpm.fd.runtime.engine.utils;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;

public class StackableCellComponentType {

	public static String getTypeByComponent(IComponentDefinition def) {
		
		if(def instanceof ComponentButtonDefinition) {
			return "button";
		}
		else if(def instanceof ComponentChartDefinition) {
			return "chart";
		}
		else if(def instanceof ComponentDataGrid) {
			return "datagrid";
		}
		else if(def instanceof ComponentFaView) {
			return "cube";
		}
		else if(def instanceof ComponentFilterDefinition) {
			return "filter";
		}
		else if(def instanceof ComponentGauge) {
			return "gauge";
		}
		else if(def instanceof ComponentJsp) {
			return "jsp";
		}
		else if(def instanceof ComponentD4C) {
			return "d4c";
		}
		else if(def instanceof ComponentLink) {
			return "link";
		}
		else if(def instanceof ComponentPicture) {
			return "picture";
		}
		else if(def instanceof ComponentReport) {
			return "report";
		}
		else if(def instanceof ComponentKpi) {
			return "kpi";
		}
		else if(def instanceof ComponentStyledTextInput) {
			return "textinput";
		}
		else if(def instanceof ComponentTimer) {
			return "timer";
		}
		else if (def instanceof LabelComponent) {
			return "label";
		}
		else if(def instanceof ComponentFlourish) {
			return "flourish";
		}
		
		return "chart";
	}
	
}
