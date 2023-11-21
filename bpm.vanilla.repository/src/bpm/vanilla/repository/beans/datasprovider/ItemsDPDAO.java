package bpm.vanilla.repository.beans.datasprovider;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ItemsDPDAO extends HibernateDaoSupport {

	public Collection findAll() {
		return getHibernateTemplate().find("from ItemsDP");
	}

	public ItemsDP findById(int id) {
		List<ItemsDP> c = getHibernateTemplate().find("from ItemsDP d where d.id=" + id);
		if (c != null && c.size() > 0) {
			return (ItemsDP) c.get(0);
		}
		else {
			return null;
		}
	}

	public List<ItemsDP> findByItemId(int itemId) {
		return getHibernateTemplate().find("from ItemsDP d where d.itemId=" + itemId);
	}

	public List<ItemsDP> findByDatasProviderId(int datasProviderId) {
		return getHibernateTemplate().find("from ItemsDP d where d.datasProviderId=" + datasProviderId);
	}

	public void save(ItemsDP d) {
		getHibernateTemplate().save(d);
	}

	public void delete(ItemsDP d) {
		getHibernateTemplate().delete(d);
	}

	public void update(ItemsDP d) {
		getHibernateTemplate().update(d);
	}

	public ItemsDP findByDpAndItem(int datasProviderId, int itemId) {
		List<ItemsDP> c = getHibernateTemplate().find("from ItemsDP d where d.datasProviderId=" + datasProviderId + " AND d.itemId=" + itemId);
		if (c != null && c.size() > 0) {
			return (ItemsDP) c.get(0);
		}
		else {
			return null;
		}
	}
}
