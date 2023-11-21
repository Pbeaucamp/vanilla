package bpm.android.vanilla.wrapper.dispatcher;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.android.vanilla.core.IAndroidConstant;
import bpm.android.vanilla.core.IAndroidReportingManager;
import bpm.android.vanilla.core.IAndroidReportingManager.ActionType;
import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.xstream.IXmlActionType;
import bpm.android.vanilla.core.xstream.XmlAction;
import bpm.android.vanilla.core.xstream.XmlArgumentsHolder;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.FwrPrompt;

import com.thoughtworks.xstream.XStream;

public class ReportingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private ComponentAndroidWrapper component;
	protected XStream xstream;
	
	public ReportingServlet(ComponentAndroidWrapper component) throws ServletException {
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String sessionId = req.getHeader(IAndroidConstant.HTTP_HEADER_VANILLA_SESSION_ID);
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			
			if(session == null){
				throw new Exception("The session is not initialized. Please try to connect to a vanilla instance.");
			}
			
			IAndroidReportingManager manager = session.getReportingManager();
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IAndroidReportingManager");
			}
			
			ActionType type = (ActionType)action.getActionType();

			Object actionResult = null;
			try{
				switch (type) {
				case GET_ALL_METADATA:
					actionResult = manager.getAllMetadata();
					break;
				case LOAD_METADATA:
					actionResult = manager.loadMetadata((Integer) args.getArguments().get(0));
					break;
				case LOAD_REPORT:
					actionResult = manager.loadReport((Integer) args.getArguments().get(0));
					break;
				case LOAD_REPOSITORY:
					actionResult = manager.getRepositoryContent((AndroidRepository) args.getArguments().get(0));
					break;
				case PREVIEW_REPORT:
					actionResult = manager.previewReport((FWRReport) args.getArguments().get(0));
					break;
				case SAVE_REPORT:
					manager.saveReport((FWRReport) args.getArguments().get(0));
					break;
				case GET_PROMPTS_RESPONSE:
					actionResult = manager.getPromptsResponse((List<FwrPrompt>) args.getArguments().get(0));
					break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception e) {
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
