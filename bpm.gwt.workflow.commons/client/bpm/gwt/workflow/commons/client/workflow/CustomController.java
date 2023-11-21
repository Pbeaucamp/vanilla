package bpm.gwt.workflow.commons.client.workflow;

import java.util.List;

import bpm.gwt.workflow.commons.client.actions.Action.TypeAction;
import bpm.gwt.workflow.commons.client.actions.ActionManager;
import bpm.gwt.workflow.commons.client.actions.ConnectionManager;
import bpm.gwt.workflow.commons.client.actions.ConnectionManager.WorkspaceLink;
import bpm.gwt.workflow.commons.client.actions.LinkAction;
import bpm.workflow.commons.beans.Link;

import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.DiagramController;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.event.UntieLinkEvent;
import com.orange.links.client.event.UntieLinkHandler;

public class CustomController extends DiagramController implements UntieLinkHandler {

	private ActionManager actionManager;
	private ConnectionManager connectionManager;

	public CustomController(ActionManager actionManager, int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);
		this.actionManager = actionManager;
		this.connectionManager = new ConnectionManager();

		addUntieLinkHandler(this);
	}

	@Override
	public Connection drawStraightArrowConnection(Widget startWidget, Widget endWidget) {
		return drawStraightArrowConnection(startWidget, endWidget, true);
	}

	public Connection drawStraightArrowConnection(Widget startWidget, Widget endWidget, boolean addAction) {
		Connection cl = super.drawStraightArrowConnection(startWidget, endWidget);
		addConnection(cl, startWidget, endWidget);

		if (addAction) {
			LinkAction action = new LinkAction(this, cl, startWidget, endWidget, TypeAction.ADD);
			actionManager.launchAction(action, false);
		}

		return cl;
	}

	public void deleteConnection(Connection c, Widget startWidget, Widget endWidget) {
		super.deleteConnection(c);
		removeConnection(startWidget, endWidget);
	}

	@Override
	public void onUntieLink(UntieLinkEvent event) {
		Connection c = event.getConnection();
		Widget startWidget = event.getStartWidget();
		Widget endWidget = event.getEndWidget();

		removeConnection(startWidget, endWidget);

		LinkAction action = new LinkAction(this, c, startWidget, endWidget, TypeAction.REMOVE);
		actionManager.launchAction(action, false);
	}

	public Connection getConnection(Widget startWidget, Widget endWidget) {
		return connectionManager.getConnection(startWidget, endWidget);
	}

	public List<WorkspaceLink> removeConnections(Widget widget) {
		List<WorkspaceLink> links = connectionManager.getLinks(widget);
		for (WorkspaceLink link : links) {
			deleteConnection(link.getConnection(), link.getStartItem(), link.getEndItem());
		}
		return links;
	}

	public void restoreConnections(List<WorkspaceLink> links) {
		if (links != null) {
			for(WorkspaceLink link : links) {
				drawStraightArrowConnection(link.getStartItem(), link.getEndItem(), false);
			}
		}
	}

	private void addConnection(Connection connection, Widget startWidget, Widget endWidget) {
		connectionManager.addLink(startWidget, endWidget, connection);
	}

	private void removeConnection(Widget startWidget, Widget endWidget) {
		connectionManager.removeLink(startWidget, endWidget);
	}

	public List<Link> getLinks() {
		return connectionManager.getLinks();
	}
}
