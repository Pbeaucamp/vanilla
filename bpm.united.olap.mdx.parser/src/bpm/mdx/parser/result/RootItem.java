package bpm.mdx.parser.result;

import java.util.ArrayList;
import java.util.List;

public class RootItem {

	private NodeEvaluator row;
	private NodeEvaluator col;
	private NodeEvaluator where;
	private String from;
	private List<NodeEvaluator> withItems;
	private boolean isNonEmpty = false;
	
	private List<List<ICubeItem>> resultCube;
	
	public void evaluate() {
		
	}

	public void setRow(NodeEvaluator row) {
		this.row = row;
	}

	public NodeEvaluator getRow() {
		return row;
	}

	public void setCol(NodeEvaluator col) {
		this.col = col;
	}

	public NodeEvaluator getCol() {
		return col;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setResultCube(List<List<ICubeItem>> resultCube) {
		this.resultCube = resultCube;
	}

	public List<List<ICubeItem>> getResultCube() {
		return resultCube;
	}

	public void setWhere(NodeEvaluator where) {
		this.where = where;
	}

	public NodeEvaluator getWhere() {
		return where;
	}

	public void setWithItems(List<NodeEvaluator> withItems) {
		this.withItems = withItems;
	}

	public List<NodeEvaluator> getWithItems() {
		return withItems;
	}
	
	public void addWithItem(NodeEvaluator withItem) {
		if(this.withItems == null) {
			this.withItems = new ArrayList<NodeEvaluator>();
		}
		this.withItems.add(withItem);
	}

	public void setNonEmpty(boolean isNonEmpty) {
		this.isNonEmpty = isNonEmpty;
	}

	public boolean isNonEmpty() {
		return isNonEmpty;
	}
}
