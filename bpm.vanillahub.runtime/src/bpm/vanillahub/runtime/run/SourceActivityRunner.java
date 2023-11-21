package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.jaxen.dom4j.Dom4jXPath;
import org.json.JSONObject;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.SOARequestCreator;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.SourceActivity;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceParameter;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.managers.WebServiceManager;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.vanillahub.runtime.utils.FTPManager;
import bpm.vanillahub.runtime.utils.MailHelper;
import bpm.vanillahub.runtime.utils.SFTPFile;
import bpm.vanillahub.runtime.utils.MailHelper.MailAttachment;
import bpm.vanillahub.runtime.utils.SFTPManager;
import bpm.workflow.commons.beans.Result;
import groovy.xml.MarkupBuilder;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SourceActivityRunner extends ActivityRunner<SourceActivity> {

	private Logger logger = Logger.getLogger(SourceActivityRunner.class);

	private List<Source> sources;

	private List<Object> items;

	private int fileIndex = 0;

	public SourceActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, SourceActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Source> sources) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.sources = sources;
	}

	@Override
	public void run(Locale locale) {
		Source source = (Source) activity.getResource(sources);
		String cibleItem = activity.getCibleItem(parameters, variables);
		String filter = cibleItem != null && !cibleItem.isEmpty() ? cibleItem : source.getFilter(parameters, variables);

		ByteArrayInputStream bis = null;
		String outputFileName = "";
		IResultInformation resultInformation = new SimpleResultInformation("");
		if (activity.isLoop() && items != null && !items.isEmpty()) {
			outputFileName = getFileName(source);
			resultInformation = getParentFolderName(source);

			bis = getStreamFile(locale, source, filter);
		}
		else {

			switch (source.getType()) {
			case FOLDER:
				browseFiles(locale, source, filter);
				outputFileName = getFileName(source);
				resultInformation = getParentFolderName(source);

				bis = getStreamFile(locale, source, filter);
				break;
			case WEB_SERVICE:
				outputFileName = getFileName(source);
				resultInformation = getParentFolderName(source);

				bis = callWebService(locale, source);
				break;
			case FTP:
			case SFTP:
				browseFiles(locale, source, filter);
				outputFileName = getFileName(source);
				resultInformation = getParentFolderName(source);

				bis = getStreamFile(locale, source, filter);
				break;
			case D4C:
				browseResources(locale, source);
				
				outputFileName = getFileName(source);
				//We get the main resource because it will get use by the target
				resultInformation = getParentFolderName(source);
				
				bis = getStreamFile(locale, source, filter);
				break;
			case MAIL:
				browseMails(locale, source, filter);
				
				outputFileName = getFileName(source);
				//We get the main resource because it will get use by the target
				resultInformation = getParentFolderName(source);
				
				bis = getStreamFile(locale, source, filter);
				break;
			default:
				break;
			}
		}

		if (bis != null) {
			result.setFileName(outputFileName);
			result.putInfoComp(resultInformation instanceof CkanResourceResultInformation ? TypeResultInformation.CKAN_RESOURCE : TypeResultInformation.SIMPLE, resultInformation);
			result.setInputStream(bis);

			if (activity.isLoop() && !isActionComplete(source, fileIndex)) {
				result.setResult(Result.RUNNING);
			}
			else {
				clearResources();
				result.setResult(Result.SUCCESS);
			}
		}
		else if (result.getResult() == null || result.getResult() != Result.ERROR) {
			addError(Labels.getLabel(locale, Labels.WebServiceDidNotSendData));

			result.setResult(Result.ERROR);
		}
	}

	private boolean isActionComplete(Source source, int fileIndex) {
		return source.getType() == TypeSource.WEB_SERVICE || (items == null || items.size() <= fileIndex);
	}

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
		return file.getFile().getFilename().equals(filter) || Pattern.matches(filter, file.getFile().getFilename());
	}

	private String getFileName(Source source) {
		if (!activity.isLoop() && source.useOutputName()) {
			return source.getOutputName(parameters, variables);
		}
		
		if (source.getType() == TypeSource.FOLDER && items != null && !items.isEmpty()) {
			if (source.isNetworkFolder()) {
				SmbFile file = (SmbFile) items.get(fileIndex);
				return file.getName();
			}
			else if (!source.isNetworkFolder()) {
				File file = (File) items.get(fileIndex);
				return file.getName();
			}
		}
		else if (source.getType() == TypeSource.FTP && items != null && !items.isEmpty()) {
			FTPFile file = (FTPFile) items.get(fileIndex);
			return file.getName();
		}
		else if (source.getType() == TypeSource.WEB_SERVICE) {
			return source.getOutputName(parameters, variables);
		}
		else if (source.getType() == TypeSource.D4C) {
			CkanResource resource = (CkanResource) items.get(fileIndex);
			//If it is the main resource and we use output file, we return the outputName
			if (resource.getId().equals(source.getCkanPackage().getSelectedResource().getId()) && source.useOutputName()) {
				return source.getOutputName(parameters, variables);
			}
			return resource.getName();
		}
		else if (source.getType() == TypeSource.MAIL && items != null && !items.isEmpty()) {
			MailAttachment file = (MailAttachment) items.get(fileIndex);
			return file.getName();
		}
		else if (source.getType() == TypeSource.SFTP && items != null && !items.isEmpty()) {
			SFTPFile file = (SFTPFile) items.get(fileIndex);
			return file.getFile().getFilename();
		}

		return "";
	}

	private IResultInformation getParentFolderName(Source source) {
		if (source.getType() == TypeSource.FOLDER && items != null && !items.isEmpty()) {
			if (source.isNetworkFolder()) {
				SmbFile file = (SmbFile) items.get(fileIndex);
				return new SimpleResultInformation(file.getParent());
			}
			else if (!source.isNetworkFolder()) {
				File file = (File) items.get(fileIndex);
				return new SimpleResultInformation(file.getParentFile() != null && file.getParentFile().getParentFile() != null ? file.getParentFile().getParentFile().getName() + "\\" + file.getParentFile().getName() : "Unknown");
			}
		}
		else if (source.getType() == TypeSource.D4C && items != null && !items.isEmpty()) {
			CkanResource resource = (CkanResource) items.get(fileIndex);
			
			boolean isMainResource = resource.getId().equals(source.getCkanPackage().getSelectedResource().getId());
			boolean isFile = isFormatAllowed(resource.getFormat());
			String format = resource.getFormat() != null ? resource.getFormat() : "";
			return new CkanResourceResultInformation(isMainResource, isFile, format);
		}
		else if (source.getType() == TypeSource.SFTP && items != null && !items.isEmpty()) {
			SFTPFile file = (SFTPFile) items.get(fileIndex);
			
			// We get the last folder if it is a path
			Path path = Paths.get(file.getParentFolder());
	        // Get the last element in the path
	        Path lastDirectory = path.getFileName();
	        String lastDirectoryString = lastDirectory.toString();
			
			return new SimpleResultInformation(lastDirectoryString);
		}

		return new SimpleResultInformation("");
	}

	private boolean isFormatAllowed(String format) {
		if (format != null) {
			return format.equalsIgnoreCase("CSV") || format.equalsIgnoreCase("XLS")
					|| format.equalsIgnoreCase("XLSX") || format.equalsIgnoreCase("PDF") || format.equalsIgnoreCase("JSON")
					|| format.equalsIgnoreCase("GEOJSON") || format.equalsIgnoreCase("ZIP");
		}
		return false;
	}

	private void browseResources(Locale locale, Source source) {
		this.items = new ArrayList<Object>();
		if (activity.isLoop()) {
			addInfo(Labels.getLabel(locale, Labels.TryResourcesRecuperation));
			
			String url = source.getUrl(parameters, variables);
			String org = source.getLogin();
			String apiKey = source.getPassword();
			
			CkanPackage pack = source.getCkanPackage();
			CkanHelper ckanHelper = new CkanHelper(url, org, apiKey);
			try {
				pack = ckanHelper.getCkanPackage(pack.getId());
				if (pack.getResources() != null) {
					for (CkanResource resource : pack.getResources()) {
						this.items.add(resource);
					}
				}
				
				addInfo(Labels.getLabel(locale, Labels.NumberOfResourceToManage) + " = " + items.size());

				setNumberTotalOfFiles(items.size());
			} catch (Exception e) {
				addError(Labels.getLabel(locale, Labels.ConnectionD4CImpossible));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);
			}
		}
		else {
			this.items.add(source.getCkanPackage().getSelectedResource());
		}
	}

	private void browseMails(Locale locale, Source source, String filter) {
		String url = source.getUrl(parameters, variables);
		String folder = source.getFolderPath(parameters, variables);
		boolean rejectNonMatchingMail = source.isRejectNonMatchingMail();
		boolean copyMail = source.isCopyFile();
		String rejectedFolder = source.getRejectedFolderPath(parameters, variables);
		String attachmentFilter = source.getAttachmentFilter(parameters, variables);
		
		String login = source.getLogin();
		String password = source.getPassword();
		
		this.items = new ArrayList<Object>();
		addInfo(Labels.getLabel(locale, Labels.ConnectionMail));
		
		String filterMessage = filter != null && !filter.isEmpty() ?  " '" + filter + "' " : " ";
		String attachmentFilterMessage = attachmentFilter != null && !attachmentFilter.isEmpty() ? Labels.getLabel(locale, Labels.withAttachmentFilter) + " '" + attachmentFilter + "' " : " ";

		addInfo(Labels.getLabel(locale, Labels.TryFileRecuperation) + filterMessage + attachmentFilterMessage + Labels.getLabel(locale, Labels.InFolder) + " " + folder);

		MailHelper manager = new MailHelper();
		try {
			List<MailAttachment> attachments = manager.browseAttachments(url, login, password, folder, rejectNonMatchingMail, copyMail, rejectedFolder, filter, attachmentFilter);
			if (attachments != null) {
				for (MailAttachment attachment : attachments) {
					items.add(attachment);
				}
			}
			
			if (items.isEmpty()) {
				addError(Labels.getLabel(locale, Labels.NoFileForFilter) + " '" + filter + "' " + Labels.getLabel(locale, Labels.orAttachmentFilter) + " '" + attachmentFilter + "'.");
				setResult(Result.ERROR);
			}
			else {
				addInfo(Labels.getLabel(locale, Labels.NumberOfFileToManage) + " = " + items.size());
			}

			setNumberTotalOfFiles(items.size());
		} catch (Exception e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.FileRecuperationImpossible) + " '" + filter + "' : " + e.getMessage());

			setResult(Result.ERROR);
		}
	}

	private ByteArrayInputStream getStreamFile(Locale locale, Source source, String filter) {
		if (source.getType() == TypeSource.FOLDER && items != null && !items.isEmpty()) {
			if (source.isNetworkFolder()) {
				SmbFile file = (SmbFile) items.get(fileIndex);

				fileIndex++;

				return getStreamFromSmb(locale, file);
			}
			else if (!source.isNetworkFolder()) {
				File file = (File) items.get(fileIndex);

				fileIndex++;

				return getStreamFromFile(locale, file);
			}
		}
		else if (source.getType() == TypeSource.FTP && items != null && !items.isEmpty()) {
			FTPFile file = (FTPFile) items.get(fileIndex);

			fileIndex++;

			return getStreamFromFtp(locale, source, file);
		}
		else if (source.getType() == TypeSource.D4C && items != null && !items.isEmpty()) {

			String url = source.getUrl(parameters, variables);
			String org = source.getLogin();
			String apiKey = source.getPassword();
			
			CkanResource resource = (CkanResource) items.get(fileIndex);

			fileIndex++;
			
			CkanHelper ckanHelper = new CkanHelper(url, org, apiKey);
			try {
				//We try to get the file if the format is defined or if it is the main resource
				if (isFormatAllowed(resource.getFormat()) || source.getCkanPackage().getSelectedResource().getId().equals(resource.getId())) {
					return ckanHelper.getResourceFileAsByteArray(resource);
				}
				else {
					JSONObject item = new JSONObject(resource);
					return new ByteArrayInputStream(item.toString().getBytes());
				}
			} catch (Exception e) {
				addError(Labels.getLabel(locale, Labels.ConnectionD4CImpossible));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);

				return null;
			}
		}
		else if (source.getType() == TypeSource.MAIL && items != null && !items.isEmpty()) {
			MailAttachment file = (MailAttachment) items.get(fileIndex);

			fileIndex++;
			
			boolean manageMail = !checkIfAttachementIsLastFromMail(file, items, fileIndex);
			return getStreamFromMail(locale, source, filter, file, manageMail);
		}
		else if (source.getType() == TypeSource.SFTP && items != null && !items.isEmpty()) {
			SFTPFile file = (SFTPFile) items.get(fileIndex);

			fileIndex++;

			return getStreamFromSftp(locale, source, file);
		}

		addError(Labels.getLabel(locale, Labels.FileNotFound));
		return null;
	}

	private boolean checkIfAttachementIsLastFromMail(MailAttachment file, List<Object> items, int fileIndex) {
		if (items.size() > fileIndex) {
			for (int i=fileIndex; i < items.size(); i++) {
				MailAttachment attachement = items.get(i) != null ? (MailAttachment) items.get(i) : null;
				if (attachement != null && file.getKeyMail().equals(attachement.getKeyMail())) {
					return true;
				}
			}
		}
		return false;
	}

	private ByteArrayInputStream getStreamFromFtp(Locale locale, Source source, FTPFile file) {
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

			return null;
		}

		try {
			ByteArrayInputStream bis = manager.getFile(locale, this, file.getName());
			manager.disconnect();
			return bis;
		} catch (Exception e) {
			manager.disconnect();

			addError(Labels.getLabel(locale, Labels.SendingFileFtpImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return null;
		}
	}

	private ByteArrayInputStream getStreamFromSftp(Locale locale, Source source, SFTPFile file) {
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

		try {
			ByteArrayInputStream bis = manager.getFile(locale, this, file.getParentFolder(), file.getFile().getFilename());
			manager.disconnect();
			return bis;
		} catch (Exception e) {
			manager.disconnect();

			addError(Labels.getLabel(locale, Labels.SendingFileFtpImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return null;
		}
	}

	private ByteArrayInputStream getStreamFromFile(Locale locale, File f) {
		try {
			long size = FileUtils.sizeOf(f);
			if(size > 1024 * 1024 * 500) {
				String path = f.getAbsolutePath();
				result.setBigFile(true);
				return new ByteArrayInputStream(path.getBytes());
			}
			else {
				return new ByteArrayInputStream(FileUtils.readFileToByteArray(f));
			}
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

	private ByteArrayInputStream getStreamFromMail(Locale locale, Source source, String filter, MailAttachment file, boolean manageMail) {
		String host = source.getUrl(parameters, variables);
		String folder = source.getFolderPath(parameters, variables);
		String treatedFolder = source.getTreatedFolderPath(parameters, variables);
		boolean copyMail = source.isCopyFile();
		
		String login = source.getLogin();
		String password = source.getPassword();

		MailHelper manager = new MailHelper();
		try {
			return manager.getAttachmentFile(host, login, password, folder, manageMail, copyMail, treatedFolder, filter, file.getName());
		} catch (Exception e) {
			addError(Labels.getLabel(locale, Labels.GetMailFileImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return null;
		}
	}

	private ByteArrayInputStream callWebService(Locale locale, Source source) {
		try {
			String webServiceUrl = source.getUrl(parameters, variables);
			WebServiceMethodDefinition method = source.getMethod();
			String namespace = WebServiceManager.getTargetNamespace(webServiceUrl);

			String soapRequest = createSOAPRequest(locale, webServiceUrl, namespace, method);

			addInfo(Labels.getLabel(locale, Labels.SendSoapRequest));
			String response = sendMessage(method.getLocation(), method.getName(), soapRequest);
			return parseResponse(method, response);
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.WebServiceCallImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
			return null;
		}
	}

	private String createSOAPRequest(Locale locale, String webServiceUrl, String namespace, WebServiceMethodDefinition method) throws Exception {
		addInfo(Labels.getLabel(locale, Labels.SoapRequestGeneration));

		WSDLParser parser = new WSDLParser();
		Definitions wsdl = parser.parse(webServiceUrl);
		StringWriter writer = new StringWriter();

		HashMap<String, String> formParams = new HashMap<String, String>();
		if (method.getParameters() != null) {
			for (WebServiceParameter param : method.getParameters()) {
				formParams.put("xpath:/" + method.getMessageNameInput() + param.getParentPath() + "/" + param.getName(), getParameterValue(param));
			}
		}

		SOARequestCreator creator = new SOARequestCreator(wsdl, new RequestCreator(), new MarkupBuilder(writer));
		creator.setFormParams(formParams);
		creator.createRequest(method.getPortName(), method.getName(), method.getBindingName());

		String soapRequest = writer.toString();
		addInfo(soapRequest);
		return writer.toString();
	}

	private String getParameterValue(WebServiceParameter param) {
		Object value = param.getParameterValue(parameters, variables);
		switch (param.getType()) {
		case TYPE_DATE:
			if (value instanceof Date) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				return format.format((Date) value);
			}
			else {
				return (String) value;
			}
		case TYPE_BOOLEAN:
		case TYPE_DOUBLE:
		case TYPE_FLOAT:
		case TYPE_INT:
		case TYPE_LONG:
		case TYPE_OBJECT:
		case TYPE_STRING:
			return String.valueOf(value);
		default:
			break;
		}
		return null;
	}

	private static String sendMessage(String location, String soapAction, String message) throws IOException {

		URL url = new URL(location);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction", soapAction);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);

		PrintWriter pw = new PrintWriter(new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8"));
		pw.write(message);
		pw.close();

		InputStream is = httpConn.getInputStream();

		String result = IOUtils.toString(is, "UTF-8");
		is.close();
		httpConn.disconnect();

		return result;
	}

	private ByteArrayInputStream parseResponse(WebServiceMethodDefinition method, String response) {
		try {
			Document doc = DocumentHelper.parseText(response);
			org.jaxen.XPath xpath = new Dom4jXPath("//*[local-name()='" + method.getMessageNameOutput() + "']");
			org.dom4j.Element result = (org.dom4j.Element) xpath.selectSingleNode(doc);

			for (Object child : result.elements()) {
				if (child instanceof org.dom4j.Element) {
					org.dom4j.Element childEl = (org.dom4j.Element) child;

					logger.debug(childEl.asXML());

					return new ByteArrayInputStream(childEl.asXML().getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
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
