package bpm.gwt.commons.client.dataset;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DatasetCreationDialog extends AbstractDialogBox {

	private static DatasetCreationDialogUiBinder uiBinder = GWT.create(DatasetCreationDialogUiBinder.class);

	interface DatasetCreationDialogUiBinder extends UiBinder<Widget, DatasetCreationDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	LabelTextBox name;

	private DatasetPanel datasetPanel;

	private Dataset dataset;
	private Datasource datasource;
	
	private boolean confirm;
	private boolean newDs;

	public DatasetCreationDialog(User user, Datasource datasource, Dataset dataset) {
		super(LabelsConstants.lblCnst.DatasetManager(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.dataset = dataset;
		this.datasource = datasource;

		if (dataset != null) {
			name.setText(dataset.getName());
		}

		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		switch (datasource.getType()) {
		case CSV:
			datasetPanel = new DatasetCsvPage(new DatasetWizard(user), datasource, dataset);
			break;
		case CSVVanilla:
			datasetPanel = new DatasetCsvVanillaPage(new DatasetWizard(user), datasource, dataset);
			break;
		case FMDT:
			datasetPanel = new DatasetFmdtPage(new DatasetWizard(user), datasource, dataset);
			break;
		case HBase:
			datasetPanel = new DatasetHBasePage(new DatasetWizard(user), datasource, dataset);
			break;
		case JDBC:
			datasetPanel = new DatasetJdbcPage(new DatasetWizard(user), datasource, dataset);
			break;
		case R:
			datasetPanel = new DatasetRPage(new DatasetWizard(user), datasource, dataset);
			break;
		case SOCIAL:
			datasetPanel = new DatasetSocialPage(new DatasetWizard(user), datasource, dataset);
			break;
		case ARCHITECT:
			datasetPanel = new DatasetArchitectPage(new DatasetWizard(user), datasource, dataset);
		}

		contentPanel.add(datasetPanel);
	}

	public ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			DatasetCreationDialog.this.hide();
		}
	};

	public ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(name.getText() == null || name.getText().isEmpty()) {
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasetNameNeeded(), false);
				dial.center();
				return;
			}
			confirm = true;
			if (dataset == null) {
				dataset = new Dataset();
				newDs = true;
			}

			dataset.setName(name.getText());
			dataset.setDatasource(datasource);

			if(datasource.getType() == DatasourceType.FMDT) {
				FmdtServices.Connect.getInstance().getDatasetFromFmdtQuery(((DatasetFmdtPage)datasetPanel).getDesignerPanel().refreshBuilder(), ((DatasetFmdtPage)datasetPanel).getDriller().getMetadataId(), ((DatasetFmdtPage)datasetPanel).getDriller().getModelName(), ((DatasetFmdtPage)datasetPanel).getDriller().getPackageName(), new AsyncCallback<Dataset>() {

					@Override
					public void onFailure(Throwable caught) {
						InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasetError(), caught.getMessage(), caught);
						dial.center();
					}

					@Override
					public void onSuccess(Dataset result) {
						dataset.setMetacolumns(result.getMetacolumns());
						dataset.setRequest(result.getRequest());
						saveOrUpdateDataset();
					}
				});
			}
			
			else {
				dataset.setMetacolumns(datasetPanel.getMetaColumns());
				dataset.setRequest(datasetPanel.getQuery(dataset.getName()));
				saveOrUpdateDataset();
			}
		}
	};

	private void saveOrUpdateDataset() {

		if (newDs) {
			CommonService.Connect.getInstance().addDataset(dataset, new AsyncCallback<Dataset>() {

				@Override
				public void onSuccess(Dataset result) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasetCreated(), false);
					dial.center();

					DatasetCreationDialog.this.hide();

				}

				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasetError(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
		else {
			CommonService.Connect.getInstance().updateDataset(dataset, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasetCreated(), false);
					dial.center();

					DatasetCreationDialog.this.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasetError(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
	}

	public boolean isConfirm() {
		return confirm;
	}

}
