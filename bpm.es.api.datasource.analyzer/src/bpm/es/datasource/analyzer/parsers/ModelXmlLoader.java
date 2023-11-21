package bpm.es.datasource.analyzer.parsers;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;



public class ModelXmlLoader {
	private IRepositoryApi sock;
	private IRepository repositoryContent;

	
	public ModelXmlLoader(IRepositoryApi sock, IRepository content){
		this.sock = sock;
		this.repositoryContent = content;
	
	}
	
	private void loadRepository() throws Exception{
		repositoryContent = new Repository(sock);
	}
	
	public IRepository getRepositoryContent() throws Exception{
		if (repositoryContent == null){
			try{
				loadRepository();
			}catch(Exception ex){
				throw new Exception("Unable to load Repository content :" + ex.getMessage());
			}
		}
		return repositoryContent;
	}
	
	public String loadXml(RepositoryItem item) throws Exception{
		if (repositoryContent == null){
			try{
				loadRepository();
			}catch(Exception ex){
				throw new Exception("Unable to load Repository content : " + ex.getMessage());
			}
		}
		
		try{
			return sock.getRepositoryService().loadModel(item);
		}catch(Exception ex){
			throw new Exception("Unable to load model Xml id= " + item.getId() + " for type " + IRepositoryApi.TYPES_NAMES[item.getType()] + ": " + ex.getMessage());
		}
	}
}
