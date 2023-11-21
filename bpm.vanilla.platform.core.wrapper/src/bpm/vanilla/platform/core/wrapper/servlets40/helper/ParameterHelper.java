package bpm.vanilla.platform.core.wrapper.servlets40.helper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.components.VanillaParameterComponentImpl;
import bpm.vanilla.platform.core.wrapper.servlets40.VanillaParameterServlet;

/**
 * Used to be in {@link VanillaParameterServlet} until i needed it too, shared
 * here, ere
 * 
 * @author who careS?
 * 
 */
public class ParameterHelper {

	private static Toolkit getToolkit(User user, IRuntimeConfig config, IVanillaComponentProvider component) throws Exception {
		Repository repository = null;
		try {
			repository = component.getRepositoryManager().getRepositoryById(config.getObjectIdentifier().getRepositoryId());

		} catch (Exception ex) {
			throw new Exception("Unable to find Repository with id=" + repository.getId() + "-" + ex.getMessage(), ex);
		}
		if (repository == null) {
			throw new Exception("This Repository is not registered within VanillaPlatform");
		}

		Group group = component.getSecurityManager().getGroupById(config.getVanillaGroupId());
		IRepositoryApi repSock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user.getLogin(), user.getPassword()), group, repository));

		RepositoryItem repItem = null;
		try {
			repItem = repSock.getRepositoryService().getDirectoryItem(config.getObjectIdentifier().getDirectoryItemId());
		} catch (Exception ex) {
			throw new Exception("Error when looking for RepositoryItem " + config.getObjectIdentifier() + "-" + ex.getMessage(), ex);
		}
		if (repItem == null) {
			throw new Exception("RepositoryItem " + config.getObjectIdentifier() + " not found");
		}

		List<Parameter> repositoryParameters = repSock.getRepositoryService().getParameters(repItem);

		return new Toolkit(repItem, repositoryParameters, repSock.getContext());
	}

	public static List<VanillaGroupParameter> getDefinition(User user, IRuntimeConfig config, IVanillaComponentProvider component) throws Exception {
		Toolkit tk = getToolkit(user, config, component);

		// if we have a FWR or a BIRT, we delegate the execution to a
		// RuntimeServer if it exists
		if (tk.getDirectoryItem().getType() == IRepositoryApi.FWR_TYPE || (tk.getDirectoryItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE && tk.getDirectoryItem().getType() == IRepositoryApi.CUST_TYPE)) {

			try {
				IReportRuntimeConfig reportConfig = null;

				if (!(config instanceof IReportRuntimeConfig)) {
					throw new Exception("Parameter Definitions cannot be find on a Report Item if the IRunimeConfig is not an IReportRuntimeConfig instance.");
				}
				else {
					reportConfig = (IReportRuntimeConfig) config;
				}

				String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
				RemoteReportRuntime remoteReport = new RemoteReportRuntime(vanillaUrl, user.getLogin(), user.getPassword());
				return remoteReport.getReportParameters(user, reportConfig);
			} catch (Exception ex) {
				Logger.getLogger(ParameterHelper.class).warn("getting parameter on report failed - " + ex.getMessage(), ex);
			}
		}
		
		VanillaParameterComponentImpl paramManager = (VanillaParameterComponentImpl) component.getVanillaParameterComponent();
		return paramManager.getParameters(tk.getDirectoryItem(), tk.getItemParameters(), tk.getRepContext(), config);
	}

	public static List<VanillaGroupParameter> getDefinition(User user, IRuntimeConfig config, ByteArrayInputStream inputStream, IVanillaComponentProvider component) throws Exception {
		// if we have a FWR or a BIRT, we delegate the execution to a
		// RuntimeServer if it exists
		try {
			IReportRuntimeConfig reportConfig = null;

			if (!(config instanceof IReportRuntimeConfig)) {
				throw new Exception("Parameter Definitions cannot be find on a Report Item if the IRunimeConfig is not an IReportRuntimeConfig instance.");
			}
			else {
				reportConfig = (IReportRuntimeConfig) config;
			}

			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
			RemoteReportRuntime remoteReport = new RemoteReportRuntime(vanillaUrl, user.getLogin(), user.getPassword());
			return remoteReport.getReportParameters(user, reportConfig, inputStream);
		} catch (Exception ex) {
			Logger.getLogger(ParameterHelper.class).warn("getting parameter on report failed - " + ex.getMessage(), ex);
		}
		
		return new ArrayList<VanillaGroupParameter>();
	}

	public static VanillaParameter getValues(User user, IRuntimeConfig config, String parameterName, IVanillaComponentProvider component) throws Exception {

		Toolkit tk = getToolkit(user, config, component);

		// if we have a FWR or a BIRT, we delegate the execution to a
		// RuntimeServer if it exists
		if (tk.getDirectoryItem().getType() == IRepositoryApi.FWR_TYPE || (tk.getDirectoryItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE && tk.getDirectoryItem().getType() == IRepositoryApi.CUST_TYPE)) {

			try {
				IReportRuntimeConfig reportConfig = null;

				if (!(config instanceof IReportRuntimeConfig)) {
					throw new Exception("Parameter Values cannot be find on a Report Item if the IRunimeConfig is not an IReportRuntimeConfig instance.");
				}
				else {
					reportConfig = (IReportRuntimeConfig) config;
				}
				
				String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
				RemoteReportRuntime remoteReport = new RemoteReportRuntime(vanillaUrl, user.getLogin(), user.getPassword());
				
				VanillaGroupParameter groupParameter = null;
				for (VanillaGroupParameter gp : config.getParametersValues()) {
					for (VanillaParameter p : gp.getParameters()) {
						if (p.getName().equals(parameterName)) {
							groupParameter = gp;
							break;
						}
					}
				}

				return remoteReport.getReportParameterValues(user, reportConfig, parameterName, groupParameter.getParameters());
			} catch (Exception ex) {
				Logger.getLogger(ParameterHelper.class).warn("getting parameter Values on report failed - " + ex.getMessage(), ex);
			}

		}

		VanillaParameterComponentImpl paramManager = (VanillaParameterComponentImpl) component.getVanillaParameterComponent();
		return paramManager.refreshDatas(tk.getRepContext(), config, tk.getItemParameters(), parameterName);
	}

	public static VanillaParameter getValues(User user, IRuntimeConfig config, String parameterName, ByteArrayInputStream inputStream, IVanillaComponentProvider component) throws Exception {
		try {
			IReportRuntimeConfig reportConfig = null;

			if (!(config instanceof IReportRuntimeConfig)) {
				throw new Exception("Parameter Values cannot be find on a Report Item if the IRunimeConfig is not an IReportRuntimeConfig instance.");
			}
			else {
				reportConfig = (IReportRuntimeConfig) config;
			}
			
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
			RemoteReportRuntime remoteReport = new RemoteReportRuntime(vanillaUrl, user.getLogin(), user.getPassword());
			
			VanillaGroupParameter groupParameter = null;
			for (VanillaGroupParameter gp : config.getParametersValues()) {
				for (VanillaParameter p : gp.getParameters()) {
					if (p.getName().equals(parameterName)) {
						groupParameter = gp;
						break;
					}
				}
			}

			return remoteReport.getReportParameterValues(user, reportConfig, parameterName, groupParameter.getParameters(), inputStream);
		} catch (Exception ex) {
			Logger.getLogger(ParameterHelper.class).warn("getting parameter Values on report failed - " + ex.getMessage(), ex);
		}
		
		return null;
	}
}
