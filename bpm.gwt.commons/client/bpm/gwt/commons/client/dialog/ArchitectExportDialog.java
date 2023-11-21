package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class ArchitectExportDialog extends AbstractDialogBox {

	private static ArchitectExportDialogUiBinder uiBinder = GWT.create(ArchitectExportDialogUiBinder.class);

	interface ArchitectExportDialogUiBinder extends UiBinder<Widget, ArchitectExportDialog> {
	}
	
	@UiField
	LabelTextBox lblName;

	@UiField
	ListBoxWithButton<Supplier> lstSuppliers;

	@UiField
	ListBoxWithButton<Contract> lstContracts;

	@UiField
	ListBoxWithButton<String> lstFormats;

	private boolean canChooseName;
	private boolean confirm;

	public ArchitectExportDialog(boolean canChooseName, boolean canSelectFormat) {
		super(LabelsConstants.lblCnst.ShareArchitect(), false, true);
		this.canChooseName = canChooseName;

		setWidget(uiBinder.createAndBindUi(this));

		lblName.setVisible(canChooseName);
		
		List<String> lstFrmt = new ArrayList<String>();
		lstFrmt.add("csv");
		lstFrmt.add("xlsx");
		lstFormats.setList(lstFrmt);
		lstFormats.setVisible(canSelectFormat);

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		loadItems();
	}

	private void loadItems() {
		CommonService.Connect.getInstance().getSuppliers(new GwtCallbackWrapper<List<Supplier>>(null, false) {
			@Override
			public void onSuccess(List<Supplier> result) {
				lstSuppliers.setList(result, true);
			}

		}.getAsyncCallback());
	}

	@UiHandler("lstSuppliers")
	public void onChangeSupplier(ChangeEvent event) {
		if (lstSuppliers.getSelectedObject() != null) {
			lstContracts.setList(lstSuppliers.getSelectedObject().getContracts(), true);
		}
		else {
			lstContracts.clear();
		}
	}

	@UiHandler("lstContracts")
	public void onChangeContract(ChangeEvent event) {
		Contract contract = lstContracts.getSelectedObject();
		if (contract.getDocId() == null) {
			// lstFormat.setVisible(true);
			lstFormats.setEnabled(true);
		}
		else {
			lstFormats.setEnabled(false);

			CommonService.Connect.getInstance().getFormat(contract, new GwtCallbackWrapper<String>(null, false) {

				@Override
				public void onSuccess(String result) {
					lstFormats.setSelectedObject(result);
				}

			}.getAsyncCallback());
		}
	}

	public boolean isConfirm() {
		return confirm;
	}

	private boolean isComplete() {
		Contract contract = getSelectedContract();
		return (canChooseName ? !lblName.getText().isEmpty() : true) && contract != null;
	}
	
	public String getName() {
		return lblName.getText();
	}

	public Contract getSelectedContract() {
		return lstContracts.getSelectedObject();
	}

	public String getFormat() {
		return lstFormats.getSelectedObject();
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!isComplete()) {
				return;
			}

			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			hide();
		}
	};
}
