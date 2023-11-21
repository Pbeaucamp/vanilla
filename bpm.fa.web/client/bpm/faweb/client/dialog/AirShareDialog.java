package bpm.faweb.client.dialog;

import bpm.faweb.client.services.FaWebService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

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
	TextHolderBox txtName;

	private IWait waitPanel;
	private int keySession;
	private int userId;
	private DrillInformations drillInfos;
	
	public AirShareDialog(IWait waitPanel, int keySession, int userId, DrillInformations drillInfos) {
		super(LabelsConstants.lblCnst.ShareAir(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.keySession = keySession;
		this.userId = userId;
		this.drillInfos = drillInfos;
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			waitPanel.showWaitPart(true);
			
			final String name = txtName.getText();
			String separator = ";";
			boolean hasHeader = true;

			FaWebService.Connect.getInstance().uploadCsv(keySession, name, hasHeader, separator, drillInfos, new GwtCallbackWrapper<DatasourceCsv>(waitPanel, true) {

				@Override
				public void onSuccess(DatasourceCsv result) {
					
					Datasource datasource = new Datasource();
					datasource.setName(name);
					datasource.setType(DatasourceType.CSV);
//					datasource.setIdAuthor(userId);
					datasource.setObject(result);
					
					CommonService.Connect.getInstance().addDatasource(datasource, new GwtCallbackWrapper<Datasource>(waitPanel, true) {

						@Override
						public void onSuccess(Datasource result) {
							InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasourceCreated(), false);
							dial.center();

							AirShareDialog.this.hide();
						}
					}.getAsyncCallback());
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

//	private DatasourceCsv createDatasourceCsv() {
//		DatasourceCsv csv = new DatasourceCsv();
//		csv.setSourceType("disk");
//		csv.setSeparator(txtSeparator.getText());
//		csv.setFilePath("C:/Test/CubeAir/Test.csv");
//		csv.setHasHeader(chkHasHeader.getValue());
//		return csv;
//	}
}
