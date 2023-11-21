package bpm.profiling.database.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.RuleSetBean;

public class RuleSetDao extends HibernateDaoSupport {

	public List<RuleSetBean> getAll(){
		return getHibernateTemplate().find("from RuleSetBean");
	}
	
	
	public void add(RuleSetBean q)throws DataAccessException{
		int id = (Integer)getHibernateTemplate().save(q);
		q.setId(id);
	}
	
	public void delete(RuleSetBean q){
		getHibernateTemplate().delete(q);
	
	}
	
	public void update(RuleSetBean q)throws DataAccessException{
		getHibernateTemplate().update(q);
	
	}


	public RuleSetBean getById(int i) {
		List<RuleSetBean> l = (List<RuleSetBean>) getHibernateTemplate().find("from RuleSetBean where id= " + i);
		
		if (l.size() > 0){
			return l.get(0);
		}
		
		return null;
	}


	public List<RuleSetBean> getForContent(AnalysisContentBean content) {
		return getHibernateTemplate().find("from RuleSetBean where analysisContentId= " + content.getId());
	}


	
}
