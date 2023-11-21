package bpm.faweb.client.utils;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.tree.FaWebTreeItem;

import com.google.gwt.user.client.ui.HTML;

public class FaWebFilterHTML extends HTML{

	private String filter;
	
	public FaWebFilterHTML(String filter) {
		super(filter);
		this.filter = filter;
	}

	public void showCaption() {
		try {
			FaWebTreeItem it = MainPanel.getInstance().getNavigationPanel().getDimensionPanel().findRootItem2(filter);
			findCaption(it, filter);
		} catch(Exception e) {
		}

	}
	
	private void findCaption(FaWebTreeItem it, String filter2) {
		for(int i = 0 ; i < it.getChildCount() ; i++) {
			FaWebTreeItem child = (FaWebTreeItem) it.getChild(i);
			if(child.getItemDim().getUname().equals(filter2)) {
				
				String[] f = filter.split("\\.");
				f[f.length - 1] = "[" + child.getItemDim().getName() + "]";
				String newval = "";
				boolean first = true;
				for(String s : f) {
					if(first) {
						first = false;
					}
					else {
						newval += ".";
					}
					newval += s;
				}
				this.setHTML(newval);
				this.setTitle(filter);
				return;
			}
			findCaption(child, filter2);
		}
	}

	public String getFilter() {
		return filter;
	}
	
	
}
