package bpm.architect.web.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;

public class CreateIntegrationDialog extends AbstractDialogBox {

	private static CreateIntegrationDialogUiBinder uiBinder = GWT.create(CreateIntegrationDialogUiBinder.class);

	interface CreateIntegrationDialogUiBinder extends UiBinder<Widget, CreateIntegrationDialog> {
	}
	
	@UiField
	LabelTextBox txtOrganization, txtDataset, txtNameService, txtItem, txtOuputName;
	
	@UiField
	ListBoxWithButton<ContractType> lstContractType;
	
	@UiField
	Label lblError;
	
	private IntegrationInformationsComposite parent;
	private Contract contract;

	public CreateIntegrationDialog(IntegrationInformationsComposite parent, Contract contract) {
		super(Labels.lblCnst.CreateIntegrationProcess(), false, true);
		this.parent = parent;
		this.contract = contract;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		lstContractType.setList(ContractType.values());
		lstContractType.addChangeHandler(changeContractTypeHandler);
		
		updateUi(ContractType.values()[0]);
	}
	
	private void updateUi(ContractType type) {
		txtNameService.setVisible(true);
		txtItem.setVisible(true);
		txtOuputName.setVisible(true);
		lblError.setVisible(false);
		
		switch (type) {
		case API:
			txtItem.setLabel(LabelsConstants.lblCnst.Url());
			break;
		case LIMESURVEY:
		case LIMESURVEY_SHAPES:
			txtItem.setLabel(Labels.lblCnst.LimeSurveyId());
			break;
		case DOCUMENT:
			txtItem.setVisible(false);
			txtOuputName.setVisible(false);
			break;
		case DATABASE:
			txtNameService.setVisible(false);
			txtItem.setVisible(false);
			txtOuputName.setVisible(false);
			lblError.setVisible(true);
			lblError.setText(Labels.lblCnst.NotSupported());
			break;
		default:
			break;
		}
	}
	
	private ChangeHandler changeContractTypeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			ContractType type = lstContractType.getSelectedObject();
			updateUi(type);
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String d4cOrg = txtOrganization.getText();
			String d4cDatasetName = txtDataset.getText();
			
			ContractType type = lstContractType.getSelectedObject();
			String nameService = txtNameService.getText();
			String item = txtItem.getText();
			String outputName = txtOuputName.getText();
			
			ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations(contract.getId(), type, nameService, item, outputName, d4cOrg, d4cDatasetName);
			ArchitectService.Connect.getInstance().generateIntegrationProcess(integrationInfos, new GwtCallbackWrapper<Void>(CreateIntegrationDialog.this, true, true) {

				@Override
				public void onSuccess(Void result) {
					parent.loadItem();
					CreateIntegrationDialog.this.hide();
				}
			}.getAsyncCallback());
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			CreateIntegrationDialog.this.hide();
		}
	};
}
