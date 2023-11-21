package bpm.metadata.web.client;

import org.realityforge.gwt.keycloak.Keycloak;

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

import bpm.gwt.commons.client.dialog.RepositoryDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.listeners.FinishListener;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.panels.IMainPanel;
import bpm.gwt.commons.client.panels.TopToolbar;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.metadata.web.client.I18N.Labels;
import bpm.metadata.web.client.images.MetadataImage;
import bpm.metadata.web.client.wizard.MetadataCreationD4CWizard;
import bpm.metadata.web.client.wizard.MetadataCreationWizard;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MainPanel extends CompositeWaitPanel implements IMainPanel {

	private static final int CLASSIC_TOP_TOOLBAR = 70;
	private static final int CLASSIC_FOOTER = 30;

	private static final int FULL_SCREEN_TOP_TOOLBAR = 25;
	private static final int FULL_SCREEN_FOOTER = 0;

	private static final int MINIMAL_TOP_TOOLBAR = 25;
	private static final int MINIMAL_FOOTER = 0;

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
	}

	private TopToolbar buildTopToolbar(Keycloak keycloak) {
		TopToolbar topToolbar = new TopToolbar(this, infoUser, "Vanilla Metadata", CommonImages.INSTANCE.vanilla_webmetadata_bandeau_64(), keycloak);
		topToolbar.addButton(MetadataImage.INSTANCE.New(), Labels.lblCnst.NewMetadata(), newHandler);
		topToolbar.addButton(MetadataImage.INSTANCE.data4citizen_logo_only(), Labels.lblCnst.NewMetadataD4C(), newD4CHandler);
		topToolbar.addButton(MetadataImage.INSTANCE.Open(), Labels.lblCnst.OpenMetadata(), openHandler);
		return topToolbar;
	}

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

	private void openMetadata(RepositoryItem item) {
		showWaitPart(true);
		
		FmdtServices.Connect.getInstance().openMetadata(item, new GwtCallbackWrapper<bpm.gwt.commons.shared.fmdt.metadata.Metadata>(this, true) {

			@Override
			public void onSuccess(bpm.gwt.commons.shared.fmdt.metadata.Metadata result) {
				showCarefullMessage();
				
				contentDisplayPanel.openCreation((bpm.gwt.commons.shared.fmdt.metadata.Metadata) result);
			}
		}.getAsyncCallback());
	}

	protected void showCarefullMessage() {
		MessageHelper.openMessageDialog(Labels.lblCnst.BeCareful(), Labels.lblCnst.MetadataLooseFunctions());
	}

	private ClickHandler newHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			MetadataCreationWizard wizard = new MetadataCreationWizard(infoUser.getUser().getId());
			wizard.addFinishListener(finishListener);
			wizard.center();
		}
	};

	private ClickHandler newD4CHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			MetadataCreationD4CWizard wizard = new MetadataCreationD4CWizard(infoUser.getUser().getId());
			wizard.addFinishListener(finishListener);
			wizard.center();
		}
	};

	private ClickHandler openHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final RepositoryDialog dial = new RepositoryDialog(IRepositoryApi.FMDT_TYPE);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm() && dial.getSelectedItem() != null) {
						RepositoryItem item = dial.getSelectedItem();
						if (item.getType() == IRepositoryApi.FMDT_TYPE) {
							openMetadata(item);
						}
					}
				}
			});
		}
	};

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			if (result instanceof bpm.gwt.commons.shared.fmdt.metadata.Metadata) {
				contentDisplayPanel.openCreation((bpm.gwt.commons.shared.fmdt.metadata.Metadata) result);
			}
		}
	};
}
