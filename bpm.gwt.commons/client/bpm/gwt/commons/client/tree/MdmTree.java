package bpm.gwt.commons.client.tree;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AddMdmDirectoryDialog;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.mdm.model.supplier.MdmDirectory;

public class MdmTree extends Composite implements IWait, SelectionHandler<TreeItem>, OpenHandler<TreeItem>, IDragListener {

	private static MdmTreeUiBinder uiBinder = GWT.create(MdmTreeUiBinder.class);

	interface MdmTreeUiBinder extends UiBinder<Widget, MdmTree> {
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Tree tree;

	@UiField
	Image btnAddFolder, btnEditFolder, btnDeleteFolder;

	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;

	private IMdmManager parent;

	private Object dragItem;

	public MdmTree(IMdmManager parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		tree.addSelectionHandler(this);
		tree.addOpenHandler(this);

		loadItems();
		manageUi();
	}

	public void loadItems() {
		CommonService.Connect.getInstance().getMdmDirectories(new GwtCallbackWrapper<List<MdmDirectory>>(this, false) {

			@Override
			public void onSuccess(List<MdmDirectory> result) {
				buildItems(result);
			}
		}.getAsyncCallback());
	}

	private void buildItems(List<MdmDirectory> items) {
		tree.clear();

		if (items != null) {
			for (MdmDirectory dir : items) {
				MdmTreeItem<MdmDirectory> item = new MdmTreeItem<MdmDirectory>(this, new TreeObjectWidget<MdmDirectory>(this, dir), false);
				tree.addItem(item);
			}
		}
	}

	private void buildItem(MdmTreeItem<MdmDirectory> item, MdmDirectory model) {
		if (item != null) {
			item.buildItem(new TreeObjectWidget<MdmDirectory>(this, model));
		}
		else {
			tree.clear();

			if (model != null) {
				item = new MdmTreeItem<MdmDirectory>(this, new TreeObjectWidget<MdmDirectory>(this, model), true);
				tree.addItem(item);

				item.setSelected(true);
				if (parent != null) {
					parent.loadItems((MdmTreeItem<?>) item);
				}
			}
		}
	}

	public Object getSelectedItem() {
		if (tree.getSelectedItem() != null) {
			MdmTreeItem<?> item = (MdmTreeItem<?>) tree.getSelectedItem();
			return item.getItem();
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem treeItem = event.getTarget();
		if (treeItem != null && treeItem instanceof MdmTreeItem<?>) {
			Object item = ((MdmTreeItem<?>) treeItem).getItem();
			if (item instanceof MdmDirectory) {
				MdmTreeItem<MdmDirectory> itemMdmDirectory = (MdmTreeItem<MdmDirectory>) treeItem;
				if (!itemMdmDirectory.isLoaded()) {
					buildItem(itemMdmDirectory, (MdmDirectory) item);
				}
			}
		}
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		if (item != null && item instanceof MdmTreeItem<?>) {
			manageUi();

			if (parent != null) {
				parent.loadItems((MdmTreeItem<?>) item);
			}
		}
	}

	private void manageUi() {
		boolean isFolderSelected = getSelectedItem() != null && getSelectedItem() instanceof MdmDirectory;
		btnEditFolder.setVisible(isFolderSelected);
		btnDeleteFolder.setVisible(isFolderSelected);
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

	@UiHandler("btnAddFolder")
	public void addFolder(ClickEvent event) {
		MdmDirectory selectedDirectory = (MdmDirectory) getSelectedItem();
		manageFolder(selectedDirectory != null ? selectedDirectory.getId() : null, null);
	}

	@UiHandler("btnEditFolder")
	public void addEditFolder(ClickEvent event) {
		MdmDirectory selectedDirectory = (MdmDirectory) getSelectedItem();
		if (selectedDirectory != null) {
			manageFolder(null, selectedDirectory);
		}
	}

	private void manageFolder(Integer parentId, MdmDirectory directory) {
		AddMdmDirectoryDialog dial = new AddMdmDirectoryDialog(this, parentId, directory);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				loadItems();
			}
		});
	}

	@UiHandler("btnDeleteFolder")
	public void deleteFolder(ClickEvent event) {
		final MdmDirectory selectedDirectory = (MdmDirectory) getSelectedItem();
		if (selectedDirectory != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeleteDirectory(), true);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						CommonService.Connect.getInstance().deleteMdmDirectory(selectedDirectory, new GwtCallbackWrapper<Void>(MdmTree.this, true, true) {

							@Override
							public void onSuccess(Void result) {
								loadItems();
							}
						}.getAsyncCallback());
					}
				}
			});
		}
	}
	
	public void clearSelection() {
		tree.setSelectedItem(null);
	}

	public interface IMdmManager {

		public void loadItems(MdmTreeItem<?> treeItem);
	}
}
