package bpm.vanilla.platform.core.runtime.dao.logs;

import java.util.List;

import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmdtQueriesLoggerDao  extends HibernateDaoSupport{

	public boolean add(FMDTQueryBean bean) throws Exception{
		Long i = (Long)getHibernateTemplate().save(bean);
		bean.setId(i);
		return true;
	}

	public boolean delete(FMDTQueryBean bean) throws Exception {
		getHibernateTemplate().delete(bean);
		return true;
	}

	public List<FMDTQueryBean> list() throws Exception{
		return (List)getHibernateTemplate().find("from FMDTQueryBean ORDER BY date DESC", 0, 500);
//		return (List)getHibernateTemplate().find("from FMDTQueryBean ORDER BY date DESC");
	}

}
