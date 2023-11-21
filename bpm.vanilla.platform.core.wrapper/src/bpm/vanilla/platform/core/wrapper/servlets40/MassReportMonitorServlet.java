package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IMassReportMonitor;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.WorkflowRunInstance;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class MassReportMonitorServlet extends HttpServlet{
	private IVanillaComponentProvider component;
	
	private XStream xstream;

	public MassReportMonitorServlet(IVanillaComponentProvider component){
		this.component = component;
	}
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream
					.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IMassReportMonitor.ActionType)) {
				throw new Exception("ActionType not a IMassReportMonitor");
			}

			IMassReportMonitor.ActionType type = (IMassReportMonitor.ActionType) action.getActionType();

			try {
				switch (type) {
				case SET_ASKED:
					setAsked(args);
					break;
				case SET_GENERATED:
					setGenerated(args);
					break;
				case GET_STATE:
					actionResult = getMassReportState(args);
					break;
				case DELETE:
					delete(args);
					break;
				case LIST: 
					actionResult = list();
					break;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			Logger.getLogger(getClass()).error(ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private void delete(XmlArgumentsHolder args) throws Exception{
		IMassReportMonitor monitor = component.getMassReportMonitor();
		monitor.delete((List<WorkflowRunInstance>)args.getArguments().get(0));
	}
	private Object list() throws Exception{
		IMassReportMonitor monitor = component.getMassReportMonitor();
		
		List<WorkflowRunInstance> l =  monitor.getWorklowsUsingMassReporting();
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		RemoteWorkflowComponent rem = new RemoteWorkflowComponent(conf.getVanillaServerUrl(), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN));
		for(WorkflowRunInstance i : l){
			try{
				i.setState(rem.getInfos(new SimpleRunIdentifier(i.getProcessInstanceUuid()), i.getWorkflowId().getDirectoryItemId(), i.getWorkflowId().getRepositoryId()));
			}catch(Exception ex){
				ex.printStackTrace();
			};
		}
		return l;
		
	}
	private Object getMassReportState(XmlArgumentsHolder args)  throws Exception{
		IMassReportMonitor monitor = component.getMassReportMonitor();
		IObjectIdentifier wkfId = (IObjectIdentifier)args.getArguments().get(0);
		IObjectIdentifier reportId = (IObjectIdentifier)args.getArguments().get(1);
		String workflowInstanceUuid = (String)args.getArguments().get(2);
		
		return monitor.getMassReportState(wkfId, reportId, workflowInstanceUuid);

	}

	private void setGenerated(XmlArgumentsHolder args) throws Exception{
		IMassReportMonitor monitor = component.getMassReportMonitor();
		IObjectIdentifier wkfId = (IObjectIdentifier)args.getArguments().get(0);
		IObjectIdentifier reportId = (IObjectIdentifier)args.getArguments().get(1);
		String workflowInstanceUuid = (String)args.getArguments().get(2);
		String activityInstanceUuid = (String)args.getArguments().get(3);
		
		monitor.setReportGenerated(wkfId, reportId, workflowInstanceUuid, activityInstanceUuid);
	}

	private void setAsked(XmlArgumentsHolder args) throws Exception{
		IMassReportMonitor monitor = component.getMassReportMonitor();
		IObjectIdentifier wkfId = (IObjectIdentifier)args.getArguments().get(0);
		IObjectIdentifier reportId = (IObjectIdentifier)args.getArguments().get(1);
		String workflowInstanceUuid = (String)args.getArguments().get(2);
		String activityInstanceUuid = (String)args.getArguments().get(3);
		
		monitor.setReportGenerationAsked(wkfId, reportId, workflowInstanceUuid, activityInstanceUuid);
		
	}

}
