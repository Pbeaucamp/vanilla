package bpm.es.web.client.panels.fiches;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReportManagerPanel extends Composite {

	private static ReportManagerPanelUiBinder uiBinder = GWT.create(ReportManagerPanelUiBinder.class);

	interface ReportManagerPanelUiBinder extends UiBinder<Widget, ReportManagerPanel> {
	}

	@UiField
	PersonsGrid gridSibling;

	public ReportManagerPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
//		buildContent();
	}

//	private void buildContent() {
//		Image img = new Image(Images.INSTANCE.identity_picture());
//		img.setSize("100%", "100%");
//		identityPicturePanel.setWidget(img);
//
//		txtLastName.setText("MARTIN");
//		txtFirstName.setText("ALEX");
//		txtFatherLastName.setText("MARTIN");
//		txtFatherFirstName.setText("JEAN");
//		txtMotherLastName.setText("MARTIN");
//		txtMotherFirstName.setText("CORINNE");
//
//		txtAddressOne.setText("11 Rue Jean Jaures");
//		txtAddressTwo.setText("");
//		txtAddressThree.setText("");
//		txtPostalCode.setText("69003");
//
//		cbCity.addItem("Lyon", "Lyon");
//		cbCity.addItem("Villeurbanne", "Villeurbanne");
//
//		gridSibling.loadPersons(createSiblings());
//
//		comment.setText("Ceci est un commentaire.");
//	}
//
//	private List<Person> createSiblings() {	
//		Person sister1 = new Person();
//		sister1.setLastName("MARTIN");
//		sister1.setFirstName("JACQUES");
//		sister1.setSexe(new Parameter("AUTRE", "Sexe", "Femme"));
//		
//		Person sister2 = new Person();
//		sister2.setLastName("MARTIN");
//		sister2.setFirstName("SOPHIE");
//		sister2.setSexe(new Parameter("AUTRE", "Sexe", "Femme"));
//		
//		Person brother1 = new Person();
//		brother1.setLastName("MARTIN");
//		brother1.setFirstName("MARIE");
//		brother1.setSexe(new Parameter("AUTRE", "Sexe", "Homme"));
//		
//		List<Person> siblings = new ArrayList<>();
//		siblings.add(sister1);
//		siblings.add(sister2);
//		siblings.add(brother1);
//		return siblings;
//	}
}
