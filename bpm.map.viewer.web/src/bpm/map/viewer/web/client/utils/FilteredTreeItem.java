package bpm.map.viewer.web.client.utils;

import bpm.fm.api.model.utils.LevelMember;
import bpm.map.viewer.web.client.dialogs.ManageMapFilters;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

public class FilteredTreeItem extends TreeItem{
	
	private CheckBox checkbox = new CheckBox();
	private ManageMapFilters parent;
	private LevelMember levelMember;
	
	private HorizontalPanel treePanel = new HorizontalPanel();

	public FilteredTreeItem(ManageMapFilters parent, LevelMember levelMember, boolean check) {
		super();
		this.parent = parent;
		this.levelMember = levelMember;
		this.checkbox.setText(levelMember.getLabel());
		this.checkbox.setValue(check);
		this.treePanel.add(checkbox);
		
		this.setWidget(treePanel);
		this.checkbox.addClickHandler(new ClickHandler() {
		      @Override
		      public void onClick(ClickEvent event) {
		        boolean checked = ((CheckBox) event.getSource()).getValue();
		        checkBoxChange(checked);
		      }
		    });
	}

	private void checkBoxChange(boolean checked){
		this.parent.onFilterSelection(checked, this);
	}

	
	
	public LevelMember getLevelMember() {
		return levelMember;
	}

	public void setLevelMember(LevelMember levelMember) {
		this.levelMember = levelMember;
	}

	public boolean getCheckBoxState(){
		return checkbox.getValue();
	}
	
	public void setCheckBoxState(boolean check) {
		this.checkbox.setValue(check);
	}
}
