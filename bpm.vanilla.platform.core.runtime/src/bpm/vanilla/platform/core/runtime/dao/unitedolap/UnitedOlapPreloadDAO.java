package bpm.vanilla.platform.core.runtime.dao.unitedolap;

import java.util.List;

import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UnitedOlapPreloadDAO extends HibernateDaoSupport{

	public void save(UOlapPreloadBean bean)throws Exception {
		getHibernateTemplate().save(bean);
		
	}

	public void delete(UOlapPreloadBean bean)throws Exception {
		getHibernateTemplate().delete(bean);
		
	}

	public List<UOlapPreloadBean> list(int repositoryId, int directoryItemId) {
		return getHibernateTemplate().find("from UOlapPreloadBean where repositoryId=" + repositoryId + " and directoryItemId=" + directoryItemId);
	}

	public List<UOlapPreloadBean> listByGroupId(int groupId) {
		return getHibernateTemplate().find("from UOlapPreloadBean where vanillaGroupId=" + groupId);
	}

}
