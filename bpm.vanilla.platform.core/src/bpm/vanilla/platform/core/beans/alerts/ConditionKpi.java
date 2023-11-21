package bpm.vanilla.platform.core.beans.alerts;




public class ConditionKpi implements IConditionInformation {

	private static final long serialVersionUID = -3806440683920367931L;

	public static final int VALUE_TYPE = 0;
	public static final int STATE_TYPE = 1;
	public static final int MISSING_TYPE = 2;
	
	public static String[] OPERATORS = {"==", "!=", "<", ">", "<=", ">="};
	
	public static final int FIELD_VAL = 0;
	public static final int FIELD_OBJ = 1;
	public static final int FIELD_MIN = 2;
	public static final int FIELD_MAX = 3;
	
	public static String[] FIELDS = {"Metric value", "Objective", "Minimum", "Maximum"};
	
	public static String[] ALERT_TYPES = {"VALUE", "STATE", "MISSING"};

	public static final int MISSING_VAL_ONLY = 0;
	public static final int MISSING_OBJ_ONLY = 1;
	public static final int MISSING_VAL_OBJ = 2;
	
	public static String[] MISSING_TYPES = {"VALUE ONLY", "OBJECTIVE ONLY", "VALUE AND OBJECTIVE"};
	
	public static final int STATE_ABOVE = 0;
	public static final int STATE_EQUAL = 1;
	public static final int STATE_UNDER = 2;
	
	public static String[] STATE_TYPES = {"ABOVE OBJECTIVE", "EQUAL TO OBJECTIVE", "UNDER OBJECTIVE"};
	
	private String type;
	private String missingType;
	private String stateType;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMissingType() {
		return missingType;
	}

	public void setMissingType(String missingType) {
		this.missingType = missingType;
	}

	public String getStateType() {
		return stateType;
	}

	public void setStateType(String stateType) {
		this.stateType = stateType;
	}

	@Override
	public boolean equals(Object o) {
		return type == ((ConditionKpi)o).getType() && missingType == ((ConditionKpi)o).getMissingType() && stateType == ((ConditionKpi)o).getStateType();
	}
}
