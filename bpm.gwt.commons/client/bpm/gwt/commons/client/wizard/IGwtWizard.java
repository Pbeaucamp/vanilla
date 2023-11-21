package bpm.gwt.commons.client.wizard;

import bpm.gwt.commons.client.loading.IWait;

public interface IGwtWizard extends IWait {
	public boolean canFinish();
	
	public void updateBtn();
	
	public void setCurrentPage(IGwtPage page);
}
