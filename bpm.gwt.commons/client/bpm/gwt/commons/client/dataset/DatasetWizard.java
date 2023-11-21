package bpm.gwt.commons.client.dataset;

import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

public class DatasetWizard extends GwtWizard {

	private IGwtPage previousPage;
	private IGwtPage actualPage;

	private DatasetJdbcPage jdbcPage;
	private DatasetRPage rPage;
	private DatasetFmdtPage fmdtPage;
	private DatasetDefPage defPage;
	private DatasetCsvPage csvPage;
	private DatasetCsvVanillaPage csvVanillaPage;
	private DatasetHBasePage hbasePage;
	private DatasetSocialPage socialPage;
	private DatasetArchitectPage architectPage;

	private boolean newDs;
	private Dataset dataset;
	private static Datasource currentDataSource;
	private User user;

	private boolean confirm = false;

	public DatasetWizard(User user) {
		super(LabelsConstants.lblCnst.AddNewDataset());
		this.newDs = true;
		this.user = user;
		defPage = new DatasetDefPage(this);
		// reqPage = new DatasetJdbcPage(this);
		setCurrentPage(defPage);
	}

	public DatasetWizard(Dataset dataset, User user) {
		super(LabelsConstants.lblCnst.EditDataset());
		this.newDs = false;
		this.dataset = dataset;
		this.user = user;
		defPage = new DatasetDefPage(this, dataset);
		// reqPage = new DatasetJdbcPage(this, dataset);
		setCurrentPage(defPage);
	}

	@Override
	public boolean canFinish() {
		if (actualPage == jdbcPage) {
			return jdbcPage.isComplete();
		}
		else if (actualPage == rPage) {
			return rPage.isComplete();
		}
		else if (actualPage == fmdtPage) {
			return fmdtPage.isComplete();
		}
		else if (actualPage == csvPage) {
			return csvPage.isComplete();
		}
		else if (actualPage == hbasePage) {
			return hbasePage.isComplete();
		}
		else if (actualPage == csvVanillaPage) {
			return csvVanillaPage.isComplete();
		}
		else if (actualPage == socialPage) {
			return socialPage.isComplete();
		}
		else if (actualPage == architectPage) {
			return architectPage.isComplete();
		}
		return false;
	}

	@Override
	public void updateBtn() {
		setBtnBackState(actualPage.canGoBack() ? true : false);
		setBtnNextState(actualPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		previousPage = actualPage;
		actualPage = page;
		setContentPanel((Composite) actualPage);
		updateBtn();
	}

	@Override
	protected void onClickFinish() {
		String datasetName = dataset.getName();
		if (defPage.getSelectedDatasource().getType() == DatasourceType.FMDT) {

			FmdtServices.Connect.getInstance().getDatasetFromFmdtQuery(fmdtPage.getDesignerPanel().refreshBuilder(), fmdtPage.getDriller().getMetadataId(), fmdtPage.getDriller().getModelName(), fmdtPage.getDriller().getPackageName(), new AsyncCallback<Dataset>() {

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
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.CSV) {
			List<DataColumn> columns = csvPage.getMetaColumns();
			String query = csvPage.getQuery(datasetName);
			dataset.setMetacolumns(columns);
			dataset.setRequest(query);
			saveOrUpdateDataset();
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.CSVVanilla) {
			List<DataColumn> columns = csvVanillaPage.getMetaColumns();
			String query = csvVanillaPage.getQuery(datasetName);
			dataset.setMetacolumns(columns);
			dataset.setRequest(query);
			saveOrUpdateDataset();
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.R) {
			List<DataColumn> columns = rPage.getMetaColumns();
			String query = rPage.getQuery(datasetName);
			
			dataset.setMetacolumns(columns);
			dataset.setRequest(query);
			saveOrUpdateDataset();
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.HBase) {
			List<DataColumn> columns = hbasePage.getMetaColumns();
			String query = hbasePage.getQuery(datasetName);
			dataset.setMetacolumns(columns);
			dataset.setRequest(query);
			saveOrUpdateDataset();
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.SOCIAL) {
			List<DataColumn> columns = socialPage.getMetaColumns();
			String query = socialPage.getQuery(datasetName);
			dataset.setMetacolumns(columns);
			dataset.setRequest(query);
			saveOrUpdateDataset();
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.ARCHITECT) {
			List<DataColumn> columns = architectPage.getMetaColumns();
			String query = architectPage.getQuery(datasetName);
			dataset.setMetacolumns(columns);
			dataset.setRequest(query);
			saveOrUpdateDataset();
		}
		else {
			saveOrUpdateDataset();
		}

	}

	private void saveOrUpdateDataset() {
		confirm = true;

		if (newDs) {
			CommonService.Connect.getInstance().addDataset(dataset, new AsyncCallback<Dataset>() {

				@Override
				public void onSuccess(Dataset result) {
					getDataset().setId(result.getId());
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasetCreated(), false);
					dial.center();

					DatasetWizard.this.hide();

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

					DatasetWizard.this.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasetError(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
	}

	@Override
	protected void onNextClick() {
		if (defPage.getSelectedDatasource().getType() == DatasourceType.FMDT) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			fmdtPage = new DatasetFmdtPage(this, currentDataSource, dataset);
			setCurrentPage(fmdtPage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.CSV) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			csvPage = new DatasetCsvPage(this, currentDataSource, dataset);
			setCurrentPage(csvPage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.JDBC) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			jdbcPage = new DatasetJdbcPage(this, currentDataSource, dataset);
			setCurrentPage(jdbcPage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.R) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			rPage = new DatasetRPage(this, currentDataSource, dataset);
			setCurrentPage(rPage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.HBase) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			hbasePage = new DatasetHBasePage(this, currentDataSource, dataset);
			setCurrentPage(hbasePage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.CSVVanilla) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			csvVanillaPage = new DatasetCsvVanillaPage(this, currentDataSource, dataset);
			setCurrentPage(csvVanillaPage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.SOCIAL) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			socialPage = new DatasetSocialPage(this, currentDataSource, dataset);
			setCurrentPage(socialPage);
		}
		else if (defPage.getSelectedDatasource().getType() == DatasourceType.ARCHITECT) {
			dataset = defPage.getDataset();
			currentDataSource = defPage.getSelectedDatasource();
			architectPage = new DatasetArchitectPage(this, currentDataSource, dataset);
			setCurrentPage(architectPage);
		}
	}

	@Override
	protected void onBackClick() {
		// dataset = reqPage.getDataset();
		// defPage.refresh();
		setCurrentPage(defPage);
	}

	public Dataset getDataset() {
		return dataset;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public User getUser() {
		return user;
	}
}
