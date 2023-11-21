package bpm.gateway.core.server.file;

import java.io.InputStream;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DataStream;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;

public class MdmFileServer extends VanillaFileServer {

	public MdmFileServer() {
	}

	public MdmFileServer(String name, String description, String url, String login, String password, String repDefId) {
		setName(name);
		setDescription(description);
		setVanillaUrl(url);
		setVanillaLogin(login);
		setVanillaPasword(password);
		setRepositoryDefinitionId(repDefId);
	}

	@Override
	public InputStream getInpuStream(DataStream stream) throws Exception {

		if (vanillaApi == null || repositorySocket == null) {
			connect();
		}

		MdmRemote mdmRemote = new MdmRemote(vanillaLogin, vanillaPasword, vanillaUrl);
		RemoteGedComponent ged = new RemoteGedComponent(vanillaUrl, vanillaLogin, vanillaPasword);

		try {
			Contract selectedContract = mdmRemote.getContract(Integer.parseInt(stream.getDefinition()));
			GedDocument doc = ged.getDocumentDefinitionById(selectedContract.getDocId());

			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(doc, vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaLogin).getId());
			if (selectedContract.getVersionId() != null) {
				DocumentVersion docVersion = ged.getDocumentVersionById(selectedContract.getVersionId());
				config = new GedLoadRuntimeConfig(doc, vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaLogin).getId(), docVersion.getVersion());
			}
			return ged.loadGedDocument(config);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getDocumentName(DataStream stream) throws Exception {
		if (vanillaApi == null || repositorySocket == null) {
			connect();
		}

		MdmRemote mdmRemote = new MdmRemote(vanillaLogin, vanillaPasword, vanillaUrl);
		RemoteGedComponent ged = new RemoteGedComponent(vanillaUrl, vanillaLogin, vanillaPasword);

		try {
			Contract selectedContract = mdmRemote.getContract(Integer.parseInt(stream.getDefinition()));
			GedDocument document = ged.getDocumentDefinitionById(selectedContract.getDocId());
			return document.getName();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getFormat(int contractId) throws Exception {
		if (vanillaApi == null || repositorySocket == null) {
			connect();
		}

		MdmRemote mdmRemote = new MdmRemote(vanillaLogin, vanillaPasword, vanillaUrl);
		RemoteGedComponent ged = new RemoteGedComponent(vanillaUrl, vanillaLogin, vanillaPasword);

		try {
			Contract selectedContract = mdmRemote.getContract(contractId);
			
			if (selectedContract.getVersionId() != null) {
				DocumentVersion docVersion = ged.getDocumentVersionById(selectedContract.getVersionId());
				return docVersion.getFormat();
			}
			else if (selectedContract.getDocId() != null) {
				GedDocument doc = ged.getDocumentDefinitionById(selectedContract.getDocId());
				if (doc.getLastVersion() != null) {
					return doc.getLastVersion().getFormat();
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void uploadFileToMDM(String fileName, int contractId, InputStream fileStream) throws Exception {
		if (vanillaApi == null || repositorySocket == null) {
			connect();
		}

		IMdmProvider mdmComponent = new MdmRemote(vanillaLogin, vanillaPasword, vanillaUrl);
		IGedComponent gedComponent = new RemoteGedComponent(vanillaUrl, vanillaLogin, vanillaPasword);

		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(vanillaLogin);

		Contract contract = mdmComponent.getContract(contractId);
		if (contract == null) {
			throw new Exception("Unable to find contract with id '" + contractId + "'");
		}
		
		GedDocument doc = null;
		if (contract.getDocId() != null) {
			doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
			if (doc != null) {
				contract.setFileVersions(doc);
			}
		}

		doc = contract.getFileVersions();
		if (doc != null) {
			String format = getFormat(contractId);
			if (format == null) {
				format = extractFormat(fileName);
			}
			
			gedComponent.addVersionToDocumentThroughServlet(doc.getId(), format, fileStream);
		}
		else {
			String format = getFormat(contractId);
			if (format == null) {
				format = extractFormat(fileName);
			}
			
			List<Integer> groupIds = mdmComponent.getSupplierSecurity(contract.getParent().getId());

			doc = gedComponent.createDocumentThroughServlet(fileName, format, user.getId(), groupIds, repositorySocket.getContext().getRepository().getId(), fileStream);
			contract.setFileVersions(doc);
		}

		contract.setVersionId(null);
		mdmComponent.addContract(contract);
		mdmComponent.launchAssociatedItems(contract.getId(), repositorySocket.getContext(), user);
	}

	private String extractFormat(String fileName) {
		try {
			int index = fileName.lastIndexOf(".") + 1;
			return fileName.substring(index);
		} catch(Exception e) {
			//The relative path hasn't been set or not contains the format
			return "any";
		}
	}

	@Override
	public Element getElement() {
		Element el = DocumentHelper.createElement("mdmFileServer");
		el.addElement("name").setText(getName());
		el.addElement("description").setText(getDescription());

		Element con = el.addElement("vanillaFileConnection");
		con.addElement("url").setText(getVanillaUrl());
		con.addElement("login").setText(getVanillaLogin());
		con.addElement("password").setText(getVanillaPasword());
		con.addElement("repDefId").setText(repDefinitionId);

		return el;
	}
}
