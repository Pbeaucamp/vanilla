package bpm.vanilla.repository.services;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.services.IDataProviderService;
import bpm.vanilla.platform.core.repository.services.IRepositoryImpactService;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.datasprovider.ItemsDP;

public class ServiceDatasourceImpl implements IDataProviderService, IRepositoryImpactService{
	
	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;
	
	public ServiceDatasourceImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
	}

	@Override
	public int add(DataSource d) throws Exception {
		int id = component.getRepositoryDao(repositoryId).getDatasourceDao().add(d);
		return id;
	}

	@Override
	public boolean del(DataSource d) throws Exception {
		component.getRepositoryDao(repositoryId).getDatasourceDao().delete(d);
		
		for(DataSourceImpact i : component.getRepositoryDao(repositoryId).getDatasourceImpactDao().getForDataSourceId(d.getId())){
			component.getRepositoryDao(repositoryId).getDatasourceImpactDao().delete(i);
		}
		
		return true;
	}

	@Override
	public List<DataSource> getAllDatasources() throws Exception {
		return component.getRepositoryDao(repositoryId).getDatasourceDao().getAll();
	}

	@Override
	public DataSource getById(int id) throws Exception {
		DataSource ds = component.getRepositoryDao(repositoryId).getDatasourceDao().getById(id);
		return ds;
	}

	@Override
	public List<DataSourceImpact> getForDataSourceId(int dataSourceId) throws Exception {
		return component.getRepositoryDao(repositoryId).getDatasourceImpactDao().getForDataSourceId(dataSourceId);
	}

	@Override
	public void update(DataSource d) throws Exception {
		component.getRepositoryDao(repositoryId).getDatasourceDao().update(d);
	}

	@Override
	public void breakLink(int itemId, int datasProviderId) throws Exception {
		ItemsDP existing = component.getRepositoryDao(repositoryId).getItemsDpDao().findByDpAndItem(datasProviderId, itemId);
		if(existing != null) {
			component.getRepositoryDao(repositoryId).getItemsDpDao().delete(existing);
		}
		else {
			ItemsDP it = new ItemsDP();
			it.setDatasProviderId(datasProviderId);
			it.setItemId(itemId);
			component.getRepositoryDao(repositoryId).getItemsDpDao().save(it);
		}
	}

	@Override
	public int createDatasProvider(DatasProvider dp) throws Exception {
		return component.getRepositoryDao(repositoryId).getDataproviderDao().save(dp);
	}

	@Override
	public void delete(DatasProvider dp) throws Exception {
		component.getRepositoryDao(repositoryId).getDataproviderDao().delete(dp);
	}

	@Override
	public List<DatasProvider> getAll() throws Exception {
		return component.getRepositoryDao(repositoryId).getDataproviderDao().findAll();
	}

	@Override
	public List<DatasProvider> getForItem(int itemId) throws Exception {
		List<DatasProvider> dps = new ArrayList<DatasProvider>();
		for(ItemsDP dp : component.getRepositoryDao(repositoryId).getItemsDpDao().findByItemId(itemId)) {
			dps.add(component.getRepositoryDao(repositoryId).getDataproviderDao().findById(dp.getDatasProviderId()));
		}
		return dps;
	}

	@Override
	public String link(int datasProviderID, int itemId) throws Exception {
		ItemsDP existing = component.getRepositoryDao(repositoryId).getItemsDpDao().findByDpAndItem(datasProviderID, itemId);
		if(existing != null) {
			component.getRepositoryDao(repositoryId).getItemsDpDao().delete(existing);
		}
		else {
			ItemsDP it = new ItemsDP();
			it.setDatasProviderId(datasProviderID);
			it.setItemId(itemId);
			component.getRepositoryDao(repositoryId).getItemsDpDao().save(it);
		}
		return "";
	}

	@Override
	public void update(DatasProvider element) throws Exception {
		component.getRepositoryDao(repositoryId).getDataproviderDao().update(element);
	}

	@Override
	public DatasProvider getDatasProvider(int id) throws Exception {
		return component.getRepositoryDao(repositoryId).getDataproviderDao().findById(id);
	}

	@Override
	public List<DataSource> getDatasourcesByType(String extensionId) throws Exception {
		return component.getRepositoryDao(repositoryId).getDatasourceDao().getByType(extensionId);
	}

}
