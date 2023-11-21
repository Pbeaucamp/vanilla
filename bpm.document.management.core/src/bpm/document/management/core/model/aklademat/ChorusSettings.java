package bpm.document.management.core.model.aklademat;

import java.util.List;

import bpm.document.management.core.model.FileType;


public class ChorusSettings implements IAkladematSettings {

	private static final long serialVersionUID = 1L;

	private int idTargetFolder;

	private List<String> patterns;
	private FileType bonDeCommandeType = null;
	private int idFieldEngagement = 0;
	
	public int getIdTargetFolder() {
		return idTargetFolder;
	}
	
	public void setIdTargetFolder(int idTargetFolder) {
		this.idTargetFolder = idTargetFolder;
	}
	
	public List<String> getPatterns() {
		return patterns;
	}
	
	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}
	
	public FileType getBonDeCommandeType() {
		return bonDeCommandeType;
	}
	
	public void setBonDeCommandeType(FileType bonDeCommandeType) {
		this.bonDeCommandeType = bonDeCommandeType;
	}
	
	public int getIdFieldEngagement() {
		return idFieldEngagement;
	}
	
	public void setIdFieldEngagement(int idFieldEngagement) {
		this.idFieldEngagement = idFieldEngagement;
	}


}
