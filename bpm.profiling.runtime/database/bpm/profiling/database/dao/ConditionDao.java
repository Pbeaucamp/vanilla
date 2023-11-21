package bpm.profiling.database.dao;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.runtime.core.Condition;

public class ConditionDao extends HibernateDaoSupport {
	public List<Condition> getAll(){
		return getHibernateTemplate().find("from Condition");
	}
	
	public void add(Condition q)throws Exception{
		int id = (Integer)getHibernateTemplate().save(q);
		q.setId(id);
	}
	
	public void delete(Condition q)throws Exception{
		getHibernateTemplate().delete(q);
	
	}
	
	public void update(Condition q){
		getHibernateTemplate().update(q);
	
	}

	public Condition getById(Integer conditionId) {
		List<Condition> l = getHibernateTemplate().find("from Condition where id=" + conditionId);
		
		if (l.size() > 0){
			return l.get(0);
		}
		
		return null;
	}

	public List<Condition> getAllFor(RuleSetBean r) {
		return getHibernateTemplate().find("from Condition where ruleSetId=" + r.getId());
	}

	
	
}
