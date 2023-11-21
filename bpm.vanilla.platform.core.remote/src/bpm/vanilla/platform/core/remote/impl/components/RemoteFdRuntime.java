package bpm.vanilla.platform.core.remote.impl.components;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.FreeDashboardComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.fd.IFdFormRuntimeConfig;
import bpm.vanilla.platform.core.remote.internal.ComponentComunicator;

public class RemoteFdRuntime implements FreeDashboardComponent{
	private ComponentComunicator communicator;
	private String vanillaUrl;
	
	public RemoteFdRuntime(String vanillaUrl, String login, String password){
		communicator = new ComponentComunicator(VanillaComponentType.COMPONENT_FREEDASHBOARD);
		communicator.init(login, password);
		//XXX ere bug fix, remove trailing '/' otherwise file not found.
		//lca : better to be here than in the methods.....
		if (vanillaUrl.endsWith("/")){
			this.vanillaUrl = vanillaUrl.substring(0, vanillaUrl.length() - 1);
		}
		else{
			this.vanillaUrl = vanillaUrl;
		}
			
	}
	
	@Override
	public String deployDashboard(IRuntimeConfig config) throws Exception {
		StringBuilder buf = new StringBuilder();

		buf.append(vanillaUrl);
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		
//		buf.append("_login=system");
//		buf.append("&_password=system");
		buf.append("_group=" + config.getVanillaGroupId());
		buf.append("&_id=" + config.getObjectIdentifier().getDirectoryItemId());
		buf.append("&_repurl=" + config.getObjectIdentifier().getRepositoryId());
		buf.append("&_pregeneration=" + false);
		try {
			if(config.getParametersValues() != null) {
				for(VanillaGroupParameter gp : config.getParametersValues()) {
					for(VanillaParameter p : gp.getParameters()) {
						buf.append("&" + p.getName() + "=" + p.getSelectedValues().get(0));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return communicator.sendMessage(buf.toString(), VanillaComponentType.COMPONENT_FREEDASHBOARD, FreeDashboardComponent.ACTION_DEPLOY);
	}

	@Override
	public String deployDashboard(IRuntimeConfig config, InputStream modelFiles) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(vanillaUrl);
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		
//		buf.append("_login=system");
//		buf.append("&_password=system");
		buf.append("_group=" + config.getVanillaGroupId());
		buf.append("&_repid=" + config.getObjectIdentifier().getRepositoryId());
		buf.append("&_pregeneration=false");
		
		InputStream is = communicator.sendMessage(buf.toString(), VanillaComponentType.COMPONENT_FREEDASHBOARD, FreeDashboardComponent.ACTION_DEPLOY_ZIP, modelFiles);
		return IOUtils.toString(is, "UTF-8");
	}

	@Override
	public String deployForm(IFdFormRuntimeConfig config) throws Exception {
		StringBuilder buf = new StringBuilder();

		
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		
//		buf.append("_login=system");
//		buf.append("&_password=system");
		buf.append("_group=" + config.getVanillaGroupId());
		buf.append("&_id=" + config.getObjectIdentifier().getDirectoryItemId());
		buf.append("&_repurl=" + config.getObjectIdentifier().getRepositoryId());
		buf.append("&_pregeneration=false");
		buf.append("&_submitUrl=" + URLEncoder.encode(config.getSubmissionUrl(), "UTF-8"));

		for(String s : config.getHiddenFields().keySet()){
			buf.append("&" + s + "=" + URLEncoder.encode(config.getHiddenFields().get(s), "UTF-8"));
		}
		
		return communicator.sendMessage(this.vanillaUrl + buf.toString(), 
				VanillaComponentType.COMPONENT_FREEDASHBOARD, 
				FreeDashboardComponent.ACTION_DEPLOY_FORM);

	}
	
	
	@Override
	public String deployValidationForm(IFdFormRuntimeConfig config) throws Exception {
		StringBuilder buf = new StringBuilder();

		
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		
//		buf.append("_login=system");
//		buf.append("&_password=system");
		buf.append("_group=" + config.getVanillaGroupId());
		buf.append("&_id=" + config.getObjectIdentifier().getDirectoryItemId());
		buf.append("&_repurl=" + config.getObjectIdentifier().getRepositoryId());
		buf.append("&_pregeneration=false");
		buf.append("&_submitUrl=" + URLEncoder.encode(config.getSubmissionUrl(), "UTF-8"));

		for(String s : config.getHiddenFields().keySet()){
			buf.append("&" + s + "=" + URLEncoder.encode(config.getHiddenFields().get(s), "UTF-8"));
		}
		
		return communicator.sendMessage(this.vanillaUrl + buf.toString(), 
				VanillaComponentType.COMPONENT_FREEDASHBOARD, 
				FreeDashboardComponent.ACTION_DEPLOY_VALIDATION_FORM);

	}

	@Override
	public InputStream runOdaReport(IRuntimeConfig config,
			InputStream odaInputModelDefinition) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(vanillaUrl);
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		return communicator.sendMessage(buf.toString(), VanillaComponentType.COMPONENT_FREEDASHBOARD, FreeDashboardComponent.ACTION_RUN_ODA_REPORT, odaInputModelDefinition);
	}
	
	@Override
	public String pregenerateDashboard(IRuntimeConfig config) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append(vanillaUrl);
		
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		
//		buf.append("_login=system");
//		buf.append("&_password=system");
		buf.append("_group=" + config.getVanillaGroupId());
		buf.append("&_id=" + config.getObjectIdentifier().getDirectoryItemId());
		buf.append("&_repurl=" + config.getObjectIdentifier().getRepositoryId());
		buf.append("&_pregeneration=true");

		
		return communicator.sendMessage(buf.toString(), VanillaComponentType.COMPONENT_FREEDASHBOARD, FreeDashboardComponent.ACTION_DEPLOY);

	}

	@Override
	public List<String> getFolderPages(String uid) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append(vanillaUrl);
		
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		buf.append("uuid=" + uid);
		buf.append("&_all=true");

		String res = communicator.sendMessage(buf.toString(), VanillaComponentType.COMPONENT_FREEDASHBOARD, FreeDashboardComponent.ACTION_GET_FOLDER_PAGES);
		
		List<String> folders = new ArrayList<String>();
		
		String[] pages = res.split(";");
		for(int i = 0 ; i < pages.length ; i++) {
			folders.add(pages[i]);
		}
		
		return folders;
	}

	@Override
	public String getActualFolderPage(String uid) throws Exception {
		StringBuilder buf = new StringBuilder();
		
		buf.append(vanillaUrl);
		
		buf.append(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET);
		buf.append("?");
		buf.append("uuid=" + uid);
		buf.append("&_all=false");
		
		return communicator.sendMessage(buf.toString(), VanillaComponentType.COMPONENT_FREEDASHBOARD, FreeDashboardComponent.ACTION_GET_ACTUAL_FOLDER_PAGE);
	}
}
