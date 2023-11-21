package bpm.android.vanilla.wrapper.dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.android.vanilla.core.IAndroidConstant;
import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.IAndroidRepositoryManager.ActionType;
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.AndroidCubeView;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.core.beans.cube.HierarchyAndCol;
import bpm.android.vanilla.core.beans.cube.MeasureAndCol;
import bpm.android.vanilla.core.beans.report.AndroidDocumentDefinition;
import bpm.android.vanilla.core.xstream.IXmlActionType;
import bpm.android.vanilla.core.xstream.XmlAction;
import bpm.android.vanilla.core.xstream.XmlArgumentsHolder;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

import com.thoughtworks.xstream.XStream;

public class RepositoryDispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ComponentAndroidWrapper component;
	protected XStream xstream;

	public RepositoryDispatcherServlet(ComponentAndroidWrapper component) throws ServletException {
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
			
			IAndroidRepositoryManager manager = session.getRepositoryManager();
			
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IAndroidRepositoryManager");
			}
			
			ActionType type = (ActionType)action.getActionType();

			Object actionResult = null;
			try{
				switch (type) {
				case CHANGE_DIMENSIONS:
					actionResult = manager.changeDimensions((AndroidCube) args.getArguments().get(0), (List<HierarchyAndCol>) args.getArguments().get(1));
					break;
				case CHANGE_MEASURES:
					actionResult = manager.changeMeasures((AndroidCube) args.getArguments().get(0), (List<MeasureAndCol>) args.getArguments().get(1));
					break;
				case DRILL_CUBE:
					actionResult = manager.drillCube((String) args.getArguments().get(0));
					break;
				case GET_REPOSITORY_CONTENT:
					actionResult = manager.getRepositoryContent();
					break;
				case HIDE_NULL:
					actionResult = manager.hideNull();
					break;
				case INIT_CUBE:
					actionResult = manager.initCube((AndroidCube) args.getArguments().get(0));
					break;
				case LOAD_CUBE:
					actionResult = manager.loadCube((AndroidCube) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case LOAD_CUBE_VIEW:
					actionResult = manager.loadCubeView((AndroidCube) args.getArguments().get(0), (AndroidCubeView) args.getArguments().get(1));
					break;
				case SWAP_CUBE:
					actionResult = manager.swapCube();
					break;
				case EXECUTE_ITEM_PARAMETER:
					actionResult = manager.getParameterValues((AndroidItem) args.getArguments().get(0), (Parameter) args.getArguments().get(1));
					break;
				case GET_VIEW_ITEM:
					actionResult = manager.getItemLastView((AndroidItem) args.getArguments().get(0));
					break;
				case LOAD_DASHBOARD:
					actionResult = manager.getDashboardUrl((AndroidItem) args.getArguments().get(0));
					break;
				case LOAD_ITEM_IN_GED:
					actionResult = manager.loadItemInGed((AndroidDocumentDefinition) args.getArguments().get(0));
					break;
				case SEARCH_IN_GED:
					actionResult = manager.searchInGed((String) args.getArguments().get(0));
					break;
				case RUN_ITEM:
					actionResult = manager.runItem((AndroidItem) args.getArguments().get(0), (String) args.getArguments().get(1));
					break;
				case GENERATE_PACKAGE:
					InputStream reportStream = manager.generateDiscoPackage((Integer) args.getArguments().get(0));
					sendReport(reportStream, resp);
					return;
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

	private void sendReport(InputStream reportStream, HttpServletResponse response) throws Exception {
		try {
			OutputStream os = response.getOutputStream();
			int sz = 0;
			byte[] buf = new byte[1024];
			while ((sz = reportStream.read(buf)) >= 0) {
				os.write(buf, 0, sz);
			}

			reportStream.close();
			os.close();
		} catch (Exception ex) {
			throw new Exception("Error when writing the loaded Report from the ReportRuntime " + ex.getMessage(), ex);
		}

	}
}
