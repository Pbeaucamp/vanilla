package bpm.profiling.database.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.runtime.core.Connection;

public class AnalysisInfoDao extends HibernateDaoSupport {

	public List<AnalysisInfoBean> getAll(){
		return getHibernateTemplate().find("from AnalysisInfoBean");
	}
	
	
	public void add(AnalysisInfoBean q)throws DataAccessException{
		int id = (Integer)getHibernateTemplate().save(q);
		q.setId(id);
	}
	
	public void delete(AnalysisInfoBean q){
		getHibernateTemplate().delete(q);
	
	}
	
	public void update(AnalysisInfoBean q)throws DataAccessException{
		getHibernateTemplate().update(q);
	
	}


	public AnalysisInfoBean getById(int i) {
		List<AnalysisInfoBean> l = (List<AnalysisInfoBean>) getHibernateTemplate().find("from QueryInfoBean where id= " + i);
		
		if (l.size() > 0){
			return l.get(0);
		}
		
		return null;
	}


	public List<AnalysisInfoBean> getAllFor(Connection connection) {
		return getHibernateTemplate().find("from AnalysisInfoBean where connectionId=" + connection.getId());
	}


	
}
