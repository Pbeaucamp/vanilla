package bpm.update.manager.api.beans;

public class UpdateProgress implements IProgress {

	private static final long serialVersionUID = 1L;
	
	private UpdateAction currentAction;
	private UpdateResult result;
	
	private double percentDownload;
	private String downloadMessage;
	
	private boolean done = false;
	
	public UpdateProgress() { }
	
	public double getPercentDownload() {
		return percentDownload;
	}
	
	public UpdateAction getCurrentAction() {
		return currentAction;
	}
	
	public void setCurrentAction(UpdateAction currentAction) {
		this.currentAction = currentAction;
		setDownloadMessage("");
		setProgress(0);
	}
	
	public boolean isDone() {
		return done;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}

	public String getDownloadMessage() {
		return downloadMessage != null ? downloadMessage : "";
	}

	@Override
	public void setDownloadMessage(String downloadMessage) {
		this.downloadMessage = downloadMessage;
	}

	@Override
	public void setProgress(double progress) {
		this.percentDownload = progress;
	}

	public UpdateResult getResult() {
		return result;
	}

	public void setResult(UpdateResult result) {
		this.result = result;
		setProgress(100);
	}
}