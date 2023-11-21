package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Cocktail;
import bpm.document.management.core.model.DocPages;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.FormFieldValue;

public class AkLadExportObject implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	private Documents doc; 
	private List<DocPages> pages;
	private List<AkLadPageInfo> pagesMeta;
//	private List<Comments> comments;
	private Map<FormCell, FormCellResult> ocrCells;
	private List<FormFieldValue> cellsFormAklabox = new ArrayList<>();
	private Chorus chorusMetadata;
	private Cocktail cocktailMetadata;
	
	public AkLadExportObject() {
		super();
	}

	public AkLadExportObject(Documents doc, List<DocPages> pages, List<AkLadPageInfo> pagesMeta, Map<FormCell, FormCellResult> ocrCells, 
			Chorus chorusMetadata, List<FormFieldValue> cellsFormAklabox) {
		super();
		this.doc = doc;
		this.pages = pages;
		this.pagesMeta = pagesMeta;
		this.ocrCells = ocrCells;
		this.chorusMetadata = chorusMetadata;
		this.cellsFormAklabox = cellsFormAklabox;
	}

	public Documents getDoc() {
		return doc;
	}

	public void setDoc(Documents doc) {
		this.doc = doc;
	}
	
	public List<DocPages> getPages() {
		return pages;
	}

	public void setPages(List<DocPages> pages) {
		this.pages = pages;
	}

	public List<AkLadPageInfo> getPagesMeta() {
		return pagesMeta;
	}

	public void setPagesMeta(List<AkLadPageInfo> pagesMeta) {
		this.pagesMeta = pagesMeta;
	}

	public Map<FormCell, FormCellResult> getOcrCells() {
		return ocrCells;
	}

	public void setOcrCells(Map<FormCell, FormCellResult> ocrCells) {
		this.ocrCells = ocrCells;
	}

	public Chorus getChorusMetadata() {
		return chorusMetadata;
	}

	public void setChorusMetadata(Chorus chorusMetadata) {
		this.chorusMetadata = chorusMetadata;
	}

	public Cocktail getCocktailMetadata() {
		return cocktailMetadata;
	}

	public void setCocktailMetadata(Cocktail cocktailMetadata) {
		this.cocktailMetadata = cocktailMetadata;
	}

	public List<FormFieldValue> getCellsFormAklabox() {
		return cellsFormAklabox;
	}

	public void setCellsFormAklabox(List<FormFieldValue> cellsFormAklabox) {
		this.cellsFormAklabox = cellsFormAklabox;
	}


}
