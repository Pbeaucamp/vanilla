package bpm.es.datasource.analyzer.remapper.internal;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class GtwRemap implements IRemapperPerformer{

	
	private Document sourceXmlDocument; 
	private RepositoryItem orginialItem;
	private RepositoryItem newItem;
	
	private String targetRepositoryUrl;
	private String targetRepositoryLogin;
	private String targetRepositoryPassword;
	private String targetGroupName;
	
	private String sourceRepositoryUrl;
	private String sourceRepositoryLogin;
	private String sourceRepositoryPassword;

	
	public GtwRemap(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem, IRepositoryApi sourceConnection, IRepositoryApi targetConnection, String targetGroupName){
		this.sourceXmlDocument = sourceXmlDocument;
		this.orginialItem = orginialItem;
		this.newItem = newItem;
		
		this.targetRepositoryUrl = targetConnection.getContext().getRepository().getUrl();
		this.targetRepositoryLogin = targetConnection.getContext().getVanillaContext().getLogin();
		this.targetRepositoryPassword = targetConnection.getContext().getVanillaContext().getPassword();
		this.targetGroupName = targetGroupName;
		
		this.sourceRepositoryUrl = sourceConnection.getContext().getRepository().getUrl();
		this.sourceRepositoryLogin = sourceConnection.getContext().getVanillaContext().getLogin();
		this.sourceRepositoryPassword = sourceConnection.getContext().getVanillaContext().getPassword();

	}

	public boolean checkModification() throws Exception {
		// TODO check if the is new FMDT model can replace the old one
		return true;
	}

	public void performModification() throws Exception {
		

		Element root = sourceXmlDocument.getRootElement();
		
		
//		XPath xpath = new Dom4jXPath( "//oda-data-source[@extensionID='bpm.metadata.birt.oda.runtime']|//oda-data-source[@extensionID='bpm.csv.oda.runtime']|//oda-data-source[@extensionID='bpm.excel.oda.runtime']" );
		
		
		
		for(Element e : (List<Element>)root.selectNodes("//fmdtInput/repositoryItemId|//subTransformation/definition/repositoryItemId|//olapDimensionInput/definition/repositoryItemId|//olapInput/definition/repositoryItemId")){
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
			}
		}
		
		//odaInput
		for(Element e : (List<Element>)root.selectNodes("//odaInput|//odaInputWithParameters")){
			String extId = e.element("odaExtensionId").getStringValue();
			if (extId.equals("bpm.metadata.birt.oda.runtime") || extId.equals("bpm.excel.oda.runtime") || extId.equals("bpm.csv.oda.runtime")){
				
				Element dirIt = null;
				Element url = null;
				Element login = null;
				Element pass = null;
				Element group = null;
				
				
				for(Element _e : (List<Element>)e.element("publicDataSource").elements("property")){
					if (_e.element("name").getStringValue().equals("DIRECTORY_ITEM_ID") && _e.element("value").getStringValue().equals(orginialItem.getId() + "")){
						dirIt = _e;
					}
					if (_e.element("name").getStringValue().equals("URL") && _e.element("value").getStringValue().equals(sourceRepositoryUrl)){
						url = _e;
					}
					if (_e.element("name").getStringValue().equals("USER") && _e.element("value").getStringValue().equals(sourceRepositoryLogin)){
						login = _e;
					}
					if (_e.element("name").getStringValue().equals("PASSWORD") && _e.element("value").getStringValue().equals(sourceRepositoryPassword)){
						pass = _e;
					}
					if (_e.element("name").getStringValue().equals("GROUP_NAME")){
						group = _e;
					}
				}
				
				if (dirIt != null && login != null && pass != null && url != null){
					dirIt.element("value").setText(newItem.getId() + "");
					login.element("value").setText(targetRepositoryLogin);
					pass.element("value").setText(targetRepositoryPassword);
					url.element("value").setText(targetRepositoryUrl);
					
					if (group != null && targetGroupName != null){
						group.element("value").setText(targetGroupName);
					}
				}
			}
			
		}
		
		//repositoryServers
		for(Element e : (List<Element>)root.element("servers").elements("repositoryServer")){
			if (e.element("repositoryConnection").element("url").getStringValue().equals(sourceRepositoryUrl) &&
				e.element("repositoryConnection").element("login").getStringValue().equals(sourceRepositoryLogin) &&
				e.element("repositoryConnection").element("password").getStringValue().equals(sourceRepositoryPassword)){
				
				e.element("repositoryConnection").element("url").setText(targetRepositoryUrl);
				e.element("repositoryConnection").element("login").setText(targetRepositoryLogin);
				e.element("repositoryConnection").element("password").setText(targetRepositoryPassword);
			}
		}

	}

	public String getTaskPerfomed() {
		return "Replace Items References  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in BIG model";
	}
}
