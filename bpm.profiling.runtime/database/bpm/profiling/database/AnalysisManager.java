package bpm.profiling.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import bpm.profiling.database.bean.AnalysisConditionResult;
import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.AnalysisResultBean;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.database.dao.AnalysisContentDAO;
import bpm.profiling.database.dao.AnalysisInfoDao;
import bpm.profiling.database.dao.AnalysisResultDao;
import bpm.profiling.database.dao.ConditionDao;
import bpm.profiling.database.dao.ConditionResultDao;
import bpm.profiling.database.dao.RuleSetDao;
import bpm.profiling.database.dao.TagDao;
import bpm.profiling.runtime.core.Column;
import bpm.profiling.runtime.core.Condition;
import bpm.profiling.runtime.core.Connection;


public class AnalysisManager {
	private AnalysisContentDAO contentDao;
	private AnalysisInfoDao infoDao;
	private AnalysisResultDao resultDao;
	private RuleSetDao ruleSetDao;
	private ConditionDao conditionDao;
	private ConditionResultDao conditionResultDao;
	private TagDao tagDao;
	
	
	/*
	 * spring getters/setters
	 */
	public TagDao getTagDao() {
		return tagDao;
	}
	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}
	public ConditionResultDao getConditionResultDao() {
		return conditionResultDao;
	}
	public void setConditionResultDao(ConditionResultDao conditionResultDao) {
		this.conditionResultDao = conditionResultDao;
	}
	public ConditionDao getConditionDao() {
		return conditionDao;
	}
	public void setConditionDao(ConditionDao conditionDao) {
		this.conditionDao = conditionDao;
	}
	public RuleSetDao getRuleSetDao() {
		return ruleSetDao;
	}
	public void setRuleSetDao(RuleSetDao ruleSetDao) {
		this.ruleSetDao = ruleSetDao;
	}
	public AnalysisContentDAO getContentDao() {
		return contentDao;
	}
	public void setContentDao(AnalysisContentDAO contentDao) {
		this.contentDao = contentDao;
	}
	public AnalysisInfoDao getInfoDao() {
		return infoDao;
	}
	public void setInfoDao(AnalysisInfoDao infoDao) {
		this.infoDao = infoDao;
	}
	public AnalysisResultDao getResultDao() {
		return resultDao;
	}
	public void setResultDao(AnalysisResultDao resultDao) {
		this.resultDao = resultDao;
	}
	
	/*
	 * utilities methods for Analysis
	 */
	
	public boolean createAnalysis(AnalysisInfoBean bean){
		try{
			infoDao.add(bean);
			return true;
		}catch(DataAccessException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateAnalysis(AnalysisInfoBean bean){
		try{
			infoDao.update(bean);
			return true;
		}catch(DataAccessException e){
			e.printStackTrace();
		}
		return false;	
	}
	
	public boolean deleteAnalysis(AnalysisInfoBean bean){
		
		for( AnalysisContentBean c : getAllAnalysisContentFor(bean)){
			deleteAnalysisContent(c);
		}
		
		infoDao.delete(bean);
		
		return true;	
	}
	
	public List<AnalysisInfoBean> getAllAnalysis(){
		return infoDao.getAll();
	}
	
	
	
	/*
	 * utilities methods for AnalysisContent
	 */
	
	public boolean createAnalysisContent(AnalysisContentBean bean){
		try{
			contentDao.add(bean);
			return true;
		}catch(DataAccessException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean updateAnalysisContent(AnalysisContentBean bean){
		try{
			contentDao.update(bean);
			return true;
		}catch(DataAccessException e){
			e.printStackTrace();
		}
		return false;	
	}
	
	public boolean deleteAnalysisContent(AnalysisContentBean bean){
		List<AnalysisResultBean> l = resultDao.getFor(bean);
		for(AnalysisResultBean r : l){
			resultDao.delete(r);
		}
		List<RuleSetBean> lr = getRuleSetsFor(bean);
		for(RuleSetBean rs  : lr){
			deleteRuleSet(rs);
		}
		

		
		try{
			contentDao.delete(bean);
			return true;
		}catch(DataAccessException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public void deleteRuleSet(RuleSetBean rs) {
		
		List<AnalysisConditionResult> l = conditionResultDao.getAll(); 
		for(AnalysisConditionResult r : l){
			if ( r.getRuleSetId() != null && rs.getId() == r.getRuleSetId()){
				try {
					conditionResultDao.delete(r);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		
		for(Condition c : getConditionForRuleSet(rs)){
			try {
				conditionDao.delete(c);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		ruleSetDao.delete(rs);
		
	}
	public List<AnalysisContentBean> getAllAnalysisContentFor(AnalysisInfoBean analysisInfo){
		return contentDao.getFor(analysisInfo);
	}
	
	
	/*
	 * utilities methods for AnalysisResults
	 */
	public boolean createAnalysisResult(AnalysisResultBean bean){
		try{
			resultDao.save(bean);
			return true;
		}catch(DataAccessException e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	
//	public boolean deleteAnalysisResult(AnalysisResultBean bean){
//		//TODO
//		return false;	
//	}
	
	public List<AnalysisResultBean> getAllAnalysisContentFor(AnalysisContentBean analysisContent){
		return resultDao.getFor(analysisContent);
	}
	
	public List<RuleSetBean> getRuleSetsFor(AnalysisContentBean content) {
		return ruleSetDao.getForContent(content);
	}
	
	public RuleSetBean getRuleSetFor(int id){
		return ruleSetDao.getById(id);
	}
	
	public List<RuleSetBean> getRuleSetsFor(Column col, AnalysisInfoBean infos) {
		
		
		/*
		 * looking for existing ruleSets
		 */
		AnalysisContentBean content = null;
		for(AnalysisContentBean a : getAllAnalysisContentFor(infos)){
			if (a.getColumnName().equals(col.getLabel())
					&&
				a.getTableName().equals(col.getTable().getLabel())){
				content = a;
				break;
			}
		}
		
		
		if (content == null){
			return new ArrayList<RuleSetBean>();
		}
		
		return ruleSetDao.getForContent(content);
	}
	public void createRuleSet(Column col, AnalysisInfoBean infos, RuleSetBean ruleSetBean) throws Exception{
		
		AnalysisContentBean content = null;
		for(AnalysisContentBean a : getAllAnalysisContentFor(infos)){
			if (a.getColumnName().equals(col.getLabel())
					&&
				a.getTableName().equals(col.getTable().getLabel())){
				content = a;
				break;
			}
		}
		if (content == null){
			throw new Exception("Cannot create a ruleSet on a Column if the have not been saved in the analysis before");
		}
		ruleSetBean.setAnalysisContentId(content.getId());
		
		try{
			ruleSetDao.add(ruleSetBean);
		}catch(Exception e){
			e.printStackTrace();
		}

		
	}
	
	public List<Condition> getConditionForRuleSet(RuleSetBean r) {
		return conditionDao.getAllFor(r);
	}
	public void createCondition(Condition c) throws Exception{
		conditionDao.add(c);
		
	}
	public void deleteCondition(Condition c) throws Exception{
		
		
		for(AnalysisConditionResult r : conditionResultDao.getAllFor(c)){
			deleteAnalysisConditionResult(r);
		}
		
		conditionDao.delete(c);
		
	}
	public void updateCondition(Condition condition) {
		conditionDao.update(condition);
		
	}
	
	public void createConditionResult(AnalysisConditionResult c) throws Exception{
		conditionResultDao.add(c);
		
	}
	public void deleteAnalysisConditionResult(AnalysisConditionResult c) throws Exception{
		conditionResultDao.delete(c);
		
	}
	public Condition getConditionForId(int conditionId) {
		return conditionDao.getById(conditionId);
	}
	public boolean deleteConnection(Connection connection) {
		if (connection == null){
			return false;
		}
		for(AnalysisInfoBean b : infoDao.getAllFor(connection)){
			deleteAnalysis(b);
		}
		
		return true;
		
	}
	public void updateRuleSet(RuleSetBean ruleSetBean) {
		try{
			ruleSetDao.update(ruleSetBean);
		}catch(Exception e){
			
		}
		
	}
	public void createTag(TagBean tag) throws Exception{
		tagDao.add(tag);
		
	}
	public AnalysisContentBean getAllAnalysisContent(int analysisContentId) {
		
		return contentDao.getById(analysisContentId);
	}
	public List<Date> getAnalysisDatesFor(AnalysisInfoBean parentElement) {
		List<Date> l = new ArrayList<Date>();
		for(AnalysisContentBean c : contentDao.getFor(parentElement)){
			for(AnalysisResultBean b : resultDao.getFor(c)){
				Date d = b.getCreation();
				
				boolean everAdded = false;
				for(Date e : l){
					if (e.equals(d)){
						everAdded = true;
					}
				}
				
				if (!everAdded){
					l.add(d);
				}
			}
		}
		
		
		return l;
	}
	public AnalysisResultBean getAnalysisResultsFor(
			AnalysisContentBean content, Date date) {
		return resultDao.getFor(content, date);
		
	}
	public List<AnalysisConditionResult>getConditionResultFor(int conditionId, Date date) {
		return conditionResultDao.getFor(conditionId, date);
		
	}
	public List<TagBean> getTagsFor(
			AnalysisResultBean analysisResultBean) {
		
		return tagDao.getAllForResult(analysisResultBean);
	}
	public List<TagBean> getTagsFor(AnalysisConditionResult v) {
		return tagDao.getAllForConditionResult(v);
	}
	public List<AnalysisConditionResult> getConditionResultFor(RuleSetBean rsB, Date date) {
		
		return conditionResultDao.getFor(rsB, date);
	}
	
	
	
}

