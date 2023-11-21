package bpm.gwt.aklabox.commons.client.panels;


public abstract class Tab extends AbstractCenterPanel {

	private TabManager tabManager;
	private String title;
	private boolean isCloseable;
	
	private TabHeader tabHeader;
	
	public Tab(TabManager tabManager, String title, boolean isCloseable) {
		super("");
		this.tabManager = tabManager;
		this.title = title;
		this.isCloseable = isCloseable;
	}
	
	public void setTabHeaderTitle(String title) {
		this.title = title;
	//	tabHeader.setTitle(title);
	}
	
	public void refreshTitle(){
		tabHeader.setTitle(title);
	}
	
	public TabHeader buildTabHeader() {
		if(tabHeader == null) {
			tabHeader = new TabHeader(title, this, tabManager, isCloseable);
		}
		return tabHeader;
	}
	
	public boolean isOpen() {
		return tabHeader != null ? tabHeader.isOpen() : false;
	}
	
	public void close() {
		if(tabHeader != null) {
			tabHeader.close();
		}
	}
}
