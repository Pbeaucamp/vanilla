package bpm.vanilla.platform.core.wrapper.dispatcher.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.UnitedOlapComponent;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaComponentDownException;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.platform.core.wrapper.dispatcher.DistributedComponentComponentEvaluator;
import bpm.vanilla.platform.core.wrapper.dispatcher.FactoryDispatcher;
import bpm.vanilla.platform.core.wrapper.dispatcher.IDispatcher;
/**
 * This dispatcher is Used by the UnitedOlap Remote and the FD FaView Component.
 * 
 * In case of the UnitedOlap Remote:
 * 
 * - the SchemaHolders list keep track of all models that have been loaded.
 * To be able to do that, the remote provide additional parameters to be able
 * to store IObjectIdentifier and/or the UnitedOlap model schemaId.
 * 
 * If for some reason, a cluster node is gone when trying to perform
 * an action a loaded Schema, a VanillaComponentDownException is thrown and sent to the Remote. 
 * The SchemaHolder will be set as hasComponentGone, meaning the 
 * schema infos are still available to allow the client to reload the model
 * on another cluster.
 * 
 * If a relaod is done(or a load) and no cluster contains a UnitedOlap Component,
 * a VanillaException will be thrown. The SchemaHolder will be removed.
 * The only way to use a Schema is to reload it fully once a ComponentUnitedOalp will be available.
 * 
 * 
 * 
 * 
 *
 * 
 * 
 * @author ludo
 *
 */
public class UnitedOlapDispatcher extends RunnableDispatcher implements IDispatcher{

	private static class SchemaHolder{
		private String schemaId;
		private IObjectIdentifier itemId;
		private IVanillaComponentIdentifier componentId;
		private boolean componentHasGone = false;
		/**
		 * @return the schemaId
		 */
		public String getSchemaId() {
			return schemaId;
		}
		/**
		 * @return the itemId
		 */
		public IObjectIdentifier getItemId() {
			return itemId;
		}
		/**
		 * @return the componentId
		 */
		public IVanillaComponentIdentifier getComponentId() {
			return componentId;
		}
		public SchemaHolder(String schemaId, IObjectIdentifier itemId,
				IVanillaComponentIdentifier componentId) {
			super();
			this.schemaId = schemaId;
			this.itemId = itemId;
			this.componentId = componentId;
		}
		
		
	}
	
	
	private FactoryDispatcher factory;
	private List<SchemaHolder> schemaHolders = new ArrayList<SchemaHolder>();
	
	private List<SchemaHolder> oldSchema = new ArrayList<SchemaHolder>();
	
	public UnitedOlapDispatcher(IVanillaComponentProvider component, FactoryDispatcher factory){
		super(component);
		this.factory = factory;
	}
	
	
	private void dispatchFaView(List<IVanillaComponentIdentifier> ids, HttpServletRequest req, HttpServletResponse resp) throws Exception{
		StringBuilder urlStr = new StringBuilder();
		urlStr.append(ids.get(0).getComponentUrl());
		
		urlStr.append("/faRuntime/FdServlet");
		urlStr.append("?");
		Enumeration<String> en = req.getParameterNames();
		boolean firstP = true;

		while(en.hasMoreElements()){
			String key = en.nextElement();
			
			if (firstP){
				firstP = false;
			}
			else{
				urlStr.append("&");
			}
			urlStr.append(key + "=");
			if (req.getParameter(key) != null){
				urlStr.append(URLEncoder.encode(req.getParameter(key), "UTF-8"));
			}
			
			
		}
		
		

		/*
		 * create a Connection
		 */
		URL url =new URL(urlStr.toString());
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		if (req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID) != null){
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID));
		}
		else if (req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID) != null){
			sock.setRequestProperty(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID, (String)req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID));
		}
		else{
			resp.sendError(resp.SC_UNAUTHORIZED, "Missing VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID Header");
			return;
		}
		
		//copy the headers
		Enumeration<String> headers = req.getHeaderNames();
		while(headers.hasMoreElements()){
			String name = headers.nextElement();
			if (req.getHeader(name) != null){
				sock.setRequestProperty(name, req.getHeader(name));
			}
		}
		


		sock.setDoInput(true);
		sock.setDoOutput(true);
		sock.setRequestMethod("POST");
		
		
		InputStream input1 = req.getInputStream();
		OutputStream output1 = sock.getOutputStream();
		
		//copy input to output1
		int sz = 0;
		byte[] buf = new byte[1024];
		while( (sz = input1.read(buf)) >= 0){
			output1.write(buf, 0, sz);
		}
		
		
		
		InputStream input2 = sock.getInputStream();
		OutputStream output2 = resp.getOutputStream();
		
		sz = 0;
		while( (sz = input2.read(buf)) >= 0){
			output2.write(buf, 0, sz);
		}
		
		
		//TODO : close streams???
		sock.disconnect();
	}
	
	
	@Override
	public void dispatch(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		
		
		String actionName = request.getHeader(VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION);
		if (actionName == null || actionName.equals("")){
			List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(this);
			
			if (ids.isEmpty()){
				throw new Exception("No UnitedOlap Component " + " registered within Vanilla");
			}
			
			dispatchFaView(ids, request, response);
			return;
//			//TODO : probable throw an exception
//			throw new Exception("Missing the VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION Header with the HttpRequest");
		}
		
		
		String repositoryId = request.getParameter(UnitedOlapComponent.SERVLET_PARAMETER_REPOSITORY_ID);
		String directoryItemId = request.getParameter(UnitedOlapComponent.SERVLET_PARAMETER_DIRECTORY_ITEM_ID);
		String schemaId = request.getParameter(UnitedOlapComponent.SERVLET_PARAMETER_SCHEMA_ID);
		
		Logger.getLogger(getClass()).debug("Schema informations : \n" + 
		"RepositoryId = " + repositoryId + "\n" + 
		"ItemId = " + directoryItemId + "\n" + 
		"SchemaId = " + schemaId + "\n");
		
		SchemaHolder schemaHolder = null;
		
		if(schemaId == null && repositoryId == null && directoryItemId == null) {
			/*
			 * Used to load a schema not in the repository
			 */
			Logger.getLogger(getClass()).debug("Create an empty schemaHolder");
			schemaHolder = new SchemaHolder(schemaId, null, getBestVanillaComponent(new ObjectIdentifier()));
		}
		else if (repositoryId != null && directoryItemId != null){
			/*
			 * if those paramters are given, this means that we have to load a schema
			 * to do this, see the hijack to be able to store the schemaId 
			 */
			Logger.getLogger(getClass()).debug("Get schemaHolder from Repository and Item");
			schemaHolder = getSchemaHolder(new ObjectIdentifier(Integer.parseInt(repositoryId), Integer.parseInt(directoryItemId)));
		}
		else if (schemaId != null){
			/*
			 * the schema has already been loaded, an action is being perfomed on it(query, browe,...)
			 */
			Logger.getLogger(getClass()).debug("Get schemaHolder from SchemaId");
			schemaHolder = getSchemaHolder(schemaId);
		}
		
		
		
		
		StringBuilder url = new StringBuilder();
		//url.append(ids.get(0)); i saw what you did here ludo, ere.
		url.append(schemaHolder.getComponentId().getComponentUrl());
		
		/*
		 * build the query for the UnitedOlapWrapper
		 * on the http header VanillaConstants.HTTP_HEADER_SERVLET_DISPATCH_ACTION
		 */
		if (UnitedOlapComponent.HTTP_SERVLET_ACTION_MODEL.equals(actionName)){
			url.append(UnitedOlapComponent.SERVLET_MODEL);
			
		}
		else if (UnitedOlapComponent.HTTP_SERVLET_ACTION_RUNTIME.equals(actionName)){
			url.append(UnitedOlapComponent.SERVLET_RUNTIME);
		}
		
		String queryString = request.getQueryString();
		
		//we do that to avoid the synchronization problems after unload/refresh schema
		if(schemaId != null) {
			queryString = queryString.replace(schemaId, schemaHolder.getSchemaId());
		}
		
		url.append( "?" + queryString);
		
		if (!checkItemIsOn(schemaHolder)){
			throw new VanillaException("The OLAP Model has been turned off from EntrepriseServices.");
		}
		
		/*
		 * redirecting
		 */
		
		try{
		
			if(repositoryId != null && directoryItemId != null && schemaId != null) {
				/*
				 * Clean after unload a schema
				 */
				Logger.getLogger(getClass()).debug("Clean schema for unload");
				oldSchema.add(schemaHolder);
				
				schemaHolders.remove(schemaHolder);
				sendCopy(request, response, url.toString());
			}
			else if (schemaHolder.getSchemaId() == null){
				/*
				 * we redirect the incoming stream to the UnitedOlapWrapper
				 * once it is done, we hijack the Result to store the schemaId of
				 * the UnitedOlap Schema that has been loaded
				 */
				Logger.getLogger(getClass()).debug("Store Id on schemaHolder");
				ByteArrayOutputStream out2 = sendCopy(request, response, url.toString());			
				if (out2.toString().startsWith("<string>")){
					schemaHolder.schemaId = out2.toString().replace("<string>", "").replace("</string>", "");
					Logger.getLogger(getClass()).debug("Store Id on schemaHolder : " + schemaHolder.schemaId);
					storeSchemaHolder(schemaHolder);
				}
				//That out2 is the uolap schema
				//We use dom4j to get the Id
				else {
					try {
						Logger.getLogger(getClass()).debug("Store Id on schemaHolder using dom4j");
						Document doc = DocumentHelper.parseText(out2.toString());
						String id = doc.getRootElement().element("id").getText();
						schemaHolder.schemaId = id;
						Logger.getLogger(getClass()).debug("Store Id on schemaHolder using dom4j : " + schemaHolder.schemaId);
						storeSchemaHolder(schemaHolder);
					} catch(Exception e) {
					}
				}
			}
			else{
				/*
				 * we just redirect the stream to the UnitedOlapWrapper 
				 */
				sendCopy(request, response, url.toString());
			}
			schemaHolder.componentHasGone = false;
		}catch(FileNotFoundException ex){
			/*
			 * The Target UnitedOlapWrapper does not respond.
			 * We flag this on the schemaHolder in case an attempt to
			 * delegate the operation to another Cluster is made by teh remote(and it is made!)
			 *  
			 *  We throw a VanillaComponentDownException for the Remote
			 */
			schemaHolder.componentHasGone = true;
			Logger.getLogger(getClass()).error("Could not disptach - " + ex.getMessage(), ex);
			Logger.getLogger(getClass()).warn("The ComponentUnited used to load the model  must have been shutdown, we try to send the request on another node");
	

			throw new VanillaComponentDownException(schemaHolder.getComponentId(), schemaHolder.getItemId());
		}
	}

	private boolean checkItemIsOn(SchemaHolder schemaHolder) throws Exception{
		if (schemaHolder.getItemId() != null){
			
			VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
			Repository rep = getComponent().getRepositoryManager().getRepositoryById(schemaHolder.getItemId().getRepositoryId());
			Group g = new Group(); g.setId(-1);
			IRepositoryApi sock = new RemoteRepositoryApi(new BaseRepositoryContext(
					new BaseVanillaContext(
							conf.getVanillaServerUrl(), 
							conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
							conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)), 
					g, 
					rep));
			
			RepositoryItem it = sock.getRepositoryService().getDirectoryItem(schemaHolder.getItemId().getDirectoryItemId());
			if (it == null){
				throw new VanillaException("Unable to find the Model on the Repository.");
			}
			return it.isOn();
			
		}
		else{
			return true;
		}
		
	}


	@Override
	public boolean needAuthentication() {
		
		return true;
	}

	
	
	private IVanillaComponentIdentifier getBestVanillaComponent(IObjectIdentifier identifier){
//		List<IVanillaComponentIdentifier> ids = factory.getComponentsFor(this);
		
		//TODO : eval load
		IVanillaComponentIdentifier bestComponent = null;
//		Integer bestComponentIndice = null;
		
		try {
			bestComponent = DistributedComponentComponentEvaluator.computeLoad(this, identifier,factory);
			if(bestComponent == null) {
				throw new VanillaException("Something wrong happened when load balancing on ReportComponent, no one could be chosen.");
			}
		}
		catch (Exception e) {
			Logger.getLogger(getClass()).error("Failed to Evalute Load on Components -"+ e.getMessage(), e);
		}
		
//		for(IVanillaComponentIdentifier id : ids){
//			if (bestComponent == null){
//				
//				try{
//					bestComponentIndice = DistributedComponentComponentEvaluator.computeLoad(id,identifier);
//					bestComponent = id;
//				}catch(Exception ex){
//					Logger.getLogger(getClass()).error("Failed to Evalute Load on Component " + id.toString() + "-"+ ex.getMessage(), ex);
//				}
//			}
//			else{
//				
//				try{
//					int k = DistributedComponentComponentEvaluator.computeLoad(id,identifier);
//					if (k < bestComponentIndice){
//						bestComponent = id;
//						bestComponentIndice = k;
//					}
//				}catch(Exception ex){
//					Logger.getLogger(getClass()).error("Failed to Evalute Load on Component " + id.toString() + "-"+ ex.getMessage(), ex);
//				}
//			}
//		}	
		
		return bestComponent;
	}
	
	private SchemaHolder getSchemaHolder(IObjectIdentifier identifier) throws Exception{
		for(SchemaHolder h : schemaHolders){
			if(h.getItemId()==null){
				continue;
			}
			if (h.getItemId().getDirectoryItemId() == identifier.getDirectoryItemId() &&
				h.getItemId().getRepositoryId() == identifier.getRepositoryId()){
				
				
				if (h.componentHasGone){
					/*
					 * the COmponent has been set to has gone
					 * this means that we need to reload the model on another node
					 */
					h.componentId = getBestVanillaComponent(identifier);
					
					if (h.componentId == null){
						/*
						 * no cluster available, to remove the schemaHolder
						 * and throw a VanillaException
						 */
						synchronized (schemaHolders) {
							schemaHolders.remove(h);
						}
						throw new VanillaException("No component UnitedOlap available. You will need to reload the Cube once an UnitedOlap Component is available.");
					}
				}
				
				return h;
			}
		}
		
		
		SchemaHolder h = new SchemaHolder(null, identifier, getBestVanillaComponent(identifier));
		
		
		
		return h;
	}
	
	
	private SchemaHolder getSchemaHolder(String schemaId) throws Exception{
		for(SchemaHolder h : schemaHolders){
			//the encoding MD5 is only made the Hypervision Transfert Cache View
			//because the schemaId is encoded on the cacheFile Name
			if (schemaId.equals(h.getSchemaId()) || schemaId.equals(MD5Helper.encode(h.getSchemaId()))){
				
				return h;
			}
		}
		
		for(SchemaHolder h : oldSchema){
			//the encoding MD5 is only made the Hypervision Transfert Cache View
			//because the schemaId is encoded on the cacheFile Name
			if (schemaId.equals(h.getSchemaId()) || schemaId.equals(MD5Helper.encode(h.getSchemaId()))){
				
				for(SchemaHolder h2 : schemaHolders){
					if(h2.getItemId().equals(h.getItemId())) {
						return h2;
					}
				}
					
			}
		}
		
		Logger.getLogger(getClass()).warn("Could not find a SchemaHolder for schemaId=" + schemaId);
		return null;
	}
	
	private void storeSchemaHolder(SchemaHolder h){
		schemaHolders.add(h);
		
	}
}
