package bpm.vanilla.platform.core.beans.forms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FormField implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeField {
		TEXTBOX(0), LISTBOX(1), CHECKBOX(2);

		private int type;

		private static Map<Integer, TypeField> map = new HashMap<Integer, TypeField>();
		static {
			for (TypeField typeField : TypeField.values()) {
				map.put(typeField.getType(), typeField);
			}
		}

		private TypeField(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeField valueOf(int typeField) {
			return map.get(typeField);
		}
	}

	private int id;
	private String name;
	private String label;
	private TypeField type;
	private boolean left = true;
	private String columnName;
	private String value;
	private boolean mandatory = false;
	private int columnType;

	public FormField() {
	}

	public FormField(TypeField type) {
		this.type = type;
	}

	public FormField(FormField field) {
		this.name = field.getName();
		this.label = field.getLabel();
		this.type = field.getType();
		this.left = field.isLeft();
		this.columnName = field.getColumnName();
		this.mandatory = field.isMandatory();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public TypeField getType() {
		return type;
	}

	public void setType(TypeField type) {
		this.type = type;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormField other = (FormField) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public String toString() {
		return label;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}
}
