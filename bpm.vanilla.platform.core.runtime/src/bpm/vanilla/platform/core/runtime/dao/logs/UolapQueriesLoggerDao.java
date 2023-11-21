package bpm.vanilla.platform.core.runtime.dao.logs;

import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UolapQueriesLoggerDao  extends HibernateDaoSupport{

	public boolean add(UOlapQueryBean bean) {
		Long i = (Long)getHibernateTemplate().save(bean);
		bean.setId(i);
		return true;
	}

	public boolean delete(UOlapQueryBean bean) {
		getHibernateTemplate().delete(bean);
		return true;
	}

	public List<UOlapQueryBean> list() {
		
		return (List)getHibernateTemplate().find("from UOlapQueryBean");
	}

	public List<UOlapQueryBean> list(IObjectIdentifier identifier) {
		return (List)getHibernateTemplate().find("from UOlapQueryBean where repositoryId=" + identifier.getRepositoryId() + " and directoryItemId=" + identifier.getDirectoryItemId());
	}

	public List<UOlapQueryBean> listByGroupId(int groupId) {
		return (List)getHibernateTemplate().find("from UOlapQueryBean where vanillaGroupId=" + groupId);
	}

}
