package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class CorMatrixActivity extends Activity {
		
	private static final long serialVersionUID = 1L;
	
	public static final String COR_PEARSON = "pearson";
	public static final String COR_KENDALL = "kendall";
	public static final String COR_SPEARMAN = "spearman";
	
	public enum TypeCor {
		PEARSON(COR_PEARSON), KENDALL(COR_KENDALL), SPEARMAN(COR_SPEARMAN);

		private String type;

		private static Map<String, TypeCor> map = new HashMap<String, TypeCor>();
		static {
			for (TypeCor actionType : TypeCor.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeCor(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	private boolean withGraph;
	private String datasetName = "";
	private List<String> colnames;
	private TypeCor corType;

	public CorMatrixActivity(){}
	
	public CorMatrixActivity(String name) {
		super(TypeActivity.CORRELATION_MATRIX, name);
		this.corType = TypeCor.PEARSON;
	}
	
	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public boolean isWithGraph() {
		return withGraph;
	}

	public void setWithGraph(boolean withGraph) {
		this.withGraph = withGraph;
	}

	public List<String> getColnames() {
		return colnames;
	}

	public void setColnames(List<String> colnames) {
		this.colnames = colnames;
	}

	public TypeCor getCorType() {
		return corType;
	}

	public void setCorType(TypeCor corType) {
		this.corType = corType;
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
		return !datasetName.isEmpty();
	}
}
