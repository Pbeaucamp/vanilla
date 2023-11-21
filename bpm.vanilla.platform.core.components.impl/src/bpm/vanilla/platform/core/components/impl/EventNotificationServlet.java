package bpm.vanilla.platform.core.components.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.components.IVanillaComponent;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class EventNotificationServlet extends HttpServlet{
	private XStream xstream;
	
	private IVanillaComponent component;
	public EventNotificationServlet(IVanillaComponent component){
		this.component = component;
	}
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();

	}
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();

			handleEvent(args);
			resp.getWriter().write(xstream.toXML(Boolean.TRUE));
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
	
	private void handleEvent(XmlArgumentsHolder args){
		for(Object o : args.getArguments()){
			if (o instanceof IVanillaEvent){
				component.notify((IVanillaEvent)o);

			}
		}
	}
}
