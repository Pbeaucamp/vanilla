package bpm.gwt.commons.shared.repository;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemCubeView extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;

	public PortailItemCubeView() { }
	
	public PortailItemCubeView(RepositoryItem item, String typeName) {
		super(item, typeName);
	}

	public PortailItemCube getCubeDto() {
		return getParentItem() != null && getParentItem() instanceof PortailItemCube ? (PortailItemCube) getParentItem() : null;
	}

	@Override
	public int getType() {
		return IRepositoryApi.FAV_TYPE;
	}
}
