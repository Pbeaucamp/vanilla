package bpm.united.olap.api.runtime.model;

import java.util.List;

import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.IRuntimeContext;

public interface HierarchyExtractor {
	
	public Hierarchy getHierarchy();
	
	
	
	public Member getMember(String uniqueName, IRuntimeContext runtimeContext) throws Exception;
	
	public List<Member> getChilds(Member parent, IRuntimeContext runtimeContext) throws Exception;
	
	public Member getRootMember();

	
	/**
	 * return all the uname from the hierarchy having a uname that contains word
	 * if the member has not its childs loaded, there will be
	 * @param word
	 * @param level : if not null, the lookup wont go after the given level uniqueName
	 * @return
	 */
	public List<String> getMembersWith(String word, String level, IRuntimeContext runtimeContext) throws Exception;

	boolean validate(Member member, Object factForeigNkey, IRuntimeContext runtimeContext) throws Exception;
	
	/**
	 * Get the member name for a given foreignKey
	 * @param factForeignKey
	 * @param levelIndex
	 * @return
	 * @throws Exception 
	 */
	public String getMemberName(Object factForeignKey, int levelIndex, IRuntimeContext runtimeContext) throws Exception;

	public String getMemberOrderValue(Object factForeignKey, int levelIndex, IRuntimeContext runtimeContext) throws Exception;

	public void clearLevelDatasInMemory();

	/**
	 * Preload levelDatas from currentLevel to lastLevel
	 * @param currentLevel
	 * @param lastLevel
	 * @throws Exception 
	 */
	public void preloadLevelDatas(int currentLevel, int lastLevel, IRuntimeContext ctx) throws Exception;

	/**
	 * Return the list of foreign keys corresponding to this member
	 * @param elem
	 * @param runtimeContext 
	 * @return
	 * @throws Exception 
	 */
	public List<Object> getMemberForeignKeys(Member elem, IRuntimeContext runtimeContext) throws Exception;

//	/**
//	 * return the Member matching to the given foreign key
//	 * @param foreignKey
//	 * @param runtimeContext
//	 * @return
//	 * @throws Exception
//	 */
//	public Member getMemberByForeignKey(Object foreignKey, IRuntimeContext runtimeContext, int levelNumber) throws Exception;
	
	/**
	 * Get back all possible foreign keys for the hierarchy
	 * @param runtimeContext
	 * @return
	 * @throws Exception 
	 */
	public List<Object> getAllForeignKeys(IRuntimeContext runtimeContext) throws Exception;


	/**
	 * Get all members for a level
	 * @param lookedLevel
	 * @param runtimeCtx
	 * @return
	 * @throws Exception 
	 */
	public List<Member> getLevelMembers(Level lookedLevel, IRuntimeContext runtimeCtx) throws Exception;
}
