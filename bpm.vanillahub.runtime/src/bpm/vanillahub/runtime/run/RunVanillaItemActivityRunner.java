package bpm.vanillahub.runtime.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.RunVanillaItemActivity;
import bpm.vanillahub.core.beans.activities.attributes.VanillaItemParameter;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Result;

public class RunVanillaItemActivityRunner extends ActivityRunner<RunVanillaItemActivity> {

	private List<ApplicationServer> vanillaServers;

	public RunVanillaItemActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, RunVanillaItemActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<ApplicationServer> vanillaServers) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.vanillaServers = vanillaServers;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(vanillaServers);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(vanillaServers);
	}

	@Override
	protected void clearResources() {
	}

	@Override
	protected void run(Locale locale) {
		try {
			VanillaServer server = (VanillaServer) activity.getResource(vanillaServers);

			String url = server.getUrl(parameters, variables);
			String login = server.getLogin(parameters, variables);
			String password = server.getPassword(parameters, variables);

			List<VanillaItemParameter> parameters = activity.getParameters();

			BaseVanillaContext vctx = new BaseVanillaContext(url, login, password);
			IVanillaAPI api = new RemoteVanillaPlatform(vctx);

			Group group = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(server.getGroupId().getString(getParameters(), getVariables())));
			Repository repository = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(server.getRepositoryId().getString(getParameters(), getVariables())));

			IRepositoryContext ctx = new BaseRepositoryContext(vctx, group, repository);
			IRepositoryApi sock = new RemoteRepositoryApi(ctx);

			List<VanillaGroupParameter> params = new ArrayList<VanillaGroupParameter>();
			int i = 1;
			if (parameters != null && !parameters.isEmpty()) {
				for (VanillaItemParameter o : parameters) {
					VanillaGroupParameter g = new VanillaGroupParameter();
					g.setName("group " + i);
					i++;
					g.addParameter(new VanillaParameter(o.getName(), o.getValue().getString(this.parameters, variables)));
					params.add(g);

				}
			}
			ObjectIdentifier id = new ObjectIdentifier(sock.getContext().getRepository().getId(), activity.getItemId());

			RepositoryItem item = sock.getRepositoryService().getDirectoryItem(activity.getItemId());
			if (item.getType() == IRepositoryApi.GTW_TYPE) {
				GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration(id, params, sock.getContext().getGroup().getId());
				conf.setRuntimeUrl(sock.getContext().getVanillaContext().getVanillaUrl());

				try {
					GatewayRuntimeState state = new RemoteGatewayComponent(vctx).runGateway(conf, api.getVanillaSecurityManager().getUserByLogin(login));

					if (state.getState() == bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.ActivityState.ENDED) {
						clearResources();
						result.setResult(Result.SUCCESS);
					}
					else {
						addError(Labels.getLabel(locale, state.getFailureCause()));
						result.setResult(Result.ERROR);
					}

				} catch (Exception e) {
					e.printStackTrace();
					addError(e.getMessage());
					result.setResult(Result.ERROR);
				}
			}
			else if (item.getType() == IRepositoryApi.BIW_TYPE) {
				IRuntimeConfig conf = new RuntimeConfiguration(sock.getContext().getGroup().getId(), id, params);
				try {
					WorkflowService remote = new RemoteWorkflowComponent(vctx);
					IRunIdentifier identifier = remote.startWorkflow(conf);
					if (identifier != null) {
						TaskInfo taskInfos = remote.getTasksInfo(identifier);
						if (taskInfos != null && taskInfos.getResult() == ActivityResult.FAILED) {
							addError(Labels.getLabel(locale, taskInfos.getFailureCause()));
							result.setResult(Result.ERROR);
							return;
						}
					}

					clearResources();
					result.setResult(Result.SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					addError(e.getMessage());
					result.setResult(Result.ERROR);
				}
			}
			else {
				throw new Exception("Item type " + IRepositoryApi.TYPES_NAMES[item.getType()] + " is not supported yet.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			result.setResult(Result.ERROR);
		}
	}

}
