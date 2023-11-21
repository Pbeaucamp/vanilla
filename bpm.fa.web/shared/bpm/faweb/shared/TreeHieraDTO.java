package bpm.faweb.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeHieraDTO extends TreeParentDTO implements IsSerializable {
	private String caption;
	
	public TreeHieraDTO() {
		super();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	

}
