package bpm.vanilla.server.reporting.server.viewer;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.server.Server;
import bpm.vanilla.server.commons.server.commands.ServerCommand;
import bpm.vanilla.server.reporting.pool.JasperPoolableModel;

public class VanillaViewerJasperCommand extends ServerCommand {

	private static final long serialVersionUID = 4972117052910206499L;
	private Logger logger = Logger.getLogger(VanillaViewerJasperCommand.class);

	private IReportRuntimeConfig reportConfig;
	private User user;
	
	public VanillaViewerJasperCommand(Server server, IReportRuntimeConfig reportConfig, User user) {
		super(server);
		this.reportConfig = reportConfig;
	}

	public VanillaParameter getParameterValuesWithCascading(String paramName, List<VanillaParameter> parameters) throws Exception {
		logger.info("Start Getting Parameter For Cascading");
		// We create a new ReportParameter which are going to contains the
		// values
		VanillaParameter parameter = new VanillaParameter();

		PoolableModel<?> poolableModel = getPoolableModel();
		if (poolableModel instanceof JasperPoolableModel) {

		}
		else {
			String errMsg = poolableModel.getClass() + " is not recognised, aborting";
			logger.error(errMsg);
			throw new Exception(errMsg);
		}

		return parameter;
	}

	public List<VanillaGroupParameter> getReportParameters() throws Exception {
		logger.info("Start Getting Parameters");
		PoolableModel<?> poolableModel = getPoolableModel();
		logger.info("Got poolable model");

		List<VanillaParameter> birtParams = new ArrayList<VanillaParameter>();
		List<VanillaGroupParameter> birtGroupParams = new ArrayList<VanillaGroupParameter>();

		if (poolableModel instanceof JasperPoolableModel) {
			
			IRepositoryApi sock = new RemoteRepositoryApi(poolableModel.getItemKey().getRepositoryContext());
			List<Parameter> params = sock.getRepositoryService().getParameters(poolableModel.getDirectoryItem());
			
			if(params != null && !params.isEmpty()) {
				for(Parameter p : params) {
					VanillaParameter vp = new VanillaParameter();
					vp.setName(p.getName());
					vp.setDisplayName(p.getName());
					vp.setPromptText(p.getName());
					vp.setVanillaParameterId(p.getId());
					birtParams.add(vp);
				}
			}
			
		}
		else {
			String errMsg = poolableModel.getClass() + " is not recognised, aborting";
			logger.error(errMsg);
			throw new Exception(errMsg);
		}
		
		if(birtParams != null && !birtParams.isEmpty()) {
			VanillaGroupParameter noGroupParam = new VanillaGroupParameter();
			noGroupParam.setCascadingGroup(false);
			noGroupParam.setName("");
			noGroupParam.setParameters(birtParams);
			birtGroupParams.add(noGroupParam);
		}

		logger.info("End of Parameter Recuperation");
		return birtGroupParams;
	}

	protected PoolableModel<?> getPoolableModel() throws Exception {
		BaseVanillaContext vCtx = new BaseVanillaContext(getServer().getConfig().getVanillaUrl(), user.getLogin(), user.getPassword());
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);

		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(reportConfig.getObjectIdentifier().getRepositoryId());
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(reportConfig.getVanillaGroupId());

		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

		PoolableModel<?> poolModel = null;
		Object model = null;

		try {
			// every borrow MUST be released as soon as possible whatever can
			// happen
			poolModel = getServer().getPool().borrow(repCtx, reportConfig.getObjectIdentifier().getDirectoryItemId());
			model = poolModel.getModel();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (poolModel != null) {
				try {
					// every borrow MUST be released as soon as possible
					// whatever can happen
					getServer().getPool().returnObject(repCtx, reportConfig.getObjectIdentifier().getDirectoryItemId(), poolModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (model == null) {
			throw new Exception("error when interpreting message");
		}

		return poolModel;
	}
}
