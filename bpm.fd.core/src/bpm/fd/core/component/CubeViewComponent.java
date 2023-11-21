package bpm.fd.core.component;

import java.util.List;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.IComponentRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;

public class CubeViewComponent extends DashboardComponent implements IComponentRepositoryItem, IComponentOption {

	private static final long serialVersionUID = 1L;

	private int itemId;

	private boolean interactive = false;
	private boolean showDimensions = false;
	private boolean showCubeFunctions = false;
	private List<CubeElement> elements = null;

	@Override
	public int getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public int getItemType() {
		return IRepositoryApi.FAV_TYPE;
	}

	public boolean isInteractive() {
		return interactive;
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}

	public boolean isShowDimensions() {
		return showDimensions;
	}

	public void setShowDimensions(boolean showDimensions) {
		this.showDimensions = showDimensions;
	}

	public boolean isShowCubeFunctions() {
		return showCubeFunctions;
	}

	public void setShowCubeFunctions(boolean showCubeFunctions) {
		this.showCubeFunctions = showCubeFunctions;
	}

	public List<CubeElement> getElements() {
		return elements;
	}

	public void setElements(List<CubeElement> elements) {
		this.elements = elements;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.OLAP_VIEW;
	}

	@Override
	protected void clearData() {
		this.itemId = 0;
		this.elements = null;
	}
}
