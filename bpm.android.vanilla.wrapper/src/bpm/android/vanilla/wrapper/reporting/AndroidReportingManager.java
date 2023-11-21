package bpm.android.vanilla.wrapper.reporting;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import bpm.android.vanilla.core.IAndroidReportingManager;
import bpm.android.vanilla.core.beans.AndroidObject;
import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.beans.metadata.AndroidBusinessModel;
import bpm.android.vanilla.core.beans.metadata.AndroidBusinessPackage;
import bpm.android.vanilla.core.beans.metadata.AndroidBusinessTable;
import bpm.android.vanilla.core.beans.metadata.AndroidMetadata;
import bpm.android.vanilla.wrapper.tools.SessionContent;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class AndroidReportingManager implements IAndroidReportingManager {

	private RemoteRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private SessionContent session;

	public AndroidReportingManager(SessionContent session) {
		this.session = session;
	}

	public void setRepositoryContext(IVanillaContext vanillaCtx, Group group, bpm.vanilla.platform.core.beans.Repository repository) {
		vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		IRepositoryContext repositoryCtx = new BaseRepositoryContext(vanillaCtx, group, repository);
		sock = new RemoteRepositoryApi(repositoryCtx);
	}

	@Override
	public List<AndroidMetadata> getAllMetadata() throws Exception {
		IRepository repositoryContent = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FMDT_TYPE);

		List<AndroidMetadata> allMetadata = new ArrayList<AndroidMetadata>();
		for (RepositoryDirectory dir : repositoryContent.getRootDirectories()) {
			loadMetadata(dir, allMetadata, repositoryContent);
		}

		return allMetadata;
	}

	private void loadMetadata(RepositoryDirectory directory, List<AndroidMetadata> allMetadata, IRepository rep) {
		List<RepositoryItem> items = null;
		try {
			items = rep.getItems(directory);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryItem item : items) {
			session.addMetadata(item.getId(), item);

			AndroidMetadata metadata = new AndroidMetadata(item.getId(), item.getItemName());
			allMetadata.add(metadata);
		}

		List<RepositoryDirectory> dirs = null;
		try {
			dirs = rep.getChildDirectories(directory);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryDirectory dir : dirs) {
			loadMetadata(dir, allMetadata, rep);
		}
	}

	@Override
	public AndroidMetadata loadMetadata(int metadataId) throws Exception {
		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(metadataId);
		AndroidMetadata metadata = loadMetadata(item, sock.getContext().getGroup().getName());
		return metadata;
	}

	private AndroidMetadata loadMetadata(RepositoryItem item, String groupName) throws Exception {
		String result = sock.getRepositoryService().loadModel(item);

		// TODO: Support onOlap
		// boolean isOnOlap = false;
		// if(result.contains("<olapDataSource>")) {
		// isOnOlap = true;
		// }

		AndroidMetadata metadata = new AndroidMetadata(item.getId(), item.getItemName());

		InputStream input = IOUtils.toInputStream(result, "UTF-8");

		Collection<IBusinessModel> bModels = MetaDataReader.read(groupName, input, sock, false);
		for (IBusinessModel businessModel : bModels) {
			AndroidBusinessModel androidModel = new AndroidBusinessModel(businessModel.getName());
			androidModel.setBusinessPackages(buildBusinessPackages(item.getId(), item.getItemName(), businessModel, groupName));

			// TODO: Support locale
			// for(Locale locale : bmodel.getLocales()) {
			// if(((BusinessModel)bmodel).getOuputName(locale) != null &&
			// ((BusinessModel)bmodel).getOuputName(locale) != "") {
			// currBM.getTitles().put(locale.getLanguage(),
			// ((BusinessModel)bmodel).getOuputName(locale));
			// }
			// else {
			// currBM.getTitles().put(locale.getLanguage() ,currBM.getName());
			// }
			// currBM.getLocales().put(locale.getLanguage(),
			// locale.getDisplayName());
			// }

			metadata.addBusinessModel(androidModel);
		}

		return metadata;
	}

	private List<AndroidBusinessPackage> buildBusinessPackages(int metadataId, String metadaParent, IBusinessModel businessModel, String groupName) {
		List<AndroidBusinessPackage> packages = new ArrayList<AndroidBusinessPackage>();

		Collection<IBusinessPackage> bPackages = businessModel.getBusinessPackages(groupName);
		for (IBusinessPackage businessPackage : bPackages) {

			AndroidBusinessPackage androidPackage = new AndroidBusinessPackage(businessPackage.getName());

			// TODO: Support locale
			// for(Locale locale : bmodel.getLocales()) {
			// if((bpackage).getOuputName(locale) != null
			// && (bpackage).getOuputName(locale) != "") {
			// currBP.getTitles().put(locale.getLanguage(),
			// (bpackage).getOuputName(locale));
			// }
			// else {
			// currBP.getTitles().put(locale.getLanguage(), currBP.getName());
			// }
			// }

			Collection<IResource> resources = businessPackage.getResources(groupName);
			for (IResource ressource : resources) {
				if (ressource instanceof IFilter) {
					IFilter f = (IFilter) ressource;

					FWRFilter resource = new FWRFilter();
					resource.setName(f.getName());
					resource.setMetadataId(metadataId);
					resource.setModelParent(businessModel.getName());
					resource.setPackageParent(businessPackage.getName());
					androidPackage.addResource(resource);
				}
				else if (ressource instanceof Prompt) {
					Prompt p = (Prompt) ressource;

					FwrPrompt resource = new FwrPrompt();
					resource.setName(p.getName());
					resource.setMetadataId(metadataId);
					resource.setModelParent(businessModel.getName());
					resource.setPackageParent(businessPackage.getName());
					androidPackage.addResource(resource);
				}
			}

			androidPackage.setBusinessTables(buildBusinessTables(metadataId, metadaParent, businessModel.getName(), businessPackage, groupName, businessModel.getLocales()));
			packages.add(androidPackage);
		}

		return packages;
	}

	private List<AndroidBusinessTable> buildBusinessTables(int metadataId, String metadataParent, String businessModelParent, IBusinessPackage businessPackage, String groupName, Collection<Locale> locales) {
		List<AndroidBusinessTable> tables = new ArrayList<AndroidBusinessTable>();

		Collection<IBusinessTable> bTables = businessPackage.getBusinessTables(groupName);

		for (IBusinessTable businessTable : bTables) {
			AndroidBusinessTable table = new AndroidBusinessTable(businessTable.getName());

			// TODO: Support locale
			// for(Locale locale : bmodel.getLocales()) {
			// if(((AbstractBusinessTable)btable).getOuputName(locale) != null
			// && ((AbstractBusinessTable)btable).getOuputName(locale) != "") {
			// table.getTitles().put(locale.getLanguage(),
			// ((AbstractBusinessTable)btable).getOuputName(locale));
			// }
			// else {
			// table.getTitles().put(locale.getLanguage(), table.getName());
			// }
			// }

			table.setColumns(buildColumns(metadataId, metadataParent, businessModelParent, businessPackage.getName(), businessTable, groupName, businessPackage.isOnOlapDataSource(), locales));

			// TODO: Support child
			// List<IBusinessTable> childs =
			// btable.getChilds(session.getGroup().getName());
			// if ( !childs.isEmpty() ) {
			// fillChilds(table, childs, session.getGroup().getName(),
			// bmodel.getLocales());
			// }
			// fwrTables.add(table);

			tables.add(table);
		}

		return tables;
	}

	private List<Column> buildColumns(int metadataId, String metadataParent, String businessModelParent, String businessPackageParent, IBusinessTable businessTable, String groupName, boolean isOnOlap, Collection<Locale> locales) {
		List<Column> columns = new ArrayList<Column>();

		if (!isOnOlap) {
			Collection<IDataStreamElement> cols = businessTable.getColumns(groupName);
			Iterator<IDataStreamElement> itc = cols.iterator();
			while (itc.hasNext()) {
				IDataStreamElement column = (IDataStreamElement) itc.next();

				if (column.isVisibleFor(groupName)) {
					Column androidColumn = new Column();
					androidColumn.setName(column.getName());
					androidColumn.setFormat("");
					try {
						androidColumn.setJavaClass(column.getJavaClassName());
					} catch (Exception e) {
						e.printStackTrace();
						androidColumn.setJavaClass("");
					}

					for (Locale locale : locales) {
						if (column.getOuputName(locale) != null && !column.getOuputName(locale).equalsIgnoreCase("")) {
							androidColumn.addLocaleTitle(locale.getLanguage(), column.getOuputName(locale));
						}
						else {
							androidColumn.addLocaleTitle(locale.getLanguage(), column.getName());
						}
					}

//					androidColumn.setType(column.getType());

					androidColumn.setMetadataId(metadataId);
					androidColumn.setMetadataParent(metadataParent);
					androidColumn.setBusinessModelParent(businessModelParent);
					androidColumn.setBusinessPackageParent(businessPackageParent);
					androidColumn.setBusinessTableParent(businessTable.getName());

					columns.add(androidColumn);
				}
			}
		}

		return columns;
	}

	@Override
	public FWRReport loadReport(int itemId) throws Exception {
		RepositoryItem item = sock.getRepositoryService().getDirectoryItem(itemId);

		String itemXML = null;
		try {
			itemXML = sock.getRepositoryService().loadModel(item);
		} catch (Exception e) {
			e.printStackTrace();
		}

		XStream xstream = new XStream();
		FWRReport report = (FWRReport) xstream.fromXML(itemXML);

		// We set the RepositoryItem informations
		SaveOptions options = report.getSaveOptions();
		if (options == null) {
			options = new SaveOptions();
		}
		options.setName(item.getItemName());
		options.setDirectoryItemid(item.getId());

		return report;
	}

	@Override
	public String previewReport(FWRReport report) throws Exception {
		XStream xstream = new XStream();
		String reportXML = xstream.toXML(report);

		InputStream reportModel = IOUtils.toInputStream(reportXML, "UTF-8");

		ObjectIdentifier objectId = new ObjectIdentifier(sock.getContext().getRepository().getId(), -1);

		ReportRuntimeConfig config = new ReportRuntimeConfig(objectId, null, sock.getContext().getGroup().getId());
		config.setOutputFormat("html");

		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(sock.getContext().getVanillaContext().getLogin());

		RemoteReportRuntime remoteReport = new RemoteReportRuntime(sock.getContext().getVanillaContext());
		InputStream resultReport = remoteReport.runReport(config, reportModel, user, false);

		String resultReportXML = IOUtils.toString(resultReport, "UTF-8");

		return resultReportXML;
	}

	@Override
	public void saveReport(FWRReport report) throws Exception {
		SaveOptions options = report.getSaveOptions();

		XStream xstream = new XStream();
		String reportXML = xstream.toXML(report);

		RepositoryDirectory dir = sock.getRepositoryService().getDirectory(options.getDirectoryId());
		sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FWR_TYPE, -1, dir, options.getName(), "", "1", "1", reportXML, true);
	}

	@Override
	public AndroidRepository getRepositoryContent(AndroidRepository repository) throws Exception {
		if (repository != null) {

			try {
				IRepository irep = new Repository(sock, IRepositoryApi.FWR_TYPE);
				repository.setRepositoryContent(buildRepository(irep));
				return repository;
			} catch (Exception e) {
				throw new Exception("Unable to browse repository: " + e.getMessage());
			}
		}
		else {
			throw new Exception("A problem happend during the repository construction.");
		}
	}

	private List<AndroidObject> buildRepository(IRepository repository) throws Exception {
		List<AndroidObject> rootDirs = new ArrayList<AndroidObject>();

		List<RepositoryDirectory> list = repository.getRootDirectories();
		for (RepositoryDirectory d : list) {

			AndroidObject dir = new AndroidObject(AndroidObject.TYPE_DIRECTORY);
			dir.setId(d.getId());
			dir.setName(d.getName());
			rootDirs.add(dir);

			try {
				buildChilds(dir, d, repository);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			try {
				for (RepositoryItem di : repository.getItems(d)) {
					try {
						AndroidObject item = new AndroidObject(AndroidObject.TYPE_FWR);
						item.setId(di.getId());
						item.setName(di.getItemName());
						dir.addChild(item);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return rootDirs;
	}

	private void buildChilds(AndroidObject parentTree, RepositoryDirectory parent, IRepository rep) throws Exception {
		List<RepositoryDirectory> dirs = rep.getChildDirectories(parent);
		if (dirs != null) {
			for (RepositoryDirectory d : dirs) {
				AndroidObject dir = new AndroidObject(AndroidObject.TYPE_DIRECTORY);
				dir.setId(d.getId());
				dir.setName(d.getName());

				parentTree.addChild(dir);

				List<RepositoryItem> items = rep.getItems(d);
				if (items != null) {
					for (RepositoryItem di : items) {
						AndroidObject item = new AndroidObject(AndroidObject.TYPE_FWR);
						item.setId(di.getId());
						item.setName(di.getItemName());
						dir.addChild(item);
					}
				}
				buildChilds(dir, d, rep);
			}
		}
	}

	@Override
	public List<FwrPrompt> getPromptsResponse(List<FwrPrompt> prompts) throws Exception {
		if (prompts != null) {
			for (FwrPrompt prompt : prompts) {
				try {
					DataSource datasource = new DataSource();
					datasource.setItemId(prompt.getMetadataId());
					datasource.setGroup(sock.getContext().getGroup().getName());
					datasource.setBusinessPackage(prompt.getPackageParent());
					datasource.setBusinessModel(prompt.getModelParent());
					datasource.setRepositoryId(sock.getContext().getRepository().getId());
					IBusinessPackage pckg = getSelectedPackage(sock, datasource);

					Prompt p = (Prompt) pckg.getResourceByName(sock.getContext().getGroup().getName(), prompt.getName());

					prompt.setValues(p.getOrigin().getDistinctValues());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return prompts;
	}

	private IBusinessPackage getSelectedPackage(IRepositoryApi sock, DataSource datasource) throws Exception {
		IRepository irep = new Repository(sock, IRepositoryApi.FMDT_TYPE);

		RepositoryItem item = irep.getItem(datasource.getItemId());
		String result = sock.getRepositoryService().loadModel(item);
		InputStream input = IOUtils.toInputStream(result, "UTF-8");

		Collection<IBusinessModel> bmodels = MetaDataReader.read(datasource.getGroup(), input, sock, false);
		IBusinessModel model = null;

		Iterator<IBusinessModel> itbm = bmodels.iterator();
		while (itbm.hasNext()) {
			IBusinessModel bmodel = (IBusinessModel) itbm.next();
			if (bmodel.getName().equals(datasource.getBusinessModel())) {
				model = bmodel;
			}
		}

		return model.getBusinessPackage(datasource.getBusinessPackage(), datasource.getGroup());
	}
}
