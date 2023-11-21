package bpm.fd.web.client;

import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.images.DashboardImage;
import bpm.fd.web.client.services.DashboardService;
import bpm.fd.web.client.wizard.DashboardCreationWizard;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.panels.IMainPanel;
import bpm.gwt.commons.client.panels.TopToolbar;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel implements IMainPanel {

	private static final int CLASSIC_TOP_TOOLBAR = 70;
	private static final int CLASSIC_FOOTER = 30;

	private static final int FULL_SCREEN_TOP_TOOLBAR = 25;
	private static final int FULL_SCREEN_FOOTER = 0;

	private static final int MINIMAL_TOP_TOOLBAR = 25;
	private static final int MINIMAL_FOOTER = 0;
	
	private static MainPanel instance;

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	@UiField
	DockLayoutPanel dockPanel;

	@UiField
	SimplePanel toolbarPanel, footerPanel, mainPanel;

	private InfoUser infoUser;

	private TopToolbar topToolbar;
	private ContentDisplayPanel contentDisplayPanel;

	public MainPanel(InfoUser infoUser, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;

		topToolbar = buildTopToolbar(keycloak);
		toolbarPanel.setWidget(topToolbar);

		contentDisplayPanel = new ContentDisplayPanel(this, infoUser);
		mainPanel.setWidget(contentDisplayPanel);

		footerPanel.addStyleName(VanillaCSS.BOTTOM);
		this.addStyleName(VanillaCSS.BODY_BACKGROUND);
		
		loadResources();
		
		instance = this;
	}

	private TopToolbar buildTopToolbar(Keycloak keycloak) {
		TopToolbar topToolbar = new TopToolbar(this, infoUser, "Vanilla Dashboard", CommonImages.INSTANCE.vanilla_webdashboard_bandeau_64(), keycloak);
		topToolbar.addButton(DashboardImage.INSTANCE.New(), Labels.lblCnst.NewDashboard(), newHandler);
		topToolbar.addButton(DashboardImage.INSTANCE.Open(), Labels.lblCnst.OpenDashboard(), openHandler);
		return topToolbar;
	}

	private void loadResources() {
		DashboardService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(null, false) {
			@Override
			public void onSuccess(List<Datasource> result) {
				ClientSession.getInstance().setDatasources(result);
			}
		}.getAsyncCallback());
		
		CommonService.Connect.getInstance().getImages(new GwtCallbackWrapper<List<VanillaImage>>(null, false) {
			@Override
			public void onSuccess(List<VanillaImage> result) {
				ClientSession.getInstance().setImages(result);
			}
		}.getAsyncCallback());
		
		DashboardService.Connect.getInstance().getVanillaMaps(new GwtCallbackWrapper<List<MapVanilla>>(null, false) {
			@Override
			public void onSuccess(List<MapVanilla> result) {
				ClientSession.getInstance().setMaps(result);
			}
		}.getAsyncCallback());
		DashboardService.Connect.getInstance().getMetricThemes(new GwtCallbackWrapper<List<Theme>>(null, false) {
			@Override
			public void onSuccess(List<Theme> result) {
				ClientSession.getInstance().setThemes(result);
			}
		}.getAsyncCallback());
	}

	public static MainPanel	getInstance() {
		return instance;
	}
	
	private void openDashboard(RepositoryItem item) {
		showWaitPart(true);
		
		DashboardService.Connect.getInstance().openDashboard(item, new GwtCallbackWrapper<bpm.fd.core.Dashboard>(this, true) {

			@Override
			public void onSuccess(bpm.fd.core.Dashboard result) {
				showCarefullMessage();
				
				contentDisplayPanel.openCreation(result);
			}
		}.getAsyncCallback());
	}	
	
	protected void showCarefullMessage() {
		MessageHelper.openMessageDialog(Labels.lblCnst.BeCareful(), Labels.lblCnst.DashboardLooseFunctions());
	}
	
	private ClickHandler newHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final DashboardCreationWizard wizard = new DashboardCreationWizard(infoUser.getUser().getId());
			wizard.addFinishListener(finishListener);
			wizard.center();
		}
	};

	private ClickHandler openHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.FD_TYPE);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm() && dial.getSelectedItem() != null) {
						RepositoryItem item = dial.getSelectedItem();
						if (item.getType() == IRepositoryApi.FD_TYPE) {
							openDashboard(item);
						}
					}
				}
			});
		}
	};

	private FinishListener finishListener = new FinishListener() {

		@Override
		@SuppressWarnings("unchecked")
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof Template<?>) {
				contentDisplayPanel.openTemplate((Template<IDashboard>) result);
			}
		}
	};

	public void switchView(TypeView typeView) {
		switch (typeView) {
		case CLASSIC_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, CLASSIC_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, CLASSIC_FOOTER);
			break;
		case FULL_SCREEN_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, FULL_SCREEN_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, FULL_SCREEN_FOOTER);
			break;
		case MINIMAL_VIEW:
			dockPanel.setWidgetSize(toolbarPanel, MINIMAL_TOP_TOOLBAR);
			dockPanel.setWidgetSize(footerPanel, MINIMAL_FOOTER);
			break;

		default:
			break;
		}
	}

	@Override
	public void showAbout() {
	}

	@Override
	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}
}
