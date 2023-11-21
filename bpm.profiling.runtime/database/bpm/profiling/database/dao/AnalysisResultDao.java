package bpm.profiling.database.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisResultBean;

public class AnalysisResultDao extends HibernateDaoSupport {
	
	
	public void save(AnalysisResultBean bean){
		int id = (Integer)getHibernateTemplate().save(bean);
		bean.setId(id);
		
	}
	
	public void delete(AnalysisResultBean bean){
		getHibernateTemplate().delete(bean);
	}

	public List<AnalysisResultBean> getFor(AnalysisContentBean contentBean){
		return (List<AnalysisResultBean>)getHibernateTemplate().find("from AnalysisResultBean where analysisContentId=" + contentBean.getId());
	}

	public AnalysisResultBean getFor(AnalysisContentBean content, Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<AnalysisResultBean> r = getHibernateTemplate().find("from AnalysisResultBean where analysisContentId=" + content.getId() + " AND creation='" + sdf.format(date) + "'");
		
		if (r.isEmpty()){
			return null;
		}
		return r.get(0);
	}
	
}
