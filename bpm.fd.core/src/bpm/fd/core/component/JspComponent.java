package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.IComponentUrl;

public class JspComponent extends DashboardComponent implements IComponentUrl, IComponentOption {
	
	private static final long serialVersionUID = 1L;
	
	private String url;
	private boolean showBorder;

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean showBorder() {
		return showBorder;
	}
	
	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.DASHLET;
	}

	@Override
	protected void clearData() {
		this.url = null;
	}
}
