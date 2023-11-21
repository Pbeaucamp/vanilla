package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IImageManager;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ImageManagerServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;
	
	private IImageManager component;
	private XStream xstream = new XStream();
	
	public ImageManagerServlet(IVanillaComponentProvider component) {
		this.component = component.getImageManager();
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

			if (!(action.getActionType() instanceof IImageManager.ActionType)) {
				throw new Exception("ActionType not a IVanillaWebServiceComponent");
			}
			
			IImageManager.ActionType type = (IImageManager.ActionType) action.getActionType();
			
			switch(type){
			case DOWNLOAD_IMAGE:
				actionResult = component.downloadImage((VanillaImage) args.getArguments().get(0));
				break;
			case GET_IMAGE:
				actionResult = component.getImage((Integer) args.getArguments().get(0));
				break;
			case GET_IMAGE_LIST:
				actionResult = component.getImageList((String) args.getArguments().get(0));
				break;
			case UPLOAD_IMAGE:
				actionResult = component.uploadImage(getInputStreamFromBytes((byte[]) args.getArguments().get(0)), (String) args.getArguments().get(1));
				break;
			case GET_IMAGE_BY_NAME:
				actionResult = component.getImageByName((String) args.getArguments().get(0));
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
	
	private InputStream getInputStreamFromBytes(byte[] bytes) {
		return new ByteArrayInputStream(Base64.decodeBase64(bytes));
	}

}
