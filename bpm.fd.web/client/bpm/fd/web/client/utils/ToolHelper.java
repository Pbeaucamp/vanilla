package bpm.fd.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.component.ComponentType;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.images.DashboardImage;
import bpm.fd.web.client.panels.ToolBox;
import bpm.fd.web.client.panels.ToolBoxCategory;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;

import com.google.gwt.resources.client.ImageResource;

public class ToolHelper {
	
	public static final String TYPE_TOOL = "ComponentType";
	
	private static final ComponentType[] WIDGETS = { ComponentType.CHART, ComponentType.FILTER, ComponentType.DATA_GRID, ComponentType.GAUGE, ComponentType.REPORT,
		ComponentType.DASHLET, ComponentType.OLAP_VIEW, ComponentType.MAP, ComponentType.MARKDOWN, ComponentType.KPI_CHART, ComponentType.KPI, ComponentType.FLOURISH, ComponentType.DATAVIZ, ComponentType.D4C};

	private static final ComponentType[] TEXT = { ComponentType.LABEL, ComponentType.DYNAMIC_LABEL, ComponentType.IMAGE, ComponentType.URL };
	
	private static final ComponentType[] CONTROLS = { ComponentType.BUTTON, ComponentType.COMMENT/*, ComponentType.CLOCK*/ };
	
	private static final ComponentType[] CONTAINERS = { ComponentType.DIV, ComponentType.STACKABLE_CELL, ComponentType.DRILL_STACKABLE_CELL };
	
	private static final ComponentType[] RCHART = {ComponentType.RCHART} ;

	public static final String CAT_WIDGETS = "Widgets";
	public static final String CAT_TEXT = "Text";
	public static final String CAT_CONTROLS = "Controls";
	public static final String CAT_CONTAINERS = "Containers";
	public static final String CAT_RCHART = "Chart R";

	public enum Category { 
		WIDGETS(CAT_WIDGETS), 
		TEXT(CAT_TEXT), 
		CONTROLS(CAT_CONTROLS),
		CONTAINERS(CAT_CONTAINERS),
		RCHART(CAT_RCHART) ;
		
		private String name;
		
		private Category(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	};

	public static List<StackNavigationPanel> createCategories(CollapsePanel collapsePanel) {
		List<StackNavigationPanel> cats = new ArrayList<>();
		boolean first = true;
		for (Category category : Category.values()) {
			String title = category.getName();
			switch (category) {
			case WIDGETS:
				title = Labels.lblCnst.Widgets();
				break;
			case CONTAINERS:
				title = Labels.lblCnst.Containers();
				break;
			case CONTROLS:
				title = Labels.lblCnst.Controls();
				break;
			case TEXT:
				title = Labels.lblCnst.Text();
				break;
			case RCHART:
				title = Labels.lblCnst.RChart() ;
			default:
				break;
			}
			
			cats.add(new ToolBoxCategory(collapsePanel, title, first, buildTools(category)));
			first = false;
		}
		return cats;
	}

	private static List<ToolBox> buildTools(Category category) {
		List<ToolBox> tools = new ArrayList<>();
		
		switch (category) {
		case WIDGETS:
			for (ComponentType tool : WIDGETS) {
				tools.add(new ToolBox(tool));
			}
			break;
		case TEXT:
			for (ComponentType tool : TEXT) {
				tools.add(new ToolBox(tool));
			}
			break;
		case CONTROLS:
			for (ComponentType tool : CONTROLS) {
				tools.add(new ToolBox(tool));
			}
			break;
		case CONTAINERS:
			for (ComponentType tool : CONTAINERS) {
				tools.add(new ToolBox(tool));
			}
			break;
		case RCHART: 
			for( ComponentType tool: RCHART ){
				tools.add( new ToolBox(tool) );
			}
		default:
			break;
		}
		return tools;
	}

	public static ImageResource getImage(ComponentType type) {
		switch (type) {
		case CHART:
			return DashboardImage.INSTANCE.tool_chart();
		case FILTER:
			return DashboardImage.INSTANCE.filter_2();
		case SLICER:
			return DashboardImage.INSTANCE.start_24dp();
		case DATA_GRID:
			return DashboardImage.INSTANCE.data_grid();
		case GAUGE:
			return DashboardImage.INSTANCE.gauge_1();
		case REPORT:
			return DashboardImage.INSTANCE.report();
		case DASHLET:
			return DashboardImage.INSTANCE.dashlet();
		case OLAP_VIEW:
			return DashboardImage.INSTANCE.olap_view();
		case MAP:
			return DashboardImage.INSTANCE.map();
		case MARKDOWN:
			return DashboardImage.INSTANCE.markdown();
		case LABEL:
			return DashboardImage.INSTANCE.label();
		case IMAGE:
			return DashboardImage.INSTANCE.image();
		case URL:
			return DashboardImage.INSTANCE.link();
		case BUTTON:
			return DashboardImage.INSTANCE.start_24dp();
		case CLOCK:
			return DashboardImage.INSTANCE.start_24dp();
		case COMMENT:
			return DashboardImage.INSTANCE.start_24dp();
		case DIV:
			return DashboardImage.INSTANCE.div();
		case STACKABLE_CELL:
			return DashboardImage.INSTANCE.stackable_cell();
		case DRILL_STACKABLE_CELL:
			return DashboardImage.INSTANCE.stackable_cell_drill();
		case KPI_CHART:
			return DashboardImage.INSTANCE.kpi_chart();
		case KPI:
			return DashboardImage.INSTANCE.kpi_tool();
		case DATAVIZ:
			return DashboardImage.INSTANCE.lk_wb_analysis_48();
		case RCHART:
			return DashboardImage.INSTANCE.tool_chart();
		case D4C:
			return DashboardImage.INSTANCE.d4c_48();
		case DYNAMIC_LABEL:
			return DashboardImage.INSTANCE.dynamic_label();
		case FLOURISH:
			return DashboardImage.INSTANCE.flourish_48();
		default:
			return DashboardImage.INSTANCE.start_24dp();
		}
	}

	public static String getLabel(ComponentType type) {
		switch (type) {
		case CHART:
			return Labels.lblCnst.Chart();
		case FILTER:
			return Labels.lblCnst.Filter();
		case SLICER:
			return Labels.lblCnst.Slicer();
		case DATA_GRID:
			return Labels.lblCnst.DataGrid();
		case GAUGE:
			return Labels.lblCnst.Gauge();
		case REPORT:
			return Labels.lblCnst.Report();
		case DASHLET:
			return Labels.lblCnst.Dashlet();
		case OLAP_VIEW:
			return Labels.lblCnst.OlapView();
		case MAP:
			return Labels.lblCnst.Map();
		case MARKDOWN:
			return Labels.lblCnst.Markdown();
		case LABEL:
			return Labels.lblCnst.Label();
		case IMAGE:
			return Labels.lblCnst.Image();
		case URL:
			return Labels.lblCnst.URL();
		case BUTTON:
			return Labels.lblCnst.Button();
		case CLOCK:
			return Labels.lblCnst.Clock();
		case COMMENT:
			return Labels.lblCnst.Comment();
		case DIV:
			return Labels.lblCnst.Div();
		case STACKABLE_CELL:
			return Labels.lblCnst.StackableCell();
		case DRILL_STACKABLE_CELL:
			return Labels.lblCnst.DrillStackableCell();
		case KPI_CHART:
			return Labels.lblCnst.KpiChart();
		case KPI:
			return Labels.lblCnst.Kpi();
		case DATAVIZ:
			return Labels.lblCnst.DataPreparation();
		case RCHART:
			return Labels.lblCnst.RChart();
		case D4C:
			return Labels.lblCnst.Data4Citizen();
		case DYNAMIC_LABEL:
			return Labels.lblCnst.DynamicLabel();
		case FLOURISH:
			return Labels.lblCnst.Flourish();
		default:
			return Labels.lblCnst.Unknown();
		}
	}

//	public static ComponentType getType(DashboardComponent component) {
//		if (component instanceof ChartComponent) {
//			return ComponentType.CHART;
//		}
//		else if (component instanceof FilterComponent) {
//			return ComponentType.FILTER;
//		}
////		else if (component instanceof SlicerComponent) {
////			return ComponentType.SLICER;
////		}
//		else if (component instanceof DatagridComponent) {
//			return ComponentType.DATA_GRID;
//		}
//		else if (component instanceof GaugeComponent) {
//			return ComponentType.GAUGE;
//		}
//		else if (component instanceof ReportComponent) {
//			return ComponentType.REPORT;
//		}
//		else if (component instanceof JspComponent) {
//			return ComponentType.DASHLET;
//		}
//		else if (component instanceof CubeViewComponent) {
//			return ComponentType.OLAP_VIEW;
//		}
//		else if (component instanceof MapComponent) {
//			return ComponentType.MAP;
//		}
//		else if (component instanceof MarkdownComponent) {
//			return ComponentType.MARKDOWN;
//		}
//		else if (component instanceof LabelComponent) {
//			return ComponentType.LABEL;
//		}
//		else if (component instanceof ImageComponent) {
//			return ComponentType.IMAGE;
//		}
//		else if (component instanceof LinkComponent) {
//			return ComponentType.URL;
//		}
//		else if (component instanceof ButtonComponent) {
//			return ComponentType.BUTTON;
//		}
////		else if (component instanceof ClockComponent) {
////			return ComponentType.CLOCK;
////		}
//		else if (component instanceof CommentComponent) {
//			return ComponentType.COMMENT;
//		}
//		else if (component instanceof DivComponent) {
//			return ComponentType.DIV;
//		}
//		else if (component instanceof StackableCellComponent) {
//			return ComponentType.STACKABLE_CELL;
//		}
//		else if (component instanceof DrillStackableCellComponent) {
//			return ComponentType.DRILL_STACKABLE_CELL;
//		}
//		
//		return null;
//	}
	
	public static boolean isContentWidget(ComponentType tool) {
		return tool == ComponentType.DIV || tool == ComponentType.STACKABLE_CELL || tool == ComponentType.DRILL_STACKABLE_CELL;
	}
}
