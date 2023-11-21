package bpm.mdx.parser.result;


public class PeriodFunctionItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2648853377708386262L;
	
	private NodeEvaluator measure;
	private NodeEvaluator level;
	private NodeEvaluator member;
	private boolean isLast;
	
	public NodeEvaluator getMeasure() {
		return measure;
	}
	public void setMeasure(NodeEvaluator measure) {
		this.measure = measure;
	}
	public NodeEvaluator getLevel() {
		return level;
	}
	public void setLevel(NodeEvaluator level) {
		this.level = level;
	}
	public NodeEvaluator getMember() {
		return member;
	}
	public void setMember(NodeEvaluator member) {
		this.member = member;
	}
	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}
	public boolean isLast() {
		return isLast;
	}
	
	

}
