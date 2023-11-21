package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.ICommentService;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class CommentServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;
	
	private ICommentService component;
	private XStream xstream = new XStream();
	
	public CommentServlet(IVanillaComponentProvider component) {
		this.component = component.getCommentComponent();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof ICommentService.ActionType)) {
				throw new Exception("ActionType not a IVanillaWebServiceComponent");
			}
			
			ICommentService.ActionType type = (ICommentService.ActionType) action.getActionType();
			
			switch(type){
			case ADD_COMMENT_VALUE:
				component.addComment((Integer) args.getArguments().get(0), (CommentValue) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case ADD_COMMENT_VALUES:
				component.addComments((Validation) args.getArguments().get(0), (Integer) args.getArguments().get(1), (List<CommentValue>) args.getArguments().get(2), (Integer) args.getArguments().get(3));
				break;
			case MODIFY_COMMENT_VALUES:
				component.modifyComments((Validation) args.getArguments().get(0), (Integer) args.getArguments().get(1), (List<CommentValue>) args.getArguments().get(2), (Integer) args.getArguments().get(3), (Boolean) args.getArguments().get(4));
				break;
			case GET_COMMENT_DEFINITION:
				actionResult = component.getCommentDefinition((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (String) args.getArguments().get(2));
				break;
			case GET_COMMENTS:
				actionResult = component.getComments((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (String) args.getArguments().get(2), (List<CommentParameter>) args.getArguments().get(3));
				break;
			case GET_COMMENTS_FOR_USER:
				actionResult = component.getComments((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case GET_COMMENT_NOT_VALIDATE:
				actionResult = component.getCommentNotValidate((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case GET_COMMENTS_DEFINITION:
				actionResult = component.getCommentDefinitions((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case VALIDATE:
				component.validate((Validation) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
				break;
			case UNVALIDATE:
				component.unvalidate((Validation) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
				break;
			case STOP_VALIDATION_PROCESS:
				component.stopValidationProcess((Validation) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
				break;
			default:
				throw new Exception("Unknown action " + type.name());
			}
				
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getMessage());
		}
	}

}
