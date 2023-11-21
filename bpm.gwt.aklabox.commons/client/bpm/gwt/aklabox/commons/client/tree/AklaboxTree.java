package bpm.gwt.aklabox.commons.client.tree;

import java.util.List;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.IObject.ItemType;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.tree.TreeObjectWidget.IDragListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxTree extends CompositeWaitPanel implements OpenHandler<TreeItem>, IDragListener {

	private static AklaboxTreeUiBinder uiBinder = GWT.create(AklaboxTreeUiBinder.class);

	interface AklaboxTreeUiBinder extends UiBinder<Widget, AklaboxTree> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel;

	@UiField
	com.google.gwt.user.client.ui.Tree tree;
	
	private IActionManager actionManager;
	private DocumentUtils docUtils;
	
	private Object dragItem;
	
	private User user;
	private ItemTreeType selectedTypeTree;
	private IObject selectedItem;
	
	private boolean displayDocument;
	private SelectionHandler<TreeItem> selectionHandler;

	public AklaboxTree(IActionManager actionManager, User user, DocumentUtils docUtils, final ItemTreeType selectedTypeTree, IObject selectedItem, boolean displayDocument, SelectionHandler<TreeItem> selectionHandler) {
		initWidget(uiBinder.createAndBindUi(this));
		this.actionManager = actionManager;
		this.user = user;
		this.docUtils = docUtils;
		this.selectedItem = selectedItem;
		this.displayDocument = displayDocument;
		this.selectionHandler = selectionHandler;
		tree.addOpenHandler(this);
		if (selectionHandler != null) {
			tree.addSelectionHandler(selectionHandler);
		}
		
		this.addStyleName(style.mainPanel());

		if (selectedItem != null) {
			IObject.ItemType type = selectedItem instanceof Documents ? ItemType.DOCUMENT : ItemType.FOLDER;
			AklaCommonService.Connect.getService().getItem(selectedItem.getId(), type, selectedTypeTree, false, false, true, true, new GwtCallbackWrapper<IObject>(this, true, true) {
	
				@Override
				public void onSuccess(IObject result) {
					AklaboxTree.this.selectedItem = result;
					changeTreeType(selectedTypeTree);
				}
			}.getAsyncCallback());
		}
		else {
			changeTreeType(selectedTypeTree);
		}
	}

	public void refresh() {
		this.selectedItem = null;
		loadTree(selectedTypeTree);
	}
	
	public void changeTreeType(ItemTreeType selectedTypeTree) {
		this.selectedTypeTree = selectedTypeTree;
		loadTree(selectedTypeTree);
	}

	public void loadTree(ItemTreeType selectedTypeTree) {
		switch (selectedTypeTree) {
		case ENTERPRISE:
			loadEnterprises();
			break;
		case MY_DOCUMENTS:
		case SHARED_WITH_ME:
		case RECYCLE:
			loadFolders();
			break;
		default:
			break;
		}
	}

	private void loadEnterprises() {
		AklaCommonService.Connect.getService().getEnterprisePerUser(user.getEmail(), new GwtCallbackWrapper<List<Enterprise>>(this, true, true) {

			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				tree.clear();
				
				if (enterprises != null) {
					for (Enterprise enterprise : enterprises) {
						DocumentTreeItem<Enterprise> item = new DocumentTreeItem<Enterprise>(actionManager, null, AklaboxTree.this, new TreeObjectWidget<Enterprise>(null, AklaboxTree.this, enterprise, selectedTypeTree, docUtils, true, null), selectedTypeTree, docUtils, displayDocument, true);
						//item.addStyleName(style.blockItem());
						tree.addItem(item);
						
						if (checkEnterpriseParent(enterprise.getEnterpriseId(), selectedItem)) {
							loadItem(item);
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	private boolean checkEnterpriseParent(int enterpriseId, IObject selectedItem) {
		if (selectedItem == null) {
			return false;
		}
		
		Enterprise enterprise = selectedItem.getEnterpriseParent();
		return enterprise != null && enterprise.getEnterpriseId() == enterpriseId;
	}

	private boolean checkParent(int folderId, IObject selectedItem) {
		if (selectedItem == null) {
			return false;
		}
		
		Tree parent = selectedItem.getParent();
		if (parent != null && parent.getId() == folderId) {
			return true;
		}
		else if (parent != null) {
			return checkParent(folderId, parent);
		}
		return false;
	}

	private void loadFolders() {
		AklaCommonService.Connect.getService().getItems(null, selectedTypeTree, new GwtCallbackWrapper<List<IObject>>(this, true, true) {

			@Override
			public void onSuccess(List<IObject> folders) {
				tree.clear();

				if (folders != null) {
					if (selectedTypeTree != ItemTreeType.RECYCLE) {
						for (IObject object : folders) {
							if (object instanceof Tree) {
								DocumentTreeItem<Tree> item = new DocumentTreeItem<Tree>(actionManager, null, AklaboxTree.this, new TreeObjectWidget<Tree>(null, AklaboxTree.this, (Tree) object, selectedTypeTree, docUtils, false, null), selectedTypeTree, docUtils, displayDocument, false);
								tree.addItem(item);
								
								if (checkParent(object.getId(), selectedItem)) {
									loadItem(item);
								}
							}
							else if (displayDocument) {
								DocumentTreeItem<Documents> item = new DocumentTreeItem<Documents>(actionManager, null, AklaboxTree.this, new TreeObjectWidget<Documents>(null, AklaboxTree.this, (Documents) object, selectedTypeTree, docUtils, false, null), selectedTypeTree, docUtils, displayDocument, false);
								tree.addItem(item);
							}
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	public void loadFolder(final DocumentTreeItem<Tree> treeItem, final Tree folder) {
		AklaCommonService.Connect.getService().getItems(folder, selectedTypeTree, new GwtCallbackWrapper<List<IObject>>(this, true, true) {

			@Override
			public void onSuccess(List<IObject> result) {
				folder.setChildren(result);

				treeItem.buildItem(new TreeObjectWidget<Tree>(null, AklaboxTree.this, folder, selectedTypeTree, docUtils, false, null), true);
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("tree")
	public void onTreeSelection(SelectionEvent<TreeItem> e) {
		TreeItem treeItem = e.getSelectedItem();
		loadItem(treeItem);
		if(selectionHandler != null){
			selectionHandler.onSelection(e);
		}
	}

	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem treeItem = event.getTarget();
		loadItem(treeItem);
	}

	@SuppressWarnings("unchecked")
	private void loadItem(TreeItem treeItem) {
		if (treeItem != null && treeItem instanceof DocumentTreeItem<?>) {
			Object item = ((DocumentTreeItem<?>) treeItem).getItem();
			if (item instanceof Tree) {
				DocumentTreeItem<Tree> itemFolder = (DocumentTreeItem<Tree>) treeItem;
				if ((itemFolder != null && !itemFolder.isLoaded()) || ((Tree) item).getChildren() == null) {
					loadFolder(itemFolder, (Tree) item);
				}
			}
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

	public int getItemCount() {
		return tree.getItemCount();
	}
}
