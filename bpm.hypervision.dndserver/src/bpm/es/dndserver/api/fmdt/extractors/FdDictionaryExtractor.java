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

public class FdDictionaryExtractor implements IFMDTExtractor{
	private List<Message> messages = new ArrayList<Message>();
	
	public List<FMDTDataSource> extractFmdtDataSources(RepositoryItem item,IRepositoryApi sock) throws Exception {
		List<FMDTDataSource> datasources = new ArrayList<FMDTDataSource>();
//		<dictionary version="v2.0" name="Dicometa">
//	  <dataSource>
//	    <name>sourceMetaSample</name>
//	    <odaExtensionDataSourceId>bpm.metadata.birt.oda.runtime</odaExtensionDataSourceId>
//	    <odaExtensionId>bpm.metadata.birt.oda.runtime</odaExtensionId>
//	    <odaDriverClassName>bpm.metadata.birt.oda.runtime.impl.Driver</odaDriverClassName>
//	    <publicProperty name="URL">http://localhost:8080/BIRepository</publicProperty>
//	    <publicProperty name="BUSINESS_MODEL">Sampledata</publicProperty>
//	    <publicProperty name="BUSINESS_PACKAGE">Sales</publicProperty>
//	    <publicProperty name="USER">system</publicProperty>
//	    <publicProperty name="DIRECTORY_ITEM_ID">1</publicProperty>
//	    <publicProperty name="GROUP_NAME">System</publicProperty>
//	    <publicProperty name="PASSWORD">system</publicProperty>
//	    <privateProperty name="CONNECTION_NAME">Default</privateProperty>
		String modelXml = ""; //$NON-NLS-1$
		
		try {
			modelXml = sock.getRepositoryService().loadModel(item);
		} catch (Exception e) {
			throw new Exception(Messages.FdDictionaryExtractor_1 + e.getMessage());
		}
		
		Document doc = DocumentHelper.parseText(modelXml);
		
		List<Element> list = doc.getRootElement().elements("dataSource"); //$NON-NLS-1$
		
		for (Element ele : list) {
			try {
				if (ele.element("odaExtensionDataSourceId") //$NON-NLS-1$
						.equals(Messages.FdDictionaryExtractor_4)) {
					OurLogger.info(Messages.FdDictionaryExtractor_5);
					
					throw new IllegalArgumentException(Messages.FdDictionaryExtractor_6 
							+ ele.attributeValue("odaExtensionDataSourceId")); //$NON-NLS-1$
				}
					
				FMDTDataSource source = new FMDTDataSource();
				source.setDataSourceName(ele.element("name").getText()); //$NON-NLS-1$
				
				for (Element e : (List<Element>)ele.elements("publicProperty")){ //$NON-NLS-1$
					if (e.attributeValue("name").equals("BUSINESS_MODEL")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setBusinessModel(e.getText());
					}
					else if (e.attributeValue("name").equals("BUSINESS_PACKAGE")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setBusinessPackage(e.getText());
					}
					else if (e.attributeValue("name").equals("CONNECTION_NAME")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setConnectionName(e.getText());
					}
					else if (e.attributeValue("name").equals("DIRECTORY_ITEM_ID")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setDirItemId(Integer.parseInt(e.getText()));
					}
					else if (e.attributeValue("name").equals("URL")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setUrl(e.getText());
					}
					else if (e.attributeValue("name").equals("USER")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setUser(e.getText());
					}
					else if (e.attributeValue("name").equals("PASSWORD")){ //$NON-NLS-1$ //$NON-NLS-2$
						source.setPass(e.getText());
					}
				}
				
				if (source.getConnectionName() == null){
					source.setConnectionName("Default"); //$NON-NLS-1$
				}
				datasources.add(source);
			} catch (IllegalArgumentException ex) {
				Message msg = new Message(item.getId(), item.getItemName(), 
						Message.WARNING, ex.getMessage());
				
				messages.add(msg);
			}
		}
		
		return datasources;
	}
	@Override
	public List<Message> getMessages() {
		return messages;
	}
}
