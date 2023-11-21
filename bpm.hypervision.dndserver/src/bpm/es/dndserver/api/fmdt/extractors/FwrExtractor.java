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
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FwrExtractor implements IFMDTExtractor{
	private List<Message> messages = new ArrayList<Message>();
	
	@Override
	public List<Message> getMessages() {
		return messages;
	}
	
	public List<FMDTDataSource> extractFmdtDataSources(RepositoryItem item,IRepositoryApi sock) throws Exception {
		List<FMDTDataSource> datasources = new ArrayList<FMDTDataSource>();
		
		String modelXml = ""; //$NON-NLS-1$
		
		try {
			modelXml = sock.getRepositoryService().loadModel(item);
		} catch (Exception e) {
			throw new Exception(Messages.FwrExtractor_1 + e.getMessage());
		}
		
		Document doc = DocumentHelper.parseText(modelXml);
		
		for(Element e : (List<Element>) doc.getRootElement().selectNodes("//datasource")){ //$NON-NLS-1$
			
			FMDTDataSource ds = new FMDTDataSource();
			ds.setBusinessModel(e.element("model").getStringValue()); //$NON-NLS-1$
			ds.setBusinessPackage(e.element("package").getStringValue()); //$NON-NLS-1$
			ds.setConnectionName(e.element("connection").getStringValue()); //$NON-NLS-1$
			ds.setDataSourceName(e.element("name").getStringValue()); //$NON-NLS-1$
			ds.setDirItemId(Integer.parseInt(e.element("itemid").getStringValue())); //$NON-NLS-1$
			ds.setGroupName(e.element("group").getStringValue()); //$NON-NLS-1$
			ds.setPass(e.element("password").getStringValue()); //$NON-NLS-1$
			ds.setUrl(e.element("url").getStringValue()); //$NON-NLS-1$
			ds.setUser(e.element("user").getStringValue()); //$NON-NLS-1$
			datasources.add(ds);
		}
		
		return datasources;
	}
}
