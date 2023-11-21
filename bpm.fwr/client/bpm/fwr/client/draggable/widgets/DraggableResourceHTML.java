package bpm.fwr.client.draggable.widgets;

import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportPanel.DropTargetType;

import com.google.gwt.user.client.ui.HTML;

public class DraggableResourceHTML extends HTML{
	
	private IResource resource;
	private ReportPanel reportPanel;
	private DropTargetType type;
	private int index;

	public DraggableResourceHTML(String html, IResource resource, ReportPanel reportPanel, DropTargetType type, int index) {
		super(html);
		this.setResource(resource);
		this.setReportPanel(reportPanel);
		this.setType(type);
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public IResource getResource() {
		return resource;
	}

	public void setReportPanel(ReportPanel reportPanel) {
		this.reportPanel = reportPanel;
	}

	public ReportPanel getReportPanel() {
		return reportPanel;
	}

	public void setType(DropTargetType type) {
		this.type = type;
	}

	public DropTargetType getType() {
		return type;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
