package bpm.united.olap.wrapper.servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.result.DrillThroughIdentifier;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.ValueResultCell;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.wrapper.UnitedOlapWrapperComponent;
import bpm.vanilla.platform.core.components.UnitedOlapComponent.ActionTypes;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RuntimeServlet extends HttpServlet {

	private XStream xstream;
	private UnitedOlapWrapperComponent component;
	
	public RuntimeServlet(UnitedOlapWrapperComponent unitedOlapWrapperComponent) {
		component = unitedOlapWrapperComponent;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			Object actionResult = null;
			
			ActionTypes type = (ActionTypes)action.getActionType();
			switch(type) {
				case EXECUTE_QUERY:
					OlapResult res = (OlapResult)executeQuery(args);
					serializeOlapResultForFmdt(res, resp.getOutputStream());
					break;
				case EXECUTE_QUERY_FMDT:
					OlapResult rs = (OlapResult)executeQuery(args);
					serializeOlapResultForFmdt(rs, resp.getOutputStream());
					break;
				case EXECUTE_QUERY_WITH_LIMIT:
//					actionResult = executeQueryWithLimit(args);
					OlapResult qfmdt = (OlapResult)executeQueryWithLimit(args);
					serializeOlapResultForFmdt(qfmdt, resp.getOutputStream());
					break;
				case DRILLTHROUGH:
					OlapResult drillt = (OlapResult)drillthrough(args);
					serializeOlapResultForFmdt(drillt, resp.getOutputStream());
					break;
				case PRELOAD:
					preload(args);
					break;
				case CREATE_EXTRAPOLATION:
					actionResult = createExtrapolation(args);
					break;
				case EXECUTE_EXTRAPOLATION:
					actionResult = executeExtrapolation(args);
					break;
					
				case EXECUTE_FMDT_QUERY:
					actionResult = executeFmdtQuery(args);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private Object executeExtrapolation(XmlArgumentsHolder args) throws Exception {
		String mdx = (String) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		Integer limit = (Integer) args.getArguments().get(2);
		String cubeName = (String)args.getArguments().get(3);
		boolean computeDatas = (Boolean)args.getArguments().get(4);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(5);
		Projection proj = (Projection) args.getArguments().get(6);
		
		return component.getRuntimeProvider().executeQueryForExtrapolationProjection(mdx, schemaId, cubeName, limit, computeDatas, ctx, proj);
	}

	private Object createExtrapolation(XmlArgumentsHolder args) throws Exception {
		String schemaId = (String) args.getArguments().get(0);
		String cubeName = (String)args.getArguments().get(1);
		Projection projection = (Projection) args.getArguments().get(2);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(3);
		return component.getRuntimeProvider().createExtrapolation(schemaId, cubeName, projection, ctx);
	}

	private void serializeOlapResultForFmdt(OlapResult rs,	OutputStream outputStream) throws Exception{
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(outputStream);
		}catch(Exception ex){
			throw new Exception("Unable to create an ObjectOutputStream " + ex.getMessage(), ex);
		}
		
		try {
			oos.writeObject(rs);
			oos.flush();
		}finally{
			try{
				oos.close();
			}finally{
				outputStream.close();
			}
		}
		
	}

	private Object executeFmdtQuery(XmlArgumentsHolder args) throws Exception {
		IExternalQueryIdentifier id = (IExternalQueryIdentifier) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		String cubeName = (String)args.getArguments().get(2);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(3);
		return component.getRuntimeProvider().executeFMDTQuery(id, schemaId, cubeName, ctx);
	}

	private Object executeQueryWithLimit(XmlArgumentsHolder args) throws Exception {
		String mdx = (String) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		Integer limit = (Integer) args.getArguments().get(2);
		String cubeName = (String)args.getArguments().get(3);
		boolean computeDatas = (Boolean)args.getArguments().get(4);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(5);
		
		return component.getRuntimeProvider().executeQuery(mdx, schemaId, cubeName, limit, computeDatas, ctx);
	}

	private Object drillthrough(XmlArgumentsHolder args) throws Exception {
		ValueResultCell cell = (ValueResultCell) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		String cubeName = (String)args.getArguments().get(2);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(3);
		
		if(args.getArguments().size() > 4) {
			Projection proj = (Projection) args.getArguments().get(4);
			return component.getRuntimeProvider().drillthrough(cell, schemaId, cubeName, ctx, proj);
		}
		
		return component.getRuntimeProvider().drillthrough(cell, schemaId, cubeName, ctx);
	}

	private void preload(XmlArgumentsHolder args) throws Exception{
		String mdx = (String) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		String cubeName = (String)args.getArguments().get(2);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(3);
		
		component.getRuntimeProvider().preload(mdx, schemaId, cubeName,  ctx);

	}
	
	private Object executeQuery(XmlArgumentsHolder args) throws Exception{
		String mdx = (String) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		String cubeName = (String)args.getArguments().get(2);
		boolean computeDatas = (Boolean)args.getArguments().get(3);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(4);
		
		return component.getRuntimeProvider().executeQuery(mdx, schemaId, cubeName, computeDatas, ctx);
	}

	@Override
	public void init() throws ServletException {
		xstream = new XStream();
		
		super.init();
	}
}
