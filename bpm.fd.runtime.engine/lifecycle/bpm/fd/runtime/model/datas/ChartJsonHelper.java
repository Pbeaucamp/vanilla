package bpm.fd.runtime.model.datas;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataPlot;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSerie;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSetRow;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class ChartJsonHelper {

	public static String getJson(ComponentChartDefinition chart, DashState state, HashMap<DataAggregation, DataSerie> datas, boolean refresh) throws Exception {
		boolean hasMultipleSeries = hasMultipleSeries(chart, datas);
		
		JSONObject root = new JSONObject();
		root.put("type", chart.getNature().getChartJsName());
		root.put("data", getData(chart, state, datas, hasMultipleSeries));
		root.put("options", getOptions(chart, state, hasMultipleSeries, refresh));
		
		if(datas.size() <= 1 && (chart.getNature().getNature() != ChartNature.PIE && chart.getNature().getNature() != ChartNature.PIE_3D)) {
			JSONObject obj = new JSONObject();
			obj.put("display", false);
			((JSONObject)root.get("options")).put("legend", obj);
		}
		
		return root.toString(); 
	}

	private static Object getOptions(ComponentChartDefinition chart, DashState state, boolean hasMultipleSeries, boolean refresh) throws Exception {
		JSONObject options = new JSONObject();
		JSONArray events = new JSONArray();
		events.put("click");
		options.put("events", events);
		options.put("maintainAspectRatio", false);

		JSONObject titleObj = new JSONObject();
		titleObj.put("display", true);
		
		StringBuffer buf = new StringBuffer();
		buf.append("function(chart) { ");
		buf.append("    var chartInstance = this.chart, ctx = chartInstance.ctx;");
		buf.append("    ctx.font = Chart.helpers.fontString(Chart.defaults.global.defaultFontSize, Chart.defaults.global.defaultFontStyle, Chart.defaults.global.defaultFontFamily);");
		buf.append("    ctx.textAlign = 'center';");
		buf.append("    ctx.textBaseline = 'bottom';");
		buf.append("    this.data.datasets.forEach(function (dataset, i) {");
		buf.append("        var meta = chartInstance.controller.getDatasetMeta(i);");
		buf.append("        meta.data.forEach(function (bar, index) {");
		buf.append("            var data = dataset.data[index];");
		buf.append("            ctx.fillText(data, bar._model.x + 5, bar._model.y - 5);");
		buf.append("        });");
		buf.append("    });");
		buf.append("}");
		

		//We set those options to print with wkhtmltopdf
		if(state.getComponentValue("print") != null && Boolean.parseBoolean(state.getComponentValue("print"))) {
			options.put("responsive", false);
			
			JSONObject animationDuration = new JSONObject();
			animationDuration.put("duration", 0);
			options.put("animation", animationDuration);
		}
		else {
			options.put("responsive", true);

			JSONObject animation = new JSONObject();
			animation.put("duration", 1);
			//We don't put the function on a refresh as it doesn't work
			if (!refresh) {
				animation.put("onComplete", new JSONFunction(buf.toString()));
			}
			options.put("animation", animation);
		}
		
		JSONObject scales = new JSONObject();
		options.put("scales", scales);
		
		for(IComponentOptions opt : chart.getOptions()) {
			for(String key : opt.getInternationalizationKeys()) {
				System.out.println("int - " + key);
				switch(key) {
				case "caption":
					String caption = opt.getValue(key);
					for(ComponentParameter p : chart.getParameters()){
						Component provider = (Component) state.getDashInstance().getDashBoard().getDesignParameterProvider(p);
						
						String pValue = state.getComponentValue(provider.getName());
						if (pValue == null){
							pValue = "Undefined";
						}
						caption = caption.replace("{$" + p.getName() + "}", pValue);
					}
					
					titleObj.put("text", caption);
					break;
				default:
					break;
				}
			}
			for(String key : opt.getNonInternationalizationKeys()) {
				System.out.println("nit - " + key);
				switch(key) {
					
					case "rotateLabels":
						if(Boolean.parseBoolean(opt.getValue(key))) {
							JSONArray axes = new JSONArray();
							JSONObject ticks = new JSONObject();
							ticks.put("autoSkip", false);
							ticks.put("maxRotation", 90);
							ticks.put("minRotation", 90);
							axes.put(ticks);
							scales.put("xAxes", axes);
						}
						break;
					default:
						break;
				}
			}
		}
		options.put("title", titleObj);
		
//		if(chart.getNature().isDualY()) {
//			
//			JSONArray yaxes = new JSONArray();
//			
//			JSONObject axe1 = new JSONObject();
//			JSONObject axe2 = new JSONObject();
//			
//			axe1.put("id", "left");
//			axe1.put("type", "linear");
//			axe1.put("position", "left");
//			
//			axe2.put("id", "right");
//			axe2.put("type", "linear");
//			axe2.put("position", "right");
//			
//			yaxes.put(axe1);
//			yaxes.put(axe2);
//			
//			scales.put("yAxes", yaxes);
//			
//		}

		
//		if (!hasMultipleSeries && (chart.getNature().getNature() != ChartNature.PIE && chart.getNature().getNature() != ChartNature.PIE_3D)) {
		JSONObject obj = new JSONObject();
		obj.put("display", false);
		options.put("legend", obj);
//		}
//		else {
//			JSONObject obj = new JSONObject();
//			obj.put("fontSize", 5);
//			options.put("legend", obj);
//		}
			
		//Add a legend function to create one in HTML so it can be modified
		buf = new StringBuffer();
		buf.append("function(chart) { ");
		buf.append("    var text = []; ");
		buf.append("    text.push('<ul class=\"' + chart.id + '-legend\">'); ");
		buf.append("    for (var i = 0; i < chart.data.datasets.length; i++) { ");
		buf.append("        text.push('<li><span style=\"background-color:' + ");
		buf.append("                   chart.data.datasets[i].backgroundColor + ");
		buf.append("                   '\"></span>'); ");
		buf.append("        if (chart.data.datasets[i].label) { ");
		buf.append("            text.push(chart.data.datasets[i].label); ");
		buf.append("        } ");
		buf.append("        text.push('</li>'); ");
		buf.append("    } ");
		buf.append("    text.push('</ul>'); ");
		buf.append("    return text.join(''); ");
		buf.append("}");

//		options.put("legendCallback", new JSONFunction(buf.toString()));

		return options;
	}

	private static Object getData(ComponentChartDefinition chart, DashState state, HashMap<DataAggregation, DataSerie> datas, boolean hasMultipleSeries) throws Exception {
		JSONObject data = new JSONObject();

		Map<String, String> categories = getCategories(datas, chart);
		
		//categories
		JSONArray labels = new JSONArray();
		JSONArray catValues = new JSONArray();
		for(String cat : categories.keySet()) {
			labels.put(cat);
			catValues.put(categories.get(cat));
		}
		
		data.put("labels", labels);
		data.put("catValues", catValues);
		JSONArray datasets = new JSONArray();

		if (datas != null) {
			int serieCount = datas.keySet().size();
		
		//datasets
//		int serieCount = datas.keySet().size();
			for(DataAggregation a : datas.keySet()) {
				DataSerie serie = datas.get(a);
				
				try {
					if(!categories.isEmpty()) {
						serie.orderPlots(new ChartAggregationer.PlotCategoryComparator(new ArrayList(categories.keySet())));
					}
					else {
						serie.orderPlots(new ChartAggregationer.PlotComparator(((IChartData) chart.getDatas()).getOrderType()));
					}
				} catch(Exception ex) { }
                
                DecimalFormat f = null;
                for(IComponentOptions opt : chart.getOptions()) {
                    if(opt instanceof NumberFormatOptions) {
                        NumberFormatOptions nfo = (NumberFormatOptions) opt;
                        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
                        //We set the decimal separator as dot because the chart does not support comma
                        //otherSymbols.setDecimalSeparator(nfo.getDecimalSeparator().charAt(0));
                        otherSymbols.setDecimalSeparator('.');
                        //We disable this because the chart does not support it.
    //					otherSymbols.setGroupingSeparator(nfo.getThousandSeparator().charAt(0));
                        String pattern = createNumberPattern(nfo);
                        f = new DecimalFormat(pattern, otherSymbols);
                        f.setGroupingUsed(false);
                        break;
                    }
                }
				
				Iterator<String> colorIterator = a.getColorsCode().iterator();
				
				List<DataSetRow> rows = serie.getOrganizedDataPlots(new ArrayList(categories.keySet()));
				if (rows != null && !rows.isEmpty()) {
	
					for(DataSetRow row : rows) {
	
						JSONArray dataArray = new JSONArray();
						JSONArray colorArray = new JSONArray();
						
						for(DataPlot p : row.getPlots()) {
							Object value = p.getValue();
							if (f != null && value != null) {
								value = f.format(value);
							}
							
							dataArray.put(value);
							
							boolean isColorDefined = false;
							// Apply color on monoseries
							if(chart.getColorRanges() != null && !chart.getColorRanges().isEmpty()) {
								double valueAsDouble = (Double) value;
								for(ColorRange cr : chart.getColorRanges()) {
									if(cr.getMin() <= valueAsDouble && cr.getMax() >= valueAsDouble) {
										String color = null;
										//See FusionChartGeneratorXML if you want to support drilledWithColor someday, I do not know what it is
	//										if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
	//											color = drilledWithColor;
	//										}
	//										else {
										color = cr.getHex();
	//										}
										colorArray.put(hexToRgb(color));
										isColorDefined = true;
										break;
									}
								}
							}
							
							if (!isColorDefined) {
								if(!colorIterator.hasNext()) {
									colorIterator = a.getColorsCode().iterator();
								}
								if(serieCount == 1) {
									colorArray.put(hexToRgb(colorIterator.next()));
								}
								else {
									colorArray.put(hexToRgb(a.getColorsCode().get(0)));
								}
							}
						}
						
						String serieLabel = a.getMeasureName();
						if (hasMultipleSeries && row.getSubCategory() != null && !row.getSubCategory().isEmpty() && !row.getSubCategory().equals("null")) {
							serieLabel = row.getSubCategory();
						}
						
						JSONObject dataset = buildDatasetJson(serieLabel, chart, a, dataArray, colorArray, serieCount);
						datasets.put(dataset);
					}
				}
				else {
					JSONArray dataArray = new JSONArray();
					JSONArray colorArray = new JSONArray();
					
					JSONObject dataset = buildDatasetJson(a.getMeasureName(), chart, a, dataArray, colorArray, serieCount);
					datasets.put(dataset);
				}
				
	//			JSONObject dataset = new JSONObject();
	//			if(serieCount > 1) {
	//				dataset.put("label", a.getMeasureName());
	//			}
	//			if(chart.getNature().isDualY()) {
	//				if(a.isPrimaryAxis()) {
	//					dataset.put("yAxisID", "left");
	//				}
	//				else {
	//					dataset.put("yAxisID", "right");
	//				}
	//			}
	//			
	//			String rendering = a.getRendering().toString().toLowerCase().replace("column", "bar");
	//			if(!rendering.equalsIgnoreCase(chart.getNature().getChartJsName()) && serieCount > 1) {
	//				dataset.put("type", rendering);
	//			}
	//			
	//			dataset.put("data", dataArray);
	//			dataset.put("backgroundColor", colorArray);
	//			dataset.put("fill", false);
	//			
	//			datasets.put(dataset);
			}
		}
		
		data.put("datasets", datasets);
		
		return data;
	}
	
	private static String createNumberPattern(NumberFormatOptions nfo) {
		String pattern = "###";
		if(nfo.getThousandSeparator() != null && !nfo.getThousandSeparator().isEmpty()) {
			pattern = "#" + "," + pattern + "," + "###";
		}
		if(nfo.getDecimals() > 0) {
			pattern += ".";
			for(int i = 0 ; i < nfo.getDecimals() ; i++) {
				pattern += "#";
			}
		}
		
		return pattern;
	}
	
	private static boolean hasMultipleSeries(ComponentChartDefinition chart, HashMap<DataAggregation, DataSerie> datas) {
		if (datas == null) {
			return false;
		}
		
		if (datas.size() > 1) {
			return true;
		}
		
		if (datas != null) {
			Map<String, String> categories = getCategories(datas, chart);
			
			for(DataAggregation a : datas.keySet()) {
				DataSerie serie = datas.get(a);

				List<DataSetRow> rows = serie.getOrganizedDataPlots(new ArrayList(categories.keySet()));
				if (rows != null && rows.size() > 1) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static JSONObject buildDatasetJson(String serieLabel, ComponentChartDefinition chart, DataAggregation a, JSONArray dataArray, JSONArray colorArray, int serieCount) throws JSONException {
		JSONObject dataset = new JSONObject();
		dataset.put("label", serieLabel);

//		if(chart.getNature().isDualY()) {
//			if(a.isPrimaryAxis()) {
//				dataset.put("yAxisID", "left");
//			}
//			else {
//				dataset.put("yAxisID", "right");
//			}
//		}
		
		String rendering = a.getRendering().toString().toLowerCase().replace("column", "bar");
		if(!rendering.equalsIgnoreCase(chart.getNature().getChartJsName()) && serieCount > 1) {
			dataset.put("type", rendering);
		}
		
		dataset.put("data", dataArray);
		if (chart.getNature().getNature() == ChartNature.LINE || chart.getNature().getNature() == ChartNature.LINE_MULTI) {
			dataset.put("borderColor", colorArray.get(0));
		}
		dataset.put("backgroundColor", colorArray);
		dataset.put("fill", false);
		
		return dataset;
	}
	
	private static String hexToRgb(String hex) {
	    hex = hex.replace("#", "");
        return "rgb(" +
        Integer.valueOf(hex.substring(0, 2), 16) + "," + 
        Integer.valueOf(hex.substring(2, 4), 16) + "," +
        Integer.valueOf(hex.substring(4, 6), 16) + ")";
	}
	
	private static Map<String, String> getCategories(HashMap<DataAggregation, DataSerie> datas, ComponentChartDefinition chart) {
//		if(!(chart.getDatas() instanceof MultiSerieChartData)) {
//			return Collections.EMPTY_LIST;
//		}

		Map<String, String> categories = new LinkedHashMap<>();

		if (datas != null) {
			for(DataAggregation a : datas.keySet()) {
				datas.get(a).orderPlots(new ChartAggregationer.PlotComparator(((IChartData)chart.getDatas()).getOrderType()));
				for(DataPlot p : datas.get(a).getPlots()) {
					boolean found = false;
					for(String s : categories.keySet()) {
						if(s.equals(p.getLabel())) {
							found = true;
							break;
						}
					}
					if(!found) {
						categories.put(p.getLabel(), p.getCategory().toString());
	
					}
	
				}
			}
		}
		return categories;
	}
	
}

