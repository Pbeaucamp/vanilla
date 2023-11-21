package bpm.es.dndserver.api;

import java.util.ArrayList;
import java.util.List;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.FMDTFind;
import bpm.es.dndserver.api.fmdt.FMDTMigration;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.api.repository.RepositoryWrapper;
import bpm.es.dndserver.tools.OurLogger;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

public class DNDOProject {

	private RepositoryWrapper inputRepository;
	private RepositoryWrapper outputRepository;

	private List<AxisDirectoryItemWrapper> migrationObjects;

	private ProjectMessenger messenger = new ProjectMessenger();

	public DNDOProject() {

	}

	public ProjectMessenger getMessenger() {
		return messenger;
	}

	public RepositoryWrapper getInputRepository() {
		return inputRepository;
	}

	public void setInputRepository(RepositoryWrapper inputRepository) {
		this.inputRepository = inputRepository;
	}

	public RepositoryWrapper getOutputRepository() {
		return outputRepository;
	}

	public void setOutputRepository(RepositoryWrapper outputRepository) {
		this.outputRepository = outputRepository;
	}

	/**
	 * Connect to a vanilla to get a list of the repositories
	 * 
	 * @param user
	 * @param password
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public List<Repository> getRepositories(String user, String password, String url) throws Exception {

		IVanillaAPI api = new RemoteVanillaPlatform(url, user, password);
		List<Repository> definitions = api.getVanillaRepositoryManager().getRepositories();

		return definitions;
	}

	public void connectToInputRepository(String user, String password, Repository definition) throws Exception {

		// FactoryRepository.getInstance().
		try {

			Group group = new Group();
			group.setId(-1);
			IRepositoryContext ctx = new BaseRepositoryContext(Activator.getDefault().getVanillaContext(), group, definition);

			IRepositoryApi sock = new RemoteRepositoryApi(ctx); //$NON-NLS-1$

			inputRepository = new RepositoryWrapper(sock, definition);
		} catch (Exception ex) {
			throw new Exception(Messages.DNDOProject_1 + Messages.DNDOProject_2 + definition.getUrl() + "\n" + //$NON-NLS-1$
					Messages.DNDOProject_4 + user);
		}

		// IRepository repository = sock.getRepository();
		// sock.get
	}

	public void connectToOutputRepository(String url, String user, String password, Repository definition) throws Exception {
		IVanillaContext context = new BaseVanillaContext(url, user, password);
		
		Group group = new Group();
		group.setId(-1);
		IRepositoryContext ctx = new BaseRepositoryContext(context, group, definition);

		IRepositoryApi sock = new RemoteRepositoryApi(ctx); //$NON-NLS-1$

		outputRepository = new RepositoryWrapper(sock, definition);
	}

	/**
	 * Load migrated object and repository dependencies (including metadata) and
	 * item metadatas
	 * 
	 * @throws Exception
	 */
	public void loadMigrationObjects() throws Exception {

		// load all repository deps
		findDependencies(outputRepository);
		// load all metadata deps
		parseDependencies(outputRepository);

		migrationObjects = outputRepository.getFlatItems();

		loadItemsMetaDatas(migrationObjects);
	}

	private void loadItemsMetaDatas(List<AxisDirectoryItemWrapper> migration) throws Exception {
		// migration.get(0).g
		for (AxisDirectoryItemWrapper mig : migration) {
			try {
				OurLogger.info(Messages.DNDOProject_6 + mig.getAxisItem().getItemName());

				List<FMDTDataSource> itemSources = FMDTFind.find(mig, inputRepository.getRepositoryClient());

				messenger.addBulkMessages(FMDTFind.getLastMessages());

				List<AxisDirectoryItemWrapper> repSources = mig.getDependencies();
				if (!repSources.isEmpty()) {
					loadItemsMetaDatas(repSources);
				}
				// should verify that they match.

				for (FMDTDataSource source : itemSources) {
					FMDTMigration fmdt = mig.getExistingMigration(source.getDirItemId());
					if (fmdt != null) {
						fmdt.setSource(source);
						OurLogger.info(Messages.DNDOProject_7 + mig.getAxisItem().getItemName());
					}
					else {
						OurLogger.info(Messages.DNDOProject_8 + mig.getAxisItem().getItemName());
					}

				}

			} catch (Exception e) {
				OurLogger.error(e.getMessage(), e);
				throw new Exception(Messages.DNDOProject_9);
			}
		}
	}

	// private FMDTDataSource loadExistingMetadatas(AxisDirectoryItemWrapper
	// metadataItem,
	// AxisDirectoryItemWrapper selectedItem) throws Exception {
	// try {
	// List<FMDTDataSource> sources = FMDTFind.find(selectedItem.getAxisItem(),
	// project.getInputRepository().getRepositoryClient());
	//			
	// selectedItem.setExistingSources(sources);
	//			
	// FMDTDataSource existingSource =
	// selectedItem.getExistingForFMDTDirItemId(metadataItem.getAxisItem().getId());
	//			
	// return existingSource;
	// } catch (Exception e) {
	// OurLogger.error("Error finding existing metadatas", e);
	// throw new Exception("Error finding existing metadatas:\n" +
	// e.getMessage(), e);
	// }
	// }

	/**
	 * Step 1, find all dependencies
	 */
	private void findDependencies(RepositoryWrapper repWrapper) {
		for (List<AxisDirectoryItemWrapper> wrappers : repWrapper.getMapObject().values()) {
			for (AxisDirectoryItemWrapper wrap : wrappers) {
				try {
					List<AxisDirectoryItemWrapper> items = recFindDeps(wrap.getAxisItem());

					OurLogger.info(Messages.DNDOProject_10 + items.size() + Messages.DNDOProject_11 + wrap.getAxisItem().getItemName());

					wrap.setDependencies(items);
				} catch (Exception e) {
					OurLogger.error(Messages.DNDOProject_12 + wrap.getAxisItem().getItemName(), e);
				}
			}
		}
	}

	private List<AxisDirectoryItemWrapper> recFindDeps(RepositoryItem item) throws Exception {

		List<RepositoryItem> deps = inputRepository.getRepositoryClient().getRepositoryService().getNeededItems(item.getId());

		// inputRepository.getRepositoryClient().get
		// inputRepository.getRepositoryClient().get
		// List<DirectoryItem> deps2 = Activator.getDefault().getManager()
		// .getNeededItemsFor(item);

		List<AxisDirectoryItemWrapper> items = new ArrayList<AxisDirectoryItemWrapper>();

		for (RepositoryItem itemDep : deps) {
			if (itemDep instanceof RepositoryItem) {
				AxisDirectoryItemWrapper w = new AxisDirectoryItemWrapper(itemDep);

				if (w.getAxisItem().getId() == item.getId()) {
					OurLogger.info(Messages.DNDOProject_13);
				}
				else {
					w.setDependencies(recFindDeps(itemDep));
				}

				items.add(w);
			}
		}

		return items;
	}

	/**
	 * Step 2, configure wrappers for metadata dependencies
	 */
	private void parseDependencies(RepositoryWrapper repWrapper) {
		for (List<AxisDirectoryItemWrapper> wrappers : repWrapper.getMapObject().values()) {

			for (AxisDirectoryItemWrapper wrap : wrappers) {
				List<AxisDirectoryItemWrapper> metas;

				metas = recParseDeps(wrap);

				wrap.setMetadataDependencies(metas);
			}
		}
	}

	private List<AxisDirectoryItemWrapper> recParseDeps(AxisDirectoryItemWrapper wrap) {
		List<AxisDirectoryItemWrapper> metas = new ArrayList<AxisDirectoryItemWrapper>();

		for (AxisDirectoryItemWrapper w : wrap.getDependencies()) {
			if (w.getAxisItem().getType() == IRepositoryApi.FMDT_TYPE) {

				metas.add(w);
			}
			else {
				List<AxisDirectoryItemWrapper> l = recParseDeps(w);
				w.setMetadataDependencies(l);

			}
		}

		return metas;
	}
}
