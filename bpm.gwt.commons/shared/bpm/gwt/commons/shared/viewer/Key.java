package bpm.gwt.commons.shared.viewer;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Key implements IsSerializable{
	/*
	 * This key is used to retrieve the report in the reporting component
	 */
	private String key;
	
	/*
	 * This key is used to retrieve the report from the portal server from the client side
	 */
	private String outputFormat;
	
	private boolean isReady;

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public boolean isReady() {
		return isReady;
	}
}
