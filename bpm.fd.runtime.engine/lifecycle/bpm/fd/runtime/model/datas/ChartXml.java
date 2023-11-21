package bpm.fd.runtime.model.datas;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillInfo;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.datas.BubbleAggregationer.BubblePlot;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataPlot;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSerie;
import bpm.fd.runtime.model.datas.ChartAggregationer.DataSetRow;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

/**
 * This class is used to create the FusionChart xml from the resultSet datas
 * 
 * @author ludo
 * 
 */
public class ChartXml {
	private static final String ROOT = "chart";
	private static final String SET = "set";
	private static final String LABEL = "label";
	private static final String VALUE = "value";
	private static final String COLOR = "color";
	private static final String CATEGORIES = "categories";
	private static final String CATEGORIE = "category";
	private static final String DATASET = "dataset";
	private static final String DATASET_NAME = "seriesName";
	private static final String DRILL = "link";
	private static final String DATA_SET_PARENTYAXIS = "parentYAxis";
	private static final String DATA_SET_RENDER_AS = "renderAs";
	private static final String Y_AXIS_NAME = "yAxisName";
	private static final String PRIMARY_AXIS_NAME = "PYAxisName";
	private ComponentChartDefinition chart;
	private DashState state;
	
	private int labelSize = 0;
	private String drilledWithColor;

	public ChartXml(ComponentChartDefinition chart, DashState state) {
		this.chart = chart;
		this.state = state;
		
		drilledWithColor = state.getDrillColor(chart.getName());
	}

	public String getXml(HashMap<DataAggregation, DataSerie> datas) throws Exception {

		Document doc = generateDocumentXml(datas);

		OutputFormat f = OutputFormat.createCompactFormat();
		f.setAttributeQuoteCharacter('\'');

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		XMLWriter writer = new XMLWriter(bos, f);

		writer.setEscapeText(false);
		writer.write(doc.getRootElement());
		writer.close();

		String xml = bos.toString("UTF-8");
		
		Logger.getLogger(ChartXml.class).debug(chart.getName() + " xml result -> " + xml);
		return xml;
	}

	private Document generateDocumentXml(HashMap<DataAggregation, DataSerie> datas) {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ROOT);

		/*
		 * apply font style
		 */
		addChartOptions(root);
		
		if(chart.getNature().getNature() == ChartNature.SCATTER) {
			try {
				generateBubbleXml(root, datas);
				return doc;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		else if(chart.getNature().getNature() == ChartNature.HEAT_MAP) {
			try {
				generateHeatMapXml(root, datas);
				return doc;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * categories
		 */
		List<String> categoriesKey = addCategories(datas, root);

		/*
		 * create series
		 */
		for(DataAggregation a : datas.keySet()) {
			DataSerie serie = datas.get(a);

			try {
				if(!categoriesKey.isEmpty()) {
					serie.orderPlots(new ChartAggregationer.PlotCategoryComparator(categoriesKey));
				}
				else {
					serie.orderPlots(new ChartAggregationer.PlotComparator(((IChartData) chart.getDatas()).getOrderType()));
				}
			} catch(Exception ex) {

			}

			Iterator<String> colorIterator = a.getColorsCode().iterator();

			for(DataSetRow row : serie.getOrganizedDataPlots(categoriesKey)) {
				String color = "";
				Element dataSetsE = createDataSetsElement(a, row.getSubCategory());

				boolean colorApplyed = false;
				if(dataSetsE == null) {
					// means that it is a mono serie chart
					dataSetsE = root;
				}
				else {
					root.add(dataSetsE);
					if(chart.getColorRanges() == null || chart.getColorRanges().isEmpty()) {
						colorApplyed = dataSetsE.attribute(COLOR) != null;
						if(!colorApplyed) {
							Palette palette = ((IChartData) chart.getDatas()).getColorPalette();
	
							if(palette != null) {
								for(String s : palette.getKeys()) {
									if(palette.getColor(s) != null && dataSetsE.attributeValue(DATASET_NAME).toLowerCase().contains(s.toLowerCase())) {
										if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
											color = drilledWithColor;
										}
										else {
											color = palette.getColor(s);
										}
										dataSetsE.addAttribute(COLOR, "#" + color);
										colorApplyed = true;
										break;
									}
								}
							}
							if(!colorApplyed) {
								if(colorIterator.hasNext()) {
									if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
										color = drilledWithColor;
									}
									else {
										color = colorIterator.next();
									}
									dataSetsE.addAttribute(COLOR, color);
								}
							}
						}
						else {
							color = dataSetsE.attribute(COLOR).getStringValue().substring(1, dataSetsE.attribute(COLOR).getStringValue().length());
						}
					}
				}

				for(DataPlot plot : row.getPlots()) {
					
					Element set = dataSetsE.addElement(SET);
					
					if(labelSize > 0) {
						
						String l = "";
						String tooltip = "";
						try {
							l = plot.getLabel().substring(0, labelSize);
							tooltip = plot.getLabel().substring(labelSize, plot.getLabel().length());
						} catch(Exception e) {
							l = plot.getLabel();
							tooltip = plot.getLabel();
						}
						
						set.addAttribute(LABEL, l);
						
						if(chart.getNature().getNature() != ChartNature.BOX) {
							
							Object value = plot.getValue();
							for(IComponentOptions opt : chart.getOptions()) {
								if(opt instanceof NumberFormatOptions) {
									NumberFormatOptions nfo = (NumberFormatOptions) opt;
									String pattern = createNumberPattern(nfo);
									DecimalFormat f = new DecimalFormat(pattern);
									value = f.format(value);
									break;
								}
							}
							
							set.addAttribute("toolText", tooltip + ", " + value);
						}
					}
					else {
						if(chart.getNature().getNature() != ChartNature.BOX) {
							set.addAttribute(LABEL, plot.getLabel());
						}
					}
					
					// apply color on monoseries
					if(dataSetsE == root) {
						
						if(chart.getColorRanges() != null && !chart.getColorRanges().isEmpty()) {
							double value = (Double) plot.getValue();
							for(ColorRange cr : chart.getColorRanges()) {
								if(cr.getMin() <= value && cr.getMax() >= value) {
									if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
										color = drilledWithColor;
									}
									else {
										color = cr.getHex();
									}
									set.addAttribute(COLOR, "#" + color);
								}
							}
						}
						else {
							colorApplyed = false;
							Palette palette = ((IChartData) chart.getDatas()).getColorPalette();
	
							if(palette != null) {
								for(String s : palette.getKeys()) {
									if(palette.getColor(s) != null && set.attributeValue(LABEL).toLowerCase().contains(s.toLowerCase())) {
										if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
											color = drilledWithColor;
										}
										else {
											color = palette.getColor(s);
										}
										set.addAttribute(COLOR, "#" + color);
										colorApplyed = true;
										break;
									}
								}
							}
							if(!colorApplyed) {
								if(colorIterator.hasNext()) {
									if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
										color = drilledWithColor;
									}
									else {
										color = colorIterator.next();
									}
									set.addAttribute(COLOR, color);
								}
							}
						}	
					}
					else {
						if(chart.getColorRanges() != null && !chart.getColorRanges().isEmpty()) {
							try {
								double value = (Double) plot.getValue();
								for(ColorRange cr : chart.getColorRanges()) {
									if(cr.getMin() <= value && cr.getMax() >= value) {
										if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
											color = drilledWithColor;
										}
										else {
											color = cr.getHex();
										}
										set.getParent().addAttribute(COLOR, "#" + color);
									}
								}
							} catch (Exception e) {
							}
						}
					}

					if(chart.getNature().getNature() == ChartNature.BOX) {
						if(plot.getAllValues() != null && !plot.getAllValues().isEmpty()) {
							StringBuilder buf = new StringBuilder();
							boolean first = true;
							for(Object o : plot.getAllValues()) {
								if(first) {
									first = false;
								}
								else {
									buf.append(",");
								}
								buf.append(o);
							}
							set.addAttribute(VALUE, buf.toString());
						}
					}
					else {
						if(plot.getValue() != null) {
							set.addAttribute(VALUE, plot.getValue() + "");
						}
					}

					if(chart.getDrillDatas() != null && chart.getDrillDatas().isDrillable()) {
						try {
							addDrillElement(set, plot, chart.getDrillDatas(), color);
						} catch(Exception ex) {

						}
					}
				}
			}
		}

		return doc;

	}
	
	private void generateHeatMapXml(Element root,
			HashMap<DataAggregation, DataSerie> datas) {
		
		root.addAttribute("showplotborder", "1");
		
		DecimalFormat f = null;
		
		for(IComponentOptions opt : chart.getOptions()) {
			if(opt instanceof NumberFormatOptions) {
				NumberFormatOptions nfo = (NumberFormatOptions) opt;
				String pattern = createNumberPattern(nfo);
				f = new DecimalFormat(pattern);
				break;
			}
		}
		Element dataset = root.addElement(DATASET);
		for(DataAggregation da : datas.keySet()) {
			DataSerie ds = datas.get(da);
			
			for(DataPlot dp : ds.getPlots()) {
				Object val = null;
				if(f != null) {
					val = f.format(dp.getValue());
				}
				else {
					val = dp.getValue();
				}
				
				Element value = dataset.addElement(SET);
				if(datas.size() > 1) {
					value.addAttribute("rowId", da.getMeasureName());
					value.addAttribute("toolText", dp.getLabel() + ", " + da.getMeasureName() + " : " + val);
				}
				else {
					value.addAttribute("rowId", dp.getSubCategory());
					value.addAttribute("toolText", dp.getLabel() + ", " + dp.getSubCategory() + " : " + val);
				}
				value.addAttribute("columnId", dp.getLabel());
				value.addAttribute("value", val + "");
				
			}
		}
		Element cRange = root.addElement("colorrange");
		cRange.addAttribute("gradient", "1");
		
		int minValue = Integer.MAX_VALUE;
		int maxValue = Integer.MIN_VALUE;
		String minColor = "";
		
		for(ColorRange range : chart.getColorRanges()) {
			if(range.getMin() < minValue) {
				minValue = range.getMin();
				minColor = range.getHex();
			}
			if(range.getMax() > maxValue) {
				maxValue = range.getMax();
			}
			Element value = cRange.addElement("color");
			value.addAttribute("maxvalue", range.getMax()+"");
			value.addAttribute("code", range.getHex());
			value.addAttribute("label", range.getName());
		}
		
		cRange.addAttribute("minvalue", minValue + "");
		cRange.addAttribute("maxvalue", maxValue + "");
		cRange.addAttribute("code", minColor);
	}
	
	private void generateBubbleXml(Element root, HashMap<DataAggregation, DataSerie> datas) throws Exception {
		
		List<BubblePlot> plots = new BubbleAggregationer().getBubblePlots(chart, datas);
		
		List<String> colors = datas.keySet().iterator().next().getColorsCode();
		
		Element categories = root.addElement(CATEGORIES);
		
		//order plots by x
		Collections.sort(plots, new Comparator<BubblePlot>() {
			@Override
			public int compare(BubblePlot o1, BubblePlot o2) {
				return (int) (o1.getX() - o2.getX());
			}
		});
		
		//Categories
		int min = 0;
		int max = (int) (plots.get(plots.size() - 1).getX() + 1);
		
		createCategoryElement(categories, min);
		
		for(int i = 1 ; i < 5 ; i++) {
			int val = (((max) / 5) * i);
			createCategoryElement(categories, val);
		}
		
		createCategoryElement(categories, max);
		
		//values
		Element dataset = null;
		String previousSub = "";
		HashMap<String, String> colorsSeries = new HashMap<String, String>();
		HashMap<String , Element> existing = new HashMap<String , Element>();
		for(BubblePlot plot : plots) {
			if(plot.getSub() == null || plot.getSub().isEmpty() || !plot.getSub().equals(previousSub)) {
				previousSub = plot.getSub();
				if(existing.get(previousSub) == null) {
					existing.put(previousSub, root.addElement(DATASET));
				}
				if(colorsSeries.get(previousSub) == null) {
					colorsSeries.put(previousSub, colors.get(colorsSeries.size()));
				}
				dataset = existing.get(previousSub);
				if(plot.getSub() != null && !plot.getSub().isEmpty()) {
					dataset.addAttribute("seriesname", plot.getSub());
				}
				dataset.addAttribute("color", "#" + colorsSeries.get(previousSub));
				dataset.addAttribute("anchorbgcolor", "#" + colorsSeries.get(previousSub));
				
			}
			Element value = dataset.addElement(SET);
			value.addAttribute("x", plot.getX() + "");
			value.addAttribute("y", plot.getY() + "");
			value.addAttribute("tooltext", plot.getLabel() != null ? plot.getLabel() : plot.getCategory());
			value.addAttribute("showplotborder", "1");
			value.addAttribute("anchorradius", "5");
			value.addAttribute("displayValue", plot.getLabel() != null ? plot.getLabel() : plot.getCategory());
		}
		
		BubblePlot verticalMed = plots.get(plots.size() / 2);
		
		Collections.sort(plots, new Comparator<BubblePlot>() {
			@Override
			public int compare(BubblePlot o1, BubblePlot o2) {
				return (int) (o1.getY() - o2.getY());
			}
		});
		BubblePlot HorizontalMed = plots.get(plots.size() / 2);
        
        root.addAttribute("drawQuadrant", "1");
		root.addAttribute("quadrantLineAlpha", "80");
		root.addAttribute("quadrantLineThickness", "3");
		root.addAttribute("quadrantXVal", verticalMed.getX() + "");
		root.addAttribute("quadrantYVal", HorizontalMed.getY() + "");
		
		GenericNonPieOptions opt = (GenericNonPieOptions) chart.getOptions(GenericNonPieOptions.class);
		String x = opt.getxAxisName();
		String y = opt.getPYAxisName();
		if(y == null || y.isEmpty()) {
			y = opt.getSYAxisName();
		}
		
		root.addAttribute("quadrantLabelTL", "low " + x + " / high " + y);
		root.addAttribute("quadrantLabelTR", "high " + x + " / high " + y);
		root.addAttribute("quadrantLabelBL", "low " + x + " / low " + y);
		root.addAttribute("quadrantLabelBR", "high " + x + " / low " + y);
		root.addAttribute("showTrendlineLabels", "1");
	}

	private void createCategoryElement(Element categories, int min) {
		Element catMin = categories.addElement(CATEGORIE);
		catMin.addAttribute("x", min + "");
		catMin.addAttribute("label", min + "");
		catMin.addAttribute("showverticalline", 1 + "");
		catMin.addAttribute("verticallinealpha", 10 + "");
		
	}

	private String createNumberPattern(NumberFormatOptions nfo) {
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

	private Element createDataSetsElement(DataAggregation a, String subCategory) {
		if(!(chart.getDatas() instanceof MultiSerieChartData)) {
			return null;
		}

		Element e = DocumentHelper.createElement(DATASET);
		if(a.isApplyOnDistinctSerie()) {
			e.addAttribute(DATASET_NAME, subCategory);
		}
		else {
			e.addAttribute(DATASET_NAME, a.getMeasureName());
		}

		if(!a.isPrimaryAxis()) {
			e.addAttribute(DATA_SET_PARENTYAXIS, "S");
		}

		if(a.getRendering() != null) {
			e.addAttribute(DATA_SET_RENDER_AS, a.getRendering().name());
		}
		
		if(chart.getColorRanges() != null && !chart.getColorRanges().isEmpty()) {
			
		}

		else {
			Palette palette = ((IChartData) chart.getDatas()).getColorPalette();
			if(palette != null) {
				for(String s : palette.getKeys()) {
					if(palette.getColor(s) != null && e.attributeValue(DATASET_NAME).toLowerCase().contains(s.toLowerCase())) {
						String color = "";
						if(drilledWithColor != null && !drilledWithColor.isEmpty() && !drilledWithColor.equals("null")) {
							color = drilledWithColor;
						}
						else {
							color = palette.getColor(s);
						}
						e.addAttribute(COLOR, "#" + color);
						break;
					}
				}
			}
		}

		return e;
	}

	private List<String> addCategories(HashMap<DataAggregation, DataSerie> datas, Element root) {
		if(!(chart.getDatas() instanceof MultiSerieChartData)) {
			return Collections.EMPTY_LIST;
		}

		Element categoriesE = root.addElement(CATEGORIES);

		List<String> categories = new ArrayList<String>();

		for(DataAggregation a : datas.keySet()) {
			datas.get(a).orderPlots(new ChartAggregationer.PlotComparator(((IChartData)chart.getDatas()).getOrderType()));
			for(DataPlot p : datas.get(a).getPlots()) {
				boolean found = false;
				for(String s : categories) {
					if(s.equals(p.getLabel())) {
						found = true;
						break;
					}
				}
				if(!found) {
					categories.add(p.getLabel());
					categoriesE.addElement(CATEGORIE).addAttribute(LABEL, p.getLabel());

				}

			}
		}
		return categories;
	}

	private void addChartOptions(Element root) {
		root.addAttribute("plotGradientColor", "");
		root.addAttribute("showPlotBorder", "0");
		
		for(IComponentOptions opt : chart.getOptions()) {
			for(String key : opt.getInternationalizationKeys()) {
				if(opt.getValue(key) != null) {
					String label = null;
					label = state.getDashInstance().getDashBoard().getI18Reader().getLabel(state, chart.getId(), key);
					if(label == null || label.isEmpty()) {
						label = opt.getValue(key);
						int curr = 0;
						String currVal = new String(label);
						while(curr >= 0) {
							curr = currVal.indexOf("{$", curr);
							if(curr >= 0) {
								String varName = currVal.substring(curr + 2, currVal.indexOf("}", curr));
								String value = state.getComponentValue(varName) + "";

								try {
									ComponentRuntime component = state.getDashInstance().getDashBoard().getComponent(chart.getId());

									for(ComponentParameter p : ((IComponentDefinition) component.getElement()).getParameters()) {
										if(p.getName().equals(varName)) {
											value = state.getComponentValue(state.getDashInstance().getDashBoard().getDesignParameterProvider(p).getElement().getName());
											break;
										}
									}

									if(value == null) {
										value = "";
									}
									currVal = currVal.replace("{$" + varName + "}", value);
								} catch(Exception ex) {
									Logger.getLogger(getClass()).error(ex.getMessage(), ex);
								}

							}

						}
						label = currVal;
					}
					
					root.addAttribute(key, label);
				}

			}
			for(String key : opt.getNonInternationalizationKeys()) {
				String val = opt.getValue(key);
				if(key.equalsIgnoreCase(PRIMARY_AXIS_NAME) && !chart.getNature().isDualY()) {
					key = Y_AXIS_NAME;
				}
				root.addAttribute(key, val);
			}
			
			//little shitty trick...
			try {
				if(opt instanceof GenericOptions) {
					labelSize = ((GenericOptions)opt).getLabelSize();
				}
			} catch(Exception e) {
			}
		}
	}

	private void addDrillElement(Element set, DataPlot plot, ChartDrillInfo drillDatas, String color) throws Exception {
		StringBuffer js = new StringBuffer();

		boolean needSetParameter = true;

		if(drillDatas.getTypeTarget() == TypeTarget.Dimension) {
			// add drill down of the level is not the last

			if(state.getDrillState(chart.getName()).canDrillDown()) {
				js.append("drillDown('" + drillDatas.getChart().getName() + "','");
				if(drillDatas.isCategorieAsParameterValue()) {
					if(plot.getCategory() instanceof String) {
						js.append(((String)plot.getCategory()).replace("'", "&#39;") + "'");
					}
					else {
						js.append(plot.getCategory() + "'");
					}
				}
				else {
					js.append(plot.getValue() + "'");
				}
				js.append(");");
			}

			// add setParameter if the currentLevelIndex is the same as the levelIndex parameterProvider
			if(state.getDrillState(chart.getName()).getCurrentLevel() == drillDatas.getDrillLevelProvider()) {
				needSetParameter = true;
			}
			else {
				needSetParameter = false;
			}

		}
		
		if(needSetParameter) {
			js.append("setParameter('" + drillDatas.getChart().getName() + "','");
			String additionalParameters = "";
			if(drillDatas.isKeepColor()) {
				additionalParameters += ":COLOR:" + color;
			}
			if(drillDatas.isCategorieAsParameterValue()) {
				if(plot.getCategory() instanceof String) {
					
					js.append(((String)plot.getCategory()).replace("'", "&#39;") + additionalParameters + "'");
				}
				else {
					js.append(plot.getCategory() + additionalParameters + "'");
				}
			}
			else {
				js.append(plot.getValue() + additionalParameters + "'");
			}
			js.append(", true);");
		}
		
		switch(drillDatas.getTypeTarget()) {
		case DetailReport:
			break;
		case TargetPopup:
			js.append("popupModelPage('" + URLEncoder.encode(drillDatas.getTargetModelPage().getName(), "UTF-8") + "','" + drillDatas.getZoomWidth() + "','" + drillDatas.getZoomHeight() + "');");
			break;
		case ZoomPopup:
			js.append("zoomComponent('" + drillDatas.getChart().getId() + "', " + drillDatas.getZoomWidth() + "," + drillDatas.getZoomHeight() + ");");
			break;
	}
//		System.out.println(js.toString());
		set.addAttribute(DRILL, "JavaScript:" + js.toString());

	}
}
