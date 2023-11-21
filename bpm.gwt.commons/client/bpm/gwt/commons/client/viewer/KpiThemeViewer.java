package bpm.gwt.commons.client.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.repository.PortailItemKpiTheme;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;

public class KpiThemeViewer extends Viewer {

	private LaunchReportInformations itemInfo;
	
	private String selectedDate;
	private String kpiUrl;

	public KpiThemeViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);

//		toolbarPanel.setVisible(false);
		reportPanel.getElement().getStyle().setTop(40, Unit.PX);

		vanillaViewer.launchReport(this, item, selectedGroup, false, false);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if (itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
//		btnRun.setVisible(true);
		btnOpenDashInNewTab.setVisible(true);
		btnOpenDashInNewTab.setTitle(LabelsConstants.lblCnst.OpenInKpiUser());
	}

	private void launchItem(PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		LaunchReportInformations itemInfo = new LaunchReportInformations(item, selectedGroup, new ArrayList<VanillaGroupParameter>(), new ArrayList<UserRunConfiguration>(), null, null, new ArrayList<String>(), null, availableGroups, null);
		runItem(itemInfo);
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}

	@Override
	public void runItem(final LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;

		defineToolbar(itemInfo);

		int themeId = -1;

		if (itemInfo.getItem() instanceof PortailItemKpiTheme) {
			themeId = ((PortailItemKpiTheme) itemInfo.getItem()).getThemeId();
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.UnexpectedType());
			return;
		}

		ReportingService.Connect.getInstance().getForwardUrlForKpi(themeId, selectedDate, new GwtCallbackWrapper<String>(this, true) {

			@Override
			public void onSuccess(String result) {
				showKpi(result + "&locale=" + LocaleInfo.getCurrentLocale().getLocaleName());
			}
		}.getAsyncCallback());
	}

	private void showKpi(String kpiUrl) {
		this.kpiUrl = kpiUrl;
		reportFrame.setUrl(kpiUrl + "&viewer=true");
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
	}

	@Override
	public void onRunClick(ClickEvent event) {
		launchItem(itemInfo.getItem(), itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
	}

	@Override
	public void onOpenDashInNewTabClick(ClickEvent event) {
		if (kpiUrl != null) {
			openNewTab(kpiUrl + "&viewer=false");
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
