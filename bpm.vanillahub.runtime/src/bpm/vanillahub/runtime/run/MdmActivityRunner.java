package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import com.jcraft.jsch.ChannelSftp.LsEntry;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.ValidationSchemaResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.MdmActivity;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.vanillahub.runtime.utils.ActionHelper;
import bpm.vanillahub.runtime.utils.FTPManager;
import bpm.vanillahub.runtime.utils.SFTPFile;
import bpm.vanillahub.runtime.utils.SFTPManager;
import bpm.workflow.commons.beans.Result;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MdmActivityRunner extends ActivityRunner<MdmActivity> {

	private List<ApplicationServer> vanillaServers;
	private List<Source> sources;

	public MdmActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, MdmActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<ApplicationServer> vanillaServers, List<Source> sources) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.vanillaServers = vanillaServers;
		this.sources = sources;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(vanillaServers);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(vanillaServers);
	}

	@Override
	protected void run(Locale locale) {
		try {
			VanillaServer server = (VanillaServer) activity.getResource(vanillaServers);
			Source manageSource = (Source) activity.getManageFileSource(sources);
//			boolean validateData = activity.validateData();
			
			String fileName = result.getFileName();
			ByteArrayInputStream newVersion = result.getInputStream();

			String login = server.getLogin(getParameters(), getVariables());
			String password = server.getPassword(getParameters(), getVariables());
			String vanillaUrl = server.getUrl(getParameters(), getVariables());
			int repositoryId = Integer.parseInt(server.getRepositoryId().getString(getParameters(), getVariables()));

			BaseVanillaContext vctx = new BaseVanillaContext(vanillaUrl, login, password);
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vctx);
			IGedComponent gedComponent = new RemoteGedComponent(vctx);
			IMdmProvider mdmComponent = new MdmRemote(login, password, vanillaUrl);

			User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(login);
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(Integer.parseInt(server.getGroupId().getString(getParameters(), getVariables())));
			Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(server.getRepositoryId().getString(getParameters(), getVariables())));
			
			IRepositoryContext repositoryCtx = new BaseRepositoryContext(vctx, group, repository);
			IRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryCtx);

			Contract contract = null;
			if (activity.isFromMeta()) {
				
				List<MetaValue> values = new ArrayList<MetaValue>();
				if (activity.getMetaDispatch() != null) {
					for (MetaDispatch metaDispatch : activity.getMetaDispatch()) {
						values.add(new MetaValue(metaDispatch.getMetaKey(), metaDispatch.getValue(parameters, variables)));
					}
				}
				List<Integer> contractIds = repositoryApi.getMetaService().getItemsByMeta(TypeMetaLink.ARCHITECT, values);
				if (contractIds == null || contractIds.isEmpty()) {
					StringBuffer buf = new StringBuffer();
					buf.append(Labels.getLabel(locale, Labels.UnableToFindContractsCorrespondingTheFollowingFilters) + " (");
					if (values != null && !values.isEmpty()) {
						boolean first = true;
						for (MetaValue value : values) {
							if (!first) {
								buf.append(", ");
							}
							first = false;
							buf.append("'" + value.getMetaKey() + "': '" + value.getValue() + "'");
						}
					}
					else {
						buf.append(Labels.getLabel(locale, Labels.NoFilterDefine));
					}
					buf.append(")");

					if (manageSource != null) {
						manageFile(locale, manageSource, fileName, newVersion, true);
						addWarning(buf.toString());
						
						result.setResult(Result.SUCCESS);
						return;
					}
					else {
						throw new Exception(buf.toString());
					}
				}
				
				int contractId = contractIds.get(0);
				contract = mdmComponent.getContract(contractId);		
				if (contract == null) {
					manageFile(locale, manageSource, fileName, newVersion, true);
					throw new Exception("Unable to find contract with id '" + contractId + "'");
				}
				
				addInfo(Labels.getLabel(locale, Labels.Found) + " " + contractIds.size() + " " + Labels.getLabel(locale, Labels.contractsAndSelectingTheFirst) + " '" + contract.getName() + "'");
			}
			else {			
				int contractId = activity.getContractId();
				contract = mdmComponent.getContract(contractId);		
				if (contract == null) {
					manageFile(locale, manageSource, fileName, newVersion, true);
					throw new Exception("Unable to find contract with id '" + contractId + "'");
				}
			}
			
			GedDocument doc = null;
			if (contract.getDocId() != null) {
				doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
				if (doc != null) {
					contract.setFileVersions(doc);
				}
			}

			doc = contract.getFileVersions();
			
			InputStream isToUpload = null;
			if (result.isBigFile()) {
				int n = newVersion.available();
				byte[] bytes = new byte[n];
				newVersion.read(bytes, 0, n);
				String f = new String(bytes, StandardCharsets.UTF_8); // Or any encoding.
//				String f = IOUtils.toString(newVersion);
				isToUpload = new FileInputStream(f);
			}
			else {
				isToUpload = newVersion;
			}
			
			String format = null;
			if (doc != null) {
				format = extractFormat(contract, doc, fileName);
				DocumentVersion version = gedComponent.addVersionToDocumentThroughServlet(doc.getId(), format, isToUpload);
				doc.addDocumentVersion(version);
			}
			else {
				format = getFormat(fileName);
				List<Integer> groupIds = mdmComponent.getSupplierSecurity(contract.getParent().getId());

				doc = gedComponent.createDocumentThroughServlet(fileName, format, user.getId(), groupIds, repositoryId, isToUpload);
				contract.setFileVersions(doc);
			}

			contract.setVersionId(null);
			mdmComponent.addContract(contract);

			mdmComponent.launchAssociatedItems(contract.getId(), repositoryCtx, user);
			
			if (newVersion != null) {
				newVersion.reset();
			}
			manageFile(locale, manageSource, fileName, newVersion, false);
			
			result.setResult(Result.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			result.setResult(Result.ERROR);
		}
	}
	
	/**
	 * We check if there is meta define by data (type = validation)
	 * And we set the metadata to the result for next hub action
	 * 
	 * @param repositoryApi
	 * @param contract
	 * @param isToUpload 
	 * @return 
	 * @throws Exception 
	 */
//	private List<MetaLink> manageMeta(IVanillaAPI vanillaApi, IRepositoryApi repositoryApi, Contract contract, String format, InputStream file) throws Exception {
//		List<MetaLink> links = repositoryApi.getMetaService().getMetaLinks(contract.getId(), TypeMetaLink.ARCHITECT, true);
//		logger.info("Found " + (links != null ? links.size() : 0) + " associated meta");
//		if (links != null) {
//			for (MetaLink link : links) {
//				if (link.getMeta() != null && link.getMeta().getType() == TypeMeta.VALIDATION) {
//					logger.info("Checking " + link.getMeta().getLabel());
//					Boolean value = validate(vanillaApi, link.getMeta(), format, file);
//					link.getValue().setValue(value != null ? value.toString() : null);
//				}
//			}
//		}
//		
//		return links;
//	}

	private void manageFile(Locale locale, Source source, String fileName, ByteArrayInputStream fileInputStream, boolean reject) throws Exception {
		if (source != null) {
			VariableString targetFolder = reject ? source.getRejectedFolderPathVS() : source.getTreatedFolderPathVS();
			
			if (source.isCopyFile()) {
				ActionHelper.copy(this, locale, source, fileName, fileInputStream, targetFolder, parameters, variables);
			}
			else {
				Object item = getItemToMove(locale, source, fileName);
				if (item != null) {
					ActionHelper.move(this, locale, source, item, targetFolder, parameters, variables);
				}
			}
		}
	}

	private Object getItemToMove(Locale locale, Source source, String fileName) {
		String folderPath = source.getFolderPath(parameters, variables);
		if (source.getType() == TypeSource.FOLDER) {
			if (source.isNetworkFolder()) {
				String url = folderPath.isEmpty() || folderPath.endsWith("/") ? folderPath : folderPath + "/";
				try {
					NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, source.getLogin(), source.getPassword());
					SmbFile dir = new SmbFile(url, auth);

					for (SmbFile f : dir.listFiles()) {
						if (f.getName().equals(fileName)) {
							return f;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					addError(Labels.getLabel(locale, Labels.FileURl) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.Malformed) + " " + e.getMessage());
				} catch (SmbException e) {
					e.printStackTrace();
					addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + fileName + "' : " + e.getMessage());
				}
			}
			else {
				folderPath = folderPath.isEmpty() || folderPath.endsWith("/") ? folderPath : folderPath + "/";

				try {
					File folder = new File(folderPath);
					if (folder.exists() && folder.isDirectory()) {

						Collection<File> collectionFiles = FileUtils.listFiles(folder, null, source.includeSubfolder());
						for (File file : collectionFiles) {
							if (file.getName().contains(fileName)) {
								return file;
							}
						}
					}
					else {
						addError("'" + folderPath + "' " + Labels.getLabel(locale, Labels.NotAFolder));
					}
				} catch (Exception e) {
					e.printStackTrace();
					addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + fileName + "' : " + e.getMessage());
				}
			}
		}
		else if (source.getType() == TypeSource.FTP) {
			String host = source.getUrl(parameters, variables);
			int port = source.getPort(parameters, variables);
			String folder = source.getFolderPath(parameters, variables);
			boolean secured = source.isSecured();
			String login = source.getLogin();
			String password = source.getPassword();

			FTPManager manager = new FTPManager(host, port, folder, secured, login, password);
			try {
				manager.connect();
				addInfo(Labels.getLabel(locale, Labels.ConnectedToFtp));
			} catch (Exception e) {
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.ConnectionFtpImpossible));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);
			}

			try {
				FTPFile[] files = manager.getFileList(locale, this);
				for (FTPFile f : files) {
					if (f.getName().equals(fileName)) {
						return f;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + fileName + "' : " + e.getMessage());

				setResult(Result.ERROR);
			}
		}
		else if (source.getType() == TypeSource.SFTP) {
			String host = source.getUrl(parameters, variables);
			int port = source.getPort(parameters, variables);
			String folder = source.getFolderPath(parameters, variables);
			boolean secured = source.isSecured();
			String login = source.getLogin();
			String password = source.getPassword();

			SFTPManager manager = new SFTPManager(host, port, folder, secured, login, password);
			try {
				manager.connect();
				addInfo(Labels.getLabel(locale, Labels.ConnectedToSftp));
			} catch (Exception e) {
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.ConnectionSftpImpossible));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);
			}

			try {
				List<SFTPFile> files = manager.getFileList(locale, this, false);
				for (SFTPFile f : files) {
					if (f.getFile().getFilename().equals(fileName)) {
						return f;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + fileName + "' : " + e.getMessage());

				setResult(Result.ERROR);
			}
		}
		return null;
	}

	private String extractFormat(Contract contract, GedDocument doc, String filePath) {
		if (doc != null && doc.getCurrentVersion(contract.getVersionId()) != null && doc.getCurrentVersion(contract.getVersionId()).getFormat() != null) {
			return doc.getLastVersion().getFormat();
		}
		else {
			return getFormat(filePath);
		}
	}

	private String getFormat(String newFileName) {
		int index = newFileName.lastIndexOf(".") + 1;
		return newFileName.substring(index);
	}

	@Override
	protected void clearResources() {
		if(result.isBigFile()) {
			ByteArrayInputStream newVersion = result.getInputStream();
			int n = newVersion.available();
			byte[] bytes = new byte[n];
			newVersion.read(bytes, 0, n);
			String f = new String(bytes, StandardCharsets.UTF_8); // Or any encoding.
//			String f = IOUtils.toString(newVersion);
			new File(f).delete();
		}
		
	}

}
