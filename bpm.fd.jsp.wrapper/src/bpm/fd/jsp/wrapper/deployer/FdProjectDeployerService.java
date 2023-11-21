package bpm.fd.jsp.wrapper.deployer;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fd.runtime.engine.GenerationContext;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.deployer.ProjectDeployer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FdProjectDeployerService {

	/**
	 * generate all deployed files in the location Folder
	 * 
	 * @param location
	 * @return
	 * @throws Exception
	 */
	public String deploy(int directoryItemId, IRepositoryContext repCtx, String location, String localeLanguage, HashMap<String, String> parametersFromUrl) throws Exception{

		IVanillaAPI api = new RemoteVanillaPlatform(repCtx.getVanillaContext());
		User user = api.getVanillaSecurityManager().authentify("", repCtx.getVanillaContext().getLogin(), repCtx.getVanillaContext().getPassword(), false);

		IRepositoryApi sock = new RemoteRepositoryApi(repCtx);

		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(directoryItemId);
		if (item == null) {
			throw new VanillaException("Could not find the model for the User " + repCtx.getVanillaContext().getLogin());
		}
		if (!item.isOn()) {
			throw new VanillaException("The Item has been turned off from EnterpriseServices.");
		}

		String deploymentPrefix = repCtx.getRepository().getId() + "";
		try {

			VanillaProfil profil = new VanillaProfil(repCtx, directoryItemId);

			FdProject project = null;
			try {
				File f = new File(Platform.getLocation().toOSString() + "/tmp");
				if (!f.exists()) {
					f.mkdirs();
				}

				if (!sock.getAdminService().canRun(item.getId(), repCtx.getGroup().getId())) {
					if (!user.isSuperUser()) {
						throw new Exception("The FreeDashboard model (directoryItem id = " + item.getId() + ") cannot be run by the group " + profil.getVanillaGroupId() + " and the user is not a superUser");
					}
				}
				project = ModelLoader.loadProjectFromVanilla(sock, repCtx.getGroup().getName(), item, f.getPath());

				deploymentPrefix = deploymentPrefix + "_" + item.getId();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("error when importing freedasboard project: " + e.getMessage(), e);
			}

			try {

				return ProjectDeployer.deploy(new ObjectIdentifier(repCtx.getRepository().getId(), directoryItemId), repCtx, user, project, localeLanguage, false, parametersFromUrl);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("error when deploying freedasboard project: " + e.getMessage(), e);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String deployVanillaForm(boolean isValidation, int directoryItemId, IRepositoryContext repCtx, String location, String localeLanguage, HashMap<String, String> hiddenFields) throws Exception {
		IVanillaAPI api = new RemoteVanillaPlatform(repCtx.getVanillaContext());
		User user = api.getVanillaSecurityManager().authentify("", repCtx.getVanillaContext().getLogin(), repCtx.getVanillaContext().getPassword(), false);

		IRepositoryApi sock = new RemoteRepositoryApi(repCtx);

		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(directoryItemId);
		if (item == null) {
			throw new VanillaException("Could not find the model for the User " + repCtx.getVanillaContext().getLogin());
		}
		if (!item.isOn()) {
			throw new VanillaException("The Item has been turned off from EnterpriseServices.");
		}

		String deploymentPrefix = repCtx.getRepository().getId() + "";
		try {
			VanillaProfil profil = new VanillaProfil(repCtx, directoryItemId);

			FdProject project = null;
			try {
				File f = new File(Platform.getLocation().toOSString() + "/tmp");
				if (!f.exists()) {
					f.mkdirs();
				}

				if (!sock.getAdminService().canRun(item.getId(), repCtx.getGroup().getId())) {
					if (!user.isSuperUser()) {
						throw new Exception("The FreeDashboard model (directoryItem id = " + item.getId() + ") cannot be run by the group " + profil.getVanillaGroupId() + " and the user is not a superUser");
					}

				}
				project = ModelLoader.loadProjectFromVanilla(sock, repCtx.getGroup().getName(), item, f.getPath());

				deploymentPrefix = deploymentPrefix + "_" + item.getId();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("error when importing freedasboard project: " + e.getMessage(), e);
			}

			try {

				return ProjectDeployer.deployForm(isValidation, new ObjectIdentifier(repCtx.getRepository().getId(), directoryItemId), repCtx.getGroup(), user, project, localeLanguage, false, hiddenFields);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("error when deploying freedasboard project: " + e.getMessage(), e);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
