package bpm.gwt.commons.client.viewer.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.viewer.ReportViewer;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.FMDTValuesDialog;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;

public class ItemMetadataLinkDialog extends AbstractDialogBox {

	private static BirtFormatDialogBoxUiBinder uiBinder = GWT.create(BirtFormatDialogBoxUiBinder.class);

	interface BirtFormatDialogBoxUiBinder extends UiBinder<Widget, ItemMetadataLinkDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	ListBoxWithButton<ItemMetadataTableLink> lstLinks;

	private ReportViewer viewer;
	
	/**
	 * This constructor is used for the export and the print of a report with
	 * his parameters already set
	 */
	public ItemMetadataLinkDialog(ReportViewer viewer, String title, LaunchReportInformations itemInfo) {
		super(title, false, true);
		this.viewer = viewer;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		for (ItemMetadataTableLink link : itemInfo.getMetadataLinks()) {
			lstLinks.addItem(link.getName(), link);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ItemMetadataTableLink link = lstLinks.getSelectedObject();
			
			FMDTValuesDialog dial = new FMDTValuesDialog(viewer, link);
			dial.center();
			
			ItemMetadataLinkDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ItemMetadataLinkDialog.this.hide();
		}
	};
}
