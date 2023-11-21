package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import com.jcraft.jsch.ChannelSftp.LsEntry;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.ActionActivity;
import bpm.vanillahub.core.beans.activities.ActionActivity.TypeAction;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.utils.ActionHelper;
import bpm.vanillahub.runtime.utils.FTPManager;
import bpm.vanillahub.runtime.utils.SFTPFile;
import bpm.vanillahub.runtime.utils.SFTPManager;
import bpm.vanillahub.runtime.utils.ZipHelper;
import bpm.workflow.commons.beans.Result;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class ActionActivityRunner extends ActivityRunner<ActionActivity> {
	
	private List<Source> sources;

	private List<Object> items;

	private int fileIndex = 0;

	public ActionActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, ActionActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Source> sources) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.sources = sources;
	}

	@Override
	public void run(Locale locale) {
		Source source = (Source) activity.getResource(sources);
		TypeAction action = activity.getTypeAction();
		Source target = activity.getTarget(sources);

		String message = isActionValid(locale, action, source, target);
		if (message != null) {
			addError(message);
			setResult(Result.ERROR);

			return;
		}

		switch (action) {
		case UNZIP:
			addInfo(Labels.getLabel(locale, Labels.ActionUnzip));
			logger.info("Start unzip action");
			break;
		case DELETE:
			addInfo(Labels.getLabel(locale, Labels.ActionDelete));
			logger.info("Start delete action");
			break;
		case COPY:
			addInfo(Labels.getLabel(locale, Labels.ActionCopy));
			logger.info("Start copy action");
			break;
		case MOVE:
			addInfo(Labels.getLabel(locale, Labels.ActionMove));
			logger.info("Start move action");
			break;

		default:
			break;
		}

		Object item = "";
//		String parentFolderName = "";
		if (activity.isLoop() && items != null && !items.isEmpty()) {
			item = items.get(fileIndex);
//			parentFolderName = getParentFolderName(source);
			
		}
		else {

			switch (source.getType()) {
			case FOLDER:
				String filter = source.getFilter(parameters, variables);

				browseFiles(locale, source, filter);
				item = items.get(fileIndex);
//				sourceFile = getFileName(source);
//				parentFolderName = getParentFolderName(source);
				break;
			case FTP:
			case SFTP:
				filter = source.getFilter(parameters, variables);

				browseFiles(locale, source, filter);
				item = items.get(fileIndex);
//				sourceFile = getFileName(source);
//				parentFolderName = getParentFolderName(source);
				break;
			case WEB_SERVICE:
			case D4C:
			case MAIL:
				// Not supported
				break;
			default:
				break;
			}
		}

		if (item != null) {
			try {
				executeAction(locale, action, source, item, target);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.setResult(Result.SUCCESS);
			fileIndex++;
		}
	}

	private String isActionValid(Locale locale, TypeAction action, Source source, Source target) {
		switch (action) {
		case MOVE:
			if (!(source.getType() != TypeSource.FOLDER || (source.getType() == TypeSource.FOLDER && source.isNetworkFolder()) || /*source.getType() != TypeSource.FTP ||*/ source.getType() != TypeSource.SFTP)) {
				return Labels.getLabel(locale, Labels.ThisActionIsNotSupportedForThisTypeOfSource);
			}

			String message = checkTarget(locale, source, target);
			if (message != null) {
				return message;
			}
			break;
		case COPY:
			if (source.getType() != TypeSource.FOLDER) {
				return Labels.getLabel(locale, Labels.ThisActionIsNotSupportedForThisTypeOfSource);
			}
			
			break;
		case DELETE:
			if (source.getType() != TypeSource.FOLDER && /*source.getType() != TypeSource.FTP ||*/ source.getType() != TypeSource.SFTP) {
				return Labels.getLabel(locale, Labels.ThisActionIsNotSupportedForThisTypeOfSource);
			}
			
			break;
		case UNZIP:
			if (source.getType() != TypeSource.FOLDER || (source.getType() == TypeSource.FOLDER && source.isNetworkFolder())) {
				return Labels.getLabel(locale, Labels.ThisActionIsNotSupportedForThisTypeOfSource);
			}
			
			message = checkTarget(locale, source, target);
			if (message != null) {
				return message;
			}
			
			break;
		default:
			break;
		}
		return null;
	}

	private String checkTarget(Locale locale, Source source, Source target) {
		if (target != null) {
			if (source.getType() != target.getType() || !source.getUrlDisplay().equals(target.getUrlDisplay())) {
				return Labels.getLabel(locale, Labels.SourceAndTargetNeedsToBeOnTheSameEnvironnement);
			}
			
			//Source and target need to be on the same environnement
			if (target.getType() == TypeSource.FOLDER) {
				String targetFolderPath = target.getFolderPath(parameters, variables);
				File folderPath = new File(targetFolderPath);
				if (folderPath == null || !folderPath.exists()) {
					if (!folderPath.mkdirs()) {
						return Labels.getLabel(locale, Labels.TargetCannotBeFound);
					}
				}
				else if (!folderPath.isDirectory()) {
					return Labels.getLabel(locale, Labels.TargetNotAFolder);
				}
			}
			else if (target.getType() == TypeSource.FTP) {
//				initFTP();
			}
			else if (target.getType() == TypeSource.SFTP) {
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

					return null;
				}
				String targetFolderPath = target.getFolderPath(parameters, variables);
				try {
					manager.createDirectory(locale, targetFolderPath);
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return Labels.getLabel(locale, Labels.TargetCannotBeFound);
				}
			}
		}
		return Labels.getLabel(locale, Labels.TargetIsNotDefined);
	}

	private void executeAction(Locale locale, TypeAction action, Source source, Object item, Source target) throws Exception {
		VariableString targetFolder = target != null ? target.getFolderPathVS() : null;
		
		switch (action) {
		case UNZIP:
			unzip(locale, item, target);
			break;
		case DELETE:
			delete(locale, source, item);
			break;
		case COPY:
			ActionHelper.copy(this, locale, source, "", item, targetFolder, parameters, variables);
			break;
		case MOVE:
			ActionHelper.move(this, locale, source, item, targetFolder, parameters, variables);
			break;

		default:
			break;
		}
	}

	private void unzip(Locale locale, Object source, Source target) throws Exception {
		String targetFolderPath = target != null ? target.getFolderPath(parameters, variables) : "";
		if (!targetFolderPath.endsWith("/")) {
			targetFolderPath = targetFolderPath + "/";
		}

		ByteArrayInputStream bis = null;
		if (source instanceof SmbFile) {
			bis = getStreamFromSmb(locale, (SmbFile) source);
		}
		else if (source instanceof File) {
			bis = getStreamFromFile(locale, (File) source);
		}
		
		ZipHelper.unzip(bis, targetFolderPath);
	}

	private void delete(Locale locale, Source source, Object item) throws Exception {
		if (item instanceof SmbFile) {
			((SmbFile) item).delete();
		}
		else if (item instanceof File) {
			((File) item).delete();
		}
		else if (item instanceof LsEntry) {
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
			String fileName = ((LsEntry) item).getFilename();
			
			manager.doAction(locale, this, TypeAction.DELETE, fileName, folder, null);
		}
	}

//	private void deleteFolders(Locale locale, Source source) {
//		try {
//			String sourceFolderPath = source.getFolderPath(parameters, variables);
//			String filter = source.getFilter(parameters, variables);
//
//			File sourceFolder = new File(sourceFolderPath);
//			if (sourceFolder.isDirectory() && (filter == null || filter.isEmpty())) {
//				Path directory = Paths.get(sourceFolder.getPath());
//				Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
//					@Override
//					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//						Files.delete(file);
//						return FileVisitResult.CONTINUE;
//					}
//
//					@Override
//					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//						Files.delete(dir);
//						return FileVisitResult.CONTINUE;
//					}
//				});
//			}
//		} catch (Exception e) {
//			addWarning(Labels.getLabel(locale, Labels.UnableDeleteFolders));
//		}
//	}

	private void browseFiles(Locale locale, Source source, String filter) {
		String folderPath = source.getFolderPath(parameters, variables);
		if (source.getType() == TypeSource.FOLDER) {
			if (source.isNetworkFolder()) {
				this.items = new ArrayList<Object>();

				String url = folderPath.isEmpty() || folderPath.endsWith("/") ? folderPath : folderPath + "/";

				addInfo(Labels.getLabel(locale, Labels.ConnexionRemoteFolder));
				if (filter != null && !filter.isEmpty()) {
					addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " '" + filter + "' " + Labels.getLabel(locale, Labels.InFolder) + " " + url);
				}
				else {
					addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " " + Labels.getLabel(locale, Labels.InFolder) + " " + url);
				}

				try {
					NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, source.getLogin(), source.getPassword());
					SmbFile dir = new SmbFile(url, auth);

					for (SmbFile f : dir.listFiles()) {
						if (filter == null || f.getName().contains(filter)) {
							items.add(f);
						}
					}

					if (items.isEmpty()) {
						addError(Labels.getLabel(locale, Labels.NoFileForFilter) + " '" + filter + "'.");
					}
					else {
						addInfo(Labels.getLabel(locale, Labels.NumberOfFileToManage) + " = " + items.size());
					}

					setNumberTotalOfFiles(items.size());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					addError(Labels.getLabel(locale, Labels.FileURl) + " '" + filter + "' " + Labels.getLabel(locale, Labels.Malformed) + " " + e.getMessage());
				} catch (SmbException e) {
					e.printStackTrace();
					addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + filter + "' : " + e.getMessage());
				}
			}
			else {
				folderPath = folderPath.isEmpty() || folderPath.endsWith("/") ? folderPath : folderPath + "/";

				if (filter != null && !filter.isEmpty()) {
					addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " '" + filter + "' " + Labels.getLabel(locale, Labels.InFolder) + " " + folderPath);
				}
				else {
					addInfo(Labels.getLabel(locale, Labels.TryFileRecuperationInFolder) + " " + folderPath);
				}
				try {
					this.items = new ArrayList<Object>();

					File folder = new File(folderPath);
					if (folder.exists() && folder.isDirectory()) {

						Collection<File> collectionFiles = FileUtils.listFiles(folder, null, source.includeSubfolder());
						this.items = checkRegex(collectionFiles, filter);
//						for (File file : collectionFiles) {
//							if (filter == null || file.getName().contains(filter)) {
//								files.add(file);
//							}
//						}

						if (items.isEmpty()) {
							addError(Labels.getLabel(locale, Labels.NoFileForFilter) + " '" + filter + "'.");
						}
						else {
							addInfo(Labels.getLabel(locale, Labels.NumberOfFileToManage) + " = " + items.size());
						}

						setNumberTotalOfFiles(items.size());
					}
					else {
						addError("'" + folderPath + "' " + Labels.getLabel(locale, Labels.NotAFolder));
					}
				} catch (Exception e) {
					e.printStackTrace();
					addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + filter + "' : " + e.getMessage());
				}
			}
		}
		else if (source.getType() == TypeSource.FTP) {
			this.items = new ArrayList<Object>();

			String host = source.getUrl(parameters, variables);
			int port = source.getPort(parameters, variables);
			String folder = source.getFolderPath(parameters, variables);
			boolean secured = source.isSecured();
			String login = source.getLogin();
			String password = source.getPassword();

			addInfo(Labels.getLabel(locale, Labels.ConnectionFTP));
			if (filter != null && !filter.isEmpty()) {
				addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " '" + filter + "' " + Labels.getLabel(locale, Labels.InFolder) + " " + folder);
			}
			else {
				addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " " + Labels.getLabel(locale, Labels.InFolder) + " " + folder);
			}

			FTPManager manager = new FTPManager(host, port, folder, secured, login, password);
			try {
				manager.connect();
				addInfo(Labels.getLabel(locale, Labels.ConnectedToFtp));
			} catch (Exception e) {
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.ConnectionFtpImpossible));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);

				return;
			}

			try {
				FTPFile[] files = manager.getFileList(locale, this);
				items = checkRegex(files, filter);

				if (items.isEmpty()) {
					addError(Labels.getLabel(locale, Labels.NoFileForFilter) + " '" + filter + "'.");
					setResult(Result.ERROR);
				}
				else {
					addInfo(Labels.getLabel(locale, Labels.NumberOfFileToManage) + " = " + items.size());
				}

				setNumberTotalOfFiles(items.size());
			} catch (Exception e) {
				e.printStackTrace();
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + filter + "' : " + e.getMessage());

				setResult(Result.ERROR);
			}
		}
		else if (source.getType() == TypeSource.SFTP) {
			this.items = new ArrayList<Object>();

			String host = source.getUrl(parameters, variables);
			int port = source.getPort(parameters, variables);
			String folder = source.getFolderPath(parameters, variables);
			boolean secured = source.isSecured();
			String login = source.getLogin();
			String password = source.getPassword();

			addInfo(Labels.getLabel(locale, Labels.ConnectionSFTP));
			if (filter != null && !filter.isEmpty()) {
				addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " '" + filter + "' " + Labels.getLabel(locale, Labels.InFolder) + " " + folder);
			}
			else {
				addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + " " + Labels.getLabel(locale, Labels.InFolder) + " " + folder);
			}

			SFTPManager manager = new SFTPManager(host, port, folder, secured, login, password);
			try {
				manager.connect();
				addInfo(Labels.getLabel(locale, Labels.ConnectedToSftp));
			} catch (Exception e) {
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.ConnectionSftpImpossible));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);

				return;
			}

			try {
				List<SFTPFile> files = manager.getFileList(locale, this, source.includeSubfolder());
				items = checkRegex(files, filter);

				if (items.isEmpty()) {
					addError(Labels.getLabel(locale, Labels.NoFileForFilter) + " '" + filter + "'.");
					setResult(Result.ERROR);
				}
				else {
					addInfo(Labels.getLabel(locale, Labels.NumberOfFileToManage) + " = " + items.size());
				}

				setNumberTotalOfFiles(items.size());
			} catch (Exception e) {
				e.printStackTrace();
				manager.disconnect();

				addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + filter + "' : " + e.getMessage());

				setResult(Result.ERROR);
			}
		}
	}

	private List<Object> checkRegex(FTPFile[] files, String filter) {
		List<Object> acceptedFiles = new ArrayList<>();
		if (files != null) {
			for (FTPFile file : files) {
				if (checkRegex(file, filter)) {
					acceptedFiles.add(file);
				}
			}
		}
		return acceptedFiles;
	}

	private List<Object> checkRegex(Collection<File> files, String filter) {
		List<Object> acceptedFiles = new ArrayList<>();
		if (files != null) {
			for (File file : files) {
				if (checkRegex(file, filter)) {
					acceptedFiles.add(file);
				}
			}
		}
		return acceptedFiles;
	}

	private List<Object> checkRegex(List<SFTPFile> files, String filter) {
		List<Object> acceptedFiles = new ArrayList<>();
		if (files != null) {
			for (SFTPFile file : files) {
				if (!file.isDirectory() && checkRegex(file, filter)) {
					acceptedFiles.add(file);
				}
			}
		}
		return acceptedFiles;
	}

	private boolean checkRegex(FTPFile file, String filter) {
		return Pattern.matches(filter, file.getName());
	}

	private boolean checkRegex(File file, String filter) {
		return Pattern.matches(filter, file.getName());
	}

	private boolean checkRegex(SFTPFile file, String filter) {
		return Pattern.matches(filter, file.getFile().getFilename());
	}

//	private String getFileName(Source source) {
//		if (source.isNetworkFolder() && smbFiles != null && !smbFiles.isEmpty()) {
//			SmbFile file = smbFiles.get(fileIndex);
//			return file.getName();
//		}
//		else if (!source.isNetworkFolder() && files != null && !files.isEmpty()) {
//			File file = files.get(fileIndex);
//			return file.getName();
//		}
//		else {
//			return "";
//		}
//	}
//
//	private File getLocalFile(Locale locale, Source source) {
//		if (!source.isNetworkFolder() && files != null && !files.isEmpty()) {
//			File file = files.get(fileIndex);
//
//			fileIndex++;
//
//			return file;
//		}
//		else {
//			addError(Labels.getLabel(locale, Labels.FileNotFound));
//			return null;
//		}
//	}
//
//	private SmbFile getNewtorkFile(Locale locale, Source source) {
//		if (source.isNetworkFolder() && smbFiles != null && !smbFiles.isEmpty()) {
//			SmbFile file = smbFiles.get(fileIndex);
//
//			fileIndex++;
//
//			return file;
//		}
//		else {
//			addError(Labels.getLabel(locale, Labels.FileNotFound));
//			return null;
//		}
//	}

	private ByteArrayInputStream getStreamFromFile(Locale locale, File f) {
		try {
			return new ByteArrayInputStream(FileUtils.readFileToByteArray(f));
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToGetFileContent));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			return null;
		}
	}

	private ByteArrayInputStream getStreamFromSmb(Locale locale, SmbFile f) {
		try (InputStream is = new SmbFileInputStream(f)) {
			byte[] buff = new byte[8000];

			int bytesRead = 0;

			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			while ((bytesRead = is.read(buff)) != -1) {
				bao.write(buff, 0, bytesRead);
			}

			byte[] data = bao.toByteArray();
			return new ByteArrayInputStream(data);
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToGetFileContent));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			return null;
		}
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(sources);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(sources);
	}

	@Override
	protected void clearResources() {
		this.items = null;
	}
}
