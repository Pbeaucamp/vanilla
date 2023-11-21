package bpm.profiling.database.bean;

public class RuleSetBean {
	public static final int AND = 0;
	public static final int OR = 1;
	public static final int XOR = 2;
	
	private int id;
	private String name;
	private int analysisContentId;
	private String description;
	private int logicalOperator; 
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAnalysisContentId() {
		return analysisContentId;
	}
	public void setAnalysisContentId(int analysisContentId) {
		this.analysisContentId = analysisContentId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLogicalOperator() {
		return logicalOperator;
	}
	
	public void setLogicalOperator(int operator) {
		this.logicalOperator = operator;
	}
	
	
	
	
	
}
