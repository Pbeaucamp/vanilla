package bpm.fd.jsp.wrapper.helper;

import java.awt.Color;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.fa.api.olap.Element;
import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentRChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.LineCombinationOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.api.core.model.components.definition.comment.CommentOptions;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption.DataGridCellType;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridData;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridLayout;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridOptions;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.DropDownOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.components.definition.filter.SliderOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.gauge.GaugeOptions;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.jsp.JspOptions;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;
import bpm.fd.api.core.model.components.definition.maps.ComponentDataViz;
import bpm.fd.api.core.model.components.definition.maps.DataVizOption;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapOption;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.olap.FaViewOption;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelComponent;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelData;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.core.Dashboard;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.DashboardPage;
import bpm.fd.core.component.ButtonComponent;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.ChartDrill;
import bpm.fd.core.component.ChartOption;
import bpm.fd.core.component.CommentComponent;
import bpm.fd.core.component.ContainerComponent;
import bpm.fd.core.component.CubeElement;
import bpm.fd.core.component.CubeElement.CubeElementType;
import bpm.fd.core.component.CubeViewComponent;
import bpm.fd.core.component.D4CComponent;
import bpm.fd.core.component.DataVizComponent;
import bpm.fd.core.component.DatagridColumn;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.core.component.DivComponent;
import bpm.fd.core.component.EventType;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.core.component.FlourishComponent;
import bpm.fd.core.component.GaugeComponent;
import bpm.fd.core.component.ImageComponent;
import bpm.fd.core.component.JspComponent;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.KpiComponent;
import bpm.fd.core.component.LinkComponent;
import bpm.fd.core.component.MapComponent;
import bpm.fd.core.component.MarkdownComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.ReportComponent;
import bpm.fd.core.component.kpi.KpiAggreg;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.fd.runtime.model.datas.ConvertDataSet;
import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * 
 * @author Marc
 * 
 *         Convert a FD Dashboard into a web dashboard
 * 
 */
public class DashboardHelper {

	private IVanillaAPI vanillaApi;

	private FdProject project;

	private List<DashboardComponent> components = new ArrayList<DashboardComponent>();
	private HashMap<String, List<bpm.fd.core.ComponentParameter>> componentParameters = new HashMap<String, List<bpm.fd.core.ComponentParameter>>();

	private List<Datasource> datasources;

	public Dashboard convertDashboard(FdProject dashboard) throws Exception {
		this.project = dashboard;

		datasources = getVanillaRootApi().getVanillaPreferencesManager().getDatasources();

		Dashboard resultDashboard = new Dashboard();
		resultDashboard.setComponents(new ArrayList<DashboardComponent>());

		FdProjectRepositoryDescriptor descriptor = (FdProjectRepositoryDescriptor) dashboard.getProjectDescriptor();

		String dashboardName = dashboard.getProjectDescriptor().getProjectName();
		resultDashboard.setName(dashboardName);

		String dashboardDescription = dashboard.getProjectDescriptor().getDescription();
		resultDashboard.setDescription(dashboardDescription);

		int mainModelId = descriptor.getModelDirectoryItemId();
		resultDashboard.setItemId(mainModelId);

		int dictionaryId = descriptor.getDictionaryDirectoryItemId();
		resultDashboard.setDictionaryId(dictionaryId);

		for (IBaseElement elem : dashboard.getFdModel().getContent()) {
			if (elem instanceof Folder) {
				Folder folder = (Folder) elem;
				for (IBaseElement element : folder.getContent()) {
					if (element instanceof FolderPage) {
						DashboardPage resultPage = convertPage((FolderPage) element);
						resultDashboard.addPage(resultPage);
					}

				}
			}
			else if (elem instanceof Cell) {
				DashboardComponent resultComp = convertComponent((Cell) elem);
				if (resultComp != null) {
					resultDashboard.addComponent(resultComp);
				}
			}

		}

		// set the parameter origins
		for (DashboardComponent comp : components) {
			if (componentParameters.get(comp.getName()) != null) {
				for (bpm.fd.core.ComponentParameter param : componentParameters.get(comp.getName())) {
					param.setProvider(comp);
				}
			}
		}

		// read resources
		FileCSS fcss = (FileCSS) project.getResources(FileCSS.class).get(0);
		String css = new String(Files.readAllBytes(Paths.get(fcss.getFile().getAbsolutePath())));
		resultDashboard.setCss(css);
		
		HashMap<ElementsEventType, String> dashboardScripts = dashboard.getFdModel().getEventScript();
		resultDashboard.setEventScript(convertScripts(dashboardScripts));

		return resultDashboard;
	}
	
	private HashMap<EventType, String> convertScripts(HashMap<ElementsEventType, String> eventScripts) {
		HashMap<EventType, String> scripts = new HashMap<>();
		for (Entry<ElementsEventType, String> set : eventScripts.entrySet()) {
			scripts.put(convertEventType(set.getKey()), set.getValue());
		}
		return scripts;
	}

	private EventType convertEventType(ElementsEventType  type) {
		switch (type) {
		case onAbort:
			return EventType.onAbort;
		case onBlur:
			return EventType.onBlur;
		case onChange:
			return EventType.onChange;
		case onClick:
			return EventType.onClick;
		case onDblClick:
			return EventType.onDblClick;
		case onFocus:
			return EventType.onFocus;
		case onKeyDown:
			return EventType.onKeyDown;
		case onKeyPress:
			return EventType.onKeyPress;
		case onKeyUp:
			return EventType.onKeyUp;
		case onLoad:
			return EventType.onLoad;
		case onMouseDown:
			return EventType.onMouseDown;
		case onMouseMove:
			return EventType.onMouseMove;
		case onMouseOut:
			return EventType.onMouseOut;
		case onMouseOver:
			return EventType.onMouseOver;
		case onMouseUp:
			return EventType.onMouseUp;
		case onSelect:
			return EventType.onSelect;
		case onSubmit:
			return EventType.onSubmit;
		case onUnload:
			return EventType.onUnload;

		default:
			break;
		}
		return null;
	}

	private DashboardComponent convertComponent(Cell element) throws Exception {
		DashboardComponent component = null;
		if(element instanceof DivCell || element instanceof StackableCell) {
			if(element instanceof DivCell) {
				component = new DivComponent();
				
				List<DashboardComponent> divComps = new ArrayList<DashboardComponent>();			
				
				for(IBaseElement elem : element.getContent()) {
					DashboardComponent comp = convertComponent((Cell) elem);
					divComps.add(comp);
					
				}
				ContainerComponent cont = new ContainerComponent(divComps);
				List<ContainerComponent> containers = new ArrayList<ContainerComponent>();
				containers.add(cont);
				((DivComponent)component).setContainers(containers);
			}
			else if(element instanceof DrillDrivenStackableCell) {
				
			}
			else {
				
			}
			
			component.setName(element.getName());
			component.setCssClass(element.getCssClass());

			component.setTop(element.getPosition().y);
			component.setLeft(element.getPosition().x);
			component.setWidth(element.getSize().x);
			component.setHeight(element.getSize().y);
			
			components.add(component);
			
		}
		else {
			
			IBaseElement elem = element.getContent().get(0);
			// components
			if (elem instanceof IComponentDefinition) {
				if (elem instanceof ComponentChartDefinition) {
					if(((ComponentChartDefinition)elem).getDatas().getDataSet().getOdaExtensionDataSourceId().equals("bpm.fm.oda.driver")) {
						component = convertKpiChart(elem, element);
					}
					
					else if (elem instanceof ComponentRChartDefinition) {
						component = convertRChart((ComponentRChartDefinition) elem, element);
					}
					else {
						component = convertChart((ComponentChartDefinition) elem, element);
					}
				}
				
				else if (elem instanceof ComponentDataViz) {
					component = convertDataviz((ComponentDataViz) elem, element);
				}
	
				else if (elem instanceof ComponentFilterDefinition) {
					component = convertFilter((ComponentFilterDefinition) elem, element);
				}
	
				else if (elem instanceof DynamicLabelComponent) {
					component = convertDynamicLabel((DynamicLabelComponent) elem, element);
				}
	
				else if (elem instanceof LabelComponent) {
					component = convertLabel((LabelComponent) elem, element);
				}
				else if (elem instanceof ComponentPicture) {
					component = convertImage((ComponentPicture) elem, element);
				}
	
				else if (elem instanceof ComponentOsmMap) {
					if (((ComponentOsmMap) elem).getDatas() instanceof VanillaMapData) {
						component = convertMap((ComponentOsmMap) elem, element);
					}
				}
	
				else if (elem instanceof ComponentGauge) {
					component = convertGauge((ComponentGauge) elem, element);
				}
	
				else if (elem instanceof ComponentReport) {
					component = convertReport((ComponentReport) elem, element);
				}
	
				else if (elem instanceof ComponentKpi) {
					component = convertKpi((ComponentKpi) elem, element);
				}
	
				else if (elem instanceof ComponentFaView) {
					component = convertCubeView((ComponentFaView) elem, element);
				}
	
				else if (elem instanceof ComponentMarkdown) {
					component = convertMarkdown((ComponentMarkdown) elem, element);
				}
	
				else if (elem instanceof ComponentJsp) {
					component = convertJsp((ComponentJsp) elem, element);
				}
	
				else if (elem instanceof ComponentD4C) {
					component = convertD4C((ComponentD4C) elem, element);
				}
	
				else if (elem instanceof ComponentFlourish) {
					component = convertFlourish((ComponentFlourish) elem, element);
				}
	
				else if (elem instanceof ComponentComment) {
					component = convertComment((ComponentComment) elem, element);
				}
	
				else if (elem instanceof ComponentLink) {
					component = convertLink((ComponentLink) elem, element);
				}
	
				else if (elem instanceof ComponentDataGrid) {
					component = convertDatagrid((ComponentDataGrid) elem, element);
				}
	
				else if (elem instanceof ComponentButtonDefinition) {
					component = convertButton((ComponentButtonDefinition) elem, element);
				}
	
				if (component == null) {
					return null;
				}
	
				// parameters
				for (Entry<IComponentDefinition, ComponentConfig> entry : element.getConfigurations().entrySet()) {
					ComponentConfig conf = entry.getValue();
					for (ComponentParameter param : conf.getParameters()) {
						bpm.fd.core.ComponentParameter p = new bpm.fd.core.ComponentParameter();
						p.setName(param.getName());
						p.setDefaultValue(param.getDefaultValue());
						p.setIndex(param.getIndice());
	
						if (componentParameters.get(conf.getComponentNameFor(param)) == null) {
							componentParameters.put(conf.getComponentNameFor(param), new ArrayList<bpm.fd.core.ComponentParameter>());
						}
						componentParameters.get(conf.getComponentNameFor(param)).add(p);
						component.addParameter(p);
					}
	
					break;
				}
	
				// general
				setComponentGeneralProperties((IComponentDefinition) elem, element, component);
	
				components.add(component);
	
			}
		}

		return component;
	}

	private DashboardComponent convertDataviz(ComponentDataViz elem, Cell element) {
		DataVizComponent comp = new DataVizComponent();
		comp.setName(elem.getName());
		comp.setDatavizId(((DataVizOption)elem.getOptions(DataVizOption.class)).getDataprepId());
		return comp;
	}

	private DashboardComponent convertKpiChart(IBaseElement elem, Cell element) throws Exception {
		ChartComponent chart = convertChart((ComponentChartDefinition) elem, element);
		
		IFreeMetricsManager fmApi = new RemoteFreeMetricsManager(getVanillaRootApi().getVanillaContext());
		
		KpiChartComponent compo = new KpiChartComponent();
		KpiChart kpiChart = new KpiChart();
		kpiChart.setOption(chart);
		compo.setDataset(chart.getDataset());
		compo.setElement(kpiChart);
		compo.setTitle(chart.getTitle());
		compo.setName(chart.getName());
		
		//aggreg
		List<KpiAggreg> aggregs = new ArrayList<KpiAggreg>();
		if(chart.getAggregations() != null && !chart.getAggregations().isEmpty()) {
			for(DataAggregation agg : chart.getAggregations()) {
				KpiAggreg kpiAgg = new KpiAggreg();
				kpiAgg.setOperator(DataAggregation.AGGREGATORS_NAME[agg.getAggregator()]);
				//find column
				int index = agg.getValueFieldIndex();
				kpiAgg.setColumn(KpiAggreg.columnNames[index - 1]);
				
				//find metric
				int metricId = -1;
				try {
					metricId = Integer.parseInt(agg.getColumnFilters().get(0).getValue());						
				} catch (Exception e) {
					//no column filter so it's a single serie chart
					String kpiQuery = chart.getDataset().getRequest();
					metricId = Integer.parseInt(kpiQuery.substring(kpiQuery.indexOf("<Metric>") + 8, kpiQuery.indexOf("</Metric>")));
				}
				Metric m = fmApi.getMetric(metricId);
				kpiAgg.setMetric(m);
				aggregs.add(kpiAgg);
			}
		}
		kpiChart.setAggregs(aggregs);
		
		//level
		int levelIndex = chart.getGroupFieldIndex();
		ColumnDescriptor column = ((ComponentChartDefinition) elem).getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(levelIndex);
		
		LOOK:for(Axis axis : fmApi.getAxis()) {
			for(Level level : axis.getChildren()) {
				if(level.getName().equals(column.getColumnName())) {
					kpiChart.setLevel(level);
					break LOOK;
				}
			}
		}
		
		
		return compo;
	}

	private ButtonComponent convertButton(ComponentButtonDefinition elem, Cell element) {
		ButtonComponent button = new ButtonComponent();
		ButtonOptions options = (ButtonOptions) elem.getOptions(ButtonOptions.class);
		button.setTitle(options.getLabel());
		return button;
	}

	private DatagridComponent convertDatagrid(ComponentDataGrid elem, Cell element) throws Exception {
		DatagridComponent datagrid = new DatagridComponent();

		// data
		DataGridData data = (DataGridData) elem.getDatas();
		datagrid.setOrderType(data.getOrderType());
		datagrid.setOrderFieldIndex(data.getOrderFieldPosition() - 1);
		
		Dataset dataset = ConvertDataSet.convertDataset(data.getDataSet(), elem.getDictionary() , this.vanillaApi );
		datagrid.setDataset(dataset);
		
		DataGridLayout layout = elem.getLayout();
		if (layout != null && layout.getColumnOptions() != null) {
			List<DatagridColumn> columns = new ArrayList<DatagridColumn>();
			List<DataGridColumnOption> columnOptions = layout.getColumnOptions();
			
			for (DataGridColumnOption option : columnOptions) {
				DataColumn column = null;
				if (dataset != null && dataset.getMetacolumns() != null) {
					for (DataColumn col : dataset.getMetacolumns()) {
						if (col.getColumnName().equals(option.getName())) {
							column = col;
							break;
						}
					}
				}

				boolean visible = option.getCellType() == DataGridCellType.Visible || option.getCellType() == DataGridCellType.Editable;
				DatagridColumn datagridColumn = new DatagridColumn(column, option.getCustomName(), visible, option.isGroup(), option.getAggregation());
				columns.add(datagridColumn);
			}
			
			datagrid.setColumns(columns);
		}

		// option
		DataGridOptions options = (DataGridOptions) elem.getOptions(DataGridOptions.class);
		datagrid.setHeadersVisible(options.isHeadersVisible());
		datagrid.setIncludeTotal(options.isIncludeTotal());

		return datagrid;
	}

	private LinkComponent convertLink(ComponentLink elem, Cell element) {
		LinkComponent link = new LinkComponent();
		LinkOptions options = (LinkOptions) elem.getOptions(LinkOptions.class);
		link.setTitle(elem.getName());
		link.setUrl(options.getUrl());
		return link;
	}

	private JspComponent convertJsp(ComponentJsp elem, Cell element) {
		JspComponent jsp = new JspComponent();
		jsp.setTitle(elem.getName());
		jsp.setUrl(elem.getJspUrl());
		JspOptions options = (JspOptions) elem.getOptions(JspOptions.class);
		jsp.setShowBorder(options.getBorder_width() > 0);
		return jsp;
	}

	private D4CComponent convertD4C(ComponentD4C elem, Cell element) {
		D4CComponent d4c = new D4CComponent();
		d4c.setTitle(elem.getName());
		d4c.setUrl(elem.getUrl());
//		D4COptions options = (D4COptions) elem.getOptions(D4COptions.class);
//		d4c.setShowBorder(options.getBorder_width() > 0);
		return d4c;
	}

	private FlourishComponent convertFlourish(ComponentFlourish elem, Cell element) {
		FlourishComponent flourish = new FlourishComponent();
		flourish.setTitle(elem.getName());
		flourish.setUrl(elem.getUrl());
//		FlourishOptions options = (FlourishOptions) elem.getOptions(FlourishOptions.class);
//		flourish.setShowBorder(options.getBorder_width() > 0);
		return flourish;
	}

	private CommentComponent convertComment(ComponentComment elem, Cell element) {
		CommentComponent comment = new CommentComponent();
		comment.setName(elem.getName());
		comment.setTitle(elem.getComment());

		CommentOptions options = (CommentOptions) elem.getOptions(CommentOptions.class);
		comment.setAllowAddComments(options.isAllowAddComments());
		comment.setShowComments(options.isShowComments());
		comment.setLimit(options.getLimit());
		comment.setLimitComment(options.isLimitComment());
		comment.setValidation(options.isValidation());
		
		return comment;
	}

	private MarkdownComponent convertMarkdown(ComponentMarkdown elem, Cell element) {
		MarkdownComponent report = new MarkdownComponent();
		report.setItemId(elem.getDirectoryItemId());
		return report;
	}

	private CubeViewComponent convertCubeView(ComponentFaView elem, Cell element) {
		CubeViewComponent report = new CubeViewComponent();
		report.setItemId(elem.getDirectoryItemId());

		FaViewOption options = (FaViewOption) elem.getOptions(FaViewOption.class);
		report.setInteractive(options.isInteractive());
		report.setShowDimensions(options.isShowDimensions());
		report.setShowCubeFunctions(options.isShowCubeFunctions());

		List<CubeElement> elements = new ArrayList<>();
		if (options.getElements() != null) {
			for (Element el : options.getElements()) {
				elements.add(new CubeElement(CubeElementType.UNKNOWN, el.getName(), el.getCaption(), el.getUniqueName(), true));
			}
		}
		report.setElements(elements);

		return report;
	}

	private ReportComponent convertReport(ComponentReport elem, Cell element) {
		ReportComponent report = new ReportComponent();
		report.setItemId(elem.getDirectoryItemId());
		return report;
	}

	private KpiComponent convertKpi(ComponentKpi elem, Cell element) {
		KpiComponent kpi = new KpiComponent();
		kpi.setItemId(elem.getDirectoryItemId());
		return kpi;
	}

	private GaugeComponent convertGauge(ComponentGauge elem, Cell element) throws Exception {
		GaugeComponent gauge = new GaugeComponent();

		GaugeOptions options = (GaugeOptions) elem.getOptions(GaugeOptions.class);
		gauge.setBgAlpha(options.getBgAlpha());
		gauge.setBgColor(convertColor(options.getBgColor()));
		gauge.setBulb(options.isBulb());
		gauge.setColorBadValue(convertColor(options.getColorBadValue()));
		gauge.setColorGoodValue(convertColor(options.getColorGoodValue()));
		gauge.setColorMediumValue(convertColor(options.getColorMediumValue()));
		gauge.setInnerRadius(options.getInnerRadius());
		gauge.setOuterRadius(options.getOuterRadius());
		gauge.setShowValues(options.isShowValues());
		gauge.setStartAngle(options.getStartAngle());
		gauge.setStopAngle(options.getStopAngle());

		ComplexGaugeDatas gaugeDatas = (ComplexGaugeDatas) elem.getDatas();
		gauge.setDataset(ConvertDataSet.convertDataset(gaugeDatas.getDataSet(), elem.getDictionary(), this.vanillaApi));

		gauge.setValueField(gaugeDatas.getIndexValue() - 1);
		gauge.setMinMaxFromField(!gaugeDatas.isRangeManual());
		if (gauge.isMinMaxFromField()) {
			gauge.setMin(new Float(gaugeDatas.getIndexMin() - 1));
			gauge.setMax(new Float(gaugeDatas.getIndexMax() - 1));
		}
		else {
			gauge.setMin(new Float(gaugeDatas.getMinValue()));
			gauge.setMax(new Float(gaugeDatas.getMaxValue()));
		}

		gauge.setTolerance(gaugeDatas.getIndexTolerance());

		gauge.setTargetFromField(gaugeDatas.isUseFieldForTarget());
		if (gaugeDatas.isTargetNeeded()) {
			if (gauge.isTargetFromField()) {
				gauge.setTarget(new Float(gaugeDatas.getTargetIndex() - 1));
			}
			else {
				gauge.setTarget(gaugeDatas.getTargetValue());
			}
		}

		gauge.setThresholdFromField(gaugeDatas.isExpectedFieldsUsed());
		if (gauge.isThresholdFromField()) {
			gauge.setMinThreshold(new Float(gaugeDatas.getIndexMinSeuil() - 1));
			gauge.setMaxThreshold(new Float(gaugeDatas.getIndexMaxSeuil() - 1));
		}
		else {
			gauge.setMinThreshold(new Float(gaugeDatas.getIndexMinSeuil()));
			gauge.setMaxThreshold(new Float(gaugeDatas.getIndexMaxSeuil()));
		}

		return gauge;
	}

	private MapComponent convertMap(ComponentOsmMap elem, Cell element) throws Exception {
		MapComponent map = new MapComponent();

		VanillaMapOption options = (VanillaMapOption) elem.getOptions(VanillaMapOption.class);
		map.setLegendLayout(options.getLegendLayout());
		map.setLegendOrientation(options.getLegendOrientation());
		map.setNumberFormat(options.getNumberFormat());
		map.setShowBaseLayer(options.isShowBaseLayer());
		map.setShowLegend(options.isShowLegend());

		VanillaMapData data = (VanillaMapData) elem.getDatas();
		map.setDataset(ConvertDataSet.convertDataset(data.getDataSet(), elem.getDictionary(),this.vanillaApi));
		map.setMap(data.getMap());
		map.setMapId(data.getMapId());
		map.setValueFieldIndex(data.getValueFieldIndex() - 1);
		map.setZoneFieldIndex(data.getZoneFieldIndex() - 1);

		map.setSeries(data.getSeries());

		return map;
	}

	private ImageComponent convertImage(ComponentPicture elem, Cell element) throws Exception {
		ImageComponent image = new ImageComponent();

		try {
			VanillaImage vanillaImage = getVanillaRootApi().getImageManager().getImageByName(elem.getPictureUrl());
			image.setUrl(vanillaImage.getUrl());
		} catch (Exception e) {
			IResource resource = project.getResource(FileImage.class, elem.getPictureUrl());

			try (FileInputStream is = new FileInputStream(resource.getFile())) {
				VanillaImage img = getVanillaRootApi().getImageManager().uploadImage(is, elem.getPictureUrl());
				image.setUrl(img.getUrl());
			} catch (Exception ex) {
				throw ex;
			}
		}

		return image;
	}

	private bpm.fd.core.component.LabelComponent convertLabel(LabelComponent elem, Cell element) {
		bpm.fd.core.component.LabelComponent label = new bpm.fd.core.component.LabelComponent();
		label.setTitle(((LabelOptions) elem.getOptions(LabelOptions.class)).getText());
		return label;
	}

	private bpm.fd.core.component.DynamicLabelComponent convertDynamicLabel(DynamicLabelComponent elem, Cell element) throws Exception {
		bpm.fd.core.component.DynamicLabelComponent component = new bpm.fd.core.component.DynamicLabelComponent();

		component.setTitle(elem.getLabel());

		// data
		DynamicLabelData data = (DynamicLabelData) elem.getDatas();
		Dataset dataset = ConvertDataSet.convertDataset(data.getDataSet(), elem.getDictionary(), this.vanillaApi);
		component.setDataset(dataset);

		component.setColumnValueIndex(data.getColumnValueIndex() - 1);
		
		return component;
	}

	private FilterComponent convertFilter(ComponentFilterDefinition elem, Cell element) throws Exception {
		FilterComponent component = new FilterComponent();

		component.setTitle(elem.getLabel());

		// data
		FilterData data = (FilterData) elem.getDatas();
		Dataset dataset = ConvertDataSet.convertDataset(data.getDataSet(), elem.getDictionary(),this.vanillaApi);
		component.setDataset(dataset);

		component.setColumnLabelIndex(data.getColumnLabelIndex() - 1);
		component.setColumnOrderIndex(data.getColumnOrderIndex() - 1);
		component.setColumnValueIndex(data.getColumnValueIndex() - 1);

		// options
		component.setRenderer(bpm.fd.core.component.FilterRenderer.getRenderer(elem.getRenderer().getRendererStyle()));
		FilterOptions option = (FilterOptions) elem.getOptions(FilterOptions.class);

		component.setDefaultValue(option.getDefaultValue());
		component.setHidden(option.isHidden());
		component.setInitParameterWithFirstValue(option.isInitParameterWithFirstValue());
		component.setRequired(option.isRequired());
		component.setSelectFirstValue(option.isSelectFirstValue());
		component.setSubmitOnChange(option.isSubmitOnChange());

		if (FilterRenderer.getRenderer(elem.getRenderer().getRendererStyle()) == FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX)) {
			DropDownOptions ddoption = (DropDownOptions) elem.getOptions(DropDownOptions.class);
			component.setSize(ddoption.getSize());
		}
		else if (FilterRenderer.getRenderer(elem.getRenderer().getRendererStyle()) == FilterRenderer.getRenderer(FilterRenderer.MENU)) {
			MenuOptions moption = (MenuOptions) elem.getOptions(MenuOptions.class);
			component.setVertical(moption.getIsVertical());
			component.setSize(moption.getSize());
		}
		else if (FilterRenderer.getRenderer(elem.getRenderer().getRendererStyle()) == FilterRenderer.getRenderer(FilterRenderer.SLIDER)) {
			SliderOptions soption = (SliderOptions) elem.getOptions(SliderOptions.class);
			component.setAutoRun(soption.isAutoRun());
			component.setBarColor(soption.getBarColor());
			component.setDelay(soption.getDelay());
			component.setSliderColor(soption.getSliderColor());
		}

		return component;
	}

	private ChartComponent convertChart(ComponentChartDefinition elem, Cell cell) throws Exception {
		ChartComponent component = new ChartComponent();

		component.setNature(elem.getNature());

		IChartData chartData = (IChartData) elem.getDatas();

		// dataset
		Dataset dataset = ConvertDataSet.convertDataset(chartData.getDataSet(), elem.getDictionary(),this.vanillaApi);
		component.setDataset(dataset);

		component.setAggregations(chartData.getAggregation());
		component.setOrderType(chartData.getOrderType());
		component.setGroupFieldIndex(chartData.getCategorieFieldIndex() - 1);
		component.setGroupFieldLabelIndex(chartData.getCategorieFieldLabelIndex() - 1);

		// simple serie
		if (chartData instanceof ChartData) {
			component.setColorPalette(((ChartData) chartData).getColorPalette());
			component.setAggregations(((ChartData) chartData).getAggregation());
		}

		// multi serie
		else if (chartData instanceof MultiSerieChartData) {
			component.setColorPalette(((MultiSerieChartData) chartData).getColorPalette());
			if (((MultiSerieChartData) chartData).getSerieFieldIndex() != null) {
				component.setSubGroupFieldIndex(((MultiSerieChartData) chartData).getSerieFieldIndex() - 1);
			}

			component.setAggregations(((MultiSerieChartData) chartData).getAggregation());
		}

		if (elem.getDrillDatas().isDrillable()) {
			component.setChartDrill(new ChartDrill());
		}

		// options
		convertChartOption(component, elem);

		return component;
	}

	private RChartComponent convertRChart(ComponentRChartDefinition elem, Cell cell) throws Exception {
		RChartComponent component = new RChartComponent();

		component.setNature(elem.getRChartNature());

		ChartData chartData = (ChartData) elem.getDatas();

		// dataset
		Dataset dataset = ConvertDataSet.convertDataset(chartData.getDataSet(), elem.getDictionary(),this.vanillaApi);
		component.setDataset(dataset);

		component.setGroupFieldIndex(chartData.getCategorieFieldIndex() - 1);
		component.setGroupFieldLabel(chartData.getGroupFieldName());
		component.setAxeXField(chartData.getAxeXField() - 1);
		component.setAxeYField(chartData.getAxeYField() - 1);
		component.setAxeXFieldLabel(chartData.getAxeXFieldName());
		component.setAxeYFieldLabel(chartData.getAxeYFieldName());
		
		if(component.getNature().getNature() == RChartNature.CORRELATION ){
			component.setSelectedColumList(chartData.getSelectedColumns());
			component.setSelectedColumName(chartData.getSelectedColumName());
		}
		else{
			component.setSelectedColumList(null);
			component.setSelectedColumName(null);
		}

		// options
		convertChartOption(component, elem);

		return component;
	}

	private void convertChartOption(ChartComponent chart, ComponentChartDefinition component) {

		ChartOption options = chart.getOption();

		for (IComponentOptions option : component.getOptions()) {
			if (option instanceof GenericNonPieOptions) {
				GenericNonPieOptions genOptions = ((GenericNonPieOptions) option);
				options.setPlaceValuesInside(genOptions.isPlaceValuesInside());
				options.setPYAxisName(genOptions.getPYAxisName());
				options.setRotateLabels(genOptions.isRotateLabels());
				options.setRotateValues(genOptions.isRotateValues());
				options.setRotateYAxisName(genOptions.isRotateYAxisName());
				options.setSlantLabels(genOptions.isSlantLabels());
				options.setSYAxisName(genOptions.getSYAxisName());
				options.setxAxisName(genOptions.getxAxisName());
				
			}
			if (option instanceof GenericOptions) {

				GenericOptions genOptions = ((GenericOptions) option);

				options.setBaseFontSize(genOptions.getBaseFontSize());
				options.setBgAlpha(genOptions.getBgAlpha());
				options.setBgColor(convertColor(genOptions.getBgColor()));
				options.setBgSWFAlpha(genOptions.getBgSWFAlpha());
				options.setBorderColor(convertColor(genOptions.getBorderColor()));
				options.setBorderThickness(genOptions.getBorderThickness());

				chart.setTitle(genOptions.getCaption());

				options.setDynamicLegend(genOptions.isDynamicLegend());
				options.setExportEnable(genOptions.isExportEnable());
				options.setLabelSize(genOptions.getLabelSize());
				options.setMultiLineLabels(genOptions.isMultiLineLabels());
				options.setShowBorder(genOptions.isShowBorder());
				options.setShowLabel(genOptions.isShowLabel());
				options.setShowValues(genOptions.isShowValues());
				options.setSubCaption(genOptions.getSubCaption());
			}
			if (option instanceof LineCombinationOption) {
				options.setLineSerieName(((LineCombinationOption) option).getLineSerieName());
			}
			if (option instanceof NumberFormatOptions) {

				NumberFormatOptions numOptions = ((NumberFormatOptions) option);

				options.setDecimals(numOptions.getDecimals());
				options.setDecimalSeparator(numOptions.getDecimalSeparator());
				options.setForceDecimal(numOptions.isForceDecimal());
				options.setFormatNumber(numOptions.isFormatNumber());
				options.setFormatNumberScale(numOptions.isFormatNumberScale());
				options.setNumberPrefix(numOptions.getNumberPrefix());
				options.setNumberSuffix(numOptions.getNumberSuffix());
				options.setThousandSeparator(numOptions.getThousandSeparator());
			}
			if (option instanceof PieGenericOptions) {

				PieGenericOptions pieOptions = ((PieGenericOptions) option);

				options.setSlicingDistance(pieOptions.getSlicingDistance());
				options.setPieRadius(pieOptions.getPieRadius());
				options.setPieSliceDepth(pieOptions.getPieSliceDepth());
			}
		}
	}

	private void convertChartOption(RChartComponent component, ComponentRChartDefinition chart) {
		
		for( IComponentOptions option : chart.getOptions() ){
			
			if (option instanceof GenericNonPieOptions) {
				component.getOption().setxAxisName(((GenericNonPieOptions) option).getxAxisName());
				component.getOption().setyAxisName(((GenericNonPieOptions) option).getSYAxisName());
			}
			if (option instanceof GenericOptions) {
				component.setTitle(((GenericOptions) option).getCaption());
				component.getOption().setShowLabel(((GenericOptions) option).isShowLabel());
				component.getOption().setDynamicR(((GenericOptions) option).isDynamicLegend());
				
				if (chart.getRChartNature().getNature() == RChartNature.BAR_H || chart.getRChartNature().getNature() == RChartNature.BAR_V  ){ //component.getNature().
					component.getOption().setSeperationBar(((GenericOptions) option).isShowValues());
					component.getOption().setSeperationBar(((GenericOptions) option).isSeparationBar());
				}
				if(chart.getRChartNature().getNature() == RChartNature.HIST ){
					component.getOption().setDensity(((GenericOptions) option).isDensity());
					component.getOption().setBins(((GenericOptions) option).getBins());
				}
			}
			if (option instanceof PieGenericOptions) {
			}
		}
	}

	private String convertColor(Color color) {
		if (color == null) {
			return null;
		}
		return Integer.toHexString(color.getRGB());
	}

	//	The commented methods have moved to another class called ConvertDataset in bpm.fd.runtine.engine.models.datas
	
	/*private Dataset convertDataset(DataSet dataSet, Dictionary dictionary) throws Exception {

		Datasource resultDatasource = new Datasource();

		String datasourceName = dataSet.getDataSourceName();
		resultDatasource.setName(datasourceName);

		DataSource datasource = dictionary.getDatasource(datasourceName);

		String datasourceId = datasource.getOdaExtensionDataSourceId();

		Dataset resultDataset = new Dataset();
		resultDataset.setDatasource(resultDatasource);
		resultDataset.setName(dataSet.getName());

		if (datasourceId.equals("org.eclipse.birt.report.data.oda.jdbc")) {
			convertJdbcDatasourceDataset(resultDataset, dataSet, datasource);
		}
		else if (datasourceId.equals("bpm.metadata.birt.oda.runtime")) {
			convertMetadataDatasourceDataset(resultDataset, dataSet, datasource);
		}
		else if (datasourceId.equals("bpm.fm.oda.driver")) {
			convertKpiDatasourceDataset(resultDataset, dataSet, datasource);
			//FIXME Change this once we support kpi datasource/dataset correctly
			return resultDataset;
		}
		resultDataset = lookInVanillaForDatasourceDatasets(resultDatasource, resultDataset);

		return resultDataset;
	}
*/
	/*private void convertKpiDatasourceDataset(Dataset resultDataset, DataSet dataSet, DataSource datasource) {
		Datasource resultDatasource = resultDataset.getDatasource();
		resultDatasource.setType(DatasourceType.KPI);
		DatasourceKpi fmdt = new DatasourceKpi();
		resultDatasource.setObject(fmdt);

		for (Object prop : datasource.getProperties().keySet()) {
			String propName = (String) prop;
			if (propName.equals("fmLogin")) {
				fmdt.setUser(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("freemetricsPassword")) {
				fmdt.setPassword(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("fmGroupId")) {
				fmdt.setGroupId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("fmThemeId")) {
				fmdt.setThemeId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("fmUrl")) {
				fmdt.setUrl(datasource.getProperties().getProperty(propName));
			}
		}

		resultDataset.setRequest(dataSet.getQueryText());
		resultDataset.setMetacolumns(convertColumns(dataSet.getDataSetDescriptor().getColumnsDescriptors()));
		
	}*/

	/*private Dataset lookInVanillaForDatasourceDatasets(Datasource resultDatasource, Dataset resultDataset) throws Exception {

		for (Datasource datasource : datasources) {
			// the datasource exists
			if (datasource.getType() == resultDatasource.getType() && datasource.getObject().equals(resultDatasource.getObject())) {
				for (Dataset dataset : datasource.getDatasets()) {
					// the dataset already exists
					try {
						if (dataset.getRequest().equals(resultDataset.getRequest())) {
							return dataset;
						}
					} catch (Exception e) {
					}
				}
				// the dataset doesn't exist
				resultDataset.setDatasource(datasource);
				Dataset result = getVanillaRootApi().getVanillaPreferencesManager().addDataset(resultDataset);
				datasource.getDatasets().add(result);
				return result;
			}
		}

		// the datasource and dataset don't exist
		resultDatasource = getVanillaRootApi().getVanillaPreferencesManager().addDatasource(resultDatasource);
		resultDataset.setDatasource(resultDatasource);

		resultDatasource.getDatasets().add(resultDataset);

		datasources.add(resultDatasource);

		return getVanillaRootApi().getVanillaPreferencesManager().addDataset(resultDataset);
	}*/

	/*private void convertMetadataDatasourceDataset(Dataset resultDataset, DataSet dataSet, DataSource datasource) throws Exception {
		Datasource resultDatasource = resultDataset.getDatasource();
		resultDatasource.setType(DatasourceType.FMDT);
		DatasourceFmdt fmdt = new DatasourceFmdt();
		resultDatasource.setObject(fmdt);

		for (Object prop : datasource.getProperties().keySet()) {
			String propName = (String) prop;
			if (propName.equals("USER")) {
				fmdt.setUser(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("PASSWORD")) {
				fmdt.setPassword(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("REPOSITORY_ID")) {
				fmdt.setRepositoryId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("DIRECTORY_ITEM_ID")) {
				fmdt.setItemId(Integer.parseInt(datasource.getProperties().getProperty(propName)));
			}
			else if (propName.equals("BUSINESS_MODEL")) {
				fmdt.setBusinessModel(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("BUSINESS_PACKAGE")) {
				fmdt.setBusinessPackage(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("GROUP_NAME")) {
				fmdt.setGroupId(getVanillaRootApi().getVanillaSecurityManager().getGroupByName(datasource.getProperties().getProperty(propName)).getId());
			}
			else if (propName.equals("VANILLA_URL")) {
				fmdt.setUrl(datasource.getProperties().getProperty(propName));
			}
		}

		resultDataset.setRequest(dataSet.getQueryText());
		resultDataset.setMetacolumns(convertColumns(dataSet.getDataSetDescriptor().getColumnsDescriptors()));
	}*/

	private IVanillaAPI getVanillaRootApi() {
		if (vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return vanillaApi;
	}

	/*private void convertJdbcDatasourceDataset(Dataset resultDataset, DataSet dataSet, DataSource datasource) {
		Datasource resultDatasource = resultDataset.getDatasource();
		resultDatasource.setType(DatasourceType.JDBC);
		DatasourceJdbc jdbc = new DatasourceJdbc();
		jdbc.setFullUrl(true);
		resultDatasource.setObject(jdbc);

		for (Object prop : datasource.getProperties().keySet()) {
			String propName = (String) prop;
			if (propName.equals("odaDriverClass")) {
				jdbc.setDriver(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("odaURL")) {
				jdbc.setUrl(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("odaPassword")) {
				jdbc.setPassword(datasource.getProperties().getProperty(propName));
			}
			else if (propName.equals("odaUser")) {
				jdbc.setUser(datasource.getProperties().getProperty(propName));
			}
		}

		resultDataset.setRequest(dataSet.getQueryText());
		resultDataset.setMetacolumns(convertColumns(dataSet.getDataSetDescriptor().getColumnsDescriptors()));
	}*/

	/*private List<DataColumn> convertColumns(List<ColumnDescriptor> columnsDescriptors) {
		List<DataColumn> columns = new ArrayList<DataColumn>();

		for (ColumnDescriptor desc : columnsDescriptors) {
			DataColumn col = new DataColumn();
			col.setColumnLabel(desc.getColumnLabel());
			col.setColumnName(desc.getColumnName());
			col.setColumnTypeName(desc.getColumnTypeName());
			col.setColumnType(desc.getColumnType());
			columns.add(col);
		}

		return columns;
	}*/

	private void setComponentGeneralProperties(IComponentDefinition elem, Cell cell, DashboardComponent component) {
		component.setName(elem.getName());
		component.setComment(elem.getComment());
		component.setCssClass(elem.getCssClass());

		component.setTop(cell.getPosition().y);
		component.setLeft(cell.getPosition().x);
		component.setWidth(cell.getSize().x);
		component.setHeight(cell.getSize().y);
	
		component.setEventScript(convertScripts(elem.getEventScript()));
	}

	private DashboardPage convertPage(FolderPage page) throws Exception {
		DashboardPage resultPage = new DashboardPage();

		resultPage.setName(page.getName());
		resultPage.setLabel(page.getTitle());

		FdModel model = (FdModel) page.getContent().get(0);

		for (IBaseElement element : model.getContent()) {
			DashboardComponent component = convertComponent((Cell) element);
			if (component != null) {
				resultPage.addComponent(component);
			}
		}

		return resultPage;
	}

}
