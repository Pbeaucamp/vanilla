//ugugug
package bpm.metadata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentProperties{

	
	public static final int FMDT_TYPE_NONE = 0;
	public static final int FMDT_TYPE_FMETRICS = 1;
	public static final int FMDT_TYPE_ORACLE_FINANCIAL = 2;

	public static final String[] FMDT_TYPE_NAME = new String[]{"None", "FreeMetrics", "Oracle Financial"};
	
	private int fmdtType = 0;
	
	private String author = "";
	private String description = "";
	private Date creation = new Date();
	private Date modification = new Date();
	private String version = "1.43.3";
	private String iconPath = "";
	private String flyOverIconPath = "";
	private String name = "";
	private String id = "";
	private String projectVersion = "1.0";
	private String projectName = "";
	
	
	
	
	public String getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getName() {
		return name;
	}

	public int getFmdtType(){
		return fmdtType;
	}
	
	public void setFmdtType(int type){
		this.fmdtType = type;
	}
	
	public void setFmdtType(String type){
		try{
			this.fmdtType = Integer.parseInt(type);
		}catch(Exception e){
			
		}
		
	}

	public void setName(String name) {
		this.name = name;
	}



	public DocumentProperties() {}



	public String getAuthor() {
		return author;
	}

	public void setId(int id){
		this.id = "" + id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		try{
			this.id = Integer.parseInt(id) + "";
		}catch(NumberFormatException e){
			
		}
		
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


	public String getFlyOverIconPath() {
		return flyOverIconPath;
	}


	public void setFlyOverIconPath(String flyOverIconPath) {
		this.flyOverIconPath = flyOverIconPath;
	}


	public String getIconPath() {
		return iconPath;
	}


	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	
	public String getXml() {
		String tmp="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		
		tmp += "    <document-properties>\n" +
			"        <id>" + id + "</id>\n" +
			"        <fmdtType>" + fmdtType + "</fmdtType>\n" +
		   "        <name>" + name + "</name>\n" +
		   "        <author>" + author + "</author>\n" +
		   "        <description>" + description + "</description>\n" +
		   "        <creation>" + sdf.format(creation) + "</creation>\n" +
		   "        <modification>" + sdf.format(modification) + "</modification>\n" +
		   "        <version>" + version + "</version>\n" +
		   "        <icon>" + iconPath + "</icon>\n" +
		   "        <projectVersion>" + projectVersion + "</projectVersion>\n" +
		   "        <projectName>" + projectName + "</projectName>\n" +
		   "        <flyover>" + flyOverIconPath + "</flyover>\n" +
		   "    </document-properties>\n\n";
		
		return tmp;
	}



	

}
