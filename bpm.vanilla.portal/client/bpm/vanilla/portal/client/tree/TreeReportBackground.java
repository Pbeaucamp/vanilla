package bpm.vanilla.portal.client.tree;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.client.loading.IWait;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.portal.client.Listeners.ReportBackgroundDoubleClickHandler;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class TreeReportBackground extends TreeItemOk {

	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";
	private static final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);
	
//	private ContentDisplayPanel contentDisplayPanel;
	private ReportBackground item;

	public TreeReportBackground(IWait waitPanel, ContentDisplayPanel contentDisplayPanel, ReportBackground item, TypeViewer typeViewer, String search) {
		super(new CustomHTML(new Image(buildImage(item)) + buildName(item)), typeViewer);
//		this.contentDisplayPanel = contentDisplayPanel;
		this.item = item;
		
//		addContextMenuHandler(new DirectoryItemContextMenuHandler(contentDisplayPanel, this, typeViewer));
		addDoubleClickHandler(new ReportBackgroundDoubleClickHandler(waitPanel, contentDisplayPanel, this));
	}

	private static ImageResource buildImage(ReportBackground item) {
		if (item.isFailed()) {
			return PortalImage.INSTANCE.background_failed_16();
		}
		else if (item.isNew()) {
			return PortalImage.INSTANCE.background_new_16();
		}
		else if (item.isRunning()) {
			return PortalImage.INSTANCE.background_running_16();
		}
		
		return PortalImage.INSTANCE.background_16();
	}

	private static String buildName(ReportBackground item) {
		StringBuffer buf = new StringBuffer(item.getName());
		buf.append(" - ");
		buf.append(dateFormatter.format(item.getCreationDate()));
		
		if (item.isFailed()) {
			buf.append(" (");
			buf.append(ToolsGWT.lblCnst.Failed());
			buf.append(")");
		}
		else if (item.isNew()) {
			buf.append(" (");
			buf.append(ToolsGWT.lblCnst.New());
			buf.append(")");
		}
		else if (item.isRunning()) {
			buf.append(" (");
			buf.append(ToolsGWT.lblCnst.IsRunning());
			buf.append(")");
		}
		
		return buf.toString();
	}
	
	public ReportBackground getItem() {
		return item;
	}
}
