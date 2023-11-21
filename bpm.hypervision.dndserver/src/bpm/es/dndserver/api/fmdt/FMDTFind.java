package bpm.es.dndserver.api.fmdt;

import java.util.ArrayList;
import java.util.List;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.Message;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.tools.OurLogger;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FMDTFind {

	private static List<Message> messages = new ArrayList<Message>();
	
	public static List<FMDTDataSource> find(AxisDirectoryItemWrapper wrapper, IRepositoryApi sock) throws Exception {
	
		messages.clear();
		
		RepositoryItem item = wrapper.getAxisItem();
		
		
		try{
			IFMDTExtractor extractor = FMDTExtractorProvider.getExtractor(item);
			
			if (extractor != null){
				List<FMDTDataSource> dataSources = extractor.extractFmdtDataSources(item, sock);
				
				messages.addAll(extractor.getMessages());
				return dataSources;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			String error = Messages.FMDTFind_0 + item.getType() + Messages.FMDTFind_1;
			Message msg = new Message(item.getId(), item.getItemName(), Message.ERROR, error);
			messages.add(msg);
			OurLogger.info(error);
			return null;
		}
		
		return new ArrayList<FMDTDataSource>();
//		if (item.getType() == IRepositoryConnection.CUST_TYPE &&
//				(item.getSubType().equals(IRepositoryConnection.BIRT_REPORT_SUBTYPE) ||
//				 item.getSubType().equals("BIRT")
//				)) {
//			return findBIRT(item, client);
//		}
//		if (item.getType() == IRepositoryConnection.FD_TYPE) {
//			for (AxisDirectoryItemWrapper dep : wrapper.getDependencies()) {
//				if (dep.getAxisItem().getType() == IRepositoryConnection.FD_DICO_TYPE) {
//					return findBPMODA(dep.getAxisItem(), client);
//				}
//			}
//			String error = "This dashboard has no dictionnary";
//			Message msg = new Message(item.getId(), item.getName(), Message.ERROR, error);
//			messages.add(msg);
//			OurLogger.info(error);
//			return null; 
//		}
//		else {
//			String error = "Type " + item.getType() + " not supported";
//			Message msg = new Message(item.getId(), item.getName(), Message.ERROR, error);
//			messages.add(msg);
//			OurLogger.info(error);
//			return null;
//			//throw new Exception(error);
//		}
	}
	
	public static List<Message> getLastMessages() {
		return messages;
	}
	
//	private static List<FMDTDataSource> findBPMODA(AxisDirectoryItem item,
//			RepositoryContentClient client) throws Exception {
//		List<FMDTDataSource> datasources = new ArrayList<FMDTDataSource>();
////		<dictionary version="v2.0" name="Dicometa">
////	  <dataSource>
////	    <name>sourceMetaSample</name>
////	    <odaExtensionDataSourceId>bpm.metadata.birt.oda.runtime</odaExtensionDataSourceId>
////	    <odaExtensionId>bpm.metadata.birt.oda.runtime</odaExtensionId>
////	    <odaDriverClassName>bpm.metadata.birt.oda.runtime.impl.Driver</odaDriverClassName>
////	    <publicProperty name="URL">http://localhost:8080/BIRepository</publicProperty>
////	    <publicProperty name="BUSINESS_MODEL">Sampledata</publicProperty>
////	    <publicProperty name="BUSINESS_PACKAGE">Sales</publicProperty>
////	    <publicProperty name="USER">system</publicProperty>
////	    <publicProperty name="DIRECTORY_ITEM_ID">1</publicProperty>
////	    <publicProperty name="GROUP_NAME">System</publicProperty>
////	    <publicProperty name="PASSWORD">system</publicProperty>
////	    <privateProperty name="CONNECTION_NAME">Default</privateProperty>
//		String modelXml = "";
//		
//		try {
//			modelXml = client.loadModel(item.getType(), item.getRepositoryItem());
//		} catch (Exception e) {
//			throw new Exception("Failed to load model : " + e.getMessage());
//		}
//		
//		Document doc = DocumentHelper.parseText(modelXml);
//		
//		List<Element> list = doc.getRootElement().elements("dataSource");
//		
//		for (Element ele : list) {
//			try {
//				if (ele.element("odaExtensionDataSourceId")
//						.equals("bpm.metadata.birt.oda.runtime")) {
//					OurLogger.info("Found an unsupported extension id, skipping");
//					
//					throw new IllegalArgumentException("Unsupported extensionId : " 
//							+ ele.attributeValue("odaExtensionDataSourceId"));
//				}
//					
//				FMDTDataSource source = new FMDTDataSource();
//				source.setDataSourceName(ele.element("name").getText());
//				
//				ele.elements("publicProperty");
//				
//				datasources.add(source);
//			} catch (IllegalArgumentException ex) {
//				Message msg = new Message(item.getId(), item.getName(), 
//						Message.WARNING, ex.getMessage());
//				
//				messages.add(msg);
//			}
//		}
//		
//		return datasources;
//	}
//	private static List<FMDTDataSource> findBIRT(AxisDirectoryItem item,
//			RepositoryContentClient client) throws Exception {
//		List<FMDTDataSource> datasources = new ArrayList<FMDTDataSource>();
//		
//		String modelXml = "";
//		
//		try {
//			modelXml = client.loadModel(item.getType(), item.getRepositoryItem());
//		} catch (Exception e) {
//			throw new Exception("Failed to load model : " + e.getMessage());
//		}
//		
//		Document doc = DocumentHelper.parseText(modelXml);
//		//.element("report")
//		//.elements("property").get(0)
//		//doc.getRootElement().element("data-sources").element("oda-data-source").elements("property").get(0)
//		List<Element> list = doc.getRootElement().element("data-sources").elements("oda-data-source");
//		
//		for (Element ele : list) {
//			try {
//				FMDTDataSource source = parseBIRTProps(ele);
//				datasources.add(source);
//			} catch (IllegalArgumentException ex) {
//				Message msg = new Message(item.getId(), item.getName(), 
//						Message.WARNING, ex.getMessage());
//				
//				messages.add(msg);
//			}
//		}
//
//		return datasources;
//	}
	
//	private static FMDTDataSource parseBIRTProps(Element element) throws IllegalArgumentException {
//		FMDTDataSource source = new FMDTDataSource();
//		
////        <list-property name="privateDriverProperties">
////        <ex-property>
////            <name>CONNECTION_NAME</name>
////			  <value>Default</value>
//		//extensionID="bpm.metadata.birt.oda.runtime
//		
//		if (!element.attributeValue("extensionID").equals("bpm.metadata.birt.oda.runtime")) {
//			OurLogger.info("Found an unsupported extension id, skipping");
//			
//			throw new IllegalArgumentException("Unsupported extensionId : " + element.attributeValue("extensionID"));
//		}
//		
//		String dsName = element.attributeValue("name");
//		source.setDataSourceName(dsName);
//		
//		String conName = element.element("list-property").element("ex-property").element("value").getText();
//		source.setConnectionName(conName);
//		
//		for (Object o : element.elements("property")) {
//			Element ele = (Element) o;
//			String propname = ele.attributeValue("name");
//			String propvalue = ele.getText();
//			
//			if (propname.equals("USER")) 
//				source.setUser(propvalue);
//			else if (propname.equals("PASSWORD")) 
//				source.setPass(propvalue);
//			else if (propname.equals("URL")) 
//				source.setUrl(propvalue);
//			else if (propname.equals("DIRECTORY_ITEM_ID")) {
//				source.setDirItemId(Integer.parseInt(propvalue));
//			}
//			else if (propname.equals("BUSINESS_MODEL")) 
//				source.setBusinessModel(propvalue);
//			else if (propname.equals("BUSINESS_PACKAGE")) 
//				source.setBusinessPackage(propvalue);
//			else if (propname.equals("GROUP_NAME")) 
//				source.setGroupName(propvalue);
//			//else 
//		}
//		
//		return source;
//	}
}
