package bpm.architect.web.client.panels;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizReportPanel extends Composite {

	private static DataVizReportPanelUiBinder uiBinder = GWT.create(DataVizReportPanelUiBinder.class);

	interface DataVizReportPanelUiBinder extends UiBinder<Widget, DataVizReportPanel> {
	}

	public interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	Frame reportFrame;
	
	private DataVizDataPanel parent;

	public DataVizReportPanel(String reportUrl) {
		initWidget(uiBinder.createAndBindUi(this));
//		this.parent = parent;

		setReportUrl(reportUrl);
//		reportFrame.addLoadHandler(this);
		
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}
	
	public void setReportUrl(String reportUrl) {
		reportFrame.setUrl(reportUrl);
	}

	@UiHandler("btnOpenInNewTab")
	public void onOpenInNewTab(ClickEvent event) {
		
	}

//	@Override
//	public void onLoad(LoadEvent event) {
//		parent.showReport();
//	}
}
