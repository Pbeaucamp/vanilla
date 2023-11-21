package bpm.es.datasource.analyzer.remapper.internal;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class WkfRemap implements IRemapperPerformer{

	
	private Document sourceXmlDocument; 
	private RepositoryItem orginialItem;
	private RepositoryItem newItem;
	
	private String targetRepositoryUrl;
	private String targetRepositoryLogin;
	private String targetRepositoryPassword;
	
	private String sourceRepositoryUrl;
	private String sourceRepositoryLogin;
	private String sourceRepositoryPassword;

	
	
	public WkfRemap(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem, IRepositoryApi sourceConnection, IRepositoryApi targetConnection){
		this.sourceXmlDocument = sourceXmlDocument;
		this.orginialItem = orginialItem;
		this.newItem = newItem;
		
		this.targetRepositoryUrl = targetConnection.getContext().getRepository().getUrl();
		this.targetRepositoryLogin = targetConnection.getContext().getVanillaContext().getLogin();
		this.targetRepositoryPassword = targetConnection.getContext().getVanillaContext().getPassword();

		
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
		
		
		
		for(Element e : (List<Element>)root.selectNodes("//BIWActivity/BIWObject/id|//gatewayActivity/gatewayObject/id|//reportActivity/birtObject/id|//burstActivity/birtObject/id|//taskListActivity/biRepositoryObject/id|//interfaceActivity/interfaceObject/id")){
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
			}
		}
		
		for(Element e : (List<Element>) root.elements("vanillaRepositoryServer")){
			
			Element url = e.element("url");
			if (!url.getStringValue().equals(sourceRepositoryUrl)){
				url = null;
			}
			Element login = e.element("login");
			if (!login.getStringValue().equals(sourceRepositoryLogin)){
				login = null;
			}
			Element pass = e.element("password");
			if (!pass.getStringValue().equals(sourceRepositoryPassword)){
				pass = null;
			}
			
			
			
			if (login != null && pass != null && url != null){
				
				login.setText(targetRepositoryLogin);
				pass.setText(targetRepositoryPassword);
				url.setText(targetRepositoryUrl);
			}
		}
		

	}

	public String getTaskPerfomed() {
		return "Replace Items References  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in WKF model";
	}
}
