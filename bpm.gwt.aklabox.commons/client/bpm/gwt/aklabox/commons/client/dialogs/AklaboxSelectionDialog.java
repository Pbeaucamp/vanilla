package bpm.gwt.aklabox.commons.client.dialogs;

import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.User;
import bpm.document.management.core.utils.DocumentUtils;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.tree.AklaboxTree;
import bpm.gwt.aklabox.commons.client.tree.DocumentTreeItem;
import bpm.gwt.aklabox.commons.client.tree.IActionManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxSelectionDialog extends AbstractDialogBox {

	private static AklaboxSelectionDialogUiBinder uiBinder = GWT.create(AklaboxSelectionDialogUiBinder.class);

	interface AklaboxSelectionDialogUiBinder extends UiBinder<Widget, AklaboxSelectionDialog> {
	}

	@UiField
	SimplePanel mainPanel;

	private AklaboxTree tree;
	private IObject selectedItem;

	private boolean confirm;

	public AklaboxSelectionDialog(IActionManager actionManager, User user, DocumentUtils docUtils, ItemTreeType type, IObject selectedItem, boolean displayDocument) {
		super("", true, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		tree = new AklaboxTree(actionManager, user, docUtils, type, selectedItem, displayDocument, selectionHandler);
		mainPanel.setWidget(tree);
	}

	private SelectionHandler<TreeItem> selectionHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			DocumentTreeItem<?> treeItem = (DocumentTreeItem<?>) event.getSelectedItem();
			Object item = ((DocumentTreeItem<?>) treeItem).getItem();
			if (item instanceof IObject) {
				selectedItem = (IObject) item;
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}

	public IObject getSelectedItem() {
		return selectedItem;
	}

	@Override
	public int getThemeColor() {
		return 5;
	}
}
