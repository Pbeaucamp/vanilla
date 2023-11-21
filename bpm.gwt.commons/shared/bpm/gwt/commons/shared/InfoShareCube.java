package bpm.gwt.commons.shared;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.beans.Group;

public class InfoShareCube extends InfoShare {

	//Informations for a cube
	private String title;
	private String description;
	private boolean isPortrait;
	private HashMap<String, String> margins;
	private String separator;
	private DrillInformations drillInfos;
	private boolean exportChart;
	
	public InfoShareCube() { }
	
	public InfoShareCube(TypeShare typeShare, TypeExport typeExport, String title, String description, String itemName, String format, String separator, boolean isPortrait, List<Group> selectedGroups, List<Email> selectedEmails, String emailText, boolean exportChart) {
		super(typeShare, typeExport, itemName, format, selectedGroups, selectedEmails, emailText, false);
		this.title = title;
		this.description = description;
		this.isPortrait = isPortrait;
		this.separator = separator;
		this.exportChart = exportChart;
	}

	public void setMargins(String left, String right, String top, String bottom) {
		margins = new HashMap<String, String> ();
		margins.put("left", left.equals("") ? "0" : left);
		margins.put("right", right.equals("") ? "0" : right);
		margins.put("top", top.equals("") ? "0" : top);
		margins.put("bottom", bottom.equals("") ? "0" : bottom);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isPortrait() {
		return isPortrait;
	}
	
	public HashMap<String, String> getMargins() {
		return margins;
	}
	
	public boolean exportChart() {
		return exportChart;
	}
	
	public void setDrillInformations(DrillInformations drillInfos) {
		this.drillInfos = drillInfos;
	}

	public DrillInformations getDrillInfo() {
		return drillInfos;
	}
	
	public String getSeparator() {
		return separator;
	}
}
