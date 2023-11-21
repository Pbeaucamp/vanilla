package bpm.es.web.client.panels.documents;

import java.util.ArrayList;
import java.util.List;

import bpm.es.web.shared.beans.Document;
import bpm.es.web.shared.beans.Document.TypeDocument;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DocumentsManagerPanel extends Composite {

	private static DocumentsManagerPanelUiBinder uiBinder = GWT.create(DocumentsManagerPanelUiBinder.class);

	interface DocumentsManagerPanelUiBinder extends UiBinder<Widget, DocumentsManagerPanel> {
	}
	
	@UiField
	DocumentsGrid documentsGrid;
	
	public DocumentsManagerPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		documentsGrid.setAddVisible(false);
		
		buildContent();
	}
	
	private void buildContent() {
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
		
		List<Document> documents = new ArrayList<>();
//		documents.add(doc1);
		documents.add(doc2);
//		documents.add(doc3);
		
		documentsGrid.loadDocuments(documents);
	}
}
