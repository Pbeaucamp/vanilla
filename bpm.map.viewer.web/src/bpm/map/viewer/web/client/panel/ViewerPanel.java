package bpm.map.viewer.web.client.panel;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.map.viewer.web.client.Bpm_map_viewer_web.TypeDisplay;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.viewer.DataPanel;
import bpm.map.viewer.web.client.panel.viewer.GraphPanel;
import bpm.map.viewer.web.client.panel.viewer.MapViewer;
import bpm.map.viewer.web.client.services.MapViewerService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ViewerPanel extends Tab {

	private static ViewerPanelUiBinder uiBinder = GWT.create(ViewerPanelUiBinder.class);
	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	interface ViewerPanelUiBinder extends UiBinder<Widget, ViewerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String splitter();
	}
	
	public enum ActionDate {
		VALEUR, EVOLUTION
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel leftPanel, rightPanel, mapViewer, graphPanel, dataPanel;
	
//	@UiField
//	SimplePanel panelSplitter;
	
	@UiField
	Image imgCollapse, imgExpand, graphCollapse, graphExpand;
	
	private MapViewer mapContent;
	private DataPanel dataContent;
	private GraphPanel graphContent;
	
	private List<ComplexMap> mapsList = new ArrayList<ComplexMap>();
	private List<MapZoneValue> mapzonevalues = new ArrayList<MapZoneValue>();
	List<ComplexObjectViewer> viewersList =  new ArrayList<ComplexObjectViewer>();
	private List<Level> levels = new ArrayList<Level>();
	private List<Metric> metrics = new ArrayList<Metric>();

	private ActionDate actionDate;
	
	private TypeDisplay display;

	public ViewerPanel(TabManager tabManager, TypeDisplay display) {
		super(tabManager, lblCnst.Viewer(), false);
		this.add(uiBinder.createAndBindUi(this));
		this.display = display;
		
//		panelSplitter.setWidget(new Splitter2(mapViewer, graphPanel, style.splitter()));
		imgExpand.setVisible(false);
		graphExpand.setVisible(false);
		
		collapseGraphPanel(false);
		
		loadEvent();
	}

	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		collapseApplicationPanel(false);
	}

	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		collapseApplicationPanel(true);
	}
	
	@UiHandler("graphCollapse")
	public void onCollapseGraphClick(ClickEvent event) {
		collapseGraphPanel(false);
	}

	@UiHandler("graphExpand")
	public void onExpandGraphClick(ClickEvent event) {
		collapseGraphPanel(true);
	}
	
	
	public void collapseApplicationPanel(boolean isCollapse){
		
		if(isCollapse){
			leftPanel.getElement().getStyle().setRight(400, Unit.PX );
			rightPanel.setWidth("400px");
			dataPanel.setVisible(true);
			imgCollapse.setVisible(true);
			imgExpand.setVisible(false);
		} else {
			leftPanel.getElement().getStyle().setRight(45, Unit.PX );
			rightPanel.setWidth("45px");
			dataPanel.setVisible(false);
			imgCollapse.setVisible(false);
			imgExpand.setVisible(true);
		}
		mapContent.updateSize();
	}
	
	public void collapseGraphPanel(boolean isCollapse){
		
		if(isCollapse){
			mapViewer.getElement().getStyle().setBottom(350, Unit.PX );
			graphPanel.setHeight("350px");
			if(graphContent != null)
				graphContent.setVisible(true);
			graphCollapse.setVisible(true);
			graphExpand.setVisible(false);
			if(graphContent != null){
				graphCollapse.getElement().getStyle().setLeft(60, Unit.PX);  //petit detail css
				graphExpand.getElement().getStyle().setLeft(60, Unit.PX);
			} else {
				graphCollapse.getElement().getStyle().setLeft(0, Unit.PX);  //petit detail css
				graphExpand.getElement().getStyle().setLeft(0, Unit.PX);
			}
			
		} else {
			mapViewer.getElement().getStyle().setBottom(45, Unit.PX );
			graphPanel.setHeight("45px");
			if(graphContent != null)
				graphContent.setVisible(false);
			graphCollapse.setVisible(false);
			graphExpand.setVisible(true);
			
			graphCollapse.getElement().getStyle().setLeft(0, Unit.PX);  //petit detail css
			graphExpand.getElement().getStyle().setLeft(0, Unit.PX);
			
		}
		if(mapContent != null)
			mapContent.updateSize();
	}
	
	private void loadEvent() {
		showWaitPart(true);
		MapViewerService.Connect.getInstance().getMaps(new AsyncCallback<List<ComplexMap>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToLoadMaps());
			}

			@Override
			public void onSuccess(List<ComplexMap> result) {
				showWaitPart(false);
				mapsList = result;
				mapViewer.clear();
				mapContent = new MapViewer(ViewerPanel.this, display);
				try {
					mapViewer.add(mapContent);
				} catch(Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	public List<ComplexMap> getMapsList() {
		return mapsList;
	}

	public void setMapsList(List<ComplexMap> mapsList) {
		this.mapsList = mapsList;
	}

	public List<MapZoneValue> getMapzonevalues() {
		return mapzonevalues;
	}

	public void setMapzonevalues(List<MapZoneValue> mapzonevalues) {
		this.mapzonevalues = mapzonevalues;
	}
	
	public void updateMapZoneValue(List<ComplexObjectViewer> result){
		viewersList = result;
		metrics.clear();
		levels.clear();
		mapzonevalues.clear();
		for(ComplexObjectViewer cov : viewersList){
			levels.add(cov.getLevel());
			metrics.add(cov.getMetric());
			mapzonevalues.addAll(cov.getMapvalues());
		}
		
		loadGraphPanel();
		loadDataPanel();
	}
	
	public void loadGraphPanel(){
		graphCollapse.getElement().getStyle().setLeft(60, Unit.PX);  //petit detail css
		graphExpand.getElement().getStyle().setLeft(60, Unit.PX);
		graphPanel.clear();
		graphContent = new GraphPanel(this, levels, metrics, viewersList);
		if(graphPanel.getElement().getStyle().getHeight().equals("45px") )
			graphContent.setVisible(false);
		graphPanel.add(graphContent);
		
	}
	
	public void loadDataPanel(){

		dataPanel.clear();
		dataContent = new DataPanel(this, levels, metrics, viewersList);
		dataPanel.add(dataContent);
		
	}

	public void refresh() {
		loadEvent();
		
	}

	public List<MapZoneValue> getMapValues(List<Level> levs, List<Metric> mets) {
		List<MapZoneValue> res = new ArrayList<MapZoneValue>();
		for(Level l : levs){
			for(Metric m : mets){
				for(ComplexObjectViewer cov : viewersList){
					if(cov.getLevel().equals(l) && cov.getMetric().equals(m)){
						res.addAll(cov.getMapvalues());
					}
				}
			}
		}
		
		return res;
	}
	
	public void refreshMap(){
		if(mapContent != null)
			mapContent.updateSize();
	}
	
	public ActionDate getActionDate() {
		return actionDate;
	}

	public void setActionDate(ActionDate actionDate) {
		this.actionDate = actionDate;
	}

	public List<ComplexObjectViewer> getViewersList() {
		return viewersList;
	}
	
	
}
