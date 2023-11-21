package bpm.es.gedmanager.api;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class GedIndex {

	private List<GedDocument> allElements = new ArrayList<GedDocument>();
	private List<GedDocument> selElements = new ArrayList<GedDocument>();
	
	public GedIndex(List<GedDocument> all) {
		allElements = all;
	}
	
	public List<GedDocument> getAllElements() {
		return allElements;
	}
	
	public void setSelElements(List<GedDocument> selElements) {
		this.selElements = selElements;
	}
	
	public List<GedDocument> getSelectedElements() {
		return selElements;
	}
	
	
}
