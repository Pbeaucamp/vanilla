package bpm.es.datasource.analyzer.remapper.internal;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class BirtRemapOda implements IRemapperPerformer{

	
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
	
	
	public BirtRemapOda(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem, IRepositoryApi sourceConnection, IRepositoryApi targetConnection, String targetGroupName){
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
		
		
		HashMap nameSpaceMap = new HashMap();

		Element root = sourceXmlDocument.getRootElement();
		if (root.getNamespaceURI() != null){
			nameSpaceMap.put("birt", root.getNamespaceURI());
		}
		
		XPath xpath = new Dom4jXPath( "//birt:oda-data-source[@extensionID='bpm.metadata.birt.oda.runtime']|//birt:oda-data-source[@extensionID='bpm.csv.oda.runtime']|//birt:oda-data-source[@extensionID='bpm.excel.oda.runtime']|//birt:oda-data-source[@extensionID='bpm.fwr.oda.runtime']" );
		
		SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext( nameSpaceMap);
		xpath.setNamespaceContext(nmsCtx);

		
		for(Element e : (List<Element>)xpath.selectNodes(sourceXmlDocument)){
			
			//items
			xpath = new Dom4jXPath( "//birt:property[@name='DIRECTORY_ITEM_ID']|//birt:privateProperty[@name='repository.item.id']|//birt://privateProperty[@name='FWREPORT_ID']" );
			xpath.setNamespaceContext(nmsCtx);

			for(Element _id : (List<Element>)xpath.selectNodes(e)){
				if (_id.getStringValue().equals(orginialItem.getId() + "")){
					_id.setText(newItem.getId() + "");
				}
			}
			
			//repositoryUrl
			xpath = new Dom4jXPath( "//birt:property[@name='URL']" );
			xpath.setNamespaceContext(nmsCtx);

			for(Element _id : (List<Element>)xpath.selectNodes(e)){
				if (_id.getStringValue().equals(sourceRepositoryUrl)){
					_id.setText(targetRepositoryUrl + "");
				}
			}
			
			//login
			xpath = new Dom4jXPath( "//birt:property[@name='USER']" );
			xpath.setNamespaceContext(nmsCtx);

			for(Element _id : (List<Element>)xpath.selectNodes(e)){
				if (_id.getStringValue().equals(sourceRepositoryLogin)){
					_id.setText(targetRepositoryLogin + "");
				}
			}
			
			
			//password
			xpath = new Dom4jXPath( "//birt:property[@name='PASSWORD']" );
			xpath.setNamespaceContext(nmsCtx);

			for(Element _id : (List<Element>)xpath.selectNodes(e)){
				if (_id.getStringValue().equals(sourceRepositoryPassword)){
					_id.setText(targetRepositoryPassword + "");
				}
			}
			
			
			//password
			xpath = new Dom4jXPath( "//birt:property[@name='GROUP_NAME']" );
			xpath.setNamespaceContext(nmsCtx);

			for(Element _id : (List<Element>)xpath.selectNodes(e)){
//				if (_id.getStringValue().equals(sourceRepositoryPassword)){
					_id.setText(targetGroupName + "");
//				}
			}
		}

	}

	public String getTaskPerfomed() {
		return "Replace Oda Item Reference  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in BIRT model";
	}
}
