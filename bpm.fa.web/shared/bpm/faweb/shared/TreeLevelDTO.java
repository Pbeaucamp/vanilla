package bpm.faweb.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeLevelDTO extends TreeParentDTO implements IsSerializable {
	private String caption;
	
	public TreeLevelDTO() {
		super();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
}
