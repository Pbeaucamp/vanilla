package bpm.gwt.commons.shared.repository;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemCube extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private List<PortailItemCubeView> views = new ArrayList<PortailItemCubeView>();
	
	public PortailItemCube() { }
	
	public PortailItemCube(RepositoryItem item, String typeName) {
		super(item, typeName);
	}

	public List<PortailItemCubeView> getViews() {
		return views;
	}
	
	public void addView(PortailItemCubeView view) {
		if(views == null) {
			this.views = new ArrayList<PortailItemCubeView>();
		}
		
		view.setParent(this);
		
		this.views.add(view);
	}
	
	@Override
	public int getType() {
		return IRepositoryApi.FA_CUBE_TYPE;
	}
	
	public int getFASDParentId() {
		if(getParentItem() == null) {
			return 0;
		}
		else {
			return getParentItem().getId();
		}
	}
}
