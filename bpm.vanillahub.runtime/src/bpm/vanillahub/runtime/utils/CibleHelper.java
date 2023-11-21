package bpm.vanillahub.runtime.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.core.utils.D4CHelper;
import bpm.vanilla.platform.core.utils.D4CHelper.D4CResult;
import bpm.vanilla.platform.core.utils.D4CHelper.Status;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.CkanResourceResultInformation;
import bpm.vanillahub.runtime.run.IResultInformation;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.vanillahub.runtime.run.IRunner;
import bpm.vanillahub.runtime.run.MetaLinksResultInformation;
import bpm.vanillahub.runtime.run.ResultActivity;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.attributes.FTPAction;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class CibleHelper {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private Logger logger = Logger.getLogger(this.getClass());

	public CibleHelper() {

	}

	public void manageStream(IRunner runner, Locale locale, int workflowId, Cible cible, String targetItem, ResultActivity result, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables, HashMap<TypeResultInformation, IResultInformation> infosComp) throws Exception {
		String host = cible.getUrl(parameters, variables);

		switch (cible.getType()) {
		case FTP:
			sendFTPFile(runner, locale, cible, host, result, fileName, fileStream, parameters, variables);
			break;
		case HTTP:
			sendHTTPFile(runner, locale, cible, host, result, fileName, fileStream, parameters, variables);
			break;
		case FOLDER:
			sendFolder(runner, locale, cible, result, fileName, fileStream, parameters, variables);
			break;
		case CKAN:
			sendCkan(runner, locale, cible, host, result, fileName, fileStream, parameters, variables);
			break;
		case D4C:
			sendD4C(runner, locale, workflowId, cible, targetItem, host, fileName, infosComp, fileStream, parameters, variables);
			break;
		case SFTP:
			sendSFTPFile(runner, locale, cible, host, fileName, fileStream, parameters, variables);
			break;

		default:
			break;
		}
	}

	private void sendFTPFile(IRunner runner, Locale locale, Cible cible, String host, ResultActivity result, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables) throws Exception {
		int port = cible.getPort(parameters, variables);
		String folder = cible.getFolder(parameters, variables);
		boolean secured = cible.isSecured();
		String login = cible.getLogin();
		String password = cible.getPassword();

		if (result.isBigFile()) {
			throw new Exception("This activity doesn't support files > 500MB");
		}

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

	private void sendHTTPFile(IRunner runner, Locale locale, Cible cible, String uploadUrl, ResultActivity result, String fileName, ByteArrayInputStream inputStream, List<Parameter> parameters, List<Variable> variables) throws Exception {
		if (result.isBigFile()) {
			throw new Exception("This activity doesn't support files > 200MB");
		}

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

	private void sendFolder(IRunner runner, Locale locale, Cible cible, ResultActivity result, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables) throws Exception {
		if (result.isBigFile()) {
			throw new Exception("This activity doesn't support files > 200MB");
		}

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

	private void sendFolderNetwork(IRunner runner, Locale locale, String folderPath, String login, String password, String fileName, ByteArrayInputStream fileStream, boolean override) {
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

	private void sendFolderLocal(IRunner runner, Locale locale, String folderPath, String fileName, ByteArrayInputStream fileStream, boolean override) {
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

	private void sendCkan(IRunner runner, Locale locale, Cible cible, String uploadUrl, ResultActivity result, String fileName, ByteArrayInputStream inputStream, List<Parameter> parameters, List<Variable> variables) {
		try {
			String org = cible.getOrg();
			String apiKey = cible.getApiKey();

			CkanPackage pack = cible.getCkanPackage();

			CkanHelper helper = new CkanHelper(uploadUrl, org, apiKey);

			String format = helper.extractFormat(pack.getSelectedResource(), fileName);
			if (result.isBigFile()) {
				int n = result.getInputStream().available();
				byte[] bytes = new byte[n];
				result.getInputStream().read(bytes, 0, n);

				String f = new String(bytes, StandardCharsets.UTF_8);
				helper.uploadCkanFile(fileName, pack.getSelectedResource().getName(), format, pack, f);
			}
			else {
				helper.uploadCkanFile(fileName, pack.getSelectedResource().getName(), format, pack, result.getInputStream());
			}

			runner.addInfo("File sent to D4C.");
		} catch (Exception e) {
			e.printStackTrace();

			runner.addError(Labels.getLabel(locale, Labels.SendingFileCKANImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		runner.setResult(Result.SUCCESS);
	}

	private void sendD4C(IRunner runner, Locale locale, int workflowId, Cible cible, String datasetName, String uploadUrl, String fileName, HashMap<TypeResultInformation, IResultInformation> infosComp, ByteArrayInputStream inputStream, List<Parameter> parameters, List<Variable> variables) {
		try {
			String login = cible.getLogin();
			String password = cible.getPassword();
			String org = cible.getOrg();
			
			MetaLinksResultInformation metaLinksInfo = infosComp != null && infosComp.get(TypeResultInformation.META_LINKS) != null ? (MetaLinksResultInformation) infosComp.get(TypeResultInformation.META_LINKS) : null;
			List<MetaLink> links = metaLinksInfo.getLinks();
			
			HashMap<String, String> extras = new HashMap<String, String>();
			extras.put("vanilla-hub-id", String.valueOf(workflowId));
			extras.put("date_modification", sdf.format(new Date()));

			CkanResourceResultInformation ckanResourceInformation = infosComp != null && infosComp.get(TypeResultInformation.CKAN_RESOURCE) != null ? (CkanResourceResultInformation) infosComp.get(TypeResultInformation.CKAN_RESOURCE) : null;
			
			// We check if the file is the main resource
			boolean isMainResource = ckanResourceInformation != null ? ckanResourceInformation.isMainResource() : true;
			boolean isFile = ckanResourceInformation != null ? ckanResourceInformation.isFile() : true;
			String format = ckanResourceInformation != null ? ckanResourceInformation.getFormat() : getFormat(fileName);
			
			D4CHelper helper = new D4CHelper(uploadUrl, org, login, password);
			D4CResult result = helper.uploadFileResourceAndCreatePackageIfNeeded(org, datasetName, cible.getCkanPackage(), fileName, inputStream, links, extras, 
					isMainResource, isFile, format);
			if (result.getStatus() == Status.ERROR) {
				throw new Exception(result.getMessage());
			}
			else {
				runner.addInfo("File sent to D4C.");
				runner.addInfo(result.getMessage());
				
				checkDataValidation(runner, locale, cible, uploadUrl, org, result.getDatasetId(), result.getMessage(), infosComp);
			}
		} catch (Exception e) {
			e.printStackTrace();

			runner.addError(Labels.getLabel(locale, Labels.SendingFileD4CImpossible));
			runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			runner.setResult(Result.ERROR);

			return;
		}

		runner.setResult(Result.SUCCESS);
	}

	private void checkDataValidation(IRunner runner, Locale locale, Cible cible, String d4cUrl, String d4cOrg, String datasetId, String resourceInfos, HashMap<TypeResultInformation, IResultInformation> infosComp) {
		if (cible.isValidateData()) {
			try {
				int contractId = -1;
				
				// Checking if we have an ArchitectInput step before in the process which allows us to get the contract Id to retrieve the schemas to validate against
				MetaLinksResultInformation metaLinks = infosComp.get(TypeResultInformation.META_LINKS) != null && infosComp.get(TypeResultInformation.META_LINKS) instanceof MetaLinksResultInformation ? (MetaLinksResultInformation) infosComp.get(TypeResultInformation.META_LINKS) : null;
				if (metaLinks != null  && metaLinks.getLinks() != null) {
					for (MetaLink link : metaLinks.getLinks()) {
						if (link.getValue() != null && link.getValue().getMetaKey() != null && link.getValue().getMetaKey().equals("vanilla_contract")) {
							contractId = Integer.parseInt(link.getValue().getValue());
						}
					}
				}

				if (contractId > 0) {
					JSONArray infos = new JSONArray(resourceInfos);
					if (infos != null && infos.length() > 0) {
						for (int i=0; i<infos.length(); i++) {
							JSONObject resource = infos.getJSONObject(i);
							
							String resourceId = "";
							if (resource != null && resource.keys() != null) {
								Iterator<?> keys = resource.keys();
								while (keys.hasNext()) {
									resourceId = (String) keys.next();
								}
							}
							
							try {
								runner.addInfo("Checking data for resource with id = " + resourceId + " and contract id = " + contractId);
								
								JSONObject resourceInfo = resource.getJSONObject(resourceId);
								// We need to check if it is CSV to check for datavalidation
								String format = !resourceInfo.isNull("format") ? resourceInfo.getString("format") : null;
								String status = !resourceInfo.isNull("status") ? resourceInfo.getString("status") : null;
								
								if (format != null && format.equalsIgnoreCase("csv") && status != null && status.equalsIgnoreCase("complete")) {
									IVanillaAPI vanillaApi = getRootVanillaApi();
									ValidationDataResult result = vanillaApi.getResourceManager().validateData(d4cUrl, d4cOrg, datasetId, resourceId, contractId, null);
									
									List<String> details = result.getDetails();
									if (details != null) {
										for (String detail : details) {
											runner.addInfo(detail);
										}
									}
								}
							} catch (Exception e) {
								runner.addError(Labels.getLabel(locale, Labels.ImpossibleToCheckData));
								runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				runner.addError(Labels.getLabel(locale, Labels.ImpossibleToCheckData));
				runner.addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());
			}
		}
	}
	
	private IVanillaAPI getRootVanillaApi() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		String vanUrl = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new RemoteVanillaPlatform(vanUrl, login, password);
	}

//	private void manageMetadataToUpdate(CkanPackage newDataset, CkanPackage existingDataset) {
//		Map<String, String> newExtras = newDataset.getExtras();
//		
//		if (existingDataset.getExtras() != null) {
//			if (newExtras == null) {
//				newDataset.setExtras(existingDataset.getExtras());
//			}
//			else {
//				for (String keyExtra : existingDataset.getExtras().keySet()) {
//					String newExtra = newExtras.get(keyExtra);
//					if (newExtra == null) {
//						//If the extra does not exist, we put it back in the updated dataset
//						String value = existingDataset.getExtras().get(keyExtra);
//						newDataset.putExtra(keyExtra, value);
//					}
//				}
//			}
//		}
//	}

	private String getFormat(String newFileName) {
		int index = newFileName.lastIndexOf(".") + 1;
		return newFileName.substring(index);
	}

	private void sendSFTPFile(IRunner runner, Locale locale, Cible cible, String host, String fileName, ByteArrayInputStream fileStream, List<Parameter> parameters, List<Variable> variables) {
		int port = cible.getPort(parameters, variables);
		String folder = cible.getFolder(parameters, variables);
		boolean secured = cible.isSecured();
		String login = cible.getLogin();
		String password = cible.getPassword();

		SFTPManager manager = new SFTPManager(host, port, folder, secured, login, password);
		try {
			manager.connect();
			runner.addInfo(Labels.getLabel(locale, Labels.ConnectedToSftp));
		} catch (Exception e) {
			manager.disconnect();

			runner.addError(Labels.getLabel(locale, Labels.ConnectionSftpImpossible));
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

		manager.disconnect();
		runner.setResult(Result.SUCCESS);
	}
}
