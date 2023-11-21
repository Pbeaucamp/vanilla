package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class HeadActivity extends Activity {

	public enum TypeHead {
		HEAD(0), TAIL(1);

		private int type;

		private static Map<Integer, TypeHead> map = new HashMap<Integer, TypeHead>();
		static {
			for (TypeHead actionType : TypeHead.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeHead(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeHead valueOf(int actionType) {
			return map.get(actionType);
		}
	}
	
	private static final long serialVersionUID = 1L;

	private String inputDataset = "";
	
	private TypeHead type;
	
	private VariableString outputDataset;
	private VariableString linesNumber;

	public HeadActivity() {}
	
	public HeadActivity(String name) {
		super(TypeActivity.HEAD, name);
		this.type = TypeHead.HEAD;
	}

	public String getInputDataset() {
		return inputDataset;
	}

	public void setInputDataset(String inputDataset) {
		this.inputDataset = inputDataset;
	}

	public TypeHead getTypeHead() {
		return type;
	}
	
	public void setTypeHead(TypeHead type) {
		this.type = type;
	}
	
	public VariableString getOutputDatasetVS() {
		return outputDataset;
	}
	
	public String getOutputDatasetDisplay() {
		return outputDataset.getStringForTextbox();
	}

	public void setOutputDataset(VariableString outputDataset) {
		this.outputDataset = outputDataset;
	}

	public String getOutputDataset(List<Parameter> parameters, List<Variable> variables) {
		return outputDataset.getString(parameters, variables);
	}
	
	
	public VariableString getLinesNumberVS() {
		return linesNumber;
	}
	
	public String getLinesNumberDisplay() {
		return linesNumber.getStringForTextbox();
	}
	
	public void setLinesNumber(VariableString linesNumber) {
		this.linesNumber = linesNumber;
	}

	public String getLinesNumber(List<Parameter> parameters, List<Variable> variables) {
		return linesNumber.getString(parameters, variables);
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(outputDataset.getVariables());
		variables.addAll(linesNumber.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(outputDataset.getParameters());
		parameters.addAll(linesNumber.getParameters());
		return parameters;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
