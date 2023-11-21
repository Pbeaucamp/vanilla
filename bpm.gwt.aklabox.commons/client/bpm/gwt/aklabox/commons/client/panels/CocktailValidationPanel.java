package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;
import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.aklademat.PastellFile;
import bpm.document.management.core.model.aklademat.PastellFile.FileType;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.ThemeCSS;
import bpm.gwt.aklabox.commons.client.viewers.DocumentViewer;
import bpm.gwt.aklabox.commons.client.viewers.DocumentViewer.TypeViewer;
import bpm.gwt.aklabox.commons.client.viewers.ParentDocViewer;
import bpm.gwt.aklabox.commons.shared.CommonConstants;
import bpm.gwt.aklabox.commons.shared.OCRSearchResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This panel contains multiple {@link DocumentOptionPanel}. Each panel is used
 * to manage options for one document.
 * 
 */
public class CocktailValidationPanel extends ChildDialogComposite implements ParentDocViewer{

	private static CocktailValidationPanelUiBinder uiBinder = GWT.create(CocktailValidationPanelUiBinder.class);

	interface CocktailValidationPanelUiBinder extends UiBinder<Widget, CocktailValidationPanel> {
	}
	
	interface MyStyle extends CssResource {
		String frame();
	}

	@UiField
	NavigationMenuPanel navigationPanel;

	@UiField
	SimplePanel panelViewer, panelValidate;
	
	@UiField
	HTMLPanel panelContent;
	
	@UiField
	Button btnApply;
	
	@UiField
	MyStyle style;

	private List<AkLadExportObject> documents;
	private List<AkladematAdminEntity<Chorus>> entities;
	private List<CocktailValidationContent> panelValidations;
	
	private boolean confirm = false;

	public CocktailValidationPanel(List<AkLadExportObject> documents, int factureTypeId) {
		initWidget(uiBinder.createAndBindUi(this));
		this.documents = documents;
		this.panelValidations = new ArrayList<>();

		navigationPanel.addStyleName(ThemeCSS.LEVEL_2);
		
		btnApply.setEnabled(false);

		if (documents != null && !documents.isEmpty()) {
			for (int i = 0; i < documents.size(); i++) {
				if((documents.get(i).getDoc().getTypeSelectedId() != null && documents.get(i).getDoc().getTypeSelectedId() == factureTypeId && documents.get(i).getDoc().getTypeSelectedId() != 0)
						|| (documents.get(i).getPagesMeta().get(0).getSelectedType() != null && documents.get(i).getPagesMeta().get(0).getSelectedType().isChorusType())){
					
					CocktailNavigationItem item = new CocktailNavigationItem(navigationPanel, documents.get(i), i + 1, this);
					item.addClickHandler(documentHandler);
					navigationPanel.addItem(item);

					if (navigationPanel.getItems().size() == 1) {
						navigationPanel.setSelectedItem(item);
					}
				}
				
			}

			loadDocument((CocktailNavigationItem)navigationPanel.getItems().get(0));
		}
	}
	
	public CocktailValidationPanel(List<AkladematAdminEntity<Chorus>> entities) {
		initWidget(uiBinder.createAndBindUi(this));
		this.entities = entities;
		this.panelValidations = new ArrayList<>();

		navigationPanel.addStyleName(ThemeCSS.LEVEL_2);
		
		btnApply.setEnabled(false);

		if (entities != null && !entities.isEmpty()) {
			for (int i = 0; i < entities.size(); i++) {
				CocktailNavigationItem item = new CocktailNavigationItem(navigationPanel, entities.get(i), i + 1, this);
				item.addClickHandler(documentHandler);
				navigationPanel.addItem(item);

				if (i == 0) {
					navigationPanel.setSelectedItem(item);
				}
			}

			loadDocument((CocktailNavigationItem)navigationPanel.getItems().get(0));
		}
	}

	public void loadDocument(CocktailNavigationItem item) {
		for(CocktailValidationContent cont : panelValidations){
			if(cont.getParentNavItem().equals(item)){
				panelValidate.clear();
//				panelValidate.add(cont);
//				panelViewer.setWidget(new DocumentViewer(item.getAkLadExportObject().getDoc(), new User(), 
//						this, TypeViewer.AKLAD_VIGNETTE));
				showContent(cont);
				return;
			}
		}
		Chorus chorus = null;
		switch (item.getType()) {
		case AKLAD:
			chorus = item.getAkLadExportObject().getChorusMetadata();
			break;
		case AKLADEMAT:
			chorus = item.getEntity().getObject();
			break;
		}
		CocktailValidationContent panelValid = new CocktailValidationContent(item, chorus);
		panelValidations.add(panelValid);
		panelValidate.clear();
		showContent(panelValid);
	}
	
	private void showContent(CocktailValidationContent content){
		panelValidate.add(content);
		switch (content.getParentNavItem().getType()) {
		case AKLAD:
			panelViewer.setWidget(new DocumentViewer(content.getParentNavItem().getAkLadExportObject().getDoc(), new User(), this, TypeViewer.AKLAD_VIGNETTE, content.getParentNavItem().getAkLadExportObject().getPages()));
			break;
		case AKLADEMAT:
			List<PastellFile> files = content.getParentNavItem().getEntity().getObject().getFiles(FileType.FICHIER_FACTURE_PDF);
			if(files != null && files.size() > 0){
				PastellFile file = files.get(0);
				String fullUrl;
				if (file.getDocumentId() > 0) {
					fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_DOCUMENT_ID + "=" + file.getDocumentId() + 
							"&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + "pdf" + "&" + CommonConstants.STREAM_CHECKOUT + "=" + true;
					
				}
				else {
					fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_DOCUMENT_ID + "=" + content.getParentNavItem().getEntity().getPastellId()
							+ "&" + CommonConstants.STREAM_CHECKOUT + "=" + true + "&" + CommonConstants.STREAM_TYPE_DOCUMENT + "=" + CommonConstants.STREAM_TYPE_AKLADEMAT_ENTITY
							+ "&" + CommonConstants.STREAM_TYPE_FILE + "=" + file.getType().getType() + "&" + CommonConstants.STREAM_FILE_NAME + "=" + file.getFileName()
							+ "&" + CommonConstants.STREAM_FILE_INDEX + "=" + file.getIndex();
					
				}
				Frame frame = new Frame(fullUrl);
				frame.addStyleName(style.frame());
				panelViewer.setWidget(frame);
			} else {
				panelViewer.clear();
			}
			
			break;
		}
		
		
	}

	private ClickHandler documentHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource() instanceof FocusPanel) {
				if (((FocusPanel) event.getSource()).getParent() instanceof CocktailNavigationItem) {
					CocktailNavigationItem item = (CocktailNavigationItem) ((FocusPanel) event.getSource()).getParent();
					loadDocument(item);
				}
			}
		}
	};
	
	public void updateProgress(){
		for(INavigationItem item : navigationPanel.getItems()){
			if(item instanceof CocktailNavigationItem){
				if(((CocktailNavigationItem)item).getProgress() < 100.0){
					btnApply.setEnabled(false);
					return;
				}
			}
		}
		btnApply.setEnabled(true);
	}
	
	@UiHandler("btnApply")
	public void onApply(ClickEvent event){
		confirm = true;

		for(CocktailValidationContent cont : panelValidations){
			if(cont.getParentNavItem().getAeo() != null){
				cont.getCocktail().fillChorus(cont.getParentNavItem().getAeo().getChorusMetadata());
			} else if(cont.getParentNavItem().getEntity() != null){
				cont.getCocktail().fillChorus(cont.getParentNavItem().getEntity().getObject());
			}
		}
		
		closeParent();
	}

	@Override
	public void onValidateImageTreatment() {
	}

	@Override
	public void onReturnToOriginal() {
	}

	@Override
	public void onCommentUpdated(List<Comments> comments) {
	}

	@Override
	public void previewPage(String path) {
	}

	@Override
	public void onSearchResult(List<OCRSearchResult> result) {
	}

	public boolean isConfirm() {
		return confirm;
	}

	public List<AkLadExportObject> getDocuments() {
		return documents;
	}

	public List<AkladematAdminEntity<Chorus>> getEntities() {
		return entities;
	}
	
	public void onCloseItem(CocktailNavigationItem item){
		for(INavigationItem it : navigationPanel.getItems()){
			if(it.equals(item)){
				if(item.equals(navigationPanel.getSelectedItem())){
					if(navigationPanel.getItems().size() > 1){
						navigationPanel.setSelectedItem(navigationPanel.getItems().get(navigationPanel.getItems().indexOf(it)-1));
						loadDocument((CocktailNavigationItem) navigationPanel.getItems().get(navigationPanel.getItems().indexOf(it)-1));
					}
				}
				navigationPanel.getItems().remove(it);
				updateProgress();
				break;
			}
		}
	}
}
