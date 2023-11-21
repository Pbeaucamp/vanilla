package bpm.vanilla.repository.beans.security;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SecuredLinkedDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<SecuredLinked> findAll() {
		return (List<SecuredLinked>) getHibernateTemplate().find("from SecuredLinked");
	}

	@SuppressWarnings("unchecked")
	public SecuredLinked findByPrimaryKey(int key) {
		List<SecuredLinked> l = getHibernateTemplate().find("from SecuredLinked d where id=" + key);
		if (l == null || l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	public int save(SecuredLinked d) {
		int id = (Integer) getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public void delete(SecuredLinked d) {
		getHibernateTemplate().delete(d);
	}

	public void update(SecuredLinked d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<SecuredLinked> getByLinkedDocId(Integer id) {
		return getHibernateTemplate().find("from SecuredLinked d where d.linkedDocumentId=" + id);
	}

	@SuppressWarnings("unchecked")
	public List<SecuredLinked> getByLinkedDocId(Integer linkedId, int begin, int step) {
		String sql = "from SecuredLinked d where d.linkedDocumentId=" + linkedId;
		Session session = getHibernateTemplate().getSessionFactory().openSession();

		Query hql = session.createQuery(sql);

		hql.setFirstResult(begin);
		hql.setMaxResults(step);

		List<SecuredLinked> l = hql.list();

		session.close();
		return l;
	}

	@SuppressWarnings("unchecked")
	public List<SecuredLinked> getByGroupIdAndLinkedId(Integer groupId, Integer linkedId) {
		List<SecuredLinked> l = getHibernateTemplate().find("from SecuredLinked d where d.linkedDocumentId=" + linkedId + " AND groupId=" + groupId);
		if(l == null) {
			l = new ArrayList<SecuredLinked>();
		}
		return l;
	}

	public List<SecuredLinked> getByGroupId(int groupId) {
		List<SecuredLinked> l = getHibernateTemplate().find("from SecuredLinked where groupId=" + groupId);
		if(l == null) {
			l = new ArrayList<SecuredLinked>();
		}
		return l;
	}

}