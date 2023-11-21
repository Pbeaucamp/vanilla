package bpm.vanilla.wopi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import bpm.document.management.core.IDocumentManager;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.remote.RemoteDocumentManager;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class AklaboxWopiManager implements IWopiManager {

	private IVdmManager aklaboxManager;
	private IDocumentManager documentManager;

	//TODO: WOPI - Review security
	public AklaboxWopiManager() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		VdmContext context = new VdmContext(config.getProperty(VanillaConfiguration.P_VANILLA_URL), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD), -1);
		
		this.aklaboxManager = new RemoteVdmManager(context);
		User user = new User();
		user.setEmail(config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN));
		user.setPassword(config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		
		try {
			this.aklaboxManager.connect(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.documentManager = new RemoteDocumentManager(context);
	}

	@Override
	public WopiFileInfo getFileInfo(String fileId) {
		if (fileId.contains("New")) {
			WopiFileInfo info = new WopiFileInfo();
			info.setBaseFileName("TestDoc");
			info.setOwnerId(String.valueOf(1));
			// TODO: WOPI To change with the current user ?
			info.setUserId(String.valueOf(1));
			info.setSha256("");
			info.setSize(0);
			// TODO: WOPI Set last version
			info.setVersion("1");

			SessionManager.getInstance().setFileInfo(fileId, info);
			
			return info;
		}
		else {
			Documents document;
			try {
				document = aklaboxManager.getDocInfo(Integer.parseInt(fileId));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	
			String hash = "";
			try (InputStream is = new FileInputStream(new File(document.getFilePath()))) {
				hash = Utils.getFileSHA256(is);
	
				WopiFileInfo info = new WopiFileInfo();
				info.setBaseFileName(document.getName());
				info.setOwnerId(String.valueOf(document.getUserId()));
				// TODO: WOPI To change with the current user ?
				info.setUserId(String.valueOf(document.getUserId()));
				info.setSha256(hash);
				info.setSize(document.getFileSize());
				// TODO: WOPI Set last version
				info.setVersion("1");
	
				SessionManager.getInstance().setFileInfo(fileId, info);
	
				return info;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public StreamingOutput getFile(String fileId) {
		if (fileId.contains("New")) {
			return null;
		}
		else {
			Documents document;
			try {
				document = aklaboxManager.getDocInfo(Integer.parseInt(fileId));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	
			try {
				final InputStream is = new FileInputStream(new File(document.getFilePath()));
				return new StreamingOutput() {
					@Override
					public void write(OutputStream arg0) throws IOException, WebApplicationException {
						IOUtils.copy(is, arg0);
						
	//					is.close();
					}
				};
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public void putFile(String fileId, InputStream is) {
		try {
			Documents document = aklaboxManager.getDocInfo(Integer.parseInt(fileId));

			documentManager.uploadNewVersion(document.getId(), document.getFileName(), (double) 0, true, is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public ByteArrayOutputStream getFullFile(String fileId) {
		try {
			Documents document;
			try {
				document = aklaboxManager.getDocInfo(Integer.parseInt(fileId));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			InputStream is = new FileInputStream(new File(document.getFilePath()));
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
