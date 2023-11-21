package bpm.gwt.aklabox.commons.client.tree;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.gwt.aklabox.commons.client.handlers.DirectoryContextMenuHandler;
import bpm.gwt.aklabox.commons.client.tree.TreeObjectWidget.IDragListener;
import bpm.gwt.aklabox.commons.client.tree.TreeObjectWidget.IDropManager;

import com.google.gwt.user.client.ui.TreeItem;

public class DocumentTreeItem<T> extends TreeItem {

	private IActionManager actionManager;
	private IDropManager dropManager;
	private IDragListener dragListener;
	private TreeObjectWidget<T> objectWidget;
	private DocumentUtils docUtils;

	private boolean loaded = false;
	private ItemTreeType type;
	private boolean displayDocument = false;
	private boolean displayCustomLogo = false;

	public DocumentTreeItem(IActionManager actionManager, IDropManager dropManager, IDragListener dragListener, TreeObjectWidget<T> objectWidget, ItemTreeType type, DocumentUtils docUtils, boolean displayDocument, boolean displayCustomLogo) {
		super(objectWidget);
		this.actionManager = actionManager;
		this.dropManager = dropManager;
		this.dragListener = dragListener;
		this.type = type;
		this.docUtils = docUtils;
		this.displayDocument = displayDocument;
		this.displayCustomLogo = displayCustomLogo;

		buildItem(objectWidget, false);
	}

	public void buildItem(TreeObjectWidget<T> objectWidget, boolean loaded) {
		this.objectWidget = objectWidget;

		removeItems();

		T item = objectWidget.getItem();
		if (item instanceof Tree) {
			Tree dir = (Tree) item;
			if (dir.getChildren() != null && !dir.getChildren().isEmpty()) {
				this.loaded = true;
				for (IObject object : dir.getChildren()) {
					if (object instanceof Tree) {
						this.addItem(new DocumentTreeItem<Tree>(actionManager, dropManager, dragListener, new TreeObjectWidget<Tree>(dropManager, dragListener, (Tree) object, type, docUtils, displayCustomLogo, null), type, docUtils, displayDocument, displayCustomLogo));
					}
					else if (displayDocument) {
						this.addItem(new DocumentTreeItem<Documents>(actionManager, dropManager, dragListener, new TreeObjectWidget<Documents>(dropManager, dragListener, (Documents) object, type, docUtils, displayCustomLogo, null), type, docUtils, displayDocument, displayCustomLogo));
					}
				}
			}
			else {
				this.loaded = loaded;
			}
			
			if (this.loaded) {
				setState(true);
			}
			else {
				this.addItem(new WaitTreeItem());
			}
			
			if (actionManager != null) {
				objectWidget.addContextMenuHandler(new DirectoryContextMenuHandler(actionManager, dir));
			}
		}
		else if (item instanceof Enterprise) {
			Enterprise enterprise = (Enterprise) item;
			if (enterprise.getFolderRoot() != null) {
				this.loaded = true;
				this.addItem(new DocumentTreeItem<Tree>(actionManager, dropManager, dragListener, new TreeObjectWidget<Tree>(dropManager, dragListener, enterprise.getFolderRoot(), type, docUtils, displayCustomLogo, null), type, docUtils, displayDocument, displayCustomLogo));
			}

			if (this.loaded) {
				setState(true);
			}
			else {
				this.addItem(new WaitTreeItem());
			}
		}
		else if (item instanceof Group) {
			Group group = (Group) item;
			if (group.getChilds() != null) {
				this.loaded = true;
				for (IObject object : group.getChilds()) {
					if (object instanceof Tree) {
						this.addItem(new DocumentTreeItem<Tree>(actionManager, dropManager, dragListener, new TreeObjectWidget<Tree>(dropManager, dragListener, (Tree) object, type, docUtils, displayCustomLogo, null), type, docUtils, displayDocument, displayCustomLogo));
					}
					else if (displayDocument) {
						this.addItem(new DocumentTreeItem<Documents>(actionManager, dropManager, dragListener, new TreeObjectWidget<Documents>(dropManager, dragListener, (Documents) object, type, docUtils, displayCustomLogo, null), type, docUtils, displayDocument, displayCustomLogo));
					}
				}
			}

			if (loaded) {
				setState(true);
			}
			else {
				this.addItem(new WaitTreeItem());
			}
		}
	}

	public Enterprise getEnterpriseParent() {
		DocumentTreeItem<?> parent = getParent();
		if (parent != null && parent.getItem() instanceof Enterprise) {
			return (Enterprise) parent.getItem();
		}
		else if (parent != null) {
			return parent.getEnterpriseParent();
		}
		return null;
	}

	public Tree getParentFolder() {
		DocumentTreeItem<?> parent = getParent();
		if (parent != null && parent.getItem() instanceof Tree) {
			return (Tree) parent.getItem();
		}
		return null;
	}

	public DocumentTreeItem<?> getParent() {
		TreeItem itemParent = getParentItem();
		if (itemParent instanceof DocumentTreeItem<?>) {
			return (DocumentTreeItem<?>) itemParent;
		}
		return null;
	}

	public void refresh() {
		buildItem(objectWidget, isLoaded());
	}

	@Override
	public void setSelected(boolean selected) {
//		if (!selected) {
//			this.objectWidget.removeStyleName(ThemeCSS.LEVEL_2);
//		}
//		else {
//			this.objectWidget.addStyleName(ThemeCSS.LEVEL_2);
//		}
		super.setSelected(selected);
	}

	public T getItem() {
		return objectWidget.getItem();
	}

	public boolean isLoaded() {
		return loaded;
	}
}
