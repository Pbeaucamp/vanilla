package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * The implementor are meant to manage declared repositories within the Vanilla Platform.
 * replace : RepositoryManager, UserRepManager, UserRepositoriesManager
 * 
 * 
 * @author ludo
 *
 */
public interface IRepositoryManager {
	
	public static enum ActionType implements IXmlActionType{
		ADD_REP(Level.INFO), DEL_REP(Level.INFO), UPDATE_REP(Level.INFO), LIST_REP(Level.DEBUG), FIND_REP_ID(Level.DEBUG), FIND_REP_NAME(Level.DEBUG),
		ADD_USER_ACCESS(Level.INFO), DEL_USER_ACCESS(Level.INFO), FIND_USER_ACCESS(Level.DEBUG), FIND_USER_ACCESS_4_REP(Level.DEBUG), FIND_USER_ACCESS_4_USER(Level.DEBUG), FIND_USER_ACCESS_4_USER_NAME(Level.DEBUG),
		HAS_ACCESS(Level.DEBUG), FIND_REP_URL(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	/**
	 * create a new Reposiory within the Vanilla platform
	 * @param repository
	 * @throws Exception
	 */
	public void addRepository(Repository repository) throws Exception;
	
	/**
	 * remove the given Repository from Vanilla and the related UserRep
	 * @param repository
	 * @throws Exception
	 */
	public void deleteRepository(Repository repository) throws Exception;
	
	/**
	 * 
	 * @return all the defined Repositries within the DataBase
	 * @throws Exception
	 */
	public List<Repository> getRepositories() throws Exception;
	
	/**
	 * 
	 * @param repId
	 * @return the Repository with the given id
	 * @throws Exception
	 */
	public Repository getRepositoryById(int repId) throws Exception;
	
	public Repository getRepositoryByName(String repName) throws Exception;
	
	/**
	 * update the Repository definition
	 * @param repository
	 * @throws Exception
	 */
	public void updateRepository(Repository repository) throws Exception;
	
	
	
	
	
	public List<Repository> getUserRepositories(String userName) throws Exception;
	
	
	
	public void addUserRep(UserRep userRep) throws Exception;
	
	public void delUserRep(UserRep userRep) throws Exception;
	
	public List<UserRep> getUserRepByRepositoryId(int repositoryId) throws Exception;
	
	public List<UserRep> getUserRepByUserId(int userId) throws Exception;
	
	public boolean hasUserHaveAccess(int userId, int repositoryId) throws Exception;
	
	public UserRep getUserRepById(int id) throws Exception ;

	public Repository getRepositoryFromUrl(String repositoryUrl) throws Exception;
}
