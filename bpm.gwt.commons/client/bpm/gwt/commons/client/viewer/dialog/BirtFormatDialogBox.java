package bpm.gwt.commons.client.viewer.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.viewer.ReportViewer;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class BirtFormatDialogBox extends AbstractDialogBox {

	private static BirtFormatDialogBoxUiBinder uiBinder = GWT.create(BirtFormatDialogBoxUiBinder.class);

	interface BirtFormatDialogBoxUiBinder extends UiBinder<Widget, BirtFormatDialogBox> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	CaptionPanel fieldSetFormat;

	@UiField
	ListBox lstFormat;

	private ReportViewer viewer;
	private LaunchReportInformations itemInfo;

	private TypeRun typeRun;
	
	/**
	 * This constructor is used for the export and the print of a report with
	 * his parameters already set
	 */
	public BirtFormatDialogBox(ReportViewer viewer, String title, LaunchReportInformations itemInfo, TypeRun typeRun) {
		super(title, false, true);
		this.viewer = viewer;
		this.itemInfo = itemInfo;
		this.typeRun = typeRun;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		fieldSetFormat.setCaptionText(LabelsConstants.lblCnst.Format());

		int indexDefaultFormat = -1;
		for (String output : itemInfo.getOutputs()) {
			if (typeRun == TypeRun.PRINT && (output.equals(CommonConstants.FORMAT_HTML) || output.equals(CommonConstants.FORMAT_PDF))) {
				lstFormat.addItem(output);
			}
			else {
				lstFormat.addItem(output);
			}
		}
		// for(int i=0; i<CommonConstants.FORMAT_DISPLAY.length; i++){
		// if(i<2 || itemInfo.getTypeRun() != TypeRun.PRINT){
		// lstFormat.addItem(CommonConstants.FORMAT_DISPLAY[i],
		// CommonConstants.FORMAT_VALUE[i]);
		// }
		//
		// if (itemInfo.getItem().hasDefaultFormat() &&
		// itemInfo.getItem().getItem().getDefaultFormat().equalsIgnoreCase(CommonConstants.FORMAT_VALUE[i]))
		// {
		// indexDefaultFormat = i;
		// }
		// }

		if (indexDefaultFormat != -1) {
			lstFormat.setSelectedIndex(indexDefaultFormat);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			// We only put one format into the list of output format because for
			// an export or a print we can't do multiple format in the same ti
			// List<String> outputType = new ArrayList<String>();
			String outputType = "";
			if (lstFormat.isVisible()) {
				outputType = lstFormat.getValue(lstFormat.getSelectedIndex());
			}
			else {
				outputType = itemInfo.getItem().getItem().getDefaultFormat();
			}

			// itemInfo.setOutputs(outputType);

			if (typeRun == TypeRun.EXPORT) {
				viewer.exportReport(itemInfo.getReportKey(), outputType);
			}
			else {
				viewer.printReport(itemInfo.getReportKey(), outputType);
			}
			BirtFormatDialogBox.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			BirtFormatDialogBox.this.hide();
		}
	};
}
