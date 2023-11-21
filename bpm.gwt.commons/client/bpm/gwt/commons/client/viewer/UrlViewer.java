package bpm.gwt.commons.client.viewer;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UrlViewer extends Viewer {

	private PortailRepositoryItem item;
	private Group selectedGroup;
	private List<Group> availableGroups;
	
	private String url;
	
	public UrlViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.availableGroups = availableGroups;
		
		LaunchReportInformations itemInfo = new LaunchReportInformations();
		itemInfo.setItem(item);
		
		runItem(itemInfo);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if(itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
		btnOpenDashInNewTab.setVisible(true);
		
		addMessageToolbar(LabelsConstants.lblCnst.IfEmptyOpenInOtherTab());
	}

	@Override
	public void runItem(LaunchReportInformations itemInformations) {
		showWaitPart(true);
		
		defineToolbar(itemInformations);
		
		ReportingService.Connect.getInstance().getExternalUrl(itemInformations.getItem().getId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(String url) {
				if (url.indexOf("<error>") > -1) {
					String msg = url.replace("<error>", "").replace("</error>", "");
					
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), msg);
					
					showWaitPart(false);
				}
				else {
					UrlViewer.this.url = url;
					showWaitPart(false);

					reportFrame.setUrl(url);
				}
			}
		});
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, item.getId(), TypeCollaboration.ITEM_NOTE, selectedGroup, availableGroups);
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}
	
	@Override
	public void onOpenDashInNewTabClick(ClickEvent event) {
		if (url != null) {
			openNewTab(url);
		}
	}
	
	public static native void openNewTab(String url)/*-{
		$wnd.open(url);
	}-*/;
}

