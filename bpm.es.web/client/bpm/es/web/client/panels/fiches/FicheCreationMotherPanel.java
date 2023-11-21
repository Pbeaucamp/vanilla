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

public class FicheCreationMotherPanel extends Composite {

	private static FicheCreationMotherPanelUiBinder uiBinder = GWT.create(FicheCreationMotherPanelUiBinder.class);

	interface FicheCreationMotherPanelUiBinder extends UiBinder<Widget, FicheCreationMotherPanel> {
	}
	
	interface MyStyle extends CssResource {
		String tabSelected();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label tabCivilState, tabBudget, tabEvaluation;
	
	@UiField
	SimplePanel contentPanel;
	
	private DossierPanel parentPanel;
	
	private Label selectedTab;
	
	private CivilStatePanel civilStatePanel;
	private EvaluationPanel evaluationPanel;
	
	public FicheCreationMotherPanel(DossierPanel parentPanel) {
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

	@UiHandler("tabBudget")
	public void displayHealth(ClickEvent event) {
		if (civilStatePanel == null) {
			civilStatePanel = new CivilStatePanel();
		}
		contentPanel.setWidget(civilStatePanel);
		
		refreshMenu(tabBudget);
	}

	@UiHandler("tabEvaluation")
	public void displayReportManager(ClickEvent event) {
		if (evaluationPanel == null) {
			evaluationPanel = new EvaluationPanel();
		}
		contentPanel.setWidget(evaluationPanel);
		
		refreshMenu(tabEvaluation);
	}
	
	@UiHandler("backRecords")
	public void onBackRecords(ClickEvent event) {
		parentPanel.displayDossiers();
	}

}
