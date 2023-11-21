package bpm.gwt.commons.client.viewer.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.MultiSelectionModel;

public class HistorizeOptionDialog extends AbstractDialogBox {

	private static HistorizeOptionDialogUiBinder uiBinder = GWT.create(HistorizeOptionDialogUiBinder.class);

	interface HistorizeOptionDialogUiBinder extends UiBinder<Widget, HistorizeOptionDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;

	@UiField
	SimplePanel panelGridGroup, datePanel;

	@UiField
	CheckBox checkBoxContribution, checkUsePeremptionDate;

	@UiField
	RadioButton newDocument, addVersion;

	@UiField
	TextBox txtNewDocument;

	@UiField
	ListBox lstDocuments, lstFormat;
	
	private DateBox dateBox;

	private IWait waitPanel;

	private boolean createNewDocument = true;

	private PortailRepositoryItem item;
	private String reportKey;

	private MultiSelectionModel<Group> selectionModel = new MultiSelectionModel<Group>();;
	private List<Group> groups;

	public HistorizeOptionDialog(IWait waitPanel, PortailRepositoryItem item, String reportKey, List<String> selectedFormats, List<Group> groups, List<DocumentDefinitionDTO> histoDocs) {
		super(LabelsConstants.lblCnst.OptionHistoric(), false, true);
		this.waitPanel = waitPanel;
		this.item = item;
		this.reportKey = reportKey;
		this.groups = groups;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);


		newDocument.setValue(true);
		lstDocuments.setEnabled(false);

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		DatePicker datePicker = new DatePicker();
		dateBox = new DateBox(datePicker, new Date(), new DateBox.DefaultFormat(dateFormat));
		dateBox.setEnabled(false);
		datePanel.setWidget(dateBox);

		panelGridGroup.add(new CustomDatagrid<Group>(groups, selectionModel, 100, LabelsConstants.lblCnst.NoGroup()));

		if (selectedFormats != null) {
			for (String format : selectedFormats) {
				lstFormat.addItem(format);
			}
		}

		if (histoDocs != null && !histoDocs.isEmpty()) {
			for (DocumentDefinitionDTO doc : histoDocs) {
				lstDocuments.addItem(doc.getName(), String.valueOf(doc.getId()));
			}
		}
		else {
			lstDocuments.addItem(LabelsConstants.lblCnst.NoDocumentAvailable());
			addVersion.setEnabled(false);
		}
	}

	@UiHandler("checkUsePeremptionDate")
	public void onChangeUsePeremptionDate(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			dateBox.setEnabled(true);
		}
		else {
			dateBox.setEnabled(false);
		}
	}

	@UiHandler("newDocument")
	public void onNewDocClick(ValueChangeEvent<Boolean> event) {
		this.createNewDocument = true;
		this.txtNewDocument.setEnabled(true);
		this.lstDocuments.setEnabled(false);
		this.panelGridGroup.setVisible(true);
	}

	@UiHandler("addVersion")
	public void onAddVersionClick(ValueChangeEvent<Boolean> event) {
		this.createNewDocument = false;
		this.txtNewDocument.setEnabled(false);
		this.lstDocuments.setEnabled(true);
		this.panelGridGroup.setVisible(false);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String gedName = "";
			Integer histoId = null;
			if (createNewDocument) {
				gedName = txtNewDocument.getText();
			}
			else {
				gedName = "AlreadyCreatedName";
				histoId = Integer.valueOf(lstDocuments.getValue(lstDocuments.getSelectedIndex()));
			}

			if (gedName.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedInformationsHisto());
				return;
			}

			boolean isContribution = checkBoxContribution.getValue();

			String format = lstFormat.getItemText(lstFormat.getSelectedIndex());
			if (format == null || format.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedInformationsHisto());
				return;
			}

			List<Integer> groupIds = null;
			if (createNewDocument) {
				groupIds = new ArrayList<Integer>();
				for (Group group : groups) {
					if (selectionModel.isSelected(group)) {
						groupIds.add(group.getId());
					}
				}

				if (groupIds.isEmpty()) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedRunForGroupOrUser());
					return;
				}
			}

			Date peremptionDate = null;
			if (checkUsePeremptionDate.getValue()) {
				peremptionDate = dateBox.getValue();
			}

			waitPanel.showWaitPart(true);

			ReportingService.Connect.getInstance().historize(false, histoId, reportKey, groupIds, gedName, format, isContribution, item, peremptionDate, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					waitPanel.showWaitPart(false);

					HistorizeOptionDialog.this.hide();

					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.SuccessHisto());
					return;
				}

				@Override
				public void onFailure(Throwable caught) {
					waitPanel.showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedHisto());
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			HistorizeOptionDialog.this.hide();
		}
	};
}
