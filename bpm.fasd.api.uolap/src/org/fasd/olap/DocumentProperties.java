package org.fasd.olap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentProperties extends OLAPElement{

	//private String name="";
	private String author = "";
	private String description = "";
	private Date creation = new Date();
	private Date modification = new Date();
	private String version = "";
//	private String iconPath = "";
//	private String flyOverIconPath = "";
	
		
	public DocumentProperties() {}



	public String getAuthor() {
		return author;
	}



	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(String creation) throws ParseException{
		if (!creation.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        this.creation = sdf.parse(creation);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getModification() {
		return modification;
	}

	public void setModification(String modification) throws ParseException{
		if (!modification.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			this.modification = sdf.parse(modification);
		}
		
	}



	public String getVersion() {
		return version;
	}



	public void setVersion(String version) {
		this.version = version;
	}


//	public String getFlyOverIconPath() {
//		return flyOverIconPath;
//	}
//
//
	@Deprecated
	public void setFlyOverIconPath(String flyOverIconPath) {
//		this.flyOverIconPath = flyOverIconPath;
	}
//
//
//	public String getIconPath() {
//		return iconPath;
//	}
//
//
	@Deprecated
	public void setIconPath(String iconPath) {
//		this.iconPath = iconPath;
	}
	
	public String getFAXML() {
		String tmp="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		
		tmp += "    <document-properties>\n" +
		   "        <name>" + getName() + "</name>\n" +
		   "        <author>" + author + "</author>\n" +
		   "        <description>" + description + "</description>\n" +
		   "        <creation-date>" + sdf.format(creation) + "</creation-date>\n" +
		   "        <modification-date>" + sdf.format(modification) + "</modification-date>\n" +
		   "        <version>" + version + "</version>\n" +
//		   "        <icon>" + iconPath + "</icon>\n" +
//		   "        <flyover-icon>" + flyOverIconPath + "</flyover-icon>\n" +
		   "    </document-properties>\n\n";
		
		return tmp;
	}

}
