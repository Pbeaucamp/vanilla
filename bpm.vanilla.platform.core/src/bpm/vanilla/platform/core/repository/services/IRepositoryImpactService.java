package bpm.vanilla.platform.core.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IRepositoryImpactService {
	public static enum ActionType implements IXmlActionType{
		CREATE_DATASOURCE(Level.DEBUG), DELETE_DATASOURCE(Level.DEBUG), LIST_DATASOURCE(Level.DEBUG), FIND_DATASOURCE(Level.DEBUG), 
		GET_DATASOURCE_IMPACTS(Level.DEBUG), UPDATE_DATASOURCE(Level.DEBUG), FIND_DATASOURCE_BY_TYPE(Level.DEBUG);
		
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
	 * 
	 * @param d
	 * @return the generated of the added dataSource
	 * @throws Exception
	 */
	public int add(DataSource d) throws Exception;
	public boolean del(DataSource d) throws Exception ;
	public List<DataSource> getAllDatasources() throws Exception;
	public DataSource getById(int id) throws Exception ;
	public void update(DataSource d) throws Exception;
	public List<DataSourceImpact> getForDataSourceId(int dataSourceId) throws Exception;
	public List<DataSource> getDatasourcesByType(String extensionId) throws Exception;
}
