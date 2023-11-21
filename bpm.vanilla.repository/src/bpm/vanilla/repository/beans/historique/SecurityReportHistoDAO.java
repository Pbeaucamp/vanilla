package bpm.vanilla.repository.beans.historique;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SecurityReportHistoDAO extends HibernateDaoSupport {

	public List<SecurityReportHisto> getAll() {
		return getHibernateTemplate().find("from SecurityReportHisto");
	}

	public int save(SecurityReportHisto d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(SecurityReportHisto d) {
		getHibernateTemplate().delete(d);
	}

	public void update(SecurityReportHisto d) {
		getHibernateTemplate().update(d);
	}

	public List<SecurityReportHisto> getForItemId(int dirItemId) {
		return getHibernateTemplate().find("from SecurityReportHisto where directoryItemId=" + dirItemId);
	}

	public List<SecurityReportHisto> getForItemId(int directoryItemId, int groupId) {
		return getHibernateTemplate().find("from SecurityReportHisto where directoryItemId=" + directoryItemId + " and groupId=" + groupId);
	}

	public List<SecurityReportHisto> getForGroupId(int groupId) {
		return getHibernateTemplate().find("from SecurityReportHisto where groupId=" + groupId);
	}

}
