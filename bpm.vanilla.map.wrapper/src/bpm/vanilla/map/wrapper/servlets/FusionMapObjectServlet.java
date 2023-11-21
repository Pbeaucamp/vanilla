package bpm.vanilla.map.wrapper.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.model.fusionmap.impl.FusionMapObject;
import bpm.vanilla.map.model.fusionmap.impl.FusionMapSpecificationEntity;
import bpm.vanilla.map.wrapper.VanillaMapComponent;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;

public class FusionMapObjectServlet extends HttpServlet{

	private XStream xstream;
	
	private VanillaMapComponent component;

	
	public FusionMapObjectServlet( VanillaMapComponent component){
		this.component = component;
	}

	@Override
	public void init() throws ServletException {
		xstream = new XStream();
		xstream.registerConverter((SingleValueConverter)new EncodedByteArrayConverter());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			
			Object actionResult = null;
			
			switch(action.getActionType()){
			case FUSION_MAP_DELETE:
				delete(args);
				break;
			case FUSION_MAP_GET:
				actionResult = getFusionMap(args);
				break;
			case FUSION_MAP_SAVE:
				actionResult = save(args);
				break;
			
			default:
				throw new Exception("Unknown action " + action.getActionType().name());
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, ex.getMessage());
			component.getLogger().error("An error occured", ex);

		}
	}
	
	private Object save(XmlArgumentsHolder args) throws Exception{
		IFusionMapObject fusionMap = (IFusionMapObject)args.getArguments().get(0);
		
		Object o = args.getArguments().get(1);
		
		return this.component.getFusionMapRegistry().addFusionMapObject(fusionMap, new ByteArrayInputStream((byte[])o));
	}
	
	private void delete(XmlArgumentsHolder args) throws Exception{
		Object o = args.getArguments().get(0);
		
		if (o instanceof Integer){
			this.component.getFusionMapRegistry().removeFusionMapObject((Integer)o);
		}
		else if (o instanceof IFusionMapObject){
			this.component.getFusionMapRegistry().removeFusionMapObject((IFusionMapObject)o);
		}
		else{
			throw new Exception("Cannot delete this type of object " + o.getClass().getName());
		}
		
	}
	
	private Object getFusionMap(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return this.component.getFusionMapRegistry().getFusionMapObjects();
		}
		else if(args.getArguments().get(0) instanceof String) {
			return this.component.getFusionMapRegistry().getFusionMapObjects((String)args.getArguments().get(0));
		}
		else {
			return this.component.getFusionMapRegistry().getFusionMapObject((Long)args.getArguments().get(0));
		}
	}
}
