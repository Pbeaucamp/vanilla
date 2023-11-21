package bpm.vanilla.portal.client.Listeners;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.tree.TreeReportBackground;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

public class ReportBackgroundDoubleClickHandler implements DoubleClickHandler {
	
	private IWait waitPanel;
	private ContentDisplayPanel contentDisplayPanel;
	private TreeReportBackground treeItem;
	
	public ReportBackgroundDoubleClickHandler(IWait waitPanel, ContentDisplayPanel contentDisplayPanel, TreeReportBackground treeItem) {
		super();
		this.waitPanel = waitPanel;
		this.contentDisplayPanel = contentDisplayPanel;
		this.treeItem = treeItem;
	}

	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		final ReportBackground item = treeItem.getItem();
		
		if (!item.isFailed() && !item.isRunning()) {
			item.setReaded(true);
			
			ReportingService.Connect.getInstance().updateReportBackground(item, new GwtCallbackWrapper<Void>(waitPanel, true, true) {

				@Override
				public void onSuccess(Void result) {
					contentDisplayPanel.openViewer(item);
				}
			}.getAsyncCallback());
		}
		else {
			StringBuffer buf = new StringBuffer();
			if (item.isFailed()) {
				buf.append(ToolsGWT.lblCnst.TheReport());
				buf.append(" '");
				buf.append(item.getName());
				buf.append("' ");
				buf.append(ToolsGWT.lblCnst.HasFailedContactAdmin());
			}
			else if (item.isRunning()) {
				buf.append(ToolsGWT.lblCnst.TheReport());
				buf.append(" '");
				buf.append(item.getName());
				buf.append("' ");
				buf.append(ToolsGWT.lblCnst.IsRunningPleaseRefreshWhenItIsDone());
			}
			
			MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), buf.toString());
		}
	}
}
