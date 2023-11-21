package bpm.faweb.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TreeDimDTO extends TreeParentDTO implements IsSerializable {
	private String caption;
	
	public TreeDimDTO() {
		super();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	

}
