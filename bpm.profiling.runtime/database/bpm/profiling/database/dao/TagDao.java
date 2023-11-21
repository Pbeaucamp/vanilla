package bpm.profiling.database.dao;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.AnalysisConditionResult;
import bpm.profiling.database.bean.AnalysisResultBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.runtime.core.Condition;

public class TagDao extends HibernateDaoSupport {
	public List<TagBean> getAll(){
		return getHibernateTemplate().find("from TagBean");
	}
	
	public void add(TagBean q)throws Exception{
		
		int id = (Integer)getHibernateTemplate().save(q);
		q.setId(id);
	}
	
	public void delete(TagBean q)throws Exception{
		getHibernateTemplate().delete(q);
	
	}
	
	public void update(TagBean q){
		getHibernateTemplate().update(q);
	
	}

	public Condition getById(Integer tagId) {
		List<Condition> l = getHibernateTemplate().find("from TagBean where id=" + tagId);
		
		if (l.size() > 0){
			return l.get(0);
		}
		
		return null;
	}

	
	public List<TagBean> getAllForConditionResult(AnalysisConditionResult  r) {
		return getHibernateTemplate().find("from TagBean where resultConditionId=" + r.getId());
	}

	public List<TagBean> getAllForResult(AnalysisResultBean v) {
		return getHibernateTemplate().find("from TagBean where resultId=" + v.getId());
	}
	
}
