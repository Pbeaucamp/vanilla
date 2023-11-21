package bpm.faweb.client.openlayer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

public class MapListBox extends ListBox {
	private List<List<String>> items;

	public MapListBox() {
		super();
	}

	public void addItem(final String item, final List<String> obj) {
		if (items == null) {
			items = new ArrayList<List<String>>();
		}

		items.add(obj);
		addItem(item, item);
	}

	public List<String> getItem(final int index) {
		return items.get(index);
	}

}
