package bpm.update.manager.api.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GlobalProgress implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public double updateRunning = -1;
	public double nbTotalUpdate;
	
	public List<UpdateProgress> progresses;
	
	public GlobalProgress() { }

	public GlobalProgress(int nbTotalUpdate) {
		this.nbTotalUpdate = nbTotalUpdate;
	}
	
	public void incrementUpdate() {
		this.updateRunning++;
		
		if (progresses == null) {
			this.progresses = new ArrayList<>();
		}
		this.progresses.add(new UpdateProgress());
	}
	
	public double getUpdateProgress() {
		if (updateRunning + 1 == nbTotalUpdate && isDone()) {
			return 100;
		}
		return updateRunning > 0 ? (Double) (updateRunning*100/nbTotalUpdate) : 0;
	}
	
	public int getUpdateRunning() {
		return new Double(updateRunning).intValue();
	}
	
	public int getNbTotalUpdate() {
		return new Double(nbTotalUpdate).intValue();
	}
	
	public UpdateProgress getCurrentProgress() {
		return progresses.get(getUpdateRunning());
	}

	public void setCurrentAction(UpdateAction action) {
		UpdateProgress progress = getCurrentProgress();
		if (progress != null) {
			progress.setCurrentAction(action);
		}
	}

	public void setResult(UpdateResult result) {
		UpdateProgress progress = getCurrentProgress();
		if (progress != null) {
			progress.setResult(result);
		}
	}

	public void setDone(boolean done) {
		UpdateProgress progress = getCurrentProgress();
		if (progress != null) {
			progress.setDone(done);
		}
	}

	public UpdateAction getCurrentAction() {
		UpdateProgress progress = getCurrentProgress();
		if (progress != null) {
			return progress.getCurrentAction();
		}
		return UpdateAction.UNDETERMINED;
	}

	public void setProgress(int progressValue) {
		UpdateProgress progress = getCurrentProgress();
		if (progress != null) {
			progress.setProgress(progressValue);
		}
	}

	public boolean isDone() {
		return (updateRunning + 1) == nbTotalUpdate && getCurrentProgress() != null && getCurrentProgress().isDone();
	}
}
