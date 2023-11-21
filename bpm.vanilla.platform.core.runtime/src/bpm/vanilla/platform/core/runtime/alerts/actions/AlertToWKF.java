package bpm.vanilla.platform.core.runtime.alerts.actions;

import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;

public class AlertToWKF {

	private IVanillaContext vanillaCtx;
	private RuntimeConfiguration conf;

	public AlertToWKF(IRepositoryContext repsitoryContext, IObjectIdentifier identifier, List<VanillaGroupParameter> parameters) {
		conf = new RuntimeConfiguration(repsitoryContext.getGroup().getId(), identifier, parameters);

		this.vanillaCtx = repsitoryContext.getVanillaContext();
	}

	public void runWKF(boolean asynch) {
		RemoteWorkflowComponent remote = new RemoteWorkflowComponent(vanillaCtx);
		try {
			if (asynch) {
				IRunIdentifier id = remote.startWorkflowAsync(conf);
				Logger.getLogger(getClass()).info("Alert started a Workflow identified with identifier " + id.getKey());
			}
			else {
				IRunIdentifier id = remote.startWorkflow(conf);
				//TODO
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("Alert failed to start Workflow for identifier" + conf.getObjectIdentifier().toString() + " - " + ex.getMessage(), ex);
		}
	}
}
