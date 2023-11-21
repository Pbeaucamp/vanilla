package bpm.profiling.database.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.database.bean.AnalysisConditionResult;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.runtime.core.Condition;

public class ConditionResultDao extends HibernateDaoSupport {
	public List<AnalysisConditionResult> getAll(){
		try{
			return getHibernateTemplate().find("from AnalysisConditionResult");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void add(AnalysisConditionResult q)throws Exception{
		int id = (Integer)getHibernateTemplate().save(q);
		q.setId(id);
	}
	
	public void delete(AnalysisConditionResult q)throws Exception{
		getHibernateTemplate().delete(q);
	
	}
	
	public void update(AnalysisConditionResult q){
		getHibernateTemplate().update(q);
	
	}

	public AnalysisConditionResult getById(Integer conditionId) {
		List<AnalysisConditionResult> l = getHibernateTemplate().find("from AnalysisConditionResult where id=" + conditionId);
		
		if (l.size() > 0){
			return l.get(0);
		}
		
		return null;
	}

	public List<AnalysisConditionResult> getAllFor(Condition r) {
		return getHibernateTemplate().find("from AnalysisConditionResult where conditionId=" + r.getId());
	}

	public List<AnalysisConditionResult> getFor(int conditionId, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List l =  getHibernateTemplate().find("from AnalysisConditionResult where conditionId=" + conditionId + " AND date='" + sdf.format(date) + "'");
//		return getHibernateTemplate().find("from AnalysisConditionResult where date='" + sdf.format(date) + "'");
		return l;
	}

	public List<AnalysisConditionResult> getFor(RuleSetBean rsB, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return getHibernateTemplate().find("from AnalysisConditionResult where ruleSetId=" + rsB.getId() + " AND date='" + sdf.format(date) + "'");
	}

	
	
}
