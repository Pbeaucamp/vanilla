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

public class DecisionTreeActivity extends Activity {
		
	private static final long serialVersionUID = 1L;
	
	public static final String RPART_ANOVA = "anova";
	public static final String RPART_POISSON = "poisson";
	public static final String RPART_CLASS = "class";
	public static final String RPART_EXP= "exp";
	
	public enum TypeRPart {
		ANOVA(RPART_ANOVA), POISSON(RPART_POISSON), CLASS(RPART_CLASS),
		EXP(RPART_EXP);

		private String type;

		private static Map<String, TypeRPart> map = new HashMap<String, TypeRPart>();
		static {
			for (TypeRPart actionType : TypeRPart.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeRPart(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	
	private boolean withGraph;
	private String datasetName = "";
	private TypeRPart rpartType;
	private String xColumnName = ""; //qualitative column
	private List<String> yColumnNames; //quantitatives columns selected
	private List<String> numColnames; //all quantitatives columns

	public DecisionTreeActivity(){}
	
	public DecisionTreeActivity(String name) {
		super(TypeActivity.DECISION_TREE, name);
		this.rpartType = TypeRPart.CLASS;
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

	public TypeRPart getRpartType() {
		return rpartType;
	}

	public void setRpartType(TypeRPart rpartType) {
		this.rpartType = rpartType;
	}

	public String getxColumnName() {
		return xColumnName;
	}

	public void setxColumnName(String xColumnName) {
		this.xColumnName = xColumnName;
	}

	public List<String> getyColumnNames() {
		return yColumnNames;
	}

	public void setyColumnNames(List<String> yColumnNames) {
		this.yColumnNames = yColumnNames;
	}

	public List<String> getNumColnames() {
		return numColnames;
	}

	public void setNumColnames(List<String> numColnames) {
		this.numColnames = numColnames;
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
