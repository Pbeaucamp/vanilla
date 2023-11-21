package bpm.vanilla.platform.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import bpm.vanilla.platform.hibernate.BatchResult.Result;

/**
 * Wrappers around new hibernate functionality. Allows most spring 2 style dao's
 * to operate unchanged after being upgraded to Spring 3.1 and Hibernate 4.
 * 
 * @author Sam Thomas
 * 
 */
public class HibernateDaoSupport {
	private static final int BATCH_SIZE = 50;

	@Autowired
	private SessionFactory sessionFactory;

	private List<Object> objectToInsert;

	// I cheated!
	public HibernateDaoSupport getHibernateTemplate() {
		return this;
	}

	public void delete(Object obj) {

		Session session = getCurrentSession();
		try {
			Object res = session.merge(obj);
			session.delete(res);
			session.flush();
			session.clear();
			session.close();
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public <T> void deleteAll(Collection<T> items) {

		for (T item : items) {
			delete(item);
		}

	}

	public Object saveOrUpdate(Object obj) {
		Session session = getCurrentSession();
		try {
			Object res = session.merge(obj);
			Transaction t = session.beginTransaction();
			session.saveOrUpdate(res);
			session.flush();
			t.commit();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public Serializable save(Object obj) {
		Session session = getCurrentSession();
		try {
			Serializable result = session.save(obj);
			session.flush();
			session.clear();
			session.close();
			return result;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public BatchResult saveBatch(Object obj, boolean end) {
		BatchResult result = new BatchResult();
		if (objectToInsert == null) {
			objectToInsert = new CopyOnWriteArrayList<Object>();
		}

		if (objectToInsert.size() >= BATCH_SIZE) {
			insertList(result);

			objectToInsert.clear();
			objectToInsert.add(obj);
		}
		else {
			result.setResult(Result.WAITING, null);

			objectToInsert.add(obj);
		}

		if (end) {
			insertList(result);
			objectToInsert = null;
		}

		return result;
	}

	private void insertList(BatchResult result) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			for (Object objToInsert : objectToInsert) {
				session.save(objToInsert);
			}
			session.flush();
			session.clear();
			tx.commit();

			result.addFilesTraited(objectToInsert.size());
			result.setResult(Result.INSERTION, null);
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			result.addFilesTraited(objectToInsert.size());
			result.setResult(Result.ERROR, e.getMessage());
		} finally {
			session.close();
		}
	}

	public void update(Object obj) {
		Session session = getCurrentSession();
		try {
			Object res = session.merge(obj);
			session.update(res);
			session.flush();
			session.clear();
			session.close();
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public Session getCurrentSession() {

		// if(session == null) {
		// sessionFactory.getStatistics().setStatisticsEnabled(true);
		Session session = sessionFactory.openSession();

		session.setFlushMode(FlushMode.MANUAL);

		// }
		return session;
	}

	/**
	 * Simulate spring 2's "getHibernateTemplate().get()" method.
	 */
	public <T> T get(Class<T> clazz, Integer id) {
		@SuppressWarnings("unchecked")
		Session session = getCurrentSession();
		try {
			T result = (T) session.get(clazz, id);
			session.flush();
			session.clear();
			session.close();
			return result;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	/**
	 * Simulate spring 2's "getHibernateTemplate().loadAll()" method.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> loadAll(Class<T> clazz) {
		Session session = getCurrentSession();
		try {
			List<T> res = session.createCriteria(clazz).list();
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public List find(String query) {
		Session session = getCurrentSession();
		try {
			Query hq = session.createQuery(query);
			List res = hq.list();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}
	
	public Object findOne(String query) {
		Session session = getCurrentSession();
		try {
			Query hq = session.createQuery(query);
			List res = hq.list();
			session.close();
			if(res != null && res.size() > 0) {
				return res.get(0);
			}
			return null;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	/**
	 * 
	 * Return result count for a query starting with "Select count(*)"
	 * 
	 * @param query
	 * @param parameters
	 * @return
	 */
	public long count(String query, List<Object> parameters) {
		Session session = getCurrentSession();
		try {
			Query hq = session.createQuery(query);
			if (parameters != null) {
				int i = 0;
				for (Object param : parameters) {
					hq.setParameter(i, param);
					i++;
				}
			}
			
			long rowCount = (long) hq.uniqueResult();
			session.close();
			return rowCount;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> List<T> find(String query, int firstResult, int maxResult) {
		Session session = getCurrentSession();
		try {
			Query hq = session.createQuery(query);
			hq.setFirstResult(firstResult).setMaxResults(maxResult);
			
			List<T> res = hq.list();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	/**
	 * This method is a lot secure than find.
	 * To avoid SQL injection
	 * 
	 * @param query
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> find(String query, Object... parameters) {
		Session session = getCurrentSession();
		try {
			Query hq = session.createQuery(query);
			if (parameters != null) {
				int i = 0;
				for (Object param : parameters) {
					hq.setParameter(i, param);
					i++;
				}
			}
			
			List<T> res = hq.list();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	/**
	 * This method is a lot secure than find.
	 * To avoid SQL injection
	 * 
	 * @param query
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findWithPag(String query, int firstResult, int maxResult, List<Object> parameters) {
		Session session = getCurrentSession();
		try {
			Query hq = session.createQuery(query);
			hq.setFirstResult(firstResult).setMaxResults(maxResult);
			
			if (parameters != null) {
				int i = 0;
				for (Object param : parameters) {
					hq.setParameter(i, param);
					i++;
				}
			}
			
			List<T> res = hq.list();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(String query, List<Object> args) {
		Session session = getCurrentSession();
		try {
			Query q = session.createQuery(query);
	
			for (int i = 0; i < args.size(); i++) {
				q.setParameter(i, args.get(i));
			}
			List res = q.list();
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public List find(String query, int limit) {
		Session session = getCurrentSession();
		try {
			Query q = session.createQuery(query);
	
	//		for (int i = 0; i < args.length; i++) {
	//			q.setParameter(i, args[i]);
	//		}
			q.setMaxResults(limit);
	
			List res = q.list();
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByCriteria(DetachedCriteria criteria) {
		Session session = getCurrentSession();
		try {
			List<T> res = criteria.getExecutableCriteria(session).list();
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByCriteria(DetachedCriteria criteria, int firstResult, int maxResults) {
		Session session = getCurrentSession();
		try {
			List<T> res = criteria.getExecutableCriteria(session).setFirstResult(firstResult).setMaxResults(maxResults).list();
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}

	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public List saveOrUpdateAll(List beans) {
		List newList = new ArrayList<>();
		for (Object o : beans) {
			Object res = saveOrUpdate(o);
			newList.add(res);
		}
		return newList;
	}

	public void flush() {
		Session session = getCurrentSession();
		try {
			session.flush();
			session.clear();
			session.close();
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}

	public Object load(Class class1, Long long1) {
		Session session = getCurrentSession();
		try {
			Object res = session.load(class1, long1);
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}
	
	public List findBySqlQuery(String sql, Class cl) {
		Session session = getCurrentSession();
		try {
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(cl);
//			query.setResultTransformer(Transformers.TO_LIST); //or other transformer
			
			List res = query.list();
			session.flush();
			session.clear();
			session.close();
			return res;
		} catch (Exception e) {
			session.clear();
			session.close();
			throw e;
		}
	}
	
	public void stop() {
		sessionFactory.close();
	}
	
	public <T> boolean isProxy(T entity) {
	    if (entity == null) {
	        throw new 
	           NullPointerException("Entity passed for initialization is null");
	    }

	   
	    if (entity instanceof HibernateProxy) {
	        return true;
	    }
	    return false;
	}
}
