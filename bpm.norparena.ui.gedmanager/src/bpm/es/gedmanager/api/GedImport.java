package bpm.es.gedmanager.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GedImport {

	private List<File> allElements = new ArrayList<File>();
	private List<File> selElements = new ArrayList<File>();
	
	public GedImport(List<File> all) {
		allElements = all;
	}
	
	public List<File> getAllElements() {
		return allElements;
	}
	
	public void setSelElements(List<File> selElements) {
		this.selElements = selElements;
	}
	
	public List<File> getSelectedElements() {
		return selElements;
	}
	
	
}
