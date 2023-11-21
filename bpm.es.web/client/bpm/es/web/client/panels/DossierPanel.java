package bpm.es.web.client.panels;

import bpm.es.web.client.panels.fiches.DossiersPanel;
import bpm.es.web.client.panels.fiches.FicheCreationMotherPanel;
import bpm.es.web.client.panels.fiches.FicheCreationPanel;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DossierPanel extends CompositeWaitPanel {

	private static HomePanelUiBinder uiBinder = GWT.create(HomePanelUiBinder.class);

	interface HomePanelUiBinder extends UiBinder<Widget, DossierPanel> {
	}
	
	@UiField
	SimplePanel panelContent;
	
	private DossiersPanel dossiersPanel;
	private FicheCreationPanel ficheCreationPanel;
	private FicheCreationMotherPanel ficheCreationMotherPanel;

	public DossierPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		displayDossiers();
	}

	public void displayDossiers() {
		if (dossiersPanel == null) {
			this.dossiersPanel = new DossiersPanel(this);
		}
		panelContent.setWidget(dossiersPanel);
	}

	public void displayFicheCreation() {
		if (ficheCreationPanel == null) {
			this.ficheCreationPanel = new FicheCreationPanel(this);
		}
		panelContent.setWidget(ficheCreationPanel);
		
//		if (ficheCreationMotherPanel == null) {
//			this.ficheCreationMotherPanel = new FicheCreationMotherPanel(this);
//		}
//		panelContent.setWidget(ficheCreationMotherPanel);
	}
}
