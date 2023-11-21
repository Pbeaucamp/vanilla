package bpm.gwt.aklabox.commons.client.tree;

import bpm.document.management.core.model.aklademat.Classification;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class ClassificationTreeItem extends TreeItem {

	private Classification classification;
	
	public ClassificationTreeItem(Classification classification) {
		this.setWidget(new Label(classification.getName()));
		
		if (classification.getChilds() != null) {
			for (Classification child : classification.getChilds()) {
				addItem(new ClassificationTreeItem(child));
			}
		}
	}
	
	public Classification getClassification() {
		return classification;
	}
}
