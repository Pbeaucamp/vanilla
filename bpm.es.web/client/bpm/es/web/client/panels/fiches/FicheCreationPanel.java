package bpm.es.web.client.panels.fiches;

import bpm.es.web.client.panels.DossierPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FicheCreationPanel extends Composite {

	private static FicheCreationPanelUiBinder uiBinder = GWT.create(FicheCreationPanelUiBinder.class);

	interface FicheCreationPanelUiBinder extends UiBinder<Widget, FicheCreationPanel> {
	}
	
	interface MyStyle extends CssResource {
		String tabSelected();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label tabCivilState, tabAdmission, tabStudy, tabSocial, tabHealth, tabAbsenceAndMovements, tabRoute, tabReportManager;
	
	@UiField
	SimplePanel contentPanel;
	
	private DossierPanel parentPanel;
	
	private Label selectedTab;
	
	private CivilStatePanel civilStatePanel;
	private HealthPanel healthPanel;
	private AbsenceMovementsPanel absenceMovementsPanel;
	private RoutePanel routePanel;
	private AdministrativePanel administrativePanel;
	
	public FicheCreationPanel(DossierPanel parentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parentPanel = parentPanel;
		
		displayCivilState(null);
	}

	private void refreshMenu(Label item) {
		if (selectedTab != null) {
			selectedTab.removeStyleName(style.tabSelected());
		}
		selectedTab = item;
		selectedTab.addStyleName(style.tabSelected());
	}
	
	
	@UiHandler("tabCivilState")
	public void displayCivilState(ClickEvent event) {
		if (civilStatePanel == null) {
			civilStatePanel = new CivilStatePanel();
		}
		contentPanel.setWidget(civilStatePanel);
		
		refreshMenu(tabCivilState);
	}

	@UiHandler("tabHealth")
	public void displayHealth(ClickEvent event) {
		if (healthPanel == null) {
			healthPanel = new HealthPanel();
		}
		contentPanel.setWidget(healthPanel);
		
		refreshMenu(tabHealth);
	}

	@UiHandler("tabAbsenceAndMovements")
	public void displayabsenceMovements(ClickEvent event) {
		if (absenceMovementsPanel == null) {
			absenceMovementsPanel = new AbsenceMovementsPanel();
		}
		contentPanel.setWidget(absenceMovementsPanel);
		
		refreshMenu(tabAbsenceAndMovements);
	}

	@UiHandler("tabReportManager")
	public void displayReportManager(ClickEvent event) {
		if (civilStatePanel == null) {
			civilStatePanel = new CivilStatePanel();
		}
		contentPanel.setWidget(civilStatePanel);
		
		refreshMenu(tabReportManager);
	}

	@UiHandler("tabRoute")
	public void displayRoute(ClickEvent event) {
		if (routePanel == null) {
			routePanel = new RoutePanel();
		}
		contentPanel.setWidget(routePanel);
		
		refreshMenu(tabRoute);
	}

	@UiHandler("tabReportManager")
	public void displayAdministrative(ClickEvent event) {
		if (administrativePanel == null) {
			administrativePanel = new AdministrativePanel();
		}
		contentPanel.setWidget(administrativePanel);
		
		refreshMenu(tabReportManager);
	}
	
	@UiHandler("backRecords")
	public void onBackRecords(ClickEvent event) {
		parentPanel.displayDossiers();
	}

}
