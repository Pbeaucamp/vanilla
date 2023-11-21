package bpm.gwt.workflow.commons.client.tabs;

import com.google.gwt.resources.client.ImageResource;

import bpm.gwt.commons.client.panels.AbstractCenterPanel;


public class HorizontalTab extends AbstractCenterPanel {

	private HorizontalTabManager tabManager;
	private String title;
	private ImageResource icon;
	
	private HorizontalTabButton tabHeader;
	
	public HorizontalTab(String title, ImageResource icon) {
		super("");
		this.title = title;
		this.icon = icon;
	}
	
	public void setTabManager(HorizontalTabManager tabManager) {
		this.tabManager = tabManager;
	}
	
	public void setTabHeaderTitle(String title) {
		this.title = title;
	}
	
	public HorizontalTabButton buildTabHeader() {
		if(tabHeader == null) {
			tabHeader = new HorizontalTabButton(title, icon, this, tabManager);
		}
		return tabHeader;
	}
}
