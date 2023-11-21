package bpm.united.olap.runtime.data.cache.disk;

import java.io.File;

import bpm.united.olap.runtime.cache.ICacheDiskConfiguration;

public class CacheDiskConfiguration implements ICacheDiskConfiguration{
	private File folder;
	private long maximumSize = 1024 * 1024 * 500 ;
	
	public CacheDiskConfiguration(String folderName){
		folder = new File(folderName);
		folder.mkdirs();
	}
	
	@Override
	public File getFolderCacheLocation() {
		return folder;
	}

	@Override
	public long getMaximumStorageSize() {
		return maximumSize;
	}

}
