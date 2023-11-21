package bpm.mdx.parser.result;

import java.util.List;

public class UnionItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8795480307304785910L;
	private NodeEvaluator leftItem;
	private NodeEvaluator rightItem;
	
	@Override
	public List<ICubeItem> evaluate() {
		
		return null;
	}

	public void setLeftItem(NodeEvaluator leftItem) {
		this.leftItem = leftItem;
	}

	public NodeEvaluator getLeftItem() {
		return leftItem;
	}

	public void setRightItem(NodeEvaluator rightItem) {
		this.rightItem = rightItem;
	}

	public NodeEvaluator getRightItem() {
		return rightItem;
	}
	
}
