package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.dialogs.FileExportPanel;
import bpm.architect.web.client.utils.ToolHelper;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.CollapseWidget;
import bpm.gwt.commons.client.panels.NavigationPanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizRulePanel extends CollapseWidget {

	private static DataVizRulePanelUiBinder uiBinder = GWT.create(DataVizRulePanelUiBinder.class);

	interface DataVizRulePanelUiBinder extends UiBinder<Widget, DataVizRulePanel> {}

	interface MyStyle extends CssResource {
		String btnSelected();
		String panelNavigation();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel btnRule, btnExplore;

	@UiField
	HTMLPanel panelTabs;

	@UiField
	SimplePanel panelContent;

	@UiField
	Image imgExpand;

	private CollapsePanel collapsePanel;
	private NavigationPanel navigationPanel;
	private DataVizDesignPanel parent;
	private String ruleTitle = "";

	public DataVizRulePanel(CollapsePanel collapsePanel, DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.collapsePanel = collapsePanel;
		this.parent = parent;
		
		List<StackNavigationPanel> panels = ToolHelper.createCategories(collapsePanel, parent);
		
		parent.setRuleTitle(ruleTitle);
		this.navigationPanel = new NavigationPanel();
		navigationPanel.addStyleName(style.panelNavigation());
		navigationPanel.setPanels(panels);
		
		panelContent.setWidget(navigationPanel);
	}

	@UiHandler("btnRule")
	public void onPaletteClick(ClickEvent event) {
		btnRule.addStyleName(style.btnSelected());
		btnExplore.removeStyleName(style.btnSelected());

		panelContent.setWidget(navigationPanel);
	}

	@UiHandler("btnExplore")
	public void onDataClick(ClickEvent event) {
		btnRule.removeStyleName(style.btnSelected());
		btnExplore.addStyleName(style.btnSelected());

		refreshRulePanel();
	}

	public void refreshRulePanel() {
		panelContent.setWidget(new DataVizAppliedRulesPanel(parent));
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
