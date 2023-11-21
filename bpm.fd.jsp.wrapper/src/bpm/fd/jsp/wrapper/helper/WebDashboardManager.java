package bpm.fd.jsp.wrapper.helper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fd.core.Dashboard;
import bpm.fd.core.xstream.IDashboardManager;
import bpm.fd.runtime.engine.deployer.ProjectDeployer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class WebDashboardManager implements IDashboardManager {

	private IRepositoryApi repositoryApi;

	public WebDashboardManager(IRepositoryApi repositoryApi) {
		this.repositoryApi = repositoryApi;
	}
	
	@Override
	public String previewDashboard(Dashboard dashboard) throws Exception {
		FdProject project = new FdDashboardHelper().convertDashboard(dashboard);
		
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		if(!vanillaUrl.endsWith("/")) {
			vanillaUrl += "/";
		}
		
		IVanillaAPI api = new RemoteVanillaPlatform(vanillaUrl, ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		User user = api.getVanillaSecurityManager().getUserByLogin(repositoryApi.getContext().getVanillaContext().getLogin());
//		Group g = repositoryApi.getContext().getGroup();
		
		String url = "/generation/" + ProjectDeployer.deploy(new ObjectIdentifier(1, 0), repositoryApi.getContext(), user, project, "fr", true, new HashMap<String, String>());
		
		if (!url.contains("?")){
			url = url + "?";
		}
		
		if (vanillaUrl.endsWith("/")) {
			url = vanillaUrl.substring(0, vanillaUrl.length() - 1) + url;
		}
		else {
			url = vanillaUrl + url;
		}
		url = ConfigurationManager.getInstance().getVanillaConfiguration().translateClientUrlToServer(url);
		url = ConfigurationManager.getInstance().getVanillaConfiguration().translateUrlToExternalUrl(url);
		
		return url;
	}

	@Override
	public String getDefaultCssFile() throws Exception {
		String cssFilePath = ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_FILES);
		if(!cssFilePath.endsWith(File.separator)) {
			cssFilePath += "/";
		}
		
		cssFilePath += "VanillaDashboard/default_css.css";
		
		return new String(Files.readAllBytes(Paths.get(cssFilePath)));
	}

	@Override
	public Integer saveDashboard(RepositoryDirectory target, Dashboard dashboard, boolean update, List<Group> groups) throws Exception {
		FdProject project = new FdDashboardHelper().convertDashboard(dashboard);
		
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		if(!vanillaUrl.endsWith("/")) {
			vanillaUrl += "/";
		}
		
		//find element to update/add
		if(update) {
			
			String projectFolderPath = ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_FILES);
			if(!projectFolderPath.endsWith(File.separator)) {
				projectFolderPath += "/";
			}
			
			RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(dashboard.getItemId());
			
			projectFolderPath += "VanillaDashboard/" + item.getName() + "_tmp/";
			
			FdProject loadedProject = ModelLoader.loadProject(repositoryApi, item, projectFolderPath);
			
			new UpdateHelper().updateDashboard(project, groups, loadedProject, repositoryApi);
		
		}
		else {
			return ModelLoader.save(project, repositoryApi, target, groups, dashboard.getName(), dashboard.getName() + "_dictionary");
		}
		
		return 0;
	}

	@Override
	public Dashboard openDashboard(int itemId) throws Exception {
		
		String projectFolderPath = ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_FILES);
		if(!projectFolderPath.endsWith(File.separator)) {
			projectFolderPath += "/";
		}
		
		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(itemId);
		
		projectFolderPath += "VanillaDashboard/" + item.getName() + "/";
		
		FdProject project = ModelLoader.loadProject(repositoryApi, item, projectFolderPath);
		return new DashboardHelper().convertDashboard(project);
	}
	
	

}
