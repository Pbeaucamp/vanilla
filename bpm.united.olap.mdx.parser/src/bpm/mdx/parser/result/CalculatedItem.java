package bpm.mdx.parser.result;

import java.util.ArrayList;
import java.util.List;

public class CalculatedItem extends NodeEvaluator {

	private List<NodeEvaluator> items = new ArrayList<NodeEvaluator>();
	private String formula;
	
	public List<NodeEvaluator> getItems() {
		return items;
	}
	
	public void setItems(List<NodeEvaluator> items) {
		this.items = items;
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public void addItem(NodeEvaluator node) {
		items.add(node);
	}
	
}
