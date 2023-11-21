package bpm.smart.web.client.panels;

import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LogRPanel extends Composite {
	
	private static final int RIGHT_OPEN = 0;
	private static final int RIGHT_CLOSE = -476;

	private static LogRPanelUiBinder uiBinder = GWT.create(LogRPanelUiBinder.class);

	interface LogRPanelUiBinder extends UiBinder<Widget, LogRPanel> {
	}

	@UiField
	Label lblLog;
	
	@UiField
	HTMLPanel mainPanel;

	@UiField
	HTMLPanel logArea;

	@UiField
	Image btnCollapse, btnClear;

	private boolean isOpen = false;

	public LogRPanel(IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("lblLog")
	public void onMessageClick(ClickEvent event) {
		updateUi();
	}

	private void updateUi() {
		if (isOpen) {
			lblLog.setVisible(true);
			mainPanel.getElement().getStyle().setRight(RIGHT_CLOSE, Unit.PX);
		}
		else {
			lblLog.setVisible(false);
			mainPanel.getElement().getStyle().setRight(RIGHT_OPEN, Unit.PX);
		}
		this.isOpen = !isOpen;
	}

	@UiHandler("btnCollapse")
	public void onCancelClick(ClickEvent event) {
		updateUi();
	}

	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		logArea.clear();
	}
	
	public void addLog(String log) {
		logArea.add(new HTML(log));
		
	}
}
