package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;

public class ImageComponent extends DashboardComponent implements IComponentOption {
	
	private static final long serialVersionUID = 1L;
	
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.IMAGE;
	}

	@Override
	protected void clearData() {
		this.url = null;
	}
}
