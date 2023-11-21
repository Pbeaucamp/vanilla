package bpm.mdx.parser.result;

import java.util.ArrayList;
import java.util.List;

public class AxisItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4331210722603274755L;
	private List<NodeEvaluator> items;
	
	@Override
	public List<ICubeItem> evaluate() {
		
		return null;
		
	}

	public void setItems(List<NodeEvaluator> items) {
		this.items = items;
	}

	public List<NodeEvaluator> getItems() {
		return items;
	}
	
	public void addItem(NodeEvaluator item) {
		if(items == null) {
			items = new ArrayList<NodeEvaluator>();
		}
		items.add(item);
	}

}
