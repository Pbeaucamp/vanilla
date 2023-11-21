package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

public class ClassDefinition implements IClassItem {

	private static final long serialVersionUID = 1L;

	private String name;
	private String identifiant;

	private ClassDefinition parent;

	private List<ClassDefinition> classes;
	private List<ClassField> fields;
	private List<ClassRule> classRules;

	public ClassDefinition() {
	}

	public ClassDefinition(String name, String identifiant) {
		this.name = name;
		this.identifiant = identifiant;
	}

	@Override
	public String getName() {
		return name != null && !name.isEmpty() ? name : identifiant;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public List<ClassDefinition> getClasses() {
		return classes;
	}

	public void addClass(ClassDefinition childClass) {
		if (classes == null) {
			this.classes = new ArrayList<ClassDefinition>();
		}
		childClass.setParent(this);
		this.classes.add(childClass);
	}

	public List<ClassField> getFields() {
		return fields;
	}

	public void addField(ClassField myField) {
		if (fields == null) {
			this.fields = new ArrayList<ClassField>();
		}
		myField.setParent(this);
		this.fields.add(myField);
	}

	@Override
	public List<ClassRule> getRules() {
		return classRules;
	}

	@Override
	public void addRule(ClassRule classRule) {
		if (classRules == null) {
			this.classRules = new ArrayList<ClassRule>();
		}
		classRule.setParent(this);
		this.classRules.add(classRule);
	}
	
	@Override
	public void removeRule(ClassRule rule) {
		if (this.classRules != null) {
			this.classRules.remove(rule);
		}
	}

	public void setParent(ClassDefinition parent) {
		this.parent = parent;
	}

	@Override
	public String getMainClassIdentifiant() {
		return parent != null ? parent.getMainClassIdentifiant() : identifiant;
	}

	@Override
	public String getPath() {
		return parent != null ? parent.getPath() + "/" + getName() : getName();
	}

	@Override
	public String toString() {
		boolean showNbRules = false;
		if (classRules != null) {
			showNbRules = true;
		}

		int nbRules = 0;
		if (fields != null) {
			showNbRules = true;
			for (ClassField field : fields) {
				nbRules += field.getRules() != null ? field.getRules().size() : 0;
			}
		}

		StringBuffer buf = new StringBuffer(getName());
		if (showNbRules) {
			buf.append(" (");
			buf.append(classRules != null ? classRules.size() : 0);
			buf.append(")");
			buf.append(" (");
			buf.append(nbRules);
			buf.append(")");
		}
		return buf.toString();
	}
}
