package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate5.SessionFactoryUtils;

import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.beans.AccessRequest.RequestAnswer;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

/**
 * 
 * @author manu
 * 
 *         Adds support for bi object access requests. done in hibernate criteria querying
 * 
 */
public class AccessRequestDao extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<AccessRequest> findAdminPendingRequests() {
		List<AccessRequest> requests;

		try {
			requests = (List<AccessRequest>)getHibernateTemplate().getCurrentSession().createCriteria(AccessRequest.class).add(Expression.eq("answerOpId", RequestAnswer.PENDING.getAnswerId())).list();
		} catch(HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}

		return requests;
	}

	@SuppressWarnings("unchecked")
	public List<AccessRequest> findAdminAllRequests() {
		List<AccessRequest> requests;

		try {
			requests = (List<AccessRequest>) getHibernateTemplate().getCurrentSession().createCriteria(AccessRequest.class).list();
		} catch(HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}

		return requests;
	}

	@SuppressWarnings("unchecked")
	public List<AccessRequest> findUserPendingRequests(int userId) {
		List<AccessRequest> requests;

		try {
			requests = (List<AccessRequest>) getHibernateTemplate().getCurrentSession().createCriteria(AccessRequest.class).add(Expression.eq("answerOpId", RequestAnswer.PENDING.getAnswerId())).add(Expression.eq("requestUserId", userId)).list();
		} catch(HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}

		return requests;
	}

	@SuppressWarnings("unchecked")
	public List<AccessRequest> findUserAllRequests(int userId) {
		List<AccessRequest> requests;

		try {
			requests = (List<AccessRequest>) getHibernateTemplate().getCurrentSession().createCriteria(AccessRequest.class).add(Expression.eq("requestUserId", userId)).list();
		} catch(HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}

		return requests;
	}

	@SuppressWarnings("unchecked")
	public List<AccessRequest> findUserPendingDemands(int userId) {
		List<AccessRequest> requests;

		try {
			requests = (List<AccessRequest>) getHibernateTemplate().getCurrentSession().createCriteria(AccessRequest.class).add(Expression.eq("answerOpId", RequestAnswer.PENDING.getAnswerId())).add(Expression.eq("userId", userId)).list();
		} catch(HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}

		return requests;
	}

	@SuppressWarnings("unchecked")
	public List<AccessRequest> findUserAllDemands(int userId) {
		List<AccessRequest> requests;

		try {
			requests = (List<AccessRequest>) getHibernateTemplate().getCurrentSession().createCriteria(AccessRequest.class).add(Expression.eq("userId", userId)).list();
		} catch(HibernateException e) {
			throw SessionFactoryUtils.convertHibernateAccessException(e);
		}

		return requests;
	}

	public void save(AccessRequest request) {
		getHibernateTemplate().save(request);
	}

	public void delete(AccessRequest request) {
		getHibernateTemplate().delete(request);
	}

	public void update(AccessRequest request) {
		getHibernateTemplate().update(request);
	}
}
