package bpm.vanilla.platform.core.wrapper.dispatcher;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaComponentListenerService;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.VanillaComponentIdentifier;
import bpm.vanilla.platform.core.listeners.event.impl.RepositoryItemEvent;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ServletDispatcher extends HttpServlet{
	
	
	
	private IVanillaLogger logger;
	private IVanillaComponentProvider component;
	private FactoryDispatcher factory;
	private VanillaHttpContext httpContext;
	private XStream xstream;
	
	public ServletDispatcher(VanillaHttpContext httpContext, IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
		this.httpContext = httpContext;
		factory =  new FactoryDispatcher(componentProvider);
		
		/*
		 * init xStream
		 */
		xstream = new XStream();
		
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		doPost(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		
		try{
			String componentTypeName = req.getHeader(IVanillaComponentIdentifier.P_COMPONENT_NATURE);
//			logger.debug("Received Dispatch request with type : " + componentTypeName);
			if (componentTypeName == null){
				throw new Exception("Missing the componentType parameter " + IVanillaComponentIdentifier.P_COMPONENT_NATURE);
			}
			
			IDispatcher dispatcher = factory.createDispatcher(componentTypeName, component);
			if (dispatcher == null){
				throw new Exception("The ComponentType " + componentTypeName + " is not supported by the VanillaServletDispatcher");
			}
			
			if (dispatcher.needAuthentication()){
				if (!httpContext.handleSecurity(req, resp)){
					throw new Exception("Bad authentication");
				}
			}
			
			try{
				dispatcher.dispatch(req, resp);
			}catch(VanillaException ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				xstream.toXML(ex, resp.getOutputStream());
				ex.printStackTrace();
			}catch(Exception ex){
				throw new Exception("Error when dispatching request - " + ex.getMessage(), ex);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		
	}
}
