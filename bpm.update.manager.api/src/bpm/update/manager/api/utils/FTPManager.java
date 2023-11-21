package bpm.update.manager.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;

import bpm.update.manager.api.beans.IProgress;

public class FTPManager {

	private String host, login, password;
	private int port;
	private boolean secured;

	private FTPClient ftp = null;

	public FTPManager(String host, int port, boolean secured, String login, String password) {
		this.host = host;
		this.port = port;
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

	public FTPFile[] getFolderList() throws Exception {
		try {
			return ftp.listDirectories();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public FTPFile[] getFileList(String pathName) throws Exception {
		try {
			if (pathName == null) {
				return ftp.listFiles();
			}
			else {
				return ftp.listFiles(pathName);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public void downloadFile(String remoteFile, OutputStream outputStream, IProgress progress) throws Exception {
		try {
			double fileSize = 0;
			try {
				fileSize = getFileSize(ftp, remoteFile);
				// FileSize in KO
				if (fileSize > 0) {
					fileSize = fileSize / 1024.0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);

			InputStream inputStream = ftp.retrieveFileStream(remoteFile);
			int downloadedBytes = 0;

			progress.setProgress(0);

			// fastCopy(inputStream, outputStream);

			byte[] bytesArray = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(bytesArray)) != -1) {
				outputStream.write(bytesArray, 0, bytesRead);

				downloadedBytes = downloadedBytes + bytesRead;
				double downloadedBytesKo = downloadedBytes / 1024.0;

				double progressValue = progress(fileSize, downloadedBytesKo);
				progress.setProgress(progressValue);
				progress.setDownloadMessage((int) downloadedBytesKo + " / " + (int) fileSize + " Ko");
			}

			progress.setProgress(100);

			boolean success = ftp.completePendingCommand();
			if (!success) {
				throw new Exception("The file can't be downloaded.");
			}

			outputStream.flush();
			outputStream.close();

			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

//	private void fastCopy(final InputStream src, final OutputStream dest) throws IOException {
//		final ReadableByteChannel inputChannel = Channels.newChannel(src);
//		final WritableByteChannel outputChannel = Channels.newChannel(dest);
//		fastCopy(inputChannel, outputChannel);
//	}
//
//	private void fastCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
//		final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
//
//		while (src.read(buffer) != -1) {
//			buffer.flip();
//			dest.write(buffer);
//			buffer.compact();
//		}
//
//		buffer.flip();
//
//		while (buffer.hasRemaining()) {
//			dest.write(buffer);
//		}
//	}

	private long getFileSize(FTPClient ftp, String filePath) throws Exception {
		long fileSize = 0;
		FTPFile[] files = ftp.listFiles(filePath);
		if (files.length == 1 && files[0].isFile()) {
			fileSize = files[0].getSize();
		}
		return fileSize;
	}

	private double progress(double fileSize, double downloadedBytesKo) {
		if (fileSize > 0 && fileSize > downloadedBytesKo) {
			return downloadedBytesKo * 100 / fileSize;
		}
		return 100;

	}
}
