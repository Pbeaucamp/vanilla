package bpm.united.olap.wrapper.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.united.olap.api.datasource.Connection;
import bpm.united.olap.api.datasource.DataObject;
import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.Operator;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.aggregation.ILevelAggregation;
import bpm.united.olap.api.model.impl.ComplexMeasure;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.wrapper.UnitedOlapWrapperComponent;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.components.UnitedOlapComponent.ActionTypes;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ModelServlet extends HttpServlet {

	private XStream xstream;
	private UnitedOlapWrapperComponent component;
	
	public ModelServlet(UnitedOlapWrapperComponent unitedOlapWrapperComponent) {
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
				case LOAD:
					actionResult = load(args);
					break;
				case SUBMEMBERS:
					actionResult = getSubmembers(args);
					break;
				case SEARCHDIMS:
					actionResult = searchDims(args);
					break;
				case UNLOAD:
					unload(args);
					break;
				case LOAD_DIM:
					loadDimension(args);
					break;
				case REFRESH:
					refreshSchema(args);
					break;
				case CHILDS:
					actionResult = getChilds(args);
					break;
				case FIND_SCHEMA:
					actionResult = findSchema(args);
					break;
				case EXPLORE:
					actionResult = explore(args);
					break;
				case DISTINCTVALUES:
					actionResult = getDistinctValues(args);
					break;
				case FIND_SCHEMA_IDENTIFIER:
					actionResult = findSchemaVanillaIdentifier(args);
					break;
				case REFRESHTIMEDIM:
					actionResult = refreshTimeDim(args);
					break;
				case REMOVE_CACHE:
					removeCache(args);
					break;
				case RELOAD_CACHE:
					reloadCache(args);
					break;
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void reloadCache(XmlArgumentsHolder args) throws Exception {
		String schemaId = (String) args.getArguments().get(0);
		String cubeName = (String) args.getArguments().get(1);
		IPreloadConfig preloadConfig = (IPreloadConfig) args.getArguments().get(2);
		IRuntimeContext runtimeContext = (IRuntimeContext) args.getArguments().get(3);
		List<String> queries = null;
		try {
			queries = (List<String>) args.getArguments().get(4);
		} catch (Exception e) {
			component.getLogger().warn("No mdx queries reloaded in cache");
		}
		
		component.getModelProvider().restoreReloadCache(schemaId, cubeName, preloadConfig, runtimeContext, queries);
		
	}

	private void removeCache(XmlArgumentsHolder args) throws Exception {
		String schemaId = (String) args.getArguments().get(0);
		String cubeName = (String) args.getArguments().get(1);
		IRuntimeContext runtimeContext = (IRuntimeContext) args.getArguments().get(2);
		boolean removeCacheDisk = (Boolean) args.getArguments().get(3);
		boolean removeMemcached = (Boolean) args.getArguments().get(4);
		
		component.getModelProvider().removeCache(schemaId, cubeName, runtimeContext, removeCacheDisk, removeMemcached);
	}

	private Object refreshTimeDim(XmlArgumentsHolder args) throws Exception {
		return component.getModelProvider().refreshTimeDimension((String)args.getArguments().get(0), (String)args.getArguments().get(1), (IRuntimeContext)args.getArguments().get(2),(Projection)args.getArguments().get(3));
	}

	private Object findSchemaVanillaIdentifier(XmlArgumentsHolder args) throws Exception{
		return component.getModelProvider().getSchemaObjectIdentifier((String)args.getArguments().get(0));
	}

	private Object getDistinctValues(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments().size() != 4) {
			throw new Exception("Wrong number of arguments, expecting 3");
		}
		
		String levelUname = (String) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		String cubeName = (String) args.getArguments().get(2);
		IRuntimeContext runtimeContext = (IRuntimeContext) args.getArguments().get(3);
		
		return component.getModelProvider().getLevelValues(levelUname, schemaId, cubeName, runtimeContext);
	}

	private Object explore(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments().size() != 4) {
			throw new Exception("Wrong number of arguments, expecting 3");
		}
		
		String dimensionName = (String) args.getArguments().get(0);
		String schemaId = (String) args.getArguments().get(1);
		String cubeName = (String) args.getArguments().get(2);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(3);
		
		return component.getModelProvider().exploreDimension(dimensionName, schemaId, cubeName, ctx);
	}

	private Object findSchema(XmlArgumentsHolder args) throws Exception {
		if (args.getArguments().size() == 1){
			String schemaId = (String)args.getArguments().get(0);
			return component.getModelProvider().getSchema(schemaId);
		}
		else if (args.getArguments().size() == 2){
			IObjectIdentifier identifier = (IObjectIdentifier) args.getArguments().get(0);
			IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(1);
			
			return component.getModelProvider().getSchema(identifier, ctx);
		}
		
		throw new Exception("Bad Arguments");
		
	}

	private Object getChilds(XmlArgumentsHolder args) throws Exception {
		String uname = (String) args.getArguments().get(0);
		String schema = (String) args.getArguments().get(1);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(2);
		
		return component.getModelProvider().getChilds(uname, schema, ctx);
	}

	private void refreshSchema(XmlArgumentsHolder args) throws Exception {
		IObjectIdentifier id = (IObjectIdentifier) args.getArguments().get(0);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(1);
		
		component.getModelProvider().refreshSchema(id, ctx);
	}

	private void loadDimension(XmlArgumentsHolder args) {
		
		
	}

	private void unload(XmlArgumentsHolder args) throws Exception {
		String schemaId = (String) args.getArguments().get(0);
		IObjectIdentifier id = (IObjectIdentifier) args.getArguments().get(1);
		component.getModelProvider().unloadSchema(schemaId,id);
//		String dimensionName = (String) args.getArguments().get(1);
//		boolean preload = (Boolean) args.getArguments().get(2);
//		return component.getModelService().loadDimension(schemaId, dimensionName, preload);
//		return null;
	}

	private Object searchDims(XmlArgumentsHolder args) throws Exception {
		String word = (String) args.getArguments().get(0);
		String level = (String) args.getArguments().get(1);
		String schemaId = (String) args.getArguments().get(2);
		String cubeName = (String) args.getArguments().get(3);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(4);
		
		return component.getModelProvider().searchOnDimensions(word, level, schemaId, cubeName, ctx);
	}

	private Object getSubmembers(XmlArgumentsHolder args) throws Exception {
		String uname = (String) args.getArguments().get(1);
		String schemaId = (String) args.getArguments().get(0);
		String cubeName = (String) args.getArguments().get(2);
		IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(3);
		
		return component.getModelProvider().getSubMembers(uname, schemaId, cubeName, ctx);
	}

	private String load(XmlArgumentsHolder args) throws Exception {
		if (args.getArguments().get(0) instanceof Schema){
			Schema schema = (Schema) args.getArguments().get(0);
			IPreloadConfig conf = (IPreloadConfig) args.getArguments().get(1);
			IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(2);
			
			return component.getModelProvider().loadSchema(schema, conf, ctx);
		}
		else if (args.getArguments().get(0) instanceof IObjectIdentifier){
			IObjectIdentifier id = (IObjectIdentifier) args.getArguments().get(0);
			IRuntimeContext ctx = (IRuntimeContext) args.getArguments().get(1);
			return component.getModelProvider().loadSchema(id, ctx);
		}
		else{
			throw new Exception("bad arguments");
		}
		
		
		
		
		
	}

	@Override
	public void init() throws ServletException {
		xstream = new XStream();
		super.init();
	}

	
	
}
