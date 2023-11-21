package bpm.faweb.client.dialog;

import java.util.ArrayList;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.shared.Email;
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ExportDialog extends AbstractDialogBox {

	private static ExportDialogUiBinder uiBinder = GWT.create(ExportDialogUiBinder.class);

	interface ExportDialogUiBinder extends UiBinder<Widget, ExportDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	TextBox txtName, txtSeparator;

	@UiField
	ListBox lstFormat;

	private IShare share;
	private DrillInformations drillInfo;

	public ExportDialog(IShare share, DrillInformations drillInfo) {
		super(FreeAnalysisWeb.LBL.ExportAsDocument(), false, true);
		this.share = share;
		this.drillInfo = drillInfo;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(FreeAnalysisWeb.LBL.Apply(), okHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);

		lstFormat.addItem(CommonConstants.FORMAT_XLS_NAME, CommonConstants.FORMAT_XLS);
		lstFormat.addItem(CommonConstants.FORMAT_CSV_NAME, CommonConstants.FORMAT_CSV);
		lstFormat.addItem(CommonConstants.FORMAT_WEKA_NAME, CommonConstants.FORMAT_WEKA);

		txtSeparator.setEnabled(false);
	}

	@UiHandler("lstFormat")
	public void changeFormat(ChangeEvent event) {
		final String format = lstFormat.getValue(lstFormat.getSelectedIndex());
		if (format.equalsIgnoreCase(CommonConstants.FORMAT_CSV)) {
			txtSeparator.setText(";");
			txtSeparator.setEnabled(true);
		}
		else {
			if (format.equalsIgnoreCase(CommonConstants.FORMAT_WEKA)) {
				txtSeparator.setText(",");
			}
			else {
				txtSeparator.setText(";");
			}
			txtSeparator.setEnabled(false);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (txtName.getText() == null || txtName.getText().equals("")) {
				txtName.setText(String.valueOf(new Object().hashCode()));
			}

			if (txtSeparator.getText() == null || txtSeparator.getText().equals("")) {
				txtSeparator.setText(";");
			}

			String name = txtName.getText();
			String separator = txtSeparator.getText();
			final String format = lstFormat.getValue(lstFormat.getSelectedIndex());

			InfoShareCube infoShare = new InfoShareCube(TypeShare.EXPORT, TypeExport.DRILLTHROUGH, name, "", "", format, separator, false, new ArrayList<Group>(), new ArrayList<Email>(), "", false);
			infoShare.setDrillInformations(drillInfo);

			share.share(infoShare);
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ExportDialog.this.hide();
		}
	};

}
