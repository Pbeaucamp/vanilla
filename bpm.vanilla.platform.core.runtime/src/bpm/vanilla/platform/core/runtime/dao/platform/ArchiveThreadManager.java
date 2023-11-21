package bpm.vanilla.platform.core.runtime.dao.platform;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion.DocumentStatus;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.components.ArchiveManager;

public class ArchiveThreadManager extends Thread {

	private int checkDelay;
	private ArchiveManager archiveManager;
	private RemoteVanillaPlatform vanillaApi;
	private RemoteHistoricReportComponent reportComponent;
	private RemoteGedComponent gedComponent;

	public ArchiveThreadManager(int checkDelay, ArchiveManager component) {
		this.checkDelay = checkDelay;
		this.archiveManager = component;
		vanillaApi = new RemoteVanillaPlatform(archiveManager.getRootVanillaContext());
		reportComponent = new RemoteHistoricReportComponent(archiveManager.getRootVanillaContext());
		gedComponent = new RemoteGedComponent(archiveManager.getRootVanillaContext());
	}

	@Override
	public void run() {

		while (true) {
			try {
				Thread.sleep(checkDelay);

				List<ArchiveType> types = archiveManager.getArchiveTypes();

				for (ArchiveType type : types) {
					List<ArchiveTypeItem> links = archiveManager.getArchiveTypeByArchive(type.getId());

					// get items
					HashMap<Integer, List<Integer>> itemsByRepository = new HashMap<Integer, List<Integer>>();
					for (ArchiveTypeItem link : links) {
						if (itemsByRepository.get(link.getRepositoryId()) == null) {
							itemsByRepository.put(link.getRepositoryId(), new ArrayList<Integer>());
						}
						itemsByRepository.get(link.getRepositoryId()).add(link.getItemId());
					}

					HashMap<IRepositoryApi, List<RepositoryItem>> items = new HashMap<IRepositoryApi, List<RepositoryItem>>();
					for (int repositoryId : itemsByRepository.keySet()) {
						IRepositoryApi api = new RemoteRepositoryApi(new BaseRepositoryContext(archiveManager.getRootVanillaContext(), new Group(), vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId)));

						List<RepositoryItem> repItems = new ArrayList<>(api.getRepositoryService().getItems(itemsByRepository.get(repositoryId)));
						items.put(api, repItems);
					}

					// check peremption
					if (type.getPeremptionMonths() > 0) {
						for (IRepositoryApi api : items.keySet()) {
							for (RepositoryItem item : items.get(api)) {
								checkPeremption(api, item, type);
							}
						}
					}

					// check retention
					if (type.getRetentionMonths() > 0) {
						for (IRepositoryApi api : items.keySet()) {
							for (RepositoryItem item : items.get(api)) {
								checkRetention(api, item, type);
							}
						}
					}

					// for non found items, check the save path and conservation

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void checkPeremption(IRepositoryApi api, RepositoryItem item, ArchiveType type) throws Exception {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.MONTH, -type.getPeremptionMonths());

		IObjectIdentifier identifier = new ObjectIdentifier(api.getContext().getRepository().getId(), item.getId());
		List<GedDocument> docs = reportComponent.getReportHistoric(identifier, -1);

		// it is out dated
		for (GedDocument doc : docs) {
			for (DocumentVersion version : doc.getDocumentVersions()) {
				if (date.getTime().after(version.getModificationDate()) && version.getStatus() == DocumentStatus.ACTIVE) {
					version.setStatus(DocumentStatus.OUTDATED);
					gedComponent.updateVersion(version);
				}
			}
		}
	}

	private void checkRetention(IRepositoryApi api, RepositoryItem item, ArchiveType type) throws Exception {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.MONTH, -type.getRetentionMonths());

		ReportHistoricComponent reportComponent = new RemoteHistoricReportComponent(api.getContext().getVanillaContext());
		IObjectIdentifier identifier = new ObjectIdentifier(api.getContext().getRepository().getId(), item.getId());
		List<GedDocument> docs = reportComponent.getReportHistoric(identifier, -1);

		// it is out dated
		for (GedDocument doc : docs) {
			for (DocumentVersion version : doc.getDocumentVersions()) {
				if (date.getTime().after(version.getModificationDate()) && version.getStatus() == DocumentStatus.OUTDATED) {
					String path = type.getSavePath();

					Files.copy(Paths.get(version.getDocumentPath()), Paths.get(path + "/" + version.getDocumentId() + "_" + version.getId() + "_"));

					gedComponent.deleteVersion(version);
				}
			}
		}
	}

}
