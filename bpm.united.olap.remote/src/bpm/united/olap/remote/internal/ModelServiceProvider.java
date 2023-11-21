package bpm.united.olap.remote.internal;

import java.net.HttpURLConnection;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.united.olap.api.communication.IModelService;
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
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.UnitedOlapComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.UnitedOlapComponent.ActionTypes;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.VanillaComponentIdentifier;
import bpm.vanilla.platform.core.remote.internal.AbstractRemoteAuthentifier;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class ModelServiceProvider implements IModelService {

	private static class Communicator extends bpm.vanilla.platform.core.remote.internal.HttpCommunicator{
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_UNITEDOLAP);
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION, UnitedOlapComponent.HTTP_SERVLET_ACTION_MODEL);
		}
		public String executeAction(String schemaId, String message) throws Exception{
			StringBuilder params = new StringBuilder();
			params.append("?");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_SCHEMA_ID);
			params.append("=");
			params.append(schemaId);
			return sendMessage(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET + params.toString(), message);

		}
		/**
		 * allow to pass the object identifier for the Dispatcher
		 * @param action
		 * @param message
		 * @param identifier
		 * @return
		 * @throws Exception
		 */
		public String executeAction(IObjectIdentifier identifier, String message) throws Exception{
			
			StringBuilder params = new StringBuilder();
			params.append("?");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_REPOSITORY_ID);
			params.append("=");
			params.append(identifier.getRepositoryId());
			params.append("&");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_DIRECTORY_ITEM_ID);
			params.append("=");
			params.append(identifier.getDirectoryItemId());
			return sendMessage(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET + params.toString(), message);
		}

		public String executeAction(IObjectIdentifier identifier, String schemaId, String message) throws Exception{
			
			StringBuilder params = new StringBuilder();
			params.append("?");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_REPOSITORY_ID);
			params.append("=");
			params.append(identifier.getRepositoryId());
			params.append("&");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_DIRECTORY_ITEM_ID);
			params.append("=");
			params.append(identifier.getDirectoryItemId());
			params.append("&");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_SCHEMA_ID);
			params.append("=");
			params.append(schemaId);
			return sendMessage(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET + params.toString(), message);
		}
	}
	
	
//	private String url;
	private XStream xstream; 	
//	private HttpCommunicator httpCommunicator;
	private Communicator httpCommunicator;
	
	public ModelServiceProvider() {
		httpCommunicator = new Communicator();
		init();
	}
	
	
	public void init(String url, String login, String password) {
		httpCommunicator.init(url, login, password);
	}

	private void init() {
		xstream = new XStream();
	}
	
	private Object parseResponse(String xml) throws Exception{
		Object o = null;
		
		try{
			o = xstream.fromXML(xml);
		}catch(Throwable t){
			t.printStackTrace();
			Logger.getLogger(getClass()).error("Deserialisation issue - " + t.getMessage(), t);
//			throw new Exception(t.getMessage(), t);
			return o;
		}
		if (o instanceof VanillaException){
			throw (VanillaException)o;
		}
		return o;
	}

	@Override
	public String loadSchema(Schema schema, IPreloadConfig config, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schema);
		args.addArgument(config);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.LOAD);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) parseResponse(xml);
	}

	@Override
	public List<Member> getSubMembers(String uname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		args.addArgument(uname);
		args.addArgument(cubeName);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.SUBMEMBERS);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		return (List<Member>) parseResponse(xml);
	}

	@Override
	public void unloadSchema(String schemaId, IObjectIdentifier id) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		args.addArgument(id);
		
		XmlAction op = new XmlAction(args, ActionTypes.UNLOAD);
		
		String xml = httpCommunicator.executeAction(id, schemaId, xstream.toXML(op));
		parseResponse(xml);
	}

	@Override
	public String loadSchema(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(identifier);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.LOAD);
		
		String xml = httpCommunicator.executeAction(identifier, xstream.toXML(op));
		return (String) parseResponse(xml);
	}

	@Override
	public List<Schema> getLoadedSchema() throws Exception {
		return null;
	}

	@Override
	public void refreshSchema(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(identifier);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.REFRESH);
		
		String xml = httpCommunicator.executeAction(identifier, xstream.toXML(op));
		parseResponse(xml);
	}

	@Override
	public List<Member> getChilds(String uname, String schemaId, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(uname);
		args.addArgument(schemaId);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.CHILDS);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		return (List<Member>) parseResponse(xml);
	}

	@Override
	public List<String> searchOnDimensions(String word, String levelUname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(word);
		args.addArgument(levelUname);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.SEARCHDIMS);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		return (List<String>) parseResponse(xml);
	}

	@Override
	public Schema getSchema(String schemaId)throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		
		XmlAction op = new XmlAction(args, ActionTypes.FIND_SCHEMA);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		return (Schema) parseResponse(xml);
	}

	@Override
	public Schema getSchema(IObjectIdentifier identifier, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(identifier);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.FIND_SCHEMA);
		
		String xml = httpCommunicator.executeAction(identifier, xstream.toXML(op));
		
		if(xml != null && !xml.isEmpty()) {
			return (Schema) parseResponse(xml);
		}
		
		return null;
	}

	@Override
	public void refreshSchema(Schema model, IObjectIdentifier id, IPreloadConfig conf, IRuntimeContext runtimeContext) throws Exception {
		unloadSchema(model.getId(),id);
		loadSchema(model, conf, runtimeContext);
	}

	@Override
	public List<List<String>> exploreDimension(String dimensionName, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(dimensionName);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.EXPLORE);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		try{
			return (List<List<String>>) parseResponse(xml);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public List<String> getLevelValues(String levelUname, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(levelUname);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(runtimeContext);
		
		XmlAction op = new XmlAction(args, ActionTypes.DISTINCTVALUES);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		try{
			return (List<String>) parseResponse(xml);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}


	@Override
	public IObjectIdentifier getSchemaObjectIdentifier(String schemaId)
			throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		
		XmlAction op = new XmlAction(args, ActionTypes.FIND_SCHEMA_IDENTIFIER);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		return (IObjectIdentifier) parseResponse(xml);
	}


	@Override
	public Member refreshTimeDimension(String utdSchemaId, String cubeName, IRuntimeContext ctx, Projection createUnitedOlapProjection) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(utdSchemaId);
		args.addArgument(cubeName);
		args.addArgument(ctx);
		args.addArgument(createUnitedOlapProjection);
		
		XmlAction op = new XmlAction(args, ActionTypes.REFRESHTIMEDIM);
		
		String xml = httpCommunicator.executeAction(utdSchemaId, xstream.toXML(op));
		
		return (Member) parseResponse(xml);
	}


	@Override
	public void removeCache(String schemaId, String cubeName, IRuntimeContext ctx, boolean removeCacheDisk, boolean removeMemCached) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(ctx);
		args.addArgument(removeCacheDisk);
		args.addArgument(removeMemCached);
		
		XmlAction op = new XmlAction(args, ActionTypes.REMOVE_CACHE);
		
		httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
	}


	@Override
	public void restoreReloadCache(String schemaId, String cubename, IPreloadConfig preloadConfig, IRuntimeContext ctx, List<String> mdxQueries) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		args.addArgument(cubename);
		args.addArgument(preloadConfig);
		args.addArgument(ctx);
		args.addArgument(mdxQueries);
		
		XmlAction op = new XmlAction(args, ActionTypes.RELOAD_CACHE);
		
		httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
	}
}
