package bpm.es.datasource.analyzer.remapper.internal;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.MD5Helper;


public class FwrRemapOda implements IRemapperPerformer{

	
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
	
	public FwrRemapOda(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem, IRepositoryApi sourceConnection, IRepositoryApi targetConnection, String targetGroupName){
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
		
		
		for(Element e : (List<Element>)root.selectNodes("//cfwr/metadata/metadataId|//xfwr/metadata/metadataId|//olapfwr/metadata/metadataId")){
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
			}
		}
		for(Element e : (List<Element>)root.selectNodes("//datasource/itemid")){
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
			}
		}
		
		for(Element e : (List<Element>)root.selectNodes("//datasource")){
			boolean match = true;
			for(Element el : (List<Element>)e.elements()){
				if (el.getName().equals("user") && !el.getStringValue().equals(sourceRepositoryLogin)){
					match = false;
				}
				else if (el.getName().equals("url") && !el.getStringValue().equals(sourceRepositoryUrl)){
					match = false;
				}
				else if (el.getName().equals("password") && !el.getStringValue().equals(sourceRepositoryPassword) && !el.getStringValue().equals(MD5Helper.encode(sourceRepositoryPassword))){
					match = false;
				}
			}
			
			if (match){
				e.element("user").setText(targetRepositoryLogin);
				e.element("password").setText(targetRepositoryPassword);
				e.element("url").setText(targetRepositoryUrl);
				if (targetGroupName != null && !targetGroupName.trim().isEmpty()){
					e.element("group").setText(targetGroupName);
				}
			}
			
		}

	}

	public String getTaskPerfomed() {
		return "Replace Oda Items References  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in FWR model";
	}
}
