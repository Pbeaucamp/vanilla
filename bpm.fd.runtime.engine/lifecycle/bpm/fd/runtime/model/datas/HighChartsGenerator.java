package bpm.fd.runtime.model.datas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation.MeasureRendering;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataPlot;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSerie;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSetRow;

public class HighChartsGenerator {

	private ComponentChartDefinition chart;
	private DashState state;

	public HighChartsGenerator(ComponentChartDefinition chart, DashState state) {
		this.chart = chart;
		this.state = state;
	}

	public String getXml(HashMap<DataAggregation, DataSerie> data) {
		StringBuffer buf = new StringBuffer();
		
		writeChartOptions(data, buf);
		
		writeChartData(data, buf);
		
		System.out.println(buf.toString());
		
		return buf.toString();
	}

	private void writeChartData(HashMap<DataAggregation, DataSerie> data, StringBuffer buf) {
		buf.append("series: [\n");
		int i = 0;
		for(DataAggregation key : data.keySet()) {
			//serie properties
			buf.append("{\n");
			buf.append("	type: '"+getMeasureType(key)+"',\n");
			buf.append("	name: '"+key.getMeasureName()+"',\n");
			
			if(!key.isPrimaryAxis()) {
				buf.append("	yAxis: 1,\n");
			}
			
			DataSerie serie = data.get(key);
			serie.orderPlots(new ChartAggregationer.PlotComparator(((IChartData) chart.getDatas()).getOrderType()));
			
			//Serie data
			List<String> categoriesKey = new ArrayList<String>();
			int j = 0;
			List<DataSetRow> rows = serie.getOrganizedDataPlots(categoriesKey);
			for(DataSetRow row : rows) {
				//Data
				buf.append("	data: [\n");
				for(DataPlot plot : row.getPlots()) {
					buf.append("{\n");
					buf.append("	 name:'"+plot.getLabel()+"',\n");
					buf.append("	 y:"+plot.getValue()+",\n");
					
					//Data color
					
					try {
						int color = j % key.getColorsCode().size();
						buf.append("	color:'#"+key.getColorsCode().get(color)+"'\n");
					} catch (Exception e) {
						buf.append("	color:'#"+key.getColorsCode().get(0)+"'\n");
					}
					
					buf.append("}");
					if(!(j == row.getPlots().size() - 1)) {
						buf.append(",\n");
					}
					buf.append("\n");
					j++;
				}
				buf.append("]");
				
			}
			
			if(i == data.size() - 1) {
				buf.append("}\n");
			}
			else {
				buf.append("},\n");
			}
			
			i++;
		}
		buf.append("]\n");
	}

	private String getMeasureType(DataAggregation key) {

		String chartType = getChartType();
		
		if(chartType == null) {
			if(key.getRendering().equals(MeasureRendering.Area)) {
				return "area";
			}
			else if(key.getRendering().equals(MeasureRendering.Line)) {
				return "line";
			}
			else if(key.getRendering().equals(MeasureRendering.Column)) {
				return "column";
			}
		}
		
		return chartType;
	}

	/**
	 * 
	 * @return the chartType or null if it's a multi serie
	 */
	private String getChartType() {
		
		switch(chart.getNature().getNature()) {
			case ChartNature.PIE:
			case ChartNature.PIE_3D:
				return "pie";
			case ChartNature.BAR:
			case ChartNature.BAR_3D:
				return "bar";
			case ChartNature.COLUMN:
			case ChartNature.COLUMN_3D:
				return "column";
			case ChartNature.FUNNEL:
				return "funnel";
			case ChartNature.LINE:
				return "line";
		}
		
		return null;
	}

	private void writeChartOptions(HashMap<DataAggregation, DataSerie> data, StringBuffer buf) {
		GenericOptions genericOption = null;
		for(IComponentOptions option : chart.getOptions()) {
			if(option instanceof GenericOptions) {
				genericOption = (GenericOptions) option;
			}
		}
		
		//Chart part
		buf.append("chart: {\n");
		setChartStyle(buf, genericOption);
		buf.append("},\n");
		
		//Axis (only if dual y)
		boolean isDualY = false;
		for(DataAggregation agg : data.keySet()) {
			if(!agg.isPrimaryAxis()) {
				isDualY = true;
				break;
			}
		}
		if(isDualY) {
			buf.append("yAxis: [{title:{text:''}},{title:{text:''},opposite: true}],\n");
		}
		
		//Colors
		Palette palette = ((IChartData) chart.getDatas()).getColorPalette();
		if(palette != null) {
			buf.append("colors: [\n");
			int i = 0;
			for(String s : palette.getKeys()) {
				buf.append("	'#" + palette.getColor(s)+"'");
				if(!(i == palette.getKeys().size() - 1)) {
					buf.append(",");
				}
					
				i++;
			}
			buf.append("],\n");
		}
		
		//Title
		buf.append("title: {\n");
		buf.append("	text: '"+ genericOption.getCaption() +"'\n");
		buf.append("},\n");
		
		//Tooltip
		buf.append("tooltip: {\n");
		buf.append("	pointFormat: '{point.name}: {point.y}'\n");
		buf.append("},\n");	
		
		//Plot Options (Not used for now)
//		buf.append("plotOptions: {\n");
//		generatePlotOptions(buf);
//		buf.append("},\n");
		
	}

	private void setChartStyle(StringBuffer buf, GenericOptions genericOption) {
		
		if(genericOption != null) {
			String bgcolor = "#" + genericOption.getValue("bgColor");
			String borderColor = "#" + genericOption.getValue("borderColor");
			int borderWidth = genericOption.getBorderThickness();
			boolean showBorder = genericOption.isShowBorder();
			
			buf.append("	backgroundColor:'" + bgcolor + "',\n");
			if(showBorder) {
				buf.append("	borderColor:'" + borderColor + "',\n");
				buf.append("	borderWidth:" + borderWidth + "\n");
			}
		}
		
		
	}

	private void generatePlotOptions(StringBuffer buf) {
		
		buf.append(""+getChartType()+": {\n");
		buf.append("	allowPointSelect: true,\n");
		buf.append("	cursor: 'pointer',\n");
		buf.append("	dataLabels: {\n");
		buf.append("	enabled: true,\n");
		buf.append("	color: '#000000',\n");
		buf.append("	connectorColor: '#000000',\n");
		buf.append("	format: '{point.name}: {point.y}'\n");
		buf.append("	}\n");
		buf.append("}\n");
    }
	
	
}
