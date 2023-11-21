	package bpm.vanillahub.web.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import bpm.aklabox.workflow.core.IAklaflowManager;
import bpm.aklabox.workflow.core.model.activities.AklaflowContext;
import bpm.aklabox.workflow.remote.RemoteAklaflowManager;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanillahub.core.beans.activities.TypeOpenData;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvSummary;
import bpm.vanillahub.core.beans.activities.attributes.VanillaItemParameter;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.core.utils.OpenDataHelper;
import bpm.vanillahub.web.client.services.ResourcesService;
import bpm.vanillahub.web.server.security.VanillaHubSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ResourcesServiceImpl extends RemoteServiceServlet implements ResourcesService {

	private static final long serialVersionUID = 1L;

	private VanillaHubSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), VanillaHubSession.class);
	}

	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}

	@Override
	public User manageUser(User user, boolean edit) throws ServiceException {
		VanillaHubSession session = getSession();
		try {
			user.setLocale(getLocale().getLanguage());
			return session.getHubResourceManager().manageUser(user, edit);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void removeUser(User user) throws ServiceException {
		VanillaHubSession session = getSession();
		try {
			session.getHubResourceManager().removeUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<User> getUsers(TypeResource user) throws ServiceException {
		VanillaHubSession session = getSession();
		try {
			return session.getHubResourceManager().getUsers();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<WebServiceMethodDefinition> getWebServiceMethods(VariableString webServiceUrl) throws ServiceException {
		VanillaHubSession session = getSession();
		try {
			return session.getHubResourceManager().getWebServiceMethods(webServiceUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<String> getCrawlReference(String url, String reference, int charBefore, int charAfter) throws ServiceException {
		VanillaHubSession session = getSession();
		try {
			return session.getHubResourceManager().getCrawlReference(url, reference, charBefore, charAfter);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Group> getVanillaGroups(String url, String user, String password) throws ServiceException {
		IVanillaAPI api = new RemoteVanillaPlatform(url, user, password);

		try {
			return api.getVanillaSecurityManager().getGroups();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<Repository> getVanillaRepositories(String url, String user, String password) throws ServiceException {
		IVanillaAPI api = new RemoteVanillaPlatform(url, user, password);

		try {
			return api.getVanillaRepositoryManager().getRepositories();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<RepositoryDirectory> getRepositoryTree(VanillaServer vanillaServer) throws ServiceException {
		try {
			BaseVanillaContext vctx = new BaseVanillaContext(vanillaServer.getUrlDisplay(), vanillaServer.getLoginDisplay(), vanillaServer.getPasswordDisplay());
			IVanillaAPI api = new RemoteVanillaPlatform(vctx);
			IRepositoryContext ctx = new BaseRepositoryContext(vctx, api.getVanillaSecurityManager().getGroupById(Integer.parseInt(vanillaServer.getGroupId().getStringForTextbox())), api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(vanillaServer.getRepositoryId().getStringForTextbox())));
			IRepositoryApi sock = new RemoteRepositoryApi(ctx);

			bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(sock);
			return repository.getRepositoryTree();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the repository.", e);
		}
	}

	@Override
	public List<VanillaItemParameter> getParameterForItem(VanillaServer vanillaServer, int itemId) throws ServiceException {
		try {
			BaseVanillaContext vctx = new BaseVanillaContext(vanillaServer.getUrlDisplay(), vanillaServer.getLoginDisplay(), vanillaServer.getPasswordDisplay());
			IVanillaAPI api = new RemoteVanillaPlatform(vctx);

			IRepositoryContext ctx = new BaseRepositoryContext(vctx, api.getVanillaSecurityManager().getGroupById(Integer.parseInt(vanillaServer.getGroupId().getStringForTextbox())), api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(vanillaServer.getRepositoryId().getStringForTextbox())));
			IRepositoryApi sock = new RemoteRepositoryApi(ctx);

			List<Parameter> params = sock.getRepositoryService().getParameters(sock.getRepositoryService().getDirectoryItem(itemId));

			List<VanillaItemParameter> parameters = new ArrayList<VanillaItemParameter>();
			for (Parameter p : params) {
				VanillaItemParameter pa = new VanillaItemParameter();
				pa.setName(p.getName());
				pa.setValue(new VariableString(p.getDefaultValue()));
				parameters.add(pa);
			}
			return parameters;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public DataGouvSummary getOpenDataDatasets(TypeOpenData typeOpenData, String url) throws ServiceException {
		try {
			return OpenDataHelper.getDatasets(url);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			throw new ServiceException("Unable to browse datasets: " + e.getMessage());
		}
	}

	@Override
	public List<Contract> getContracts(VanillaServer vanillaServer) throws ServiceException {
		String vanillaUrl = vanillaServer.getUrlDisplay();
		String login = vanillaServer.getLoginDisplay();
		String password = vanillaServer.getPasswordDisplay();
		int groupId = Integer.parseInt(vanillaServer.getGroupId().getStringForTextbox());

		BaseVanillaContext vctx = new BaseVanillaContext(vanillaUrl, login, password);
		IVanillaAPI api = new RemoteVanillaPlatform(vctx);

		IGedComponent gedComponent = new RemoteGedComponent(vctx);
		IMdmProvider mdmComponent = new MdmRemote(login, password, vanillaUrl);
		IVanillaSecurityManager securityManager = api.getVanillaSecurityManager();

		try {
			List<Contract> contracts = new ArrayList<>();

			List<Supplier> suppliers = mdmComponent.getSuppliersByGroupId(groupId);
			if (suppliers != null) {
				for (Supplier supplier : suppliers) {
					if (supplier.getContracts() != null) {
						List<Contract> supplierContracts = new ArrayList<>();
						for (Contract contract : supplier.getContracts()) {
							if (contract.getDocId() != null) {
								GedDocument doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
								if (doc.getDocumentVersions() != null) {
									for (DocumentVersion version : doc.getDocumentVersions()) {
										if (version.getModifiedBy() > 0) {
											User user = securityManager.getUserById(version.getModifiedBy());
											version.setModificator(user);
										}
									}
								}

								contract.setFileVersions(doc);
							}

							contract.setParent(supplier);
							supplierContracts.add(contract);
						}
						supplier.setContracts(supplierContracts);

						contracts.addAll(supplierContracts);
					}
				}
			}

			return contracts;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get contracts : " + e.getMessage());
		}
	}
	
	@Override
	public List<String> getAvailableLangs(AklaboxServer server) throws ServiceException {
		AklaflowContext ctx = new AklaflowContext(server.getUrlDisplay(), server.getLoginDisplay(), server.getPasswordDisplay());
		
		IAklaflowManager aklaFlowManager = new RemoteAklaflowManager(ctx);
		try {
			return aklaFlowManager.getAvailableLangs();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get available languages.");
		}
	}
}
