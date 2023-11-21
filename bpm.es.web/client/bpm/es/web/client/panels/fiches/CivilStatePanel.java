package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.client.images.Images;
import bpm.es.web.client.utils.CustomDateBox;
import bpm.es.web.shared.beans.Parameter;
import bpm.es.web.shared.beans.Person;
import bpm.gwt.commons.client.custom.ComboBox;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CivilStatePanel extends Composite {

	private static CivilStatePanelUiBinder uiBinder = GWT.create(CivilStatePanelUiBinder.class);

	interface CivilStatePanelUiBinder extends UiBinder<Widget, CivilStatePanel> {
	}

	@UiField
	SimplePanel identityPicturePanel;

	@UiField(provided = true)
	CustomDateBox dbBirthDate;

	@UiField
	LabelTextBox txtLastName, txtFirstName, txtFatherLastName, txtFatherFirstName, txtMotherLastName, txtMotherFirstName;

	@UiField
	LabelTextBox txtAddressOne, txtAddressTwo, txtAddressThree, txtPostalCode;

	@UiField
	ComboBox<String> cbCity;

	@UiField
	PersonsGrid gridSibling;

	@UiField
	LabelTextArea comment;

	public CivilStatePanel() {
		dbBirthDate = new CustomDateBox(DateTimeFormat.getFormat("dd/MM/yyyy"), null);
		initWidget(uiBinder.createAndBindUi(this));
		
//		buildContent();
		buildMotherContent();
	}

	private void buildContent() {
		Image img = new Image(Images.INSTANCE.identity_picture());
		img.setSize("100%", "100%");
		identityPicturePanel.setWidget(img);

		txtLastName.setText("MARTIN");
		txtFirstName.setText("ALEX");
		txtFatherLastName.setText("MARTIN");
		txtFatherFirstName.setText("JEAN");
		txtMotherLastName.setText("MARTIN");
		txtMotherFirstName.setText("CORINNE");

		txtAddressOne.setText("11 Rue Jean Jaures");
		txtAddressTwo.setText("");
		txtAddressThree.setText("");
		txtPostalCode.setText("69003");

		cbCity.addItem("Lyon", "Lyon");
		cbCity.addItem("Villeurbanne", "Villeurbanne");

		gridSibling.loadPersons(createSiblings());

		comment.setText("Ceci est un commentaire.");
	}
	
	private void buildMotherContent() {
//		Image img = new Image(Images.INSTANCE.identity_picture());
//		img.setSize("100%", "100%");
//		identityPicturePanel.setWidget(img);

		txtLastName.setText("DUPOND");
		txtFirstName.setText("CHARLENE");
		txtFatherLastName.setText("DUPOND");
		txtFatherFirstName.setText("LUC");
		txtMotherLastName.setText("DUPOND");
		txtMotherFirstName.setText("MARIE");

		txtAddressOne.setText("25 Boulevard de Montbonnot");
		txtAddressTwo.setText("");
		txtAddressThree.setText("");
		txtPostalCode.setText("69009");

		cbCity.addItem("Lyon", "Lyon");
		cbCity.addItem("Villeurbanne", "Villeurbanne");

//		gridSibling.loadPersons(createSiblings());

//		comment.setText("Ceci est un commentaire.");
	}

	private List<Person> createSiblings() {	
		Person sister1 = new Person();
		sister1.setLastName("MARTIN");
		sister1.setFirstName("JACQUES");
		sister1.setSexe(new Parameter("AUTRE", "Sexe", "Femme"));
		
		Person sister2 = new Person();
		sister2.setLastName("MARTIN");
		sister2.setFirstName("SOPHIE");
		sister2.setSexe(new Parameter("AUTRE", "Sexe", "Femme"));
		
		Person brother1 = new Person();
		brother1.setLastName("MARTIN");
		brother1.setFirstName("MARIE");
		brother1.setSexe(new Parameter("AUTRE", "Sexe", "Homme"));
		
		List<Person> siblings = new ArrayList<>();
		siblings.add(sister1);
		siblings.add(sister2);
		siblings.add(brother1);
		return siblings;
	}
}
