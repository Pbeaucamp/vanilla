package bpm.gwt.workflow.commons.client.actions;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.workflow.commons.beans.Link;

import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.connection.Connection;

public class ConnectionManager {

	private List<WorkspaceLink> links;

	public void addLink(Widget startWidget, Widget endWidget, Connection connection) {
		if (links == null) {
			links = new ArrayList<WorkspaceLink>();
		}

		if (startWidget instanceof BoxItem && endWidget instanceof BoxItem) {
			links.add(new WorkspaceLink((BoxItem) startWidget, (BoxItem) endWidget, connection));
		}
	}

	public void removeLink(Widget startItem, Widget endItem) {
		WorkspaceLink link = getLink(startItem, endItem);
		if (link != null) {
			links.remove(link);
		}
	}

	public Connection getConnection(Widget startItem, Widget endItem) {
		WorkspaceLink link = getLink(startItem, endItem);
		if (link != null) {
			return link.getConnection();
		}
		return null;
	}

	public List<Link> getLinks() {
		if (links == null) {
			return new ArrayList<Link>();
		}

		List<Link> workflowLinks = new ArrayList<Link>();
		for (WorkspaceLink link : links) {
			workflowLinks.add(new Link(link.getStartItem().getActivity(), link.getEndItem().getActivity()));
		}
		return workflowLinks;
	}

	private WorkspaceLink getLink(Widget startItem, Widget endItem) {
		if (!(startItem instanceof BoxItem && endItem instanceof BoxItem)) {
			return null;
		}

		if (links != null) {
			for (WorkspaceLink link : links) {
				if (link.getStartItem() == startItem && link.getEndItem() == endItem) {
					return link;
				}
			}
		}
		return null;
	}

	public List<WorkspaceLink> getLinks(Widget widget) {
		List<WorkspaceLink> widgetLinks = new ArrayList<WorkspaceLink>();
		if (links != null) {
			for (WorkspaceLink link : links) {
				if (link.getStartItem() == widget || link.getEndItem() == widget) {
					widgetLinks.add(link);
				}
			}
		}
		return widgetLinks;
	}

	public class WorkspaceLink {

		private BoxItem startItem;
		private BoxItem endItem;

		private Connection connection;

		public WorkspaceLink(BoxItem startItem, BoxItem endItem, Connection connection) {
			this.startItem = startItem;
			this.endItem = endItem;
			this.connection = connection;
		}

		public Connection getConnection() {
			return connection;
		}

		public BoxItem getStartItem() {
			return startItem;
		}

		public BoxItem getEndItem() {
			return endItem;
		}
	}
}
