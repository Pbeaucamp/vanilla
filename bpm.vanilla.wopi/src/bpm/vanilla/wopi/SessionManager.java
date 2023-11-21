package bpm.vanilla.wopi;

import java.util.HashMap;

public class SessionManager {

	private static SessionManager instance;
	private IWopiManager vanillaManager;
	private IWopiManager aklaboxManager;
	private IWopiManager officeManager;
	
	private HashMap<String, WopiFileInfo> fileInfos = new HashMap<>();
	
	public static SessionManager getInstance() {
		if(instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}
	
	private SessionManager() {
		init(new VanillaWopiManager(), new AklaboxWopiManager(), new OfficeWopiManager());
	}
	
	public void init(IWopiManager vanillaManager, IWopiManager aklaboxManager, IWopiManager officeManager) {
		this.vanillaManager = vanillaManager;
		this.aklaboxManager = aklaboxManager;
		this.officeManager = officeManager;
	}
	
	public IWopiManager getVanillaManager() {
		return vanillaManager;
	}
	
	public IWopiManager getAklaboxManager() {
		return aklaboxManager;
	}
	
	public IWopiManager getOfficeManager() {
		return officeManager;
	}
	
	public void setFileInfo(String fileId, WopiFileInfo fileInfo) {
		fileInfos.put(fileId, fileInfo);
	}
	
	public WopiFileInfo getFileInfo(String fileId) {
		return fileInfos.get(fileId);
	}
}
