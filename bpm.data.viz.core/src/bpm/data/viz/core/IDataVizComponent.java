package bpm.data.viz.core;

import java.io.InputStream;
import java.util.List;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IDataVizComponent {
	
	public static final String DATA_VIZ_SERVLET = "/datavizServlet";

	public static enum ActionType implements IXmlActionType {
		SAVE_DATA_PREP(Level.DEBUG), GET_ALL_DATA_PREP(Level.DEBUG), DELETE_DATA_PREP(Level.DEBUG), EXECUTE_DATA_PREP(Level.DEBUG), 
		EXPORT_DATA_PREP(Level.DEBUG), CREATE_DATABASE(Level.DEBUG), ETL(Level.DEBUG), COUNT_DATA_PREP(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public DataPreparation saveDataPreparation(DataPreparation dataprep) throws Exception;
	
	public void deleteDataPreparation(DataPreparation dataprep) throws Exception;
	
	public List<DataPreparation> getDataPreparations() throws Exception;

	public DataPreparationResult executeDataPreparation(DataPreparation dataPrep) throws Exception;

	public Integer countDataPreparation(DataPreparation dataPrep) throws Exception;

	public InputStream exportDataPreparation(ExportPreparationInfo info) throws Exception;

	public String publicationETL(DataPreparation dataPrep) throws Exception;

	public void createDatabase(String tableName, DataPreparation dataPrep, boolean insert) throws Exception;
}
