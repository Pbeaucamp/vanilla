package bpm.faweb.client.panels.navigation;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.FreeAnalysisWeb.TypeDisplay;
import bpm.faweb.client.MainPanel;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationPanel extends Composite {

	private static NavigationPanelUiBinder uiBinder = GWT.create(NavigationPanelUiBinder.class);

	interface NavigationPanelUiBinder extends UiBinder<Widget, NavigationPanel> {
	}

	interface MyStyle extends CssResource {
		String contentPanel();
		String navigationPanel();
		String navigationPanelViewer();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel contentPanel;

	@UiField
	HTMLPanel navigationPanel;
	
	@UiField
	Image imgExpand;
	
	private List<String> itemSelected = new ArrayList<String>();

	private MainPanel mainPanel;

	private StackHeader dimensionHeader, measureHeader;
	private DimensionPanel dimensionPanel;
	private MeasurePanel measurePanel;

	public NavigationPanel(MainPanel mainPanel, String cubeName, String[] unames) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		
		imgExpand.setVisible(false);
		
		dimensionHeader = new StackHeader(mainPanel, FreeAnalysisWeb.LBL.Dimensions() + " for " + cubeName, true);
		dimensionPanel = new DimensionPanel(mainPanel, unames);
		
		measureHeader = new StackHeader(mainPanel, FreeAnalysisWeb.LBL.Measures() + " for " + cubeName, false);
		measurePanel = new MeasurePanel(mainPanel, unames);
		
		StackLayoutPanel stackPanel = new StackLayoutPanel(Unit.PX);
		stackPanel.addStyleName(style.contentPanel());
		stackPanel.add(dimensionPanel, dimensionHeader, 40);
		stackPanel.add(measurePanel, measureHeader, 40);
		
		contentPanel.setWidget(stackPanel);
	}

	public DimensionPanel getDimensionPanel() {
		return dimensionPanel;
	}

	public MeasurePanel getMeasurePanel() {
		return measurePanel;
	}

	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		mainPanel.collapseNavigationPanel(true);
	}

	private void managePanel(boolean isCollapse) {
		if (isCollapse) {
			contentPanel.setVisible(true);

			imgExpand.setVisible(false);

			this.removeStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
		else {
			contentPanel.setVisible(false);

			imgExpand.setVisible(true);

			this.addStyleName(VanillaCSS.RIGHT_TOOLBAR);
		}
	}

	public void adaptSize(int navigationPanelWidth, boolean isCollapse) {
		navigationPanel.setWidth(navigationPanelWidth + "px");

		managePanel(isCollapse);
	}

	public void refreshDataPanels(String cubeName) {
		dimensionHeader.updateHeader(FreeAnalysisWeb.LBL.Dimensions() + " for " + cubeName);
		dimensionPanel.refresh();
		
		measureHeader.updateHeader(FreeAnalysisWeb.LBL.Measures() + " for " + cubeName);
		measurePanel.refresh();
	}
	
	public void clearItemSelected(){
		this.itemSelected = new ArrayList<String>();
	}
	
	public void addItemSelected(String item){
		this.itemSelected.add(item);
	}
	
	public void removeItemSelected(String item){
		this.itemSelected.remove(item);
	}
	
	public List<String> getItemSelected(){
		return itemSelected;
	}

	public void changeDisplay(TypeDisplay display) {
		switch (display) {
		case VIEWER:
			navigationPanel.removeStyleName(style.navigationPanel());
			navigationPanel.addStyleName(style.navigationPanelViewer());
			break;

		default:
			break;
		}
	}
}
