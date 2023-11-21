package bpm.architect.web.client.dialogs;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.ConsultPanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.viewer.dialog.ViewerDialog;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.vanilla.platform.core.IRepositoryApi;

public class UploadDialog extends AbstractDialogBox {

	private static UploadDialogUiBinder uiBinder = GWT.create(UploadDialogUiBinder.class);

	interface UploadDialogUiBinder extends UiBinder<Widget, UploadDialog> {
	}
	
	@UiField
	LabelTextBox txtName;

	@UiField
	SimplePanel mainPanel;
	
	private FileUploadWidget fileUploadPanel;
	
	private ConsultPanel parent;
	private Contract contract;

	public UploadDialog(ConsultPanel parent, Contract contract) {
		super(Labels.lblCnst.AddNewVersion(), true, true);
		this.parent = parent;
		this.contract = contract;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		JSONObject parameters = buildParameters(contract);
		fileUploadPanel = new FileUploadWidget(CommonConstants.DATASOURCE_ARCHITECT_SERVLET, parameters);
		mainPanel.setWidget(fileUploadPanel);
		
		if (contract.getFileVersions() == null) {
			txtName.setVisible(true);
		}
	}

	private JSONObject buildParameters(Contract contract) {
		JSONObject params = new JSONObject();
//		params.put(UploadServlet.PARAM_ELEMENT_ID, new JSONString(String.valueOf(contract.getId())));
		return params;
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!fileUploadPanel.isFileUploaded()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), Labels.lblCnst.NeedUploadFile());
				return;
			}
			
			showWaitPart(true);
			
			String documentName = txtName.getText();
			String filePath = fileUploadPanel.getFileName();
			
			ArchitectService.Connect.getInstance().confirmUpload(contract, documentName, filePath, new GwtCallbackWrapper<Void>(UploadDialog.this, false) {

				@Override
				public void onSuccess(Void result) {
					launchEtl();
				}
			}.getAsyncCallback());
		}
	};

	private void launchEtl() {
		ArchitectService.Connect.getInstance().getLinkedItems(contract.getId(), new GwtCallbackWrapper<List<DocumentItem>>(this, true) {

			@Override
			public void onSuccess(List<DocumentItem> result) {
				parent.refresh(null);
				hide();
				
				if (result != null) {
					int nbEtl = 0;
					DocumentItem onlyEtl = null;
					for (DocumentItem item : result) {
						if (item.getItem().getType() == IRepositoryApi.GTW_TYPE) {
							onlyEtl = item;
							nbEtl++;
						}
					}
					
					if (nbEtl == 1) {
						final DocumentItem selectedEtl = onlyEtl;
						
						final InformationsDialog dial = new InformationsDialog(Labels.lblCnst.LaunchEtl(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.WouldYouLikeToLaunchEtl(), true);
						dial.center();
						dial.addCloseHandler(new CloseHandler<PopupPanel>() {
							
							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								if (dial.isConfirm()) {
									PortailRepositoryItem itemReport = new PortailRepositoryItem(selectedEtl.getItem(), IRepositoryApi.BIG);

									ViewerDialog dial = new ViewerDialog(itemReport);
									dial.center();
								}
							}
						});
					}
					else {
						ItemLinkedDialog dial = new ItemLinkedDialog(parent.getInfoUser(), contract, true, nbEtl == 0);
						dial.center();
					}
				}
			}
		}.getAsyncCallback());
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
