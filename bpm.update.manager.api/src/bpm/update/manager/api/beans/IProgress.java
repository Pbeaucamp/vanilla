package bpm.update.manager.api.beans;

import java.io.Serializable;

public interface IProgress extends Serializable {

	public void setProgress(double progress);
	
	public void setDownloadMessage(String downloadMessage);
}
