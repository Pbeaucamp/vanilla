package bpm.vanilla.platform.core.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_datas_provider")
public class DatasProvider {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "dataprovider_name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "creation_date")
	private Date creationDate;
	
	@Column(name = "modification_date")
	private Date modificationDate;
	
	@Column(name = "`xml`", length = 10000000)
	private String xml;
	
	@Column(name = "field_definition_name")
	private String fieldDefinitionName;
	
	@Transient
	private static transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = new Integer(id);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public void setCreationDate(String date){
		try {
			this.creationDate = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = creationDate;
	}
	public void setModificationDate(String date){
		try {
			this.modificationDate = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String getModel() {
		return this.xml;
		
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getFieldDefinitionName() {
		return fieldDefinitionName;
	}

	public void setFieldDefinitionName(String fieldDefinitionName) {
		this.fieldDefinitionName = fieldDefinitionName;
	}
	
	
	public Element getElement() {
		Element root = DocumentHelper.createElement("datasprovider");
		root.addElement("id").setText(this.id + "");
		root.addElement("name").setText(this.name);
		root.addElement("description").setText(this.description);
		
		if (this.fieldDefinitionName != null) {
			root.addElement("fielddefinitionname").setText(fieldDefinitionName);
		}
		
		root.addElement("creationdate").setText(sdf.format(this.creationDate));
		
		root.addElement("xml").setText(this.xml);
		
		return root;
		
	}
	
	public String getXmlDataSourceDefinition(){
		return xml;
	}
	public String getXml() {
		return getElement().asXML();
	}
	



}
