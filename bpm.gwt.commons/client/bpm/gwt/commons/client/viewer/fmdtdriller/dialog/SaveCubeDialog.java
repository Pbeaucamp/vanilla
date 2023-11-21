package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.tree.RepositoryTree;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class SaveCubeDialog extends AbstractDialogBox {

	private static SaveCubeDialogUiBinder uiBinder = GWT.create(SaveCubeDialogUiBinder.class);

	interface SaveCubeDialogUiBinder extends UiBinder<Widget, SaveCubeDialog> {
	}

	interface MyStyle extends CssResource {
		String tree();
	}

	@UiField
	MyStyle style;

	@UiField
	TextBox txtName, txtDesc;

	@UiField
	ListBox lstGroups;

	@UiField
	SimplePanel treePanel;

	private IWait waitPanel;
	private List<Group> groups;
	private boolean update;
	private String model;
	private int itemId = 0;
	private Boolean confirm = false;
	private String name;

	private RepositoryTree repositoryTree;
	private Button confirmButton;

	public SaveCubeDialog(IWait waitPanel, List<Group> availableGroups, String model, boolean update) {
		super(LabelsConstants.lblCnst.SaveInformations(), false, true);
		this.waitPanel = waitPanel;
		this.groups = availableGroups;
		this.update = update;
		this.model = model;

		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		confirmButton = createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);
		confirmButton.setEnabled(false);
		if (update) {
			txtName.setText(LabelsConstants.lblCnst.CantModify());
			txtName.setEnabled(false);

			lstGroups.addItem(LabelsConstants.lblCnst.CantModify());
			lstGroups.setEnabled(false);
		} else {
			if (groups != null) {
				for (Group gr : groups) {
					lstGroups.addItem(gr.getName(), String.valueOf(gr.getId()));
				}
			}
		}
		repositoryTree = new RepositoryTree(repositoryHandler);
		repositoryTree.addStyleName(style.tree());
		treePanel.add(repositoryTree);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				loadTree();
			}
		});
		txtName.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (repositoryTree.getSelectedDirectory() != null && txtName.getText() != null && !txtName.getText().isEmpty()) {
					confirmButton.setEnabled(true);
				} else {
					confirmButton.setEnabled(false);
				}
			}
		});
	}

	public void loadTree() {
		repositoryTree.loadTree(IRepositoryApi.FASD_TYPE);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			int selectedDirectoryId;
			if (repositoryTree.getSelectedDirectory() == null) {
				return;
			} else
				selectedDirectoryId = repositoryTree.getSelectedDirectory().getId();
			Group selectedGroup = null;
			try {
				if (!update) {
					int selectedGroupId = Integer.parseInt(lstGroups.getValue(lstGroups.getSelectedIndex()));
					if (groups != null) {
						for (Group gr : groups) {
							if (gr.getId() == selectedGroupId) {
								selectedGroup = gr;
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "You need to select a valid group.");
			}

			waitPanel.showWaitPart(true);

			FmdtServices.Connect.getInstance().saveCube(selectedDirectoryId, txtName.getText(), txtDesc.getText(), selectedGroup, model, new AsyncCallback<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					waitPanel.showWaitPart(false);
					itemId = result;
					name=txtName.getText();
					confirm = true;
					hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					waitPanel.showWaitPart(false);
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	public int getItemId() {
		return itemId;
	}

	public Boolean getConfirm() {
		return confirm;
	}

	public String getName() {
		return name;
	}

	private SelectionHandler<TreeItem> repositoryHandler = new SelectionHandler<TreeItem>() {

		@Override
		public void onSelection(SelectionEvent<TreeItem> event) {
			if (repositoryTree.getSelectedDirectory() != null && txtName.getText() != null && !txtName.getText().isEmpty()) {
				confirmButton.setEnabled(true);
			} else {
				confirmButton.setEnabled(false);
			}
		}
	};

}
