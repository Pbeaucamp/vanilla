package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.VanillaParameterComponentImpl;
import bpm.vanilla.platform.core.wrapper.servlets40.helper.ParameterHelper;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

@SuppressWarnings("serial")
public class VanillaParameterServlet extends AbstractComponentServlet {

	public VanillaParameterServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger) {
		this.logger = logger;
		this.component = componentProvider;
	}

	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaParameterServlet...");
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof VanillaParameterComponent.ActionType)) {
				throw new Exception("ActionType not a IRepositoryManager");
			}

			VanillaParameterComponent.ActionType type = (VanillaParameterComponent.ActionType) action.getActionType();

			log(type, ((VanillaParameterComponentImpl) component.getVanillaParameterComponent()).getComponentName(), req);

			try {
				switch (type) {
				case PARAMETERS_DEFINITION:
					actionResult = ParameterHelper.getDefinition(extractUser(req), (IRuntimeConfig) args.getArguments().get(0), component);
					break;
				case PARAMETER_VALUES:
					actionResult = ParameterHelper.getValues(extractUser(req), (IRuntimeConfig) args.getArguments().get(0), (String) args.getArguments().get(1), component);
					break;
				case PARAMETERS_DEFINITION_FROM_MODEL:
					actionResult = ParameterHelper.getDefinition(extractUser(req), (IRuntimeConfig) args.getArguments().get(0), new ByteArrayInputStream((byte[]) args.getArguments().get(1)), component);
					break;
				case PARAMETER_VALUES_FROM_MODEL:
					actionResult = ParameterHelper.getValues(extractUser(req), (IRuntimeConfig) args.getArguments().get(0), (String) args.getArguments().get(1), new ByteArrayInputStream((byte[]) args.getArguments().get(2)), component);
					break;

				}
			} catch (Exception ex) {
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

}
