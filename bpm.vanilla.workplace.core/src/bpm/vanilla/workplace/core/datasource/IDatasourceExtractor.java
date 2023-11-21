package bpm.vanilla.workplace.core.datasource;

import java.util.List;

import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;

public interface IDatasourceExtractor {

	public List<IDatasource> extractDatasources(String xml) throws Exception;

	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception;
}
