package bpm.fwr.client.panels;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.WysiwygPanel;
import bpm.fwr.client.dialogs.FwrRepositoryDialog;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.services.FwrServiceConnection;
import bpm.fwr.client.wizard.template.ReportCreationWizard;
import bpm.fwr.shared.models.TreeParentDTO;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.Template;

import org.realityforge.gwt.keycloak.Keycloak;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TopToolbar extends Composite {

	private static TopToolbarUiBinder uiBinder = GWT.create(TopToolbarUiBinder.class);

	interface TopToolbarUiBinder extends UiBinder<Widget, TopToolbar> {
	}

	interface MyStyle extends CssResource {
		String imgLoggedUser();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbar, panelFullInfo, panelMinimalInfo;
	
	@UiField
	Image imgNew, imgOpen, imgOut, imgUser;
	
	@UiField
	HTML lblLoggedAs;
	
	private WysiwygPanel mainPanel;
	private InfoUser infoUser;
	private Keycloak keycloak;

	public TopToolbar(WysiwygPanel mainPanel, InfoUser infoUser, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.infoUser = infoUser;
		this.keycloak = keycloak;

		loadingTheme();
		
//		String vanillaCss = infoUser.getUser().getVanillaTheme();
//		if(vanillaCss != null && !vanillaCss.isEmpty()){
//			for(int i=0; i<VanillaCSS.CSS_FILE_NAMES.length; i++){
//				if(VanillaCSS.CSS_FILE_NAMES[i][1].equals(vanillaCss)){
//					loadingTheme();
//					break;
//				}
//			}
//		}
		loadUserInfo(infoUser.getUser());
	}

	private void loadingTheme() {
		bpm.gwt.commons.client.utils.ToolsGWT.setLinkHref("themeCssElement", "cssHelper?loadLoginCss=true&cssFileName=");
	}
	
	public void loadUserInfo(User user) {
		lblLoggedAs.setHTML("You are logged as <b>" + user.getLogin() + "</b>");
		lblLoggedAs.setVisible(false);
		
		LoginService.Connect.getInstance().getUserImg(user.getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				imgUser.setUrl(WysiwygImage.INSTANCE.user_profile().getSafeUri());
				imgUser.addStyleName(style.imgLoggedUser());
			}

			@Override
			public void onSuccess(String result) {
				if(result != null && !result.isEmpty()){
					imgUser.setUrl(result);
				}
				else {
					imgUser.setUrl(WysiwygImage.INSTANCE.user_profile().getSafeUri());
				}
				imgUser.addStyleName(style.imgLoggedUser());
			}
		});
	}
	
	@UiHandler("imgNew")
	public void onNewClick(ClickEvent event) {
//		String name = mainPanel.buildNameReport();
//		mainPanel.addTabReport(name, null);
		
		final ReportCreationWizard wizard = new ReportCreationWizard(infoUser.getUser().getId());
		wizard.addFinishListener(finishListener);
		wizard.center();
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		@SuppressWarnings("unchecked")
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof Template<?>) {
				mainPanel.openTemplate((Template<IReport>) result);
			}
		}
	};

	@UiHandler("imgOpen")
	public void onOpenClick(ClickEvent event) {
		Bpm_fwr.getInstance().showWaitPart(true);
		
		FwrServiceConnection.Connect.getInstance().browseRepositoryService(new AsyncCallback<TreeParentDTO>() {

			@Override
			public void onFailure(Throwable e) {
				e.printStackTrace();
				
				Bpm_fwr.getInstance().showWaitPart(false);
			}

			@Override
			public void onSuccess(TreeParentDTO metadatas) {
				Bpm_fwr.getInstance().showWaitPart(false);
				
				FwrRepositoryDialog repositoryDialog = new FwrRepositoryDialog(mainPanel, metadatas);
				repositoryDialog.center();
			}
		});
	}

	@UiHandler("imgUser")
	public void onUserProfilClick(ClickEvent event) {
		UserPanel userPanel = new UserPanel(infoUser);
		userPanel.show();
	}

	@UiHandler("imgOut")
	public void onLogoutClick(ClickEvent event) {
		LoginService.Connect.getInstance().disconnectUser(new AsyncCallback<Void>() {

			public void onFailure(Throwable arg0) {
				deconnect();
			}

			public void onSuccess(Void arg0) {
				deconnect();
			}

		});
	}
	
	private void deconnect() {
		try {
			String sessionID = Cookies.getCookie(CommonConstants.SID);
			if (sessionID != null) {
				Cookies.removeCookie(CommonConstants.SID, "/");
			}
		} catch(Exception e) { }
		
		if (keycloak != null) {
			keycloak.logout();
		}
		
		String url = GWT.getHostPageBaseURL();
		bpm.gwt.commons.client.utils.ToolsGWT.changeCurrURL(url);
	}
}
