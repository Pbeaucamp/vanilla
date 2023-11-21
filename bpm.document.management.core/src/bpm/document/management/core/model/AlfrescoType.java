package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlfrescoType  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private int exportId;
	private int typeId;
	private int formId;
	private List<FormField> fields= new ArrayList<FormField>();
	private Boolean fill=false;
	
	public AlfrescoType() {
		super();
	}
	
	public AlfrescoType(int id, int exportId, int typesId, int formId, List<FormField> fields) {
		super();
		this.id = id;
		this.exportId = exportId;
		this.typeId = typesId;
		this.fields=fields;
		this.formId=formId;
	}
	public AlfrescoType(int typesId, int formId) {
		super();
		this.typeId = typesId;
		this.formId=formId;
		fill=true;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getExportId() {
		return exportId;
	}
	public void setExportId(int exportId) {
		this.exportId = exportId;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typesId) {
		this.typeId = typesId;
	}
	public int getFormId() {
		return formId;
	}
	public void setFormId(int formId) {
		this.formId = formId;
	}

	public List<FormField> getFields() {
		return fields;
	}
	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
	public Boolean isFill() {
		return fill;
	}
	public void setFill(Boolean fill) {
		this.fill = fill;
	}
	
	
}
