package bpm.mdx.parser.result;

public class TopCountItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1469214698014691293L;
	private NodeEvaluator set;
	private int count;
	private NodeEvaluator measure;
	private boolean isTop;
	
	public void setSet(NodeEvaluator set) {
		this.set = set;
	}
	
	public NodeEvaluator getSet() {
		return set;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void setMeasure(NodeEvaluator measure) {
		this.measure = measure;
	}

	public NodeEvaluator getMeasure() {
		return measure;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public boolean isTop() {
		return isTop;
	}
	
}
