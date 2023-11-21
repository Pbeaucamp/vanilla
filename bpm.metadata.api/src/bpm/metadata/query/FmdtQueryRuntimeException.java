package bpm.metadata.query;

public class FmdtQueryRuntimeException extends RuntimeException{
	private String effectiveQuery;
	private int weight;
	public FmdtQueryRuntimeException(String message, String effectiveQuery, int weight){
		super(message);
		this.effectiveQuery = effectiveQuery;
		this.weight = weight;
	}
	
	public String getEffectiveQuery(){
		return effectiveQuery;
	}
	
	public int getWeight(){
		return weight;
	}
}
