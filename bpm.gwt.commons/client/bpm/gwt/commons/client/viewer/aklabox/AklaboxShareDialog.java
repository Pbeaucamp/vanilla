package bpm.gwt.commons.client.viewer.aklabox;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.User;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.dialog.ButtonTab;
import bpm.gwt.commons.client.dialog.ITabDialog;
import bpm.gwt.commons.client.listeners.TabSwitchHandler;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.TypeShareAklabox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AklaboxShareDialog extends AbstractDialogBox implements IWait, ITabDialog {
	private static AklaboxShareUiBinder uiBinder = GWT.create(AklaboxShareUiBinder.class);

	interface AklaboxShareUiBinder extends UiBinder<Widget, AklaboxShareDialog> {
	}
	
	interface MyStyle extends CssResource {
		
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	HTMLPanel panelTab;
	
	@UiField
	SimplePanel panelContent;
	
	private AklaboxPropertiesView propertiesView;
	private AklaboxFolderView folderView;
	private AklaboxRightsView rightsView;
	
	private ButtonTab selectedButton;
	
	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;
	
	private String key;
	private String dashboardUrl;
	private boolean exportDashboard;

	public AklaboxShareDialog(PortailRepositoryItem item, String reportKey, List<String> formats) {
		super(LabelsConstants.lblCnst.ShareAklabox(), false, true);
		this.key = reportKey;
		this.exportDashboard = false;
		
		buildContent(item, formats, null);
	}

	public AklaboxShareDialog(PortailRepositoryItem item, String uuid, String dashboardUrl, List<String> formats, List<String> folders) {
		super(LabelsConstants.lblCnst.ShareAklabox(), false, true);
		this.key = uuid;
		this.dashboardUrl = dashboardUrl;
		this.exportDashboard = true;
		
		buildContent(item, formats, folders);
	}
	
	private void buildContent(PortailRepositoryItem item, List<String> formats, List<String> folders) {
		setWidget(uiBinder.createAndBindUi(this));
		
		propertiesView = new AklaboxPropertiesView(item, formats, folders, exportDashboard);
		folderView = new AklaboxFolderView(this);
		rightsView = new AklaboxRightsView(this, item);
		
		addTab(LabelsConstants.lblCnst.Properties(), propertiesView, true);
		addTab(LabelsConstants.lblCnst.Folders(), folderView, false);
		addTab(LabelsConstants.lblCnst.Permissions(), rightsView, false);

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			showWaitPart(true);
			
			String name = propertiesView.getName();
			List<String> selectedFormats = propertiesView.getSelectedFormats();
			
			if(name == null || name.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedName());
				return;
			}
			
			if(selectedFormats == null || selectedFormats.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseFormat());
				return;
			}
			
			int selectedDirectoryId = folderView.getSelectedFolder();
			if(selectedDirectoryId == -1) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.SelectFolder());
				return;
			}
			
			TypeShareAklabox type = rightsView.getTypeShare();
			List<User> selectedUsers = rightsView.getSelectedUsers();
			List<Group> selectedGroups = rightsView.getSelectedGroups();
			
			if(type == TypeShareAklabox.USERS_SHARE && (selectedUsers == null || selectedUsers.isEmpty())) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseUsers());
				return;
			}
			
			if(type == TypeShareAklabox.GROUPS_SHARE && (selectedGroups == null || selectedGroups.isEmpty())) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.ChooseGroups());
				return;
			}
			
			List<String> selectedFolders = new ArrayList<String>();
			if(exportDashboard) {
				selectedFolders = propertiesView.getSelectedFolders();
			}
			
			boolean isLandscape = propertiesView.isLandscape();
			
			ReportingService.Connect.getInstance().shareAklabox(key, name, dashboardUrl, selectedFormats, selectedFolders, type, selectedUsers, selectedGroups, selectedDirectoryId, exportDashboard, isLandscape, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					showWaitPart(false);
					
					caught.printStackTrace();
					
					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
				}

				@Override
				public void onSuccess(Void result) {
					showWaitPart(false);
					
					hide();
					
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ShareAklaboxSuccess());
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
	
	public void addTab(String buttonName, Widget widget, boolean selectTab) {
		ButtonTab button = new ButtonTab(buttonName);
		button.addClickHandler(new TabSwitchHandler(this, button, widget));
		panelTab.add(button);
		
		if(selectTab) {
			switchViewer(button, widget);
		}
	}
	
	@Override
	public void switchViewer(ButtonTab button, Widget widget) {
		if(selectedButton != null) {
			this.selectedButton.select(false);
		}
		button.select(true);
		
		this.selectedButton = button;
		
		panelContent.setWidget(widget);
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible) {
			if (!isCharging) {
				isCharging = true;

				greyPanel = new GreyAbsolutePanel();
				waitPanel = new WaitAbsolutePanel();

				contentPanel.add(greyPanel);
				contentPanel.add(waitPanel);
				
				int width = contentPanel.getOffsetWidth();
				if(width == 0) {
					width = 850;
				}

				DOM.setStyleAttribute(waitPanel.getElement(), "top", 50 + "px");
				DOM.setStyleAttribute(waitPanel.getElement(), "left", ((width / 2) - 100) + "px");
			}
		}
		else if (!visible) {
			if (isCharging) {
				isCharging = false;

				contentPanel.remove(greyPanel);
				contentPanel.remove(waitPanel);
			}
		}
	}

}
