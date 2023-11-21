package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.client.panels.DossierPanel;
import bpm.es.web.shared.beans.Dossier;
import bpm.es.web.shared.beans.Parameter;
import bpm.es.web.shared.beans.Person;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DossiersPanel extends Composite {

	private static DossierPanelUiBinder uiBinder = GWT.create(DossierPanelUiBinder.class);

	interface DossierPanelUiBinder extends UiBinder<Widget, DossiersPanel> {
	}
	
	@UiField(provided=true)
	DossiersGrid dossiersGrid;
	
	public DossiersPanel(DossierPanel parentPanel) {
		dossiersGrid = new DossiersGrid(parentPanel);
		initWidget(uiBinder.createAndBindUi(this));
		
		buildConten();
	}
	
	private void buildConten() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");
		
		Dossier dos1 = new Dossier();
		dos1.setId("DOS01");
		dos1.setCreationDate(dtf.parse("15:02 12/06/2016"));
		
		Person person1 = new Person();
		person1.setLastName("MARTIN");
		person1.setFirstName("JACQUES");
		person1.setSexe(new Parameter("AUTRE", "Sexe", "Femme"));
		person1.setDossier(dos1);
		
		Dossier dos2 = new Dossier();
		dos2.setId("DOS02");
		dos2.setCreationDate(dtf.parse("16:00 12/06/2016"));
		
		Person person2 = new Person();
		person2.setLastName("MARTIN");
		person2.setFirstName("SOPHIE");
		person2.setSexe(new Parameter("AUTRE", "Sexe", "Femme"));
		person2.setDossier(dos2);
		
		Dossier dos3 = new Dossier();
		dos3.setId("DOS03");
		dos3.setCreationDate(dtf.parse("11:52 11/06/2016"));
		
		Person person3 = new Person();
		person3.setLastName("MARTIN");
		person3.setFirstName("MARIE");
		person3.setSexe(new Parameter("AUTRE", "Sexe", "Homme"));
		person3.setDossier(dos3);
		
		List<Person> persons = new ArrayList<>();
//		persons.add(person1);
//		persons.add(person2);
		persons.add(person3);
		
		dossiersGrid.loadPersons(persons);
	}
}
