package bpm.metadata.query;

public class EffectiveQuery {
	private String generatedQuery;
	private int weight;
	public EffectiveQuery(String generatedQuery, int weight) {
		super();
		this.generatedQuery = generatedQuery;
		this.weight = weight;
	}
	/**
	 * @return the generatedQuery
	 */
	public String getGeneratedQuery() {
		return generatedQuery;
	}
	/**
	 * @param generatedQuery the generatedQuery to set
	 */
	public void setGeneratedQuery(String generatedQuery) {
		this.generatedQuery = generatedQuery;
	}
	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
