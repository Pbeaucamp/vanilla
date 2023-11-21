package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClassRule implements Serializable {

	public enum TypeRule {
		VALUE_COMPARAISON(0),
		COLUMN_COMPARAISON(1),
		COLUMN_DATE_COMPARAISON(2),
		LISTE_DB_COMPARAISON(3),
		PATTERN(4),
		CLASS_COLUMN_NULL(5),
		COLUMN_OPERATION(6),
		COLUMN_COMPARAISON_DATE(7),
		COLUMN_COMPARAISON_BETWEEN_COLUMN(8);

		private int type;

		private static Map<Integer, TypeRule> map = new HashMap<Integer, TypeRule>();
		static {
			for (TypeRule type : TypeRule.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeRule(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeRule valueOf(int type) {
			return map.get(type);
		}
	}

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String description;
	private String mainClassIdentifiant;
	private String parentPath;
	private boolean enabled = true;

	private IClassItem parent;

	private TypeRule type;
	private IRule rule;

	private String model;

	public ClassRule() {
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public void setMainClassIdentifiant(String mainClassIdentifiant) {
		this.mainClassIdentifiant = mainClassIdentifiant;
	}

	public String getMainClassIdentifiant() {
		return mainClassIdentifiant;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getParentPath() {
		return parentPath;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setParent(IClassItem parent) {
		this.parent = parent;
	}

	public IClassItem getParent() {
		return parent;
	}

	public String getParentName() {
		return parent != null ? parent.getName() : "Inconnu";
	}

	public void setType(TypeRule type) {
		this.type = type;
	}

	public TypeRule getType() {
		return type;
	}

	public void setRule(IRule rule) {
		this.rule = rule;
	}

	public IRule getRule() {
		return rule;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void copyDefinition(ClassRule rule) {
		setId(rule.getId());
		setName(rule.getName());
		setDescription(rule.getDescription());
		setEnabled(rule.isEnabled());
		setMainClassIdentifiant(rule.getMainClassIdentifiant());
	}

	public boolean match(ClassRule rule) {
		if (getType() == rule.getType()) {
			return getRule() != null && getRule().match(rule.getRule());
		}
		return false;
	}

	@Override
	public String toString() {
		return rule != null ? rule.toString() : "Not defined";
	}
}
