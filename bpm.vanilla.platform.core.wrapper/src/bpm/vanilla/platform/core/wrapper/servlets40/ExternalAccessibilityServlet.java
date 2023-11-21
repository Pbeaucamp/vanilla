package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IExternalAccessibilityManager;
import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.ExternalAccessibilityManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * This is the servlet called for admin purposes (add an url, etc)
 * {@link ExternalCallObjectServlet} is the one for execution
 *
 */
public class ExternalAccessibilityServlet extends AbstractComponentServlet{
	
	public ExternalAccessibilityServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing ExternalAccessibilityServlet...");
		super.init();

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			
			
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IExternalAccessibilityManager.ActionType)){
				throw new Exception("ActionType not a IRepositoryManager");
			}
			
			IExternalAccessibilityManager.ActionType type = (IExternalAccessibilityManager.ActionType)action.getActionType();
			
			log(type, ((ExternalAccessibilityManager)component.getExternalAccessibilityManager()).getComponentName(), req);
			
			try{
				switch (type) {
				case ADD_FMDT:
					actionResult = addFmdtUrl(args);
					break;
				case ADD_PARAM:
					actionResult = addPublicParam(args);
					break;
				case CLEAR_URL_PARAM:
					clearAdressableParameters(args);
					break;
				case DEL_FMDT:
					delFmdtUrl(args);
					break;
				case DEL_URL:
					deletePublicUrl(args);
					break;
				case FIND_FMDT_4ITEM:
					actionResult = findFmdtUrl4Item(args);
					break;
				case FIND_FMDT_BY_NAME:
					actionResult = findFmdtUrlByName(args);
					break;
				case FIND_PARAM:
					actionResult = findPublicParameter(args);
					break;
				case FIND_URL:
					actionResult = findPublicUrl(args);
					break;
				case GET_URL_BY_ID:
					actionResult = getPublicUrlById(args);
					break;
				case LIST_URL_4ITEM:
					actionResult = getPublicUrlsByItemIdRepositoryId(args);
					break;
//				case GET_GRANTED_GROUPS:
//					actionResult = getAllowedGroups(args);
//					break;
//				case REMOVE_GROUP_ACCESS:
//					removeAccessForGroupId(args);
//					break;
				case SAVE_URL:
					User user = extractUser(req);
					actionResult = savePublicUrl(user, args);
					break;
				case UPDATE_FMDT:
					updateFmdtUrl(args);
					break;
				case CALL_CLASSIFICATION_KMEAN:
					actionResult = classificationKmean(args);
					break;
					
				case CALL_DECISIONTREE:
					actionResult = callRDecisionTree(args);
					break;
					
				case GET_PUBLIC_URLS:
					actionResult = component.getExternalAccessibilityManager().getPublicUrls((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1), (TypeURL)args.getArguments().get(2));
					break;
					
				}
			}catch(Exception ex){
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
				
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();	
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
	
//	private void removeAccessForGroupId(XmlArgumentsHolder args) throws Exception{
//		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("removeAccessForGroupId", int.class), args);
//		component.getExternalAccessibilityManager().removeAccessForGroupId((Integer)args.getArguments().get(0));
//
//	}

	private Object getPublicUrlsByItemIdRepositoryId(XmlArgumentsHolder args) throws Exception {
		return component.getExternalAccessibilityManager().getPublicUrlsByItemIdRepositoryId((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));
	}

	private Object getPublicUrlById(XmlArgumentsHolder args) throws Exception {
		return component.getExternalAccessibilityManager().getPublicUrlById((Integer)args.getArguments().get(0));
	}

	private void updateFmdtUrl(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("updateFmdtUrl", FmdtUrl.class), args);
		component.getExternalAccessibilityManager().updateFmdtUrl((FmdtUrl)args.getArguments().get(0));

		
	}
	
//	private Object getAllowedGroups(XmlArgumentsHolder args) throws Exception{
//		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("getGrantedGroupFor", int.class, int.class), args);
//		return component.getExternalAccessibilityManager().getGrantedGroupFor((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));
//
//	}

	private Object savePublicUrl(User user, XmlArgumentsHolder args) throws Exception{
		PublicUrl publicUrl = (PublicUrl)args.getArguments().get(0);
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("savePublicUrl", PublicUrl.class), args);
		return component.getExternalAccessibilityManager().savePublicUrl(publicUrl);

	}

	private Object findPublicUrl(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("getPublicUrlsByPublicKey", String.class), args);
		return component.getExternalAccessibilityManager().getPublicUrlsByPublicKey((String)args.getArguments().get(0));

	}

	private Object findPublicParameter(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("getParametersForPublicUrl", int.class), args);
		return component.getExternalAccessibilityManager().getParametersForPublicUrl((Integer)args.getArguments().get(0));

	}

	private Object findFmdtUrlByName(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("getFmdtUrlByName", String.class), args);
		return component.getExternalAccessibilityManager().getFmdtUrlByName((String)args.getArguments().get(0));

	}

	private Object findFmdtUrl4Item(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("getFmdtUrlForItemId", int.class), args);
		return component.getExternalAccessibilityManager().getFmdtUrlForItemId((Integer)args.getArguments().get(0));

	}

	private void deletePublicUrl(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("deletePublicUrl", int.class), args);
		component.getExternalAccessibilityManager().deletePublicUrl((Integer)args.getArguments().get(0));

	}

	private void delFmdtUrl(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("deleteFmdtUrl", FmdtUrl.class), args);
		component.getExternalAccessibilityManager().deleteFmdtUrl((FmdtUrl)args.getArguments().get(0));

		
	}

	private void clearAdressableParameters(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("deletePublicParameterForPublicUrlId", int.class), args);
		component.getExternalAccessibilityManager().deletePublicParameterForPublicUrlId((Integer)args.getArguments().get(0));

	}

	private Object addPublicParam(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("addPublicParameter", PublicParameter.class), args);
		return component.getExternalAccessibilityManager().addPublicParameter((PublicParameter)args.getArguments().get(0));
	}

	private Object addFmdtUrl(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("addFmdtUrl", FmdtUrl.class), args);
		return component.getExternalAccessibilityManager().addFmdtUrl((FmdtUrl)args.getArguments().get(0));

	}
	
	private Object classificationKmean(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("launchRFonctions", List.class, List.class, String.class, int.class), args);
		return component.getExternalAccessibilityManager().launchRFonctions((List<List<Double>>)args.getArguments().get(0), (List<List<Double>>)args.getArguments().get(1), (String)args.getArguments().get(2), (int)args.getArguments().get(3));
	}
	
	private Object callRDecisionTree(XmlArgumentsHolder args) throws Exception{
		argChecker.checkArguments(IExternalAccessibilityManager.class.getMethod("launchRDecisionTree", List.class, List.class, Double.class, List.class), args);
		return component.getExternalAccessibilityManager().launchRDecisionTree((List<List<Double>>)args.getArguments().get(0), (List<String>)args.getArguments().get(1), (Double)args.getArguments().get(2), (List<String>)args.getArguments().get(3));
	}
}
