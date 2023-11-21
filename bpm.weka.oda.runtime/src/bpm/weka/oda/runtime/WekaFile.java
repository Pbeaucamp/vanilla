package bpm.weka.oda.runtime;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class WekaFile {

	private IVanillaAPI vanillaApi;
	private IRepositoryApi repositoryApi;
	private RepositoryItem item;
	private Instances wekaFile;
	private String localfile;
	
	public WekaFile(String url, String user, String password, int groupId, int repositoryId, int itemId) {
		
		try {
			IVanillaContext vanillaContext = new BaseVanillaContext(url, user, password);
			vanillaApi = new RemoteVanillaPlatform(vanillaContext);
			
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
			Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
			
			repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(
					vanillaContext, 
					group, 
					rep));
			
			item = repositoryApi.getRepositoryService().getDirectoryItem(itemId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public WekaFile(String localfile) {
		this.localfile = localfile;
	}

	public List<Attribute> getColumns() throws Exception {
		if(wekaFile == null) {
			wekaFile = loadWekaFile();
		}
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		
		for(int i = 0 ; i < wekaFile.numAttributes() ; i++) {
			attributes.add(wekaFile.attribute(i));
		}
		
		return attributes;
	}

	private Instances loadWekaFile() throws Exception {
		InputStream wekaInputStream = null;
		if(item != null) {
			wekaInputStream = repositoryApi.getDocumentationService().importExternalDocument(item);
		}
		else {
			wekaInputStream = new FileInputStream(localfile);
		}
		DataSource source = new DataSource(wekaInputStream);
		return source.getDataSet();
	}
	
	public Instances getInstances() throws Exception {
		if(wekaFile == null) {
			wekaFile = loadWekaFile();
		}
		return wekaFile;
	}
	
}
