package bpm.data.viz.runtime;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.data.viz.core.IDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

import com.thoughtworks.xstream.XStream;

public class DataVizServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private XStream xstream;

	private IVanillaLogger logger;
	private DataVizComponent component;
	
	public DataVizServlet(DataVizComponent component, IVanillaLogger logger) {
		this.logger = logger;
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing DataVizServlet...");
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("actionType", IXmlActionType.class, IDataVizComponent.ActionType.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);

	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if(action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if(!(action.getActionType() instanceof IDataVizComponent.ActionType)) {
				throw new Exception("ActionType not a IDataVizComponent");
			}

			Date startDate = new Date();

			IDataVizComponent.ActionType type = (IDataVizComponent.ActionType) action.getActionType();

			try {
				switch(type) {
					case SAVE_DATA_PREP:
						actionResult = component.saveDataPreparation((DataPreparation) args.getArguments().get(0));
						break;
					case DELETE_DATA_PREP:
						component.deleteDataPreparation((DataPreparation) args.getArguments().get(0));
						break;
					case GET_ALL_DATA_PREP:
						actionResult = component.getDataPreparations();
						break;
					case EXECUTE_DATA_PREP:
						actionResult = component.executeDataPreparation((DataPreparation) args.getArguments().get(0));
						break;
					case COUNT_DATA_PREP:
						actionResult = component.countDataPreparation((DataPreparation) args.getArguments().get(0));
						break;
					case EXPORT_DATA_PREP:
						actionResult = component.exportDataPreparation((ExportPreparationInfo) args.getArguments().get(0));
						break;
					case CREATE_DATABASE:
						component.createDatabase((String) args.getArguments().get(0), (DataPreparation) args.getArguments().get(1), (boolean) args.getArguments().get(2));
						break;
					case ETL:
						actionResult = component.publicationETL((DataPreparation) args.getArguments().get(0));
						break;
				}
			} catch(Exception ex) {
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			Date endDate = new Date();

			logger.trace("Execution time for : " + type.toString() + " -> " + (endDate.getTime() - startDate.getTime()) + " with arguments -> " + args.toString());

			if(actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
				resp.getWriter().close();
			}

		} catch(Exception ex) {
			logger.error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
