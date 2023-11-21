package bpm.fwr.api.birt;

import java.util.Properties;

import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ElementFactory;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.StructureFactory;
import org.eclipse.birt.report.model.api.elements.structures.ComputedColumn;

import bpm.birt.fusioncharts.core.chart.AvailableChart;
import bpm.birt.fusioncharts.core.model.IChart;
import bpm.birt.fusioncharts.core.reportitem.FusionchartsItem;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class VanillaChartComponentCreator implements IComponentCreator<VanillaChartComponent> {

	@Override
	public DesignElementHandle createComponent(ReportDesignHandle designHandle, ElementFactory designFactory, String selectedLanguage, VanillaChartComponent component, String outputFormat) throws Exception {
		String vanillaRuntimeUrl = getVanillaRuntimeUrl();
		
		FusionchartsItem item = new FusionchartsItem(designFactory.newExtendedItem(component.getName(), FusionchartsItem.EXTENSION_NAME));
		item.setVanillaRuntimeUrl(vanillaRuntimeUrl);
		item.setXml(buildChartXml(component));
		
		Properties props = new Properties();
		for(String p : component.getOptions().getProperties().keySet()) {
			props.setProperty(p, component.getOptions().getProperties().get(p));
		}
		
		item.setCustomProperties(props);
		
		item.getItemHandle().setDataSet(designHandle.findDataSet(component.getDataset().getName()));
		item.getItemHandle().setHeight("300px");
		item.getItemHandle().setWidth(200);

		for (int i = 0; i < component.getColumnDetails().size(); i++) {
			ComputedColumn bindingColumnId = StructureFactory.newComputedColumn(item.getItemHandle(), "columnDetail_" + i);
			bindingColumnId.setExpression("dataSetRow[\"" + component.getColumnDetails().get(i).getDatasetRowExpr(selectedLanguage) + "\"]");
			((ReportItemHandle) item.getItemHandle()).addColumnBinding(bindingColumnId, false);
		}

		ComputedColumn bindingColumnValues = StructureFactory.newComputedColumn(item.getItemHandle(), "columnGroup");
		bindingColumnValues.setExpression("dataSetRow[\"" + component.getColumnGroup().getDatasetRowExpr(selectedLanguage) + "\"]");
		((ReportItemHandle) item.getItemHandle()).addColumnBinding(bindingColumnValues, false);

		return item.getItemHandle();
	}

	private String getVanillaRuntimeUrl() {
		return ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
	}

	private String buildChartXml(VanillaChartComponent chart) {
		IChart fschart = getFusionChart(chart);
		
		
		StringBuilder buf = new StringBuilder();
		buf.append("<root>\n");
		buf.append("  <chart>\n");
		buf.append("	<chartname>" + fschart.getChartName() + "</chartname>\n");
		buf.append("	<title>" + chart.getChartTitle() + "</title>\n");
		buf.append("	<id>" + chart.getChartTitle() + "</id>\n");
		buf.append("	<type>" + chart.getChartType().getTypeNumber() + "</type>\n");
		if(chart.getOptions().isGlassEnabled()){
			buf.append("	<glassstyle>1</glassstyle>\n");
		}
		else{
			buf.append("	<glassstyle>0</glassstyle>\n");			
		}
		if(chart.getOptions().getWidth() == 0){
			buf.append("	<width>400</width>\n");
		}
		else{
			buf.append("	<width>" + chart.getOptions().getWidth() + "</width>\n");				
		}
		
		if(chart.getOptions().getHeight() == 0){
			buf.append("	<height>300</height>\n");
		}
		else{
			buf.append("	<height>" + chart.getOptions().getHeight() + "</height>\n");				
		}
		
		buf.append("	<parameters></parameters>\n");
		buf.append("	<series>\n");
		for(int i=0; i<chart.getColumnDetails().size(); i++){
			buf.append("	  <serie>\n");
			buf.append("	    <name>" + chart.getColumnDetails().get(i).getName() + "</name>\n");
			buf.append("	    <color>" + "#00BFFF" + "</color>\n");
			buf.append("	    <expy>" + "row[\"columnDetail_" + i + "\"]" + "</expy>\n");
			buf.append("	    <agg>sum</agg>\n");
			buf.append("	  </serie>\n");
		}
		buf.append("	</series>\n");
		buf.append("	<expx>" + "row[\"columnGroup\"]" + "</expx>\n");
		buf.append("	<group>true</group>\n");
		buf.append("  </chart>\n");
		buf.append("</root>");
		
		return buf.toString();
	}

	private IChart getFusionChart(VanillaChartComponent chartFwr) {
		IChart[] charts = null;
        switch (chartFwr.getChartType().getTypeNumber()) {
            case IChart.COLUMN:  
            	charts = AvailableChart.AVAILABLE_CHARTS_COLUMN;
            	break;
            case IChart.BAR:  
            	charts = AvailableChart.AVAILABLE_CHARTS_BAR;
            	break;
            case IChart.LINE:  
            	charts = AvailableChart.AVAILABLE_CHARTS_LINE;
            	break;
            case IChart.PIE:  
            	charts = AvailableChart.AVAILABLE_CHARTS_PIE;
            	break;
            case IChart.DOUGHNUT:  
            	charts = AvailableChart.AVAILABLE_CHARTS_DOUGHNUT;
            	break;
            case IChart.RADAR:  
            	charts = AvailableChart.AVAILABLE_CHARTS_RADAR;
            	break;
            case IChart.PARETO:  
            	charts = AvailableChart.AVAILABLE_CHARTS_PARETO;
            	break;
            default: 
            	System.out.println("Invalid type of chart");
            	break;
        }
        
		int subType = chartFwr.getOptions().isStacked() ? 1 : 0;
		boolean is3D = chartFwr.getOptions().is3D();
		boolean isMS = chartFwr.getColumnDetails().size() > 1;
		boolean isScroll = false;
		
		if(subType == -1){
			return null;
		}
		
		for(IChart chart : charts){
			if(chart.getSubType() == subType){
				if(isMS && chart.isMultiSeries()){
					if(chart.isScrollable() && isScroll){
						if(is3D && chart.is3D()){
							return chart;
						}
						else if(!is3D && !chart.is3D()){
							return chart;
						}
					}
					else if(!isScroll && !chart.isScrollable()){
						if(is3D && chart.is3D()){
							return chart;
						}
						else if(!is3D && !chart.is3D()){
							return chart;
						}
					}
				}
				else if(!isMS){
					if(chart.isScrollable() && isScroll){
						if(is3D && chart.is3D()){
							return chart;
						}
						else if(!is3D && !chart.is3D()){
							return chart;
						}
					}
					else if(!isScroll && !chart.isScrollable()){
						if(is3D && chart.is3D()){
							return chart;
						}
						else if(!is3D && !chart.is3D()){
							return chart;
						}
					}
				}
			}
		}
		
		return null;
	}
	
}
