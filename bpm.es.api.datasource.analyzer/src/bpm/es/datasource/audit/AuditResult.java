package bpm.es.datasource.audit;

public class AuditResult {
	
	private boolean succeed;
	private String effectiveQuery;
	private String dataSetName;
	private String failulreCause;
	
	
	public AuditResult(boolean succeed, String effectiveQuery,
			String dataSetName, String failulreCause) {
		super();
		this.succeed = succeed;
		this.effectiveQuery = effectiveQuery;
		this.dataSetName = dataSetName;
		this.failulreCause = failulreCause;
	}


	/**
	 * @return the succeed
	 */
	public boolean isSucceed() {
		return succeed;
	}


	/**
	 * @return the effectiveQuery
	 */
	public String getEffectiveQuery() {
		return effectiveQuery;
	}


	/**
	 * @return the dataSetName
	 */
	public String getDataSetName() {
		return dataSetName;
	}


	/**
	 * @return the failulreCause
	 */
	public String getFailulreCause() {
		return failulreCause;
	}
	
	
}
