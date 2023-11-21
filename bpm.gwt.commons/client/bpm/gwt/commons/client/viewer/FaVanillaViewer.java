package bpm.gwt.commons.client.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.repository.PortailItemCube;
import bpm.gwt.commons.shared.repository.PortailItemCubeFmdt;
import bpm.gwt.commons.shared.repository.PortailItemCubeView;
import bpm.gwt.commons.shared.repository.PortailItemFasd;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FaVanillaViewer extends Viewer {
	
	private LaunchReportInformations itemInfo;
	private String cubeUrl;
	
	public FaVanillaViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups, boolean isDisco) {
		super(vanillaViewer);
		
		launchItem(item, selectedGroup, availableGroups, isDisco);
		
		toolbarPanel.setVisible(false);
		reportPanel.getElement().getStyle().setTop(0, Unit.PX);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if(itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
		btnRun.setVisible(true);
		btnOpenDashInNewTab.setVisible(true);
		btnOpenDashInNewTab.setTitle(LabelsConstants.lblCnst.OpenInAnalysis());
	}
	
	private void launchItem(PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups, boolean isDisco) {
		LaunchReportInformations itemInfo = new LaunchReportInformations(item, selectedGroup, new ArrayList<VanillaGroupParameter>(), new ArrayList<UserRunConfiguration>(), 
				null, null, new ArrayList<String>(), null, availableGroups, null);
		if(isDisco) {
			itemInfo.setTypeRun(TypeRun.DISCO);
		}
		runItem(itemInfo);
	}
	
	@Override
	public void runItem(final LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
		
		defineToolbar(itemInfo);
		
		int fasdDirItemId = -1;
		String cubeName = null;
		String viewName = null;
		
		if (itemInfo.getItem() instanceof PortailItemFasd) {
			fasdDirItemId = itemInfo.getItem().getId();
		}
		else if (itemInfo.getItem() instanceof PortailItemCube) {
			fasdDirItemId = ((PortailItemCube)itemInfo.getItem()).getFASDParentId();
			cubeName = ((PortailItemCube)itemInfo.getItem()).getName();
		}
		else if (itemInfo.getItem() instanceof PortailItemCubeView) {
			fasdDirItemId = ((PortailItemCubeView)itemInfo.getItem()).getCubeDto().getFASDParentId();
			cubeName = ((PortailItemCubeView)itemInfo.getItem()).getCubeDto().getName();	
			viewName = ((PortailItemCubeView)itemInfo.getItem()).getName();
		}
		else if (itemInfo.getItem() instanceof PortailItemCubeFmdt) {
			showCube(((PortailItemCubeFmdt)itemInfo.getItem()).getUrl());
			return;
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.UnexpectedType());
			return;
		}
		
		ReportingService.Connect.getInstance().getForwardUrlForCubes(fasdDirItemId, cubeName, viewName, new AsyncCallback<String>() {
			public void onSuccess(String arg0) {
				boolean isDisco = itemInfo.getTypeRun() == TypeRun.DISCO;
				
				showCube(arg0 + "&locale=" + LocaleInfo.getCurrentLocale().getLocaleName() + "&viewer=true&disco=" + isDisco);
			}
			
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetUrl());
			}
		});
	}
	
	private void showCube(String cubeUrl) {
		this.cubeUrl = cubeUrl;
		reportFrame.setUrl(cubeUrl);
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
	}

	@Override
	public void onRunClick(ClickEvent event) {
		launchItem(itemInfo.getItem(), itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups(), itemInfo.getTypeRun() == TypeRun.DISCO);
	}

	@Override
	public void onOpenDashInNewTabClick(ClickEvent event) {
		if (cubeUrl != null) {
			openNewTab(cubeUrl);
		}
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}
	
	public static native void openNewTab(String url)/*-{
		$wnd.open(url);
	}-*/;
}
