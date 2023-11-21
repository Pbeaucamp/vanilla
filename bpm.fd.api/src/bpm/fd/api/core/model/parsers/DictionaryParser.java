package bpm.fd.api.core.model.parsers;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.ComponentStyle;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.components.definition.OutputParameter;
import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.ChartRenderer;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentRChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation.MeasureRendering;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.api.core.model.components.definition.comment.CommentOptions;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.datagrid.Aggregation;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption.DataGridCellType;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridData;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridDrill.DrillTarget;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridOptions;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridRenderer;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.DropDownOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.components.definition.filter.MultipleValuesOptions;
import bpm.fd.api.core.model.components.definition.filter.SliderOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.gauge.StaticGaugeDatas;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.jsp.D4COptions;
import bpm.fd.api.core.model.components.definition.jsp.FlourishOptions;
import bpm.fd.api.core.model.components.definition.jsp.JspOptions;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;
import bpm.fd.api.core.model.components.definition.maps.ComponentDataViz;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.DataVizOption;
import bpm.fd.api.core.model.components.definition.maps.GoogleMapDatas;
import bpm.fd.api.core.model.components.definition.maps.IMapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerieLine;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerieMarker;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSeriePolygon;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmMapOptions;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapOption;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.olap.FaViewOption;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.components.definition.report.ReportOptions;
import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.components.definition.slicer.SlicerData;
import bpm.fd.api.core.model.components.definition.slicer.SlicerOptions;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelComponent;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelData;
import bpm.fd.api.core.model.components.definition.styledtext.StyledTextOptions;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.components.definition.text.LabelRenderer;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.components.definition.timer.TimerOptions;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.core.model.datas.filters.ColumnFilter;
import bpm.fd.api.core.model.datas.filters.ValueFilter;
import bpm.fd.api.core.model.datas.filters.ValueFilter.Type;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class DictionaryParser {
	
	private Dictionary model;
	private List<AbstractParserException> errors = new ArrayList<AbstractParserException>();
	
	
	public Dictionary parse(Document document) throws Exception{
		parse(document.getRootElement());
		return model;
	}
	
	public Dictionary parse(InputStream stream) throws Exception{
		String xml = IOUtils.toString(stream, "UTF-8");
		Document doc = DocumentHelper.parseText(xml);
		return parse(doc);
		
	}
	
	public List<AbstractParserException> getErrors(){
		return errors;
	}
	
	private void parse(Element root) throws Exception{
		model = new Dictionary();
		
		model.setName(root.attribute("name").getValue());
		parseDataSource(root);
		parseDataSet(root);
		
		for(Element e : (List<Element>)root.elements("palette")){
			model.addPalette(parsePalette(e));
		}
		
		for(Element e : (List<Element>)root.elements("filter")){
			model.addComponent(parseFilter(e));
			
		}
		
		for(Element e : (List<Element>)root.elements("chart")){
			model.addComponent(parseChart(e));
		}
		
		for(Element e : (List<Element>)root.elements("rchart")){
			model.addComponent(parseRChart(e));
		}
		for(Element e : (List<Element>)root.elements("slicer")){
			model.addComponent(parseSlicer(e));
		}
		
		for(Element e : (List<Element>)root.elements("label")){
			model.addComponent(parseLabel(e));
		}
		for(Element e : (List<Element>)root.elements("picture")){
			model.addComponent(parsePicture(e));
		}
		for(Element e : (List<Element>)root.elements("report")){
			model.addComponent(parseReport(e));
		}
		for(Element e : (List<Element>)root.elements("kpi")){
			model.addComponent(parseKpi(e));
		}
		for(Element e : (List<Element>)root.elements("olapView")){
			model.addComponent(parseOlapView(e));
		}
		for(Element e : (List<Element>)root.elements("link")){
			model.addComponent(parseLink(e));
		}
		for(Element e : (List<Element>)root.elements("button")){
			model.addComponent(parseButton(e));
		}
		for(Element e : (List<Element>)root.elements("jsp")){
			model.addComponent(parseJsp(e));
		}
		for(Element e : (List<Element>)root.elements("d4c")){
			model.addComponent(parseD4C(e));
		}
		for(Element e : (List<Element>)root.elements("flourish")){
			model.addComponent(parseFlourish(e));
		}
		for(Element e : (List<Element>)root.elements("dataGrid")){
			model.addComponent(parseDataGrid(e));
		}
		for(Element e : (List<Element>)root.elements("dynamicLabel")){
			model.addComponent(parseDynamicLabel(e));
		}
		for(Element e : (List<Element>)root.elements("gauge")){
			model.addComponent(parseGauge(e));
		}
		for(Element e : (List<Element>)root.elements("styledTextInput")){
			model.addComponent(parseStyledTextInput(e));
		}
		for(Element e : (List<Element>)root.elements("componentTimer")){
			model.addComponent(parseTimer(e));
		}
		for(Element e : (List<Element>)root.elements("map")){
			model.addComponent(parseMap(e));
			
		}
		for(Element e : (List<Element>)root.elements("dataviz")){
			model.addComponent(parseDataviz(e));
			
		}
		
		for(Element e : (List<Element>)root.elements("comment")) {
			model.addComponent(parseComment(e));
		}
		
		for(Element e : (List<Element>)root.elements("mapwms")) {
			model.addComponent(parseWms(e));
		}
		for(Element e : (List<Element>)root.elements("maposm")) {
			model.addComponent(parseOsm(e));
		}
		for(Element e : (List<Element>)root.elements("markdown")) {
			model.addComponent(parseMarkdown(e));
		}
	}

	
	private IComponentDefinition parseDataviz(Element mepElement) {
		String name = mepElement.attributeValue("name");
		ComponentDataViz map = new ComponentDataViz(name, model);
		
		Element e = mepElement.element("comment");

		if (e != null){
			map.setComment(e.getText());
		}
		
		if (mepElement.attribute("cssClass") != null){
			map.setCssClass(mepElement.attributeValue("cssClass"));
		}
		
		try {
			e = mepElement.element("options");
			if (e != null && DataVizOption.class.getName().equals(e.attributeValue("class"))){
				DataVizOption opt = new DataVizOption();
				
				try {
					opt.setDataprepId(Integer.parseInt(e.element(DataVizOption.DATAPREP_ID).getStringValue()));
				} catch (Exception e2) {
					
				}
				
				map.setComponentOption(opt);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		EventParser.parse(map, mepElement);
		return map;
	}

	private ComponentOsmMap parseOsm(Element mapElement) {
		String name = mapElement.attributeValue("name");
		ComponentOsmMap component = new ComponentOsmMap(name, model);
		
		Element e = mapElement.element("comment");
		
		if (mapElement.attribute("cssClass") != null){
			component.setCssClass(mapElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			component.setComment(e.getText());
		}
		
		try{
			component.setWidth(Integer.parseInt(mapElement.attributeValue("width")));
		}catch(NumberFormatException ex){
			
		}
		
		try{
			component.setHeight(Integer.parseInt(mapElement.attributeValue("height")));
		}catch(NumberFormatException ex){
			
		}
		
		e = mapElement.element("options");
		if(e != null) {
			//old map options
			if (OsmMapOptions.class.getName().equals(e.attributeValue("class"))){
				OsmMapOptions opt = new OsmMapOptions();
				
				try {
					opt.setZoom(Integer.parseInt(e.element(OsmMapOptions.standardKeys[OsmMapOptions.ZOOM]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setBoundBottom(Double.parseDouble(e.element(OsmMapOptions.standardKeys[OsmMapOptions.BOUNDS_BOTTOM]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setBoundLeft(Double.parseDouble(e.element(OsmMapOptions.standardKeys[OsmMapOptions.BOUNDS_LEFT]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setBoundRight(Double.parseDouble(e.element(OsmMapOptions.standardKeys[OsmMapOptions.BOUNDS_RIGHT]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setBoundTop(Double.parseDouble(e.element(OsmMapOptions.standardKeys[OsmMapOptions.BOUNDS_TOP]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setOriginLat(Double.parseDouble(e.element(OsmMapOptions.standardKeys[OsmMapOptions.ORIGIN_LAT]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setOriginLong(Double.parseDouble(e.element(OsmMapOptions.standardKeys[OsmMapOptions.ORIGIN_LONG]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setProjection(e.element(OsmMapOptions.standardKeys[OsmMapOptions.PROJECTION]).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setShowLegend(Boolean.parseBoolean(e.element(OsmMapOptions.standardKeys[OsmMapOptions.SHOW_LEGEND]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setShowBaseLayer(Boolean.parseBoolean(e.element(OsmMapOptions.standardKeys[OsmMapOptions.SHOW_BASE_LAYER]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setLegendLayout(e.element(OsmMapOptions.standardKeys[OsmMapOptions.LEGEND_LAYOUT]).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setLegendOrientation(e.element(OsmMapOptions.standardKeys[OsmMapOptions.LEGEND_ORIENTATION]).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setNumberFormat(e.element(OsmMapOptions.standardKeys[OsmMapOptions.NUMBER_FORMAT]).getStringValue());
				} catch (Exception e2) {
					
				}
				
				component.setComponentOption(opt);
			}
			
			else if(VanillaMapOption.class.getName().equals(e.attributeValue("class"))){
				VanillaMapOption opt = new VanillaMapOption();
				
				try {
					opt.setShowLegend(Boolean.parseBoolean(e.element(OsmMapOptions.standardKeys[OsmMapOptions.SHOW_LEGEND]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setShowBaseLayer(Boolean.parseBoolean(e.element(OsmMapOptions.standardKeys[OsmMapOptions.SHOW_BASE_LAYER]).getStringValue()));
				} catch (Exception e2) {
					
				}
				try {
					opt.setLegendLayout(e.element(OsmMapOptions.standardKeys[OsmMapOptions.LEGEND_LAYOUT]).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setLegendOrientation(e.element(OsmMapOptions.standardKeys[OsmMapOptions.LEGEND_ORIENTATION]).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setNumberFormat(e.element(OsmMapOptions.standardKeys[OsmMapOptions.NUMBER_FORMAT]).getStringValue());
				} catch (Exception e2) {
					
				}
				
				component.setComponentOption(opt);
			}
		}
		
		int i = 0;
		for(Element el : (List<Element>)mapElement.elements("parameter")){
			component.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		//if the map is an old one
		e = mapElement.element("mapData");
		if(e != null) {
			OsmData data = new OsmData();
				
			String dataSetname = e.element("dataSet-ref").getText();		
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					data.setDataSet(ds);
					break;
				}
			}
			if (data.getDataSet() == null){
				errors.add(new ParserComponentException(component, "Unable to find dataSet " + dataSetname +" in dictionary"));
			}
			
			component.setData(data);
			
			try {
				data.setValueFieldIndex(Integer.parseInt(e.attributeValue("valueFieldIndex")));
			} catch(Exception ex) {
				
			}
			try {
				data.setZoneFieldIndex(Integer.parseInt(e.attributeValue("zoneFieldIndex")));
			} catch(Exception ex) {
				
			}
			try {
				data.setZoneFieldLabelIndex(Integer.parseInt(e.attributeValue("zoneFieldLabelIndex")));
			} catch(Exception ex) {
				
			}
			
			//parse series
			Element seriesElem = e.element("series");
			for(Element elem : (List<Element>)seriesElem.elements()) {
				try {
					OsmDataSerie serie = null;
					if(elem.element("type").getText().equals(OsmDataSerie.POLYGON)) {
						OsmDataSeriePolygon polygon = new OsmDataSeriePolygon(elem);
						String dataSetName = elem.element("dataSet-ref").getText();
						for(DataSet ds : model.getDatasets()){
							if (ds.getName().equals(dataSetName)){
								polygon.setDataset(ds);
								break;
							}
						}
						data.addSerie(polygon);
						serie = polygon;
					}
					else if(elem.element("type").getText().equals(OsmDataSerie.LINE)) {
						OsmDataSerieLine line = new OsmDataSerieLine(elem);
						String dataSetName = elem.element("dataSet-ref").getText();
						for(DataSet ds : model.getDatasets()){
							if (ds.getName().equals(dataSetName)){
								line.setDataset(ds);
								break;
							}
						}
						data.addSerie(line);
						serie = line;
					}
					else {
						OsmDataSerieMarker marker = new OsmDataSerieMarker(elem);
						data.addSerie(marker);
						serie = marker;
						
						try {
							String modelName = elem.element("targetModel").getText();
							((OsmDataSerieMarker)serie).setTargetPageName(modelName);
						} catch(Exception ex) {
							
						}
						try {
							((OsmDataSerieMarker)serie).setMinMarkerSize(Integer.parseInt(elem.element("minMarkerSize").getText()));
						} catch(Exception ex) {
							
						}
						try {
							((OsmDataSerieMarker)serie).setMaxMarkerSize(Integer.parseInt(elem.element("maxMarkerSize").getText()));
						} catch(Exception ex) {
							
						}
						try {
							((OsmDataSerieMarker)serie).setMarkerUrl(elem.element("markerUrl").getText());
						} catch(Exception ex) {
							
						}
					}
					
					try {
						serie.setHasParent(Boolean.parseBoolean(elem.element("hasParent").getText()));
					} catch(Exception ex) {
						
					}
					try {
						serie.setDrillable(Boolean.parseBoolean(elem.element("drillable").getText()));
					} catch(Exception ex) {
						
					}
					try {
						serie.setParentSerie(elem.element("parentName").getText());
					} catch(Exception ex) {
						
					}
					try {
						serie.setParentIdFieldIndex(Integer.parseInt(elem.element("parentIdFieldIndex").getText()));
					} catch(Exception ex) {
						
					}
					
					try {
						Element colors = elem.element("colorRanges");
						if (colors != null){
							
							try {
								String min = colors.element("minColor").getText();
								serie.setMinColor(min);
							} catch(Exception ex) {
								
							}
							try {
								String max = colors.element("maxColor").getText();
								serie.setMaxColor(max);
							} catch(Exception ex) {
								
							}
						}
					} catch (Exception e1) {
					}
					
				} catch (Exception e1) {
				}
			}
		}
		else {
			e = mapElement.element("vanillaMapData");
			
			VanillaMapData data = new VanillaMapData();
			
			String dataSetname = e.element("dataSet-ref").getText();		
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					data.setDataset(ds);
					break;
				}
			}
			if (data.getDataSet() == null){
				errors.add(new ParserComponentException(component, "Unable to find dataSet " + dataSetname +" in dictionary"));
			}
			
			component.setData(data);
			
			try {
				data.setValueFieldIndex(Integer.parseInt(e.attributeValue("valueFieldIndex")));
			} catch(Exception ex) {
				
			}
			try {
				data.setZoneFieldIndex(Integer.parseInt(e.attributeValue("zoneFieldIndex")));
			} catch(Exception ex) {
				
			}
			try {
				data.setMapId(Integer.parseInt(e.attributeValue("mapId")));
			} catch(Exception ex) {
				
			}
			
			//parse series
			Element seriesElem = e.element("series");
			for(Element elem : (List<Element>)seriesElem.elements()) {
				try {
					VanillaMapDataSerie serie = new VanillaMapDataSerie();
					data.addSerie(serie);
					serie.setType(elem.element("type").getText());
					serie.setName(elem.element("name").getText());

					try {
						String modelName = elem.element("targetModel").getText();
						((VanillaMapDataSerie)serie).setTargetPageName(modelName);
					} catch(Exception ex) {
						
					}
					
					try {
						String modelName = elem.element("useColsForColors").getText();
						((VanillaMapDataSerie)serie).setUseColsForColors(Boolean.parseBoolean(modelName));
					} catch(Exception ex) {
						
					}
					Integer datasetId = Integer.parseInt(elem.element("datasetId").getText());
					serie.setMapDatasetId(datasetId);
					
					try {
						serie.setDisplay(Boolean.parseBoolean(elem.element("display").getText()));
					} catch(Exception ex) {
						
					}
					
					try {
						Element colors = elem.element("markerColors");
						if (colors != null){
							for(Element o : (List<Element>)colors.elements("colorRange")){
								ColorRange r = new ColorRange();
								
								r.setHex(o.attributeValue("color"));
								if (o.attribute("legend") != null){
									r.setName(o.attributeValue("legend"));
								}
								if (o.attribute("minValue") != null){
									try{
										r.setMin(Integer.parseInt(o.attributeValue("minValue")));
									}catch(Exception ex){
										ex.printStackTrace();
									}
								}
								
								if (o.attribute("maxValue") != null){
									try{
										r.setMax(Integer.parseInt(o.attributeValue("maxValue")));
									}catch(Exception ex){
										ex.printStackTrace();
									}
								}
								
								serie.addMarkerColor(r);
							}
						}
					} catch (Exception e1) {
					}
					
					try {
						Element colors = elem.element("colorRanges");
						if (colors != null){
							
							try {
								String min = colors.element("minColor").getText();
								serie.setMinColor(min);
							} catch(Exception ex) {
								
							}
							try {
								String max = colors.element("maxColor").getText();
								serie.setMaxColor(max);
							} catch(Exception ex) {
								
							}
						}
					} catch (Exception e1) {
					}
					
					try {
						try {
							String min = elem.element("minMarkerSize").getText();
							serie.setMinMarkerSize(Integer.parseInt(min));
						} catch(Exception ex) {
							
						}
						try {
							String max = elem.element("maxMarkerSize").getText();
							serie.setMaxMarkerSize(Integer.parseInt(max));
						} catch(Exception ex) {
							
						}
					} catch (Exception e1) {
					}
					
				} catch (Exception e1) {
				}
			}
		}
		
		EventParser.parse(component, mapElement);
		
		return component;
	}

	private ComponentMapWMS parseWms(Element mapElement) {
		String name = mapElement.attributeValue("name");
		ComponentMapWMS component = new ComponentMapWMS(name, model);
		
		Element e = mapElement.element("comment");
		
		if (mapElement.attribute("cssClass") != null){
			component.setCssClass(mapElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			component.setComment(e.getText());
		}
		
		try{
			component.setWidth(Integer.parseInt(mapElement.attributeValue("width")));
		}catch(NumberFormatException ex){
			
		}
		
		try{
			component.setHeight(Integer.parseInt(mapElement.attributeValue("height")));
		}catch(NumberFormatException ex){
			
		}
		
		e = mapElement.element("options");
		if (MapWMSOptions.class.getName().equals(e.attributeValue("class"))){
			MapWMSOptions opt = new MapWMSOptions();
			
			try {
				opt.setBaseLayerName(e.element(MapWMSOptions.BASE_LAYER_NAME).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setBaseLayerUrl(e.element(MapWMSOptions.BASE_LAYER_URL).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setVectorLayerName(e.element(MapWMSOptions.VECTOR_LAYER_NAME).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setVectorLayerUrl(e.element(MapWMSOptions.VECTOR_LAYER_URL).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setBounds(e.element(MapWMSOptions.BOUNDS).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setTileOrigin(e.element(MapWMSOptions.TILE_ORIGIN).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setProjection(e.element(MapWMSOptions.PROJECTION).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setOpacity(e.element(MapWMSOptions.OPACITY).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setBaseLayerType(e.element(MapWMSOptions.BASE_LAYER_TYPE).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setVectorLayerType(e.element(MapWMSOptions.VECTOR_LAYER_TYPE).getStringValue());
			} catch (Exception e2) {
				
			}
			try {
				opt.setBaseVectorGeometry(e.element(MapWMSOptions.VECTOR_LAYER_GEO).getStringValue());
			} catch (Exception e2) {
				
			}
			
			component.setComponentOption(opt);
		}
		
		int i = 0;
		for(Element el : (List<Element>)mapElement.elements("parameter")){
			component.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		Element colors = mapElement.element("colorRanges");
		if (colors != null){
			for(Element o : (List<Element>)colors.elements("colorRange")){
				ColorRange r = new ColorRange();
				
				r.setHex(o.attributeValue("color"));
				if (o.attribute("legend") != null){
					r.setName(o.attributeValue("legend"));
				}
				if (o.attribute("minValue") != null){
					try{
						r.setMin(Integer.parseInt(o.attributeValue("minValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				if (o.attribute("maxValue") != null){
					try{
						r.setMax(Integer.parseInt(o.attributeValue("maxValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				component.addColorRange(r);
			}
		}
		
		
		e = mapElement.element("mapData");
		
		
		IMapDatas mapDatas = null; 
		if (e.attributeValue("type", MapDatas.class.getName()).equals(MapDatas.class.getName())){
			mapDatas = new MapDatas();
			if (e.attribute("valueFieldIndex") != null){
				((MapDatas)mapDatas).setValueFieldIndex(Integer.parseInt(e.attributeValue("valueFieldIndex")));
			}
			
			if (e.attribute("zoneIdFieldIndex") != null){
				((MapDatas)mapDatas).setZoneIdFieldIndex(Integer.parseInt(e.attributeValue("zoneIdFieldIndex")));
			}
			if (e.attribute("imgPathIndex") != null){
				((MapDatas)mapDatas).setImgPathIndex(Integer.parseInt(e.attributeValue("imgPathIndex")));
			}
			if (e.attribute("imgSizeIndex") != null){
				((MapDatas)mapDatas).setImgSizeIndex(Integer.parseInt(e.attributeValue("imgSizeIndex")));
			}
			if (e.attribute("latitudeIndex") != null){
				((MapDatas)mapDatas).setLatitudeIndex(Integer.parseInt(e.attributeValue("latitudeIndex")));
			}
			if (e.attribute("longitudeIndex") != null){
				((MapDatas)mapDatas).setLongitudeIndex(Integer.parseInt(e.attributeValue("longitudeIndex")));
			}
			if (e.attribute("commentIndex") != null){
				((MapDatas)mapDatas).setCommentIndex(Integer.parseInt(e.attributeValue("commentIndex")));
			}
		}
		
		String dataSetname = e.element("dataSet-ref").getText();
		
		
		for(DataSet ds : model.getDatasets()){
			if (ds.getName().equals(dataSetname)){
				if (mapDatas instanceof MapDatas){
					((MapDatas)mapDatas).setDataSet(ds);
				}
				
			}
		}
		if (mapDatas.getDataSet() == null){
			errors.add(new ParserComponentException(component, "Unable to find dataSet " + dataSetname +" in dictionary"));
		}
		
		component.setDatas((MapDatas) mapDatas);
		
		
		EventParser.parse(component, mapElement);
		
		return component;
	}

	private IComponentDefinition parseComment(Element commentElement) {
		String name = commentElement.attributeValue("name");
		ComponentComment comment = new ComponentComment(name, model);
		
		Element e = commentElement.element("comment");

		if (commentElement.attribute("cssClass") != null){
			comment.setCssClass(commentElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			comment.setComment(e.getText());
		}
		
		e = commentElement.element("options");
		if (CommentOptions.class.getName().equals(e.attributeValue("class"))){
			CommentOptions opt = new CommentOptions();
			try{
				opt.setAllowAddComments(Boolean.parseBoolean(e.element("allowAddComments").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				opt.setShowComments(Boolean.parseBoolean(e.element("showComments").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				opt.setLimit(Integer.parseInt(e.element("limit").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				opt.setLimitComment(Boolean.parseBoolean(e.element("limitComment").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				opt.setValidation(Boolean.parseBoolean(e.element("validation").getStringValue()));
			}catch(Exception ex){
				
			}
			comment.setComponentOption(opt);
		}
		
		int i = 0;
		for(Element el : (List<Element>)commentElement.elements("parameter")){
			comment.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		EventParser.parse(comment, commentElement);
		return comment;
	}
	
	private ComponentDataGrid parseDataGrid(Element element){
		String name = element.attributeValue("name");
		ComponentDataGrid dg = new ComponentDataGrid(name, model);
		
		Element e = element.element("comment");

		if (element.attribute("cssClass") != null){
			dg.setCssClass(element.attributeValue("cssClass"));
		}
		
		if (e != null){
			dg.setComment(e.getText());
		}
		
		try{
			e = element.element("options");
			((DataGridOptions)dg.getOptions(DataGridOptions.class)).setHeadersVisible(Boolean.parseBoolean(e.attributeValue("showHeaders")));
			((DataGridOptions)dg.getOptions(DataGridOptions.class)).setRowsCanBeAdded(Boolean.parseBoolean(e.attributeValue("rowsCanBeAdded")));
			((DataGridOptions)dg.getOptions(DataGridOptions.class)).setIncludeTotal(Boolean.parseBoolean(e.attributeValue("includeTotal")));
			

		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(dg, "no datagrid options definition in the XML for component filter " + name, ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(dg, "error on component datagrid ", ex));
		}		
		
		
		try{
			e = element.element("renderer");
			int rendererStyle = Integer.parseInt(e.attributeValue("rendererStyle"));
			
			dg.setRenderer(DataGridRenderer.getRenderer(rendererStyle));

		}catch(Exception ex){
			ex.printStackTrace();
		}
				
		
		try{
			String dataSetname = element.element("dataGridData").element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					((DataGridData)dg.getDatas()).setDataSet(ds);
					break;
				}
			}
			
			if (element.element("dataGridData").attribute("orderDatas") != null){
				try{
					((DataGridData)dg.getDatas()).setOrderType(OrderingType.valueOf(element.element("dataGridData").attributeValue("orderDatas")));
				}catch(Exception ex){
					
				}
			}
			
			if (element.element("dataGridData").element("orderField") != null){
				try{
					((DataGridData)dg.getDatas()).setOrderFieldPosition(Integer.parseInt(element.element("dataGridData").element("orderField").getStringValue()));
				}catch(Exception ex){
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		try{
			e = element.element("dataLayout");
			
			if (e.attribute("useColor") != null){
				try{
					dg.getLayout().setUseColors(Boolean.parseBoolean(e.attributeValue("useColor")));
				}catch(Exception ex){
					
				}
			}
			
			if (e.attribute("colorFieldIndex") != null){
				try{
					dg.getLayout().setColorFieldIndex(Integer.parseInt(e.attributeValue("colorFieldIndex")));
				}catch(Exception ex){
					
				}
			}
			
//			for(Element _e : (List<Element>)e.elements("layout-property")){
//				try{
//					Integer pos = Integer.parseInt(_e.attributeValue("position"));
//					DataGridCellType type = DataGridCellType.valueOf(_e.attributeValue("type"));
//					
//					dg.getLayout().setFieldType(pos, type);
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				
//			}
//			
//			for(Element _e : (List<Element>)e.elements("header-property")){
//				try{
//					Integer pos = Integer.parseInt(_e.attributeValue("position"));
//					String label = _e.attributeValue("label");
//					if ("".equals(label)){
//						dg.getLayout().setHeaderTitle(pos, null);
//					}
//					else{
//						dg.getLayout().setHeaderTitle(pos, label);
//					}
//					
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				
//			}
			
			for(Element _e : (List<Element>)e.elements("color")){
				dg.getLayout().addColor(_e.attributeValue("rgb"));
			}

			Element optionsEl = e.element("columnOptions");
			if (optionsEl != null) {
				List<DataGridColumnOption> options = new ArrayList<DataGridColumnOption>();
				for(Element el : (List<Element>)optionsEl.elements("column")){
					
					String colName = null;
					String customName = null;
					DataGridCellType type = null;
					boolean group = false;
					Aggregation aggregation = null;
					
					if (el.element("name") != null) {
						colName = el.elementText("name");
					}
					
					if (el.element("customName") != null) {
						customName = el.elementText("customName");
					}
					
					if (el.element("cellType") != null) {
						String t = el.elementText("cellType");
						type = t != null && !t.isEmpty() ? DataGridCellType.valueOf(t) : null;
					}
					
					if (el.element("group") != null) {
						group = Boolean.parseBoolean(el.elementText("group"));
					}
					
					if (el.element("aggregation") != null) {
						String agg = el.elementText("aggregation");
						aggregation = agg != null && !agg.isEmpty() ? Aggregation.valueOf(agg) : null;
					}
					options.add(new DataGridColumnOption(colName, customName, type, group, aggregation));
				}
				
				dg.getLayout().setColumnOptions(options);
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
		int i = 0;
		for(Element el : (List<Element>)element.elements("parameter")){
			dg.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		
		Element el = element.element("drill");
		if (el != null){
			if (el.element("targetPage") != null){
				dg.getDrillInfo().setModelPage(el.element("targetPage").getStringValue());
			}
			
			if (el.attribute("type") != null){
				dg.getDrillInfo().setType(DrillTarget.valueOf(el.attributeValue("type")));
			}
			
			if (el.attribute("drillFieldIndex") != null){
				try{
					dg.getDrillInfo().setDrillableFieldIndex(Integer.valueOf(el.attributeValue("drillFieldIndex")));
				}catch(Exception ex){}
			}
			
			if (el.attribute("popupWidth") != null){
				try{
					dg.getDrillInfo().setPopupWidth(Integer.parseInt(el.attributeValue("popupWidth")));
				}catch(Exception ex){
					
				}
			}
			
			if (el.attribute("popupHeight") != null){
				try{
					dg.getDrillInfo().setPopupHeight(Integer.parseInt(el.attributeValue("popupHeight")));
				}catch(Exception ex){
					
				}
			}
			
//			
		}
		
		
		EventParser.parse(dg, element);
		return dg;
	}
	
	private LabelComponent parseLabel(Element labelElement){
		String name = labelElement.attributeValue("name");
		LabelComponent label = new LabelComponent(name, model);
		
		Element e = labelElement.element("comment");

		if (labelElement.attribute("cssClass") != null){
			label.setCssClass(labelElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			label.setComment(e.getText());
		}
		
		e = labelElement.element("labelRenderer");
		if (e != null){
			try{
				label.setRenderer(LabelRenderer.values()[Integer.parseInt(e.attributeValue("rendererStyle"))]);
			}catch(Exception ex){
				
			}
		}
		
		e = labelElement.element("options");
		((LabelOptions)label.getOptions(LabelOptions.class)).setText(e.element("content").getText());
		
		int i = 0;
		for(Element el : (List<Element>)labelElement.elements("parameter")){
			label.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		EventParser.parse(label, labelElement);
		return label;
	}
	
	
	private ComponentGauge parseGauge(Element gaugeElement){
		String name = gaugeElement.attributeValue("name");
		ComponentGauge gauge = new ComponentGauge(name, model);
		
		Element e = gaugeElement.element("comment");

		
			
		
		if (gaugeElement.attribute("cssClass") != null){
			gauge.setCssClass(gaugeElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			gauge.setComment(e.getText());
		}
		
		try{
			gauge.setWidth(Integer.parseInt(gaugeElement.attributeValue("width")));
		}catch(Exception ex){
			
		}
		try{
			gauge.setHeight(Integer.parseInt(gaugeElement.attributeValue("height")));
		}catch(Exception ex){
			
		}
		e = gaugeElement.element("staticGaugeData");
		if (e != null){
			StaticGaugeDatas datas = (StaticGaugeDatas)gauge.getDatas();
			try{
				datas.setMax(Float.parseFloat(e.element("maximum").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setMaxSeuil(Float.parseFloat(e.element("maximumExpected").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setMinSeuil(Float.parseFloat(e.element("minimumExpected").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setMin(Float.parseFloat(e.element("minimum").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setTarget(Float.parseFloat(e.element("target").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setTolerancePerc(Float.parseFloat(e.element("tolerancePerc").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setValue(Float.parseFloat(e.element("value").getStringValue()));
			}catch(Exception ex){
				
			}
		}
		
		
		e = gaugeElement.element("fmGaugeData");
		if (e != null){
			ComplexGaugeDatas datas = new ComplexGaugeDatas();
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					datas.setDataSet(ds);
					break;
				}
			}
			
			try{
				datas.setIndexMax(Integer.parseInt(e.element("indexMax").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setIndexMaxSeuil(Integer.parseInt(e.element("indexMaxSeuil").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setIndexMinSeuil(Integer.parseInt(e.element("indexMinSeuil").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setIndexMin(Integer.parseInt(e.element("indexMin").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setTargetValue(Float.parseFloat(e.element("targetValue").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setIndexTolerance(Integer.parseInt(e.element("indexTolerance").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setIndexValue(Integer.parseInt(e.element("indexValue").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setMaxValue(Float.parseFloat(e.element("maxValue").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				datas.setMinValue(Float.parseFloat(e.element("minValue").getStringValue()));
			}catch(Exception ex){
				
			}
			
			try{
				datas.setManualRange(Boolean.parseBoolean(e.attributeValue("rangeManual")));
			}catch(Exception ex){
				datas.setManualRange(false);
			}
			
			try{
				datas.setUseTarget(Boolean.parseBoolean(e.attributeValue("useTarget")));
			}catch(Exception ex){
				datas.setUseTarget(false);
			}
			try{
				datas.setUseExpected(Boolean.parseBoolean(e.attributeValue("useExpectedFields")));
			}catch(Exception ex){
				datas.setUseExpected(false);
			}
			
			try{
				datas.setUseFieldForTarget(Boolean.parseBoolean(e.attributeValue("useFieldForTarget")));
			}catch(Exception ex){
				datas.setUseFieldForTarget(false);
			}
			
			try{
				datas.setTargetIndex(Integer.valueOf(e.element("targetIndex").getStringValue()));
			}catch(Exception ex){
				datas.setTargetIndex(-1);
			}
			gauge.setComponentDatas(datas);
		}
		
		
		/*
		 * parseGauge
		 */
		try{
			e = gaugeElement.element("gaugeOptions");
			
			if (e != null){

					IComponentOptions opt = FusionChartOptionParser.parseOption(e);
					gauge.setComponentOption(opt);
					for(Exception ex : FusionChartOptionParser.getExceptions()){
						errors.add(new ParserComponentException(gauge, "Error when parsing GaugeOptions", ex));
					}
					

			}
			
		}catch(Exception ex){
			errors.add(new ParserComponentException(gauge, "error for component chart ", ex));
		}
		

		try{
			e = gaugeElement.element("options");
			
			if (e != null){
				for(Element eOpt : (List<Element>)e.elements()){
					IComponentOptions opt = FusionChartOptionParser.parseOption(eOpt);
					gauge.setComponentOption(opt);
					for(Exception ex : FusionChartOptionParser.getExceptions()){
						errors.add(new ParserComponentException(gauge, "Error when parsing ChartOptions", ex));
					}
					
				}
			}
			
		}catch(Exception ex){
			errors.add(new ParserComponentException(gauge, "error for component chart ", ex));
		}
		
		int i = 0;
		for(Element el : (List<Element>)gaugeElement.elements("parameter")){
			gauge.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		EventParser.parse(gauge, gaugeElement);
		return gauge;
	}
	
	
	private ComponentButtonDefinition parseButton(Element buttonElement){
		String name = buttonElement.attributeValue("name");
		ComponentButtonDefinition button = new ComponentButtonDefinition(name, model);
		
		Element e = buttonElement.element("comment");

		if (buttonElement.attribute("cssClass") != null){
			button.setCssClass(buttonElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			button.setComment(e.getText());
		}
		
		e = buttonElement.element("buttonOptions");
		if (e != null && e.element("label") != null){
			((ButtonOptions)button.getOptions(ButtonOptions.class)).setLabel(e.element("label").getText());
		}
		
		
		
		EventParser.parse(button, buttonElement);
		return button;
	}
	
	
	private ComponentLink parseLink(Element linkElement){
		String name = linkElement.attributeValue("name");
		ComponentLink link = new ComponentLink (name, model);
		
		Element e = linkElement.element("comment");

		if (linkElement.attribute("cssClass") != null){
			link.setCssClass(linkElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			link.setComment(e.getText());
		}
		
		e = linkElement.element("options");
		((LinkOptions)link.getOptions(LinkOptions.class)).setUrl(e.element("url").getText());
		((LinkOptions)link.getOptions(LinkOptions.class)).setLabel(e.element("label").getText());
		
		int i = 0;
		for(Element el : (List<Element>)linkElement.elements("parameter")){
			link.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		EventParser.parse(link, linkElement);
		return link;
	}
	
	private ComponentKpi parseKpi(Element reportElement) throws Exception{
		String name = reportElement.attributeValue("name");
		ComponentKpi report = new ComponentKpi(name, model);
		
		Element e = reportElement.element("comment");

		if (reportElement.attribute("cssClass") != null){
			report.setCssClass(reportElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			report.setComment(e.getText());
		}
		
		e = reportElement.element("directoryItemId");
		if (e != null){
			try{
				report.setDirectoryItemId(Integer.parseInt(e.getText()));
			}catch(NumberFormatException ex){
				errors.add(new ParserComponentException(report, "No directoryItemId found"));
			}
			
		}
		int i = 0;
		List<ComponentParameter> l = new ArrayList<ComponentParameter>();
		for(Element el : (List<Element>)reportElement.elements("parameter")){
			l.add( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		report.defineParameter(l);
		
		
		e = reportElement.element("reportOptions");
		if (e != null){
			try{
				((ReportOptions)report.getOptions(ReportOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			}catch(Exception ex){
				
			}
			try{
				((ReportOptions)report.getOptions(ReportOptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			}catch(Exception ex){
				
			}
			
		}
		EventParser.parse(report, reportElement);
		return report;
	}
	
	private ComponentReport parseReport(Element reportElement) throws Exception{
		String name = reportElement.attributeValue("name");
		ComponentReport report = new ComponentReport(name, model);
		
		Element e = reportElement.element("comment");

		if (reportElement.attribute("cssClass") != null){
			report.setCssClass(reportElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			report.setComment(e.getText());
		}
		
		e = reportElement.element("directoryItemId");
		if (e != null){
			try{
				report.setDirectoryItemId(Integer.parseInt(e.getText()));
			}catch(NumberFormatException ex){
				errors.add(new ParserComponentException(report, "No directoryItemId found"));
			}
			
		}
		int i = 0;
		List<ComponentParameter> l = new ArrayList<ComponentParameter>();
		for(Element el : (List<Element>)reportElement.elements("parameter")){
			l.add( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		report.defineParameter(l);
		
		
		e = reportElement.element("reportOptions");
		if (e != null){
			try{
				((ReportOptions)report.getOptions(ReportOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			}catch(Exception ex){
				
			}
			try{
				((ReportOptions)report.getOptions(ReportOptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			}catch(Exception ex){
				
			}
			
		}
		EventParser.parse(report, reportElement);
		return report;
	}
	
	private ComponentFaView parseOlapView(Element viewElement) throws Exception{
		String name = viewElement.attributeValue("name");
		ComponentFaView view = new ComponentFaView(name, model);
		
		Element e = viewElement.element("comment");

		if (viewElement.attribute("cssClass") != null){
			view.setCssClass(viewElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			view.setComment(e.getText());
		}
		
		e = viewElement.element("directoryItemId");
		if (e != null){
			try{
				view.setDirectoryItemId(Integer.parseInt(e.getText()));
			}catch(NumberFormatException ex){
				errors.add(new ParserComponentException(view, "No directoryItemId found"));
			}
			
		}
		int i = 0;
		List<ComponentParameter> l = new ArrayList<ComponentParameter>();
		for(Element el : (List<Element>)viewElement.elements("parameter")){
			l.add( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		view.defineParameter(l);
		
		
		e = viewElement.element("reportOptions");
		if (e != null){
			try{
				((ReportOptions)view.getOptions(ReportOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			}catch(Exception ex){
				
			}
			try{
				((ReportOptions)view.getOptions(ReportOptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			}catch(Exception ex){
				
			}
			
		}
		EventParser.parse(view, viewElement);
		
		e = viewElement.element("faViewOption");
		if (e != null){
			try{
				((FaViewOption)view.getOptions(FaViewOption.class)).setInteraction(Boolean.parseBoolean(e.attributeValue("isInteractive")));
			}catch(Exception ex){
				
			}
			try{
				((FaViewOption)view.getOptions(FaViewOption.class)).setShowDimensions(Boolean.parseBoolean(e.attributeValue("showDimensions")));
			}catch(Exception ex){
				
			}
			try{
				((FaViewOption)view.getOptions(FaViewOption.class)).setShowCubeFunctions(Boolean.parseBoolean(e.attributeValue("showCubeFunctions")));
			}catch(Exception ex){
				
			}		
			try{
				((FaViewOption)view.getOptions(FaViewOption.class)).setElementsFromString((e.attributeValue("listDimension")));
			}catch(Exception ex){
				
			}	
		}
		EventParser.parse(view, viewElement);
		return view;
	}
	
	
	private ComponentJsp parseJsp(Element jspElement) throws Exception{
		String name = jspElement.attributeValue("name");
		ComponentJsp jsp = new ComponentJsp(name, model);
		
		Element e = jspElement.element("comment");

		if (jspElement.attribute("cssClass") != null){
			jsp.setCssClass(jspElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			jsp.setComment(e.getText());
		}
		
		e = jspElement.element("jspUrl");
		if (e != null){
			
			jsp.setJspUrl(e.getText());
			
			
		}
		int i = 0;
		
		for(Element el : (List<Element>)jspElement.elements("parameter")){
			jsp.addComponentParameter( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		
		
		
		
		e = jspElement.element("jspOptions");
		if (e != null){
			try{
				((JspOptions)jsp.getOptions(JspOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			}catch(Exception ex){
				
			}
			try{
				((JspOptions)jsp.getOptions(JspOptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			}catch(Exception ex){
				
			}
			try{
				((JspOptions)jsp.getOptions(JspOptions.class)).setBorder_width(Integer.parseInt(e.attributeValue("border_width")));
			}catch(Exception ex){
				
			}
			
		}
		
		EventParser.parse(jsp, jspElement);
		return jsp;
	}
	
	
	private ComponentD4C parseD4C(Element d4cElement) throws Exception{
		String name = d4cElement.attributeValue("name");
		ComponentD4C d4c = new ComponentD4C(name, model);
		
		Element e = d4cElement.element("comment");

		if (d4cElement.attribute("cssClass") != null){
			d4c.setCssClass(d4cElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			d4c.setComment(e.getText());
		}
		
		e = d4cElement.element("d4cUrl");
		if (e != null){
			
			d4c.setUrl(e.getText());
			
			
		}
		int i = 0;
		
		for(Element el : (List<Element>)d4cElement.elements("parameter")){
			d4c.addComponentParameter( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		
		
		
		
		e = d4cElement.element("d4cOptions");
		if (e != null){
			try{
				((D4COptions)d4c.getOptions(D4COptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			}catch(Exception ex){
				
			}
			try{
				((D4COptions)d4c.getOptions(D4COptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			}catch(Exception ex){
				
			}
			try{
				((D4COptions)d4c.getOptions(D4COptions.class)).setBorder_width(Integer.parseInt(e.attributeValue("border_width")));
			}catch(Exception ex){
				
			}
			
		}
		
		EventParser.parse(d4c, d4cElement);
		return d4c;
	}
	
	
	private ComponentFlourish parseFlourish(Element flourishElement) throws Exception{
		String name = flourishElement.attributeValue("name");
		ComponentFlourish flourish = new ComponentFlourish(name, model);
		
		Element e = flourishElement.element("comment");

		if (flourishElement.attribute("cssClass") != null){
			flourish.setCssClass(flourishElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			flourish.setComment(e.getText());
		}
		
		e = flourishElement.element("flourishId");
		if (e != null){
			flourish.setUrl(e.getText());
		}
		int i = 0;
		
		for(Element el : (List<Element>)flourishElement.elements("parameter")){
			flourish.addComponentParameter( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		e = flourishElement.element("flourishOptions");
		if (e != null){
			try{
				((FlourishOptions)flourish.getOptions(FlourishOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			}catch(Exception ex){
				
			}
			try{
				((FlourishOptions)flourish.getOptions(FlourishOptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			}catch(Exception ex){
				
			}
			try{
				((FlourishOptions)flourish.getOptions(FlourishOptions.class)).setBorder_width(Integer.parseInt(e.attributeValue("border_width")));
			}catch(Exception ex){
				
			}
			
		}
		
		EventParser.parse(flourish, flourishElement);
		return flourish;
	}
	
	private ComponentPicture parsePicture(Element labelElement){
		String name = labelElement.attributeValue("name");
		ComponentPicture label = new ComponentPicture(name, model);
		
		Element e = labelElement.element("comment");

		if (labelElement.attribute("cssClass") != null){
			label.setCssClass(labelElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			label.setComment(e.getText());
		}
		
		
		e = labelElement.element("pictureUrl");
		if (e != null){
			label.setPictureUrl(e.getText());
		}
//		((LabelOptions)label.getOptions(LabelOptions.class)).setText(e.element("content").getText());
		
		EventParser.parse(label, labelElement);
		return label;
	}
	
	
	
	private ComponentFilterDefinition parseFilter(Element filterElement){
		String name = filterElement.attributeValue("name");
		ComponentFilterDefinition filter = new ComponentFilterDefinition(name, model);
		
		Element e = filterElement.element("comment");

		if (e != null){
			filter.setComment(e.getText());
		}
		
		if (filterElement.attribute("cssClass") != null){
			filter.setCssClass(filterElement.attributeValue("cssClass"));
		}
		
		if (filterElement.element("label") != null){
			filter.setLabel(filterElement.element("label").getText());
		}
		try{
			e = filterElement.element("options");
			((FilterOptions)filter.getOptions(FilterOptions.class)).setSubmitOnChange(Boolean.parseBoolean(e.attributeValue("submitOnChange")));
			((FilterOptions)filter.getOptions(FilterOptions.class)).setSelectFirstValue(Boolean.parseBoolean(e.attributeValue("selectFirstValue")));
			((FilterOptions)filter.getOptions(FilterOptions.class)).setRequired(Boolean.parseBoolean(e.attributeValue("required")));
			if (e.attribute("defaultValue") != null){
				((FilterOptions)filter.getOptions().get(0)).setDefaultValue(e.attributeValue("defaultValue"));
			}
			if (e.attribute("hidden") != null){
				((FilterOptions)filter.getOptions().get(0)).setHidden(Boolean.parseBoolean(e.attributeValue("hidden")));
			}
			

		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(filter, "no filter options definition in the XML for component filter " + name, ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(filter, "error on component filter ", ex));
		}
		
		
		
		
		try{
			e = filterElement.element("filterRenderer");
			int rendererStyle = Integer.parseInt(e.attributeValue("rendererStyle"));
			
			FilterRenderer renderer = FilterRenderer.getRenderer(rendererStyle);
			filter.setRenderer(renderer);
			
		}catch(NumberFormatException ex){
			errors.add(new ParserComponentException(filter, "rendererStyle attribute is not a number for component chart", ex));
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(filter, "no renderer definition in the XML for component chart ", ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(filter, "cannot create a FilterRenderer for component chart ", ex));
		}
			
		e = filterElement.element("valueJavaScript");
		if (e != null){
			filter.setValueJavaScript(e.getStringValue());
		}
		
		try{
			e = filterElement.element("dropDownOptions");
			((DropDownOptions)filter.getOptions(DropDownOptions.class)).setSize(Integer.parseInt(e.attributeValue("size")));

		}catch(NullPointerException ex){
		}catch(Exception ex){
		
		}
		
		try{
			e = filterElement.element("multipleValuesOptions");
			((MultipleValuesOptions)filter.getOptions(MultipleValuesOptions.class)).setMultipleValues(Boolean.parseBoolean(e.attributeValue("multipleValues")));

		}catch(NullPointerException ex){
		}catch(Exception ex){
		
		}
		
		try{
			e = filterElement.element("menuOptions");
			((MenuOptions)filter.getOptions(MenuOptions.class)).setSize(Integer.parseInt(e.attributeValue("size")));
			((MenuOptions)filter.getOptions(MenuOptions.class)).setIsVertical(Boolean.parseBoolean(e.attributeValue("isHorizontal")));
			((MenuOptions)filter.getOptions(MenuOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));

		}catch(NullPointerException ex){
		}catch(Exception ex){
		
		}
		
		try{
			e = filterElement.element("sliderOptions");
			((SliderOptions)filter.getOptions(SliderOptions.class)).setBarColor(e.attributeValue("barColor"));
			((SliderOptions)filter.getOptions(SliderOptions.class)).setDelay(Integer.parseInt(e.attributeValue("delay")));
			((SliderOptions)filter.getOptions(SliderOptions.class)).setHeight(Integer.parseInt(e.attributeValue("height")));
			((SliderOptions)filter.getOptions(SliderOptions.class)).setSliderColor(e.attributeValue("sliderColor"));
			((SliderOptions)filter.getOptions(SliderOptions.class)).setWidth(Integer.parseInt(e.attributeValue("width")));
			((SliderOptions)filter.getOptions(SliderOptions.class)).setAutoRun(Boolean.parseBoolean(e.attributeValue("autoRun")));

		}catch(NullPointerException ex){
		}catch(Exception ex){
		
		}
		
		
		try{
			e = filterElement.element("filterData");
			FilterData filterData = new FilterData();
			filterData.setColumnLabelIndex(Integer.parseInt(e.attributeValue("columnLabelIndex")));
			filterData.setColumnValueIndex(Integer.parseInt(e.attributeValue("columnValueIndex")));
			
			if (e.attribute("columnOrderIndex") != null){
				filterData.setColumnOrderIndex(Integer.parseInt(e.attributeValue("columnOrderIndex")));
			}
			try {
				if (e.attribute("orderType") != null){
					filterData.setOrderType(OrderingType.valueOf(e.attributeValue("orderType")));
				}
			} catch(Exception e1) {
			}
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					filterData.setDataSet(ds);
				}
			}
			if (filterData.getDataSet() == null){
				errors.add(new ParserComponentException(filter, "Unable to find dataSet " + dataSetname +" in dictionary"));
			}
			
			filter.setComponentDatas(filterData);
			
			
		}catch(NullPointerException ex){
			if (filter.getRenderer().getRendererStyle() != FilterRenderer.TEXT_FIELD &&
				filter.getRenderer().getRendererStyle() != FilterRenderer.DATE_PIKER &&
				filter.getRenderer().getRendererStyle() != FilterRenderer.DYNAMIC_TEXT ){
				errors.add(new ParserComponentException(filter, "no filterData definition in the XML for component filter " + name));
			}
			
		}catch(Exception ex){
			if (filter.getRenderer().getRendererStyle() != FilterRenderer.TEXT_FIELD&&
				filter.getRenderer().getRendererStyle() != FilterRenderer.DATE_PIKER ){
				errors.add(new ParserComponentException(filter, "error for component filter " + name));
			}
			
		}
		
		
//		int i = filter.getParameters().size();
//		for(Element el : (List<Element>)filterElement.elements("parameter")){
//			filter.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
//		}
		EventParser.parse(filter, filterElement);
		return filter;
	}
	
	private DynamicLabelComponent parseDynamicLabel(Element filterElement){
		String name = filterElement.attributeValue("name");
		DynamicLabelComponent label = new DynamicLabelComponent(name, model);
		
		Element e = filterElement.element("comment");

		
		if (filterElement.element("label") != null){
			label.setLabel(filterElement.element("label").getText());
		}
		if (e != null){
			label.setComment(e.getText());
		}
		
		if (filterElement.attribute("cssClass") != null){
			label.setCssClass(filterElement.attributeValue("cssClass"));
		}
		
		try{
			e = filterElement.element("dynamicLabelData");
			DynamicLabelData labelData = new DynamicLabelData();
			labelData.setColumnValueIndex(Integer.parseInt(e.attributeValue("columnValueIndex")));

			String dataSetname = e.element("dataSet-ref").getText();
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					labelData.setDataSet(ds);
				}
			}
			if (labelData.getDataSet() == null){
				errors.add(new ParserComponentException(label, "Unable to find dataSet " + dataSetname +" in dictionary"));
			}
			
			label.setComponentDatas(labelData);
			
			
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(label, "no filterData definition in the XML for component dynamic label " + name));	
		}catch(Exception ex){
				errors.add(new ParserComponentException(label, "error for component dynamic label " + name));
		}
		
		
//		int i = filter.getParameters().size();
//		for(Element el : (List<Element>)filterElement.elements("parameter")){
//			filter.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
//		}
		
		
		int i = 0;
		for(Element el : (List<Element>)filterElement.elements("parameter")){
			label.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		EventParser.parse(label, filterElement);
		return label;
	}
	
	
	private void parseDataSource(Element root){
		for(Element e : (List<Element>)root.elements("dataSource")){
			String name = e.element("name").getText();
			String odaExtensionDataSourceId = e.element("odaExtensionDataSourceId").getText();
			String odaExtensionId = e.element("odaExtensionId").getText();
			
			
			Properties publicP = new Properties();
			
			for(Element _p : (List<Element>)e.elements("publicProperty")){
				String aName = _p.attribute("name").getStringValue();
				String aValue = _p.getText();
				publicP.setProperty(aName, aValue);
			}
			
			Properties privateP = new Properties();
			
			for(Element _p : (List<Element>)e.elements("privateProperty")){
				String aName = _p.attribute("name").getStringValue();
				String aValue = _p.getText();
				privateP.setProperty(aName, aValue);
			}
			
			DataSource dataSource = new DataSource(model, name, odaExtensionDataSourceId, odaExtensionId, publicP, privateP);
			try{
				String odaDriverClassName = e.element("odaDriverClassName").getText();
				dataSource.setOdaDriverClassName(odaDriverClassName);
			}catch(Exception ex){
//				ex.printStackTrace();
			}
			
			try {
				model.addDataSource(dataSource);
			} catch (DictionaryException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	private void parseDataSet(Element root){
		for(Element e : (List<Element>)root.elements("dataSet")){
			String name = e.element("name").getText();
			String dataSourceName = e.element("dataSourceName").getText();
			String odaExtensionDataSourceId = e.element("odaExtensionDataSourceId").getText();
			String odaExtensionDataSetId = e.element("odaExtensionDataSetId").getText();
			String queryText = e.element("queryText").getText();
			
			Properties publicP = new Properties();
			
			for(Element _p : (List<Element>)e.elements("publicProperty")){
				String aName = _p.attribute("name").getStringValue();
				String aValue = _p.getText();
				publicP.setProperty(aName, aValue);
			}
			
			Properties privateP = new Properties();
			
			for(Element _p : (List<Element>)e.elements("privateProperty")){
				String aName = _p.attribute("name").getStringValue();
				String aValue = _p.getText();
				privateP.setProperty(aName, aValue);
			}
			
			for(DataSource ds :  model.getDatasources()){
				if (ds.getName().equals(dataSourceName)){
					DataSet dataSet = new DataSet(name, odaExtensionDataSetId, odaExtensionDataSourceId, publicP, privateP, queryText, ds);
					
					try{
						model.addDataSet(dataSet);
						dataSet.buildDescriptor(ds);
					}catch(Exception ex){
						errors.add(new ParserDataSetException(dataSet, "Error when building dataSet descriptor for " + dataSet.getName(), ex));
					}	
					//parameters labels
					for(Element _p : (List<Element>)e.element("parameters").elements("parameter")){
						String pname = _p.attributeValue("name");
						int pos= Integer.parseInt(_p.attributeValue("position"));
						String plabel = _p.attributeValue("label");
						
						
						boolean found = false;
						for(ParameterDescriptor p : dataSet.getDataSetDescriptor().getParametersDescriptors()){
							if (p.getName().equals(pname) && p.getPosition() == pos){
								p.setLabel(plabel);
								found = true;
							}
						}
						if (!found){
							for(ParameterDescriptor p : dataSet.getDataSetDescriptor().getRealParameters()){
								if(p.getName().equals(pname)) {
									p.setLabel(plabel);
									p.setPosition(pos);
									found = true;
									break;
								}
							}
							if (!found){
								dataSet.getDataSetDescriptor().addDummyParameter(pname, pos, plabel);
							}
						}
						
					}
						
						
						break;
//					}catch(Exception ex){
//						errors.add(new ParserDataSetException(dataSet, "Error when building dataSet descriptor for " + dataSet.getName(), ex));
//					}
					
					
				}
			}
			
			
			
			
			
		}
	}
	
	private Palette parsePalette(Element e){
		Palette p = new Palette();
		p.setName(e.attributeValue("name"));
		for(Element el : (List<Element>)e.elements("color")){
			String key = el.attributeValue("key");
			String color = el.attributeValue("value");
			if (key != null && color != null){
				p.addColor(key, color, false);
			}
			
		}
		return p;
	}
	
	private ComponentSlicer parseSlicer(Element chartElement){
		String name = chartElement.attributeValue("name");
		ComponentSlicer chart = new ComponentSlicer(name, model);
		
		
		if (chartElement.attribute("cssClass") != null){
			chart.setCssClass(chartElement.attributeValue("cssClass"));
		}
		
		Element e = chartElement.element("comment");
		if (e != null){
			chart.setComment(e.getText());
		}

		try{
			e = chartElement.element("slicerData");
			
			SlicerData chartData = (SlicerData)chart.getDatas();

			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					chartData.setDataSet(ds);
				}
			}
			/*
			 * dimension settings
			 */
			for(Element _e : (List<Element>)e.elements("level")){
				try{
					chartData.setLevelIndex(
							Integer.valueOf(_e.attributeValue("index")), 
							Integer.valueOf(_e.attributeValue("categoryDataSetIndex")));
					chartData.getLevelCategoriesIndex().get(Integer.valueOf(_e.attributeValue("index"))).setLabel(_e.attributeValue("levelLabel"));
				}catch(Exception ex){
					
				}
				
			}

		}catch(NullPointerException ex){
		}catch(Exception ex){
		}
		
		//Options
		try{
			e = chartElement.element("options");
			
			
			e = e.element("slicerOptions");
			SlicerOptions opts = (SlicerOptions)chart.getOptions(SlicerOptions.class);
			opts.setLevelLinked(Boolean.valueOf(e.attributeValue("levelLinked")));
			opts.setSubmitOnCheck(Boolean.valueOf(e.attributeValue("submitOnCheck")));
			
			//colors
			int r = Integer.parseInt(e.element("defaultColor").attributeValue("red"));
			int g = Integer.parseInt(e.element("defaultColor").attributeValue("green"));
			int b = Integer.parseInt(e.element("defaultColor").attributeValue("blue"));
			opts.setDefaultColor( new Color(r, g, b));
			r = Integer.parseInt(e.element("activeColor").attributeValue("red"));
			g = Integer.parseInt(e.element("activeColor").attributeValue("green"));
			b = Integer.parseInt(e.element("activeColor").attributeValue("blue"));
			opts.setActiveColor( new Color(r, g, b));

		}catch(Exception ex){
			
		}
		
		
		EventParser.parse(chart, chartElement);
		return chart;
		
	}
	
	private ComponentChartDefinition parseChart(Element chartElement){
		String name = chartElement.attributeValue("name");
		ComponentChartDefinition chart = new ComponentChartDefinition(name, model);
		
		
		if (chartElement.attribute("cssClass") != null){
			chart.setCssClass(chartElement.attributeValue("cssClass"));
		}
		
		
		
		
		Element e = chartElement.element("comment");
		if (e != null){
			chart.setComment(e.getText());
		}
		try{
			chart.setWidth(Integer.parseInt(chartElement.attributeValue("width")));
		}catch(NumberFormatException ex){
			
		}
		
		try{
			chart.setHeight(Integer.parseInt(chartElement.attributeValue("height")));
		}catch(NumberFormatException ex){
			
		}
		
		Element colors = chartElement.element("colorRanges");
		if (colors != null){
			for(Element o : (List<Element>)colors.elements("colorRange")){
				ColorRange r = new ColorRange();
				
				r.setHex(o.attributeValue("color"));
				if (o.attribute("legend") != null){
					r.setName(o.attributeValue("legend"));
				}
				if (o.attribute("minValue") != null){
					try{
						r.setMin(Integer.parseInt(o.attributeValue("minValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				if (o.attribute("maxValue") != null){
					try{
						r.setMax(Integer.parseInt(o.attributeValue("maxValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				chart.addColorRange(r);
			}
		}
		
		
		try{
			e = chartElement.element("drillInfo");
			chart.getDrillDatas().setDrillable(Boolean.parseBoolean(e.attributeValue("active")));
			chart.getDrillDatas().setCategorieAsParameterValue(Boolean.parseBoolean(e.attributeValue("categoryAsValue")));
			
			if (e.attribute("runDrillReport") != null && Boolean.parseBoolean(e.attributeValue("runDrillReport")) == Boolean.TRUE){
//				chart.getDrillDatas().setRunDetailReport(Boolean.parseBoolean(e.attributeValue("runDrillReport")));
				chart.getDrillDatas().setTypeTarget(TypeTarget.DetailReport);
			}
			if (e.attribute("drillLevelProvider") != null ){
				try{
					chart.getDrillDatas().setDrillLevelProvider(Integer.parseInt(e.attributeValue("drillLevelProvider") ));
				}catch(Exception ex){
					
				}
			}
			
			if (e.element("url") != null){
				chart.getDrillDatas().setUrl(e.element("url").getStringValue());
			}
			if (e.attribute("targetModel") != null){
				chart.getDrillDatas().setUrl(e.attributeValue("targetModel"));
			}
			if (e.attribute("drillType") != null){
				chart.getDrillDatas().setTypeTarget(TypeTarget.valueOf(e.attributeValue("drillType")));
			}
			
			if (e.attribute("keepColor") != null){
				chart.getDrillDatas().setKeepColor(Boolean.parseBoolean(e.attributeValue("keepColor")));
			}
			
			if (e.element("zoomPopup") != null && Boolean.parseBoolean(e.element("zoomPopup").getText()) == Boolean.TRUE){
				chart.getDrillDatas().setTypeTarget(TypeTarget.ZoomPopup);
//				chart.getDrillDatas().setZoomPopup(Boolean.parseBoolean(e.element("zoomPopup").getStringValue()));
			}
			if (e.element("zoomWidth") != null){
				chart.getDrillDatas().setZoomWidth(Integer.parseInt(e.element("zoomWidth").getStringValue()));
			}
			if (e.element("zoomHeight") != null){
				chart.getDrillDatas().setZoomHeight(Integer.parseInt(e.element("zoomHeight").getStringValue()));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		try{
			e = chartElement.element("chartRenderer");
			int rendererStyle = Integer.parseInt(e.attributeValue("rendererStyle"));
			
			ChartRenderer renderer = ChartRenderer.getRenderer(rendererStyle);
			chart.setRenderer(renderer);
			
		}catch(NumberFormatException ex){
			errors.add(new ParserComponentException(chart, "rendererStyle attribute is not a number", ex));
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(chart, "no renderer definition in the XML", ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "cannot create a FilterRenderer", ex));
		}
		
		try{
			e = chartElement.element("chartNature");
			int natureStyle = Integer.parseInt(e.attributeValue("style"));
			
			ChartNature nature = ChartNature.getNature(natureStyle);
			chart.setNature(nature);
			
		}catch(NumberFormatException ex){
			errors.add(new ParserComponentException(chart, "nature attribute is not a number for component chart ", ex));
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(chart, "no nature definition in the XML for component chart ", ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "cannot create a FilterRenderer for component chart ", ex));
		}
			
		try{
			e = chartElement.element("chartData");
			
			ChartData chartData = new ChartData();
			
			if (e.attribute("colorPalette") != null){
				chartData.setColorPalette(model.getPalette(e.attributeValue("colorPalette")));
			}
			
			
			if (e.attribute("valueField") != null){
				chartData.setValueFieldIndex(Integer.parseInt(e.attributeValue("valueField")));
			}
			if (e.attribute("groupField") != null){
				chartData.setGroupFieldIndex(Integer.parseInt(e.attributeValue("groupField")));
			}
			if (e.attribute("groupLabelField") != null){
				chartData.setCategorieFieldLabelIndex(Integer.parseInt(e.attributeValue("groupLabelField")));
			}
			if (e.attribute("orderType") != null){
				chartData.setOrderType(ChartOrderingType.valueOf(e.attributeValue("orderType")));
			}
			
			chartData.setAggregator(Integer.parseInt(e.attributeValue("aggregator")));
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					chartData.setDataSet(ds);
				}
			}
			
			for(Element _e : (List<Element>)e.elements("dataAggregation")){
				try{
					DataAggregation agg = new DataAggregation();
					if (_e.attribute("applyOnDistinctSerie") != null){
						agg.setApplyOnDistinctSerie(Boolean.parseBoolean(_e.attributeValue("applyOnDistinctSerie")));
					}
					
					if (_e.attribute("orderType") != null){
						agg.setOrderType(ChartOrderingType.valueOf(_e.attributeValue("orderType")));
					}
					
					agg.setAggregator(Integer.parseInt(_e.attributeValue("aggregator")));
					agg.setValueFieldIndex(Integer.parseInt(_e.attributeValue("valueField")));
					if (_e.attribute("measureName") != null){
						agg.setMeasureName(_e.attributeValue("measureName"));
					}

					if (_e.element("paletteColors") != null){
						int k = 0;
						for(Element _c : (List<Element>)_e.element("paletteColors").elements("color")){
							agg.setColorCode(_c.getStringValue(), k++);
						}
						List<String> l = new ArrayList<String>();
						for(int ii = k; ii < agg.getColorsCode().size(); ii++){
							l.add(agg.getColorsCode().get(ii));
						}
						agg.getColorsCode().removeAll(l);
					}
					int i = 0;
					for(String s : agg.getColorsCode()){
						chartData.getAggregation().get(0).setColorCode(s, i++);	
					}
					
//					e.addAttribute("primaryAxis", isPrimaryAxis() + "");
//					e.addAttribute("rendering", getRendering().name());
					if (_e.attribute("primaryAxis") != null){
						
						agg.setPrimaryAxis(Boolean.valueOf(_e.attributeValue("primaryAxis")));
					}
					if (_e.attribute("rendering") != null){
						try{
							agg.setRendering(MeasureRendering.valueOf(_e.attributeValue("rendering") ));
						}catch(Exception ex){
							
						}
					}
					
					/*
					 * valueFilters
					 */
					for(Element _eF : (List<Element>)_e.elements("valueFilter")){
						ValueFilter f = new ValueFilter();
						try{
							f.setValue(Double.valueOf(_eF.attributeValue("value")));
							
						}catch(Exception ex){
							continue;
						}
						try{
							f.setType(Type.valueOf(_eF.attributeValue("type")));
						}catch(Exception ex){
							continue;
						}
						chartData.getAggregation().get(0).addFilter(f);
						
					}
					
					for(Element _eF : (List<Element>)_e.elements("columnFilter")){
						ColumnFilter f = new ColumnFilter();
						try{
							f.setValue(_eF.element("value").getText());
							
						}catch(Exception ex){
							continue;
						}
						try{
							f.setColumnIndex(Integer.parseInt(_eF.attributeValue("columnIndex")));
						}catch(Exception ex){
							continue;
						}
						chartData.getAggregation().get(0).addColumnFilter(f);
						
					}
					
				}catch(Exception ex){
					
				}
				
			}
			/*
			 * dimension settings
			 */
			for(Element _e : (List<Element>)e.elements("level")){
				try{
					chartData.setLevelIndex(
							Integer.valueOf(_e.attributeValue("index")), 
							Integer.valueOf(_e.attributeValue("categoryDataSetIndex")));
				}catch(Exception ex){
					
				}
				
			}
			
			
			chart.setComponentDatas(chartData);
			
			
			
		}catch(NullPointerException ex){
		}catch(Exception ex){
		}
		
		try{
			e = chartElement.element("multiSeriesChartData");
			
			MultiSerieChartData chartData = new MultiSerieChartData();
			if (e.attribute("colorPalette") != null){
				chartData.setColorPalette(model.getPalette(e.attributeValue("colorPalette")));
			}
			if (e.attribute("groupField") != null){
				chartData.setCategorieFieldIndex(Integer.parseInt(e.attributeValue("groupField")));
			}
			if (e.attribute("groupLabelField") != null){
				chartData.setCategorieFieldLabelIndex(Integer.parseInt(e.attributeValue("groupLabelField")));
			}
			if (e.attribute("orderType") != null){
				chartData.setOrderType(ChartOrderingType.valueOf(e.attributeValue("orderType")));
			}
			if (e.attribute("serieField") != null){
				chartData.setSerieFieldIndex(Integer.parseInt(e.attributeValue("serieField")));
			}
			
			for(Element _e : (List<Element>)e.elements("dataAggregation")){
				try{
					DataAggregation agg = new DataAggregation();
					agg.setAggregator(Integer.parseInt(_e.attributeValue("aggregator")));
					agg.setValueFieldIndex(Integer.parseInt(_e.attributeValue("valueField")));
					if (_e.attribute("measureName") != null){
						agg.setMeasureName(_e.attributeValue("measureName"));
					}
					if (_e.attribute("applyOnDistinctSerie") != null){
						agg.setApplyOnDistinctSerie(Boolean.parseBoolean(_e.attributeValue("applyOnDistinctSerie")));
					}
					if (_e.element("paletteColors") != null){
						int k = 0;
						for(Element _c : (List<Element>)_e.element("paletteColors").elements("color")){
							agg.setColorCode(_c.getStringValue(), k++);
						}
						List<String> l = new ArrayList<String>();
						for(int ii = k; ii < agg.getColorsCode().size(); ii++){
							l.add(agg.getColorsCode().get(ii));
						}
						agg.getColorsCode().removeAll(l);
					}
					
					
					if (_e.attribute("primaryAxis") != null){
						
						agg.setPrimaryAxis(Boolean.valueOf(_e.attributeValue("primaryAxis")));
					}
					if (_e.attribute("rendering") != null){
						try{
							agg.setRendering(MeasureRendering.valueOf(_e.attributeValue("rendering") ));
						}catch(Exception ex){
							
						}
					}
					
					chartData.addAggregation(agg);
					
					/*
					 * valueFilters
					 */
					for(Element _eF : (List<Element>)_e.elements("valueFilter")){
						ValueFilter f = new ValueFilter();
						try{
							f.setValue(Double.valueOf(_eF.attributeValue("value")));
							
						}catch(Exception ex){
							
						}
						try{
							f.setType(Type.valueOf(_eF.attributeValue("type")));
						}catch(Exception ex){
							
						}
						agg.addFilter(f);
						
					}
					
					for(Element _eF : (List<Element>)_e.elements("columnFilter")){
						ColumnFilter f = new ColumnFilter();
						try{
							f.setValue(_eF.element("value").getText());
							
						}catch(Exception ex){
							
						}
						try{
							f.setColumnIndex(Integer.parseInt(_eF.attributeValue("columnIndex")));
						}catch(Exception ex){
							
						}
						agg.addColumnFilter(f);
						
					}
					
				}catch(Exception ex){
					
				}
				
			}
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					chartData.setDataSet(ds);
				}
			}
			/*
			 * dimension settings
			 */
			for(Element _e : (List<Element>)e.elements("level")){
				try{
					chartData.setLevelIndex(
							Integer.valueOf(_e.attributeValue("index")), 
							Integer.valueOf(_e.attributeValue("categoryDataSetIndex")));
				}catch(Exception ex){
					
				}
				
			}
			
			chart.setComponentDatas(chartData);
			
			
		}catch(NullPointerException ex){
		}catch(Exception ex){
		}
		
		for(Element _e : (List<Element>)chartElement.elements()){
			if (_e.element("datasLimit") != null){
				
				try{
					((IChartData)chart.getDatas()).getLimit().setSize(Integer.parseInt(_e.element("datasLimit").attributeValue("size")));
				}catch(Exception ex){
//					ex.printStackTrace();
				}
				try{
					((IChartData)chart.getDatas()).getLimit().setType(Integer.parseInt(_e.element("datasLimit").attributeValue("type")));
				}catch(Exception ex){
//					ex.printStackTrace();
				}
				
			}
		}
		
		if (chart.getDatas() == null){
			errors.add(new ParserComponentException(chart, "unable to rebuild datas"));
		}
		else if (chart.getDatas().getDataSet() == null){
			errors.add(new ParserComponentException(chart, "no matching dataset"));
		}
		
		try{
			e = chartElement.element("options");
			
			if (e != null){
				for(Element eOpt : (List<Element>)e.elements()){
					IComponentOptions opt = FusionChartOptionParser.parseOption(eOpt);
					chart.setComponentOption(opt);
					for(Exception ex : FusionChartOptionParser.getExceptions()){
						errors.add(new ParserComponentException(chart, "Error when parsing ChartOptions", ex));
					}
					
				}
			}
			
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "error for component chart ", ex));
		}
		
		try{
			e = chartElement.element("valueFormat");
			if (e != null){
				if (e.element("backgroundColor") != null){
					int r = Integer.parseInt(e.element("backgroundColor").attributeValue("red"));
					int g = Integer.parseInt(e.element("backgroundColor").attributeValue("green"));
					int b = Integer.parseInt(e.element("backgroundColor").attributeValue("blue"));
					Color color = new Color(r, g, b);
					chart.getValueFormat().setBackgroundColor(color);

				}
				if (e.element("fontColor") != null){
					int r = Integer.parseInt(e.element("fontColor").attributeValue("red"));
					int g = Integer.parseInt(e.element("fontColor").attributeValue("green"));
					int b = Integer.parseInt(e.element("fontColor").attributeValue("blue"));
					Color color = new Color(r, g, b);
					chart.getValueFormat().setBackgroundColor(color);			
				}
				if (e.element("font") != null){
					chart.getValueFormat().setFont(e.element("font").getText());
				}
				if (e.element("size") != null){
					chart.getValueFormat().setFont(e.element("size").getText());
				}
				chart.getValueFormat().setBold(Boolean.parseBoolean(e.elementText("bold")));
				chart.getValueFormat().setItalic(Boolean.parseBoolean(e.elementText("italic")));
				chart.getValueFormat().setUnderline(Boolean.parseBoolean(e.elementText("underline")));
			}
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "error for component chart ", ex));
		}
		
		//component style
		Element style = chartElement.element("componentStyle");
		if (style != null){
			ComponentStyle cStyle = chart.getComponentStyle();
			for(Element el : (List<Element>)style.elements("style")){
				cStyle.setStyleFor(el.attributeValue("objectName"), el.getStringValue());
				
			}

		}
//		int i = chart.getParameters().size();
//		for(Element el : (List<Element>)chartElement.elements("parameter")){
//			chart.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
//		}
		
		
		
		//outputParameters
		try{
			e = chartElement.element("options");
			
			for(Element eP : (List<Element>)chartElement.elements("outputParameter")){
				
				try{
					int i = chart.getOutputParameters().size();
					OutputParameter p = new OutputParameter(i, eP.attributeValue("name"));
					chart.addComponentParameter(p);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "error for component chart ", ex));
		}
		
		EventParser.parse(chart, chartElement);
		return chart;
	}
	
	private ComponentChartDefinition parseRChart(Element chartElement){
		String name = chartElement.attributeValue("name");
		ComponentRChartDefinition chart = new ComponentRChartDefinition(name, model);
		
		
		if (chartElement.attribute("cssClass") != null){
			chart.setCssClass(chartElement.attributeValue("cssClass"));
		}
		
		
		
		
		Element e = chartElement.element("comment");
		if (e != null){
			chart.setComment(e.getText());
		}
		try{
			chart.setWidth(Integer.parseInt(chartElement.attributeValue("width")));
		}catch(NumberFormatException ex){
			
		}
		
		try{
			chart.setHeight(Integer.parseInt(chartElement.attributeValue("height")));
		}catch(NumberFormatException ex){
			
		}
		
		Element colors = chartElement.element("colorRanges");
		if (colors != null){
			for(Element o : (List<Element>)colors.elements("colorRange")){
				ColorRange r = new ColorRange();
				
				r.setHex(o.attributeValue("color"));
				if (o.attribute("legend") != null){
					r.setName(o.attributeValue("legend"));
				}
				if (o.attribute("minValue") != null){
					try{
						r.setMin(Integer.parseInt(o.attributeValue("minValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				if (o.attribute("maxValue") != null){
					try{
						r.setMax(Integer.parseInt(o.attributeValue("maxValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				chart.addColorRange(r);
			}
		}
		
		
		try{
			e = chartElement.element("drillInfo");
			chart.getDrillDatas().setDrillable(Boolean.parseBoolean(e.attributeValue("active")));
			chart.getDrillDatas().setCategorieAsParameterValue(Boolean.parseBoolean(e.attributeValue("categoryAsValue")));
			
			if (e.attribute("runDrillReport") != null && Boolean.parseBoolean(e.attributeValue("runDrillReport")) == Boolean.TRUE){
//				chart.getDrillDatas().setRunDetailReport(Boolean.parseBoolean(e.attributeValue("runDrillReport")));
				chart.getDrillDatas().setTypeTarget(TypeTarget.DetailReport);
			}
			if (e.attribute("drillLevelProvider") != null ){
				try{
					chart.getDrillDatas().setDrillLevelProvider(Integer.parseInt(e.attributeValue("drillLevelProvider") ));
				}catch(Exception ex){
					
				}
			}
			
			if (e.element("url") != null){
				chart.getDrillDatas().setUrl(e.element("url").getStringValue());
			}
			if (e.attribute("targetModel") != null){
				chart.getDrillDatas().setUrl(e.attributeValue("targetModel"));
			}
			if (e.attribute("drillType") != null){
				chart.getDrillDatas().setTypeTarget(TypeTarget.valueOf(e.attributeValue("drillType")));
			}
			
			if (e.attribute("keepColor") != null){
				chart.getDrillDatas().setKeepColor(Boolean.parseBoolean(e.attributeValue("keepColor")));
			}
			
			if (e.element("zoomPopup") != null && Boolean.parseBoolean(e.element("zoomPopup").getText()) == Boolean.TRUE){
				chart.getDrillDatas().setTypeTarget(TypeTarget.ZoomPopup);
//				chart.getDrillDatas().setZoomPopup(Boolean.parseBoolean(e.element("zoomPopup").getStringValue()));
			}
			if (e.element("zoomWidth") != null){
				chart.getDrillDatas().setZoomWidth(Integer.parseInt(e.element("zoomWidth").getStringValue()));
			}
			if (e.element("zoomHeight") != null){
				chart.getDrillDatas().setZoomHeight(Integer.parseInt(e.element("zoomHeight").getStringValue()));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		try{
			e = chartElement.element("chartRenderer");
			int rendererStyle = Integer.parseInt(e.attributeValue("rendererStyle"));
			
			ChartRenderer renderer = ChartRenderer.getRenderer(rendererStyle);
			chart.setRenderer(renderer);
			
		}catch(NumberFormatException ex){
			errors.add(new ParserComponentException(chart, "rendererStyle attribute is not a number", ex));
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(chart, "no renderer definition in the XML", ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "cannot create a FilterRenderer", ex));
		}
		
		try{
			e = chartElement.element("chartNature");
			int natureStyle = Integer.parseInt(e.attributeValue("style"));
			
			RChartNature nature = RChartNature.getNature(natureStyle);
			chart.setNature(nature);
			
		}catch(NumberFormatException ex){
			errors.add(new ParserComponentException(chart, "nature attribute is not a number for component chart ", ex));
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(chart, "no nature definition in the XML for component chart ", ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "cannot create a FilterRenderer for component chart ", ex));
		}
			
		try{
			e = chartElement.element("chartData");
			
			ChartData chartData = new ChartData();
			
			if (e.attribute("colorPalette") != null){
				chartData.setColorPalette(model.getPalette(e.attributeValue("colorPalette")));
			}
			
			
			if (e.attribute("valueField") != null){
				chartData.setValueFieldIndex(Integer.parseInt(e.attributeValue("valueField")));
			}
			if (e.attribute("groupField") != null){
				chartData.setGroupFieldIndex(Integer.parseInt(e.attributeValue("groupField")));
			}
			if (e.attribute("groupLabelField") != null){
				chartData.setCategorieFieldLabelIndex(Integer.parseInt(e.attributeValue("groupLabelField")));
			}

			if (e.attribute("axeXField") != null){
				chartData.setAxeXField(Integer.parseInt(e.attributeValue("axeXField")));
			}
			if (e.attribute("axeXFieldName") != null){
				chartData.setAxeXFieldName(e.attributeValue("axeXFieldName"));
			}
			if (e.attribute("axeYField") != null){
				chartData.setAxeYField(Integer.parseInt(e.attributeValue("axeYField")));
			}
			if (e.attribute("axeYFieldName") != null){
				chartData.setAxeYFieldName(e.attributeValue("groupLabelField"));
			}
			
			if (e.attribute("orderType") != null){
				chartData.setOrderType(ChartOrderingType.valueOf(e.attributeValue("orderType")));
			}
			
			chartData.setAggregator(Integer.parseInt(e.attributeValue("aggregator")));
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					chartData.setDataSet(ds);
				}
			}
			
			for(Element _e : (List<Element>)e.elements("dataAggregation")){
				try{
					DataAggregation agg = new DataAggregation();
					if (_e.attribute("applyOnDistinctSerie") != null){
						agg.setApplyOnDistinctSerie(Boolean.parseBoolean(_e.attributeValue("applyOnDistinctSerie")));
					}
					
					if (_e.attribute("orderType") != null){
						agg.setOrderType(ChartOrderingType.valueOf(_e.attributeValue("orderType")));
					}
					
					agg.setAggregator(Integer.parseInt(_e.attributeValue("aggregator")));
					agg.setValueFieldIndex(Integer.parseInt(_e.attributeValue("valueField")));
					if (_e.attribute("measureName") != null){
						agg.setMeasureName(_e.attributeValue("measureName"));
					}

					if (_e.element("paletteColors") != null){
						int k = 0;
						for(Element _c : (List<Element>)_e.element("paletteColors").elements("color")){
							agg.setColorCode(_c.getStringValue(), k++);
						}
						List<String> l = new ArrayList<String>();
						for(int ii = k; ii < agg.getColorsCode().size(); ii++){
							l.add(agg.getColorsCode().get(ii));
						}
						agg.getColorsCode().removeAll(l);
					}
					int i = 0;
					for(String s : agg.getColorsCode()){
						chartData.getAggregation().get(0).setColorCode(s, i++);	
					}
					
//					e.addAttribute("primaryAxis", isPrimaryAxis() + "");
//					e.addAttribute("rendering", getRendering().name());
					if (_e.attribute("primaryAxis") != null){
						
						agg.setPrimaryAxis(Boolean.valueOf(_e.attributeValue("primaryAxis")));
					}
					if (_e.attribute("rendering") != null){
						try{
							agg.setRendering(MeasureRendering.valueOf(_e.attributeValue("rendering") ));
						}catch(Exception ex){
							
						}
					}
					
					/*
					 * valueFilters
					 */
					for(Element _eF : (List<Element>)_e.elements("valueFilter")){
						ValueFilter f = new ValueFilter();
						try{
							f.setValue(Double.valueOf(_eF.attributeValue("value")));
							
						}catch(Exception ex){
							continue;
						}
						try{
							f.setType(Type.valueOf(_eF.attributeValue("type")));
						}catch(Exception ex){
							continue;
						}
						chartData.getAggregation().get(0).addFilter(f);
						
					}
					
					for(Element _eF : (List<Element>)_e.elements("columnFilter")){
						ColumnFilter f = new ColumnFilter();
						try{
							f.setValue(_eF.element("value").getText());
							
						}catch(Exception ex){
							continue;
						}
						try{
							f.setColumnIndex(Integer.parseInt(_eF.attributeValue("columnIndex")));
						}catch(Exception ex){
							continue;
						}
						chartData.getAggregation().get(0).addColumnFilter(f);
						
					}
					
				}catch(Exception ex){
					
				}
				
			}
			/*
			 * dimension settings
			 */
			for(Element _e : (List<Element>)e.elements("level")){
				try{
					chartData.setLevelIndex(
							Integer.valueOf(_e.attributeValue("index")), 
							Integer.valueOf(_e.attributeValue("categoryDataSetIndex")));
				}catch(Exception ex){
					
				}
				
			}
			
			
			chart.setComponentDatas(chartData);
			
			
			
		}catch(NullPointerException ex){
		}catch(Exception ex){
		}
		
		try{
			e = chartElement.element("multiSeriesChartData");
			
			MultiSerieChartData chartData = new MultiSerieChartData();
			if (e.attribute("colorPalette") != null){
				chartData.setColorPalette(model.getPalette(e.attributeValue("colorPalette")));
			}
			if (e.attribute("groupField") != null){
				chartData.setCategorieFieldIndex(Integer.parseInt(e.attributeValue("groupField")));
			}
			if (e.attribute("groupLabelField") != null){
				chartData.setCategorieFieldLabelIndex(Integer.parseInt(e.attributeValue("groupLabelField")));
			}
			if (e.attribute("orderType") != null){
				chartData.setOrderType(ChartOrderingType.valueOf(e.attributeValue("orderType")));
			}
			if (e.attribute("serieField") != null){
				chartData.setSerieFieldIndex(Integer.parseInt(e.attributeValue("serieField")));
			}
			
			for(Element _e : (List<Element>)e.elements("dataAggregation")){
				try{
					DataAggregation agg = new DataAggregation();
					agg.setAggregator(Integer.parseInt(_e.attributeValue("aggregator")));
					agg.setValueFieldIndex(Integer.parseInt(_e.attributeValue("valueField")));
					if (_e.attribute("measureName") != null){
						agg.setMeasureName(_e.attributeValue("measureName"));
					}
					if (_e.attribute("applyOnDistinctSerie") != null){
						agg.setApplyOnDistinctSerie(Boolean.parseBoolean(_e.attributeValue("applyOnDistinctSerie")));
					}
					if (_e.element("paletteColors") != null){
						int k = 0;
						for(Element _c : (List<Element>)_e.element("paletteColors").elements("color")){
							agg.setColorCode(_c.getStringValue(), k++);
						}
						List<String> l = new ArrayList<String>();
						for(int ii = k; ii < agg.getColorsCode().size(); ii++){
							l.add(agg.getColorsCode().get(ii));
						}
						agg.getColorsCode().removeAll(l);
					}
					
					
					if (_e.attribute("primaryAxis") != null){
						
						agg.setPrimaryAxis(Boolean.valueOf(_e.attributeValue("primaryAxis")));
					}
					if (_e.attribute("rendering") != null){
						try{
							agg.setRendering(MeasureRendering.valueOf(_e.attributeValue("rendering") ));
						}catch(Exception ex){
							
						}
					}
					
					chartData.addAggregation(agg);
					
					/*
					 * valueFilters
					 */
					for(Element _eF : (List<Element>)_e.elements("valueFilter")){
						ValueFilter f = new ValueFilter();
						try{
							f.setValue(Double.valueOf(_eF.attributeValue("value")));
							
						}catch(Exception ex){
							
						}
						try{
							f.setType(Type.valueOf(_eF.attributeValue("type")));
						}catch(Exception ex){
							
						}
						agg.addFilter(f);
						
					}
					
					for(Element _eF : (List<Element>)_e.elements("columnFilter")){
						ColumnFilter f = new ColumnFilter();
						try{
							f.setValue(_eF.element("value").getText());
							
						}catch(Exception ex){
							
						}
						try{
							f.setColumnIndex(Integer.parseInt(_eF.attributeValue("columnIndex")));
						}catch(Exception ex){
							
						}
						agg.addColumnFilter(f);
						
					}
					
				}catch(Exception ex){
					
				}
				
			}
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					chartData.setDataSet(ds);
				}
			}
			/*
			 * dimension settings
			 */
			for(Element _e : (List<Element>)e.elements("level")){
				try{
					chartData.setLevelIndex(
							Integer.valueOf(_e.attributeValue("index")), 
							Integer.valueOf(_e.attributeValue("categoryDataSetIndex")));
				}catch(Exception ex){
					
				}
				
			}
			
			chart.setComponentDatas(chartData);
			
			
		}catch(NullPointerException ex){
		}catch(Exception ex){
		}
		
		for(Element _e : (List<Element>)chartElement.elements()){
			if (_e.element("datasLimit") != null){
				
				try{
					((IChartData)chart.getDatas()).getLimit().setSize(Integer.parseInt(_e.element("datasLimit").attributeValue("size")));
				}catch(Exception ex){
//					ex.printStackTrace();
				}
				try{
					((IChartData)chart.getDatas()).getLimit().setType(Integer.parseInt(_e.element("datasLimit").attributeValue("type")));
				}catch(Exception ex){
//					ex.printStackTrace();
				}
				
			}
		}
		
		if (chart.getDatas() == null){
			errors.add(new ParserComponentException(chart, "unable to rebuild datas"));
		}
		else if (chart.getDatas().getDataSet() == null){
			errors.add(new ParserComponentException(chart, "no matching dataset"));
		}
		
		try{
			e = chartElement.element("options");
			
			if (e != null){
				for(Element eOpt : (List<Element>)e.elements()){
					IComponentOptions opt = FusionChartOptionParser.parseOption(eOpt);
					chart.setComponentOption(opt);
					for(Exception ex : FusionChartOptionParser.getExceptions()){
						errors.add(new ParserComponentException(chart, "Error when parsing ChartOptions", ex));
					}
					
				}
			}
			
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "error for component chart ", ex));
		}
		
		try{
			e = chartElement.element("valueFormat");
			if (e != null){
				if (e.element("backgroundColor") != null){
					int r = Integer.parseInt(e.element("backgroundColor").attributeValue("red"));
					int g = Integer.parseInt(e.element("backgroundColor").attributeValue("green"));
					int b = Integer.parseInt(e.element("backgroundColor").attributeValue("blue"));
					Color color = new Color(r, g, b);
					chart.getValueFormat().setBackgroundColor(color);

				}
				if (e.element("fontColor") != null){
					int r = Integer.parseInt(e.element("fontColor").attributeValue("red"));
					int g = Integer.parseInt(e.element("fontColor").attributeValue("green"));
					int b = Integer.parseInt(e.element("fontColor").attributeValue("blue"));
					Color color = new Color(r, g, b);
					chart.getValueFormat().setBackgroundColor(color);			
				}
				if (e.element("font") != null){
					chart.getValueFormat().setFont(e.element("font").getText());
				}
				if (e.element("size") != null){
					chart.getValueFormat().setFont(e.element("size").getText());
				}
				chart.getValueFormat().setBold(Boolean.parseBoolean(e.elementText("bold")));
				chart.getValueFormat().setItalic(Boolean.parseBoolean(e.elementText("italic")));
				chart.getValueFormat().setUnderline(Boolean.parseBoolean(e.elementText("underline")));
			}
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "error for component chart ", ex));
		}
		
		//component style
		Element style = chartElement.element("componentStyle");
		if (style != null){
			ComponentStyle cStyle = chart.getComponentStyle();
			for(Element el : (List<Element>)style.elements("style")){
				cStyle.setStyleFor(el.attributeValue("objectName"), el.getStringValue());
				
			}

		}
//		int i = chart.getParameters().size();
//		for(Element el : (List<Element>)chartElement.elements("parameter")){
//			chart.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
//		}
		
		
		
		//outputParameters
		try{
			e = chartElement.element("options");
			
			for(Element eP : (List<Element>)chartElement.elements("outputParameter")){
				
				try{
					int i = chart.getOutputParameters().size();
					OutputParameter p = new OutputParameter(i, eP.attributeValue("name"));
					chart.addComponentParameter(p);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
		}catch(Exception ex){
			errors.add(new ParserComponentException(chart, "error for component chart ", ex));
		}
		
		EventParser.parse(chart, chartElement);
		return chart;
	}
	
	private ComponentStyledTextInput parseStyledTextInput(Element element){
		String name = element.attributeValue("name");
		ComponentStyledTextInput label = new ComponentStyledTextInput(name, model);
		
		Element e = element.element("comment");

		if (element.attribute("cssClass") != null){
			label.setCssClass(element.attributeValue("cssClass"));
		}
		
		if (e != null){
			label.setComment(e.getText());
		}
		
		e = element.element("options");
		if (StyledTextOptions.class.getName().equals(e.attributeValue("class"))){
			try{
				((StyledTextOptions)label.getOptions(StyledTextOptions.class)).setColumnsNumber(Integer.parseInt(e.element("columnsNumber").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				((StyledTextOptions)label.getOptions(StyledTextOptions.class)).setRowsNumber(Integer.parseInt(e.element("rowsNumber").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				((StyledTextOptions)label.getOptions(StyledTextOptions.class)).setWrap(Boolean.parseBoolean(e.element("wrap").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				((StyledTextOptions)label.getOptions(StyledTextOptions.class)).setInitialValue(e.element("initialValue").getStringValue());
			}catch(Exception ex){
				
			}
			
		}
		
		
		int i = 0;
		for(Element el : (List<Element>)element.elements("parameter")){
			label.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		EventParser.parse(label, element);
		return label;
	}
	
	
	
	private ComponentTimer parseTimer(Element element){
		String name = element.attributeValue("name");
		ComponentTimer component = new ComponentTimer(name, model);
		
		Element e = element.element("comment");

		if (element.attribute("cssClass") != null){
			component.setCssClass(element.attributeValue("cssClass"));
		}
		
		if (e != null){
			component.setComment(e.getText());
		}
		
		e = element.element("options");
		if (TimerOptions.class.getName().equals(e.attributeValue("class"))){
			try{
				((TimerOptions)component.getOptions(TimerOptions.class)).setDelay(Integer.parseInt(e.element("delay").getStringValue()));
			}catch(Exception ex){
				
			}
			try{
				((TimerOptions)component.getOptions(TimerOptions.class)).setLabel(e.element("label").getStringValue());
			}catch(Exception ex){
				
			}
			try{
				((TimerOptions)component.getOptions(TimerOptions.class)).setStartOnLoad(Boolean.parseBoolean(e.element("startOnLoad").getStringValue()));
			}catch(Exception ex){
				
			}

			
		}
		
		
		int i = 0;
		for(Element el : (List<Element>)element.elements("parameter")){
			component.addComponentParameter(new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		
		EventParser.parse(component, element);
		return component;
	}
	
	
	
	private ComponentMap parseMap(Element mepElement){
		String name = mepElement.attributeValue("name");
		ComponentMap map = new ComponentMap(name, model);
		
		Element e = mepElement.element("comment");

		if (e != null){
			map.setComment(e.getText());
		}
		
		if (mepElement.attribute("cssClass") != null){
			map.setCssClass(mepElement.attributeValue("cssClass"));
		}
		
		if(mepElement.element("isShowLabels") != null) {
			map.setShowLabels(Boolean.parseBoolean(mepElement.element("isShowLabels").getText()));
		}
		
		
		try{
			e = mepElement.element("mapRenderer");
			int rendererStyle = Integer.parseInt(e.attributeValue("rendererStyle"));
			
			MapRenderer renderer = MapRenderer.getRenderer(rendererStyle);
			map.setRenderer(renderer);
			
		}catch(NumberFormatException ex){
			errors.add(new ParserComponentException(map, "rendererStyle attribute is not a number for component map", ex));
		}catch(NullPointerException ex){
			errors.add(new ParserComponentException(map, "no renderer definition in the XML for component map ", ex));
		}catch(Exception ex){
			errors.add(new ParserComponentException(map, "cannot create a FilterRenderer for component map ", ex));
		}
		
		try {
			e = mepElement.element("options");
			if (e != null && MapWMSOptions.class.getName().equals(e.attributeValue("class"))){
				MapWMSOptions opt = new MapWMSOptions();
				
				try {
					opt.setBaseLayerName(e.element(MapWMSOptions.BASE_LAYER_NAME).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setBaseLayerUrl(e.element(MapWMSOptions.BASE_LAYER_URL).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setVectorLayerName(e.element(MapWMSOptions.VECTOR_LAYER_NAME).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setVectorLayerUrl(e.element(MapWMSOptions.VECTOR_LAYER_URL).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setBounds(e.element(MapWMSOptions.BOUNDS).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setTileOrigin(e.element(MapWMSOptions.TILE_ORIGIN).getStringValue());
				} catch (Exception e2) {
					
				}
				try {
					opt.setProjection(e.element(MapWMSOptions.PROJECTION).getStringValue());
				} catch (Exception e2) {
					
				}
				
				map.setComponentOption(opt);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		//ColorRanges
		Element colors = mepElement.element("colorRanges");
		if (colors != null){
			for(Element o : (List<Element>)colors.elements("colorRange")){
				ColorRange r = new ColorRange();
				
				r.setHex(o.attributeValue("color"));
				if (o.attribute("legend") != null){
					r.setName(o.attributeValue("legend"));
				}
				if (o.attribute("minValue") != null){
					try{
						r.setMin(Integer.parseInt(o.attributeValue("minValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				if (o.attribute("maxValue") != null){
					try{
						r.setMax(Integer.parseInt(o.attributeValue("maxValue")));
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				map.addColorRange(r);
			}
		}
		
		/*
		 * mapInfo
		 */
		Element inf = mepElement.element("mapInfo");
		if (inf != null){
			try{
				((MapInfo)map.getMapInfo()).setMapObjectId(Long.parseLong(inf.attributeValue("fusionMapId")));
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			try{
				((MapInfo)map.getMapInfo()).setSwfFileName(inf.attributeValue("swfFileName"));
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			((MapInfo)map.getMapInfo()).setVanillaRuntimeUrl(inf.attributeValue("vanillaRuntimeUrl"));
			
			if (inf.attribute("width") != null){
				try{
					map.setWidth(Integer.parseInt(inf.attributeValue("width")));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (inf.attribute("height") != null){
				try{
					map.setHeight(Integer.parseInt(inf.attributeValue("height")));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (inf.attribute("mapId") != null){
				try{
					((MapInfo)map.getMapInfo()).setMapId(Integer.parseInt(inf.attributeValue("mapId")));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (inf.attribute("mapType") != null){
				try{
					((MapInfo)map.getMapInfo()).setMapType(inf.attributeValue("mapType"));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (inf.attribute("fmUser") != null){
				try{
					((MapInfo)map.getMapInfo()).setFmUser(inf.attributeValue("fmUser"));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (inf.attribute("fmPassword") != null){
				try{
					((MapInfo)map.getMapInfo()).setFmPassword(inf.attributeValue("fmPassword"));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (inf.attribute("isFusionMap") != null){
				try{
					((MapInfo)map.getMapInfo()).setFusionMap(Boolean.parseBoolean(inf.attributeValue("isFusionMap")));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		/*
		 * datas
		 */
		try{
			e = mepElement.element("mapData");
			
			
			IMapDatas mapDatas = null; 
			if (e.attributeValue("type", MapDatas.class.getName()).equals(MapDatas.class.getName())){
				mapDatas = new MapDatas();
				if (e.attribute("valueFieldIndex") != null){
					((MapDatas)mapDatas).setValueFieldIndex(Integer.parseInt(e.attributeValue("valueFieldIndex")));
				}
				
				if (e.attribute("zoneIdFieldIndex") != null){
					((MapDatas)mapDatas).setZoneIdFieldIndex(Integer.parseInt(e.attributeValue("zoneIdFieldIndex")));
				}
			}
			else{
				mapDatas = new GoogleMapDatas();
				if (e.attribute("valueFieldIndex") != null){
					((GoogleMapDatas)mapDatas).setValueFieldIndex(Integer.parseInt(e.attributeValue("valueFieldIndex")));
				}
				
				if (e.attribute("labelFieldIndex") != null){
					((GoogleMapDatas)mapDatas).setLabelFieldIndex(Integer.parseInt(e.attributeValue("labelFieldIndex")));
				}
				
				if (e.attribute("latitudeFieldIndex") != null){
					((GoogleMapDatas)mapDatas).setLatitudeFieldIndex(Integer.parseInt(e.attributeValue("latitudeFieldIndex")));
				}
				
				if (e.attribute("longitudeFieldIndex") != null){
					((GoogleMapDatas)mapDatas).setLongitudeFieldIndex(Integer.parseInt(e.attributeValue("longitudeFieldIndex")));
				}
			}
			
			String dataSetname = e.element("dataSet-ref").getText();
			
			
			for(DataSet ds : model.getDatasets()){
				if (ds.getName().equals(dataSetname)){
					if (mapDatas instanceof MapDatas){
						((MapDatas)mapDatas).setDataSet(ds);
					}
					else if (mapDatas instanceof GoogleMapDatas){
						((GoogleMapDatas)mapDatas).setDataSet(ds);
					}
					
				}
			}
			if (mapDatas.getDataSet() == null){
				errors.add(new ParserComponentException(map, "Unable to find dataSet " + dataSetname +" in dictionary"));
			}
			
			map.setComponentDatas(mapDatas);
			
			
		}catch(NullPointerException ex){
			if (map.getRenderer().getRendererStyle() != FilterRenderer.TEXT_FIELD &&
				map.getRenderer().getRendererStyle() != FilterRenderer.DATE_PIKER &&
				map.getRenderer().getRendererStyle() != FilterRenderer.DYNAMIC_TEXT && 
				(((MapInfo)map.getMapInfo()).getMapType() == null || !((MapInfo)map.getMapInfo()).getMapType().equals("FreeMetrics Map"))){
				errors.add(new ParserComponentException(map, "no filterData definition in the XML for component filter " + name));
			}
			
		}catch(Exception ex){
			if (map.getRenderer().getRendererStyle() != FilterRenderer.TEXT_FIELD&&
				map.getRenderer().getRendererStyle() != FilterRenderer.DATE_PIKER &&
				(((MapInfo)map.getMapInfo()).getMapType() == null || !((MapInfo)map.getMapInfo()).getMapType().equals("FreeMetrics Map"))){
				errors.add(new ParserComponentException(map, "error for component filter " + name));
			}
			
		}
		
		/*
		 * drill
		 */
		try{
			e = mepElement.element("drillInfo");
			
			map.getDrillInfo().setDrillable(Boolean.parseBoolean(e.attributeValue("active", "false")));
			map.getDrillInfo().setSendCategory(Boolean.parseBoolean(e.attributeValue("categoryAsValue", "false")));
			if (e.element("targetPage") != null){
				map.getDrillInfo().setModelPageName(e.element("targetPage").getText());
			}
			if (e.attribute("width") != null){
				map.getDrillInfo().setWidth(Integer.parseInt(e.attributeValue("width")));
			}
			if (e.attribute("height") != null){
				map.getDrillInfo().setHeight(Integer.parseInt(e.attributeValue("height")));
			}
			if (e.attribute("folderPageName") != null){
				map.getDrillInfo().setFolderPageName(e.attributeValue("folderPageName"));
			}
		}catch(Exception ex){
			
		}
		
		EventParser.parse(map, mepElement);
		return map;
	}
	
	private IComponentDefinition parseMarkdown(Element markdownElement) {
		String name = markdownElement.attributeValue("name");
		ComponentMarkdown markdown = new ComponentMarkdown(name, model);
		
		Element e = markdownElement.element("comment");

		if (markdownElement.attribute("cssClass") != null){
			markdown.setCssClass(markdownElement.attributeValue("cssClass"));
		}
		
		if (e != null){
			markdown.setComment(e.getText());
		}
		
		e = markdownElement.element("directoryItemId");
		if (e != null){
			try{
				markdown.setDirectoryItemId(Integer.parseInt(e.getText()));
			}catch(NumberFormatException ex){
				errors.add(new ParserComponentException(markdown, "No directoryItemId found"));
			}
			
		}
		int i = 0;
		List<ComponentParameter> l = new ArrayList<ComponentParameter>();
		for(Element el : (List<Element>)markdownElement.elements("parameter")){
			l.add( new ReportComponentParameter(el.attributeValue("name"), i++));
		}
		markdown.defineParameter(l);
		
		EventParser.parse(markdown, markdownElement);
		return markdown;
	}

}
