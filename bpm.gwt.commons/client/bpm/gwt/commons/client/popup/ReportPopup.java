package bpm.gwt.commons.client.popup;

import java.util.List;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.ReportGroupViewer;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportPopup extends PopupPanel {

	private static ReportUiBinder uiBinder = GWT.create(ReportUiBinder.class);

	interface ReportUiBinder extends UiBinder<Widget, ReportPopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private ReportGroupViewer viewer;
	
	public ReportPopup(ReportGroupViewer viewer, List<LaunchReportInformations> itemInfos) {
		setWidget(uiBinder.createAndBindUi(this));
		this.viewer = viewer;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		boolean first = true;
		for(LaunchReportInformations itemInfo : itemInfos){
			addReport(itemInfo, first);
			first = false;
		}
		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private void addReport(LaunchReportInformations itemInfo, boolean first) {
		Label lblTheme = new Label(itemInfo.getItem().getName());
		lblTheme.addClickHandler(new ReportHandler(itemInfo));
		if(first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}
		
		panelMenu.add(lblTheme);
	}
	
	private class ReportHandler implements ClickHandler {
		
		private LaunchReportInformations itemInfo;

		public ReportHandler(LaunchReportInformations itemInfo) {
			this.itemInfo = itemInfo;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			viewer.changeReport(itemInfo);
			ReportPopup.this.hide();
		}
	}
}
