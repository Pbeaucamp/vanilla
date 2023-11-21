package bpm.gwt.commons.client.dialog;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.viewer.dialog.ViewerDialog;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class LaunchContractETLDialog extends AbstractDialogBox {

	private static LaunchContractETLDialogUiBinder uiBinder = GWT.create(LaunchContractETLDialogUiBinder.class);

	interface LaunchContractETLDialogUiBinder extends UiBinder<Widget, LaunchContractETLDialog> {
	}
	
	@UiField
	ListBoxWithButton<Group> lstGroups;

	private int contractId;
	
	public LaunchContractETLDialog(int contractId) {
		super(LabelsConstants.lblCnst.RunItem(), false, true);
		this.contractId = contractId;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		loadGroups();
	}
	
	private void loadGroups() {
		showWaitPart(true);
		LoginService.Connect.getInstance().getInfoUser(new GwtCallbackWrapper<InfoUser>(this, true) {

			@Override
			public void onSuccess(InfoUser result) {
				lstGroups.setList(result.getAvailableGroups());
			}
		}.getAsyncCallback());
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
			
			showWaitPart(true);
			CommonService.Connect.getInstance().getLinkedItems(contractId, new GwtCallbackWrapper<List<DocumentItem>>(LaunchContractETLDialog.this, true) {

				@Override
				public void onSuccess(List<DocumentItem> result) {
					//On récupère le premier input
					if (result != null) {
						DocumentItem selectedEtl = null;
						for (DocumentItem item : result) {
							if ((item.getItem().getType() == IRepositoryApi.GTW_TYPE || item.getItem().getType() == IRepositoryApi.BIW_TYPE) && item.isInput()) {
								selectedEtl = item;
							}
						}
						
						if (selectedEtl != null) {
							String type = selectedEtl.getItem().getType() == IRepositoryApi.GTW_TYPE ? IRepositoryApi.BIG : IRepositoryApi.BIW;
							
							Group selectedGroup = lstGroups.getSelectedObject();
							PortailRepositoryItem itemReport = new PortailRepositoryItem(selectedEtl.getItem(), type);
		
							ViewerDialog dial = new ViewerDialog(itemReport, selectedGroup);
							dial.center();
						}
					}
				}
			}.getAsyncCallback());
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
