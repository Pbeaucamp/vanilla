package bpm.es.datasource.analyzer.remapper.internal;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

import bpm.es.datasource.analyzer.remapper.IRemapperPerformer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class FdDicoRemapDependancy implements IRemapperPerformer{

	
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
	
	public FdDicoRemapDependancy(Document sourceXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem, IRepositoryApi sourceConnection, IRepositoryApi targetConnection, String targetGroupName){
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
		
		
//		HashMap nameSpaceMap = new HashMap();

		Element root = sourceXmlDocument.getRootElement();
		XPath xpath = new Dom4jXPath( "//dependancies/dependantDirectoryItemId" );

		/*
		 * dependancy
		 */
		for(Element e : (List<Element>)xpath.selectNodes(sourceXmlDocument)){
			
			if (e.getStringValue().equals(orginialItem.getId() + "")){
				e.setText(newItem.getId() + "");
				
				xpath = new Dom4jXPath( "//directoryItemId" );
				for(Element _id : (List<Element>)(xpath.selectNodes(sourceXmlDocument))){
					if (_id.getStringValue().equals(orginialItem.getId() + "")){
						_id.setText(newItem.getId() + "");
					}
				}
			}
		}
		/*
		 * oda
		 */
		for(Element e : (List<Element>)root.selectNodes("//dataSource/odaExtensionDataSourceId")){
			
			
			if (e.getStringValue().equals("bpm.metadata.birt.oda.runtime") ||
				e.getStringValue().equals("bpm.csv.oda.runtime") ||
				e.getStringValue().equals("bpm.excel.oda.runtime") ||
				e.getStringValue().equals("bpm.fwr.oda.runtime")){
				
				for(Element _id : (List<Element>)e.getParent().selectNodes("//publicProperty[@name='DIRECTORY_ITEM_ID']|//privateProperty[@name='repository.item.id']|//privateProperty[@name='FWREPORT_ID']")){
					if (_id.getStringValue().equals(orginialItem.getId() + "")){
						_id.setText(newItem.getId() + "");
					}
				}
				
				
				for(Element _id : (List<Element>)e.getParent().selectNodes("//publicProperty[@name='URL']")){
					if (_id.getStringValue().equals(sourceRepositoryUrl + "")){
						_id.setText(targetRepositoryUrl);
					}
				}
				for(Element _id : (List<Element>)e.getParent().selectNodes("//publicProperty[@name='USER']")){
					if (_id.getStringValue().equals(sourceRepositoryLogin + "")){
						_id.setText(targetRepositoryLogin);
					}
				}
				
				for(Element _id : (List<Element>)e.getParent().selectNodes("//publicProperty[@name='PASSWORD']")){
					if (_id.getStringValue().equals(sourceRepositoryPassword + "")){
						_id.setText(targetRepositoryPassword);				
					}
				}
				
				for(Element _id : (List<Element>)e.getParent().selectNodes("//publicProperty[@name='GROUP_NAME']")){
//					if (_id.getStringValue().equals(sourceRepositoryPassword + "")){
						_id.setText(targetGroupName);				
//					}
				}
			}

			
		}
	}

	public String getTaskPerfomed() {
		return "Replace Items References(ExtDoc, Fav, Reports)  " + orginialItem.getItemName() + " by " + newItem.getItemName() + " in FdDictionary model";
	}
}
