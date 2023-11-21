package bpm.fd.web.client.wizard;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.TemplateWidget;
import bpm.gwt.commons.client.panels.TemplateWidget.ITemplateManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.repository.IDashboard;
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

public class DashboardTemplatePage extends Composite implements IGwtPage, ITemplateManager<IDashboard> {

	private static DashboardTemplatePageUiBinder uiBinder = GWT.create(DashboardTemplatePageUiBinder.class);

	interface DashboardTemplatePageUiBinder extends UiBinder<Widget, DashboardTemplatePage> {
	}
	
	@UiField
	HTMLPanel panelContent;

	private DashboardCreationWizard parent;

	private List<Template<IDashboard>> templates;
	private TemplateWidget<IDashboard> selectedWidget;

	public DashboardTemplatePage(DashboardCreationWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		loadTemplates();
	}

	private void loadTemplates() {
		parent.showWaitPart(true);
		
		panelContent.clear();

		DashboardService.Connect.getInstance().getDashboardTemplates(new GwtCallbackWrapper<List<Template<IDashboard>>>(parent, true) {

			@Override
			public void onSuccess(List<Template<IDashboard>> result) {
				loadTemplates(result);
			}
		}.getAsyncCallback());
	}

	private void loadTemplates(List<Template<IDashboard>> templates) {
		this.templates = templates != null ? templates : new ArrayList<Template<IDashboard>>();
		this.templates.add(0, buildBlankTemplate());
		
		for (Template<IDashboard> template : templates) {
			panelContent.add(new TemplateWidget<IDashboard>(this, template));;
		}
	}

	private Template<IDashboard> buildBlankTemplate() {
		Template<IDashboard> blankTemplate = new Template<IDashboard>(LabelsConstants.lblCnst.BlankTemplate(), TypeTemplate.DASHBOARD);
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
	
	public Template<IDashboard> getSelectedTemplate() {
		return selectedWidget.getTemplate();
	}

	@Override
	public void deleteTemplate(final Template<IDashboard> template) {
		final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.DeleteTemplate(), LabelsConstants.lblCnst.Yes(), LabelsConstants.lblCnst.No(), LabelsConstants.lblCnst.DeleteTemplateConfirmationMessage(), true);
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isClose()) {
					return;
				}
				
				if (dial.isConfirm()) {
					parent.showWaitPart(true);
					
					DashboardService.Connect.getInstance().deleteTemplate(template, new GwtCallbackWrapper<Void>(parent, true) {

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
	public void select(TemplateWidget<IDashboard> widget) {
		if (selectedWidget != null) {
			selectedWidget.setSelected(false);
		}
		
		this.selectedWidget = widget;
		selectedWidget.setSelected(true);
		
		
		parent.updateBtn();
	}
}
