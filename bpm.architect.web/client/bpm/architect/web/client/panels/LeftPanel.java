package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.utils.ToolHelper;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.CollapseWidget;
import bpm.gwt.commons.client.panels.NavigationPanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LeftPanel extends CollapseWidget {

	private static RightPanelUiBinder uiBinder = GWT.create(RightPanelUiBinder.class);

	interface RightPanelUiBinder extends UiBinder<Widget, LeftPanel> {
	}
	
	interface MyStyle extends CssResource {
		String btnSelected();
		String panelNavigation();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel btnPalette, btnData;
	
	@UiField
	HTMLPanel panelTabs;
	
	@UiField
	SimplePanel panelContent;
	
	@UiField
	Image imgExpand;
	
	private CollapsePanel collapsePanel;
	
	private NavigationPanel navigationPanel;

	public LeftPanel(CollapsePanel collapsePanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collapsePanel = collapsePanel;
		
		List<StackNavigationPanel> panels = ToolHelper.createCategories(collapsePanel);
		
		this.navigationPanel = new NavigationPanel();
		navigationPanel.addStyleName(style.panelNavigation());
		navigationPanel.setPanels(panels);
		
		panelContent.setWidget(navigationPanel);
	}

	@UiHandler("btnPalette")
	public void onPaletteClick(ClickEvent event) {
		btnPalette.addStyleName(style.btnSelected());
		btnData.removeStyleName(style.btnSelected());
		
		panelContent.setWidget(navigationPanel);
	}

	@UiHandler("btnData")
	public void onDataClick(ClickEvent event) {
		btnPalette.removeStyleName(style.btnSelected());
		btnData.addStyleName(style.btnSelected());

		
	}
	
	@Override
	public void onCollapseClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(false);
	}

	@Override
	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		collapsePanel.collapseNavigationPanel(true);
	}

	@Override
	public Image getImgExpand() {
		return imgExpand;
	}

	@Override
	public List<Widget> getCollapseWidgets() {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(panelTabs);
		widgets.add(panelContent);
		return widgets;
	}

	@Override
	protected void additionnalCollapseTreatment(boolean isCollapse) { }
}
