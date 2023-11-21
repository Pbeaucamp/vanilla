package bpm.vanilla.repository.ui.versionning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Activator;

/**
 * This class provide helpers functions to keep the informations to perform chekin after
 * a chekout have been done.
 * 
 * The modelProperties contains the fileNames and a Property object containing al information 
 * to perform the chekin on the given file.
 * 
 * 
 * 
 * @author LCA
 *
 */
public final class VersionningManager {

	private  HashMap<String, Properties> modelProperties = new HashMap<String, Properties>();
	
	private static VersionningManager instance = null;
	
	
	private VersionningManager(){}
	
	public static VersionningManager getInstance(){
		if (instance == null){
			instance = new VersionningManager();
		}
		return instance;
	}
	
	
	/**
	 * save the checkout information for this fileName and this connection 
	 * @param fileName
	 * @param sock
	 */
	public void saveChekout(String fileName, IRepositoryApi sock, RepositoryItem item){
		try{
			Properties p = new Properties();
			p.setProperty("serverUrl", ((IRepositoryApi)sock).getContext().getRepository().getUrl());
			p.setProperty("username", ((IRepositoryApi)sock).getContext().getVanillaContext().getLogin());
			p.setProperty("password", ((IRepositoryApi)sock).getContext().getVanillaContext().getPassword());
			p.setProperty("groupId", ((IRepositoryApi)sock).getContext().getGroup().getId() + "");
			
			
			p.setProperty("directoryItemId", item.getId() + "");
			
			modelProperties.put(fileName, p);
			
			save(Platform.getInstallLocation().getURL().getPath()  + "/resources/" + Activator.versionningFileName );
			
			//update checkinstate
			ISourceProviderService service = (ISourceProviderService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
			SessionSourceProvider provider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CHECK_IN_STATE);
			provider.setCheckedIn(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public Properties getCheckoutInfos(String fileName){
		for(String s : modelProperties.keySet()){
			if (s.equals(fileName)){
				
				Properties p = modelProperties.get(s);
				
				return p;
			}
		}
		
		return null;
	}
	
	public void performCheckin(String fileName){
		List<String> keys = new ArrayList<String>(modelProperties.keySet());
		for(String s : keys){
			if (s.equals(fileName)){
				modelProperties.remove(s);
			}
		}
		//update checkinstate
		ISourceProviderService service = (ISourceProviderService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
		SessionSourceProvider provider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CHECK_IN_STATE);
		provider.setCheckedIn(false);


	}
	
	/**
	 * save the informations
	 * @param fileName
	 */
	protected void save(String fileName) throws Exception{
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement("versionning");
		
		for(String key : modelProperties.keySet()){
			Element checkout = root.addElement("checkout");
			checkout.addElement("fileName").setText(key);
			checkout.addElement("serverUrl").setText(modelProperties.get(key).getProperty("serverUrl"));
			checkout.addElement("username").setText(modelProperties.get(key).getProperty("username"));
			checkout.addElement("password").setText(modelProperties.get(key).getProperty("password"));
			
			if (modelProperties.get(key).getProperty("groupId") != null){
				checkout.addElement("groupId").setText(modelProperties.get(key).getProperty("groupId"));
			}
			
			checkout.addElement("directoryItemId").setText(modelProperties.get(key).getProperty("directoryItemId"));
		}
		
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(fileName), OutputFormat.createPrettyPrint());
		xmlWriter.write(doc);
		xmlWriter.close();
	}
	
	
	public void load(String fileName) throws Exception{
		File f = new File(fileName);
		if (!f.exists()){
			f.createNewFile();
			save(fileName);
			
		}
		
		Document doc = DocumentHelper.parseText(IOUtils.toString(new FileInputStream(fileName), "UTF-8"));
		Element root = doc.getRootElement();
		
		for(Object o : root.elements("checkout")){
			Element e = (Element)o;
			
			Properties p = new Properties();
			p.setProperty("serverUrl", e.elementText("serverUrl"));
			p.setProperty("username", e.elementText("username"));
			p.setProperty("password", e.elementText("password"));
			p.setProperty("directoryItemId", e.elementText("directoryItemId"));
			
			if (e.element("groupId") != null){
				p.setProperty("groupId", e.elementText("groupId"));
			}
			
			modelProperties.put(e.elementText("fileName"), p);
			
		}
		
	}
	
}
