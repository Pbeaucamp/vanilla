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

public class HAClustActivity extends Activity {
		
	private static final long serialVersionUID = 1L;
	
	public static final String DIST_EUCLIDEAN = "euclidean";
	public static final String DIST_MAXIMUM= "maximum";
	public static final String DIST_MANHATTAN = "manhattan";
	public static final String DIST_CANBERRA= "canberra";
	public static final String DIST_BINARY = "binary";
	public static final String DIST_MINKOWSKI= "minkowski";
	public static final String CLUST_WARD = "ward.D";
	public static final String CLUST_WARD2 = "ward.D2";
	public static final String CLUST_SINGLE = "single";
	public static final String CLUST_COMPLETE = "complete";
	public static final String CLUST_AVERAGE = "average";
	public static final String CLUST_MCQUITTY = "mcquitty";
	public static final String CLUST_MEDIAN = "median";
	public static final String CLUST_CENTROID = "centroid";
	
	public enum TypeDist {
		EUCLIDEAN(DIST_EUCLIDEAN), MAXIMUM(DIST_MAXIMUM), MANHATTAN(DIST_MANHATTAN),
		CANBERRA(DIST_CANBERRA), BINARY(DIST_BINARY), MINKOWSKI(DIST_MINKOWSKI);

		private String type;

		private static Map<String, TypeDist> map = new HashMap<String, TypeDist>();
		static {
			for (TypeDist actionType : TypeDist.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeDist(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	public enum TypeClust {
		WARD_D(CLUST_WARD), WARD_D2(CLUST_WARD2), SINGLE(CLUST_SINGLE),
		COMPLETE(CLUST_COMPLETE), AVERAGE(CLUST_AVERAGE), MCQUITTY(CLUST_MCQUITTY), 
		MEDIAN(CLUST_MEDIAN), CENTROID(CLUST_CENTROID);

		private String type;

		private static Map<String, TypeClust> map = new HashMap<String, TypeClust>();
		static {
			for (TypeClust actionType : TypeClust.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeClust(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	
	private boolean rotate;
	private String datasetName = "";
	private TypeDist distType;
	private TypeClust clustType;

	public HAClustActivity(){}
	
	public HAClustActivity(String name) {
		super(TypeActivity.HAC, name);
		this.distType = TypeDist.EUCLIDEAN;
		this.clustType = TypeClust.WARD_D;
	}
	
	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public boolean isRotate() {
		return rotate;
	}

	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	public TypeDist getDistType() {
		return distType;
	}

	public void setDistType(TypeDist distType) {
		this.distType = distType;
	}

	public TypeClust getClustType() {
		return clustType;
	}

	public void setClustType(TypeClust clustType) {
		this.clustType = clustType;
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
