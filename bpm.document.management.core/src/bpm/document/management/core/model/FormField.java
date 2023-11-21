package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FormField implements Serializable {

	public enum FormFieldType {
		STRING(0), INTEGER(1), BOOLEAN(2), LOV(3), OPTIONS(4), ADDRESS(5), DATE(6), THRESHOLD(7), CALENDAR_TYPE(8), UPLOAD_DOCUMENT(9), NAME(10), DEED_CLASSIFICATION(11);

		private int type;

		private static Map<Integer, FormFieldType> map = new HashMap<Integer, FormFieldType>();
		static {
			for (FormFieldType type : FormFieldType.values()) {
				map.put(type.getType(), type);
			}
		}

		private FormFieldType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static FormFieldType valueOf(int type) {
			return map.get(type);
		}
	}

	private static final long serialVersionUID = 1L;

	private int id;
	private String label;
	private String helpText;
	private FormFieldType type;
	private boolean required;
	private int formId;
	private Date creationDate = new Date();
	private LOV lovTableCode;
	private String regex;
	private Boolean canAdd;

	private int taskTypeId;
	private TypeTask typeTask;
	
	private String colSourceConnection = "";  //nom de la colonne de la datasource liée
	private boolean isKeyFilterSourceConnection = false;  //si la colonne est une clé pour les filtres
	
	private boolean displayed = true;  //pour les forms metadata (type 2) champs affiché ou non

	public FormField() {
	}

	public FormField(String label, String helpText, FormFieldType type, boolean required, int formId) {
		super();
		this.label = label;
		this.helpText = helpText;
		this.type = type;
		this.required = required;
		this.formId = formId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public FormFieldType getType() {
		return type;
	}

	public void setType(FormFieldType type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public LOV getLovTableCode() {
		return lovTableCode;
	}

	public void setLovTableCode(LOV lovTableCode) {
		this.lovTableCode = lovTableCode;
	}

	public Boolean getCanAdd() {
		return canAdd;
	}

	public void setCanAdd(Boolean canAdd) {
		this.canAdd = canAdd;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public int getTaskTypeId() {
		return taskTypeId;
	}

	public void setTaskTypeId(int taskTypeId) {
		this.taskTypeId = taskTypeId;
	}

	public TypeTask getTypeTask() {
		return typeTask;
	}

	public void setTypeTask(TypeTask typeTask) {
		this.typeTask = typeTask;
	}

	public String getColSourceConnection() {
		return colSourceConnection;
	}

	public void setColSourceConnection(String colSourceConnection) {
		this.colSourceConnection = colSourceConnection;
	}

	public boolean isKeyFilterSourceConnection() {
		return isKeyFilterSourceConnection;
	}
	
	public boolean getIsKeyFilterSourceConnection() {
		return isKeyFilterSourceConnection;
	}

	public void setIsKeyFilterSourceConnection(boolean isKeyFilterSourceConnection) {
		this.isKeyFilterSourceConnection = isKeyFilterSourceConnection;
	}

	@Override
	public String toString() {
		return label;
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}
	
}
