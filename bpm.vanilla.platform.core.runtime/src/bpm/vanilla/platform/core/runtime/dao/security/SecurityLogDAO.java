package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SecurityLogDAO extends HibernateDaoSupport {
	
	private UserDAO userDao;

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
	
	public int save(SecurityLog d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(SecurityLog d) {
		getHibernateTemplate().delete(d);
	}

	public void update(SecurityLog d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<SecurityLog> getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) {
		StringBuffer buf = new StringBuffer("from SecurityLog");
		boolean firstParam = true;
		if (userId != null) {
			buf.append(" WHERE userId = " + userId);
			firstParam = false;
		}
		if (type != null) {
			if (firstParam) {
				buf.append(" WHERE");
			}
			else {
				buf.append(" AND");
			}
			buf.append(" type = " + type.getType());
			firstParam = false;
		}
//		if (startDate != null) {
//			if (!firstParam) {
//				buf.append(" AND");
//			}
//			buf.append(" date > " + startDate);
//			firstParam = false;
//		}
		//Not used for now
//		if (endDate != null) {
//			if (!firstParam) {
//				buf.append(" AND");
//			}
//			buf.append(" date ");
//		}
		buf.append(" ORDER BY date DESC");
		
		List<SecurityLog> l = (List<SecurityLog>) getHibernateTemplate().find(buf.toString());
		List<SecurityLog> logs = new ArrayList<>();
		if (startDate != null) {
			for (SecurityLog log : l) {
				if (log.getDate().before(startDate)) {
					break;
				}
				logs.add(buildLog(log));
			}
		}
		return logs;
	}

	private SecurityLog buildLog(SecurityLog log) {
		User user = userDao.findByPrimaryKey(log.getUserId());
		log.setUser(user);
		
		return log;
	}
}
