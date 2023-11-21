package bpm.fd.web.client.tree;

import bpm.fd.core.Dashboard;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.user.client.ui.TreeItem;

public class DashboardTreeItem<T> extends TreeItem {

	private TreeObjectWidget<T> objectWidget;

	public DashboardTreeItem(TreeObjectWidget<T> objectWidget) {
		super(objectWidget);
		this.objectWidget = objectWidget;

		T item = objectWidget.getItem();
		if (item instanceof Dashboard) {
//			Dashboard model = (Dashboard) item;
//			if (model.getDatasource() != null) {
//				this.addItem(new DashboardTreeItem<Datasource>(new TreeObjectWidget<Datasource>(model.getDatasource())));
//			}
//			if (model.getModels() != null) {
//				for (MetadataModel object : model.getModels()) {
//					this.addItem(new DashboardTreeItem<MetadataModel>(new TreeObjectWidget<MetadataModel>(object)));
//				}
//			}

			setState(true);
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
}
