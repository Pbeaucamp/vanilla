package bpm.gwt.commons.client.panels;

public class Tab extends AbstractCenterPanel {

	private TabManager tabManager;
	private String title;
	private boolean isCloseable;
	
	protected AbstractTabHeader tabHeader;
	
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
	
	public AbstractTabHeader buildTabHeader() {
		if (tabHeader == null) {
			tabHeader = new TabHeader(title, this, tabManager, isCloseable);
		}
		return tabHeader;
	}
	
	public String getTabTitle() {
		return title;
	}
	
	public boolean isOpen() {
		return tabHeader != null ? tabHeader.isOpen() : false;
	}
	
	public boolean isCloseable() {
		return isCloseable;
	}
	
	public void close() {
		if(tabHeader != null) {
			tabHeader.close();
		}
	}

	public TabManager getTabManager() {
		return tabManager;
	}

	/**
	 * Override if you want to do something in the tab
	 * when it is selected
	 */
	public void doActionAfterSelection() { };
	
}
