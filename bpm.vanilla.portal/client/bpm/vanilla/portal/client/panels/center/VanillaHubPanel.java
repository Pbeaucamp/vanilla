package bpm.vanilla.portal.client.panels.center;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.workflow.hub.IHubManager;
import bpm.gwt.workflow.commons.client.workflow.hub.VanillaHubViewer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class VanillaHubPanel extends Tab implements IHubManager {

	private static VanillaHubPanelUiBinder uiBinder = GWT.create(VanillaHubPanelUiBinder.class);

	interface VanillaHubPanelUiBinder extends UiBinder<Widget, VanillaHubPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent;
	
	private InfoUser infoUser;
	
	private VanillaHubViewer hubViewer;

	public VanillaHubPanel(TabManager tabManager, InfoUser infoUser) {
		super(tabManager, ToolsGWT.lblCnst.VanillaHub(), true);
		this.infoUser = infoUser;
		
		add(uiBinder.createAndBindUi(this));
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		
		this.hubViewer = new VanillaHubViewer(this, infoUser);
		panelContent.setWidget(hubViewer);
	}

	@UiHandler("btnOpenVanillaHub")
	public void onOpenClick(ClickEvent event) {
		String url = infoUser.getUrl(IRepositoryApi.HUB);
		openWebApp(url);
	}
	
	private void openWebApp(String webAppUrl) {
		String locale = LocaleInfo.getCurrentLocale().getLocaleName();
		CommonService.Connect.getInstance().forwardSecurityUrl(webAppUrl, locale, new GwtCallbackWrapper<String>(null, false) {

			@Override
			public void onSuccess(String url) {
				ToolsGWT.doRedirect(url);
			}
		}.getAsyncCallback());
	}

	//On ne supporte pas les Tabs
	@Override
	public boolean canEditWorkflow() {
		return false;
	}

	@Override
	public void displayWorkflow(Workflow workflow) {
		// Not supported
	}

	@Override
	public IResourceManager getResourceManager() {
		return null;
	}
}
