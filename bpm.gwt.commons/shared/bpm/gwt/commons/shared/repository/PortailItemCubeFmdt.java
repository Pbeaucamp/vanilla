package bpm.gwt.commons.shared.repository;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemCubeFmdt extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	private String url;
	
	public PortailItemCubeFmdt() {
		super();
	}

	public PortailItemCubeFmdt(String url) {
		super();
		this.url = url;
	}

	public PortailItemCubeFmdt(String url, RepositoryItem item) {
		super(item, "FmdtCube");
		this.url = url;
	}

	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	
	

}
