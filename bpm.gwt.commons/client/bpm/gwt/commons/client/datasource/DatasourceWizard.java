package bpm.gwt.commons.client.datasource;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

public class DatasourceWizard extends GwtWizard {
	
	private IGwtPage previousPage;
	private IGwtPage actualPage;
	
	private DatasourceTypePage typePage;
	private IDatasourceObjectPage objectPage;
	
	private Datasource datasource;

	public DatasourceWizard() {
		super(LabelsConstants.lblCnst.CreateUpdateDatasource());
		
		typePage = new DatasourceTypePage(this);
		
		setCurrentPage(typePage);
	}
	public DatasourceWizard(Datasource datasource) {
		super(LabelsConstants.lblCnst.CreateUpdateDatasource());
		
		this.datasource = datasource;
		
		typePage = new DatasourceTypePage(this, datasource);
		
		setCurrentPage(typePage);
	}

	@Override
	public boolean canFinish() {
		if(actualPage != typePage) {
			return true;
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
		setContentPanel((Composite)actualPage);
		updateBtn();
	}

	@Override
	protected void onClickFinish() {
		if(!actualPage.isComplete()){
			return;
		}
		
		boolean newDs = false;
		if(datasource == null) {
			newDs = true;
			datasource = new Datasource();
		}
		datasource.setName(typePage.getName());
		datasource.setType(typePage.getSelectedType());
		datasource.setObject(objectPage.getDatasourceObject());
		
		if(newDs) {
			CommonService.Connect.getInstance().addDatasource(datasource, new AsyncCallback<Datasource>() {
				
				@Override
				public void onSuccess(Datasource result) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasourceCreated(), false);
					dial.center();
					
					DatasourceWizard.this.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasourceError(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
		else {
			CommonService.Connect.getInstance().updateDatasource(datasource, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasourceCreated(), false);
					dial.center();
					
					DatasourceWizard.this.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasourceError(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
	}

	@Override
	protected void onNextClick() {
		DatasourceType type = typePage.getSelectedType();
		
		switch(type) {
			case JDBC:
				if(!(objectPage instanceof DatasourceJdbcPage)) {
					objectPage = new DatasourceJdbcPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case FMDT:
				if(!(objectPage instanceof DatasourceFmdtPage)) {
					objectPage = new DatasourceFmdtPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case CSV:
				if(!(objectPage instanceof DatasourceCsvPage)) {
					objectPage = new DatasourceCsvPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case R:
				if(!(objectPage instanceof DatasourceRPage)) {
					objectPage = new DatasourceRPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case HBase:
				if(!(objectPage instanceof DatasourceHBasePage)) {
					objectPage = new DatasourceHBasePage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case CSVVanilla:
				if(!(objectPage instanceof DatasourceCsvVanillaPage)) {
					objectPage = new DatasourceCsvVanillaPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case SOCIAL:
				if(!(objectPage instanceof DatasourceSocialPage)) {
					objectPage = new DatasourceSocialPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
			case ARCHITECT:
				if(!(objectPage instanceof DatasourceArchitectPage)) {
					objectPage = new DatasourceArchitectPage(this, datasource);
				}
				setCurrentPage(objectPage);
				break;
		default:
			break;
		}
		
	}

	@Override
	protected void onBackClick() {
		setCurrentPage(typePage);
	}

}
