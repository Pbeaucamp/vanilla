package bpm.gwt.commons.client.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class SaveItemDialog extends AbstractDialogBox {

	private static ExportToRepositoryDialogUiBinder uiBinder = GWT.create(ExportToRepositoryDialogUiBinder.class);

	interface ExportToRepositoryDialogUiBinder extends UiBinder<Widget, SaveItemDialog> {
	}

	@UiField
	TextHolderBox txtName;

	@UiField
	TextAreaHolderBox txtDescription;

	@UiField
	SimplePanel panelGroups;

	private List<Group> availableGroups;
	private MultiSelectionModel<Group> multiSelectionModel;

	private int repositoryType;
	private int subRepositoryType;
	private RepositoryDirectory selectedDirectory;
	private Serializable itemToSave;

	private boolean isConfirm = false;

	/* kmo 03/2016 connexion distance */
	private String url;
	private String login;
	private String pass;
	private Group group;
	private Repository repo;

	private IRepositoryManager manager;

	public SaveItemDialog(int repositoryType, int subRepositoryType, RepositoryDirectory selectedDirectory, List<Group> availableGroups, Serializable itemToSave) {
		super(LabelsConstants.lblCnst.ExportToRepository(), false, true);
		this.repositoryType = repositoryType;
		this.selectedDirectory = selectedDirectory;
		this.subRepositoryType = subRepositoryType;
		this.availableGroups = availableGroups;
		this.itemToSave = itemToSave;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		multiSelectionModel = new MultiSelectionModel<Group>();
		panelGroups.setWidget(new CustomDatagrid<Group>(availableGroups, multiSelectionModel, 150, LabelsConstants.lblCnst.NoGroupAvailable()));
	}

	public SaveItemDialog(String url, String login, String pass, Group group, Repository repo, int repositoryType, int subRepositoryType, RepositoryDirectory selectedDirectory, List<Group> availableGroups, Serializable itemToSave) {
		this(repositoryType, subRepositoryType, selectedDirectory, availableGroups, itemToSave);
		this.url = url;
		this.login = login;
		this.pass = pass;
		this.group = group;
		this.repo = repo;
	}

	public SaveItemDialog(IRepositoryManager manager, int repositoryType, int subRepositoryType, RepositoryDirectory selectedDirectory, List<Group> availableGroups, Serializable itemToSave) {
		this(repositoryType, subRepositoryType, selectedDirectory, availableGroups, itemToSave);
		this.manager = manager;
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = txtName.getText();
			String description = txtDescription.getText();

			if (name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NeedName());
				return;
			}

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

			showWaitPart(true);

			SaveItemInformations itemInfo = new SaveItemInformations(name, description, selectedGroups, repositoryType, subRepositoryType, selectedDirectory, itemToSave);

			if (url == null) {
				CommonService.Connect.getInstance().saveItem(itemInfo, new GwtCallbackWrapper<RepositoryItem>(SaveItemDialog.this, true) {

					@Override
					public void onSuccess(RepositoryItem result) {
						isConfirm = true;

						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ItemSavedSuccessfully());
						hide();

						if (manager != null) {
							manager.savedItem(result);
						}
					}
				}.getAsyncCallback());
			}
			else {
				CommonService.Connect.getInstance().saveItem(url, login, pass, group, repo, itemInfo, new GwtCallbackWrapper<RepositoryItem>(SaveItemDialog.this, true) {

					@Override
					public void onSuccess(RepositoryItem result) {
						isConfirm = true;

						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ItemSavedSuccessfully());
						hide();

						if (manager != null) {
							manager.savedItem(result);
						}
					}
				}.getAsyncCallback());
			}

		}
	};

	public boolean isConfirm() {
		return isConfirm;
	}

	public interface IRepositoryManager {

		public void savedItem(RepositoryItem item);
	}
}
