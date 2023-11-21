package bpm.metadata.tools;

import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataLoader {

	public static IBusinessPackage loadMetadata(DatasourceFmdt datasource) throws Exception {
		
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL),
				datasource.getUser(),
				datasource.getPassword());
		
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(datasource.getGroupId());
		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(datasource.getRepositoryId());
		
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaApi.getVanillaContext(), group, repository));
		
		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(datasource.getItemId());
		
		String xml = repositoryApi.getRepositoryService().loadModel(item);
		
		List<IBusinessModel> models = MetaDataReader.read(group.getName(), IOUtils.toInputStream(xml, "UTF-8"), repositoryApi, false);
		
		for(IBusinessModel model : models) {
			if(model.getName().equals(datasource.getBusinessModel())) {
				for(IBusinessPackage pack : model.getBusinessPackages(group.getName())) {
					if(pack.getName().equals(datasource.getBusinessPackage())) {
						return pack;
					}
				}
			}
		}
		throw new Exception("Package not found");
		
	}
	
	
}
