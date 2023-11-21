package bpm.fd.runtime.engine;



import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;




/**
 * This class is used to generate the final JSP.
 * It represent the user Vanilla Context informations
 * They are used for the ComponentsReports
 * 
 * @author LCA
 *
 */
public class VanillaProfil {
	private String vanillaLogin;
	private String vanillaPassword;
//	private String vanillaGroup;
	private String vanillaUrl;
	private String repositoryUrl;
	private boolean passwordEncrypted = false;
	
	private String freeAnalysisWebUrl;
	private Integer repositoryId;
	
	private String vanillaRuntimeUrl;
	private Integer vanillaGroupId;
	private Integer directoryItemId;
	
//	private String vanillaRuntimeUrl;
	
	public VanillaProfil(IRepositoryContext repCtx, Integer directoryItemId) throws Exception{
		this.vanillaLogin = repCtx.getVanillaContext().getLogin();
		this.vanillaPassword = repCtx.getVanillaContext().getPassword();
		this.directoryItemId = directoryItemId;
//		this.vanillaGroup = repCtx.getGroup().getName();
		this.vanillaGroupId = repCtx.getGroup().getId();
		this.vanillaUrl = repCtx.getVanillaContext().getVanillaUrl();
		this.passwordEncrypted = repCtx.getVanillaContext().getPassword().matches("[0-9a-f]{32}");
		this.repositoryId = repCtx.getRepository().getId();
		this.repositoryUrl = repCtx.getRepository().getUrl();
		
		try{
			IVanillaAPI api = new RemoteVanillaPlatform(vanillaUrl, vanillaLogin, vanillaPassword);
			
			freeAnalysisWebUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);

		}catch(Exception ex){
			Logger.getLogger(getClass()).warn("Could not connect to Vanilla to get the OlapView provider Url");
//			throw new Exception("Unable to get FreeAnalysisWeb Url - " + ex.getMessage(), ex);
		}
	}
	
//	public VanillaProfil(String vanillaLogin, String vanillaPassword, String vanillaGroup, String vanillaUrl, String repositoryUrl, boolean passwordEncrypted) {
//		super();
//		this.vanillaLogin = vanillaLogin;
//		this.vanillaPassword = vanillaPassword;
//		this.vanillaGroup = vanillaGroup;
//		this.vanillaUrl = vanillaUrl;
//		this.repositoryUrl = repositoryUrl;
//		this.passwordEncrypted = passwordEncrypted;
//		
//		try{
//			IVanillaAPI api = new RemoteVanillaPlatform(vanillaUrl, vanillaLogin, vanillaPassword);
//			VanillaSetup setup = api.getVanillaSystemManager().getVanillaSetup();
//			freeAnalysisWebUrl = setup.getFreeAnalysisServer();
//			Repository repDef = api.getVanillaRepositoryManager().getRepositoryFromUrl(this.repositoryUrl);
//			if (repDef != null){
//				repositoryId = repDef.getId();
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		
//	}
//	//ere, taken out after merge 
//	public VanillaProfil(String vanillaLogin, String vanillaPassword, String vanillaGroup, String vanillaUrl, String repositoryUrl, boolean passwordEncrypted, Integer directoryItemId) {
//		this(vanillaLogin, vanillaPassword, vanillaGroup, vanillaUrl, repositoryUrl, passwordEncrypted);
//		this.directoryItemId = directoryItemId;
//	}

	public boolean isPasswordEncrypted(){
		return passwordEncrypted;
	}
	public String getVanillaLogin() {
		return vanillaLogin;
	}

	public String getVanillaPassword() {
		return vanillaPassword;
	}

//	public String getVanillaGroup() {
//		return vanillaGroup;
//	}

	public String getVanillaUrl() {
		return ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(vanillaUrl);
	}

	public String getRepositoryUrl() {
		return ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(repositoryUrl);
	}
	
	public boolean isFullyDefined(){
		return vanillaUrl != null && vanillaGroupId != null && vanillaLogin != null && vanillaPassword != null && repositoryUrl != null;
	}
	
	public String getFreeAnalysisWebUrl(){
		return ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(freeAnalysisWebUrl);
	}
	
	public Integer getRepositoryId(){
		return repositoryId;
	}


	public String getVanillaRuntimeUrl() {
		return ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(vanillaRuntimeUrl);
	}

	public Integer getVanillaGroupId() {
		return vanillaGroupId;
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}
	
}
