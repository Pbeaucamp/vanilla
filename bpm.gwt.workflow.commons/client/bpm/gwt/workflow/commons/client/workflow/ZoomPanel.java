package bpm.gwt.workflow.commons.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ZoomPanel extends Composite {
	private static final double ZOOM_STEP = 0.2;

	private static ZoomPanelUiBinder uiBinder = GWT.create(ZoomPanelUiBinder.class);

	interface ZoomPanelUiBinder extends UiBinder<Widget, ZoomPanel> {
	}

	private WorkspacePanel creationPanel;
	private double zoomValue = 1;

	public ZoomPanel(WorkspacePanel creationPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.creationPanel = creationPanel;
	}

	@UiHandler("btnZoomMax")
	public void onZoomMax(ClickEvent e) {
		if (zoomValue <= 1) {
			zoomValue = zoomValue + ZOOM_STEP;
			creationPanel.setZoom(zoomValue);
		}
	}

	@UiHandler("btnZoomMin")
	public void onZoomMin(ClickEvent e) {
		if (zoomValue > 0.6) {
			zoomValue = zoomValue - ZOOM_STEP;
			creationPanel.setZoom(zoomValue);
		}
	}
}
