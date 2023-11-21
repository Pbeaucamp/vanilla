package bpm.vanilla.server.reporting.pool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.config.ConfigurationException;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.pool.VanillaItemKey;
import bpm.vanilla.server.reporting.server.tasks.birt.BirtResourceLocator;

public class BirtPoolableModel extends PoolableModel<IReportRunnable>{

	private IReportEngine engine;
	private String modelXmlFromRep;
	
	private String reportName;
	
	//private boolean hasBeenUpdated = false;
	private Map map = null;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public BirtPoolableModel(RepositoryItem directoryItem, String xml, VanillaItemKey itemKey, 
			IReportEngine engine) throws Exception {
		super(directoryItem, xml, itemKey);
		Date start = new Date();
		logger.debug("Creating birtPoolableModel");
		this.engine = engine;
		this.modelXmlFromRep = xml;
		
		try{
			modelXmlFromRep = overrideFmdtConnection();
		} catch (Exception e){
			throw new RuntimeException("error when overriding BIRT XML", e);
		}
		
		logger.debug("Fmdt connection overriden : " + (new Date().getTime() - start.getTime()));
		
		try {
			if(itemKey != null) {
				map = getBirtResourcesFromLinkedFiles(itemKey.getRepositoryContext(), directoryItem);
			}
			
			logger.debug("Linked documents browsed : " + (new Date().getTime() - start.getTime()));
			
			if (map != null && !map.isEmpty()) {
				//there is resources
				//XXX double work, but how to get the report name?
				
				IReportRunnable predesign = engine.openReportDesign(
						new ByteArrayInputStream(modelXmlFromRep.getBytes("UTF-8")));
				
				reportName = predesign.getReportName();
				
				IReportRunnable design = engine.openReportDesign(reportName, 
						new ByteArrayInputStream(modelXmlFromRep.getBytes("UTF-8")), 
						new BirtResourceLocator(logger, map));
				
				setModel(design);
			}
			else {
				//no resources
				IReportRunnable design = engine.openReportDesign(
						new ByteArrayInputStream(modelXmlFromRep.getBytes("UTF-8")));
				setModel(design);
				
				logger.debug("Design loaded : " + (new Date().getTime() - start.getTime()));
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
//	/**
//	 * This is used to rebuild the model to use certain map parameters 
//	 * for locales when i dit it
//	 * 
//	 * @param map
//	 * @throws UnsupportedEncodingException 
//	 * @throws EngineException 
//	 */
//	public void rebuildModel(String name, Map map, String groupName, Logger logger) throws Exception {
//		hasBeenUpdated = true;
//		
//		try{
//			modelXmlFromRep = overrideFmdtConnection();
//		}catch(Exception e){
//			logger.info(e.getMessage());
//			throw new Exception("error when overriding BIRT XML", e);
//		}
//		
//		IReportRunnable design = engine.openReportDesign(name, 
//				new ByteArrayInputStream(modelXmlFromRep.getBytes("UTF-8")), 
//				new BirtResourceLocator(logger, map));
//		
//		setModel(design);
//	}
	
//	/**
//	 * Checks if the model has been rebuild using resourceLocator.
//	 * If it has, clear changes
//	 * 
//	 * @param groupName
//	 * @throws Exception 
//	 */
//	public void checkModel(/*String groupName*/) throws Exception {
//		if (hasBeenUpdated) {
//			hasBeenUpdated = false;
//			try {
//				getModel();
//			} catch (Exception e) {
//				throw new Exception("Failed to rebuild a clean model after resource locator changes : " + e.getMessage(), e);
//			}
//		}
//		//else, do nothing
//	}
	
	public IReportEngine getBirtEngine(){
		return engine;
	}

	@Override
	protected void buildModel() throws Exception {
	
		
		
	}

	public IReportRunnable getModel(/*String groupName*/) {
//		try{
//			modelXmlFromRep = overrideFmdtConnection();
//		}catch(Exception e){
//			e.printStackTrace();
//			throw new RuntimeException("error when overriding BIRT XML", e);
//		}
//
//		IReportRunnable design = null;
//		
//		try{
//			if (hasBeenUpdated) {
//				
//				design = engine.openReportDesign(getModel().getReportName(), 
//						new ByteArrayInputStream(modelXmlFromRep.getBytes("UTF-8")), 
//						map);
//			} 
//			else {
//				design = engine.openReportDesign(IOUtils.toInputStream(modelXmlFromRep, "UTF-8"));
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw new RuntimeException("error when overriding BIRT XML", ex);
//		}
//		
//		setModel(design);

		return super.getModel();
	}
	
	/**
	 * 
	 * @param groupName
	 * @param login
	 * @param password
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 * @throws ConfigurationException, if we could not get a proper config
	 */
	synchronized private String overrideFmdtConnection()
			throws DocumentException, IOException, ConfigurationException {
		
		Document doc = DocumentHelper.parseText(modelXmlFromRep);
		doc.setXMLEncoding("UTF-8");
		Element root = doc.getRootElement();

		Element dataSources = root.element("data-sources");

		if (dataSources == null) {
			return doc.asXML();
		}

		for (Object o : dataSources.elements("oda-data-source")) {

			Element dataSource = (Element) o;

			if (dataSource.attribute("extensionID") != null && 
					(dataSource.attribute("extensionID").getStringValue().equals("bpm.metadata.birt.oda.runtime")) ||
					(dataSource.attribute("extensionID").getStringValue().equals("bpm.metadata.birt.oda.runtime.olap"))) {
				Element ePass = null;
				Element ecryptedEl = null;
				boolean foundRepositoryIdProp = false;
				boolean foundVanillaUrlProp = false;
				for (Object _o : dataSource.elements("property")) {
					if (((Element) _o).attribute("name").getStringValue().equals("GROUP_NAME")) {
						((Element) _o).setText(getItemKey().getRepositoryContext().getGroup().getName());
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("USER")) {
						((Element) _o).setText(getItemKey().getRepositoryContext().getVanillaContext().getLogin());
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("PASSWORD")) {
						((Element) _o).setText(getItemKey().getRepositoryContext().getVanillaContext().getPassword());
						ePass = ((Element) _o);
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("PASSWORD")){

						ecryptedEl = (Element) _o;
					}
					
//					dsHandle.setProperty("IS_ENCRYPTED", encrypted + "");
					/*
					 * ere, added support to replace url's
					 */
					else if (((Element) _o).attribute("name").getStringValue().equals("URL")) {
						String clientSideUrl = ((Element) _o).getStringValue();
						VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
						String serverSideUrl = vanillaConfig.translateClientUrlToServer(getItemKey().getRepositoryContext().getRepository().getUrl());
						((Element) _o).setText(serverSideUrl);
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("REPOSITORY_ID")) {
						foundRepositoryIdProp = true;
						((Element) _o).setText(getItemKey().getRepositoryContext().getRepository().getId() + "");
					}
					else if (((Element) _o).attribute("name").getStringValue().equals("VANILLA_URL")) {
						foundVanillaUrlProp = true;
						((Element) _o).setText(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
					}
					
				}
				if (!foundRepositoryIdProp){
					Element _n = dataSource.addElement("property");
					_n.addAttribute("name", "REPOSITORY_ID").setText(getItemKey().getRepositoryContext().getRepository().getId() + "");
				}
				if (!foundVanillaUrlProp){
					Element _n = dataSource.addElement("property");
					_n.addAttribute("name", "VANILLA_URL").setText(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
				}
				if (ecryptedEl == null){
					ecryptedEl = dataSource.addElement("property");
					ecryptedEl.addAttribute("name", "IS_ENCRYPTED").setText("true");
				}
				
//				else{
//					dataSource.addElement("property").addAttribute("name", "IS_ENCRYPTED").setText("true");
//					
//					
//				}
				
				
				
					//verify that the password is encrypted
//				if (password.matches("[0-9a-f]{32}")) {
//					//encrypted, good
//				}
//				else {
////					ecryptedEl.setText("false");
//					//not encrypted, encrypt
//						ePass.setText(MD5.encode(ePass.getStringValue()));
//				}
			}
				
				
		}
		
		return doc.asXML();
	}
	
	
	/**
	 * Fetches all the resources(linked documents) of a birt model.
	 * 
	 * @param sock
	 * @param item
	 * 
	 * @return a map containing keys resourceName/resourceFolder
	 */
	private Map getBirtResourcesFromLinkedFiles(IRepositoryContext repCtx, RepositoryItem item) {
		
		try {
			IRepositoryApi sock = new RemoteRepositoryApi(repCtx);
			
			List<LinkedDocument> docs = sock.getRepositoryService().getLinkedDocumentsForGroup(item.getId(), repCtx.getGroup().getId()); 
			
			logger.info("Found " + docs.size() + " linked doc(s)");
			
			Map resources = new HashMap();
			for (LinkedDocument doc : docs) {
				logger.info("Found resource file " + doc.getName());
				resources.putAll(writeLinkedDocument(doc.getName(), 
						sock.getDocumentationService().importLinkedDocument(doc.getId())));
			}
			
			return resources;
			
		} catch (Exception e) {
			logger.error("Failed to retrieve linkedDocuments", e);
		}
		
		logger.error("No matching linked doc found");
		return null;
	}
	
	/**
	 * Returns a handle NOT on the file, but on the directory
	 * @param docName
	 * @param in
	 * @throws Exception 
	 */
	private Map writeLinkedDocument(String docName, InputStream in) throws Exception {
		File dir = null;
		File file = null;
		FileOutputStream out = null;
		try {
			String tmpPath = System.getProperty("java.io.tmpdir");
			logger.debug("Using tmpPath : " + tmpPath);
	
			dir = new File(tmpPath + File.separator + docName.replace(".properties", ""));
			dir.mkdir();
			
			logger.debug("Created temp folder...");
			
			file = new File(dir.getPath() + File.separator + docName);
			out = new FileOutputStream(file);
			
			byte buffer[] = new byte[1024];
			int nb;
			
			while ((nb = in.read(buffer)) != -1){
				out.write(buffer, 0, nb);
			}
			logger.info("Imported LinkedDoc, returning folder " + dir.getPath());
			
			Map newMap = new HashMap();
			newMap.put(docName, dir.getPath());
			
			return newMap;
			
		} catch (Exception e) {
			throw new Exception("Failed to write LinkedDocument : " + e.getMessage(), e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}
}
