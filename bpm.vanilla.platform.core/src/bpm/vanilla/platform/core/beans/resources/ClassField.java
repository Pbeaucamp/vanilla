package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassField implements IClassItem {

	private static final long serialVersionUID = 1L;
	
	public static final String GLOBAL_FIELD = "Global";

	public enum TypeField {
		STRING(0, "String"), 
		NUMERIC(1, "Numeric"), 
		DATE(2, "Date"), 
		ENUM(3, "Enum"),
		BOOLEAN(4, "Boolean");

		private int type;
		private String name;

		private static Map<Integer, TypeField> map = new HashMap<Integer, TypeField>();
		static {
			for (TypeField type : TypeField.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeField(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public static TypeField valueOf(int type) {
			return map.get(type);
		}
	}

	private String name;
	private String cleanFieldName;
	private TypeField type;
	
	// If type is date
	private String format;
	
	private boolean required;

	private ClassDefinition parent;

	private List<ClassRule> rules;
	private List<String> possibleNames;

	public ClassField() {
	}

	public ClassField(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getCleanFieldName() {
		return cleanFieldName != null && !cleanFieldName.isEmpty() ? cleanFieldName : name;
	}
	
	public void setCleanFieldName(String cleanFieldName) {
		this.cleanFieldName = cleanFieldName;
	}

	public TypeField getType() {
		return type;
	}

	public void setType(TypeField type) {
		this.type = type;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public List<ClassRule> getRules() {
		return rules;
	}

	public void setRules(List<ClassRule> rules) {
		this.rules = rules;
	}

	@Override
	public void addRule(ClassRule rule) {
		if (this.rules == null) {
			this.rules = new ArrayList<ClassRule>();
		}
		rule.setParent(this);
		this.rules.add(rule);
	}
	
	@Override
	public void removeRule(ClassRule rule) {
		if (this.rules != null) {
			this.rules.remove(rule);
		}
	}

	public void setParent(ClassDefinition parent) {
		this.parent = parent;
	}
	
	public ClassDefinition getParent() {
		return parent;
	}

	@Override
	public String getMainClassIdentifiant() {
		return parent != null ? parent.getMainClassIdentifiant() : name;
	}

	@Override
	public String getPath() {
		return parent != null ? parent.getPath() + "/" + name : name;
	}
	
	public List<String> getPossibleNames() {
		return possibleNames;
	}
	
	public void setPossibleNames(List<String> possibleNames) {
		this.possibleNames = possibleNames;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(getName());
		buf.append(required ? " * " : "");
		if (type != null) {
			buf.append(" (");
			buf.append(type.getName());
			buf.append(")");
		}
		buf.append(" (");
		buf.append(rules != null ? rules.size() : 0);
		buf.append(")");
		
		if (possibleNames != null && !possibleNames.isEmpty()) {
			buf.append(" (");
			boolean first = true;
			for (String possibleName : possibleNames) {
				if (!first) {
					buf.append(",");
				}
				buf.append(possibleName);
				first = false;
			}
			buf.append(")");
		}
		return buf.toString();
	}
}
