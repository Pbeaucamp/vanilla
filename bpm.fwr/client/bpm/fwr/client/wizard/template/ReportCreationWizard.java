package bpm.fwr.client.wizard.template;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.services.WysiwygService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.user.client.ui.Composite;

public class ReportCreationWizard extends GwtWizard {

	private IGwtPage currentPage;

	private ReportOptionPage optionPage;
	private ReportTemplatePage templatePage;
	
	public ReportCreationWizard(int userId) {
		super(Bpm_fwr.LBLW.CreateNew());
		
		optionPage = new ReportOptionPage();
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
		if (currentPage instanceof ReportOptionPage) {
			if (templatePage == null) {
				templatePage = new ReportTemplatePage(this);
			}
			setCurrentPage(templatePage);
		}
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof ReportTemplatePage) {
			setCurrentPage(optionPage);
		}
	}

	@Override
	protected void onClickFinish() {
		showWaitPart(true);
		
		Template<IReport> template = templatePage.getSelectedTemplate();
		if (template.getId() == 0) {
			loadTemplate(template);
			return;
		}
		
		WysiwygService.Connect.getInstance().getTemplate(template.getId(), new GwtCallbackWrapper<Template<IReport>>(this, true) {

			@Override
			public void onSuccess(Template<IReport> template) {
				loadTemplate(template);
			}
		}.getAsyncCallback());
	}
	
	private void loadTemplate(Template<IReport> template) {
		String name = optionPage.getName();
		String description = optionPage.getDescription();
		
		FWRReport report = new FWRReport();
		if (template.getItem() != null) {
			report = (FWRReport) template.getItem();
		}
		else {
			template.setItem(report);
		}
		report.setName(name);
		report.setDescription(description);
		
		template.setName(name);
		
		finish(template, ReportCreationWizard.this, null);
		
		hide();
	}
}
