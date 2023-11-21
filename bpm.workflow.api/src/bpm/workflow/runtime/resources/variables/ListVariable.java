package bpm.workflow.runtime.resources.variables;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkfowModelParameter;

/**
 * List of the variables contained in the workspace
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ListVariable {
	private static ListVariable instance;
	private List<Variable> variables;
	private List<Variable> listeVarEnvironnement = new ArrayList<Variable>();

	public static final String VANILLA_HOME = "{$VANILLA_HOME}";
	public static final String GENERATED_REPORTS_HOME = "{$GENERATED_REPORTS_HOME}";
	public static final String VANILLA_FILES = "{$VANILLA_FILES}";
	public static final String VANILLA_MAIL = "{$VANILLA_MAIL}";

	/**
	 * VANILLA_TEMPORARY_FILES = VANILLA_FILES\tempBIW
	 */
	public static final String VANILLA_TEMPORARY_FILES = "{$VANILLA_TEMPORARY_FILES}";

	public static final String USER_DIR = "{$USER_DIR}";

	public static final String[] ENVIRONEMENT_VARIABLE = new String[] { "{$VANILLA_HOME}", "{$GENERATED_REPORTS_HOME}", "{$VANILLA_FILES}", "{$E_TIME}", "{$E_IP}", "{$VANILLA_MAIL}", VANILLA_TEMPORARY_FILES, USER_DIR };
	public static final String[] VANILLA_PATHS = new String[] { VANILLA_HOME, GENERATED_REPORTS_HOME, VANILLA_FILES, VANILLA_TEMPORARY_FILES };

	/**
	 * 
	 * @return the list of variables of the workspace
	 */
	public static ListVariable getInstance() {
		if (instance == null) {
			instance = new ListVariable();
		}
		return instance;
	}

	/**
	 * do not use, only for XML parsing
	 */
	public ListVariable() {
		if (variables == null) {
			/*
			 * Create the environment variables
			 */
			for (int i = 0; i < ENVIRONEMENT_VARIABLE.length; i++) {
				Variable var = new Variable();
				var.setName(ENVIRONEMENT_VARIABLE[i]);
				var.addValue(ENVIRONEMENT_VARIABLE[i]);
				listeVarEnvironnement.add(var);
			}
			variables = new ArrayList<Variable>();
			variables.addAll(listeVarEnvironnement);
		}
		if (instance == null) {
			instance = new ListVariable(variables);
		}
	}

	/**
	 * Set the lis<Variable> of the workspace
	 * 
	 * @param v
	 */
	private ListVariable(List<Variable> v) {
		variables = v;
	}

	/**
	 * 
	 * @return the list<Variable> of the workspace
	 */
	public List<Variable> getVariables() {
		return new ArrayList<Variable>(variables);
	}

	/**
	 * Add a variable into the workspace (this list)
	 * 
	 * @param v
	 *            : Variable
	 * @throws Exception
	 */
	public void addVariable(Variable v) throws Exception {
		if (getVariable(v.getName()) == null) {
			variables.add(v);
		}
		else {
			Variable old = getVariable(v.getName());
			variables.remove(old);
			variables.add(v);
		}
	}

	/**
	 * Remove the specified variable from the list
	 * 
	 * @param v
	 */
	public void removeVariable(Variable v) {
		if (v != null) {
			variables.remove(v);
			instance.removeVariable(v.getName());
		}
	}

	/**
	 * Remove the variable from the list thanks to its name
	 * 
	 * @param variableName
	 */
	public void removeVariable(String variableName) {
		Variable v = getVariable(variableName);
		if (v != null) {
			variables.remove(v);
		}
	}

	/**
	 * 
	 * @param name
	 * @return the Variable specified thanks to its name
	 */
	public Variable getVariable(String name) {
		// To ensure compatibility with older versions
		if (!name.endsWith("}")) {
			name += "}";
		}
		if (!name.startsWith("{$")) {
			name = "{$" + name;
		}
		for (Variable v : variables) {
			if (v.getName().equals(name)) {
				return v;
			}
		}

		Variable var = new Variable();
		var.setName(name);
		this.variables.add(var);
		return var;
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("listVariable");
		for (Variable v : variables) {
			e.add(v.getXmlNode());
		}
		return e;
	}

	/**
	 * 
	 * @return the String[]
	 */
	public String[] getNoEnvironementVariable() {
		String[] res = new String[variables.size() - listeVarEnvironnement.size()];
		int index = 0;
		for (int i = 0; i < variables.size(); i++) {
			Variable v = variables.get(i);
			if (!listeVarEnvironnement.contains(v)) {
				res[index] = v.getName();
				index++;
			}
		}
		return res;
	}

	/**
	 * 
	 * @return the String[] containing all the names of the variables which are
	 *         in the workspace
	 */
	public String[] getArray(WorkflowModel model) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < variables.size(); i++) {
			if (!contains(list, variables.get(i).getName())) {
				list.add(variables.get(i).getName());
			}
		}

		for (WorkfowModelParameter param : model.getParameters()) {
			if (!contains(list, "{$" + param.getName() + "}")) {
				list.add("{$" + param.getName() + "}");
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private boolean contains(List<String> list, String itemToFind) {
		if (list != null) {
			for (String item : list) {
				if (item.equals(itemToFind)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Update the specified variable
	 * 
	 * @param v
	 *            : Variable
	 */
	public void updateVariable(Variable v) {
		for (Variable var : variables) {
			if (var.getName().equalsIgnoreCase(v.getName())) {
				var.setType(v.getType());
				var.setValues(v.getValues());
			}
		}

	}

}
