package bpm.fd.web.client.wizard;

import bpm.fd.core.Dashboard;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.user.client.ui.Composite;

public class DashboardCreationWizard extends GwtWizard {

	private IGwtPage currentPage;

	private DashboardOptionPage optionPage;
	private DashboardTemplatePage templatePage;
	
	public DashboardCreationWizard(int userId) {
		super(Labels.lblCnst.CreateDashboard());
		
		optionPage = new DashboardOptionPage();
		setCurrentPage(optionPage);
	}

	@Override
	public boolean canFinish() {
		return optionPage.isComplete() && templatePage != null && templatePage.isComplete();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		setContentPanel((Composite) page);
		currentPage = page;
		updateBtn();
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof DashboardOptionPage) {
			if (templatePage == null) {
				templatePage = new DashboardTemplatePage(this);
			}
			setCurrentPage(templatePage);
		}
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof DashboardTemplatePage) {
			setCurrentPage(optionPage);
		}
	}

	@Override
	protected void onClickFinish() {
//		Dashboard dashboard = DashboardCreationHelper.buildDefaultDashboard(name, description);
		
		showWaitPart(true);
		
		Template<IDashboard> template = templatePage.getSelectedTemplate();
		if (template.getId() == 0) {
			loadTemplate(template);
			return;
		}
		
		DashboardService.Connect.getInstance().getDashboardTemplate(template.getId(), new GwtCallbackWrapper<Template<IDashboard>>(this, true) {

			@Override
			public void onSuccess(Template<IDashboard> template) {
				loadTemplate(template);
			}
		}.getAsyncCallback());
	}
	
	private void loadTemplate(Template<IDashboard> template) {
		String name = optionPage.getName();
		String description = optionPage.getDescription();
		
		Dashboard dashboard = new Dashboard();
		if (template.getItem() != null) {
			dashboard = (Dashboard) template.getItem();
		}
		else {
			template.setItem(dashboard);
		}
		dashboard.setName(name);
		dashboard.setDescription(description);
		
		finish(template, DashboardCreationWizard.this, null);
		
		hide();
	}
}
