package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.ScanDocumentType;
import bpm.document.management.core.model.User;

public class AkLadPageInfo implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private AklaBoxServer selectedServer = null;
	private StandardForm selectedForm = null;
	private User selectedReferent = null;
	private ScanDocumentType selectedType = null;
	List<Comments> comments = new ArrayList<Comments>();
	
	private FormCellResult fullTextOCR;
//	private Map<FormCell, FormCellResult> cellsTextOCR;
	private boolean isRADed;
	private String title;
	private int numPage = 1;
	private boolean isBlank = false;
	
	
	
	private String lang = null;
	
	public AkLadPageInfo() {
		super();
	}

	public AkLadPageInfo(AklaBoxServer selectedServer, StandardForm selectedForm, User selectedReferent, ScanDocumentType selectedType, List<Comments> comments,
			Map<FormCell, FormCellResult> cellsTextOCR, boolean isRADed, String title, int numPage, boolean isBlank, Chorus chorus,
			String lang) {
		super();
		this.selectedServer = selectedServer;
		this.selectedForm = selectedForm;
		this.selectedReferent = selectedReferent;
		this.selectedType = selectedType;
		this.comments = comments;
//		this.cellsTextOCR = cellsTextOCR;
		this.isRADed = isRADed;
		this.title = title;
		this.numPage = numPage;
		this.isBlank = isBlank;
		this.lang = lang;
	}

	public AklaBoxServer getSelectedServer() {
		return selectedServer;
	}

	public void setSelectedServer(AklaBoxServer selectedServer) {
		this.selectedServer = selectedServer;
	}

	public StandardForm getSelectedForm() {
		return selectedForm;
	}

	public void setSelectedForm(StandardForm selectedForm) {
		this.selectedForm = selectedForm;
	}

	public User getSelectedReferent() {
		return selectedReferent;
	}

	public void setSelectedReferent(User selectedReferent) {
		this.selectedReferent = selectedReferent;
	}

	public ScanDocumentType getSelectedType() {
		return selectedType;
	}

	public void setSelectedType(ScanDocumentType selectedType) {
		this.selectedType = selectedType;
	}

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

//	public Map<FormCell, FormCellResult> getCellsTextOCR() {
//		return cellsTextOCR;
//	}
//
//	public void setCellsTextOCR(Map<FormCell, FormCellResult> cellsTextOCR) {
//		this.cellsTextOCR = cellsTextOCR;
//	}

	public boolean isRADed() {
		return isRADed;
	}

	public void setRADed(boolean isRADed) {
		this.isRADed = isRADed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumPage() {
		return numPage;
	}

	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}

	public boolean isBlank() {
		return isBlank;
	}

	public void setBlank(boolean isBlank) {
		this.isBlank = isBlank;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public FormCellResult getFullTextOCR() {
		return fullTextOCR;
	}

	public void setFullTextOCR(FormCellResult fullTextOCR) {
		this.fullTextOCR = fullTextOCR;
	}

}
