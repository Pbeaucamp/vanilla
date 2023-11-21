package bpm.es.datasource.analyzer.ui.gef.model;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class Diagram {
	private DataSource dataSource;
	private List<RepositoryItem> items = new ArrayList<RepositoryItem>();

	private List<Link> links = new ArrayList<Link>();

	public Diagram() {

	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void addDirectoryItem(RepositoryItem item) {
		for (RepositoryItem it : items) {
			if (it.getId() == item.getId()) {
				return;
			}
		}
		items.add(item);

	}

	public void addPrimaryDirectoryItem(RepositoryItem item) {
		addLink(new Link(dataSource, item));
	}

	public List<RepositoryItem> getItems() {
		return new ArrayList<RepositoryItem>(items);
	}

	public List<Link> getLinks() {
		return links;
	}

	public List getOutgoingLinks(Object source) {
		List<Link> lst = new ArrayList<Link>();
		for (Link l : links) {
			if (l.getSource() == source) {
				lst.add(l);
			}
		}
		return lst;
	}

	public List getIncomingLinks(Object target) {
		List<Link> lst = new ArrayList<Link>();
		for (Link l : links) {
			if (l.getTarget() == target) {
				lst.add(l);
			}
		}
		return lst;
	}

	public void addLink(Link lk) {
		RepositoryItem source = null;
		RepositoryItem target = null;

		for (RepositoryItem it : items) {
			if (lk.getSource() instanceof RepositoryItem) {
				if (((RepositoryItem) lk.getSource()).getId() == it.getId()) {
					source = it;
				}
			}

			if (lk.getTarget().getId() == it.getId()) {
				target = it;
			}

			if (source != null && target != null) {
				break;
			}
		}

		if (source != null && target != null) {
			links.add(new Link(source, target));
		}
		else if (source != null && target == null) {
			links.add(new Link(source, lk.getTarget()));
			addDirectoryItem(lk.getTarget());

		}
		else if (source == null && target != null) {
			if (lk.getSource() instanceof RepositoryItem) {
				addDirectoryItem((RepositoryItem) lk.getSource());
			}

			links.add(new Link(lk.getSource(), target));
		}
		else {
			if (lk.getSource() instanceof RepositoryItem) {
				addDirectoryItem((RepositoryItem) lk.getSource());
			}
			addDirectoryItem(lk.getTarget());

			links.add(lk);
		}

	}

	public Object[] getConnectedTo(Object o) {
		List<Object> lst = new ArrayList<Object>();
		for (Link l : links) {
			if (l.getSource() == o) {
				lst.add(l.getTarget());
			}
			else if (l.getTarget() == o) {
				lst.add(l.getSource());
			}
		}
		return lst.toArray(new Object[lst.size()]);
	}
}
