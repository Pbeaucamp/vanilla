package bpm.vanilla.workplace.core.disco;


public interface IDisconnectedReplacement {
	
	public String replaceElement(String xml, String datasourceName, String sqlLiteURL, DisconnectedBackupConnection backupConnection) throws Exception;
}
