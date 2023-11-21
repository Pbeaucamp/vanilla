package bpm.gwt.commons.client.viewer.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomListBox;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.gwt.commons.shared.cmis.CmisInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CmisShareConnectionPage extends Composite implements IGwtPage {
	private static CmisShareConnectionPageUiBinder uiBinder = GWT.create(CmisShareConnectionPageUiBinder.class);

	interface CmisShareConnectionPageUiBinder extends UiBinder<Widget, CmisShareConnectionPage> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelSimple, panelExpert;

	@UiField
	LabelTextBox txtUrl, txtLogin, txtPassword;

	@UiField
	LabelTextArea txtExpert;

	@UiField
	CustomListBox lstRepositories;
	
	@UiField
	Button btnExpertView;

	private IGwtWizard parent;
	private int index;

	private boolean isExpert;
	
	private CmisInformations cmisInfos;

	public CmisShareConnectionPage(IGwtWizard parent, int index) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return cmisInfos != null && getSelectedRepositoryId() != null;
	}

	@UiHandler("btnExpertView")
	public void onExpertView(ClickEvent event) {
		this.isExpert = !isExpert;

		btnExpertView.setText(isExpert ? LabelsConstants.lblCnst.SimpleView() : LabelsConstants.lblCnst.ExpertView());
		
		panelSimple.setVisible(!isExpert);
		panelExpert.setVisible(isExpert);
	}

	@UiHandler("btnLoadRepositories")
	public void onLoadRepositories(ClickEvent event) {
		CmisInformations cmisInfos = null;
		String properties = null;
		if (isExpert) {
			properties = txtExpert.getText();
		}
		else {
			String url = txtUrl.getText();
			String login = txtLogin.getText();
			String password = txtPassword.getText();

			cmisInfos = new CmisInformations(login, password, url);
		}
		
		if (cmisInfos != null || properties != null) {
			ReportingService.Connect.getInstance().getCmisRepositories(cmisInfos, properties, new GwtCallbackWrapper<CmisInformations>(parent, true, true) {

				@Override
				public void onSuccess(CmisInformations result) {
					CmisShareConnectionPage.this.cmisInfos = result;
					
					if (result.getAvailableRepositories() != null && !result.getAvailableRepositories().isEmpty()) {
						for (String repository : result.getAvailableRepositories()) {
							lstRepositories.addItem(repository);
						}
						
						lstRepositories.setSelectedIndex(0);
					}

					parent.updateBtn();
				}
			}.getAsyncCallback());
		}
	}

	private String getSelectedRepositoryId() {
		String repositoryId = lstRepositories.getValue(lstRepositories.getSelectedIndex());
		return !repositoryId.isEmpty() ? repositoryId : null;
	}
	
	public CmisInformations getCmisInfos() {
		String repositoryId = getSelectedRepositoryId();
		
		cmisInfos.setSelectedRepositoryId(repositoryId);
		return cmisInfos;
	}
}
