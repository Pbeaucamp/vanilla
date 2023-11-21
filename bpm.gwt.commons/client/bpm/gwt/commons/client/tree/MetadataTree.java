package bpm.gwt.commons.client.tree;

import java.util.List;

import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MetadataTree extends Composite implements IWait, SelectionHandler<TreeItem>, OpenHandler<TreeItem>, IDragListener {

	private static MetadataTreeUiBinder uiBinder = GWT.create(MetadataTreeUiBinder.class);

	interface MetadataTreeUiBinder extends UiBinder<Widget, MetadataTree> {
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Tree tree;

	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;

	private IPropertiesListener parent;
	
	private Object dragItem;

	public MetadataTree(IPropertiesListener parent, boolean singleMetadata) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		tree.addSelectionHandler(this);
		tree.addOpenHandler(this);

		if (!singleMetadata) {
			loadMetadatas();
		}
	}

	private void loadMetadatas() {
		FmdtServices.Connect.getInstance().getMetadatas(new GwtCallbackWrapper<List<Metadata>>(this, false) {

			@Override
			public void onSuccess(List<Metadata> result) {
				buildMetadatas(result);
			}
		}.getAsyncCallback());
	}

	private void loadMetadata(final MetadataTreeItem<Metadata> treeItem, Metadata metadata) {
		showWaitPart(true);

		RepositoryItem item = new RepositoryItem();
		item.setId(metadata.getItemId());
		item.setType(IRepositoryApi.FMDT_TYPE);

		FmdtServices.Connect.getInstance().openMetadata(item, new GwtCallbackWrapper<Metadata>(this, true) {

			@Override
			public void onSuccess(Metadata result) {
				buildItem(treeItem, result, false);
			}
		}.getAsyncCallback());
	}

	public void refresh(Metadata model) {
		buildItem(null, model, false);
	}

	public void buildMetadatas(List<Metadata> metadatas) {
		tree.clear();

		if (metadatas != null) {
			for (Metadata metadata : metadatas) {
				MetadataTreeItem<Metadata> item = new MetadataTreeItem<Metadata>(this, new TreeObjectWidget<Metadata>(this, metadata), false);
				tree.addItem(item);
			}
		}
	}

	public void buildItem(MetadataTreeItem<Metadata> item, Metadata model, boolean displayMainProperties) {
		if (item != null) {
			item.buildItem(new TreeObjectWidget<Metadata>(this, model));
		}
		else {
			tree.clear();

			if (model != null) {
				item = new MetadataTreeItem<Metadata>(this, new TreeObjectWidget<Metadata>(this, model), true);
				tree.addItem(item);

				if (displayMainProperties) {
					item.setSelected(true);
					if (parent != null) {
						parent.loadProperties((MetadataTreeItem<?>) item);
					}
				}
			}
		}
	}

	public Object getSelectedItem() {
		if (tree.getSelectedItem() != null) {
			MetadataTreeItem<?> item = (MetadataTreeItem<?>) tree.getSelectedItem();
			return item.getItem();
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem treeItem = event.getTarget();
		if (treeItem != null && treeItem instanceof MetadataTreeItem<?>) {
			Object item = ((MetadataTreeItem<?>) treeItem).getItem();
			if (item instanceof Metadata) {
				MetadataTreeItem<Metadata> itemMetadata = (MetadataTreeItem<Metadata>) treeItem;
				if (!itemMetadata.isLoaded()) {
					loadMetadata(itemMetadata, (Metadata) item);
				}
			}
		}
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		if (item != null && item instanceof MetadataTreeItem<?>) {
			if (parent != null) {
				parent.loadProperties((MetadataTreeItem<?>) item);
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

	public interface IPropertiesListener {

		public void loadProperties(MetadataTreeItem<?> treeItem);
	}
}
