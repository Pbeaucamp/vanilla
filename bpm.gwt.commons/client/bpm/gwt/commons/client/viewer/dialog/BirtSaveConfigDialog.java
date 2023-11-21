package bpm.gwt.commons.client.viewer.dialog;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BirtSaveConfigDialog extends AbstractDialogBox {

	private static BirtSaveConfigDialogUiBinder uiBinder = GWT.create(BirtSaveConfigDialogUiBinder.class);

	interface BirtSaveConfigDialogUiBinder extends UiBinder<Widget, BirtSaveConfigDialog> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Label lblConfigName, lblConfigDescription;
	
	@UiField
	TextBox configName, configDescription;
	
	private IWait viewer;
	
	private LaunchReportInformations itemInfo;
	
	public BirtSaveConfigDialog(IWait viewer, LaunchReportInformations itemInfo) {
		super(LabelsConstants.lblCnst.SaveConfigParam(), false, true);
		this.viewer = viewer;
		this.itemInfo = itemInfo;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		lblConfigName.setText(LabelsConstants.lblCnst.Name());
		lblConfigDescription.setText(LabelsConstants.lblCnst.Description());
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			viewer.showWaitPart(true);
			
			String name = configName.getText();
			String configDesc = configDescription.getText();
			
			if(name.isEmpty()){
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedConfigName());
				return;
			}

			ReportingService.Connect.getInstance().saveReportParameterConfig(itemInfo.getGroupParameters(), itemInfo.getItem(), name, configDesc, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					BirtSaveConfigDialog.this.hide();
					
					caught.printStackTrace();

					viewer.showWaitPart(false);

					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedSaveConfig());
				}

				@Override
				public void onSuccess(Void result) {
					viewer.showWaitPart(false);

					BirtSaveConfigDialog.this.hide();
				}
			});
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			BirtSaveConfigDialog.this.hide();
		}
	};
}
