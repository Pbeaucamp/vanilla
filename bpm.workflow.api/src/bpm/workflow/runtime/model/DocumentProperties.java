package bpm.workflow.runtime.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Properties of the Workflow document
 * @author ??
 *
 */
public class DocumentProperties{

	private String author = "";
	private String description = "";
	private Date creation = new Date();
	private Date modification = new Date();
	private String version = "";
	private String iconPath = "";
	private String flyOverIconPath = "";
	private String name = "";
	private String id = "";
	private String path = "";
		
	/**
	 * 
	 * @return the name of the document
	 */
	public String getName() {
		return name;
	}


/**
 * Set the name of the document
 * @param name
 */
	public void setName(String name) {
		this.name = name;
	}



	public DocumentProperties() {}


/**
 * 
 * @return the author of the document
 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Set the id of the document
	 * @param id : int
	 */
	public void setId(int id){
		this.id = "" + id;
	}
	
	/**
	 * Set the id of the document
	 * @param id : String
	 */
	public void setId(String id){
		try{
			this.id = Integer.parseInt(id) + "";
		}catch(NumberFormatException e){
			
		}
		
	}
	

	/**
	 * Set the author of the document
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * 
	 * @return the date of creation of the document
	 */
	public Date getCreation() {
		return creation;
	}

	/**
	 * Set the date of creation of the document
	 * @param creation : String "yyyy-MM-dd"
	 * @throws ParseException
	 */
	public void setCreation(String creation) throws ParseException{
		if (!creation.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        this.creation = sdf.parse(creation);
		}
	}

	/**
	 * 
	 * @return the description of the document
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the document
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return the date of last modification
	 */
	public Date getModification() {
		return modification;
	}

	/**
	 * Set the date of modification
	 * @param modification : String "yyyy--MM--dd"
	 * @throws ParseException
	 */
	public void setModification(String modification) throws ParseException{
		if (!modification.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			this.modification = sdf.parse(modification);
		}
		
	}


/**
 * 
 * @return the version of the document
 */
	public String getVersion() {
		return version;
	}


/**
 * Set the version of the document
 * @param version
 */
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
	
	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	
	public Element getXmlNode() {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		Element root = DocumentHelper.createElement("document-properties");
		root.addElement("id").setText(id);
		root.addElement("name").setText(name);
		root.addElement("author").setText(author);
		root.addElement("description").setText(description);
		root.addElement("creation").setText(sdf.format(creation));
		root.addElement("modification").setText(sdf.format(modification));
		root.addElement("version").setText(version);
		root.addElement("icon").setText(iconPath);
		root.addElement("path").setText(path);
		root.addElement("flyover").setText(flyOverIconPath);
		
		
		
		return root;
	}



	

}
