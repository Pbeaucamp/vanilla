package bpm.vanilla.portal.client.wizard;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

public class AddAlertWizard extends GwtWizard implements IWait {

	private IGwtPage currentPage;
	
	private AddAlertDefinitionPage definitionPage;
	private AddAlertEventPage eventPage;
	private AddAlertActionPage actionPage;
	
	private boolean edit = false;
	private Alert selectedAlert;
	private boolean isSuccess = false;
	
	public AddAlertWizard() {
		super(ToolsGWT.lblCnst.AddNewAlert());
		this.edit = false;

		this.selectedAlert = new Alert();
		definitionPage = new AddAlertDefinitionPage(this, 0);
		setCurrentPage(definitionPage);
	}
	
	public AddAlertWizard(Alert selectedAlert) {
		super(ToolsGWT.lblCnst.Edit());
		this.edit = true;
		this.selectedAlert = selectedAlert;

		definitionPage = new AddAlertDefinitionPage(this, 0);
		definitionPage.setName(selectedAlert.getName());
		definitionPage.setDescription(selectedAlert.getDescription());
		definitionPage.setTypeEvent(selectedAlert.getTypeEvent());
		setCurrentPage(definitionPage);

//		int groupId = biPortal.get().getInfoUser().getGroup().getId();
	}

	@Override
	public boolean canFinish() {
		return definitionPage.isComplete() && (eventPage != null && eventPage.isComplete()) && (actionPage != null && actionPage.isComplete());
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if (page instanceof AddAlertDefinitionPage)
			setContentPanel((AddAlertDefinitionPage) page);
		else if (page instanceof AddAlertEventPage)
			setContentPanel((AddAlertEventPage) page);
		else if (page instanceof AddAlertActionPage)
			setContentPanel((AddAlertActionPage) page);
		currentPage = page;
		updateBtn();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	protected void onClickFinish() {
		selectedAlert.setState(actionPage.getState());
		selectedAlert.setAction(actionPage.getAction());
		selectedAlert.setTypeAction(actionPage.getSelectedActionType());
		
		BiPortalService.Connect.getInstance().addOrEditAlert(selectedAlert, new GwtCallbackWrapper<Void>(this, true) {
			@Override
			public void onSuccess(Void result) {
				isSuccess = true;
				AddAlertWizard.this.hide();
//				alertManagerPanel.onRefreshClick(null);
			}
		}.getAsyncCallback());
		
		
	}

	@Override
	protected void onBackClick() {

		if (currentPage instanceof AddAlertEventPage) {
			setCurrentPage(definitionPage);
		}
		else if (currentPage instanceof AddAlertActionPage) {
			setCurrentPage(eventPage);
		}
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof AddAlertDefinitionPage) {
			selectedAlert.setName(definitionPage.getName());
			selectedAlert.setDescription(definitionPage.getDescription());
			selectedAlert.setTypeEvent(definitionPage.getTypeEvent());
			
			eventPage = new AddAlertEventPage(this, 1, selectedAlert);
			
			setCurrentPage(eventPage);
		}
		else if (currentPage instanceof AddAlertEventPage) {
			selectedAlert.setOperator(eventPage.getOperator());
			selectedAlert.setConditions(eventPage.getConditions());
			selectedAlert.setEventObject(eventPage.getAlertModel());
			selectedAlert.setDataProviderId(eventPage.getDataProviderId());
			
			actionPage = new AddAlertActionPage(this, 2, selectedAlert);
			
			setCurrentPage(actionPage);
		}
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public Alert getSelectedAlert() {
		return selectedAlert;
	} 

	
}
