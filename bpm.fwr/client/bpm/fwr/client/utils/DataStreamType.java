package bpm.fwr.client.utils;

import com.google.gwt.user.client.rpc.IsSerializable;

public final class DataStreamType implements IsSerializable{
	
	public static final int DIMENSION_TYPE = 0;
	public static final int MEASURE_TYPE = 1;
	public static final int PROPERTY_TYPE = 2;
	public static final int UNDEFINED_TYPE = 3;
	
	public static String[] TYPE_NAME = new String[]{"Dimension", "Measure", "Property", "Undefined"};
	public static final String[] MEASURE_DEFAULT_BEHAVIOR = new String[]{"SUM", "COUNT", "AVG", "MIN", "MAX"};
	
	public static final int BEHAVIOR_SUM = 0;
	public static final int BEHAVIOR_COUNT = 1;
	public static final int BEHAVIOR_AVG = 2;
	public static final int BEHAVIOR_MIN = 3;
	public static final int BEHAVIOR_MAX = 4;
	
	public DataStreamType() { }
}
