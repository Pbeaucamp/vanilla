package bpm.gwt.commons.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

public abstract class AbstractTabHeader extends Composite implements ClickHandler  {
	
	private Tab tab;
	private TabManager tabManager;

	private boolean open = false;
	private boolean headerOpen = false;
	
	public AbstractTabHeader(Tab tab, TabManager tabManager) {
		this.tab = tab;
		this.tabManager = tabManager;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (tabManager != null) {
			tabManager.selectTab(this);
		}
	}

	public void close() {
		if (tabManager != null) {
			tabManager.closeTab(this);
		}
		setOpen(false);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public boolean isHeaderOpen() {
		return headerOpen;
	}
	
	public void setHeaderOpen(boolean headerOpen) {
		this.headerOpen = headerOpen;
	}

	public Tab getTab() {
		return tab;
	}
	
	public TabManager getTabManager() {
		return tabManager;
	}

	public abstract void setTitle(String title);
	
	public abstract void setModified(boolean isModified);

	public abstract void setSelected(boolean selected);

	public abstract void applySize(double percentage);

}
