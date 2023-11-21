package bpm.update.manager.api.beans;

public class InstallationInformations {

	private String updateUrl;
	private int updatePort;
	private String updateLogin;
	private String updatePassword;

	private String managerUrl;
	private String managerLogin;
	private String managerPassword;

	private String runtimeInstallationPath;
	private String webappInstallationPath;
	private String savePath;
	private String historyPath;

//	private String runtimeUrl;
//	private String runtimeLogin;
//	private String runtimePassword;
	
	private boolean isUnix = false;
	private String restartCmdFile;

	public InstallationInformations(String updateUrl, int updatePort, String updateLogin, String updatePassword, String managerUrl, String managerLogin, 
			String managerPassword, String runtimeInstallationPath, String webappInstallationPath, String savePath, String historyPath, boolean isUnix, String restartCmdFile) {
		this.updateUrl = updateUrl;
		this.updatePort = updatePort;
		this.updateLogin = updateLogin;
		this.updatePassword = updatePassword;

		this.managerUrl = !managerUrl.endsWith("/") ? managerUrl + "/" : managerUrl;
		this.managerLogin = managerLogin;
		this.managerPassword = managerPassword;

		this.runtimeInstallationPath = runtimeInstallationPath.endsWith("/") ? runtimeInstallationPath : runtimeInstallationPath + "/";
		this.webappInstallationPath = webappInstallationPath.endsWith("/") ? webappInstallationPath : webappInstallationPath + "/";
		this.savePath = savePath.endsWith("/") ? savePath : savePath + "/";
		this.historyPath = historyPath.endsWith("/") ? historyPath : historyPath + "/";

//		this.runtimeUrl = runtimeUrl;
//		this.runtimeLogin = runtimeLogin;
//		this.runtimePassword = runtimePassword;
		
		this.isUnix = isUnix;
		this.restartCmdFile = restartCmdFile;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public int getUpdatePort() {
		return updatePort;
	}

	public String getUpdateLogin() {
		return updateLogin;
	}

	public String getUpdatePassword() {
		return updatePassword;
	}

	public String getManagerUrl() {
		return managerUrl;
	}

	public String getManagerLogin() {
		return managerLogin;
	}

	public String getManagerPassword() {
		return managerPassword;
	}

	public String getRuntimeInstallationPath() {
		return runtimeInstallationPath;
	}

	public String getWebappInstallationPath() {
		return webappInstallationPath;
	}

	public String getSavePath() {
		return savePath;
	}
	
	public String getHistoryPath() {
		return historyPath;
	}

//	public String getRuntimeUrl() {
//		return runtimeUrl;
//	}
//	
//	public String getRuntimeLogin() {
//		return runtimeLogin;
//	}
//	
//	public String getRuntimePassword() {
//		return runtimePassword;
//	}
	
	public boolean isUnix() {
		return isUnix;
	}
	
	public String getRestartCmdFile() {
		return restartCmdFile;
	}
}
