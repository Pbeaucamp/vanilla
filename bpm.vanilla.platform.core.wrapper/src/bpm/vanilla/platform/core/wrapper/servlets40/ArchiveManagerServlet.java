package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IArchiveManager;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ArchiveManagerServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;
	
	private IArchiveManager component;
	private XStream xstream = new XStream();
	
	public ArchiveManagerServlet(IVanillaComponentProvider component) {
		this.component = component.getArchiveManager();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IArchiveManager.ActionType)) {
				throw new Exception("ActionType not a Archive manager action");
			}
			
			IArchiveManager.ActionType type = (IArchiveManager.ActionType) action.getActionType();
			
			switch(type){
			case ADD_ARCHIVE_TYPE:
				actionResult = component.addArchiveType((ArchiveType) args.getArguments().get(0));
				break;
			case GET_ARCHIVE_TYPES:
				actionResult = component.getArchiveTypes();
				break;
			case UPDATE_ARCHIVE_TYPE:
				actionResult = component.updateArchiveType((ArchiveType) args.getArguments().get(0));
				break;
			case DELETE_ARCHIVE_TYPE:
				component.deleteArchiveType((ArchiveType) args.getArguments().get(0));
				break;
			case LINK_ITEM_ARCHIVE_TYPE:
				actionResult = component.linkItemArchiveType((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Boolean) args.getArguments().get(3));
				break;
			case GET_LINK_BY_ITEM:
				actionResult = component.getArchiveTypeByItem((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Boolean) args.getArguments().get(2));
				break;
			case GET_LINK_BY_ARCHIVE:
				actionResult = component.getArchiveTypeByArchive((Integer) args.getArguments().get(0));
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
