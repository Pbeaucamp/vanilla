package bpm.vanilla.platform.core.runtime.dao.external;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ExternalDao extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public D4C getD4CDefinition(int id) {
		List<D4C> items = getHibernateTemplate().find("FROM D4C WHERE id = " + id);
		return items != null && !items.isEmpty() ? items.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<D4C> getD4CDefinitions() {
		return getHibernateTemplate().find("FROM D4C");
	}

	@SuppressWarnings("unchecked")
	public List<D4CItem> getD4cItems(int parentId, TypeD4CItem type) {
		List<D4CItem> items = new ArrayList<D4CItem>();
		
		switch (type) {
		case VISUALIZATION:
			items.addAll(getHibernateTemplate().find("FROM D4CItemVisualization WHERE parentId = " + parentId));
		default:
			break;
		}
		
		return items;
	}

}
