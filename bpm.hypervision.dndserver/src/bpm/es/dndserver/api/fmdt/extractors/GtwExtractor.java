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

public class GtwExtractor implements IFMDTExtractor{
	private List<Message> messages = new ArrayList<Message>();
	
	@Override
	public List<Message> getMessages() {
		return messages;
	}
	
	@Override
	public List<FMDTDataSource> extractFmdtDataSources(RepositoryItem item, IRepositoryApi sock) throws Exception {
		messages.clear();
		List<FMDTDataSource> datasources = new ArrayList<FMDTDataSource>();
		
		String modelXml = ""; //$NON-NLS-1$
		
		try {
			modelXml = sock.getRepositoryService().loadModel(item);
		} catch (Exception e) {
			throw new Exception(Messages.GtwExtractor_1 + e.getMessage());
		}
		
		Document doc = DocumentHelper.parseText(modelXml);
		Element root = doc.getRootElement();
		
		for(Element e : (List<Element>)root.selectNodes("//fmdtInput")){ //$NON-NLS-1$
			FMDTDataSource ds = new FMDTDataSource();
			ds.setBusinessModel(e.element("businessModelName").getStringValue()); //$NON-NLS-1$
			ds.setBusinessPackage(e.element("businessPackageName").getStringValue()); //$NON-NLS-1$
			ds.setConnectionName(e.element("connectionName").getStringValue()); //$NON-NLS-1$
			ds.setDataSourceName(e.element("name").getStringValue()); //$NON-NLS-1$
			ds.setDirItemId(Integer.parseInt(e.element("repositoryItemId").getStringValue())); //$NON-NLS-1$
			
			String serverName = e.element("serverRef").getStringValue(); //$NON-NLS-1$
			for(Element el : (List<Element>)e.getParent().element("servers").elements("repositoryServer")){ //$NON-NLS-1$ //$NON-NLS-2$
				if (el.element("name").getStringValue().equals(serverName)){ //$NON-NLS-1$
					ds.setUrl(el.element("repositoryConnection").element("url").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
					ds.setUser(el.element("repositoryConnection").element("login").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
					ds.setPass(el.element("repositoryConnection").element("password").getStringValue()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			datasources.add(ds);
		}
		
		//odaInput
		for(Element e : (List<Element>)root.selectNodes("//odaInput|//odaInputWithParameters")){ //$NON-NLS-1$
			String extId = e.element("odaExtensionId").getStringValue(); //$NON-NLS-1$
			if (extId.equals("bpm.metadata.birt.oda.runtime")){ //$NON-NLS-1$
				FMDTDataSource ds = new FMDTDataSource();
				ds.setDataSourceName(e.element("name").getStringValue()); //$NON-NLS-1$
				for(Element _e : (List<Element>)e.element("publicDataSource").elements("property")){ //$NON-NLS-1$ //$NON-NLS-2$
					if (_e.element("name").getStringValue().equals("DIRECTORY_ITEM_ID") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setDirItemId(Integer.parseInt(_e.element("value").getStringValue())); //$NON-NLS-1$
					}
					if (_e.element("name").getStringValue().equals("BUSINESS_MODEL") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setBusinessModel(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
					if (_e.element("name").getStringValue().equals("BUSINESS_PACKAGE") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setBusinessPackage(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
					if (_e.element("name").getStringValue().equals("GROUP_NAME") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setGroupName(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
					if (_e.element("name").getStringValue().equals("USER") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setUser(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
					if (_e.element("name").getStringValue().equals("PASSWORD") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setPass(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
					if (_e.element("name").getStringValue().equals("URL") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setUrl(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
				}
				for(Element _e : (List<Element>)e.element("privateDataSource").elements("property")){ //$NON-NLS-1$ //$NON-NLS-2$
					if (_e.element("name").getStringValue().equals("CONNECTION_NAME") ){ //$NON-NLS-1$ //$NON-NLS-2$
						ds.setConnectionName(_e.element("value").getStringValue()); //$NON-NLS-1$
					}
				}
				

				datasources.add(ds);
			}
			
		}
		return datasources;
	}
}
