package bpm.gwt.commons.client.viewer;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.popup.GroupPopup;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GtwVanillaViewer extends Viewer {

	private VanillaViewer vanillaViewer;
	private LaunchReportInformations itemInfo;
	
	private boolean forceOpen;
	
	public GtwVanillaViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, boolean forceOpen) {
		super(vanillaViewer);
		this.vanillaViewer = vanillaViewer;
		this.forceOpen = forceOpen;

		vanillaViewer.launchReport(this, item, selectedGroup, false, forceOpen);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if(itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
		btnRun.setVisible(true);
		btnGroups.setVisible(true);
		btnGroups.setText(itemInfo.getSelectedGroup().getName());
	}
	
	@Override
	public void runItem(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
		
		showWaitPart(true);
		
		defineToolbar(itemInfo);
		
		ReportingService.Connect.getInstance().runGateway(itemInfo, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}
			@Override
			public void onSuccess(String gtwUrl) {
				showWaitPart(false);
				
				showGateway(gtwUrl);
			}
		});
	}
	
	private void showGateway(String gtwUrl) {
		reportFrame.setUrl(gtwUrl);
	}

	@Override
	public void onRunClick(ClickEvent event) {
		vanillaViewer.launchReport(this, itemInfo.getItem(), itemInfo.getSelectedGroup(), false, forceOpen);
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	@Override
	public void onGroupChange(ClickEvent event) {
		GroupPopup displayPopup = new GroupPopup(this, itemInfo.getAvailableGroups());
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

	@Override
	public void changeGroup(Group selectedGroup) {
		itemInfo.setSelectedGroup(selectedGroup);
		
		btnGroups.setText(selectedGroup.getName());

		runItem(itemInfo);
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
	}
}
