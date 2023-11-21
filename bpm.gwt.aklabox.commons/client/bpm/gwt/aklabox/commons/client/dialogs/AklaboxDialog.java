package bpm.gwt.aklabox.commons.client.dialogs;

import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.tree.AklaboxTreeOld;
import bpm.gwt.aklabox.commons.client.tree.CustomTreeItem;
import bpm.gwt.aklabox.commons.client.tree.DirectoryTreeItem;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;

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

/**
 * Please use {@link AklaboxSelectionDialog}
 * 
 */
@Deprecated
public class AklaboxDialog extends AbstractDialogBox {

	private static AklaboxDialogUiBinder uiBinder = GWT.create(AklaboxDialogUiBinder.class);

	interface AklaboxDialogUiBinder extends UiBinder<Widget, AklaboxDialog> {
	}

	@UiField
	SimplePanel mainPanel;

	private AklaboxTreeOld tree;
	private IObject selectedItem;

	private boolean confirm;

	public AklaboxDialog(AklaboxConnection server, String login, ItemTreeType type, Integer selectedItem) {
		super("", true, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		tree = new AklaboxTreeOld(server, login, type, selectedItem, selectionHandler);
		mainPanel.setWidget(tree);
	}

	private SelectionHandler<TreeItem> selectionHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			CustomTreeItem item = (CustomTreeItem) event.getSelectedItem();
			if (item instanceof DirectoryTreeItem) {
				selectedItem = ((DirectoryTreeItem) item).getDirectory();
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
