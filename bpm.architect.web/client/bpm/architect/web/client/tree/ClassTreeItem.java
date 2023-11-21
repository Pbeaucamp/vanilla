package bpm.architect.web.client.tree;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;

import com.google.gwt.user.client.ui.TreeItem;

public class ClassTreeItem<T> extends TreeItem {

	private TreeObjectWidget<T> objectWidget;

	private boolean loaded = false;

	public ClassTreeItem(TreeObjectWidget<T> objectWidget) {
		super(objectWidget);
		buildItem(objectWidget);
	}

	public void buildItem(TreeObjectWidget<T> objectWidget) {
		this.objectWidget = objectWidget;

		removeItems();

		T item = objectWidget.getItem();
		if (item instanceof ClassDefinition) {
			ClassDefinition classDef = (ClassDefinition) item;
			if (classDef.getClasses() != null) {
				for (ClassDefinition object : classDef.getClasses()) {
					this.addItem(new ClassTreeItem<ClassDefinition>(new TreeObjectWidget<ClassDefinition>(object)));
				}
			}
			if (classDef.getFields() != null) {
				for (ClassField object : classDef.getFields()) {
					this.addItem(new ClassTreeItem<ClassField>(new TreeObjectWidget<ClassField>(object)));
				}
			}
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if (!selected) {
			this.objectWidget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			this.objectWidget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		super.setSelected(selected);
	}

	public T getItem() {
		return objectWidget.getItem();
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void refreshLabel() {
		objectWidget.refreshLabel();

		TreeItem parent = getParentItem();
		if (parent != null && parent instanceof ClassTreeItem<?>) {
			((ClassTreeItem<?>) parent).refreshLabel();
		}
	}
}
