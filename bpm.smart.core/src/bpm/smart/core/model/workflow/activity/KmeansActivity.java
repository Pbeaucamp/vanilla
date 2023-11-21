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

public class KmeansActivity extends Activity {
		
	private static final long serialVersionUID = 1L;
	
	public static final String KMEANS_HARTIGAN_WONG = "Hartigan-Wong";
	public static final String KMEANS_LLOYD= "Lloyd";
	public static final String KMEANS_FORGY = "Forgy";
	public static final String KMEANS_MACQUEEN= "MacQueen";
	
	public enum TypeAlgoKMeans {
		HARTIGAN_WONG(KMEANS_HARTIGAN_WONG), LLOYD(KMEANS_LLOYD), FORGY(KMEANS_FORGY),
		MACQUEEN(KMEANS_MACQUEEN);

		private String type;

		private static Map<String, TypeAlgoKMeans> map = new HashMap<String, TypeAlgoKMeans>();
		static {
			for (TypeAlgoKMeans actionType : TypeAlgoKMeans.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeAlgoKMeans(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	
	private boolean withGraph;
	private String datasetName = "";
	private String xColumnName = "";
	private String yColumnName = "";
	private int nbClust;
	private int iterMax;
	private int nStart;
	private TypeAlgoKMeans algoType;

	public KmeansActivity(){}
	
	public KmeansActivity(String name) {
		super(TypeActivity.KMEANS, name);
		this.algoType = TypeAlgoKMeans.HARTIGAN_WONG;
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

	public String getxColumnName() {
		return xColumnName;
	}

	public void setxColumnName(String xColumnName) {
		this.xColumnName = xColumnName;
	}

	public String getyColumnName() {
		return yColumnName;
	}

	public void setyColumnName(String yColumnName) {
		this.yColumnName = yColumnName;
	}

	public int getNbClust() {
		return nbClust;
	}

	public void setNbClust(int nbClust) {
		this.nbClust = nbClust;
	}

	public int getIterMax() {
		return iterMax;
	}

	public void setIterMax(int iterMax) {
		this.iterMax = iterMax;
	}

	public int getnStart() {
		return nStart;
	}

	public void setnStart(int nStart) {
		this.nStart = nStart;
	}

	public TypeAlgoKMeans getAlgoType() {
		return algoType;
	}

	public void setAlgoType(TypeAlgoKMeans algoType) {
		this.algoType = algoType;
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
