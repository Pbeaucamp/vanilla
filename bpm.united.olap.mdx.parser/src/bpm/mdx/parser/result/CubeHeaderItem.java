package bpm.mdx.parser.result;

import java.util.ArrayList;
import java.util.List;

public class CubeHeaderItem implements ICubeItem{

	private List<TermItem> items = new ArrayList<TermItem>();

	public void setItems(List<TermItem> items) {
		this.items = items;
	}

	public List<TermItem> getItems() {
		return items;
	}
	
}
