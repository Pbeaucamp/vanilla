package bpm.gwt.workflow.commons.client.actions;

import bpm.gwt.workflow.commons.client.workflow.CustomController;

import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.connection.Connection;

public class LinkAction extends Action {

	private CustomController controller;
	private Widget startWidget, endWidget;

	private TypeAction type;

	public LinkAction(CustomController controller, Connection connection, Widget startWidget, Widget endWidget, TypeAction type) {
		super("Link");
		this.controller = controller;
		this.startWidget = startWidget;
		this.endWidget = endWidget;
		this.type = type;
	}

	@Override
	public void doAction() {
		if (type == TypeAction.ADD) {
			controller.drawStraightArrowConnection(startWidget, endWidget, false);
		}
		else if (type == TypeAction.REMOVE) {
			Connection connection = controller.getConnection(startWidget, endWidget);
			if (connection != null) {
				controller.deleteConnection(connection, startWidget, endWidget);
			}
		}
	}

	@Override
	public void undoAction() {
		if (type == TypeAction.ADD) {
			Connection connection = controller.getConnection(startWidget, endWidget);
			if (connection != null) {
				controller.deleteConnection(connection, startWidget, endWidget);
			}
		}
		else if (type == TypeAction.REMOVE) {
			controller.drawStraightArrowConnection(startWidget, endWidget, false);
		}
	}

}
