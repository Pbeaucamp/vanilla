package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class VanillaWebServiceServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = -6527305511352253240L;

	private IVanillaComponentProvider component;

	private IVanillaLogger logger;
	
	public VanillaWebServiceServlet(IVanillaComponentProvider component, IVanillaLogger logger) {
		this.logger = logger;
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaWebServiceServlet...");
		super.init();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IVanillaWebServiceComponent.ActionType)) {
				throw new Exception("ActionType not a IVanillaWebServiceComponent");
			}

			IVanillaWebServiceComponent.ActionType type = (IVanillaWebServiceComponent.ActionType) action.getActionType();
			
			try {
				switch (type) {
					case ADD_WEBSERVICE_DEFINITION:
						ServiceTransformationDefinition definition = (ServiceTransformationDefinition) args.getArguments().get(0);
						component.getVanillaWebServiceComponent().addWebServiceDefinition(definition);
						break;
					case DELETE_WEBSERVICE_DEFINITION:
						int id = -1;
						try {
							ServiceTransformationDefinition toRm = (ServiceTransformationDefinition) args.getArguments().get(0);
							id = toRm.getId();
						} catch(Exception e) {
							id = (Integer) args.getArguments().get(0);
						}
						component.getVanillaWebServiceComponent().deleteWebServiceDefinition(id);
						break;
					case GET_WEBSERVICE_ALLDEFINTIONS:
						actionResult = component.getVanillaWebServiceComponent().getWebServiceDefinitions();
						break;
					case GET_WEBSERVICE_DEFINITION:
						int idToGet = (Integer) args.getArguments().get(0);
						actionResult = component.getVanillaWebServiceComponent().getWebServiceDefinition(idToGet);
						break;
					case UPDATE_WEBSERVICE_DEFINITION:
						ServiceTransformationDefinition toUpdate = (ServiceTransformationDefinition) args.getArguments().get(0);
						component.getVanillaWebServiceComponent().updateWebServiceDefinition(toUpdate);
						break;
				}
			} catch (Exception ex) {
				logger.error(ex);
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}
}
