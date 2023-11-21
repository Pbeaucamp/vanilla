package bpm.vanilla.repository.beans.versionning;

import java.util.Calendar;
import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class LockDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Lock> getAllLocks() {
		return (List<Lock>) getHibernateTemplate().find("from Lock");
	}

	public Lock addLock(int directoryItemId, int userId) {
		Lock lock = new Lock();
		lock.setCreationDate(Calendar.getInstance().getTime());
		lock.setUserId(userId);

		lock.setId((Integer) getHibernateTemplate().save(lock));
		return lock;
	}

	@SuppressWarnings("unchecked")
	public void removeLock(int directoryItemId) {
		List<Lock> m = getHibernateTemplate().find("from Lock where directoryItemId=" + directoryItemId);

		getHibernateTemplate().deleteAll(m);

	}
}
