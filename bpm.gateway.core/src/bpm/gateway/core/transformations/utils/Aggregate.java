package bpm.gateway.core.transformations.utils;

/**
 * This class is to manage information about AgrregateTransformation
 * @author LCA
 *
 */
public class Aggregate {
	public static final String[] FUNCTIONS = new String[]{
		"SUM", "MAXIMUM", "MINIMUM", "AVERAGE", "COUNT", "DISTINCT COUNT"
	};
	
	public static final int SUM = 0;
	public static final int MAXIMUM = 1;
	public static final int MINIMUM = 2;
	public static final int AVERAGE = 3;
	public static final int COUNT = 4;
	public static final int DISTINCT_COUNT = 5;

	private String streamElementName;
	public int function = 0;
	
	public Integer streamElementNum;
	
	/**
	 * Only used by AggregateTransformation refreshDescriptor method for compatibility with old GTW Model
	 * @return the index of the stream which is aggregate, null otherwise
	 */
	public Integer getStreamElementNum() {
		return streamElementNum;
	}
	
	/**
	 * Only used by AggregateTransformation refreshDescriptor method for compatibility with old GTW Model
	 */
	public void setStreamElementNumberToNull() {
		this.streamElementNum = null;
	}
	
	/**
	 * Only used by digester for old models
	 * @param the index of the StreamElement concerned by this Aggregate Function
	 */
	public void setStreamElementNum(String streamElementNum) {
		try{
			this.streamElementNum = Integer.parseInt(streamElementNum);
		}catch(NumberFormatException e){ }
	}
	
	public String getStreamElementName(){
		return streamElementName;
	}
	
	public void setStreamElementName(String streamElementName){
		this.streamElementName = streamElementName;
	}
	
	/**
	 * 
	 * @return the constant aggregate Function
	 */
	public int getFunction() {
		return function;
	}
	
	/**
	 * 
	 * @param function Constant
	 */
	public void setFunction(int function) {
		
		this.function = function;
	}
	
	public void setFunction(String function) {
		try{
			this.function = Integer.parseInt(function);
		}catch(NumberFormatException e){
			
		}	
	}
}
