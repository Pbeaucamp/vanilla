package bpm.gwt.commons.client.panels;

import bpm.vanilla.platform.core.repository.IRepositoryObject;

public interface TabManager {

	public void closeTab(AbstractTabHeader tab);

	public void selectTab(AbstractTabHeader tabHeader);
	
	public void openViewer(IRepositoryObject item);
	
	public int getIndex(AbstractTabHeader tabHeader);

	public void updatePosition(String tabId, int index);
	
	public void postProcess();
}
