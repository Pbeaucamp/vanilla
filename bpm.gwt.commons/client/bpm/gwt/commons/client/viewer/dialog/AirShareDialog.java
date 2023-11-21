package bpm.gwt.commons.client.viewer.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class AirShareDialog extends AbstractDialogBox {

	private static AirShareDialogUiBinder uiBinder = GWT.create(AirShareDialogUiBinder.class);

	interface AirShareDialogUiBinder extends UiBinder<Widget, AirShareDialog> {
	}
	
	@UiField
	LabelTextBox txtDatasourceName, txtDatasetName;
	
	private IWait waitPanel;
	
	private FmdtQueryBuilder builder;
	private FmdtQueryDriller driller;

	public AirShareDialog(IWait waitPanel, FmdtQueryBuilder builder, FmdtQueryDriller driller) {
		super(LabelsConstants.lblCnst.ShareAir(), false, true);
		this.waitPanel = waitPanel;
		this.builder = builder;
		this.driller = driller;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			waitPanel.showWaitPart(true);
			
			FmdtServices.Connect.getInstance().getDatasetFromFmdtQuery(builder, driller.getMetadataId(), driller.getModelName(), driller.getPackageName(), new GwtCallbackWrapper<Dataset>(waitPanel, true) {

				@Override
				public void onSuccess(Dataset result) {
					saveDatasourceAndDataset(result);
				}
			}.getAsyncCallback());
		}
	};

	private void saveDatasourceAndDataset(Dataset dataset) {
		String datasourceName = txtDatasourceName.getText();
		String datasetName = txtDatasetName.getText();
		
		waitPanel.showWaitPart(true);
		
		CommonService.Connect.getInstance().addDatasourceAndDataset(driller.getMetadataId(), driller.getModelName(), driller.getPackageName(), datasourceName, datasetName, dataset, new GwtCallbackWrapper<Void>(waitPanel, true) {

			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.DatasetCreated());
				
				AirShareDialog.this.hide();
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
