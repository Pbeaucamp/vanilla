package bpm.vanilla.map.wrapper.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.kml.KmlColoringDatas;
import bpm.vanilla.map.model.kml.impl.KmlObject;
import bpm.vanilla.map.model.kml.impl.KmlSpecificationEntity;
import bpm.vanilla.map.wrapper.VanillaMapComponent;
import bpm.vanilla.map.wrapper.impl.KmlImplementor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.extended.EncodedByteArrayConverter;

public class KmlGenerationServlet extends HttpServlet{

	private XStream xstream;
	private VanillaMapComponent component;
	
	public KmlGenerationServlet(VanillaMapComponent component){
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
			case KML_GENERATE:
				actionResult = generate(args);
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
	
	private Object generate(XmlArgumentsHolder args) throws Exception{
		String originalKmlFile = (String)args.getArguments().get(0);
		KmlColoringDatas datas = (KmlColoringDatas)args.getArguments().get(1);
		
		
		
		
		return new KmlImplementor(component).generateKml(originalKmlFile, datas);
	}
	
	
}
