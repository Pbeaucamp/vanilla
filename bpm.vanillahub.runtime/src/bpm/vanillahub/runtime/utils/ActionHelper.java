package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;

import com.jcraft.jsch.ChannelSftp.LsEntry;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.activities.ActionActivity.TypeAction;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.ActivityRunner;
import bpm.vanillahub.runtime.run.ResultActivity;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.Cible.TypeCible;

public class ActionHelper {

	public static void move(ActivityRunner<?> runner, Locale locale, Source source, Object item, VariableString targetFolder, List<Parameter> parameters, List<Variable> variables) throws Exception {
		String targetFolderPath = targetFolder.getString(parameters, variables);
		if (!targetFolderPath.endsWith("/")) {
			targetFolderPath = targetFolderPath + "/";
		}
		
		if (item instanceof File) {
			String filename = ((File) item).getName();

			File to = new File(targetFolderPath + filename);

			if (!((File) item).renameTo(to)) {
				runner.addWarning(Labels.getLabel(locale, Labels.FailedMoveFiles));
			}
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
				runner.addInfo(Labels.getLabel(locale, Labels.ConnectedToSftp));
			} catch (Exception e) {
				manager.disconnect();

				runner.addError(Labels.getLabel(locale, Labels.ConnectionSftpImpossible));
				runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				runner.setResult(Result.ERROR);
			}
			String fileName = ((LsEntry) item).getFilename();
			
			manager.doAction(locale, runner, TypeAction.MOVE, fileName, folder, targetFolderPath);
		}
	}

	public static void copy(ActivityRunner<?> runner, Locale locale, Source source, String fileName, Object item, VariableString targetFolder, List<Parameter> parameters, List<Variable> variables) throws Exception {
		if (item instanceof ByteArrayInputStream) {
			Cible cible = createCibleFromSource(source, targetFolder);

			ResultActivity result = runner.getResult();
			
			CibleHelper helper = new CibleHelper();
			helper.manageStream(runner, locale, -1, cible, null, result, fileName, (ByteArrayInputStream) item, parameters, variables, null);
		}
		else if (item instanceof File) {
			String targetFolderPath = targetFolder.getString(parameters, variables);
			if (!targetFolderPath.endsWith("/")) {
				targetFolderPath = targetFolderPath + "/";
			}
			
			String filename = ((File) item).getName();

			Path from = Paths.get(((File) item).getPath());
			Path to = Paths.get(targetFolderPath + filename);

			CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES };
			Files.copy(from, to, options);
		}
		else if (item instanceof LsEntry) {
			String targetFolderPath = targetFolder.getString(parameters, variables);
			if (!targetFolderPath.endsWith("/")) {
				targetFolderPath = targetFolderPath + "/";
			}
			
			String host = source.getUrl(parameters, variables);
			int port = source.getPort(parameters, variables);
			String folder = source.getFolderPath(parameters, variables);
			boolean secured = source.isSecured();
			String login = source.getLogin();
			String password = source.getPassword();

			SFTPManager manager = new SFTPManager(host, port, folder, secured, login, password);
			try {
				manager.connect();
				runner.addInfo(Labels.getLabel(locale, Labels.ConnectedToSftp));
			} catch (Exception e) {
				manager.disconnect();

				runner.addError(Labels.getLabel(locale, Labels.ConnectionSftpImpossible));
				runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				runner.setResult(Result.ERROR);
			}
			fileName = ((LsEntry) item).getFilename();
			manager.doAction(locale, runner, TypeAction.COPY, fileName, folder, targetFolderPath);
		}
	}

	private static Cible createCibleFromSource(Source source, VariableString targetFolder) {
		Cible cible = new Cible();
		cible.setType(getTypeCible(source.getType()));
		cible.setUrl(source.getUrlVS());
		cible.setPort(source.getPortVS());
		cible.setSecured(source.isSecured());
		cible.setLogin(source.getLogin());
		cible.setPassword(source.getPassword());
		cible.setFolder(targetFolder);
		cible.setNetworkFolder(source.isNetworkFolder());
		cible.setOverride(true);
		return cible;
	}

	private static TypeCible getTypeCible(TypeSource type) {
		switch (type) {
		case D4C:
			return TypeCible.D4C;
		case FOLDER:
			return TypeCible.FOLDER;
		case FTP:
			return TypeCible.FTP;
		case SFTP:
			return TypeCible.SFTP;
		case WEB_SERVICE:
		case MAIL:
			return null;
		default:
			break;
		}
		return null;
	}
}
