package bpm.odata.service.data;

import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;

import bpm.odata.service.KpiEdmProvider;
import bpm.odata.service.MetadataEdmProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;

public class DataManager {
	
	private IVanillaContext vanillaContext;

	private MetadataManager metadataManager;
	private KpiManager kpiManager;

	public DataManager() {
		try {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String vanillaRuntimeUrl = config.getVanillaServerUrl();
			String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			if (vanillaRuntimeUrl.endsWith("/"))
				vanillaRuntimeUrl = vanillaRuntimeUrl.substring(0, vanillaRuntimeUrl.length() - 1);
	
			this.vanillaContext = new BaseVanillaContext(vanillaRuntimeUrl, login, password);
	
			int groupId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
			int repId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));
	
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(config);
			Repository publicRep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repId);
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
	
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, publicRep);
			IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
			IRepository repository = new bpm.vanilla.platform.core.repository.Repository(repositoryApi, IRepositoryApi.FMDT_TYPE);
			
			this.metadataManager = new MetadataManager(vanillaRuntimeUrl, repositoryApi, group, repository);
			this.kpiManager = new KpiManager(vanillaContext, groupId);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public IVanillaContext getVanillaContext() {
		return vanillaContext;
	}

	public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet, List<EntityKey> keys, Expression filterExpression) throws ODataApplicationException {
		EntityCollection entitySet = null;
		if (edmEntitySet.getName().equals(MetadataEdmProvider.ES_METADATAS_NAME)) {
			entitySet = metadataManager.getMetadataCollection();
		}
		else if (edmEntitySet.getName().equals(MetadataEdmProvider.ES_BUSINESS_MODELS_NAME)) {
			entitySet = metadataManager.getBusinessModelCollection(keys);
		}
		else if (edmEntitySet.getName().equals(MetadataEdmProvider.ES_BUSINESS_PACKAGES_NAME)) {
			entitySet = metadataManager.getBusinessPackageCollection(keys);
		}
		else if (edmEntitySet.getName().equals(MetadataEdmProvider.ES_BUSINESS_TABLES_NAME)) {
			entitySet = metadataManager.getBusinessTableCollection(keys);
		}
		else if (edmEntitySet.getName().equals(MetadataEdmProvider.ES_COLUMNS_NAME)) {
			entitySet = metadataManager.getColumnCollection(keys);
		}
		else if (edmEntitySet.getName().equals(KpiEdmProvider.ES_OBSERVATORIES_NAME)) {
			entitySet = kpiManager.getObservatoryCollection();
		}
		else if (edmEntitySet.getName().equals(KpiEdmProvider.ES_THEMES_NAME)) {
			entitySet = kpiManager.getThemeCollection(keys);
		}
		else if (edmEntitySet.getName().equals(KpiEdmProvider.ES_METRICS_NAME)) {
			entitySet = kpiManager.getMetricCollection(keys);
		}
		else if (edmEntitySet.getName().equals(KpiEdmProvider.ES_AXES_NAME)) {
			entitySet = kpiManager.getAxeCollection(keys);
		}
		else if (edmEntitySet.getName().equals(KpiEdmProvider.ES_LEVELS_NAME)) {
			entitySet = kpiManager.getLevelCollection(keys);
		}
		else if (edmEntitySet.getName().equals(KpiEdmProvider.ES_VALUES_NAME)) {
			entitySet = kpiManager.getValueCollection(keys, filterExpression);
		}
		else {
			entitySet = metadataManager.getDatasetValueCollection(edmEntitySet.getName());
		}

		return entitySet;
	}

	public Entity readEntityData(EdmEntityType edmEntityType, List<EntityKey> keys) throws ODataApplicationException {
		Entity entity = null;

		if (edmEntityType.getName().equals(MetadataEdmProvider.ET_METADATA_NAME)) {
			entity = metadataManager.getMetadataEntity(keys);
		}
		else if (edmEntityType.getName().equals(MetadataEdmProvider.ET_BUSINESS_MODEL_NAME)) {
			entity = metadataManager.getBusinessModelEntity(keys);
		}
		else if (edmEntityType.getName().equals(MetadataEdmProvider.ET_BUSINESS_PACKAGE_NAME)) {
			entity = metadataManager.getBusinessPackageEntity(keys);
		}
		else if (edmEntityType.getName().equals(MetadataEdmProvider.ET_BUSINESS_TABLE_NAME)) {
			entity = metadataManager.getBusinessTableEntity(keys);
		}
		else if (edmEntityType.getName().equals(MetadataEdmProvider.ET_COLUMN_NAME)) {
			entity = metadataManager.getColumnEntity(keys);
		}
		else if (edmEntityType.getName().equals(KpiEdmProvider.ET_OBSERVATORY_NAME)) {
			entity = kpiManager.getObservatoryEntity(keys);
		}
		else if (edmEntityType.getName().equals(KpiEdmProvider.ET_THEME_NAME)) {
			entity = kpiManager.getThemeEntity(keys);
		}
		else if (edmEntityType.getName().equals(KpiEdmProvider.ET_METRIC_NAME)) {
			entity = kpiManager.getMetricEntity(keys);
		}
		else if (edmEntityType.getName().equals(KpiEdmProvider.ET_AXIS_NAME)) {
			entity = kpiManager.getAxeEntity(keys);
		}
		else if (edmEntityType.getName().equals(KpiEdmProvider.ET_LEVEL_NAME)) {
			entity = kpiManager.getLevelEntity(keys);
		}
		else if (edmEntityType.getName().equals(KpiEdmProvider.ET_VALUE_NAME)) {
//			entity = kpiManager.getMetricValueEntity(keys);
		}
		else {
			entity = metadataManager.getDatasetValue(keys);
		}

		return entity;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}
}