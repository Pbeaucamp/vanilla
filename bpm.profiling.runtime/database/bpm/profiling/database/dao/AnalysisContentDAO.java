package bpm.profiling.database.dao;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;

public class AnalysisContentDAO extends HibernateDaoSupport{

	public void add(AnalysisContentBean bean) {
		int id = (Integer)getHibernateTemplate().save(bean);
		bean.setId(id);
		
	}

	public void update(AnalysisContentBean bean) {
		
		
	}

	public List<AnalysisContentBean> getFor(AnalysisInfoBean analysisInfo) {
		return getHibernateTemplate().find("from AnalysisContentBean where analysisId=" + analysisInfo.getId());

	}

	public void delete(AnalysisContentBean bean) {
		getHibernateTemplate().delete(bean);
		
	}

	public AnalysisContentBean getById(int analysisContentId) {
		List<AnalysisContentBean> l = getHibernateTemplate().find("from AnalysisContentBean where id=" + analysisContentId);
		if (l.isEmpty()){
			return null;
		}
		
		return l.get(0);
	}

}
