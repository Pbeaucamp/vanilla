package bpm.vanilla.platform.core.runtime.alerts.actions;

import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;

public class AlertToGTW {

	private GatewayRuntimeConfiguration conf = null;
	private IVanillaContext vanillaContext;

	public AlertToGTW(IRepositoryContext repsitoryContext, IObjectIdentifier identifier, List<VanillaGroupParameter> parameters) {
		conf = new GatewayRuntimeConfiguration(identifier, parameters, repsitoryContext.getGroup().getId());
		this.vanillaContext = repsitoryContext.getVanillaContext();
	}

	public GatewayRuntimeState runGTW(boolean asynch) throws Exception {
		User user = new RemoteVanillaPlatform(vanillaContext).getVanillaSecurityManager().getUserByLogin(vanillaContext.getLogin());

		RemoteGatewayComponent remote = new RemoteGatewayComponent(vanillaContext);

		try {
			// XXX : not sure if must be asynch or waited to the end, should be
			// an option on the alert
			if(asynch) {
				IRunIdentifier id = remote.runGatewayAsynch(conf, user);
				if (id == null) {
					Logger.getLogger(getClass()).warn("Alert run GTW has not been executed for item " + conf.getObjectIdentifier().toString());
				}
				else {
					Logger.getLogger(getClass()).info("Alert run GTW has been launched with identifier  " + id.getKey());
				}
			}
			else {
				return remote.runGateway(conf, user);
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error("Failed to run gateway when performing alert action - " + ex.getMessage(), ex);
		}

		return null;
	}
}
