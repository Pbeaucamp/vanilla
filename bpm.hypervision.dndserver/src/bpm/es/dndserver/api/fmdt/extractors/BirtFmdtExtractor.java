package bpm.es.dndserver.api.fmdt.extractors;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.Message;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.IFMDTExtractor;
import bpm.es.dndserver.tools.OurLogger;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class BirtFmdtExtractor implements IFMDTExtractor{

	private List<Message> messages = new ArrayList<Message>();
	
	@Override
	public List<FMDTDataSource> extractFmdtDataSources(RepositoryItem item, IRepositoryApi sock) throws Exception {
		messages.clear();
		
		List<FMDTDataSource> datasources = new ArrayList<FMDTDataSource>();
		
		String modelXml = ""; //$NON-NLS-1$
		
		try {
			modelXml = sock.getRepositoryService().loadModel(item);
		} catch (Exception e) {
			throw new Exception(Messages.BirtFmdtExtractor_1 + e.getMessage());
		}
		
		Document doc = DocumentHelper.parseText(modelXml);
		//.element("report")
		//.elements("property").get(0)
		//doc.getRootElement().element("data-sources").element("oda-data-source").elements("property").get(0)
		List<Element> list = doc.getRootElement().element("data-sources").elements("oda-data-source"); //$NON-NLS-1$ //$NON-NLS-2$
		
		for (Element ele : list) {
			try {
				FMDTDataSource source = parseBIRTProps(ele);
				datasources.add(source);
			} catch (IllegalArgumentException ex) {
				Message msg = new Message(item.getId(), item.getItemName(), 
						Message.WARNING, ex.getMessage());
				
				messages.add(msg);
			}
		}

		return datasources;
	}

	
	private static FMDTDataSource parseBIRTProps(Element element) throws IllegalArgumentException {
		FMDTDataSource source = new FMDTDataSource();
		
//        <list-property name="privateDriverProperties">
//        <ex-property>
//            <name>CONNECTION_NAME</name>
//			  <value>Default</value>
		//extensionID="bpm.metadata.birt.oda.runtime
		
		if (!element.attributeValue("extensionID").equals("bpm.metadata.birt.oda.runtime")) { //$NON-NLS-1$ //$NON-NLS-2$
			OurLogger.info(Messages.BirtFmdtExtractor_6);
			
			throw new IllegalArgumentException(Messages.BirtFmdtExtractor_0 + element.attributeValue("extensionID")); //$NON-NLS-1$
		}
		
		String dsName = element.attributeValue("name"); //$NON-NLS-1$
		source.setDataSourceName(dsName);
		
		String conName = element.element("list-property").element("ex-property").element("value").getText(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		source.setConnectionName(conName);
		
		for (Object o : element.elements("property")) { //$NON-NLS-1$
			Element ele = (Element) o;
			String propname = ele.attributeValue("name"); //$NON-NLS-1$
			String propvalue = ele.getText();
			
			if (propname.equals("USER"))  //$NON-NLS-1$
				source.setUser(propvalue);
			else if (propname.equals("PASSWORD"))  //$NON-NLS-1$
				source.setPass(propvalue);
			else if (propname.equals("URL"))  //$NON-NLS-1$
				source.setUrl(propvalue);
			else if (propname.equals("DIRECTORY_ITEM_ID")) { //$NON-NLS-1$
				source.setDirItemId(Integer.parseInt(propvalue));
			}
			else if (propname.equals("BUSINESS_MODEL"))  //$NON-NLS-1$
				source.setBusinessModel(propvalue);
			else if (propname.equals("BUSINESS_PACKAGE"))  //$NON-NLS-1$
				source.setBusinessPackage(propvalue);
			else if (propname.equals("GROUP_NAME"))  //$NON-NLS-1$
				source.setGroupName(propvalue);
			//else 
		}
		
		return source;
	}


	@Override
	public List<Message> getMessages() {
		return messages;
	}
}
