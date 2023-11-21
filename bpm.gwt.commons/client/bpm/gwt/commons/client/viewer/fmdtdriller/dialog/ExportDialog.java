package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.gwt.commons.shared.utils.CommonConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
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

	@UiField
	CheckBox cbFormatted;

	private IWait waitPanel;

	private FmdtRow columnNames;
	// private FmdtTable selectedTable;
	private FmdtTable flatTable = null;
	private FmdtTable formattedTable = null;

	public ExportDialog(IWait waitPanel, FmdtRow columnNames, FmdtTable selectedTable) {
		super(LabelsConstants.lblCnst.ExportTitle(), false, true);
		this.waitPanel = waitPanel;
		this.columnNames = columnNames;
		this.flatTable = selectedTable;

		initExportdialog();
	}

	public ExportDialog(IWait waitPanel, FmdtRow columnNames, FmdtTable formattedTable, FmdtTable flatTable) {
		super(LabelsConstants.lblCnst.ExportTitle(), false, true);
		this.waitPanel = waitPanel;
		this.columnNames = columnNames;
		this.flatTable = flatTable;
		this.formattedTable = formattedTable;

		initExportdialog();
	}

	public ExportDialog(IWait waitPanel, FmdtTable selectedTable) {
		super(LabelsConstants.lblCnst.ExportTitle(), false, true);
		this.waitPanel = waitPanel;
		this.columnNames = null;
		this.flatTable = selectedTable;

		initExportdialog();
	}

	private void initExportdialog() {
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Export(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		lstFormat.addItem(CommonConstants.FORMAT_XLS_NAME, CommonConstants.FORMAT_XLS);
		lstFormat.addItem(CommonConstants.FORMAT_CSV_NAME, CommonConstants.FORMAT_CSV);
		lstFormat.addItem(CommonConstants.FORMAT_WEKA_NAME, CommonConstants.FORMAT_WEKA);

		txtSeparator.setEnabled(false);

		if (formattedTable == null)
			cbFormatted.setVisible(false);
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
		if (formattedTable != null)
			if (format.equalsIgnoreCase(CommonConstants.FORMAT_XLS)) {
				cbFormatted.setVisible(true);
			}
			else {
				cbFormatted.setVisible(false);
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

			FmdtTable selectedTable = flatTable;
			if (cbFormatted.getValue())
				selectedTable = formattedTable;

			waitPanel.showWaitPart(true);
			if (columnNames != null)
				FmdtServices.Connect.getInstance().exportToXLS(columnNames, selectedTable, name, format, separator, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						waitPanel.showWaitPart(false);
					}

					@Override
					public void onSuccess(String result) {
						waitPanel.showWaitPart(false);

						hide();

						String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + format;
						ToolsGWT.doRedirect(fullUrl);
					}
				});
			else
				FmdtServices.Connect.getInstance().exportToXLS(selectedTable, name, format, separator, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						waitPanel.showWaitPart(false);
					}

					@Override
					public void onSuccess(String result) {
						waitPanel.showWaitPart(false);

						hide();

						String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + format;
						ToolsGWT.doRedirect(fullUrl);
					}
				});

		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ExportDialog.this.hide();
		}
	};

}
