package bpm.gwt.commons.shared.cmis;

import java.util.List;

public class CmisFolder extends CmisItem {

	private boolean loaded;
	
	private List<CmisItem> childs;
	
	public CmisFolder() {
		super();
	}
	
	public CmisFolder(String itemId, String name) {
		super(itemId, name, CmisType.FOLDER);
	}

	public void setChilds(List<CmisItem> childs) {
		this.loaded = true;
		this.childs = childs;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public List<CmisItem> getChilds() {
		return childs;
	}
}
