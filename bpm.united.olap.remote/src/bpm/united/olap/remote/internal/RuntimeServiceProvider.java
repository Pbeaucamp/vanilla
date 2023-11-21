package bpm.united.olap.remote.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.HttpURLConnection;

import org.apache.log4j.Logger;

import bpm.united.olap.api.communication.IRuntimeService;
import bpm.united.olap.api.communication.IServiceProvider;
import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.result.DrillThroughIdentifier;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.result.ResultCell;
import bpm.united.olap.api.result.ResultLine;
import bpm.united.olap.api.result.impl.ResultLineImpl;
import bpm.united.olap.api.result.impl.ValueResultCell;
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
import bpm.vanilla.platform.core.impl.VanillaComponentIdentifier;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;
/**
 * All IRuntimeService methods should use the parseResponse method to check if the
 * VanillaPlatform has thrown a VanillaException.
 * 
 * 
 * 
 * 
 * @author ludo
 *
 */
public class RuntimeServiceProvider implements  IRuntimeService{

	private static class Communicator extends bpm.vanilla.platform.core.remote.internal.HttpCommunicator{
		@Override
		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_UNITEDOLAP);
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION, UnitedOlapComponent.HTTP_SERVLET_ACTION_RUNTIME);
		}
		public String executeAction(String schemaId, String message) throws Exception{
			StringBuilder params = new StringBuilder();
			params.append("?");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_SCHEMA_ID);
			params.append("=");
			params.append(schemaId);
			try{
				return sendMessage(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET + params.toString(), message);
			}catch(Exception t){
				throw t;
				
			}catch(Throwable t){
				throw new Exception("A critical execption has been thrown - " + t.getMessage(), t);
			}
		}
		
		public InputStream executeActionManualDeserialisation(String schemaId, String message) throws Exception{
			StringBuilder params = new StringBuilder();
			params.append("?");
			params.append(UnitedOlapComponent.SERVLET_PARAMETER_SCHEMA_ID);
			params.append("=");
			params.append(schemaId);
			try{
				return executeActionAsStream(VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET + params.toString(), message);
			}catch(Exception t){
				throw t;
				
			}catch(Throwable t){
				throw new Exception("A critical execption has been thrown - " + t.getMessage(), t);
			}
		}
	}
	
	private String url;
	private XStream xstream; 	
//	private HttpCommunicator httpCommunicator;
	private Communicator httpCommunicator;
	private IServiceProvider serviceProvider;
	
	/**
	 * the serviceProvider holding this RuntimeProvider is required to be able
	 * to reload the Schema if the original cluster has gone
	 * @param serviceProvider
	 */
	public RuntimeServiceProvider(IServiceProvider serviceProvider) {
		httpCommunicator = new Communicator();
		this.serviceProvider = serviceProvider;
		init();
	}
	
	private void init() {
		xstream = new XStream();
	}

	public void init(String url, String login, String password) {
		httpCommunicator.init(url, login, password);
	}

	/**
	 * Deserialize through XSTREAM the server Response.
	 * 
	 * It take care of the VanillaExceptions. The Vanilla Exception should
	 * be dumped to the users.
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Object parseResponse(String xml) throws Exception{
		Object o = null;
		
		try{
			if (xml.isEmpty()){
				return null;
			}
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
	public OlapResult executeQuery(String mdx, String schemaId, String cubeName, boolean computeDatas, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(mdx);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(computeDatas);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.EXECUTE_QUERY);
		
//		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		InputStream is = null;
		
		OlapResult res = null;
		
		try{
//			return (OlapResult) parseResponse(xml);
			is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
		}catch(VanillaComponentDownException ex){
			if (ex.getItemIdentifier() != null){
				Logger.getLogger(getClass()).warn("The Component UnitedOlap has gone on the cluster, we try to reload the model ad perform the query on another node...");
				try{
					serviceProvider.getModelProvider().loadSchema(ex.getItemIdentifier(), ctx);
					is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
//					return (OlapResult) parseResponse(xml);
				}catch(Exception ex2){
					Logger.getLogger(getClass()).error("Failed to execute on another node - " + ex2.getMessage(), ex2);
					throw ex2;
				}
			}
			else{
				throw new Exception(ex.getMessage() + ".No ObjectIdentifier within the exception to allow to retry on another Cluster.");
			}
			
		}
		
		if (is != null){
			ObjectInputStream ois = new OsgiContextObjectInputStream(is);
			
			
			try{
				Object o = ois.readObject();
				if (o instanceof OlapResult){
					res = (OlapResult)o;
				}
				else{
					throw new Exception("The received obect is not an OlapResult");
				}
			}finally{
				try{
					ois.close();
				}finally{
					is.close();
				}
			}
			
			
		}
		return res;
		
	}

	@Override
	public OlapResult drillthrough(ValueResultCell cell, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(cell);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.DRILLTHROUGH);
		
//		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		InputStream is = null;
		
		OlapResult res = null;
		
//		return (OlapResult) parseResponse(xml);
		is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
		
		if (is != null){
			ObjectInputStream ois = new OsgiContextObjectInputStream(is);
			
			
			try{
				Object o = ois.readObject();
				if (o instanceof OlapResult){
					res = (OlapResult)o;
				}
				else{
					throw new Exception("The received obect is not an OlapResult");
				}
			}finally{
				try{
					ois.close();
				}finally{
					is.close();
				}
			}
			
			
		}
		return res;
	}

	@Override
	public OlapResult executeQuery(String mdx, String schemaId, String cubeName, Integer limit, boolean computeData, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(mdx);
		args.addArgument(schemaId);
		args.addArgument(limit);
		args.addArgument(cubeName);
		args.addArgument(computeData);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.EXECUTE_QUERY_WITH_LIMIT);
		
//		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		InputStream is = null;
		
		OlapResult res = null;
		
		try{
//			return (OlapResult) parseResponse(xml);
			is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
		}catch(VanillaComponentDownException ex){
			
			if (ex.getItemIdentifier() != null){
				Logger.getLogger(getClass()).warn("The Component UnitedOlap has gone on the cluster, we try to reload the model ad perform the query on another node...");
				try{
					serviceProvider.getModelProvider().loadSchema(ex.getItemIdentifier(), ctx);
					is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
//					xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
//					return (OlapResult) parseResponse(xml);
				}catch(Exception ex2){
					Logger.getLogger(getClass()).error("Failed to execute on another node - " + ex2.getMessage(), ex2);
					throw ex2;
				}
			}
			else{
				throw new Exception(ex.getMessage() + ".No ObjectIdentifier within the exception to allow to retry on another Cluster.");
			}
			
		}
		if (is != null){
			ObjectInputStream ois = new OsgiContextObjectInputStream(is);
			
			
			try{
				Object o = ois.readObject();
				if (o instanceof OlapResult){
					res = (OlapResult)o;
				}
				else{
					throw new Exception("The received obect is not an OlapResult");
				}
			}finally{
				try{
					ois.close();
				}finally{
					is.close();
				}
			}
			
			
		}
		return res;
	}

	@Override
	public void setIsInStreamMode(boolean isInStreamMode) {
		
	}

	@Override
	public void preload(String mdx, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(mdx);
		args.addArgument(schemaId);
		args.addArgument(cubeName);

		args.addArgument(runtimeContext);
		
		XmlAction op = new XmlAction(args, ActionTypes.PRELOAD);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		parseResponse(xml);
	}

	@Override
	public OlapResult executeFMDTQuery(IExternalQueryIdentifier identifier, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(identifier);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(runtimeContext);
		
		XmlAction op = new XmlAction(args, ActionTypes.EXECUTE_FMDT_QUERY);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		try{
			return (OlapResult) parseResponse(xml);
		}catch(VanillaComponentDownException ex){
			
			if (ex.getItemIdentifier() != null){
				Logger.getLogger(getClass()).warn("The Component UnitedOlap has gone on the cluster, we try to reload the model ad perform the query on another node...");
				try{
					serviceProvider.getModelProvider().loadSchema(ex.getItemIdentifier(), runtimeContext);
					xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
					return (OlapResult) parseResponse(xml);
				}catch(Exception ex2){
					Logger.getLogger(getClass()).error("Failed to execute on another node - " + ex2.getMessage(), ex2);
					throw ex2;
				}
			}
			else{
				throw new Exception(ex.getMessage() + ".No ObjectIdentifier within the exception to allow to retry on another Cluster.");
			}
			
		}
	}

	@Override
	public String createExtrapolation(String schemaId, String cubeName, Projection projection, IRuntimeContext ctx) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(projection);
		args.addArgument(ctx);
		
		XmlAction op = new XmlAction(args, ActionTypes.CREATE_EXTRAPOLATION);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		return (String) parseResponse(xml);
	}

	@Override
	public OlapResult executeQueryForFmdt(String mdx, String schemaId,
			String cubeName, boolean computeDatas,
			IRuntimeContext runtimeContext) throws Exception {
		
		
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(mdx);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(computeDatas);
		args.addArgument(runtimeContext);
		
		XmlAction op = new XmlAction(args, ActionTypes.EXECUTE_QUERY_FMDT);
		
		InputStream is = null;
		
		OlapResult res = null;
		try{
			is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
		}catch(VanillaComponentDownException ex){
			if (ex.getItemIdentifier() != null){
				Logger.getLogger(getClass()).warn("The Component UnitedOlap has gone on the cluster, we try to reload the model ad perform the query on another node...");
				try{
					serviceProvider.getModelProvider().loadSchema(ex.getItemIdentifier(), runtimeContext);
					is = httpCommunicator.executeActionManualDeserialisation(schemaId, xstream.toXML(op));
				}catch(Exception ex2){
					Logger.getLogger(getClass()).error("Failed to execute on another node - " + ex2.getMessage(), ex2);
					throw ex2;
				}
			}
			else{
				throw new Exception(ex.getMessage() + ".No ObjectIdentifier within the exception to allow to retry on another Cluster.");
			}
			
		}
		
		if (is != null){
			ObjectInputStream ois = new OsgiContextObjectInputStream(is);
			
			
			try{
				Object o = ois.readObject();
				if (o instanceof OlapResult){
					res = (OlapResult)o;
				}
				else{
					throw new Exception("The received obect is not an OlapResult");
				}
			}finally{
				try{
					ois.close();
				}finally{
					is.close();
				}
			}
			
			
		}
		return res;
	}
	/**
	 * this class is used to deserialise OlapResult coming from the Uolap Runtime server
	 * it is mandatory because of the Java class loading system that is not
	 * able to resolve classes coming from Osgi Class loader system
	 * @author ludo
	 *
	 */
	private static class OsgiContextObjectInputStream extends ObjectInputStream{

		public OsgiContextObjectInputStream(InputStream in) throws IOException {
			super(in);
		}
		
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc)
				throws IOException, ClassNotFoundException {
			return this.getClass().getClassLoader().loadClass(desc.getName());
		}
	}
	@Override
	public OlapResult drillthrough(ValueResultCell cell, String schemaId, String cubeName, IRuntimeContext runtimeContext, Projection projection) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(cell);
		args.addArgument(schemaId);
		args.addArgument(cubeName);
		args.addArgument(runtimeContext);
		args.addArgument(projection);
		
		XmlAction op = new XmlAction(args, ActionTypes.DRILLTHROUGH);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		return (OlapResult) parseResponse(xml);
	}

	@Override
	public OlapResult executeQueryForExtrapolationProjection(String mdx, String schemaId, String cubeName, Integer limit, boolean computeDatas, IRuntimeContext runtimeContext, Projection projection) throws Exception {
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		args.addArgument(mdx);
		args.addArgument(schemaId);
		args.addArgument(limit);
		args.addArgument(cubeName);
		args.addArgument(computeDatas);
		args.addArgument(runtimeContext);
		args.addArgument(projection);
		
		XmlAction op = new XmlAction(args, ActionTypes.EXECUTE_EXTRAPOLATION);
		
		String xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
		
		try{
			return (OlapResult) parseResponse(xml);
		}catch(VanillaComponentDownException ex){
			
			if (ex.getItemIdentifier() != null){
				Logger.getLogger(getClass()).warn("The Component UnitedOlap has gone on the cluster, we try to reload the model ad perform the query on another node...");
				try{
					serviceProvider.getModelProvider().loadSchema(ex.getItemIdentifier(), runtimeContext);
					xml = httpCommunicator.executeAction(schemaId, xstream.toXML(op));
					return (OlapResult) parseResponse(xml);
				}catch(Exception ex2){
					Logger.getLogger(getClass()).error("Failed to execute on another node - " + ex2.getMessage(), ex2);
					throw ex2;
				}
			}
			else{
				throw new Exception(ex.getMessage() + ".No ObjectIdentifier within the exception to allow to retry on another Cluster.");
			}
			
		}
	}

}
