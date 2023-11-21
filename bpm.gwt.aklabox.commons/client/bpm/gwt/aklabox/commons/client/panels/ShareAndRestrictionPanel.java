package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.ItemRight;
import bpm.document.management.core.model.Permission;
import bpm.document.management.core.model.Permission.ShareType;
import bpm.document.management.core.model.PermissionItem;
import bpm.document.management.core.model.PermissionItem.ShareStatus;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomCheckbox;
import bpm.gwt.aklabox.commons.client.customs.LabelDateBox;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;
import bpm.gwt.aklabox.commons.client.customs.ListBoxWithButton;
import bpm.gwt.aklabox.commons.client.dialogs.PermissionItemDialog;
import bpm.gwt.aklabox.commons.client.dialogs.UsersSelectionWithGroupDialog;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.loading.IWait;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.ButtonImageCell;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.ResizableImageCell;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class ShareAndRestrictionPanel extends Composite {

	private static ShareAndRestrictionPanelUiBinder uiBinder = GWT.create(ShareAndRestrictionPanelUiBinder.class);

	interface ShareAndRestrictionPanelUiBinder extends UiBinder<Widget, ShareAndRestrictionPanel> {
	}

	interface MyStyle extends CssResource {
		String grid();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelPublic, panelPeremption, panelEnterprise, panelPublicOptions, panelUsers;

	@UiField
	CustomCheckbox chkSharePeremption, chkPublicPassword;

	@UiField
	LabelDateBox dbSharePeremption;

	@UiField
	CustomCheckbox chkPublic, chkShareUsers;

	@UiField
	LabelTextBox txtPublicLink, txtPublicPassword;

	@UiField
	RadioButton btnAll, btnByUsers;

	@UiField
	ListBoxWithButton<ItemRightWithLabel> lstGlobalRights, lstAllUserRights;

	@UiField
	SimplePanel panelGridUsers, panelDisabled;

	private ListDataProvider<Permission> dataProvider;
	private ListHandler<Permission> sortHandler;
	private DataGrid<Permission> datagridUsers;

	private IWait waitPanel;
	private PermissionItemDialog parent;
	private Enterprise enterprise;

	private List<Permission> permissions;
	private HashMap<ItemRight, ItemRightWithLabel> rights;

	private ShareType shareType;
	private ItemRight globalRight = ItemRight.READ_DOWNLOAD_WRITE;
	private String publicHash;
	
	private UsersSelectionWithGroupDialog dial;

	public ShareAndRestrictionPanel(IWait waitPanel, PermissionItemDialog parent, PermissionItem permissionItem, Enterprise enterprise, ShareType shareType) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.parent = parent;
		this.enterprise = enterprise;
		this.shareType = shareType;

		panelEnterprise.setVisible(shareType == ShareType.DOCUMENTARY_SPACE);
		panelPublic.setVisible(shareType == ShareType.SHARE);
		panelPeremption.setVisible(shareType == ShareType.SHARE);

		this.rights = buildItemRightWithLabels();
		lstGlobalRights.setList(new ArrayList<ItemRightWithLabel>(rights.values()));
		lstAllUserRights.setList(new ArrayList<ItemRightWithLabel>(rights.values()), true);

		this.datagridUsers = buildGridPermissions();
		panelGridUsers.setWidget(datagridUsers);

		loadPermissionItem(permissionItem, shareType);
		updateUi();
	}
	
	@UiHandler("chkSharePeremption")
	public void onSharePeremption(ValueChangeEvent<Boolean> event) {
		updateUi();
	}
	
	@UiHandler("chkPublicPassword")
	public void onPublicPassword(ValueChangeEvent<Boolean> event) {
		updateUi();
	}
	
	@UiHandler("chkShareUsers")
	public void onShareUsers(ValueChangeEvent<Boolean> event) {
		updateUi();
	}
	
	@UiHandler("btnAll")
	public void onAll(ValueChangeEvent<Boolean> event) {
		updateUi();
	}
	
	@UiHandler("btnByUsers")
	public void onByUsers(ValueChangeEvent<Boolean> event) {
		updateUi();
	}
	
	private void updateUi() {
		dbSharePeremption.setEnabled(chkSharePeremption.getValue());
		panelPublicOptions.setVisible(chkPublic.getValue());
		txtPublicPassword.setEnabled(chkPublicPassword.getValue());
		
		boolean showUsers = chkShareUsers.getValue() || btnByUsers.getValue();
//		panelUsers.setVisible(showUsers);
//		if (showUsers) {
//			datagridUsers.redraw();
//		}
		panelDisabled.setVisible(!showUsers);
		
		lstGlobalRights.setEnabled(btnAll.getValue());
		
		if (parent != null) {
			parent.center();
		}
	}

	private HashMap<ItemRight, ItemRightWithLabel> buildItemRightWithLabels() {
		HashMap<ItemRight, ItemRightWithLabel> rights = new LinkedHashMap<>();
		for (ItemRight right : ItemRight.values()) {
			String label = null;
			switch (right) {
			case READ:
				label = LabelsConstants.lblCnst.ReadOnlyInitial();
				break;
			case READ_DOWNLOAD:
				label = LabelsConstants.lblCnst.ReadAndDownloadInitial();
				break;
			case READ_DOWNLOAD_WRITE:
				label = LabelsConstants.lblCnst.ReadDownloadAndWriteInitial();
				break;
			default:
				label = LabelsConstants.lblCnst.Unknown();
				break;
			}
			rights.put(right, new ItemRightWithLabel(right, label));
		}
		return rights;
	}

	private void loadPermissionItem(PermissionItem permissionItem, ShareType shareType) {
		this.globalRight = permissionItem.getRight() != null ? permissionItem.getRight() : ItemRight.READ_DOWNLOAD_WRITE;
		lstGlobalRights.setSelectedIndex(globalRight.ordinal());

		if (shareType == ShareType.SHARE) {
			chkSharePeremption.setValue(permissionItem.getSharePeremptionDate() != null);
			dbSharePeremption.setValue(permissionItem.getSharePeremptionDate());

			chkPublic.setValue(permissionItem.isPublic());
			chkShareUsers.setValue(permissionItem.isShare());

			buildLink(permissionItem.getHash() != null ? permissionItem.getHash() : "");
			chkPublicPassword.setValue(permissionItem.getSharePassword() != null);
			txtPublicPassword.setText(permissionItem.getSharePassword() != null ? permissionItem.getSharePassword() : "");
		}
		else if (shareType == ShareType.DOCUMENTARY_SPACE) {
			//boolean isRestricted = permissionItem.getDocumentarySpaceStatus() != null;
			ShareStatus st = permissionItem.getDocumentarySpaceStatus();
			btnAll.setValue(st != null && st == ShareStatus.EVERYONE);
			btnByUsers.setValue(st != null && st == ShareStatus.BY_USERS);
		}

		AklaCommonService.Connect.getService().getPermissions(permissionItem.getId(), shareType, false, new GwtCallbackWrapper<List<Permission>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<Permission> result) {
				loadPermissions(result);
			}
		}.getAsyncCallback());
	}

	private void buildLink(String publicHash) {
		this.publicHash = publicHash;
		if (publicHash != null) {
			String link = GWT.getHostPageBaseURL() + "FolderView?hash=" + publicHash;
			txtPublicLink.setText(link);
		}
		else {
			txtPublicLink.setText("");
		}
	}
	
	@UiHandler("txtPublicLink")
	public void onPublicLink(ClickEvent event) {
		Window.open(txtPublicLink.getText(), "_blank", "");
	}

	private void loadPermissions(List<Permission> permissions) {
		this.permissions = permissions;
		if (permissions == null) {
			this.permissions = new ArrayList<>();
		}

		dataProvider.setList(this.permissions);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<Permission> buildGridPermissions() {
		this.sortHandler = new ListHandler<Permission>(new ArrayList<Permission>());

		Column<Permission, String> colImage = new Column<Permission, String>(new ResizableImageCell("20px", "30px")) {
			@Override
			public String getValue(Permission object) {
				return PathHelper.getRightPath(object.getUser().getProfilePic());
			}
		};

		Column<Permission, String> colEmail = new Column<Permission, String>(new TextCell()) {
			@Override
			public String getValue(Permission object) {
				return object.getUser().getEmail();
			}
		};
		colEmail.setSortable(true);
		sortHandler.setComparator(colEmail, new Comparator<Permission>() {

			@Override
			public int compare(Permission o1, Permission o2) {
				if (o1.getUser().getEmail() != null && o2.getUser().getEmail() != null) {
					return o1.getUser().getEmail().compareTo(o2.getUser().getEmail());
				}
				return 0;
			}
		});

		List<String> rightsLabel = new ArrayList<>();
		for (ItemRightWithLabel right : rights.values()) {
			rightsLabel.add(right.getLabel());
		}

		SelectionCell categoryCell = new SelectionCell(rightsLabel);
		Column<Permission, String> colRights = new Column<Permission, String>(categoryCell) {
			@Override
			public String getValue(Permission object) {
				return rights.get(object.getRight()).getLabel();
			}
		};
		colRights.setFieldUpdater(new FieldUpdater<Permission, String>() {
			@Override
			public void update(int index, Permission object, String value) {
				ItemRightWithLabel selectedRight = null;
				for (ItemRightWithLabel right : rights.values()) {
					if (right.getLabel().equals(value)) {
						selectedRight = right;
						break;
					}
				}
				object.setRight(selectedRight.getRight());
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(CommonImages.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<Permission, String> colDelete = new Column<Permission, String>(deleteCell) {

			@Override
			public String getValue(Permission object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Permission, String>() {

			@Override
			public void update(int index, final Permission object, String value) {
				deletePermission(object);
			}
		});

		DataGrid<Permission> datagrid = new DataGrid<>();
		datagrid.addColumn(colImage, "");
		datagrid.setColumnWidth(colImage, "10%");
		datagrid.addColumn(colEmail, LabelsConstants.lblCnst.Email());
		datagrid.setColumnWidth(colEmail, "60%");
		datagrid.addColumn(colRights, LabelsConstants.lblCnst.Rights());
		datagrid.setColumnWidth(colRights, "20%");
		datagrid.addColumn(colDelete);
		datagrid.setColumnWidth(colDelete, "10%");

		datagrid.setAutoHeaderRefreshDisabled(true);
		datagrid.setAutoFooterRefreshDisabled(true);
		datagrid.setSize("98%", "100%");
		datagrid.addStyleName(style.grid());

		dataProvider = new ListDataProvider<Permission>();
		dataProvider.addDataDisplay(datagrid);
		datagrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return datagrid;
	}

	private void deletePermission(Permission object) {
		permissions.remove(object);
		loadPermissions(permissions);
	}

	@UiHandler("chkPublic")
	public void onPublic(ValueChangeEvent<Boolean> event) {
		if (event.getValue()) {
			String hashCode = String.valueOf(new Object().hashCode());
			buildLink(hashCode);
		}
		else {
			buildLink(null);
		}
		updateUi();
	}

	@UiHandler("btnChangeAllUserRights")
	public void onChangeAllUserRights(ClickEvent event) {
		if (permissions != null) {
			ItemRightWithLabel selectedRight = lstAllUserRights.getSelectedObject();
			if (selectedRight != null) {
				for (Permission permission : permissions) {
					permission.setRight(selectedRight.getRight());
				}
	
				dataProvider.refresh();
			}
		}
	}

	@UiHandler("btnAddUsers")
	public void onAddUsers(ClickEvent event) {
		if (dial == null) {
			dial = new UsersSelectionWithGroupDialog(enterprise);
		}
		else {
			dial.clearSelection();
		}
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					List<User> selectedUsers = dial.getSelectedUsers();
					addPermissions(selectedUsers);
				}
			}
		});
	}

	private void addPermissions(List<User> selectedUsers) {
		List<User> usersToAdd = new ArrayList<>();
		if (permissions != null && selectedUsers != null) {
			for (User user : selectedUsers) {
				boolean found = false;
				for (Permission permission : permissions) {
					if (user.getUserId() == permission.getUserId()) {
						found = true;
						break;
					}
				}

				if (!found) {
					usersToAdd.add(user);
				}
			}
		}
		else {
			usersToAdd.addAll(selectedUsers != null ? selectedUsers : new ArrayList<User>());
		}
		
		if (permissions == null) {
			this.permissions = new ArrayList<>();
		}
		for (User user : usersToAdd) {
			permissions.add(new Permission(user, shareType, globalRight));
		}
		loadPermissions(permissions);
	}

	public void applySettings(PermissionItem permissionItem) {
		permissionItem.setRight(lstGlobalRights.getSelectedObject().getRight());

		if (shareType == ShareType.SHARE) {
			if (chkSharePeremption.getValue()) {
				Date sharePeremptionDate = dbSharePeremption.getValue();
				permissionItem.setSharePeremptionDate(sharePeremptionDate);
			}
			else {
				permissionItem.setSharePeremptionDate(null);
			}

			String sharePassword = null;
			if (chkPublic.getValue()) {
				permissionItem.setHash(publicHash);

				if (chkPublicPassword.getValue()) {
					sharePassword = txtPublicPassword.getText();
				}
			}
			permissionItem.setSharePassword(sharePassword);

			if (chkShareUsers.getValue()) {
				permissionItem.setShareStatus(ShareStatus.BY_USERS);
			}
			else {
				permissionItem.setShareStatus(null);
			}
		}
		else if (shareType == ShareType.DOCUMENTARY_SPACE) {
			if (btnAll.getValue()) {
				permissionItem.setDocumentarySpaceStatus(ShareStatus.EVERYONE);
			}
			else if (btnByUsers.getValue()) {
				permissionItem.setDocumentarySpaceStatus(ShareStatus.BY_USERS);
			}
		}
	}

	private boolean savePermissions() {
		return (shareType == ShareType.SHARE && chkShareUsers.getValue()) || (shareType == ShareType.DOCUMENTARY_SPACE && btnByUsers.getValue());
	}

	public List<Permission> getPermissions() {
		return savePermissions() ? permissions : null;
	}

	private class ItemRightWithLabel {

		private ItemRight right;
		private String label;

		public ItemRightWithLabel(ItemRight right, String label) {
			this.right = right;
			this.label = label;
		}

		public ItemRight getRight() {
			return right;
		}

		public String getLabel() {
			return label;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
