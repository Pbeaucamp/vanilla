package bpm.fd.jsp.wrapper.helper;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import bpm.connection.manager.JdbcUrlHelper;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Element;
import bpm.fa.api.olap.Measure;
import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.FdProjectRepositoryDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentRChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
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
import bpm.fd.api.core.model.components.definition.datagrid.DataGridRenderer;
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
import bpm.fd.api.core.model.components.definition.jsp.D4COptions;
import bpm.fd.api.core.model.components.definition.jsp.FlourishOptions;
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
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelData;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.filters.ColumnFilter;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.core.Dashboard;
import bpm.fd.core.DashboardComponent;
import bpm.fd.core.DashboardPage;
import bpm.fd.core.IComponentData;
import bpm.fd.core.component.ButtonComponent;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.CommentComponent;
import bpm.fd.core.component.ContainerComponent;
import bpm.fd.core.component.ContentComponent;
import bpm.fd.core.component.ContentComponent.TabDisplay;
import bpm.fd.core.component.CubeElement;
import bpm.fd.core.component.CubeElement.CubeElementType;
import bpm.fd.core.component.CubeViewComponent;
import bpm.fd.core.component.D4CComponent;
import bpm.fd.core.component.DataVizComponent;
import bpm.fd.core.component.DatagridColumn;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.core.component.DivComponent;
import bpm.fd.core.component.DrillStackableCellComponent;
import bpm.fd.core.component.DynamicLabelComponent;
import bpm.fd.core.component.EventType;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.core.component.FlourishComponent;
import bpm.fd.core.component.GaugeComponent;
import bpm.fd.core.component.ImageComponent;
import bpm.fd.core.component.JspComponent;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.KpiComponent;
import bpm.fd.core.component.LabelComponent;
import bpm.fd.core.component.LinkComponent;
import bpm.fd.core.component.MapComponent;
import bpm.fd.core.component.MarkdownComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.ReportComponent;
import bpm.fd.core.component.StackableCellComponent;
import bpm.fd.core.component.kpi.KpiAggreg;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceKpi;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * 
 * @author Marc
 * 
 *         Convert a web dashboard to a FD Dashboard
 * 
 */
public class FdDashboardHelper {

	private FdProject resultDashboard;
	private File projectFolder;
	private Properties i18nProperties;
	private IVanillaAPI rootVanillaApi;

	public FdProject convertDashboard(Dashboard dashboard) throws Exception {

		String projectFolderPath = ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_FILES);
		if (!projectFolderPath.endsWith(File.separator)) {
			projectFolderPath += "/";
		}

		projectFolderPath += "VanillaDashboard/" + dashboard.getName() + "/";

		projectFolder = new File(projectFolderPath);
		projectFolder.mkdirs();

		FdProjectDescriptor descriptor = new FdProjectDescriptor();
		descriptor.setInternalApiDesignVersion(FdProjectDescriptor.API_DESIGN_VERSION);
		descriptor.setProjectName(dashboard.getName());

		FdProjectRepositoryDescriptor repositoryDescriptor = new FdProjectRepositoryDescriptor(descriptor);

		resultDashboard = new MultiPageFdProject(repositoryDescriptor);

		// create I18N file property
		i18nProperties = new Properties();

		// pages
		Folder folder = resultDashboard.getFdModel().getStructureFactory().createFolder("folder");
		resultDashboard.getFdModel().addToContent(folder);

		for (DashboardPage page : dashboard.getPages()) {

			FolderPage resultPage = resultDashboard.getFdModel().getStructureFactory().createFolderPage(page.getName());
			resultPage.setTitle(page.getLabel());

			FdModel model = convertPage(page);

			resultPage.addToContent(model);
			folder.addToContent(resultPage);
			((MultiPageFdProject) resultDashboard).addPageModel(model);
		}

		Integer maxTop = null;

		// root page
		for (DashboardComponent component : dashboard.getComponents()) {
			IStructureElement elem = convertComponent(component);

			if (elem instanceof Cell) {
				int bottom = ((Cell) elem).getPosition().y + ((Cell) elem).getSize().y;
				if (maxTop == null || maxTop < bottom) {
					maxTop = bottom;
				}
			}

			resultDashboard.getFdModel().addToContent(elem);
		}

		if (maxTop != null) {
			folder.setPosition(folder.getPosition().x, maxTop + 5);
		}

		// resources
		FileProperties fileProp = new FileProperties("components.properties", null, new File(projectFolderPath + "components.properties"));
		i18nProperties.store(new FileOutputStream(fileProp.getFile()), "");
		resultDashboard.addResource(fileProp);

		FileCSS cssFile = new FileCSS(dashboard.getName() + ".css", new File(projectFolderPath + dashboard.getName() + ".css"));
		Files.write(Paths.get(cssFile.getFile().getAbsolutePath()), dashboard.getCss().getBytes());
		resultDashboard.addResource(cssFile);

		HashMap<EventType, String> dashboardScripts = dashboard.getEventScript();
		resultDashboard.getFdModel().setEventScript(convertScripts(dashboardScripts));
		
		return resultDashboard;
	}

	private HashMap<ElementsEventType, String> convertScripts(HashMap<EventType, String> eventScripts) {
		HashMap<ElementsEventType, String> scripts = new HashMap<>();
		for (Entry<EventType, String> set : eventScripts.entrySet()) {
			scripts.put(convertEventType(set.getKey()), set.getValue());
		}
		return scripts;
	}

	private ElementsEventType convertEventType(EventType type) {
		switch (type) {
		case onAbort:
			return ElementsEventType.onAbort;
		case onBlur:
			return ElementsEventType.onBlur;
		case onChange:
			return ElementsEventType.onChange;
		case onClick:
			return ElementsEventType.onClick;
		case onDblClick:
			return ElementsEventType.onDblClick;
		case onFocus:
			return ElementsEventType.onFocus;
		case onKeyDown:
			return ElementsEventType.onKeyDown;
		case onKeyPress:
			return ElementsEventType.onKeyPress;
		case onKeyUp:
			return ElementsEventType.onKeyUp;
		case onLoad:
			return ElementsEventType.onLoad;
		case onMouseDown:
			return ElementsEventType.onMouseDown;
		case onMouseMove:
			return ElementsEventType.onMouseMove;
		case onMouseOut:
			return ElementsEventType.onMouseOut;
		case onMouseOver:
			return ElementsEventType.onMouseOver;
		case onMouseUp:
			return ElementsEventType.onMouseUp;
		case onSelect:
			return ElementsEventType.onSelect;
		case onSubmit:
			return ElementsEventType.onSubmit;
		case onUnload:
			return ElementsEventType.onUnload;

		default:
			break;
		}
		return null;
	}

	private FdModel convertPage(DashboardPage page) throws Exception {
		FdModel result = new FdModel(resultDashboard.getFdModel().getStructureFactory(), resultDashboard, page.getName());

		for (DashboardComponent component : page.getComponents()) {
			result.addToContent(convertComponent(component));
		}

		return result;
	}

	private IStructureElement convertContentComponent(ContentComponent component) throws Exception {
		Cell cell = null;
		if (component instanceof DivComponent) {
			cell = resultDashboard.getFdModel().getStructureFactory().createDivCell(component.getName(), 1);
		}
		else if (component instanceof StackableCellComponent) {
			cell = resultDashboard.getFdModel().getStructureFactory().createStackableCell(component.getName(), 1, 1);
			((StackableCell) cell).setType(findType(component.getTabDisplay()));
		}
		else if (component instanceof DrillStackableCellComponent) {
			cell = resultDashboard.getFdModel().getStructureFactory().createDrillDrivenStackableCell(component.getName(), 1);
			((DrillDrivenStackableCell) cell).setType(findType(component.getTabDisplay()));
		}

		cell.setCssClass(component.getCssClass());
		cell.setPosition(component.getLeft(), component.getTop());
		cell.setSize(component.getWidth(), component.getHeight());

		List<ContainerComponent> containers = component.getContainers();
		if (containers != null && !containers.isEmpty() && containers.get(0).getComponents() != null) {
			for (ContainerComponent container : containers) {
				for (DashboardComponent comp : container.getComponents()) {
					cell.addToContent(convertComponent(comp));
				}
			}
		}
		
		cell.setEventScript(convertScripts(component.getEventScript()));

		return cell;
	}

	private String findType(TabDisplay tabDisplay) {
		switch (tabDisplay) {
		case BOTTOM:
			return "bottom";
		case TOP:
			return "top";
		case LEFT:
			return "left";
		case RIGHT:
			return "right";

		default:
			break;
		}
		return "top";
	}

	private IStructureElement convertComponent(DashboardComponent component) throws Exception {
		Cell cell = resultDashboard.getFdModel().getStructureFactory().createCell(component.getName() + "_cell", 1, 1);

		cell.setPosition(component.getLeft(), component.getTop());
		cell.setSize(component.getWidth(), component.getHeight());

		if (component instanceof ChartComponent) {
			cell.addBaseElementToContent(convertChart((ChartComponent) component));
		}
		else if (component instanceof RChartComponent) {
			cell.addBaseElementToContent(convertChart((RChartComponent) component));
		}
		else if (component instanceof ButtonComponent) {
			cell.addBaseElementToContent(convertButton((ButtonComponent) component));
		}
		else if (component instanceof DatagridComponent) {
			cell.addBaseElementToContent(convertDatagrid((DatagridComponent) component));
		}
		else if (component instanceof FilterComponent) {
			cell.addBaseElementToContent(convertFilter((FilterComponent) component));
		}
		else if (component instanceof GaugeComponent) {
			cell.addBaseElementToContent(convertGauge((GaugeComponent) component));
		}
		else if (component instanceof CommentComponent) {
			cell.addBaseElementToContent(convertComment((CommentComponent) component));
		}
		else if (component instanceof LabelComponent) {
			cell.addBaseElementToContent(convertLabel((LabelComponent) component));
		}
		else if (component instanceof DynamicLabelComponent) {
			cell.addBaseElementToContent(convertDynamicLabel((DynamicLabelComponent) component));
		}
		else if (component instanceof ImageComponent) {
			cell.addBaseElementToContent(convertImage((ImageComponent) component));
		}
		else if (component instanceof ReportComponent) {
			cell.addBaseElementToContent(convertReport((ReportComponent) component));
		}
		else if (component instanceof KpiComponent) {
			cell.addBaseElementToContent(convertKpi((KpiComponent) component));
		}
		else if (component instanceof CubeViewComponent) {
			cell.addBaseElementToContent(convertCubeView((CubeViewComponent) component));
		}
		else if (component instanceof MarkdownComponent) {
			cell.addBaseElementToContent(convertMarkdown((MarkdownComponent) component));
		}
		else if (component instanceof LinkComponent) {
			cell.addBaseElementToContent(convertLink((LinkComponent) component));
		}
		else if (component instanceof JspComponent) {
			cell.addBaseElementToContent(convertJsp((JspComponent) component));
		}
		else if (component instanceof D4CComponent) {
			cell.addBaseElementToContent(convertD4C((D4CComponent) component));
		}
		else if (component instanceof FlourishComponent) {
			cell.addBaseElementToContent(convertFlourish((FlourishComponent) component));
		}
		else if (component instanceof MapComponent) {
			cell.addBaseElementToContent(convertMap((MapComponent) component));
		}
		else if (component instanceof DataVizComponent) {
			cell.addBaseElementToContent(convertDataviz((DataVizComponent) component));
		}
		else if (component instanceof KpiChartComponent) {
			cell.addBaseElementToContent(convertKpiComponent((KpiChartComponent) component));
		}
		else if (component instanceof ContentComponent) {
			cell = (Cell) convertContentComponent((ContentComponent) component);
			cell.setPosition(component.getLeft(), component.getTop());
			cell.setSize(component.getWidth(), component.getHeight());
		}

		IBaseElement element = cell.getContent().get(0);
		if (element instanceof IComponentDefinition) {
			resultDashboard.getDictionary().addComponent((IComponentDefinition) cell.getContent().get(0));

			IComponentDefinition c = (IComponentDefinition) cell.getContent().get(0);

			int i = 0;
			for (ComponentParameter p : c.getParameters()) {
				String origin = component.getParameters().get(i).getProvider().getName();

				cell.getConfig(c).setParameterOrigin(p, origin);
				i++;
			}
		}
		// else {
		// resultDashboard.getDictionary().addComponent((IComponentDefinition) ((Cell) ((DivCell)element).getContent().get(0)).getContent().get(0));
		// }

		return cell;
	}


	private IBaseElement convertDataviz(DataVizComponent component) {
		
		ComponentDataViz comp = new ComponentDataViz(component.getName(), resultDashboard.getDictionary());
		comp.setComment(component.getComment());
		comp.setCssClass(component.getCssClass());
		
		((DataVizOption)comp.getOptions(DataVizOption.class)).setDataprepId(component.getDatavizId());
		
		return comp;
	}

	//XXX
	private IBaseElement convertKpiComponent(KpiChartComponent component) throws Exception {
		if(component.getElement() instanceof KpiChart) {
			//put the dataset and columns info on the chartComponent
			KpiChart chart = (KpiChart) component.getElement();
			ChartComponent compo = chart.getOption();
			compo.setTitle(component.getTitle());
			compo.setName(component.getName());
			
			compo.setDataset(component.getDataset());
			
			//dummy aggreg to convert chart
			List<DataAggregation> result = new ArrayList<DataAggregation>();
			for(KpiAggreg agg : chart.getAggregs()) {
				DataAggregation dataAgg = new DataAggregation();
				result.add(dataAgg);
			}
			compo.setAggregations(result);
			
			ComponentChartDefinition def =  (ComponentChartDefinition) convertChart(compo);
			DataSet ds = def.getDatas().getDataSet();
			//set columns
			for(ColumnDescriptor desc : ds.getDataSetDescriptor().getColumnsDescriptors()) {
				if(desc.getColumnName().equals(chart.getLevel().getName())) {
					if(def.getDatas() instanceof ChartData) {
						ChartData data = (ChartData)def.getDatas();
						data.setCategorieFieldLabelIndex(desc.getColumnIndex());
						data.setGroupFieldIndex(desc.getColumnIndex());
						data.setOrderType(ChartOrderingType.CATEGORY_ASC);
					}
					else {
						MultiSerieChartData data = (MultiSerieChartData)def.getDatas();
						data.setCategorieFieldIndex(desc.getColumnIndex());
						data.setCategorieFieldLabelIndex(desc.getColumnIndex());
						data.setOrderType(ChartOrderingType.CATEGORY_ASC);
					}
					break;
				}
			}
			
			//create aggregs
			if(def.getDatas() instanceof MultiSerieChartData) {
				((MultiSerieChartData)def.getDatas()).setAggregations(convertKpiAggregToChartAggreg(chart, ds));
			}
			else {
				ChartData data = (ChartData)def.getDatas();
				//get column 
				for(ColumnDescriptor desc : ds.getDataSetDescriptor().getColumnsDescriptors()) {
					if(desc.getColumnName().equals(chart.getAggregs().get(0).getColumn())) {
						data.setValueFieldIndex(desc.getColumnIndex());
						break;
					}
				}
				
				data.setAggregator(chart.getAggregs().get(0).getOperator());
			}
			
			return def;
		}
 		return null;
	}

	private List<DataAggregation> convertKpiAggregToChartAggreg(KpiChart chart, DataSet ds) {
		List<DataAggregation> result = new ArrayList<DataAggregation>();
		int i = 0;
		for(KpiAggreg agg : chart.getAggregs()) {
			DataAggregation dataAgg = new DataAggregation();
			if(i > 7) {
				i = 0;
			}
			dataAgg.getColorsCode().remove(i);
			i++;
			dataAgg.setMeasureName(agg.getMetric().getName());
			
			dataAgg.setAggregator(agg.getOperator());
			for(ColumnDescriptor desc : ds.getDataSetDescriptor().getColumnsDescriptors()) {
				if(desc.getColumnName().equals(agg.getColumn())) {
					dataAgg.setValueFieldIndex(desc.getColumnIndex());
					break;
				}
			}
			
			dataAgg.setOrderType(ChartOrderingType.CATEGORY_ASC);
			ColumnFilter filter = new ColumnFilter();
			filter.setValue(agg.getMetric().getId()+"");
			for(ColumnDescriptor desc : ds.getDataSetDescriptor().getColumnsDescriptors()) {
				if(desc.getColumnName().equals(KpiAggreg.COLUMN_ID)) {
					filter.setColumnIndex(desc.getColumnIndex());
					break;
				}
			}
			dataAgg.addColumnFilter(filter);
			
			result.add(dataAgg);
		}
		return result;
	}

	private IBaseElement convertMap(MapComponent component) throws Exception {
		ComponentOsmMap map = new ComponentOsmMap(component.getName(), resultDashboard.getDictionary());
		map.setComment(component.getComment());
		map.setCssClass(component.getCssClass());

		VanillaMapOption options = (VanillaMapOption) map.getOptions(VanillaMapOption.class);
		options.setLegendLayout(component.getLegendLayout());
		options.setLegendOrientation(component.getLegendOrientation());
		options.setNumberFormat(component.getNumberFormat());
		options.setShowBaseLayer(component.isShowBaseLayer());
		options.setShowLegend(component.isShowLegend());

		VanillaMapData data = new VanillaMapData();
		data.setDataset(convertDataset(component));
		data.setMap(component.getMap());
		data.setMapId(component.getMapId());
		data.setValueFieldIndex(component.getValueFieldIndex() + 1);
		data.setZoneFieldIndex(component.getZoneFieldIndex() + 1);

		data.setSeries(component.getSeries());

		map.setData(data);

		return map;
	}

	private IBaseElement convertMarkdown(MarkdownComponent component) {
		ComponentMarkdown markdown = new ComponentMarkdown(component.getName(), resultDashboard.getDictionary());
		markdown.setComment(component.getComment());
		markdown.setCssClass(component.getCssClass());
		markdown.setDirectoryItemId(component.getItemId());
		return markdown;
	}

	private IBaseElement convertCubeView(CubeViewComponent component) {
		ComponentFaView cubeView = new ComponentFaView(component.getName(), resultDashboard.getDictionary());
		cubeView.setComment(component.getComment());
		cubeView.setCssClass(component.getCssClass());
		cubeView.setDirectoryItemId(component.getItemId());
	
		FaViewOption options = (FaViewOption) cubeView.getOptions(FaViewOption.class);
		options.setInteraction(component.isInteractive());
		options.setShowCubeFunctions(component.isShowCubeFunctions());
		options.setShowDimensions(component.isShowDimensions());
		
		List<Element> elements = new ArrayList<>();
		if (component.getElements() != null) {
			for (CubeElement el : component.getElements()) {
				if (el.isVisible()) {
					if (el.getType() == CubeElementType.DIMENSION) {
						elements.add(new Dimension(el.getName(), el.getUniqueName(), el.getCaption())); 
					}
					else if (el.getType() == CubeElementType.MEASURE) {
						elements.add(new Measure(el.getName(), el.getUniqueName(), el.getCaption(), null)); 
					}
				}
			}
		}
		options.setElements(elements);

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		if (component.getParameters() != null) {
			for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
				parameters.add(p);
			}
		}
		cubeView.defineParameter(parameters);

		return cubeView;
	}

	private IBaseElement convertReport(ReportComponent component) {
		ComponentReport report = new ComponentReport(component.getName(), resultDashboard.getDictionary());
		report.setComment(component.getComment());
		report.setCssClass(component.getCssClass());
		report.setDirectoryItemId(component.getItemId());

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		
		if(component.getParameters() != null && !component.getParameters().isEmpty()) {
			for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
				parameters.add(p);
			}
		}
		report.defineParameter(parameters);

		return report;
	}

	private IBaseElement convertKpi(KpiComponent component) {
		ComponentKpi report = new ComponentKpi(component.getName(), resultDashboard.getDictionary());
		report.setComment(component.getComment());
		report.setCssClass(component.getCssClass());
		report.setDirectoryItemId(component.getItemId());

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		
		if(component.getParameters() != null && !component.getParameters().isEmpty()) {
			for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
				parameters.add(p);
			}
		}
		report.defineParameter(parameters);

		return report;
	}

	private IBaseElement convertImage(ImageComponent component) throws Exception {
		ComponentPicture picture = new ComponentPicture(component.getName(), resultDashboard.getDictionary());
		picture.setComment(component.getComment());
		picture.setCssClass(component.getCssClass());
		picture.setName(component.getName());

		String compUrl = null;

		if (component.getUrl() != null) {
			compUrl = component.getUrl().substring(component.getUrl().lastIndexOf("/") + 1, component.getUrl().length());
		}
		else {
			compUrl = "0_no_image.png";
		}

		picture.setPictureUrl(compUrl);

		// create the resource file
		FileImage imageFile = new FileImage(compUrl, new File(projectFolder.getAbsolutePath() + compUrl));
		try {
			String urlToRead = component.getUrl();
			if (!component.getUrl().startsWith("/")) {
				urlToRead = "/" + urlToRead;
			}

			URL url = new URL(ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_URL) + "/" + urlToRead.replace("webapps", ".."));

			FileUtils.copyURLToFile(url, imageFile.getFile());
		} catch (Exception e) {

			String noImagePath = ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_IMAGE_FOLDER) + "/" + "0_no_image.png";
			FileUtils.copyFile(new File(noImagePath), imageFile.getFile());
		}

		// Files.write(Paths.get(imageFile.getFile().getAbsolutePath()), IOUtils.toString(url.openStream()).getBytes());
		resultDashboard.addResource(imageFile);

		// FileCSS cssFile = new FileCSS(dashboard.getName() + ".css", new File(projectFolderPath + dashboard.getName() + ".css"));
		// Files.write(Paths.get(cssFile.getFile().getAbsolutePath()), dashboard.getCss().getBytes());
		// resultDashboard.addResource(cssFile);

		return picture;
	}

	private IBaseElement convertLabel(LabelComponent component) {
		bpm.fd.api.core.model.components.definition.text.LabelComponent label = new bpm.fd.api.core.model.components.definition.text.LabelComponent(component.getName(), resultDashboard.getDictionary());
		label.setComment(component.getComment() != null ? component.getComment() : "");
		label.setCssClass(component.getCssClass());
		label.setText(component.getTitle());
		label.setName(component.getName());

		label.setEventScript(convertScripts(component.getEventScript()));
		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return label;
	}

	private IBaseElement convertDynamicLabel(DynamicLabelComponent component) throws Exception {
		bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelComponent label = new bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelComponent(component.getName(), resultDashboard.getDictionary());
		label.setComment(component.getComment() != null ? component.getComment() : "");
		label.setCssClass(component.getCssClass());
		label.setLabel(component.getTitle());
		label.setName(component.getName());

		label.setEventScript(convertScripts(component.getEventScript()));
		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");
		
		// data
		DynamicLabelData data = new DynamicLabelData();
		if(component.getColumnValueIndex() != null) {
			data.setColumnValueIndex(component.getColumnValueIndex() + 1);
		}
		data.setDataSet(convertDataset(component));

		label.setComponentDatas(data);

		return label;
	}

	private IBaseElement convertComment(CommentComponent component) {
		ComponentComment comment = new ComponentComment(component.getName(), resultDashboard.getDictionary());
		comment.setComment(component.getTitle());
		comment.setCssClass(component.getCssClass());

		CommentOptions options = (CommentOptions) comment.getOptions(CommentOptions.class);
		options.setAllowAddComments(component.isAllowAddComments());
		options.setShowComments(component.isShowComments());
		options.setLimit(component.getLimit());
		options.setLimitComment(component.isLimitComment());
		options.setValidation(component.isValidation());

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		if (component.getParameters() != null) {
			for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
				parameters.add(p);
			}
		}
		comment.setParameters(parameters);

		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return comment;
	}

	private IBaseElement convertGauge(GaugeComponent component) throws Exception {
		ComponentGauge gauge = new ComponentGauge(component.getName(), resultDashboard.getDictionary());

		gauge.setComment(component.getComment());
		gauge.setCssClass(component.getCssClass());
		gauge.setName(component.getName());

		GaugeOptions options = (GaugeOptions) gauge.getOptions(GaugeOptions.class);
		options.setBgAlpha(component.getBgAlpha());
		options.setBgColor(convertToColor(component.getBgColor()));
		options.setBulb(component.isBulb());
		options.setColorBadValue(convertToColor(component.getColorBadValue()));
		options.setColorGoodValue(convertToColor(component.getColorGoodValue()));
		options.setColorMediumValue(convertToColor(component.getColorMediumValue()));
		options.setInnerRadius(component.getInnerRadius());
		options.setOuterRadius(component.getOuterRadius());
		options.setShowValues(component.isShowValues());
		options.setStartAngle(component.getStartAngle());
		options.setStopAngle(component.getStopAngle());

		ComplexGaugeDatas gaugeDatas = new ComplexGaugeDatas();
		gaugeDatas.setDataSet(convertDataset(component));

		gaugeDatas.setIndexValue(component.getValueField() + 1);

		gaugeDatas.setManualRange(!component.isMinMaxFromField());
		if (component.isMinMaxFromField()) {
			gaugeDatas.setIndexMin(component.getMin().intValue() + 1);
			gaugeDatas.setIndexMax(component.getMax().intValue() + 1);
		}
		else {
			gaugeDatas.setMinValue(component.getMin());
			gaugeDatas.setMaxValue(component.getMax());
		}

		gaugeDatas.setIndexTolerance(component.getTolerance());

		gaugeDatas.setUseFieldForTarget(component.isTargetFromField());
		if (component.getTarget() == null) {
			gaugeDatas.setUseTarget(false);
		}
		else {
			gaugeDatas.setUseTarget(true);
			if (component.isTargetFromField()) {
				gaugeDatas.setTargetIndex(component.getTarget().intValue() + 1);
			}
			else {
				gaugeDatas.setTargetValue(component.getTarget());
			}
		}

		gaugeDatas.setUseExpected(component.isThresholdFromField());
		if (component.isThresholdFromField()) {
			gaugeDatas.setIndexMinSeuil(component.getMinThreshold().intValue() + 1);
			gaugeDatas.setIndexMaxSeuil(component.getMaxThreshold().intValue() + 1);
		}
		else {
			gaugeDatas.setIndexMinSeuil(component.getMinThreshold().intValue());
			gaugeDatas.setIndexMaxSeuil(component.getMaxThreshold().intValue());
		}

		gauge.setComponentDatas(gaugeDatas);

		return gauge;
	}

	private IBaseElement convertFilter(FilterComponent component) throws Exception {
		ComponentFilterDefinition filter = new ComponentFilterDefinition(component.getName(), resultDashboard.getDictionary());

		filter.setComment(component.getComment());
		filter.setCssClass(component.getCssClass());
		filter.setLabel(component.getTitle());
		filter.setName(component.getName());
		filter.setRenderer(FilterRenderer.getRenderer(component.getRenderer().getRendererStyle()));

		// data
		FilterData data = new FilterData();
		if(component.getColumnLabelIndex() != null) {
			data.setColumnLabelIndex(component.getColumnLabelIndex() + 1);
		}
		if(component.getColumnOrderIndex() != null) {
			data.setColumnOrderIndex(component.getColumnOrderIndex() + 1);
		}
		if(component.getColumnValueIndex() != null) {
			data.setColumnValueIndex(component.getColumnValueIndex() + 1);
		}
		data.setDataSet(convertDataset(component));

		filter.setComponentDatas(data);

		// option
		FilterOptions option = (FilterOptions) filter.getOptions(FilterOptions.class);
		option.setDefaultValue(component.getDefaultValue());
		option.setHidden(component.isHidden());
		option.setInitParameterWithFirstValue(component.isInitParameterWithFirstValue());
		option.setRequired(component.isRequired());
		option.setSelectFirstValue(component.isSelectFirstValue());
		option.setSubmitOnChange(component.isSubmitOnChange());

		if (FilterRenderer.getRenderer(component.getRenderer().getRendererStyle()) == FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX)) {
			DropDownOptions ddoption = (DropDownOptions) filter.getOptions(DropDownOptions.class);
			ddoption.setSize(component.getSize());
		}
		else if (FilterRenderer.getRenderer(component.getRenderer().getRendererStyle()) == FilterRenderer.getRenderer(FilterRenderer.MENU)) {
			MenuOptions moption = (MenuOptions) filter.getOptions(MenuOptions.class);
			moption.setIsVertical(component.isVertical());
			moption.setSize(component.getSize());
		}
		else if (FilterRenderer.getRenderer(component.getRenderer().getRendererStyle()) == FilterRenderer.getRenderer(FilterRenderer.SLIDER)) {
			SliderOptions soption = (SliderOptions) filter.getOptions(SliderOptions.class);
			soption.setAutoRun(component.isAutoRun());
			soption.setBarColor(component.getBarColor());
			soption.setDelay(component.getDelay());
			soption.setSliderColor(component.getSliderColor());
		}

		filter.setEventScript(convertScripts(component.getEventScript()));
		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return filter;
	}

	private IBaseElement convertDatagrid(DatagridComponent component) throws Exception {
		ComponentDataGrid dataGrid = new ComponentDataGrid(component.getName(), resultDashboard.getDictionary());

		dataGrid.setComment(component.getComment());
		dataGrid.setCssClass(component.getCssClass());
		dataGrid.setName(component.getName());
		dataGrid.setRenderer(DataGridRenderer.getRenderer(DataGridRenderer.RENDERER_VERTICAL));

		// data
		DataGridData data = new DataGridData();
		data.setOrderType(component.getOrderType());
		data.setOrderFieldPosition(component.getOrderFieldIndex() + 1);
		data.setDataSet(convertDataset(component));

		dataGrid.setComponentDatas(data);

		dataGrid.setLayout(convertLayout(component.getColumns()));

		// option
		DataGridOptions options = (DataGridOptions) dataGrid.getOptions(DataGridOptions.class);
		// options.setDrillHeader(drillHeader);
		// options.setDrillText(drillText);
		options.setHeadersVisible(component.isHeadersVisible());
		options.setIncludeTotal(component.isIncludeTotal());

		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return dataGrid;
	}

	private DataGridLayout convertLayout(List<DatagridColumn> columns) {
		DataGridLayout layout = new DataGridLayout();

		if (columns != null) {
			List<DataGridColumnOption> options = new ArrayList<DataGridColumnOption>();
			for (DatagridColumn column : columns) {
				options.add(new DataGridColumnOption(column.getName(), column.getCustomName(), column.isVisible() ? DataGridCellType.Visible : DataGridCellType.Hidden, column.isGroup(), column.getAggregation()));
			}
			layout.setColumnOptions(options);
		}

		return layout;
	}

	private IBaseElement convertButton(ButtonComponent component) {
		ComponentButtonDefinition button = new ComponentButtonDefinition(component.getName(), resultDashboard.getDictionary());
		button.setComment(component.getComment());
		button.setCssClass(component.getCssClass());

		ButtonOptions options = (ButtonOptions) button.getOptions(ButtonOptions.class);
		options.setLabel(component.getTitle());

		button.setEventScript(convertScripts(component.getEventScript()));
		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return button;
	}

	private IBaseElement convertLink(LinkComponent component) {
		ComponentLink link = new ComponentLink(component.getName(), resultDashboard.getDictionary());
		link.setComment(component.getComment());
		link.setCssClass(component.getCssClass());

		LinkOptions options = (LinkOptions) link.getOptions(LinkOptions.class);
		options.setLabel(component.getTitle());
		options.setUrl(component.getUrl());

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
			ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
			parameters.add(p);
		}
		link.setParameters(parameters);

		link.setEventScript(convertScripts(component.getEventScript()));
		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return link;
	}

	private IBaseElement convertJsp(JspComponent component) {
		ComponentJsp jsp = new ComponentJsp(component.getName(), resultDashboard.getDictionary());
		jsp.setComment(component.getComment());
		jsp.setCssClass(component.getCssClass());
		jsp.setJspUrl(component.getUrl());

		JspOptions options = (JspOptions) jsp.getOptions(JspOptions.class);
		options.setBorder_width(component.showBorder() ? 1 : 0);
		options.setHeight(component.getHeight() - 25);
		options.setWidth(component.getWidth() - 10);

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
			ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
			parameters.add(p);
		}
		jsp.setParameters(parameters);

		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return jsp;
	}

	private IBaseElement convertD4C(D4CComponent component) {
		ComponentD4C d4c = new ComponentD4C(component.getName(), resultDashboard.getDictionary());
		d4c.setComment(component.getComment());
		d4c.setCssClass(component.getCssClass());
		d4c.setUrl(component.getUrl());

		D4COptions options = (D4COptions) d4c.getOptions(D4COptions.class);
		options.setBorder_width(0);
		options.setHeight(component.getHeight() - 25);
		options.setWidth(component.getWidth() - 10);

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		if (component.getParameters() != null) {
			for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
				parameters.add(p);
			}
		}
		d4c.setParameters(parameters);

		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return d4c;
	}

	private IBaseElement convertFlourish(FlourishComponent component) {
		ComponentFlourish flourish = new ComponentFlourish(component.getName(), resultDashboard.getDictionary());
		flourish.setComment(component.getComment());
		flourish.setCssClass(component.getCssClass());
		flourish.setUrl(component.getUrl());

		FlourishOptions options = (FlourishOptions) flourish.getOptions(FlourishOptions.class);
		options.setBorder_width(0);
		options.setHeight(component.getHeight() - 25);
		options.setWidth(component.getWidth() - 10);

		List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
		if (component.getParameters() != null) {
			for (bpm.fd.core.ComponentParameter param : component.getParameters()) {
				ReportComponentParameter p = new ReportComponentParameter(param.getName(), param.getIndex());
				parameters.add(p);
			}
		}
		flourish.setParameters(parameters);

		// i18nProperties.put(component.getName() + ".text", component.getTitle() != null ? component.getTitle() : "");

		return flourish;
	}

	private IBaseElement convertChart(ChartComponent component) throws Exception {
		ComponentChartDefinition chart = new ComponentChartDefinition(component.getName(), resultDashboard.getDictionary());

		chart.setComment(component.getComment());
		chart.setCssClass(component.getCssClass());
		chart.setNature(component.getNature());
		if (component.getNature().equals(ChartNature.getNature(ChartNature.PIE)) || component.getNature().equals(ChartNature.getNature(ChartNature.PIE_3D))) {
			GenericOptions opt = new GenericOptions();
			chart.setComponentOption(opt);
		}
		else {
			GenericOptions opt = new PieGenericOptions();
			chart.setComponentOption(opt);
		}

		// multiserie
		if (component.getAggregations().size() > 1 || component.getSubGroupFieldIndex() != null || chart.getNature().isMultiSerie()) {
			
			if(chart.getNature().getNature() == ChartNature.BAR) {
				chart.setNature(ChartNature.getNature(ChartNature.MSBAR2D));
			}
			else if(chart.getNature().getNature() == ChartNature.COLUMN) {
				chart.setNature(ChartNature.getNature(ChartNature.COLUMN_MULTI));
			}
			
			MultiSerieChartData data = new MultiSerieChartData();
			data.setCategorieFieldIndex(component.getGroupFieldIndex() + 1);
			data.setCategorieFieldLabelIndex(component.getGroupFieldLabelIndex() + 1);
			if (component.getSubGroupFieldIndex() != null) {
				data.setSerieFieldIndex(component.getSubGroupFieldIndex() + 1);
			}
			data.setOrderType(component.getOrderType());
			data.setDataSet(convertDataset(component));
			data.setAggregations(component.getAggregations());
			data.setColorPalette(component.getColorPalette());

			chart.setComponentDatas(data);
		}
		else {
			ChartData data = new ChartData();
			data.setGroupFieldIndex(component.getGroupFieldIndex() + 1);
			data.setCategorieFieldLabelIndex(component.getGroupFieldLabelIndex() + 1);
			data.setOrderType(component.getOrderType());
			data.setDataSet(convertDataset(component));
			data.setColorPalette(component.getColorPalette());

			// use the aggregation for value field and aggregator
			DataAggregation aggreg = component.getAggregations().get(0);
			data.setAggregator(aggreg.getAggregator());
			data.setValueFieldIndex(aggreg.getValueFieldIndex());

			chart.setComponentDatas(data);
		}

		// drill
		if (component.getChartDrill() != null) {
			chart.getDrillDatas().setCategorieAsParameterValue(component.getChartDrill().isCategorieAsParameter());
			chart.getDrillDatas().setDrillable(true);
			chart.getDrillDatas().setKeepColor(component.getChartDrill().isKeepColor());
			// chart.getDrillDatas().setTargetModelPage(targetModelPage);
			chart.getDrillDatas().setTypeTarget(component.getChartDrill().getTypeTarget());
			chart.getDrillDatas().setUrl(component.getChartDrill().getUrl());
			chart.getDrillDatas().setZoomHeight(component.getChartDrill().getZoomHeight());
			chart.getDrillDatas().setZoomWidth(component.getChartDrill().getZoomWidth());
		}

		// options
		convertChartOption(component, chart);

		return chart;
	}
	
	// Methode Basée sur ConvertChart de ChartComponent 
	private IBaseElement convertChart(RChartComponent component) throws Exception {

		ComponentRChartDefinition chart = new ComponentRChartDefinition(component.getName() , resultDashboard.getDictionary()  );
		chart.setComment(component.getComment() );
		chart.setNature(component.getNature() );
		
		// A revoir si vraiment besoin d' option de PIE ou pas 
		if(component.getNature().equals( RChartNature.PIE )){
			GenericOptions opt = new GenericOptions();
			chart.setComponentOption(opt);
		}
		else{
			GenericOptions opt = new GenericOptions();
			chart.setComponentOption(opt);
		}
		
		ChartData data = new ChartData();
		data.setGroupFieldIndex(component.getGroupFieldIndex() + 1);
		data.setGroupFieldName(component.getGroupFieldLabel());
		data.setAxeXField( component.getAxeXField()+1);
		data.setAxeYField( component.getAxeYField()+1);
		data.setAxeXFieldName(component.getAxeXFieldLabel() );
		data.setAxeYFieldName(component.getAxeYFieldLabel());
		data.setDataSet(convertDataset(component));
		if(component.getNature().getNature() == RChartNature.CORRELATION ){
			data.setSelectedColumns( component.getSelectedColumList() );
			data.setSelectedColumName(  component.getSelectedColumName() );
		}
		else{
			data.setSelectedColumns( null );
			data.setSelectedColumName(null);
		}
		
		chart.setComponentDatas(data);
		// options
		convertChartOption(component, chart);
		
		return chart;
	}

	private void convertChartOption(RChartComponent component, ComponentRChartDefinition chart) {
		// TODO Auto-generated method stub
		
		for( IComponentOptions option : chart.getOptions() ){
			
			if (option instanceof GenericNonPieOptions) {

				((GenericNonPieOptions) option).setxAxisName(component.getOption().getxAxisName());
				((GenericNonPieOptions) option).setSYAxisName(component.getOption().getyAxisName());

			}
			if (option instanceof GenericOptions) {
				((GenericOptions) option).setCaption(component.getTitle());
				((GenericOptions) option).setShowLabel(component.getOption().isShowLabel());
				((GenericOptions) option).setDynamicLegend(component.getOption().isDynamicR());
				// i18n
				i18nProperties.put(component.getName() + ".caption", component.getTitle() != null ? component.getTitle() : "");
				// Si c'est un bar ajoute l'option ..
				if(  component.getNature().getNature() == RChartNature.BAR_H || component.getNature().getNature() == RChartNature.BAR_V  ){ //component.getNature().
					((GenericOptions) option).setShowValues( component.getOption().isSeperationBar() );
					((GenericOptions) option).setSeparationBar( component.getOption().isSeperationBar()  );
				}
				if(  component.getNature().getNature() == RChartNature.HIST ){ 
					((GenericOptions) option).setDensity( component.getOption().isDensity());
					((GenericOptions) option).setBins( component.getOption().getBins()  );
				}
			}
			if (option instanceof PieGenericOptions) {
			}
		
			
		}
		
		
	}

	private void convertChartOption(ChartComponent component, ComponentChartDefinition chart) {
		for (IComponentOptions option : chart.getOptions()) {
			if (option instanceof GenericNonPieOptions) {
				((GenericNonPieOptions) option).setPlaceValuesInside(component.getOption().isPlaceValuesInside());
				((GenericNonPieOptions) option).setPYAxisName(component.getOption().getPYAxisName());
				((GenericNonPieOptions) option).setRotateLabels(component.getOption().isRotateLabels());
				((GenericNonPieOptions) option).setRotateValues(component.getOption().isRotateValues());
				((GenericNonPieOptions) option).setRotateYAxisName(component.getOption().isRotateYAxisName());
				((GenericNonPieOptions) option).setSlantLabels(component.getOption().isSlantLabels());
				((GenericNonPieOptions) option).setSYAxisName(component.getOption().getSYAxisName());
				((GenericNonPieOptions) option).setxAxisName(component.getOption().getxAxisName());
			}
			if (option instanceof GenericOptions) {
				((GenericOptions) option).setBaseFontColor(convertToColor(component.getOption().getBaseFontColor()));
				((GenericOptions) option).setBaseFontSize(component.getOption().getBaseFontSize());
				((GenericOptions) option).setBgAlpha(component.getOption().getBgAlpha());
				((GenericOptions) option).setBgColor(convertToColor(component.getOption().getBgColor()));
				((GenericOptions) option).setBgSWFAlpha(component.getOption().getBgSWFAlpha());
				((GenericOptions) option).setBorderColor(convertToColor(component.getOption().getBorderColor()));
				((GenericOptions) option).setBorderThickness(component.getOption().getBorderThickness());
				((GenericOptions) option).setCaption(component.getTitle());
				((GenericOptions) option).setDynamicLegend(component.getOption().isDynamicLegend());
				((GenericOptions) option).setExportEnable(component.getOption().isExportEnable());
				((GenericOptions) option).setLabelSize(component.getOption().getLabelSize());
				((GenericOptions) option).setMultiLineLabels(component.getOption().isMultiLineLabels());
				((GenericOptions) option).setShowBorder(component.getOption().isShowBorder());
				((GenericOptions) option).setShowLabel(component.getOption().isShowLabel());
				((GenericOptions) option).setShowValues(component.getOption().isShowValues());
				((GenericOptions) option).setSubCaption(component.getOption().getSubCaption());

				// i18n
				i18nProperties.put(component.getName() + ".caption", component.getTitle() != null ? component.getTitle() : "");
				i18nProperties.put(component.getName() + ".subCaption", component.getOption().getSubCaption() != null ? component.getOption().getSubCaption() : "");

			}
			if (option instanceof LineCombinationOption) {
				((LineCombinationOption) option).setLineSerieName(component.getOption().getLineSerieName());
			}
			if (option instanceof NumberFormatOptions) {
				((NumberFormatOptions) option).setDecimals(component.getOption().getDecimals());
				((NumberFormatOptions) option).setDecimalSeparator(component.getOption().getDecimalSeparator());
				((NumberFormatOptions) option).setForceDecimal(component.getOption().isForceDecimal());
				((NumberFormatOptions) option).setFormatNumber(component.getOption().isFormatNumber());
				((NumberFormatOptions) option).setFormatNumberScale(component.getOption().isFormatNumberScale());
				((NumberFormatOptions) option).setNumberPrefix(component.getOption().getNumberPrefix());
				((NumberFormatOptions) option).setNumberSuffix(component.getOption().getNumberSuffix());
				((NumberFormatOptions) option).setThousandSeparator(component.getOption().getThousandSeparator());
			}
			if (option instanceof PieGenericOptions) {
				((PieGenericOptions) option).setSlicingDistance(component.getOption().getSlicingDistance());
				((PieGenericOptions) option).setPieRadius(component.getOption().getPieRadius());
				((PieGenericOptions) option).setPieSliceDepth(component.getOption().getPieSliceDepth());
			}
		}

	}

	private Color convertToColor(String stringColor) {
		if (stringColor == null) {
			return null;
		}
		return Color.decode("#" + stringColor);
	}

	private DataSet convertDataset(IComponentData component) throws Exception {

		DataSet result = null;

		if (component.getDataset().getDatasource().getType() == DatasourceType.JDBC) {
			result = convertDatasetJdbc(component);
		}
		else if (component.getDataset().getDatasource().getType() == DatasourceType.FMDT) {
			result = convertDatasetFmdt(component);
		}
		else if (component.getDataset().getDatasource().getType() == DatasourceType.KPI) {
			result = convertDatasetKpi(component);
		}

		return result;
	}

	private DataSet convertDatasetKpi(IComponentData component) throws Exception {
		DatasourceKpi fmdt = (DatasourceKpi) component.getDataset().getDatasource().getObject();
		
		Properties publicProps = new Properties();
		Properties privateProps = new Properties();
		
		if(fmdt.getUrl() == null || fmdt.getUrl().isEmpty()) {
			fmdt.setUrl(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
		}
		
		publicProps.put("fmUrl", fmdt.getUrl());
		publicProps.put("fmLogin", fmdt.getUser());
		publicProps.put("freemetricsPassword", fmdt.getPassword());
		publicProps.put("fmGroupId", fmdt.getGroupId()+"");
		publicProps.put("fmThemeId", fmdt.getThemeId()+"");

		DataSource resultDatasource = new DataSource(resultDashboard.getDictionary(), component.getDataset().getDatasource().getName(), "bpm.fm.oda.driver", "bpm.fm.oda.driver", publicProps, privateProps);

		DataSet result = new DataSet(component.getDataset().getName(), "bpm.fm.oda.driver.dataSet", "bpm.fm.oda.driver", new Properties(), new Properties(), component.getDataset().getRequest(), resultDatasource);

		result.buildDescriptor(resultDatasource);

		try {
			resultDashboard.getDictionary().addDataSource(resultDatasource);
		} catch (Exception e) {
		}
		try {
			resultDashboard.getDictionary().addDataSet(result);
		} catch (Exception e) {
		}

		return result;
	}

	private DataSet convertDatasetFmdt(IComponentData component) throws Exception {
		DatasourceFmdt fmdt = (DatasourceFmdt) component.getDataset().getDatasource().getObject();

		Properties publicProps = new Properties();
		Properties privateProps = new Properties();

		publicProps.put("URL", fmdt.getUrl());
		publicProps.put("USER", fmdt.getUser());
		publicProps.put("PASSWORD", fmdt.getPassword());
		publicProps.put("VANILLA_URL", fmdt.getUrl());
		publicProps.put("REPOSITORY_ID", fmdt.getRepositoryId() + "");
		publicProps.put("DIRECTORY_ITEM_ID", fmdt.getItemId() + "");
		publicProps.put("BUSINESS_MODEL", fmdt.getBusinessModel());
		publicProps.put("BUSINESS_PACKAGE", fmdt.getBusinessPackage());
		publicProps.put("GROUP_NAME", getVanillaRootApi().getVanillaSecurityManager().getGroupById(fmdt.getGroupId()).getName());
		publicProps.put("CONNECTION_NAME", "Default");
		publicProps.put("IS_ENCRYPTED", false + "");

		DataSource resultDatasource = new DataSource(resultDashboard.getDictionary(), component.getDataset().getDatasource().getName(), "bpm.metadata.birt.oda.runtime", "bpm.metadata.birt.oda.runtime", publicProps, privateProps);

		DataSet result = new DataSet(component.getDataset().getName(), "bpm.metadata.birt.oda.runtime.dataSet", "bpm.metadata.birt.oda.runtime", new Properties(), new Properties(), component.getDataset().getRequest(), resultDatasource);

		result.buildDescriptor(resultDatasource);

		try {
			resultDashboard.getDictionary().addDataSource(resultDatasource);
		} catch (Exception e) {
		}
		try {
			resultDashboard.getDictionary().addDataSet(result);
		} catch (Exception e) {
		}

		return result;
	}

	private DataSet convertDatasetJdbc(IComponentData component) throws Exception {
		DatasourceJdbc jdbc = (DatasourceJdbc) component.getDataset().getDatasource().getObject();

		Properties publicProps = new Properties();
		Properties privateProps = new Properties();

		publicProps.put("odaDriverClass", jdbc.getDriver());
		if (jdbc.getFullUrl()) {
			publicProps.put("odaURL", jdbc.getUrl());
		}
		else {
			publicProps.put("odaURL", JdbcUrlHelper.getJdbcUrl(jdbc.getHost(), jdbc.getPort(), jdbc.getDatabaseName(), jdbc.getDriver()));
		}
		publicProps.put("odaPassword", jdbc.getPassword());
		publicProps.put("odaUser", jdbc.getUser());

		DataSource resultDatasource = new DataSource(resultDashboard.getDictionary(), component.getDataset().getDatasource().getName(), "org.eclipse.birt.report.data.oda.jdbc", "org.eclipse.birt.report.data.oda.jdbc", publicProps, privateProps);

		DataSet result = new DataSet(component.getDataset().getName(), "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet", "org.eclipse.birt.report.data.oda.jdbc", new Properties(), new Properties(), component.getDataset().getRequest(), resultDatasource);

		result.buildDescriptor(resultDatasource);

		try {
			resultDashboard.getDictionary().addDataSource(resultDatasource);
		} catch (Exception e) {
		}
		try {
			resultDashboard.getDictionary().addDataSet(result);
		} catch (Exception e) {
		}

		return result;
	}

	private IVanillaAPI getVanillaRootApi() {
		if (rootVanillaApi == null) {
			rootVanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return rootVanillaApi;
	}

}
