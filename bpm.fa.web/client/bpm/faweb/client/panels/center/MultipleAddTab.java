package bpm.faweb.client.panels.center;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.MultipleAddPanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MultipleAddTab extends Tab {

	private static MultipleAddTabUiBinder uiBinder = GWT.create(MultipleAddTabUiBinder.class);

	interface MultipleAddTabUiBinder extends UiBinder<Widget, MultipleAddTab> {
	}

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent;

	@UiField
	Image imgRefresh, imgApply;
	
	private MainPanel mainPanel;
	
	private MultipleAddPanel multipleAddPanel;

	public MultipleAddTab(TabManager tabManager, MainPanel mainPanel) {
		super(tabManager, FreeAnalysisWeb.LBL.MultipleAdding(), true);
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	public void loadMultipleAddPanel() {
		multipleAddPanel = new MultipleAddPanel(mainPanel);
		panelContent.setWidget(multipleAddPanel);
	}
	
	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		if(multipleAddPanel != null) {
			multipleAddPanel.refresh();
		}
	}
	
	@UiHandler("imgApply")
	public void onApplyClick(ClickEvent event) {
		if(multipleAddPanel != null) {
			multipleAddPanel.apply();
		}
	}
}
