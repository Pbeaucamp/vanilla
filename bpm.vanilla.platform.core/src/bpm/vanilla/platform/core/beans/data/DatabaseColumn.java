package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;

public class DatabaseColumn implements Serializable, IDatabaseObject {
	
	public static final int DIMENSION_TYPE = 0;
	public static final int MEASURE_TYPE = 1;
	public static final int PROPERTY_TYPE = 2;
	public static final int UNDEFINED_TYPE = 3;
	
	public static final int BEHAVIOR_SUM = 0;
	public static final int BEHAVIOR_COUNT = 1;
	public static final int BEHAVIOR_AVG = 2;
	public static final int BEHAVIOR_MIN = 3;
	public static final int BEHAVIOR_MAX = 4;
	
	private static final long serialVersionUID = 1L;

	private DatabaseTable parent;
	private String parentSQLOriginName;
	
	private String name;
	private String customName;
	private String originName;
	private String type;

	private int metadataType = UNDEFINED_TYPE;
	private int measureBehavior = BEHAVIOR_SUM;
	
	public DatabaseColumn() { }
	
	public void setParent(DatabaseTable parent) {
		this.parent = parent;
	}
	
	public DatabaseTable getParent() {
		return parent;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getCustomName() {
		return customName != null && !customName.isEmpty() ? customName : name;
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getMetadataType() {
		return metadataType;
	}
	
	public void setMetadataType(int metadataType) {
		this.metadataType = metadataType;
	}
	
	public int getMeasureBehavior() {
		return measureBehavior;
	}
	
	public void setMeasureBehavior(int measureBehavior) {
		this.measureBehavior = measureBehavior;
	}
	
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	
	public String getOriginName() {
		return originName != null && !originName.isEmpty() ? originName : (parent != null ? parent.getName() + "." + name : name);
	}
	
	public String getParentSQLOriginName() {
		return parentSQLOriginName;
	}
	
	public void setParentSQLOriginName(String parentSQLOriginName) {
		this.parentSQLOriginName = parentSQLOriginName;
	}

	@Override
	public String toString() {
		return parent != null ? parent.getName() + "." + name : "unknown." + name;
	}
}
