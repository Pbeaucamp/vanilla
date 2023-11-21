package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.panels.documents.DocumentsGrid;
import bpm.es.web.shared.beans.Document;
import bpm.es.web.shared.beans.Document.TypeDocument;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AdministrativePanel extends Composite {

	private static AdministrativePanelUiBinder uiBinder = GWT.create(AdministrativePanelUiBinder.class);

	interface AdministrativePanelUiBinder extends UiBinder<Widget, AdministrativePanel> {
	}
	
	@UiField
	DocumentsGrid gridFactures, gridDebriefing;
	
	@UiField(provided=true)
	NameDateGrid gridBookBound;

	public AdministrativePanel() {
		gridBookBound = new NameDateGrid(true, Labels.lblCnst.Comment(), Labels.lblCnst.Date(), null);
		initWidget(uiBinder.createAndBindUi(this));
		
		createContent();
	}
	
	private void createContent() {
		DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");
		
		Document doc1 = new Document();
		doc1.setName("00253 - FACTURE - MARTIN JACQUES");
		doc1.setType(TypeDocument.BILL);
		doc1.setCreationDate(dtf.parse("15:02 12/06/2016"));
		
		Document doc2 = new Document();
		doc2.setName("00252 - FACTURE - MARTIN JACQUES");
		doc2.setType(TypeDocument.BILL);
		doc2.setCreationDate(dtf.parse("11:08 25/04/2016"));
		
		Document doc3 = new Document();
		doc3.setName("00251 - FACTURE - MARTIN JACQUES");
		doc3.setType(TypeDocument.BILL);
		doc3.setCreationDate(dtf.parse("11:54 24/04/2016"));

		
		List<Document> bills = new ArrayList<>();
		bills.add(doc1);
		bills.add(doc2);
		bills.add(doc3);
		
		gridFactures.loadDocuments(bills);
		

		Document compteRendu1 = new Document();
		compteRendu1.setName("12062016 - COMPTE RENDU");
		compteRendu1.setType(TypeDocument.LETTER);
		compteRendu1.setCreationDate(dtf.parse("15:02 12/06/2016"));
		
		Document compteRendu2 = new Document();
		compteRendu2.setName("25042016 - COMPTE RENDU");
		compteRendu2.setType(TypeDocument.LETTER);
		compteRendu2.setCreationDate(dtf.parse("11:08 25/04/2016"));

		List<Document> compteRendus = new ArrayList<>();
		compteRendus.add(compteRendu1);
		compteRendus.add(compteRendu2);
		
		gridDebriefing.loadDocuments(compteRendus);
	}
}
