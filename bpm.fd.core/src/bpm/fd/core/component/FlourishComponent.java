package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.IComponentUrl;

public class FlourishComponent extends DashboardComponent implements IComponentUrl, IComponentOption {
	
	private static final long serialVersionUID = 1L;
	
	private String url;

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.FLOURISH;
	}

	@Override
	protected void clearData() {
		this.url = null;
	}
}
