package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.apache.commons.net.io.CopyStreamException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanillahub.core.beans.activities.ActionActivity.TypeAction;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IRunner;
import bpm.workflow.commons.resources.Cible;

public class SFTPManager {

	private String host, folder, login, password;
	private int port;
	// private boolean secured;

	private Session session = null;

	public SFTPManager(String host, int port, String folder, boolean secured, String login, String password) {
		this.host = host;
		this.port = port;
		this.folder = folder;
		// this.secured = secured;
		this.login = login;
		this.password = password;
	}

	public boolean connect() throws Exception {
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(login, host, port);
			session.setPassword(password);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.connect();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("FTP Server Unreachable : " + e.getMessage());
		}
	}

	public void disconnect() {
		if (session != null) {
			try {
				session.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String writeFile(Locale locale, IRunner runner, Cible cible, String fileName, ByteArrayInputStream inputStream) throws Exception {
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp channelSftp = (ChannelSftp) channel;

		if (folder != null && !folder.isEmpty()) {
			if (runner != null) {
				runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
			}
			channelSftp.cd(folder);
		}

		if (runner != null) {
			runner.addInfo(Labels.getLabel(locale, Labels.FtpUpload));
		}
		
		boolean hasUpload = true;
		if (runner.getResult().isBigFile()) {
			int n = inputStream.available();
			byte[] bytes = new byte[n];
			inputStream.read(bytes, 0, n);
			String f = new String(bytes, StandardCharsets.UTF_8);
			channelSftp.put(new FileInputStream(f), fileName);
		}
		else {
			channelSftp.put(inputStream, fileName);
		}
		
		channelSftp.disconnect();

		if (hasUpload) {
			if (runner != null) {
				runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.PutOnFtp));
			}
			return fileName;
		}
		else {
			throw new Exception("Unable to upload file.");
		}
	}

	public ByteArrayInputStream getFile(Locale locale, IRunner runner, String fileFolder, String fileName) throws Exception {
		try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;

			if (fileFolder != null && !fileFolder.isEmpty()) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
				}
				
				channelSftp.cd(fileFolder);
			}
			else if (folder != null && !folder.isEmpty()) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
				}
				
				channelSftp.cd(folder);
			}

			if (runner != null) {
				runner.addInfo(Labels.getLabel(locale, Labels.FtpDownload));
			}

			SftpATTRS stats = channelSftp.lstat(fileName);
			long size = stats.getSize();
			byte[] data;
			boolean hasUpload = false;
			if (size > 1024 * 1024 * 500) {
				String path = ConfigurationManager.getProperty(VanillaConfiguration.HUB_FILE_PATH) + "/temp/" + fileName;

				channelSftp.get(fileName, new FileOutputStream(path));
				data = path.getBytes();
				runner.getResult().setBigFile(true);

				hasUpload = true;
			}
			else {
				channelSftp.get(fileName, bao);
				data = bao.toByteArray();

				hasUpload = true;
			}

			channel.disconnect();

			if (hasUpload) {
				if (runner != null) {
					runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.GetFromFtp));
				}

				return new ByteArrayInputStream(data);
			}
			else {
				throw new Exception("Unable to get file '" + fileName + "'");
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

	@SuppressWarnings("unchecked")
	public List<SFTPFile> getFileList(Locale locale, IRunner runner, boolean includeSubfolder) throws Exception {
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;

			if (folder != null && !folder.isEmpty()) {
				if (runner != null) {
					runner.addInfo(Labels.getLabel(locale, Labels.FolderPlacement));
				}
				channelSftp.cd(folder);
			}

			Vector<LsEntry> items = channelSftp.ls(".");
			
			List<SFTPFile> acceptedFiles = new ArrayList<>();
			if (items != null) {
				for (LsEntry file : items) {
					if (!file.getAttrs().isDir()) {
						acceptedFiles.add(new SFTPFile(folder, file));
					}
					else if (file.getAttrs().isDir() && !file.getFilename().equals(".") && !file.getFilename().equals("..") && includeSubfolder) {
						acceptedFiles.addAll(getSubfiles(channelSftp, folder, file.getFilename(), 0));
					}
				}
			}
			
			return acceptedFiles;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	private List<SFTPFile> getSubfiles(ChannelSftp channelSftp, String parentPath, String parentFolder, int index) throws SftpException {
		// The subfolder are limited to 5 for security reason
		if (index > 5) {
			return new ArrayList<SFTPFile>();
		}
		
		String folder = parentPath + "/" + parentFolder;
		
		channelSftp.cd(folder);

		Vector<LsEntry> items = channelSftp.ls(".");
		
		List<SFTPFile> acceptedFiles = new ArrayList<>();
		if (items != null) {
			for (LsEntry file : items) {
				if (!file.getAttrs().isDir()) {
					acceptedFiles.add(new SFTPFile(folder, file));
				}
				else if (file.getAttrs().isDir() && !file.getFilename().equals(".") && !file.getFilename().equals("..")) {
					acceptedFiles.addAll(getSubfiles(channelSftp, folder, file.getFilename(), index++));
				}
			}
		}
		
		return acceptedFiles;
	}

	public void createDirectory(Locale locale, String to) throws Exception {
		try {
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp) channel;
			
			SftpATTRS attrs=null;
			try {
			    attrs = channelSftp.stat(to);
			} catch (Exception e) {
			    e.printStackTrace();
			}

			if (attrs != null) {
				if (!attrs.isDir()) {
					throw new Exception("This folder is not a directory");
				}
			}
			else {
			    channelSftp.mkdir(to);
			}
			channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public void doAction(Locale locale, IRunner runner, TypeAction action, String fileName, String from, String to) throws Exception {
		Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			
			switch (action) {
			case DELETE:
				delete(channelSftp, locale, runner, fileName, from);
				break;
			case MOVE:
				move(channelSftp, locale, runner, fileName, from, to);
				break;
			case COPY:
				copy(channelSftp, locale, runner, fileName, from, to);
				break;

			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} finally {
			if (channelSftp != null && !channelSftp.isClosed()){
				channelSftp.exit();
	            channelSftp.disconnect();
	        }
			else if (!channel.isClosed()) {
				channel.disconnect();
	        }
		}
	}

	private void copy(ChannelSftp uploadChannel, Locale locale, IRunner runner, String fileName, String from, String to) throws Exception {
		String fromWithFile = from.endsWith("/") ? from + fileName : from + "/" + fileName;
		String toWithFile = to.endsWith("/") ? to + fileName : to + "/" + fileName;

		String fromLog = from == null || from.isEmpty() ? "/" : from;
		String toLog = to == null || to.isEmpty() ? "/" : to;
		
	    Channel downChannel = null;
	    ChannelSftp downloadChannel = null;
	    try {
	        downChannel = session.openChannel("sftp");
	        downChannel.connect();
	        downloadChannel = (ChannelSftp) downChannel;
	        InputStream inputStream = uploadChannel.get(fromWithFile);
	        downloadChannel.put(inputStream, toWithFile);
	    } catch (JSchException e) {
	    	throw new Exception(Labels.getLabel(locale, Labels.ImpossibleToCopy) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.FromFolder) + " '" + fromLog + "' " + Labels.getLabel(locale, Labels.ToFolder) + " '" + toLog + "'.\n " + Labels.getLabel(locale, Labels.Reason) + " : " + e.getMessage());
		} finally {
			if (downloadChannel != null && !downloadChannel.isClosed()){
				downloadChannel.exit();
				downloadChannel.disconnect();
	        }
			else if (!downChannel.isClosed()) {
				downChannel.disconnect();
	        }
		}
	}

	private void delete(ChannelSftp channelSftp, Locale locale, IRunner runner, String fileName, String from) throws Exception {
		String fromWithFile = from.endsWith("/") ? from + fileName : from + "/" + fileName;

		String fromLog = from == null || from.isEmpty() ? "/" : from;

		boolean rename = false;
		try {
			channelSftp.rm(fromWithFile);
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

	private void move(ChannelSftp channelSftp, Locale locale, IRunner runner, String fileName, String from, String to) throws Exception {
		String fromWithFile = from.endsWith("/") ? from + fileName : from + "/" + fileName;
		String toWithFile = to.endsWith("/") ? to + fileName : to + "/" + fileName;

		String fromLog = from == null || from.isEmpty() ? "/" : from;
		String toLog = to == null || to.isEmpty() ? "/" : to;

		try {
			channelSftp.rename(fromWithFile, toWithFile);
		} catch (Exception e) {
			throw new Exception(Labels.getLabel(locale, Labels.ImpossibleToMove) + " '" + fileName + "' " + Labels.getLabel(locale, Labels.FromFolder) + " '" + fromLog + "' " + Labels.getLabel(locale, Labels.ToFolder) + " '" + toLog + "'.\n " + Labels.getLabel(locale, Labels.Reason) + " : " + e.getMessage());
		}

		runner.addInfo("'" + fileName + "' " + Labels.getLabel(locale, Labels.MovedFromFolder) + " '" + fromLog + "' " + Labels.getLabel(locale, Labels.ToFolder) + " '" + toLog + "'");
	}
}
