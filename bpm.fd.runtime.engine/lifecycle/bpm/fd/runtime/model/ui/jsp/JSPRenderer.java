package bpm.fd.runtime.model.ui.jsp;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentRChartDefinition;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.maps.ComponentDataViz;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmData;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.IRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.ButtonRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.ChartRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.CommentRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.D4CRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.DashletRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.DatavizRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.DrillDrivenCellRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.DynamicLabelRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.FaViewRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.FilterRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.FlourishRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.FolderRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.FormatedTextRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.GaugeRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.GridRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.IHTMLRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.ImageRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.KpiRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.LabelRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.LinkRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.MapRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.MarkdownRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.OsmMapRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.ReportRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.SlicerRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.TimerRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.VanillaMapRenderer;
import bpm.fd.runtime.model.ui.jsp.renderer.WmsMapRenderer;

public class JSPRenderer implements IRenderer{
	
	private static HashMap<Class<?>, IHTMLRenderer<?>> renderers = new HashMap<Class<?>, IHTMLRenderer<?>>();
	static{
		renderers.put(ComponentFilterDefinition.class, new FilterRenderer());
		renderers.put(ComponentChartDefinition.class, new ChartRenderer());
		renderers.put(LabelComponent.class, new LabelRenderer());
		renderers.put(DynamicLabelComponent.class, new DynamicLabelRenderer());
		renderers.put(ComponentLink.class, new LinkRenderer());
		renderers.put(ComponentPicture.class, new ImageRenderer());
		renderers.put(ComponentReport.class, new ReportRenderer());
		renderers.put(ComponentKpi.class, new KpiRenderer());
		renderers.put(ComponentFaView.class, new FaViewRenderer());
		renderers.put(ComponentDataViz.class, new DatavizRenderer());
		renderers.put(ComponentDataGrid.class, new GridRenderer());
		renderers.put(ComponentGauge.class, new GaugeRenderer());
		renderers.put(ComponentMap.class, new MapRenderer());
		renderers.put(ComponentJsp.class, new DashletRenderer());
		renderers.put(ComponentD4C.class, new D4CRenderer());
		renderers.put(ComponentTimer.class, new TimerRenderer());
		renderers.put(ComponentButtonDefinition.class, new ButtonRenderer());
		renderers.put(ComponentComment.class, new CommentRenderer());
		renderers.put(ComponentStyledTextInput.class, new FormatedTextRenderer());
		renderers.put(ComponentMapWMS.class, new WmsMapRenderer());
		renderers.put(DrillDrivenStackableCell.class, new DrillDrivenCellRenderer());
		renderers.put(Folder.class, new FolderRenderer());
		renderers.put(ComponentSlicer.class, new SlicerRenderer());
//		renderers.put(ComponentOsmMap.class, new OsmMapRenderer());
		renderers.put(ComponentOsmMap.class, new VanillaMapRenderer());
		renderers.put(ComponentMarkdown.class, new MarkdownRenderer());
		//Pour les graphiques R
		renderers.put(ComponentRChartDefinition.class, new ChartRenderer());
		renderers.put(ComponentFlourish.class, new FlourishRenderer());
	}
	
	public static IHTMLRenderer<?> get(Class<? extends IBaseElement> c){
		return renderers.get(c);
	}

	@Override
	public Object renderComponent(ComponentRuntime component, DashState state, IResultSet resultSet, boolean refresh) throws Exception {
		
		IHTMLRenderer renderer = get(component.getElement().getClass());
		
		if (renderer != null){
			Rectangle layout = null;
			if (component instanceof Component){
				layout = ((Component)component).getLayout();
			}
			
			//for the old maps to work
			if(component.getElement() instanceof ComponentOsmMap && ((ComponentOsmMap)component.getElement()).getDatas() instanceof OsmData) {
				return new OsmMapRenderer().getHTML(layout, (ComponentOsmMap) component.getElement(), state, resultSet, refresh);
			}
			
			//XXX
			if(component.getElement() instanceof ComponentChartDefinition) {
				ComponentChartDefinition chart = (ComponentChartDefinition) component.getElement();
				
				DrillDrivenStackableCell cell = findCellForComponent(chart, state.getDashInstance().getDashBoard().getProject().getFdModel().getContent());
				
				if(cell == null) {
					System.out.println("chart : " + chart.getName() + " not found in stackable cells");
					return renderer.getHTML(layout, component.getElement(), state, resultSet, refresh);
				}
				int index = 0;
				for(IComponentDefinition c : ((DrillDrivenStackableCell)cell).getComponents()) {
					if(c instanceof ComponentChartDefinition && ((ComponentChartDefinition)c).getName().equals(chart.getName())) {
						break;
					}
					index++;
				}
				
				String level = "";
				String val = state.getComponentValue("FilterDirection");
				if(val == null || val.isEmpty()) {
					val = state.getComponentValue("Toolbar_Filter_Direction");
				}
				
				if(!val.equals("%")) {
					level = "dir";
				}
				else {
					val = state.getComponentValue("FilterDGA");
					if(val == null || val.isEmpty()) {
						val = state.getComponentValue("Toolbar_Filter_DGA");
					}
					if(!val.equals("%")) {
						level = "dga";
					}
					else {
						level = "col";
					}
				}
				
				try {
					if(index == 0 && !level.equals("col")) {
						while(resultSet.next()) {}
					}
					else if(index == 1 && !level.equals("dga")) {
						while(resultSet.next()) {}
					}
					else if(index == 2 && !level.equals("dir")) {
						while(resultSet.next()) {}
					}
				} catch(Exception e) {
					System.out.println("null for - " + chart.getName());
				}
			}
			//XXX
			
			return renderer.getHTML(layout, component.getElement(), state, resultSet, refresh);
		}
		
		
		return "<div id='" + component.getName() + "' id='" + component.getElement().getId() + "' >No Renderer for Type "+ component.getElement().getClass().getSimpleName() + "</div>\n" ;
		
	}
	
	private DrillDrivenStackableCell findCellForComponent(ComponentChartDefinition chart, List<IBaseElement> elements) {
		for(IBaseElement elem : elements) {
			if(elem instanceof Folder) {
				for(IBaseElement p : ((Folder)elem).getContent()) {
					DrillDrivenStackableCell cell =  findCellForComponent(chart, ((FdModel)((FolderPage)p).getContent().get(0)).getContent());
					if(cell != null) {
						return cell;
					}
				}
			}
			else {
				if(elem instanceof DrillDrivenStackableCell) {
					for(IComponentDefinition c : ((DrillDrivenStackableCell)elem).getComponents()) {
						if(c instanceof ComponentChartDefinition && ((ComponentChartDefinition)c).getName().equals(chart.getName())) {
							return (DrillDrivenStackableCell) elem;
						}
					}
				}
			}
		}
		return null;
	}

}
