package bpm.map.viewer.web.client.utils;

import bpm.fm.api.model.Theme;
import bpm.map.viewer.web.client.wizard.AddMapComponentsPage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

public class ThemeTreeItem extends TreeItem{

	private CheckBox checkbox = new CheckBox();
	private AddMapComponentsPage wizard;
	private Theme theme;
	private HorizontalPanel treePanel = new HorizontalPanel();

	public ThemeTreeItem(AddMapComponentsPage wizard, Theme theme, boolean check) {
		super();
		this.wizard = wizard;
		this.theme = theme;
		this.checkbox.setText(theme.getName());
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
		this.wizard.onTreeSelection(checked, theme);
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}
	
	public boolean getCheckBoxState(){
		return checkbox.getValue();
	}
	
	public void setCheckBoxState(boolean check){
		checkbox.setValue(check);
	}
}
