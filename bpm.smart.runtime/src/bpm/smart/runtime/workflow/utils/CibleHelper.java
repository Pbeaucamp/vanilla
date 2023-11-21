package bpm.smart.runtime.workflow.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import bpm.smart.runtime.i18n.Labels;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.attributes.FTPAction;

public class CibleHelper {

	private Logger logger = Logger.getLogger(this.getClass());

	public CibleHelper(ActivityLog runner, Locale locale, Cible cible, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables) {
		String host = cible.getUrl(parameters, variables);

		switch (cible.getType()) {
		case FTP:
			sendFTPFile(runner, locale, cible, host, fileName, fileStream, parameters, variables);
			break;
		case HTTP:
			sendHTTPFile(runner, locale, cible, host, fileName, fileStream, parameters, variables);
			break;
		case FOLDER:
			sendFolder(runner, locale, cible, fileName, fileStream, parameters, variables);
			break;

		default:
			break;
		}
	}

	private void sendFTPFile(ActivityLog runner, Locale locale, Cible cible, String host, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables) {
		int port = cible.getPort(parameters, variables);
		String folder = cible.getFolder(parameters, variables);
		boolean secured = cible.isSecured();
		String login = cible.getLogin();
		String password = cible.getPassword();

		FTPManager manager = new FTPManager(host, port, folder, secured, login, password);
		try {
			manager.connect();
			runner.addInfo(Labels.getLabel(locale, Labels.ConnectedToFtp));
		} catch (Exception e) {
			manager.disconnect();

			runner.addError(Labels.getLabel(locale, Labels.ConnectionFtpImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		try {
			fileName = manager.writeFile(locale, runner, cible, fileName, fileStream);
		} catch (Exception e) {
			manager.disconnect();

			runner.addError(Labels.getLabel(locale, Labels.SendingFileFtpImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		try {
			if (cible.getActions() != null && !cible.getActions().isEmpty()) {
				if (fileName == null) {
					throw new Exception(Labels.getLabel(locale, Labels.FtpActionCannotBeDone));
				}

				for (FTPAction action : cible.getActions()) {
					String to = action.getValue(parameters, variables);

					manager.doAction(locale, runner, action, fileName, folder, to);
				}
			}
		} catch (Exception e) {
			manager.disconnect();

			runner.addError(Labels.getLabel(locale, Labels.ActionFtpImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		manager.disconnect();
		runner.setResult(Result.SUCCESS);
	}

	private void sendHTTPFile(ActivityLog runner, Locale locale, Cible cible, String uploadUrl, String fileName, ByteArrayInputStream inputStream, List<Parameter> parameters, List<Variable> variables) {
		try {
			String httpFileParam = cible.getHttpFileParam(parameters, variables);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart(httpFileParam, new InputStreamBody(inputStream, ContentType.APPLICATION_XML, fileName));

			// TODO: Add parameters (see
			// bpm.gateway.core/bpm.gateway.runtime2.transformation.tsbn.RunSyriusConnector.sendTheFile()
			// multipart.addPart("userId", new StringBody(userId));

			HttpPost post = new HttpPost(uploadUrl);
			post.setEntity(builder.build());

			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();

			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));

			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			reader.close();

			runner.addInfo(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();

			runner.addError(Labels.getLabel(locale, Labels.SendingFileFtpImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		runner.setResult(Result.SUCCESS);
	}

	private void sendFolder(ActivityLog runner, Locale locale, Cible cible, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables) {
		String folder = cible.getFolder(parameters, variables);
		if (cible.isNetworkFolder() && folder != null && !folder.isEmpty()) {
			String login = cible.getLogin();
			String password = cible.getPassword();

			sendFolderNetwork(runner, locale, folder, login, password, fileName, fileStream, cible.isOverride());
		}
		else if (folder != null && !folder.isEmpty()) {
			sendFolderLocal(runner, locale, folder, fileName, fileStream, cible.isOverride());
		}
		else {
			runner.addError(Labels.getLabel(locale, Labels.NoFolderDefine));
		}
	}

	private void sendFolderNetwork(ActivityLog runner, Locale locale, String folderPath, String login, String password, String fileName, ByteArrayInputStream fileStream, boolean override) {
		SmbFile file = null;
		try {
			if (login != null && password != null && !login.isEmpty() && !password.isEmpty()) {
				NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, login, password);
				SmbFile folder = new SmbFile(folderPath, auth);

				if (!folder.exists()) {
					logger.info("No folder at " + folderPath + ". Creating it.");
					throw new Exception(Labels.getLabel(locale, Labels.UnableToCreateFolder));
				}

				file = new SmbFile(folder.getPath() + "/" + fileName, auth);
				if (file.exists() && !override) {
					runner.addError(Labels.getLabel(locale, Labels.UnableToAccessFolder));
					runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + Labels.getLabel(locale, Labels.FileAlreadyExist));

					runner.setResult(Result.ERROR);

					return;
				}
				else {
					file.createNewFile();
				}
			}
			else {
				SmbFile folder = new SmbFile(folderPath);
				if (!folder.exists()) {
					logger.info("No folder at " + folderPath + ". Creating it.");
					throw new Exception(Labels.getLabel(locale, Labels.UnableToCreateFolder));
				}

				file = new SmbFile(folder.getPath() + "/" + fileName);
				if (file.exists() && !override) {
					runner.addError(Labels.getLabel(locale, Labels.UnableToAccessFolder));
					runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + Labels.getLabel(locale, Labels.FileAlreadyExist));

					runner.setResult(Result.ERROR);

					return;
				}
				else {
					file.createNewFile();
				}
			}
		} catch (Exception e) {
			runner.addError(Labels.getLabel(locale, Labels.UnableToAccessFolder));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		try (SmbFileOutputStream sfos = new SmbFileOutputStream(file)) {
			logger.info("Saving file...");
			InputStream inputStream = fileStream;

			byte[] buffer = new byte[8992];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				sfos.write(buffer, 0, bytesRead);
			}
			logger.info("File saved.");
		} catch (Exception e) {
			e.printStackTrace();

			runner.addError(Labels.getLabel(locale, Labels.SavingFileImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		runner.setResult(Result.SUCCESS);
	}

	private void sendFolderLocal(ActivityLog runner, Locale locale, String folderPath, String fileName, ByteArrayInputStream fileStream, boolean override) {
		File folder = new File(folderPath);
		try {
			if (!folder.exists()) {
				logger.info("No folder at " + folderPath + ". Creating it.");
				if (!folder.mkdirs()) {
					throw new Exception(Labels.getLabel(locale, Labels.UnableToCreateFolder));
				}
			}
		} catch (Exception e) {
			runner.addError(Labels.getLabel(locale, Labels.UnableToAccessFolder));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		try {
			logger.info("Saving file...");

			if (override) {
				Files.copy(fileStream, Paths.get(folder.getAbsolutePath() + "/" + fileName), StandardCopyOption.REPLACE_EXISTING);
			}
			else {
				Files.copy(fileStream, Paths.get(folder.getAbsolutePath() + "/" + fileName));
			}

			logger.info("File saved.");
		} catch (FileAlreadyExistsException e) {
			runner.addError(Labels.getLabel(locale, Labels.SavingFileImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + Labels.getLabel(locale, Labels.FileAlreadyExist));

			runner.setResult(Result.ERROR);

			return;
		} catch (Exception e) {
			runner.addError(Labels.getLabel(locale, Labels.SavingFileImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		runner.setResult(Result.SUCCESS);
	}
}
