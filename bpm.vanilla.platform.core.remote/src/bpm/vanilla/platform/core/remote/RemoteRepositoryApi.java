package bpm.vanilla.platform.core.remote;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteAlert;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteDatasProvider;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteImpactDetection;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteMeta;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteRepository;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteRepositoryAdmin;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteRepositoryDocumentation;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteRepositoryLogs;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteRepositoryReportHistoric;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteVersioning;
import bpm.vanilla.platform.core.remote.impl.repository.RemoteWatchList;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.remote.internal.RepositoryHttpCommunicator;
import bpm.vanilla.platform.core.repository.services.IDataProviderService;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IMetaService;
import bpm.vanilla.platform.core.repository.services.IModelVersionningService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAlertService;
import bpm.vanilla.platform.core.repository.services.IRepositoryImpactService;
import bpm.vanilla.platform.core.repository.services.IRepositoryLogService;
import bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;
import bpm.vanilla.platform.core.repository.services.IWatchListService;

public class RemoteRepositoryApi implements IRepositoryApi {
	
	private IRepositoryService repositoryService;
	private IRepositoryAdminService adminService;
	private IDocumentationService documentationService;
	private IWatchListService watchListService;
	private IRepositoryImpactService impactService;
	private IRepositoryLogService logService;
	private IDataProviderService datasProviderService;
	private IRepositoryReportHistoricService reportHistoService;
	private IModelVersionningService versioningService;
	private IRepositoryAlertService alertService;
	private IMetaService metaService;
	private IRepositoryContext ctx;

	public RemoteRepositoryApi(IRepositoryContext ctx) {
		this.ctx = ctx;
		
		HttpCommunicator httpCommunicator = new RepositoryHttpCommunicator(ctx, VanillaConstants.REPOSITORY_SERVLET);
		
		this.repositoryService = new RemoteRepository(httpCommunicator);
		this.adminService = new RemoteRepositoryAdmin(httpCommunicator);
		this.documentationService = new RemoteRepositoryDocumentation(httpCommunicator);
		this.watchListService = new RemoteWatchList(httpCommunicator);
		this.impactService = new RemoteImpactDetection(httpCommunicator);
		this.logService = new RemoteRepositoryLogs(httpCommunicator);
		this.datasProviderService = new RemoteDatasProvider(httpCommunicator);
		this.reportHistoService = new RemoteRepositoryReportHistoric(httpCommunicator);
		this.versioningService = new RemoteVersioning(httpCommunicator);
		this.alertService = new RemoteAlert(httpCommunicator);
		this.metaService = new RemoteMeta(httpCommunicator);
	}

	@Override
	public IRepositoryContext getContext() {
		return ctx;
	}

	@Override
	public IModelVersionningService getVersioningService() {
		return versioningService;
	}

	@Override
	public IRepositoryReportHistoricService getReportHistoricService() {
		return reportHistoService;
	}

	@Override
	public IDataProviderService getDatasProviderService() {
		return datasProviderService;
	}

	@Override
	public IRepositoryLogService getRepositoryLogService() {
		return logService;
	}

	@Override
	public IRepositoryService getRepositoryService() {
		return repositoryService;
	}

	@Override
	public IRepositoryAdminService getAdminService() {
		return adminService;
	}

	@Override
	public IDocumentationService getDocumentationService() {
		return documentationService;
	}

	@Override
	public IWatchListService getWatchListService() {
		return watchListService;
	}

	@Override
	public IRepositoryImpactService getImpactDetectionService() {
		return impactService;
	}
	
	@Override
	public IRepositoryAlertService getAlertService() {
		return alertService;
	}

	@Override
	public IMetaService getMetaService() {
		return metaService;
	}

	@Override
	public void test() throws Exception {
		

	}
}
