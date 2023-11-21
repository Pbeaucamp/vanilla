package bpm.gwt.commons.shared.cmis;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CmisItem implements IsSerializable {

	public enum CmisType {
		FOLDER,
		DOCUMENT
	}
	
	private String itemId;
	private String name;
	private CmisType type;
	
	public CmisItem() { }
	
	public CmisItem(String itemId, String name, CmisType type) {
		this.itemId = itemId;
		this.name = name;
		this.type = type;
	}
	
	public String getItemId() {
		return itemId;
	}
	
	public String getName() {
		return name;
	}
	
	public CmisType getType() {
		return type;
	}

	@Override
	public String toString() {
		return name != null ? name : "";
	}
}
