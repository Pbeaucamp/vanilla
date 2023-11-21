package bpm.gwt.commons.client.tree;

import java.util.List;

import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.shared.cmis.CmisFolder;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.cmis.CmisItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CmisTree extends Composite implements IWait, OpenHandler<TreeItem>, IDragListener {

	private static CmisTreeUiBinder uiBinder = GWT.create(CmisTreeUiBinder.class);

	interface CmisTreeUiBinder extends UiBinder<Widget, CmisTree> {
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Tree tree;

	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	
	private Object dragItem;
	
	private CmisInformations cmisInfos;

	public CmisTree(CmisInformations cmisInfos) {
		initWidget(uiBinder.createAndBindUi(this));
		this.cmisInfos = cmisInfos;

		tree.addOpenHandler(this);

		loadFolders();
	}

	private void loadFolders() {
		ReportingService.Connect.getInstance().getCmisFolders(cmisInfos, null, new GwtCallbackWrapper<List<CmisItem>>(this, false) {

			@Override
			public void onSuccess(List<CmisItem> result) {
				buildFolders(result);
			}
		}.getAsyncCallback());
	}

	private void loadFolder(final CmisTreeItem<CmisItem> treeItem, final CmisFolder folder) {
		showWaitPart(true);

		ReportingService.Connect.getInstance().getCmisFolders(cmisInfos, folder.getItemId(), new GwtCallbackWrapper<List<CmisItem>>(this, true) {

			@Override
			public void onSuccess(List<CmisItem> result) {
				folder.setChilds(result);
				treeItem.rebuildItem(folder);
			}
		}.getAsyncCallback());
	}

	public void refresh() {
		loadFolders();
	}

	public void buildFolders(List<CmisItem> items) {
		tree.clear();

		if (items != null) {
			for (CmisItem cmisItem : items) {
				CmisTreeItem<CmisItem> item = new CmisTreeItem<CmisItem>(this, new TreeObjectWidget<CmisItem>(this, cmisItem), true);
				tree.addItem(item);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public CmisItem getSelectedItem() {
		if (tree.getSelectedItem() != null) {
			CmisTreeItem<CmisItem> item = (CmisTreeItem<CmisItem>) tree.getSelectedItem();
			return item.getItem();
		}

		return null;
	}
	
	public void addSelectionHandler(SelectionHandler<TreeItem> handler) {
		tree.addSelectionHandler(handler);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem treeItem = event.getTarget();
		if (treeItem != null && treeItem instanceof CmisTreeItem<?>) {
			Object item = ((CmisTreeItem<?>) treeItem).getItem();
			if (item instanceof CmisFolder) {
				CmisTreeItem<CmisItem> itemCmis = (CmisTreeItem<CmisItem>) treeItem;
				if (!itemCmis.isLoaded()) {
					loadFolder(itemCmis, (CmisFolder) item);
				}
			}
		}
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible && !isCharging) {
			isCharging = true;

			greyPanel = new GreyAbsolutePanel();
			waitPanel = new WaitAbsolutePanel();

			mainPanel.add(greyPanel);
			mainPanel.add(waitPanel);

			int width = mainPanel.getOffsetWidth();

			waitPanel.getElement().getStyle().setProperty("top", 50 + "px");
			if (width > 0) {
				waitPanel.getElement().getStyle().setProperty("left", ((width / 2) - 100) + "px");
			}
			else {
				waitPanel.getElement().getStyle().setProperty("left", 120 + "px");
			}
		}
		else if (!visible && isCharging) {
			isCharging = false;

			mainPanel.remove(greyPanel);
			mainPanel.remove(waitPanel);
		}
	}
	
	@Override
	public Object getDragItem() {
		return dragItem;
	}
	
	@Override
	public void setDragItem(Object dragItem) {
		this.dragItem = dragItem;
	}
	
	@Override
	public void removeDragItem() {
		this.dragItem = null;
	}
}
