package bpm.vanilla.wopi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.IComProperties;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;

public class VanillaWopiManager implements IWopiManager {

	private IVanillaAPI vanillaApi;
	private IGedComponent gedComponent;

	public VanillaWopiManager() {
		vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		gedComponent = new RemoteGedComponent(vanillaApi.getVanillaContext());
	}

	@Override
	public StreamingOutput getFile(String fileId) {
		try {
			WopiFileInfo fileInfo = SessionManager.getInstance().getFileInfo(fileId);
			DocumentVersion docVersion = gedComponent.getDocumentVersionById(Integer.parseInt(fileInfo.getVersion()));

			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), docVersion.getModifiedBy(), docVersion.getVersion());

			final InputStream is = gedComponent.loadGedDocument(config);

			return new StreamingOutput() {
				@Override
				public void write(OutputStream arg0) throws IOException, WebApplicationException {
					IOUtils.copy(is, arg0);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public WopiFileInfo getFileInfo(String fileId) {
		try {
			GedDocument doc = gedComponent.getDocumentDefinitionById(Integer.parseInt(fileId));

			DocumentVersion version = doc.getLastVersion();

			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(version.getParent(), version.getModifiedBy(), version.getVersion());

			final InputStream is = gedComponent.loadGedDocument(config);

			String hash = Utils.getFileSHA256(is);

			WopiFileInfo info = new WopiFileInfo();
			info.setBaseFileName(version.getDocumentPath().substring(version.getDocumentPath().lastIndexOf("/") + 1, version.getDocumentPath().length()));
			info.setOwnerId(vanillaApi.getVanillaContext().getLogin());
			info.setUserId(vanillaApi.getVanillaContext().getLogin());
			info.setSha256(hash);
			String rootDir = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_GED_INDEX_DIRECTORY);
			String path = rootDir + version.getDocumentPath();
			path = path.replace("/\\", "/");

			File file = new File(path);
			info.setSize(file.length());
			info.setVersion(String.valueOf(version.getId()));

			SessionManager.getInstance().setFileInfo(fileId, info);

			return info;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void putFile(String fileId, InputStream is) {
		try {
			GedDocument doc = gedComponent.getDocumentDefinitionById(Integer.parseInt(fileId));
			DocumentVersion version = doc.getLastVersion();
			IComProperties comProps = new ComProperties();

			GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(comProps, doc.getCreatedBy(), -1, new ArrayList<Integer>(), -1, version.getFormat(), doc.getId(), version.getId());

			gedComponent.index(config, is);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ByteArrayOutputStream getFullFile(String fileId) {
		try {
			WopiFileInfo fileInfo = SessionManager.getInstance().getFileInfo(fileId);
			DocumentVersion docVersion = gedComponent.getDocumentVersionById(Integer.parseInt(fileInfo.getVersion()));

			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), docVersion.getModifiedBy(), docVersion.getVersion());

			InputStream is = gedComponent.loadGedDocument(config);
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

}
