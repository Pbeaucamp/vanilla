package bpm.gwt.workflow.commons.client.workflow.properties;

import java.util.List;

import bpm.workflow.commons.beans.Activity;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class PropertiesListBox {

	private Label lbl;
	private ListBox listBox;
	private Image imgRefresh;

	public PropertiesListBox(Label lbl, ListBox listBox, Image imgRefresh) {
		this.lbl = lbl;
		this.listBox = listBox;
		this.imgRefresh = imgRefresh;
	}

	public void setVisible(boolean visible) {
		if (lbl != null) {
			this.lbl.setVisible(visible);
		}
		this.listBox.setVisible(visible);
		if (imgRefresh != null) {
			this.imgRefresh.setVisible(visible);
		}
	}

	public ListBox getListBox() {
		return listBox;
	}

	public int getSelectedIndex() {
		return listBox.getSelectedIndex();
	}

	public void setSelectedIndex(int selectedIndex) {
		this.listBox.setSelectedIndex(selectedIndex);
	}

	public String getValue(int selectedIndex) {
		return listBox.getValue(selectedIndex);
	}

	public String getValue() {
		return listBox.getValue(listBox.getSelectedIndex());
	}

	public void clear() {
		this.listBox.clear();
	}

	public void addItem(String name, String value) {
		this.listBox.addItem(name, value);
	}

	public void addItem(String name) {
		this.listBox.addItem(name);
	}

	public void setItems(List<PropertiesPanel<Activity>.ListItem> items) {
		this.listBox.clear();

		for (PropertiesPanel<Activity>.ListItem item : items) {
			this.listBox.addItem(item.getItem(), item.getValue());
		}
	}

	public Label getLbl() {
		return lbl;
	}

	public void setLbl(Label lbl) {
		this.lbl = lbl;
	}
	
	
}
