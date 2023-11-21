package bpm.fd.web.client.tree;

import bpm.fd.core.Dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class DashboardTree extends Composite implements SelectionHandler<TreeItem> {

	private static DashboardTreeUiBinder uiBinder = GWT.create(DashboardTreeUiBinder.class);

	interface DashboardTreeUiBinder extends UiBinder<Widget, DashboardTree> {
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Tree tree;
	
	public DashboardTree() {
		initWidget(uiBinder.createAndBindUi(this));
		
		tree.addSelectionHandler(this);
	}

	public void buildTree(Dashboard dashboard) {
		tree.clear();

		if (dashboard != null) {
			DashboardTreeItem<Dashboard> item = new DashboardTreeItem<Dashboard>(new TreeObjectWidget<Dashboard>(dashboard));
			item.setSelected(true);
			tree.addItem(item);
		}
	}

	public void refresh(Dashboard dashboard) {
		buildTree(dashboard);
	}
	
	public Object getSelectedItem() {
		if (tree.getSelectedItem() != null) {
			DashboardTreeItem<?> item = (DashboardTreeItem<?>) tree.getSelectedItem();
			return item.getItem();
		}
		
		return null;
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		if (item != null && item instanceof DashboardTreeItem<?>) {
//			parent.loadProperties((DashboardTreeItem<?>) item);
		}
	}
}
