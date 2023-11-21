package bpm.workflow.commons.resources.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class FTPAction implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeAction {
		MOVE(0),
		DELETE(1);

		private int type;

		private static Map<Integer, TypeAction> map = new HashMap<Integer, TypeAction>();
		static {
			for (TypeAction actionType : TypeAction.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeAction(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeAction valueOf(int actionType) {
			return map.get(actionType);
		}
	}
	
	private TypeAction type;
	private VariableString value;
	
	public FTPAction() { }
	
	public FTPAction(TypeAction type, VariableString value) {
		this.type = type;
		this.value = value;
	}
	
	public TypeAction getType() {
		return type;
	}

	public VariableString getValueVS() {
		return value;
	}
	
	public String getValueDisplay() {
		return value.getStringForTextbox();
	}

	public String getValue(List<Parameter> parameters, List<Variable> variables) {
		return value.getString(parameters, variables);
	}

	public void setValue(VariableString value) {
		this.value = value;
	}

	public List<Parameter> getParameters() {
		return value != null ? value.getParameters() : new ArrayList<Parameter>();
	}
	
	public List<Variable> getVariables() {
		return value != null ? value.getVariables() : new ArrayList<Variable>();
	}
}
