package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.IExcelManager;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.ExcelManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ExcelServlet extends AbstractComponentServlet{

	
	public ExcelServlet(IVanillaComponentProvider componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.component = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing VanillaRepositoryServlet...");
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IExcelManager.ActionType)){
				throw new Exception("ActionType not a IRepositoryManager");
			}
			
			IExcelManager.ActionType type = (IExcelManager.ActionType)action.getActionType();
			
			log(type, ((ExcelManager)component.getExcelManager()).getComponentName(), req);
			
			try{
				switch (type) {
				case GET_DRIVERS:
					actionResult = getListDrivers(args);
					break;
					
				case GET_COLUMNTYPE:
					actionResult = getColumnsType(args);
					break;
					
				case GET_TABLES:
					actionResult = getListTable(args);
					break;
					
				case GET_COLUMNS:
					actionResult = getListColumns(args);
					break;
					
				case TESTCONNECT_SERVER:
					actionResult = testConnectionDatabase(args);
					break;	
					
				case ADD_CONTRACT:
					actionResult = addContract(args);
					break;
					
				case CREATE_TABLE:
					actionResult= createTable(args);
					break;
					

					
				}
			}catch(Exception ex){
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();	
			
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
	
	private String testConnectionDatabase(XmlArgumentsHolder args) throws Exception{	
		return component.getExcelManager().testConnectionDatabase((HashMap<String, String>)args.getArguments().get(0));
	}
	
	private String getListDrivers(XmlArgumentsHolder args) throws Exception{	
		return component.getExcelManager().getListDriver();
	}
	
	private String getColumnsType(XmlArgumentsHolder args) throws Exception{		
		return component.getExcelManager().getColumnType((HashMap<String, String>)args.getArguments().get(0));
	}
	
	private String getListTable(XmlArgumentsHolder args) throws Exception{		
		return component.getExcelManager().getListTables((HashMap<String, String>)args.getArguments().get(0));
	}
	
	private String getListColumns(XmlArgumentsHolder args) throws Exception{		
		return component.getExcelManager().getListColumns((HashMap<String, String>)args.getArguments().get(0));
	}
	
	private String addContract(XmlArgumentsHolder args) throws Exception{	
		return component.getExcelManager().loaderExcel((String)args.getArguments().get(0), (String)args.getArguments().get(1), (InputStream)args.getArguments().get(2),
				(IRepositoryContext)args.getArguments().get(3),(HashMap<String, String>) args.getArguments().get(4) );
	}
	
	private String createTable(XmlArgumentsHolder args) throws Exception{	
		return component.getExcelManager().createTable((String)args.getArguments().get(0),(HashMap<String, String>)args.getArguments().get(1));
	}

	
	
}
