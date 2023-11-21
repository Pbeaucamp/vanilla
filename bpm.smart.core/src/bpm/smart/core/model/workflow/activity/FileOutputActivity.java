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
import bpm.workflow.commons.resources.Cible;

public class FileOutputActivity extends Activity {

	public enum TypeOutput {
		CSV(0), XLS(1), TXT(2);

		private int type;

		private static Map<Integer, TypeOutput> map = new HashMap<Integer, TypeOutput>();
		static {
			for (TypeOutput actionType : TypeOutput.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeOutput(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeOutput valueOf(int actionType) {
			return map.get(actionType);
		}
	}

	private static final long serialVersionUID = 1L;

	private TypeOutput type;

	private VariableString dataset = new VariableString();
	private VariableString outputFile = new VariableString();

	private Cible cible;

	public FileOutputActivity() {
	}

	public FileOutputActivity(String name) {
		super(TypeActivity.OUTPUT_FILE, name);
		this.type = TypeOutput.CSV;
		this.dataset = new VariableString();
		this.outputFile = new VariableString();
	}

	public TypeOutput getTypeOutput() {
		return type;
	}

	public void setTypeOutput(TypeOutput type) {
		this.type = type;
	}

	public VariableString getDatasetVS() {
		return dataset;
	}

	public String getDataset() {
		return dataset.getStringForTextbox();
	}

	public void setDataset(VariableString dataset) {
		this.dataset = dataset;
	}

	public String getDataset(List<Parameter> parameters, List<Variable> variables) {
		return dataset.getString(parameters, variables);
	}

	public VariableString getOutputFileVS() {
		return outputFile;
	}

	public String getOutputFile() {
		return outputFile.getStringForTextbox();
	}

	public void setOutputFile(VariableString outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputFile(List<Parameter> parameters, List<Variable> variables) {
		return outputFile.getString(parameters, variables);
	}

	public void setCible(Cible cible) {
		this.cible = cible;
	}

	public Integer getCibleId() {
		return cible != null ? cible.getId() : null;
	}

	public Cible getCible(List<Cible> cibles) {
		return cible != null ? findResource(cible, cibles) : null;
	}

	protected Cible findResource(Cible cible, List<Cible> cibles) {
		if (cibles != null) {
			for (Cible currentResource : cibles) {
				if (cible.getId() == currentResource.getId()) {
					return currentResource;
				}
			}
		}
		return this.cible;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return new ArrayList<Parameter>();
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
