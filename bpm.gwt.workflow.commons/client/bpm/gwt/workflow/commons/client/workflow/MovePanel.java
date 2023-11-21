package bpm.gwt.workflow.commons.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MovePanel extends Composite {
	private static final int LEFT = 100;
	private static final int RIGHT = -100;
	private static final int DOWN = -100;
	private static final int UP = 100;
	
	private static MovePanelUiBinder uiBinder = GWT.create(MovePanelUiBinder.class);

	interface MovePanelUiBinder extends UiBinder<Widget, MovePanel> {
	}

	private WorkspacePanel creationPanel;

	public MovePanel(WorkspacePanel creationPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.creationPanel = creationPanel;
	}

	@UiHandler("btnLeft")
	void onMoveLeft(ClickEvent e) {
		creationPanel.moveGrid(LEFT, 0);
	}

	@UiHandler("btnRight")
	void onMoveRight(ClickEvent e) {
		creationPanel.moveGrid(RIGHT, 0);
	}

	@UiHandler("btnTop")
	void onMoveTop(ClickEvent e) {
		creationPanel.moveGrid(0, UP);
	}

	@UiHandler("btnBelow")
	void onMoveBelow(ClickEvent e) {
		creationPanel.moveGrid(0, DOWN);
	}
}
