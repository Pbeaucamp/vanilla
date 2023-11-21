package bpm.faweb.shared;

import java.util.ArrayList;
import java.util.List;

public class ItemCube extends IDirectoryItemDTO {

	private IDirectoryItemDTO parent;
	private List<ItemCubeView> views = new ArrayList<ItemCubeView>();

	public ItemCube() {
	}

	public ItemCube(String name) {
		super(name);
	}

	public List<ItemCubeView> getViews() {
		return views;
	}
	
	public void setParent(IDirectoryItemDTO parent) {
		this.parent = parent;
	}

	public void addView(ItemCubeView view) {
		if (views == null) {
			this.views = new ArrayList<ItemCubeView>();
		}

		view.setParent(this);

		this.views.add(view);
	}

	public int getFASDParentId() {
		if (parent == null) {
			return 0;
		}
		else {
			return parent.getId();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
