package bpm.vanilla.wopi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;

public class OfficeWopiManager implements IWopiManager {

	private RemoteDataVizComponent remoteDataViz;
	private IVanillaAPI vanillaApi;
	private IGedComponent gedComponent;
	private IMdmProvider mdmRemote;

	private String login;

	private int repositoryId;
	private int userId;

	public OfficeWopiManager() {
		try {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

			this.login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
			String pass = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
			String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);

			int groupId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
			this.repositoryId = Integer.parseInt(config.getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));

			Group group = new Group();
			group.setId(groupId);

			Repository repository = new Repository();
			repository.setId(repositoryId);

			BaseVanillaContext ctx = new BaseVanillaContext(url, login, pass);
			IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(ctx, group, repository));

			this.vanillaApi = new RemoteVanillaPlatform(url, login, pass);
			this.gedComponent = new RemoteGedComponent(vanillaApi.getVanillaContext());
			this.mdmRemote = new MdmRemote(login, pass, url);
			this.remoteDataViz = new RemoteDataVizComponent(repositoryApi);

			this.userId = vanillaApi.getVanillaSecurityManager().getUserByLogin(login).getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public WopiFileInfo getFileInfo(String param) {
		try {
			WopiFileInfo info = new WopiFileInfo();

			String fileId = param;
			if (param.contains(",")) {
				fileId = param.substring(0, param.indexOf(","));
			}
			String fileName = "DataPreparation_" + new Object().hashCode() + ".xlsx";

			String hash = "Hash_" + new Object().hashCode();
			int taille = 0;

			info.setBaseFileName(fileName);
			info.setOwnerId(login);
			info.setUserId(login);
			info.setSha256(hash);
			info.setSize(taille);
			info.setVersion("1");

			SessionManager.getInstance().setFileInfo(fileId, info);
			return info;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public StreamingOutput getFile(String param) {
		try {
			String fileId = param;
			if (param.contains(",")) {
				fileId = param.substring(0, param.indexOf(","));
			}
			final InputStream input = getfileInputStream(Integer.parseInt(fileId));
			return new StreamingOutput() {
				@Override
				public void write(OutputStream arg0) throws IOException, WebApplicationException {
					if (input != null) {
						IOUtils.copy(input, arg0);
					}
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void putFile(String fileId, InputStream is) {

		String contractId = fileId.substring(fileId.lastIndexOf(",") + 1);
		String format = "xlsx";
		try {
			Contract contract = this.mdmRemote.getContract(Integer.parseInt(contractId));
			if (contract.getDocId() != null) {
				GedDocument doc = this.gedComponent.getDocumentDefinitionById(contract.getDocId());
				contract.setFileVersions(doc);
			}
			GedDocument doc = contract.getFileVersions();

			if (doc != null) {
				this.gedComponent.addVersionToDocumentThroughServlet(doc.getId(), format, is);
			}
			else {
				List<Integer> groupIds = this.mdmRemote.getSupplierSecurity(contract.getParent().getId());
				doc = this.gedComponent.createDocumentThroughServlet("New Document", format, this.userId, groupIds, this.repositoryId, is);
				contract.setFileVersions(doc);
			}

			contract.setVersionId(null);
			this.mdmRemote.addContract(contract);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ByteArrayOutputStream getFullFile(String param) {
		try {
			String fileId = param;
			if (param.contains(",")) {
				fileId = param.substring(0, param.indexOf(","));
			}
			InputStream is = this.getfileInputStream(Integer.parseInt(fileId));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int sz = 0;
			byte[] buf = new byte[1024];
			while ((sz = is.read(buf)) >= 0) {
				bos.write(buf, 0, sz);
			}
			is.close();
			return bos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public InputStream getfileInputStream(int id) {
		try {
			List<DataPreparation> preps = remoteDataViz.getDataPreparations();
			DataPreparation requestedDataPreparation = findDataPreparation(preps, id);
			ExportPreparationInfo info = new ExportPreparationInfo("DataPreparation", requestedDataPreparation, "xlsx", ",");
			return remoteDataViz.exportDataPreparation(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Search and return if it exists a dataPrepaation in a given list
	 * 
	 * @param list
	 *            a list of dataPrepaation
	 * @param dataPreparationID
	 *            the requested dataPreparation id
	 * @return the dataPreparation or null
	 */
	public DataPreparation findDataPreparation(List<DataPreparation> list, int dataPreparationID) {
		for (DataPreparation data : list) {
			if (data.getId() == dataPreparationID) {
				return data;
			}

		}
		return null;
	}
}
