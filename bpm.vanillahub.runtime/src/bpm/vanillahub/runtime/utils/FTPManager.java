package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamException;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IRunner;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.attributes.FTPAction;

public class FTPManager {

	private String host, folder, login, password;
	private int port;
	private boolean secured;

	private FTPClient ftp = null;

	public FTPManager(String host, int port, String folder, boolean secured, String login, String password) {
		this.host = host;
		this.port = port;
		this.folder = folder;
		this.secured = secured;
		this.login = login;
		this.password = password;
	}

	public boolean connect() throws Exception {
		this.ftp = new FTPClient();
		try {
			ftp.connect(host, port);
			ftp.enterLocalPassiveMode();
			if (secured) {
				return ftp.login(login, password);
			}
			else {
				return ftp.login("anonymous", "");
			}
		} catch (FTPConnectionClosedException e) {
			e.printStackTrace();
			throw new Exception("FTP Server Unreachable : " + e.getMessage());
		} catch (SocketException e) {
			e.printStackTrace();
			throw new Exception("FTP Server Unreachable : " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("FTP Server Unreachable : " + e.getMessage());
		}
	}

	public void disconnect() {
		if (ftp != null) {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String writeFile(Locale locale, IRunner runner, Cible cible, String fileName, ByteArrayInputStream inputStream) throws Exception {
		try {
			boolean hasChange = true;
			if (folder != null && !folder.isEmpty()) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
				}
				hasChange = ftp.changeWorkingDirectory(folder);
			}
			String reply = ftp.getReplyString();
			if (hasChange) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FtpUpload));
				}
				boolean hasUpload;
				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				if(runner.getResult().isBigFile()) {
					int n = inputStream.available();
					byte[] bytes = new byte[n];
					inputStream.read(bytes, 0, n);
					String f = new String(bytes, StandardCharsets.UTF_8);
					hasUpload = ftp.storeFile(fileName, new FileInputStream(f));
				}
				else {
					hasUpload = ftp.storeFile(fileName, inputStream);
				}
				
				reply = ftp.getReplyString();
				if (hasUpload) {
					if (runner != null) {
						runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.PutOnFtp));
					}
					return fileName;
				}
				else {
					throw new Exception(reply);
				}
			}
			else {
				throw new Exception(reply);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public ByteArrayInputStream getFile(Locale locale, IRunner runner, String fileName) throws Exception {
		try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
			boolean hasChange = true;
			if (folder != null && !folder.isEmpty()) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
				}
				hasChange = ftp.changeWorkingDirectory(folder);
			}
			String reply = ftp.getReplyString();
			if (hasChange) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FtpDownload));
				}

				ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

				FTPFile[] f = ftp.listFiles(fileName);
				long size = f[0].getSize();
				boolean hasUpload;
				byte[] data;
				if(size > 1024 * 1024 * 500) {
					String path = ConfigurationManager.getProperty(VanillaConfiguration.HUB_FILE_PATH) + "/temp/" + fileName;
					 
					hasUpload = ftp.retrieveFile(fileName, new FileOutputStream(path));
					data = path.getBytes();
					runner.getResult().setBigFile(true);
				}
				else {
					hasUpload = ftp.retrieveFile(fileName, bao);
					data = bao.toByteArray();
				}
				
				
				
				reply = ftp.getReplyString();
				if (hasUpload) {
					if (runner != null) {
						runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.GetFromFtp));
					}

					
					return new ByteArrayInputStream(data);
				}
				else {
					throw new Exception(reply);
				}
			}
			else {
				throw new Exception(reply);
			}
		} catch (IOException e) {
			if (e instanceof CopyStreamException) {
				CopyStreamException ex = (CopyStreamException) e;
				ex.getIOException().printStackTrace();
			}
			else {
				e.printStackTrace();
			}
			throw new Exception(e.getMessage());
		}
	}

	public FTPFile[] getFileList(Locale locale, IRunner runner) throws Exception {
		try {
			boolean hasChange = true;
			if (folder != null && !folder.isEmpty()) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
				}
				hasChange = ftp.changeWorkingDirectory(folder);
			}
			String reply = ftp.getReplyString();
			if (hasChange) {
				return ftp.listFiles();
			}
			else {
				throw new Exception(reply);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public void doAction(Locale locale, IRunner runner, FTPAction action, String fileName, String from, String to) throws Exception {
		switch (action.getType()) {
		case DELETE:
			delete(locale, runner, fileName, from);
			break;
		case MOVE:
			move(locale, runner, fileName, from, to);
			break;

		default:
			break;
		}
	}

	private void delete(Locale locale, IRunner runner, String fileName, String from) throws Exception {
		String fromWithFile = from.endsWith("/") ? from + fileName : from + "/" + fileName;

		String fromLog = from == null || from.isEmpty() ? "/" : from;

		boolean rename = false;
		try {
			rename = ftp.deleteFile(fromWithFile);
		} catch (Exception e) {
			throw new Exception(Labels.getLabel(locale, Labels.ImpossibleToDelete) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.FromFolder) + " '" + fromLog + "'.\n " + Labels.getLabel(locale, Labels.Reason) + " : " + e.getMessage());
		}

		if (rename) {
			runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.DeleteFromFolder) + " '" + fromLog + "'");
		}
		else {
			throw new Exception(Labels.getLabel(locale, Labels.ImpossibleToDelete) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.FromFolder) + " '" + fromLog + "'");
		}
	}

	private void move(Locale locale, IRunner runner, String fileName, String from, String to) throws Exception {
		String fromWithFile = from.endsWith("/") ? from + fileName : from + "/" + fileName;
		String toWithFile = to.endsWith("/") ? to + fileName : to + "/" + fileName;

		String fromLog = from == null || from.isEmpty() ? "/" : from;
		String toLog = to == null || to.isEmpty() ? "/" : to;

		boolean rename = false;
		try {
			rename = ftp.rename(fromWithFile, toWithFile);
		} catch (Exception e) {
			throw new Exception(Labels.getLabel(locale, Labels.ImpossibleToMove) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.FromFolder) + " '" + fromLog + "' " + Labels.getLabel(locale, Labels.ToFolder) + " '" + toLog + "'.\n " + Labels.getLabel(locale, Labels.Reason) + " : " + e.getMessage());
		}

		if (rename) {
			runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.MovedFromFolder) + " '" + fromLog + "' " + Labels.getLabel(locale, Labels.ToFolder) + " '" + toLog + "'");
		}
		else {
			throw new Exception(Labels.getLabel(locale, Labels.ImpossibleToMove) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.FromFolder) + " '" + fromLog + "' " + Labels.getLabel(locale, Labels.ToFolder) + " '" + toLog + "'");
		}
	}
}
