package bpm.android.vanilla.wrapper.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import bpm.android.vanilla.core.IAndroidReportingManager;
import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.wrapper.reporting.AndroidReportingManager;
import bpm.android.vanilla.wrapper.reporting.AndroidRepositoryManager;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;

/**
 * Store the datas for a session.
 * 
 * When this session is used, a call to setUsedTime is required to update the object
 * and disallow the SessionHolder cleaner thread to remove this SessionContent.
 */
public class SessionContent {
	public static final long maxDurationSession = 1000 * 60 * 30;
	
	private IAndroidReportingManager reportingManager;
	private IAndroidRepositoryManager repositoryManager;
	
	private HashMap<Integer, RepositoryItem> metadata = new HashMap<Integer, RepositoryItem>();
	
	private String identifier;
	private Date lastUsedTime = Calendar.getInstance().getTime();
	
	private IRepositoryContext repositoryContext;
	private CubeInfos cubeInfos;
	private AndroidCube androidCube;
	
	public SessionContent(String identifier){
		this.identifier = identifier;
		reportingManager = new AndroidReportingManager(this);
		repositoryManager = new AndroidRepositoryManager(this);
	}
	
	public void setUsedTime(){
		synchronized (lastUsedTime) {
			lastUsedTime = Calendar.getInstance().getTime();
		}
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	public IAndroidReportingManager getReportingManager() {
		return reportingManager;
	}

	public IAndroidRepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	public boolean timeOutReached() {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		
		return currentTime > lastUsedTime.getTime() + maxDurationSession;
	}

	public void setMetadata(HashMap<Integer, RepositoryItem> metadata) {
		this.metadata = metadata;
	}
	
	public void addMetadata(int itemId, RepositoryItem item){
		if(metadata == null){
			metadata = new HashMap<Integer, RepositoryItem>();
		}
		metadata.put(itemId, item);
	}

	public HashMap<Integer, RepositoryItem> getAllMetadata() {
		return metadata;
	}
	
	public RepositoryItem getMetadata(int itemId) {
		return metadata.get(itemId);
	}
	
	public void setRepositoryContext(IRepositoryContext repositoryContext) {
		this.repositoryContext = repositoryContext;
	}
	
	public IRepositoryContext getRepositoryContext() {
		return repositoryContext;
	}

	public CubeInfos getCubeInfos() {
		return cubeInfos;
	}

	public void setCubeInfos(CubeInfos cubeInfos) {
		this.cubeInfos = cubeInfos;
	}

	public AndroidCube getAndroidCube() {
		return androidCube;
	}
	
	public void setAndroidCube(AndroidCube androidCube) {
		this.androidCube = androidCube;
	}
}
