package bpm.fwr.client.wizard.template;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.client.services.WysiwygService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.TemplateWidget;
import bpm.gwt.commons.client.panels.TemplateWidget.ITemplateManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReportTemplatePage extends Composite implements IGwtPage, ITemplateManager<IReport> {

	private static DashboardTemplatePageUiBinder uiBinder = GWT.create(DashboardTemplatePageUiBinder.class);

	interface DashboardTemplatePageUiBinder extends UiBinder<Widget, ReportTemplatePage> {
	}
	
	@UiField
	HTMLPanel panelContent;

	private ReportCreationWizard parent;

	private List<Template<IReport>> templates;
	private TemplateWidget<IReport> selectedWidget;

	public ReportTemplatePage(ReportCreationWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		loadTemplates();
	}

	private void loadTemplates() {
		parent.showWaitPart(true);
		
		panelContent.clear();

		WysiwygService.Connect.getInstance().getTemplates(new GwtCallbackWrapper<List<Template<IReport>>>(parent, true) {

			@Override
			public void onSuccess(List<Template<IReport>> result) {
				loadTemplates(result);
			}
		}.getAsyncCallback());
	}

	private void loadTemplates(List<Template<IReport>> templates) {
		this.templates = templates != null ? templates : new ArrayList<Template<IReport>>();
		this.templates.add(0, buildBlankTemplate());
		
		for (Template<IReport> template : templates) {
			panelContent.add(new TemplateWidget<IReport>(this, template));;
		}
	}

	private Template<IReport> buildBlankTemplate() {
		Template<IReport> blankTemplate = new Template<IReport>(LabelsConstants.lblCnst.BlankTemplate(), TypeTemplate.DASHBOARD);
		return blankTemplate;
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return selectedWidget != null;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}
	
	public Template<IReport> getSelectedTemplate() {
		return selectedWidget.getTemplate();
	}

	@Override
	public void deleteTemplate(final Template<IReport> template) {
		final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.DeleteTemplate(), LabelsConstants.lblCnst.Yes(), LabelsConstants.lblCnst.No(), LabelsConstants.lblCnst.DeleteTemplateConfirmationMessage(), true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isClose()) {
					return;
				}
				
				if (dial.isConfirm()) {
					parent.showWaitPart(true);
					
					WysiwygService.Connect.getInstance().deleteTemplate(template, new GwtCallbackWrapper<Void>(parent, true) {

						@Override
						public void onSuccess(Void result) {
							loadTemplates();
						}
						
					}.getAsyncCallback());
				}
			}
		});
		dial.center();
	}

	@Override
	public void select(TemplateWidget<IReport> widget) {
		if (selectedWidget != null) {
			selectedWidget.setSelected(false);
		}
		
		this.selectedWidget = widget;
		selectedWidget.setSelected(true);
		
		
		parent.updateBtn();
	}
}
