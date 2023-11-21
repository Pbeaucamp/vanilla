package bpm.vanilla.platform.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_linked_documents")
public class LinkedDocument {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "item_id")
	private int itemId;
	
	@Column(name = "linked_document_name")
	private String name;
	
	@Column(name = "format")
	private String format;
	
	@Column(name = "relative_path")
	private String relativePath;
	
	@Column(name = "version")
	private int version;
	
	@Column(name = "active")
	private boolean active;
	
	@Column(name = "`comment`")
	private String comment;
	
	public LinkedDocument(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = new Integer(itemId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativepath) {
		this.relativePath = relativepath;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	public void setVersion(String version) {
		this.version = new Integer(version);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setActive(String active) {
		this.active = "1".equalsIgnoreCase(active);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

//	public String getXml() {
//		return getElement().asXML();
//	}

	public Element getElement() {
		Element root = DocumentHelper.createElement("linkeddocument");
		root.addElement("id").setText(id+"");
		root.addElement("itemid").setText(itemId+"");
		root.addElement("name").setText(name);
		root.addElement("format").setText(format);
		root.addElement("relativepath").setText(relativePath);
		root.addElement("version").setText(version+"");
		root.addElement("active").setText(active ? 1+"" : 0+"");
		if (comment != null)
			root.addElement("comment").setText(comment);
		return root;
	}
}
