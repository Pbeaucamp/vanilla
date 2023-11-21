package bpm.gwt.commons.client.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.tree.RepositoryTree;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class RepositorySaveDialog<T extends Serializable> extends AbstractDialogBox implements SelectionHandler<TreeItem> {

	private static RepositorySaveDialogUiBinder uiBinder = GWT.create(RepositorySaveDialogUiBinder.class);

	interface RepositorySaveDialogUiBinder extends UiBinder<Widget, RepositorySaveDialog<?>> {
	}
	
	interface MyStyle extends CssResource {
		String disabled();
		String btnHover();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbar;

	@UiField
	LabelTextBox txtName;

	@UiField
	LabelTextArea txtDescription;

	@UiField(provided = true)
	RepositoryTree tree;

	@UiField
	SimplePanel panelGroups;

	@UiField
	Image btnAddFolder, btnDeleteFolder;

	private Button btnConfirm;

	private SaveHandler saveHandler;
	private T item;
	private int itemType;

	private List<Group> availableGroups;
	private MultiSelectionModel<Group> multiSelectionModel;

	private RepositoryDirectory selectedDirectory;

	private boolean close;
	
	public RepositorySaveDialog(SaveHandler saveHandler, T item, int itemType, String name, String description, List<Group> availableGroups, boolean close) {
		super(LabelsConstants.lblCnst.SaveItem(), false, true);
		tree = new RepositoryTree(this);
		this.saveHandler = saveHandler;
		this.item = item;
		this.itemType = itemType;
		this.availableGroups = availableGroups;
		this.close = close;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		this.btnConfirm = createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);
		
		toolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);

		txtName.setText(name != null ? name : "");
		txtDescription.setText(description != null ? description : "");

		multiSelectionModel = new MultiSelectionModel<Group>();
		panelGroups.setWidget(new CustomDatagrid<Group>(availableGroups, multiSelectionModel, 150, LabelsConstants.lblCnst.NoGroupAvailable()));

		updateUi();
		refreshTree();
	}

	@UiHandler("btnAddFolder")
	public void addFolder(ClickEvent event) {
		RepositoryDirectory selectedDirectory = tree.getSelectedDirectory();
		if (selectedDirectory != null) {
			AddDirectoryDialog dial = new AddDirectoryDialog(this, selectedDirectory);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshTree();
				}
			});
		}
	}

	@UiHandler("btnDeleteFolder")
	public void deleteFolder(ClickEvent event) {
		final RepositoryDirectory selectedDirectory = tree.getSelectedDirectory();
		if (selectedDirectory != null) {
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Warning(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmDeleteDirectory(), true);
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						CommonService.Connect.getInstance().deleteDirectory(selectedDirectory, new GwtCallbackWrapper<Void>(RepositorySaveDialog.this, true) {

							@Override
							public void onSuccess(Void result) {
								refreshTree();
							}
						}.getAsyncCallback());
					}
				}
			});
		}
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		this.selectedDirectory = tree.getSelectedDirectory();
		updateUi();
	}

	private void updateUi() {
		if (selectedDirectory != null) {
			btnAddFolder.removeStyleName(style.disabled());
			btnDeleteFolder.removeStyleName(style.disabled());
			btnAddFolder.addStyleName(style.btnHover());
			btnDeleteFolder.addStyleName(style.btnHover());
		}
		else {
			btnAddFolder.addStyleName(style.disabled());
			btnDeleteFolder.addStyleName(style.disabled());
			btnAddFolder.removeStyleName(style.btnHover());
			btnDeleteFolder.removeStyleName(style.btnHover());
		}
		btnConfirm.setEnabled(selectedDirectory != null);
	}

	private void refreshTree() {
		tree.loadTree(itemType);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String description = txtDescription.getText();

			if (name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedName());
				return;
			}

			RepositoryDirectory selectedDirectory = tree.getSelectedDirectory();

			List<Group> selectedGroups = new ArrayList<Group>();
			if (availableGroups != null) {
				for (Group group : availableGroups) {
					if (multiSelectionModel.isSelected(group)) {
						selectedGroups.add(group);
					}
				}
			}

			if (selectedGroups.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedSelectGroupForItem());
				return;
			}

			SaveItemInformations itemInfos = new SaveItemInformations(name, description, selectedGroups, itemType, -1, selectedDirectory, item);
			saveHandler.saveItem(itemInfos, close);

			RepositorySaveDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			RepositorySaveDialog.this.hide();
		}
	};

	public interface SaveHandler {

		public void saveItem(SaveItemInformations itemInfos, boolean close);
	}
}
