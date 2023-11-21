package bpm.united.olap.runtime.cache;

import java.io.File;

public interface ICacheDiskConfiguration {
	public long getMaximumStorageSize();
//	public boolean getRebuildIndexAtStartup();
	public File getFolderCacheLocation();
}
