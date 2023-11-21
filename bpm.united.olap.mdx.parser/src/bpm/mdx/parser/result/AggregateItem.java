package bpm.mdx.parser.result;

import java.util.ArrayList;
import java.util.List;

public class AggregateItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7857080658972811129L;

	private List<NodeEvaluator> nodes;
	
	public List<NodeEvaluator> getNodes() {
		return nodes;
	}
	
	public void setNodes(List<NodeEvaluator> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(NodeEvaluator node) {
		if(nodes == null) {
			nodes = new ArrayList<NodeEvaluator>();
		}
		nodes.add(node);
	}
	
}
