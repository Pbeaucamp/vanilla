package bpm.architect.web.client;

import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.panels.ConsultPanel;
import bpm.architect.web.client.panels.DataVizTabPanel;
import bpm.architect.web.client.panels.RulesPanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.panels.IMainPanel;
import bpm.gwt.commons.client.panels.NavigationItem;
import bpm.gwt.commons.client.panels.NavigationMenuPanel;
import bpm.gwt.commons.client.panels.TopToolbar;
import bpm.gwt.commons.client.popup.ViewsPopup.TypeView;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel implements IMainPanel {

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	@UiField(provided = true)
	TopToolbar topToolbar;

	@UiField
	NavigationMenuPanel navigationPanel;

	@UiField
	SimplePanel footerPanel, contentPanel;
	
	private static MainPanel instance;

	private InfoUser infoUser;

	private ConsultPanel consultPanel;
	private DataVizTabPanel datavizPanel;
	private RulesPanel rulesPanel;

	public static MainPanel getInstance() {
		return instance;
	}

	public MainPanel(InfoUser infoUser, Keycloak keycloak) {
		topToolbar = new TopToolbar(this, infoUser, "Vanilla DataPreparation", Images.INSTANCE.VanillaDataPreparation_Bandeau(), false, keycloak);
		initWidget(uiBinder.createAndBindUi(this));
		instance = this;
		this.infoUser = infoUser;

		NavigationItem itemGed = new NavigationItem(navigationPanel, Labels.lblCnst.Ged(), Images.INSTANCE.ged_32(), NavigationMenuPanel.DEFAULT_WIDTH);
		itemGed.addClickHandler(gedHandler);
		navigationPanel.addItem(itemGed);
		navigationPanel.addStyleName(VanillaCSS.LEVEL_2);
		
		NavigationItem dpItem = new NavigationItem(navigationPanel, Labels.lblCnst.DataPreparation(), Images.INSTANCE.ged_32(), NavigationMenuPanel.DEFAULT_WIDTH);
		dpItem.addClickHandler(dataPreparationHandler);
		navigationPanel.addItem(dpItem);
		navigationPanel.addStyleName(VanillaCSS.LEVEL_2);

		footerPanel.addStyleName(VanillaCSS.BOTTOM);
		this.addStyleName(VanillaCSS.BODY_BACKGROUND);

		dpItem.fireSelection();

		loadAvailableClasses();
	}

	private void loadAvailableClasses() {
		ArchitectService.Connect.getInstance().getAvailableClasses(new GwtCallbackWrapper<List<ClassDefinition>>(this, true, true) {

			@Override
			public void onSuccess(List<ClassDefinition> classes) {
				infoUser.setClasses(classes);
				if (classes != null && !classes.isEmpty()) {
					NavigationItem itemRules = new NavigationItem(navigationPanel, Labels.lblCnst.Rules(), Images.INSTANCE.rules_32(), NavigationMenuPanel.DEFAULT_WIDTH);
					itemRules.addClickHandler(rulesHandler);
					navigationPanel.addItem(itemRules);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void setInfoUser(InfoUser infoUser) {
		this.infoUser = infoUser;
	}

	public InfoUser getInfoUser() {
		return infoUser;
	}
	
	public DataVizTabPanel getDatavizPanel() {
		return datavizPanel;
	}

	@Override
	public void showAbout() {
	}

	@Override
	public void switchView(TypeView typeView) {
	}

	private ClickHandler gedHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (consultPanel == null) {
				consultPanel = new ConsultPanel(infoUser);
			}
			contentPanel.setWidget(consultPanel);
		}
	};
	
	private ClickHandler dataPreparationHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (datavizPanel == null) {
				datavizPanel = new DataVizTabPanel(infoUser);
			}
			contentPanel.setWidget(datavizPanel);
		}
	};

	private ClickHandler rulesHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (rulesPanel == null) {
				rulesPanel = new RulesPanel(infoUser);
			}
			contentPanel.setWidget(rulesPanel);
		}
	};
}
