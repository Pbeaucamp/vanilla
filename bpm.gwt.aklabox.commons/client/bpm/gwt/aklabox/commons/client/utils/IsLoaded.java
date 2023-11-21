package bpm.gwt.aklabox.commons.client.utils;

public class IsLoaded {

	private boolean isLoaded;
	private int totalLoad;
	private int loadFinish;

	public IsLoaded() {
	}
	
	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public void setTotalLoad(int totalLoad) {
		this.totalLoad = totalLoad;
	}

	public void incrementLoadFinish() {
		this.loadFinish++;
	}

	public boolean isLoaded() {
		return isLoaded && totalLoad == loadFinish;
	}
}