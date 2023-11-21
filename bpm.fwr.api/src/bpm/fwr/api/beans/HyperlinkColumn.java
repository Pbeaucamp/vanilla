package bpm.fwr.api.beans;

import bpm.fwr.api.beans.dataset.Column;

public class HyperlinkColumn extends Column {

	private String labelUrl;
	private String url;
	private boolean keepColumnValues = false;

	public HyperlinkColumn() {
		super();
	}

	public boolean keepColumnValues() {
		return keepColumnValues;
	}

	public void setKeepColumnValues(boolean keepColumnValues) {
		this.keepColumnValues = keepColumnValues;
	}

	public void setKeepColumnValues(String keepColumnValues) {
		if (keepColumnValues.equalsIgnoreCase("true")) {
			this.keepColumnValues = true;
		}
		else {
			this.keepColumnValues = false;
		}
	}

	public void setLabelUrl(String labelUrl) {
		this.labelUrl = labelUrl;
	}

	public String getLabelUrl() {
		return labelUrl;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
