package bpm.fwr.api.beans.dataset;

import java.util.ArrayList;
import java.util.List;

public class JoinDataSet extends DataSet {

	private String type;
	private String operator;
	private String leftExpression;
	private String rightExpression;
	private String leftDatasetName;
	private String rightDatasetName;
	
	private List<DataSet> childs;
	
	public JoinDataSet() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getLeftExpression() {
		return leftExpression;
	}

	public void setLeftExpression(String leftExpression) {
		this.leftExpression = leftExpression;
	}

	public String getRightExpression() {
		return rightExpression;
	}

	public void setRightExpression(String rightExpression) {
		this.rightExpression = rightExpression;
	}

	public String getLeftDatasetName() {
		return leftDatasetName;
	}

	public void setLeftDatasetName(String leftDatasetName) {
		this.leftDatasetName = leftDatasetName;
	}

	public String getRightDatasetName() {
		return rightDatasetName;
	}

	public void setRightDatasetName(String rightDatasetName) {
		this.rightDatasetName = rightDatasetName;
	}

	public void setChilds(List<DataSet> childs) {
		this.childs = childs;
	}

	public List<DataSet> getChilds() {
		return childs;
	}
	
	public void addDataset(DataSet dataset) {
		if(childs == null){
			childs = new ArrayList<DataSet>();
		}
		childs.add(dataset);
	}
	
}
