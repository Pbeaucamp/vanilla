package bpm.gwt.aklabox.commons.client.wizard;

import bpm.gwt.aklabox.commons.client.loading.IWait;

public interface IGwtWizard extends IWait {
	public boolean canFinish();
	
	public void updateBtn();
	
	public void setCurrentPage(IGwtPage page);
}
