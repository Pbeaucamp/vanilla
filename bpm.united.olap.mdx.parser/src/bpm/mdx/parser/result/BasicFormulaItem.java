package bpm.mdx.parser.result;

public class BasicFormulaItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9079164004637109533L;

	private NodeEvaluator leftItem;
	private NodeEvaluator rightItem;
	private String operator;
	
	public NodeEvaluator getLeftItem() {
		return leftItem;
	}
	
	public void setLeftItem(NodeEvaluator leftItem) {
		this.leftItem = leftItem;
	}
	
	public NodeEvaluator getRightItem() {
		return rightItem;
	}
	
	public void setRightItem(NodeEvaluator rightItem) {
		this.rightItem = rightItem;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
}
