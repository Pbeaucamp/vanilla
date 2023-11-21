package bpm.faweb.client.dnd;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.center.chart.ChartPanel.ListType;
import bpm.faweb.client.tree.FaWebTreeItem;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;

public class DraggableLabel extends FocusPanel{
	
	private ListType type;
	private String text;
	private Label lbl;

	public DraggableLabel(String text, ListType type) {
		super();
		this.text = text;
		this.type = type;
		lbl = new Label(text);
		
		this.add(lbl);
		
		showCaption();
	}

	public String getLabel() {
		return text;
	}
	
	public void showCaption() {
		try {
			FaWebTreeItem it = MainPanel.getInstance().getNavigationPanel().getDimensionPanel().findRootItem2(text);
			findCaption(it, text);
		} catch(Exception e) {
		}

	}
	
	private void findCaption(FaWebTreeItem it, String filter2) {
		for(int i = 0 ; i < it.getChildCount() ; i++) {
			FaWebTreeItem child = (FaWebTreeItem) it.getChild(i);
			if(child.getItemDim().getUname().equals(filter2)) {
				
				String[] f = text.split("\\.");
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
				lbl.setText(newval);
				this.setTitle(text);
				return;
			}
			findCaption(child, filter2);
		}
	}
	
	
	

	public ListType getType(){
		return type;
	}
}
