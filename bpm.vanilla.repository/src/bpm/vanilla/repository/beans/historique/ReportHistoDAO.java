package bpm.vanilla.repository.beans.historique;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ReportHistoDAO extends HibernateDaoSupport {

	public List<ReportHisto> getAll() {
		return getHibernateTemplate().find("from ReportHisto");
	}

	public int save(ReportHisto d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(ReportHisto d) {
		getHibernateTemplate().delete(d);
	}

	public void update(ReportHisto d) {
		getHibernateTemplate().update(d);
	}

	public List<ReportHisto> getForItemId(int dirItemId) {
		return getHibernateTemplate().find("from ReportHisto where directoryItemId=" + dirItemId);
	}

	public void delForDoc(int id) {
		for (ReportHisto r : (List<ReportHisto>) getHibernateTemplate().find("from ReportHisto where gedDocId=" + id)) {
			delete(r);
		}
	}

	public List<ReportHisto> getForItemIdAndGroupId(int itemId, int grpId, User user) {
		List<ReportHisto> res = new ArrayList<ReportHisto>();
		List l = null;
		l = getHibernateTemplate().find("from " + "ReportHisto histo, SecurityReportHisto secu " + "where histo.directoryItemId=" + itemId + " and ((histo.directoryItemId=secu.directoryItemId" + " and secu.groupId=" + grpId + " and secu.groupId=histo.grpId)  or( histo.userId=" + user.getId() + " and histo.grpId <= 0))");
		for (Object o : l) {
			res.add((ReportHisto) ((Object[]) o)[0]);
		}

		return res;
	}

	public List<ReportHisto> get(Date start, Date end) {
		List<ReportHisto> res = new ArrayList<ReportHisto>();
		StringBuffer where = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (start != null) {
			where.append(" where  histo.creation >='" + sdf.format(start) + "'");
		}
		if (end != null) {
			if (where.length() != 0) {
				where.append(" and  histo.creation <='" + sdf.format(end) + "'");
			}
			else {
				where.append(" where  histo.creation <'" + sdf.format(start) + "'");
			}
		}
		res.addAll(getHibernateTemplate().find("from ReportHisto histo " + where.toString()));
		return res;
	}

	public List<ReportHisto> getForGroupId(int grpId, User user) {
		List<ReportHisto> res = new ArrayList<ReportHisto>();
		List l = null;
		if(grpId > -1) {
			l = getHibernateTemplate().find("from ReportHisto histo, SecurityReportHisto secu where " + " histo.directoryItemId=secu.directoryItemId" + " and secu.groupId=" + grpId + " and (histo.grpId=" + grpId + " or( histo.userId=" + user.getId() + " and histo.grpId <= 0))");
			for (Object o : l) {
				res.add((ReportHisto) ((Object[]) o)[0]);
			}
		}
		else {
			l = getHibernateTemplate().find("from ReportHisto histo");
			for (Object o : l) {
				res.add((ReportHisto)o);
			}
		}


		return res;
	}

	@SuppressWarnings("unchecked")
	public List<ReportBackground> getReportBackgrounds(int userId, int limit) {
		return (List<ReportBackground>) getHibernateTemplate().find("from ReportBackground where userId=" + userId + " ORDER BY creationDate DESC", limit);
	}

	public ReportBackground getReportBackground(int reportBackgroundId) {
		return (ReportBackground) getHibernateTemplate().find("from ReportBackground where id=" + reportBackgroundId).get(0);
	}
}
