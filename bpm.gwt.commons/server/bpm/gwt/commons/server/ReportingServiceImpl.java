package bpm.gwt.commons.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

import bpm.document.management.core.IVdmManager;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.CmisHelper;
import bpm.gwt.commons.server.helper.CustomXWPFDocument;
import bpm.gwt.commons.server.helper.RepositoryImpl;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.cmis.CmisItem;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailItemReportsGroup;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShareAklabox;
import bpm.gwt.commons.shared.viewer.CommentInformations;
import bpm.gwt.commons.shared.viewer.CommentRestitutionInformations;
import bpm.gwt.commons.shared.viewer.CommentValidationInformations;
import bpm.gwt.commons.shared.viewer.CommentsInformations;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.smart.core.xstream.RemoteWorkflowManager;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.FileInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.KpiMapBean;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.UserRunConfigurationParameter;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition.TypeComment;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.SecurityProperties;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.components.CustomRunIdentifier;
import bpm.vanilla.platform.core.components.FreeDashboardComponent;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.IRunIdentifier;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.RunIdentifier;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig.HistorizationTarget;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.components.system.IMailConfig;
import bpm.vanilla.platform.core.components.system.MailConfig;
import bpm.vanilla.platform.core.config.ConfigurationException;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;

public class ReportingServiceImpl extends RemoteServiceServlet implements ReportingService {

	private static final long serialVersionUID = 8262963920223532020L;

	private Logger logger = Logger.getLogger(this.getClass());
	private CommonConfiguration portalConfig;

	private String location;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ServletContext context = getServletContext();
		location = context.getRealPath("/");

		logger.info("Initing Portal's ReportingService...");
		logger.info("Using location : " + location);
		try {
			portalConfig = CommonConfiguration.getInstance();
			logger = Logger.getLogger(this.getClass());

		} catch (ConfigurationException ex) {
			String errMsg = "Failed to init Portal's ReportingService : " + ex.getMessage();
			logger.fatal(errMsg);
			throw new ServletException(errMsg, ex);
		}
		logger.info("Portal's ReportingService is ready.");
	}

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}

	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}

	@Override
	public String runReport(LaunchReportInformations itemInfo) throws ServiceException {
		CommonSession session = getSession();

		if (itemInfo.getTypeRun() == TypeRun.DISCO) {
			// return runDiscoReport(session, itemInfo);
		}

		if (!itemInfo.getItem().getItem().isOn()) {
			throw new ServiceException("Item '" + itemInfo.getItem().getName() + "' is disabled. Enable with ES if you want to run it.");
		}

		try {
			ObjectInputStream reportsStream = new ObjectInputStream();
			
			String itemName = itemInfo.getItem().getItem().getName();
			itemName = itemName.replace("'","").replace(" ", "_");
		    String nfdNormalizedString = Normalizer.normalize(itemName, Normalizer.Form.NFD); 
		    Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]");
		    String name = pattern.matcher(nfdNormalizedString).replaceAll("");
			
			
//			String name = new Object().hashCode() + "";
			for (String outputFormat : itemInfo.getOutputs()) {
				InputStream is = executeReport(itemInfo, outputFormat, itemInfo.getSelectedGroup().getId(), session);

				if (itemInfo.getTypeRun() != TypeRun.BACKGROUND) {
					byte currentXMLBytes[] = IOUtils.toByteArray(is);
					ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
					reportsStream.addStream(outputFormat, byteArrayIs);
					is.close();
				}
			}

//			if (itemInfo.getTypeRun() == TypeRun.BACKGROUND) {
//				
//				BackgroundItemDTO backgroundItem = session.getBackgroundReport(itemInfo.getItem().getId());
//				if (backgroundItem != null) {
//					session.removeBackgroundReport(itemInfo.getItem().getId());
//				}
//				return null;
//			}

			session.addReport(name, reportsStream);
			return name;
		} catch (ServiceException e) {
			// already logged and everything, just throw again
			throw e;
		} catch (Exception e) {
			String msg = "Failed to run report, reason : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public LaunchReportInformations runReports(LaunchReportInformations itemInfo) throws ServiceException {
		for (LaunchReportInformations report : itemInfo.getReports()) {
			if (report.isToRun()) {
				String reportKey = runReport(report);
				report.setReportKey(reportKey);
				report.setHasBeenRun(true);
				report.setToRun(false);
			}
		}
		return itemInfo;
	}

	// private String runDiscoReport(CommonSession session,
	// LaunchReportInformations itemInfo) throws ServiceException {
	// IVanillaContext ctx = portalConfig.getRootContext();
	// String discoPackageFolder =
	// ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_DISCO_PACKAGE_FOLDER);
	//
	// ObjectInputStream reportsStream = new ObjectInputStream();
	// String name = new Object().hashCode() + "";
	// for (String outputFormat : itemInfo.getOutputs()) {
	//
	// ObjectIdentifier objectId = new ObjectIdentifier(1, -1);
	// ReportRuntimeConfig config = new ReportRuntimeConfig(objectId, null, 1);
	// config.setRuntimeUrl(ctx.getVanillaUrl());
	// config.setOutputFormat(outputFormat);
	//
	// FileInputStream fis;
	// try {
	// fis = new FileInputStream(discoPackageFolder + "/" +
	// itemInfo.getItem().getName());
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// throw new ServiceException("The file '" + discoPackageFolder + "/" +
	// itemInfo.getItem().getName() + "' could not be found.");
	// }
	//
	// User dummy = new User();
	// dummy.setLogin(ctx.getLogin());
	// dummy.setPassword(ctx.getPassword());
	//
	// ReportingComponent remoteReport = new
	// RemoteReportRuntime(portalConfig.getRootContext());
	// InputStream is = null;
	// try {
	// is = remoteReport.runReport(config, fis, dummy, true);
	//
	// byte currentXMLBytes[] = IOUtils.toByteArray(is);
	// ByteArrayInputStream byteArrayIs = new
	// ByteArrayInputStream(currentXMLBytes);
	// reportsStream.addReport(outputFormat, byteArrayIs);
	//
	// is.close();
	// fis.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new ServiceException("The file '" + discoPackageFolder + "/" +
	// itemInfo.getItem().getName() + "' could not be run.", e);
	// }
	//
	// session.addReport(name, reportsStream);
	// }
	//
	// return name;
	// }

	private InputStream executeReport(LaunchReportInformations itemInfo, String outputFormat, int groupId, CommonSession session) throws ServiceException {
		// find valid locale
		Locale locale = null;
		if (itemInfo.getLocale() != null && !itemInfo.getLocale().isEmpty()) {
			for (Locale loc : Locale.getAvailableLocales()) {
				if (loc.toString().equals(itemInfo.getLocale())) {
					logger.info("Using specified Locale = " + loc.toString());
					locale = loc;
					break;
				}
			}
		}

		if (locale == null) {
			logger.warn("No valid locale specified, will use the default Locale.");
		}

		AlternateDataSourceConfiguration alternateConfiguration = itemInfo.getAlternateDataSourceConfig();

		try {
			// ReportingComponent reportingComponent =
			// session.getReportingComponent();
			int repositoryId = session.getCurrentRepository().getId();
			ReportingComponent reportingComponent = session.getReportingComponent();

			ObjectIdentifier ident = new ObjectIdentifier(repositoryId, itemInfo.getItem().getId());

			ReportRuntimeConfig config = new ReportRuntimeConfig();

			config.setObjectIdentifier(ident);
			config.setVanillaGroupId(groupId);
			if (locale != null) {
				config.setLocale(locale);
			}
			config.setAlternateDataSourceConfiguration(alternateConfiguration);

			config.setParameters(itemInfo.getGroupParameters());
			if (outputFormat.equalsIgnoreCase("pht")) {
				config.setOutputFormat("ppt");
			}
			else {
				config.setOutputFormat(outputFormat);
			}
			config.setMaxRowsPerQuery(itemInfo.getLimitRows());
			config.setDisplayComments(itemInfo.displayComments());

			if (itemInfo.getTypeRun() == TypeRun.BACKGROUND) {
				logger.info("Running report in background...");
				RunIdentifier runId = reportingComponent.runReportAsynch(config, session.getUser());
				
				int userId = session.getUser().getId();
				
				ReportBackground report = new ReportBackground();
				report.setItemId(itemInfo.getItem().getId());
				report.setName(itemInfo.getItem().getName());
				report.setOutputFormat(outputFormat);
				report.setCreationDate(new Date());
				report.setTaskKey(runId.getKey());
				report.setTaskId(runId.getTaskId());
				report.setRunning(true);
				report.setUserId(userId);
				report.setGroupId(groupId);

				session.getRepositoryConnection().getReportHistoricService().addOrUpdateReportBackground(report);

				return null;
			}
			else {
				logger.info("Running report...");
				InputStream is = reportingComponent.runReport(config, session.getUser());
				return is;
			}
		} catch (Exception e) {
			String msg = "Failed to run report, reason : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	/**
	 * 
	 * We ll be replacing : {TARGET_USERNAME} {TARGET_GROUP} {CURRENT_USERNAME} {REPORT_NAME}
	 * 
	 * @throws ServiceException
	 */
	@Override
	public String burstReport(LaunchReportInformations itemInfo, List<Group> groups, String htmlText, boolean sendMail, boolean addToGed, String gedName) throws ServiceException {

		if (!itemInfo.getItem().getItem().isOn()) {
			throw new ServiceException("Item '" + itemInfo.getItem().getName() + "' is disabled. Enable thru ES if you want to run it.");
		}

		itemInfo.setLimitRows(-1);
		itemInfo.setDisplayComments(true);

		logger.info("Bursting report : " + itemInfo.getItem().getName() + " for " + groups.size() + " group(s).");

		CommonSession session = getSession();
		String currentUserName = session.getUser().getName();

		boolean hasError = false;
		String errorString = "";

		boolean hasWarning = false;
		String warningString = "";

		int sentEmail = 0;

		List<Integer> groupIds = new ArrayList<Integer>();
		for (Group group : groups) {
			groupIds.add(group.getId());
		}

		for (Group group : groups) {

			for (String outputFormat : itemInfo.getOutputs()) {
				logger.info("Burst: Generating report : " + itemInfo.getItem().getName() + " for group : " + group.getName() + " and format : " + outputFormat);
				try {
					ObjectInputStream reportsStream = new ObjectInputStream();
					String name = new Object().hashCode() + "";
					InputStream is = executeReport(itemInfo, outputFormat, group.getId(), session);

					byte currentXMLBytes[] = IOUtils.toByteArray(is);
					ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
					reportsStream.addStream(outputFormat, byteArrayIs);
					is.close();

					session.addReport(name, reportsStream);

					if (addToGed) {
						historize(false, null, name, groupIds, gedName + "_" + group.getName(), outputFormat, false, itemInfo.getItem(), null);

						// for (Integer grId : groupIds) {
						// if (grId == group.getId()) {
						// logger.info("Report has historization/ged set, sending to ged...");
						// byteArrayIs.reset();
						// List<Integer> tmp = new ArrayList<Integer>();
						// tmp.add(grId);
						// prepareGedConfig(session, itemInfo.getItem().getId(),
						// outputFormat, gedName + "_" + group.getName(),
						// byteArrayIs, tmp);
						// logger.info("Ged is finished");
						// }
						// }
					}
					else {
						logger.info("Report had no historization/ged set, skipping sent to ged");
					}

					logger.info("Burst: Report generated, preparing email...");

					if (sendMail) {
						// for each user, send a email now
						List<User> users = portalConfig.getRootVanillaApi().getVanillaSecurityManager().getUsersForGroup(group);
						for (User user : users) {
							if (user.getBusinessMail() != null || !user.getBusinessMail().isEmpty()) {
								logger.info("Burst : Preparing email for " + user.getName());

								String attachementName = itemInfo.getItem().getName() + "_" + group.getName() + "." + outputFormat;

								byteArrayIs.reset();
								HashMap<String, InputStream> attachement = new HashMap<String, InputStream>();
								attachement.put(attachementName, byteArrayIs);

								// text replacements for email string
								htmlText = htmlText.replace("{TARGET_USERNAME}", user.getName());
								htmlText = htmlText.replace("{TARGET_GROUP}", group.getName());
								htmlText = htmlText.replace("{CURRENT_USERNAME}", currentUserName);
								htmlText = htmlText.replace("{REPORT_NAME}", itemInfo.getItem().getName());

								IMailConfig config = new MailConfig(user.getBusinessMail(), currentUserName, htmlText, "VanillaBurst : " + itemInfo.getItem().getName(), true);

								logger.info("Burst : Sending email...");
								String res = session.getSystemManager().sendEmail(config, attachement);
								logger.info("Burst : Mailing system says : " + res);

								sentEmail++;
							}
							else {
								String warning = "Burst: Ignoring user '" + user.getName() + "' since he doesnt have a business email registered.";
								logger.warn(warning);
								warningString += warning + "\n";
							}
						}
					}

				} catch (ServiceException e) {
					hasError = true;
					logger.error(e.getMessage(), e);
					errorString += "Error " + e.getMessage() + "\n";
				} catch (Exception e) {
					hasError = true;
					logger.error(e.getMessage(), e);
					errorString += "Error " + e.getMessage() + "\n";
				}
			}
		}
		String result = "Burst Result:\n";
		if (sendMail) {
			result += "Sent " + sentEmail + " email(s)\n";
		}

		if (hasWarning) {
			result += "Warnings :\n";
			result += warningString;
		}
		else {
			result += "No warnings\n";
		}

		if (hasError) {
			result += "Errors :\n";
			result += errorString;
		}
		else {
			result += "No errors\n";
		}

		return result;
	}

	@Override
	public void saveReportParameterConfig(List<VanillaGroupParameter> groupParams, PortailRepositoryItem item, String configName, String configDesc) throws ServiceException {
		CommonSession session = getSession();

		try {
			logger.info("Saving parameters configuration...");
			int idUser = session.getUser().getId();
			int idRepository = session.getCurrentRepository().getId();
			int idItem = item.getId();

			if (groupParams != null) {
				List<UserRunConfigurationParameter> parameters = new ArrayList<UserRunConfigurationParameter>();
				for (VanillaGroupParameter groupParam : groupParams) {
					if (parameters != null) {
						for (VanillaParameter parameter : groupParam.getParameters()) {
							String paramName = parameter.getName();
							List<String> values = parameter.getSelectedValues();
							parameters.add(new UserRunConfigurationParameter(paramName, values));
						}
					}

				}

				if (!parameters.isEmpty()) {
					UserRunConfiguration config = new UserRunConfiguration(configName, idUser, idRepository, idItem, parameters);
					config.setDescription(configDesc);

					session.getVanillaApi().getVanillaPreferencesManager().addUserRunConfiguration(config);
					logger.info("Parameters configuration are saved");
				}
				else {
					logger.info("There isn't any parameters to save.");
				}
			}
			else {
				logger.info("There isn't any parameters to save.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save the parameter configuration: " + e.getMessage());
		}
	}

	@Override
	public List<VanillaGroupParameter> getParameters(LaunchReportInformations itemInfo) throws ServiceException {
		CommonSession session = getSession();

		ObjectIdentifier ident = new ObjectIdentifier(session.getCurrentRepository().getId(), itemInfo.getItem().getId());
		IRuntimeConfig config;
		if (itemInfo.getItem().isReport()) {
			config = new ReportRuntimeConfig(ident, null, itemInfo.getSelectedGroup().getId());
		}
		else {
			config = new RuntimeConfiguration(itemInfo.getSelectedGroup().getId(), ident, null);
		}
		return getParameters(session, config, itemInfo.getItem().getId());
	}

	private List<VanillaGroupParameter> getParameters(CommonSession session, IRuntimeConfig config, int itemId) throws ServiceException {
		try {
			VanillaParameterComponent paramComponent = session.getParameterComponent();

			List<VanillaGroupParameter> groupParams = paramComponent.getParameters(config);
			logger.info("Found " + groupParams.size() + " group parameters for item with id " + itemId);
			return groupParams;
		} catch (Exception e) {
			String msg = "Failed to get parameters, reason : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	private List<VanillaGroupParameter> getParametersForDisco(CommonSession session, IRuntimeConfig config, PortailRepositoryItem item) throws ServiceException {
		try {
			String discoPackageFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_DISCO_PACKAGE_FOLDER);
			VanillaParameterComponent paramComponent = new RemoteVanillaParameterComponent(portalConfig.getRootContext());

			FileInputStream fis;
			try {
				fis = new FileInputStream(discoPackageFolder + "/" + item.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new ServiceException("The file '" + discoPackageFolder + "/" + item.getName() + "' could not be found.");
			}

			List<VanillaGroupParameter> groupParams = paramComponent.getParameters(config, fis);
			logger.info("Found " + groupParams.size() + " group parameters for item.");
			return groupParams;
		} catch (Exception e) {
			String msg = "Failed to get parameters, reason : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public VanillaParameter getParameterValues(LaunchReportInformations itemInfo, List<VanillaGroupParameter> currentParams, String parameterName) throws ServiceException {

		CommonSession session = getSession();

		ObjectIdentifier ident = new ObjectIdentifier(session.getCurrentRepository().getId(), itemInfo.getItem().getId());

		IRuntimeConfig config;

		if (currentParams == null) {
			throw new ServiceException("Existing params is null");
		}

		if (itemInfo.getItem().isReport()) {
			config = new ReportRuntimeConfig(ident, currentParams, itemInfo.getSelectedGroup().getId());
		}
		else {
			config = new RuntimeConfiguration(itemInfo.getSelectedGroup().getId(), ident, currentParams);
		}

		try {
			VanillaParameterComponent paramComponent = session.getParameterComponent();

			VanillaParameter updatedParam = paramComponent.getReportParameterValues(config, parameterName);
			return updatedParam;
		} catch (Exception e) {
			String msg = "Failed to get report's parameters, reason : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public LaunchReportInformations getLaunchReportInformations(PortailRepositoryItem item, Group selectedGroup) throws ServiceException {
		CommonSession session = getSession();

		IRuntimeConfig config = null;
		AlternateDataSourceHolder alternate = null;

		List<String> availableLocales = new ArrayList<String>();
		List<UserRunConfiguration> configs = null;

		List<VanillaGroupParameter> groupParams = new ArrayList<VanillaGroupParameter>();
		List<LaunchReportInformations> launchReports = new ArrayList<LaunchReportInformations>();

		if (item instanceof PortailItemReportsGroup) {
			List<PortailRepositoryItem> reports = ((PortailItemReportsGroup) item).getReports();
			if (reports != null) {
				for (PortailRepositoryItem report : reports) {
					try {
						LaunchReportInformations itemInfo = getLaunchReportInformations(report, selectedGroup);
						if (itemInfo != null) {
							launchReports.add(itemInfo);
						}
						else {
							logger.error("Unable to find runtime informations for item " + report.getName());
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Unable to find runtime informations for item " + report.getName());
					}
				}
			}
		}
		else {
			ObjectIdentifier ident = new ObjectIdentifier(session.getCurrentRepository().getId(), item.getId());
			if (item.isReport()) {
				config = new ReportRuntimeConfig(ident, null, selectedGroup.getId());

				alternate = fetchReportAlternateDatasource(session, item.getItem(), (ReportRuntimeConfig) config);
				availableLocales = getReportAvailableLocales(session, item.getItem(), selectedGroup);
			}
			else if (item.getType() == IRepositoryApi.GTW_TYPE || item.getType() == IRepositoryApi.BIW_TYPE || item.getType() == IRepositoryApi.R_MARKDOWN_TYPE) {
				config = new RuntimeConfiguration(selectedGroup.getId(), ident, null);
			}

			if (config != null) {
				groupParams = getParameters(session, config, item.getId());

				try {
					configs = session.getVanillaApi().getVanillaPreferencesManager().getUserRunConfigurationsByUserIdObjectId(session.getUser().getId(), ident);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServiceException(e.getMessage());
				}
			}
		}

		List<Integer> groupIds;
		try {
			groupIds = session.getRepositoryConnection().getAdminService().getAllowedGroupId(item.getItem());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the allowed group for item with id = " + item.getItem().getId() + ": " + e.getMessage());
		}

		List<Integer> availableGroupIds = new ArrayList<Integer>();
		try {
			if (groupIds != null) {
				for (Integer groupId : groupIds) {
					if (session.getRepositoryConnection().getAdminService().canRun(item.getItem().getId(), groupId)) {
						availableGroupIds.add(groupId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the allowed group for item with id = " + item.getItem().getId() + ": " + e.getMessage());
		}

		List<Group> userGroups;
		try {
			userGroups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the available groups for the user " + session.getUser().getLogin());
		}

		List<Group> availableGroups = new ArrayList<Group>();
		for (Group gr : userGroups) {
			for (Integer grId : availableGroupIds) {
				if (gr.getId().equals(grId)) {
					availableGroups.add(gr);
					break;
				}
			}
		}

		Validation validation;
		try {
			validation = session.getRepositoryConnection().getRepositoryService().getValidation(item.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the available validation for the item " + item.getName());
		}

		CommentsInformations commentsInformations = getRestitutionComments(session, item.getItem(), validation, false);

		List<ItemMetadataTableLink> links;
		try {
			links = session.getRepositoryConnection().getRepositoryService().getMetadataLinks(item.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the metadata links for the item " + item.getName());
		}

		LaunchReportInformations launchReport = new LaunchReportInformations(item, selectedGroup, groupParams, configs, commentsInformations, validation, availableLocales, alternate, availableGroups, links);
		launchReport.setReports(launchReports);
		return launchReport;
	}

	private AlternateDataSourceHolder fetchReportAlternateDatasource(CommonSession session, RepositoryItem item, ReportRuntimeConfig config) throws ServiceException {
		logger.info("Fetching AlternateDatasource for item " + item.getName());
		try {
			ReportingComponent reportingComponent = session.getReportingComponent();
			return reportingComponent.getAlternateDataSourcesConnections(config, session.getUser());
		} catch (Exception e) {
			String errMsg = "Failed to get AlternateDataSources for item " + item.getName() + ",reason : " + e.getMessage();
			logger.error(errMsg, e);
			throw new ServiceException(errMsg, e);
		}
	}

	private List<String> getReportAvailableLocales(CommonSession session, RepositoryItem item, Group selectedGroup) throws ServiceException {
		List<String> availableLocales = new ArrayList<String>();

		logger.info("Getting Resources for itemId " + item.getName());
		try {
			List<LinkedDocument> docs = session.getRepositoryConnection().getRepositoryService().getLinkedDocumentsForGroup(item.getId(), selectedGroup.getId());

			logger.info("Found " + docs.size() + " linked doc(s)");

			for (LinkedDocument doc : docs) {
				String[] tokens = doc.getName().split("_");
				if (tokens.length < 2 || !doc.getName().contains(".properties")) {
					logger.info("Ignoring linkeddoc : " + doc.getName());
					continue;
				}
				String locale = tokens[tokens.length - 2] + "_" + tokens[tokens.length - 1].replace(".properties", "");
				availableLocales.add(locale);
			}
			logger.info("Found " + availableLocales.size() + " supported locales.");
		} catch (Exception e) {
			String errMsg = "Failed to retrieve LinkedDocuments for item " + item.getId() + ",reason : " + e.getMessage();
			logger.error(errMsg, e);
			throw new ServiceException(errMsg, e);
		}

		return availableLocales;
	}

	@Override
	public LaunchReportInformations getLaunchReportInformationsForDisco(PortailRepositoryItem item) throws ServiceException {
		CommonSession session = getSession();

		IRuntimeConfig config = null;

		ObjectIdentifier ident = new ObjectIdentifier(1, -1);
		if (item.isReport()) {
			config = new ReportRuntimeConfig(ident, null, 1);
		}

		List<VanillaGroupParameter> groupParams = new ArrayList<VanillaGroupParameter>();
		if (config != null) {
			groupParams = getParametersForDisco(session, config, item);
		}

		LaunchReportInformations itemInfo = new LaunchReportInformations();
		itemInfo.setItem(item);
		itemInfo.setGroupParameters(groupParams);
		itemInfo.setTypeRun(TypeRun.DISCO);
		return itemInfo;
	}

	@Override
	public String runDashboard(LaunchReportInformations itemInfo) throws ServiceException {
		logger.info("Running dashboard named : " + itemInfo.getItem().getName());

		CommonSession session = getSession();

		try {
			FreeDashboardComponent dashboardComponent = session.getDashboardComponent();

			IObjectIdentifier ident = new ObjectIdentifier(session.getCurrentRepository().getId(), itemInfo.getItem().getId());

			IRuntimeConfig config = new RuntimeConfiguration(itemInfo.getSelectedGroup().getId(), ident, null);

			// RemoteFdRuntime
			logger.info("Deploying dashboard...");
			String dash = dashboardComponent.deployDashboard(config);

			logger.info("Redirecting user to " + dash);

			return dash;

		} catch (Exception e) {
			String msg = "Failed to run dashboard : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	// public String runDashboardForDisco(LaunchReportInformations itemInfo)
	// throws ServiceException {
	// logger.info("Running dashboard named : " + itemInfo.getItem().getName());
	//
	// CommonSession session = getSession();
	//
	// try {
	// FreeDashboardComponent dashboardComponent = new
	// RemoteFdRuntime(getVanillaRuntimeUrl(), getUser().getLogin(),
	// getUser().getPassword());
	//
	// // RemoteFdRuntime
	// logger.info("Deploying dashboard...");
	// String dash = dashboardComponent.deployDashboard(config);
	// logger.info("Redirecting user to " + dash);
	//
	// return dash;
	//
	// } catch (Exception e) {
	// String msg = "Failed to run dashboard : " + e.getMessage();
	// logger.error(msg, e);
	// throw new ServiceException(msg);
	// }
	// }

	@Override
	public String exportDashboard(String uuid, String url, String format, List<String> selectedFolders, boolean isLandscape) throws ServiceException {
		CommonSession session = getSession();

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String delay = config.getProperty(VanillaConfiguration.P_DASHBOARD_EXPORT_DELAY);

		if (format.equalsIgnoreCase("pdf")) {
			boolean onLinux = config.getProperty(VanillaConfiguration.P_DASHBOARD_EXPORT_ON_LINUX) != null ? Boolean.parseBoolean(config.getProperty(VanillaConfiguration.P_DASHBOARD_EXPORT_ON_LINUX)) : false;

			String wkhtmltopdfPath = config.getProperty(VanillaConfiguration.P_DASHBOARD_EXPORT_WKHTMLTOPDF_PATH);

			if (wkhtmltopdfPath == null || wkhtmltopdfPath.isEmpty()) {
				throw new ServiceException("The path to wkhtmltopdf in vanilla.properties is not valid. Please configure it properly.");
			}

			// String vanillaFilePath =
			// config.getProperty(VanillaConfiguration.P_VANILLA_FILES);
			//
			// if (vanillaFilePath == null || vanillaFilePath.isEmpty()) {
			// throw new
			// ServiceException("The path to Vanilla Files in vanilla.properties is not valid. Please configure it properly.");
			// }
			//
			// String filePath = vanillaFilePath.endsWith("/") ? vanillaFilePath

			url = url.replace(" ", "%20");
			
			String htmlPages = "";
			try {
				for (String folder : selectedFolders) {
					if (onLinux) {
						htmlPages += " \"" + url + "&exportdashboard=true&folder=" + URLEncoder.encode(folder, "UTF-8") + "";
					}
					else {
						htmlPages += " \"" + url + "&exportdashboard=true&folder=" + URLEncoder.encode(folder, "UTF-8") + "\"";
					}
				}

				if (htmlPages.isEmpty()) {
					logger.info("No folder selected, put the current folder.");
					FreeDashboardComponent comp = session.getDashboardComponent();
					String folder;
					try {
						folder = comp.getActualFolderPage(uuid);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("Unable to get the actual folder selected : " + e.getMessage());
					}

					htmlPages = " \"" + url + "&exportdashboard=true&folder=" + URLEncoder.encode(folder, "UTF-8") + "\"";
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new ServiceException("Unable to create a PDF document for this dashboard : " + e1.getMessage());
			}

			Runtime rt = Runtime.getRuntime();
			Process process;
			try {

				String landscape = "";
				if (isLandscape) {
					landscape = " -O landscape";
				}

				String command = "";
				if (onLinux) {
					command = "./" + wkhtmltopdfPath + landscape + " --javascript-delay " + delay + " " + htmlPages + " -";
				}
				else {
					command = wkhtmltopdfPath + landscape + " --javascript-delay " + delay + " " + htmlPages + " -";
				}

				System.out.println(command);

				process = rt.exec(command);

				InputStream inputStream = process.getInputStream();

				ObjectInputStream reportsStream = new ObjectInputStream();
				String name = new Object().hashCode() + "";

				byte currentXMLBytes[] = IOUtils.toByteArray(inputStream);

				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
				reportsStream.addStream(format, byteArrayIs);
				inputStream.close();

				BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String lineErr = errStreamReader.readLine();
				while (lineErr != null) {
					logger.info(lineErr);
					lineErr = errStreamReader.readLine();
				}

				session.addReport(name, reportsStream);
				return name;
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new ServiceException("Unable to create a PDF document for this dashboard : " + e1.getMessage());
			}
		}
		else if (format.equalsIgnoreCase("docx")) {
			String wkhtmltoimagePath = config.getProperty(VanillaConfiguration.P_DASHBOARD_EXPORT_WKHTMLTOIMAGE_PATH);

			if (wkhtmltoimagePath == null || wkhtmltoimagePath.isEmpty()) {
				throw new ServiceException("The path to wkhtmltopdf in vanilla.properties is not valid. Please configure it properly.");
			}

			String vanillaFilePath = config.getProperty(VanillaConfiguration.P_VANILLA_FILES);

			if (vanillaFilePath == null || vanillaFilePath.isEmpty()) {
				throw new ServiceException("The path to Vanilla Files in vanilla.properties is not valid. Please configure it properly.");
			}

			String imgPath = vanillaFilePath.endsWith("/") ? vanillaFilePath + "temp/" + new Object().hashCode() + ".png" : vanillaFilePath + "/temp/" + new Object().hashCode() + ".png";

			logger.info("No folder selected, put the current folder.");
			FreeDashboardComponent comp = session.getDashboardComponent();
			String folder;
			try {
				folder = comp.getActualFolderPage(uuid);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new ServiceException("Unable to get the actual folder selected : " + e1.getMessage());
			}

			String htmlPages = "\"" + url + "&exportdashboard=true&folder=" + folder + "\"";

			String command = wkhtmltoimagePath + " --format bmp --javascript-delay " + delay + " " + htmlPages + " " + imgPath;

			Runtime rt = Runtime.getRuntime();
			Process process;
			try {
				process = rt.exec(command);

				BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String lineErr = errStreamReader.readLine();
				while (lineErr != null) {
					logger.info(lineErr);
					lineErr = errStreamReader.readLine();
				}

				File img = new File(imgPath);

				// Create a document file
				CustomXWPFDocument document = new CustomXWPFDocument();

				FileInputStream fileIs = new FileInputStream(img);

				XWPFParagraph paragraphX = document.createParagraph();
				paragraphX.setAlignment(ParagraphAlignment.CENTER);
				String blipId = paragraphX.getDocument().addPictureData(fileIs, org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG);
				document.createPicture(blipId, document.getNextPicNameNumber(org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG), 600, 600);

				ByteArrayOutputStream os = new ByteArrayOutputStream();

				document.write(os);

				ObjectInputStream reportsStream = new ObjectInputStream();
				String name = new Object().hashCode() + "";

				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
				reportsStream.addStream(format, byteArrayIs);
				os.close();

				fileIs.close();
				img.delete();

				session.addReport(name, reportsStream);
				return name;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to create a WORD document for this dashboard : " + e.getMessage());
			}
		}

		return null;
	}

	@Override
	public String runWorkflow(LaunchReportInformations itemInfo) throws ServiceException {
		CommonSession session = getSession();
		WorkflowService workflowComponent = session.getWorkflowComponent();

		logger.info("Running workflow named " + itemInfo.getItem().getName());

		try {

			ObjectIdentifier ident = new ObjectIdentifier(session.getCurrentRepository().getId(), itemInfo.getItem().getId());

			IRuntimeConfig config = new RuntimeConfiguration(itemInfo.getSelectedGroup().getId(), ident, itemInfo.getGroupParameters());
			IRunIdentifier runId = workflowComponent.startWorkflowAsync(config);

			String baseUrl = session.getVanillaRuntimeExternalUrl();
			if (baseUrl.endsWith("/")) {
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
			}

			String extraUrl = WorkflowService.WORKFLOW_JOB_MONITOR_SERVLET + "?" + WorkflowService.P_WORKFLOW_RUN_IDENTIFIER + "=" + runId.getKey() + "&" + WorkflowService.P_WORKFLOW_MONITOR_INCLUDE_TITLE + "=" + false;

			logger.info("Redirecting user to workflow execution at url : " + baseUrl + extraUrl);

			return baseUrl + extraUrl;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Failed to run workflow : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public String runGateway(LaunchReportInformations itemInfo) throws ServiceException {

		CommonSession session = getSession();
		GatewayComponent gtwComponent = session.getGatewayComponent();

		logger.info("Running gateway named " + itemInfo.getItem().getName());

		try {
			ObjectIdentifier ident = new ObjectIdentifier(session.getCurrentRepository().getId(), itemInfo.getItem().getId());
			IGatewayRuntimeConfig runtime = new GatewayRuntimeConfiguration(ident, itemInfo.getGroupParameters(), itemInfo.getSelectedGroup().getId());

			IRunIdentifier runId = gtwComponent.runGatewayAsynch(runtime, session.getUser());

			String baseUrl = session.getVanillaRuntimeExternalUrl();

			if (baseUrl.endsWith("/")) {
				baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
			}

			String extraUrl = GatewayComponent.GATEWAY_JOB_MONITOR_SERVLET + "?" + GatewayComponent.P_GATEWAY_RUN_IDENTIFIER + "=" + runId.getKey() + "&" + GatewayComponent.P_GATEWAY_MONITOR_INCLUDE_TITLE + "=" + false;

			logger.info("Redirecting user to gateway at url : " + baseUrl + extraUrl);

			return baseUrl + extraUrl;

		} catch (Exception e) {
			String msg = "error while running gateway : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public String getForwardUrlForCubes(int fasdDirItemId, String cubeName, String viewName) throws ServiceException {

		CommonSession session = getSession();

		String faBaseUrl = portalConfig.getFawebUrl();
		logger.info("Using FAWeb base url " + faBaseUrl);

		String url = "";

		try {
			url += faBaseUrl;
			String sessionId = session.getVanillaSessionId();
			if (url.contains("?")) {
				url += "&bpm.vanilla.sessionId=" + sessionId;
			}
			else {
				url += "?bpm.vanilla.sessionId=" + sessionId;
			}
			url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
			url += "&bpm.vanilla.repositoryId=" + session.getCurrentRepository().getId();
			url += "&bpm.vanilla.fasd.id=" + fasdDirItemId;
			if (cubeName != null) {
				url += "&bpm.vanilla.cubename=" + URLEncoder.encode(cubeName, "UTF-8");
				if (viewName != null) {
					url += "&bpm.vanilla.viewname=" + URLEncoder.encode(viewName, "UTF-8");
				}
			}

			logger.info("forwarding to secured url : " + url);

			return url;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			logger.info(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public DisplayItem getDocumentUrl(int itemId) throws ServiceException {
		CommonSession session = getSession();

		try {
			RepositoryItem item = session.getRepository().getItem(itemId);

			boolean isCommentable = session.getRepositoryConnection().getDocumentationService().canComment(session.getCurrentGroup().getId(), item.getId(), Comment.ITEM);

			// TODO: DO it better by getting the InputStream
//			String url = session.getVanillaRuntimeUrl();
//			url = url.replace("VanillaRuntime", "");

			String modelXml = session.getRepositoryConnection().getRepositoryService().loadModel(item);

			String path = null;
			String startTag = null;
			String endTag = null;
			if (modelXml.contains("<relativePath>")) {
				startTag = "<relativePath>";
				endTag = "</relativePath>";
			}
			else if (modelXml.contains("<path>")) {
				startTag = "<path>";
				endTag = "</path>";
			}
			else {
				throw new ServiceException("Unable to get the document URL.");
			}

			int beginIndex = modelXml.indexOf(startTag) + startTag.length();
			int endIndex = modelXml.indexOf(endTag);

			path = modelXml.subSequence(beginIndex, endIndex).toString();
			path = path.replace(File.separator, "/");

			logger.info("Getting document url, returning : " + "vanilla_files" + path);

			String format = "";
			try {
				format = path.substring(path.lastIndexOf("."));
			} catch (Exception e) {
				logger.info("Coulnd not found format for url = " + path + " : so we leave it to blank.");
			}

			DisplayItem it = new DisplayItem();
			it.setKey("vanilla_files" + path);
			it.setOutputFormat(format);
			it.setCommentable(isCommentable);
			it.setItemId(item.getId());
			it.setType(Comment.ITEM);

			return it;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException("Unable to get the document URL.", e);
		}
	}

	@Override
	public DisplayItem openGedDocument(PortailRepositoryItem dto) throws ServiceException {
		CommonSession session = getSession();

		String xml;
		IGedComponent gedComponent;
		try {
			RepositoryItem item = session.getRepository().getItem(dto.getId());
			String vanillaUrl = session.getVanillaRuntimeUrl();
			String login = session.getUser().getLogin();
			String password = session.getUser().getPassword();

			IVanillaContext vanilla = new BaseVanillaContext(vanillaUrl, login, password);

			SecurityProperties security = new SecurityProperties();
			security.addGroup(String.valueOf(session.getCurrentGroup().getId()));
			security.setRepositoryId("" + session.getCurrentRepository().getId());
			security.setUserId(String.valueOf(session.getUser().getId()));

			gedComponent = new RemoteGedComponent(vanilla);
			xml = session.getRepositoryConnection().getRepositoryService().loadModel(item);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}

		if (xml.indexOf("<indexid>") >= 0) {
			try {
				int versionId = Integer.parseInt(xml.substring(xml.indexOf("<indexid>") + 9, xml.indexOf("</indexid>")));

				// GedDocument definition =
				// gedComponent.getDocumentVersionById(versionId);
				// DocumentVersion version = definition.getLastVersion();

				DocumentVersion version = gedComponent.getDocumentVersionById(versionId);
				GedDocument definition = version.getParent();

				boolean isCommentable = session.getRepositoryConnection().getDocumentationService().canComment(session.getCurrentGroup().getId(), version.getId(), Comment.DOCUMENT_VERSION);

				GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(definition, session.getUser().getId());
				InputStream is = gedComponent.loadGedDocument(config);

				byte currentXMLBytes[] = IOUtils.toByteArray(is);
				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
				ObjectInputStream repIS = new ObjectInputStream();
				repIS.addStream(version.getFormat(), byteArrayIs);

				is.close();

				String name = dto.getName() + "_" + new Object().hashCode();
				session.addReport(name, repIS);

				DisplayItem item = new DisplayItem();
				item.setKey(name);
				item.setOutputFormat(version.getFormat());
				item.setCommentable(isCommentable);
				item.setItemId(version.getId());
				item.setType(Comment.DOCUMENT_VERSION);

				return item;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to open the historize report.", e);
			}
		}
		else {
			throw new ServiceException("The xml of the ged entry is malformed. Please check with an administrator.");
		}
	}

	@Override
	public String getExternalUrl(int itemId) throws ServiceException {
		CommonSession session = getSession();

		String res = "";
		try {

			String xml = session.getRepositoryConnection().getRepositoryService().loadModel(session.getRepository().getItem(itemId));

			Document doc = null;
			doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			String url = (String) root.element("url").getData();

			logger.info("Opening external url : " + url);
			return url;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			res = "<error>" + e.getMessage() + "</error>";
		}

		return res;

	}

	@Override
	public List<Group> getAvailableGroupsForHisto(int itemId) throws ServiceException {
		CommonSession session = getSession();

		String vanillaUrl = session.getVanillaRuntimeUrl();

		IVanillaContext context = new BaseVanillaContext(vanillaUrl, session.getUser().getLogin(), session.getUser().getPassword());

		ReportHistoricComponent component = new RemoteHistoricReportComponent(context);

		List<Integer> groupIds;
		try {
			IObjectIdentifier identifier = new ObjectIdentifier(session.getCurrentRepository().getId(), itemId);
			groupIds = component.getGroupsAuthorizedByItemId(identifier);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the available groups for historic of item with id = " + itemId);
		}

		List<Group> userGroups;
		try {
			userGroups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the available groups for the user " + session.getUser().getLogin());
		}

		List<Group> availablesGroups = new ArrayList<Group>();
		for (Group group : userGroups) {
			for (int groupId : groupIds) {
				if (group.getId() == groupId) {
					availablesGroups.add(group);
					break;
				}
			}
		}

		return availablesGroups;
	}

	@Override
	public List<DocumentDefinitionDTO> getAvailableDocuments(int itemId) throws ServiceException {
		CommonSession session = getSession();
		ReportHistoricComponent histoComponent = session.getHistoricComponent();

		List<DocumentDefinitionDTO> historics = new ArrayList<DocumentDefinitionDTO>();

		try {
			IObjectIdentifier obj = new ObjectIdentifier(session.getCurrentRepository().getId(), itemId);

			List<GedDocument> histos = histoComponent.getReportHistoric(obj, session.getCurrentGroup().getId());
			if (histos != null && !histos.isEmpty()) {
				for (GedDocument histo : histos) {
					historics.add(bpm.gwt.commons.server.helper.DocumentHelper.transformGedDocumentToDto(histo));
				}
			}
		} catch (Exception e) {
			String msg = "Failed to fetch historic " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}

		return historics;
	}

	@Override
	public void historize(boolean histoForMe, Integer histoId, String reportName, List<Integer> groupIds, String gedName, String outputFormat, boolean isCommentable, PortailRepositoryItem item, Date peremptionDate) throws ServiceException {

		CommonSession session = getSession();

		// ged time mate!
		logger.info("Report has historization set, sending to document manager...");

		ByteArrayInputStream byteArrayIs;
		if (item.getType() == IRepositoryApi.R_MARKDOWN_TYPE) {
			if(outputFormat.equals( CommonConstants.FORMAT_AUTO)) outputFormat = CommonConstants.FORMAT_HTML;
			byteArrayIs = session.getStream(reportName + "0", outputFormat);
		}
		else {
			byteArrayIs = session.getReport(reportName).getStream(outputFormat);
		}

		byteArrayIs.reset();

		ObjectIdentifier obj = new ObjectIdentifier(session.getCurrentRepository().getId(), item.getId());

		HistoricRuntimeConfiguration config = null;
		if (histoForMe) {
			config = new HistoricRuntimeConfiguration(obj, session.getCurrentGroup().getId(), HistorizationTarget.User, groupIds, gedName, outputFormat, session.getUser().getId(), histoId);
		}
		else {
			config = new HistoricRuntimeConfiguration(obj, session.getCurrentGroup().getId(), HistorizationTarget.Group, groupIds, gedName, outputFormat, session.getUser().getId(), histoId);
		}
		config.setPeremptionDate(peremptionDate);

		Integer id;
		try {
			id = session.getHistoricComponent().historizeReport(config, byteArrayIs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to historize the report.", e);
		}

		if (item.getItem().isRealtimeGed()) {
			try {
				config.setDirectoryTargetId(item.getDirectoryId() > 0 ? item.getDirectoryId() : null);
				session.getGedComponent().indexExistingFile(config, id, item.getItem().isCreateEntry());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to index in realtime.", e);
			}
		}

		if (isCommentable) {
			if (groupIds != null) {
				List<SecuredCommentObject> secs = new ArrayList<SecuredCommentObject>();
				for (Integer grId : groupIds) {
					SecuredCommentObject secObj = new SecuredCommentObject();
					secObj.setObjectId(id);
					secObj.setType(Comment.DOCUMENT_VERSION);
					secObj.setGroupId(grId);
					secs.add(secObj);
				}
				try {
					session.getRepositoryConnection().getDocumentationService().addSecuredCommentObjects(secs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		logger.info("Document Manager is finished");
	}

	@Override
	public void shareAklabox(String key, String name, String dashboardUrl, List<String> selectedFormats, List<String> selectedFolders, TypeShareAklabox type, List<bpm.document.management.core.model.User> selectedUsers, List<bpm.document.management.core.model.Group> selectedGroups, int selectedDirectoryId, boolean exportDashboard, boolean isLandscape) throws ServiceException {
		CommonSession session = getSession();

		IVdmManager vdmManager = session.getAklaboxManager();
		bpm.document.management.core.model.User aklaboxUser = session.getAklaboxUser();

		if (exportDashboard) {
			key = exportDashboard(key, dashboardUrl, selectedFormats.get(0), selectedFolders, isLandscape);
		}

		ObjectInputStream rep = session.getReport(key);

		String typeShare = "Public";
		if (type == TypeShareAklabox.PRIVATE) {
			typeShare = "Private";
		}
		else if (type == TypeShareAklabox.USERS_SHARE) {
			typeShare = "UserShared";
		}
		else if (type == TypeShareAklabox.GROUPS_SHARE) {
			typeShare = "GroupShared";
		}

		for (String format : selectedFormats) {
			InputStream is = rep.getStream(format);
			try {
				vdmManager.shareDocument(name + "." + format, typeShare, selectedUsers, selectedGroups, is, aklaboxUser, selectedDirectoryId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to share the document with the format " + format, e);
			}
		}
	}

	@Override
	public List<Group> getAvailableGroupsForRun(int itemId, boolean isDirectory) throws ServiceException {
		if (!isDirectory) {
			CommonSession session = getSession();

			List<Group> groups;
			try {
				groups = session.getVanillaApi().getVanillaSecurityManager().getGroups();
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the groups for item with id = " + itemId + ": " + e.getMessage());
			}

			List<Integer> availableGroupIds = new ArrayList<Integer>();

			try {
				if (groups != null) {
					for (Group group : groups) {
						if (session.getRepositoryConnection().getAdminService().canRun(itemId, group.getId())) {
							availableGroupIds.add(group.getId());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the allowed group for item with id = " + itemId + ": " + e.getMessage());
			}

			List<Group> userGroups;
			try {
				userGroups = session.getVanillaApi().getVanillaSecurityManager().getGroups(session.getUser());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to get the available groups for the user " + session.getUser().getLogin());
			}

			List<Group> availableGroups = new ArrayList<Group>();
			for (Group gr : userGroups) {
				for (Integer grId : availableGroupIds) {
					if (gr.getId().equals(grId)) {
						availableGroups.add(gr);
						break;
					}
				}
			}

			return availableGroups;
		}
		else {
			return new ArrayList<Group>();
		}
	}

	@Override
	public List<String> getAvailableFolders(String uuid) throws ServiceException {
		CommonSession session = getSession();

		FreeDashboardComponent fdComp = session.getDashboardComponent();
		try {
			return fdComp.getFolderPages(uuid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public ExportResult sendEmail(InfoShareMail infoShare) throws ServiceException {
		CommonSession session = getSession();

		// Part in upper
		String key = infoShare.getKey();
		if (infoShare.getTypeExport() == TypeExport.DASHBOARD) {

			List<String> selectedFolders = new ArrayList<String>();
			for (String folder : infoShare.getSelectedFolders().keySet()) {
				selectedFolders.add(folder);
			}

			key = exportDashboard(key, infoShare.getDashboardUrl(), infoShare.getFormat(), selectedFolders, infoShare.isLandscape());
		}

		return session.sendMails(infoShare, key, infoShare.getTypeExport());
	}

	@Override
	public CommentsInformations addComments(RepositoryItem item, Validation validation, List<CommentValue> commentValues) throws ServiceException {
		CommonSession session = getSession();
		int userId = session.getUser().getId();

		try {
			session.getVanillaApi().getCommentService().addComments(validation, userId, commentValues, session.getCurrentRepository().getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add the comments for the item " + item.getName());
		}

		return getRestitutionComments(session, item, validation, false);
	}

	@Override
	public CommentsInformations modifyComments(RepositoryItem item, Validation validation, List<CommentValue> commentValues, boolean isLastCommentUnvalidate) throws ServiceException {
		CommonSession session = getSession();
		int userId = session.getUser().getId();

		try {
			session.getVanillaApi().getCommentService().modifyComments(validation, userId, commentValues, session.getCurrentRepository().getId(), isLastCommentUnvalidate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add the comments for the item " + item.getName());
		}

		return getRestitutionComments(session, item, validation, false);
	}

	private CommentsInformations getRestitutionComments(CommonSession session, RepositoryItem item, Validation validation, boolean includeValidate) throws ServiceException {
		int userId = session.getUser().getId();
		int repId = session.getCurrentRepository().getId();

		List<CommentDefinition> commentsDefinition;
		try {
			commentsDefinition = session.getVanillaApi().getCommentService().getCommentDefinitions(item.getId(), repId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the available comments for the item " + item.getName());
		}

		if (validation != null && (includeValidate || !validation.isValid())) {

			boolean canValidate = false;
			if (validation.getValidators() != null) {
				for (UserValidation valid : validation.getValidators()) {
					if (valid.getUserId() == userId) {
						canValidate = true;
						break;
					}
				}
			}

			List<CommentInformations> commentInformations = new ArrayList<CommentInformations>();
			if (commentsDefinition != null) {
				for (CommentDefinition definition : commentsDefinition) {

					boolean canComment = false;
					CommentValue comment;
					try {
						comment = session.getVanillaApi().getCommentService().getCommentNotValidate(definition.getId(), repId);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("Unable to get the last comment for the item " + item.getName());
					}

					int nextUser = getNextUser(validation, comment);
					canComment = nextUser == userId;
					canValidate = canValidate && nextUser == -1;
					boolean canModify = !canComment && comment != null && comment.getUserId() == userId;
					boolean lastCommentUnvalidate = comment != null && comment.getStatus() == CommentStatus.UNVALIDATE;

					commentInformations.add(new CommentValidationInformations(definition, comment, canComment, canModify, lastCommentUnvalidate, nextUser));
				}
			}

			boolean isAdmin = validation.getAdminUserId() == userId;

			return new CommentsInformations(validation, TypeComment.VALIDATION, commentInformations, canValidate, isAdmin);
		}
		else if (validation == null) {
			List<CommentInformations> commentInformations = new ArrayList<CommentInformations>();
			if (commentsDefinition != null) {
				for (CommentDefinition definition : commentsDefinition) {

					try {
						List<CommentValue> comments = session.getVanillaApi().getCommentService().getComments(definition.getId(), repId, userId);
						commentInformations.add(new CommentRestitutionInformations(definition, comments));
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("Unable to get the last comments for the item " + item.getName());
					}
				}
			}
			return new CommentsInformations(validation, TypeComment.RESTITUTION, commentInformations, false, false);
		}

		return null;
	}

	private int getNextUser(Validation validation, CommentValue comment) {
		if (comment == null) {
			return validation.getCommentators().size() > 0 ? validation.getCommentators().get(0).getUserId() : -1;
		}

		boolean found = false;
		for (UserValidation commentator : validation.getCommentators()) {
			if (found) {
				return commentator.getUserId();
			}

			if (commentator.getUserId() == comment.getUserId()) {
				found = true;
			}
		}

		return -1;
	}

	@Override
	public CommentsInformations validate(RepositoryItem item, Validation validation, boolean validate) throws ServiceException {
		CommonSession session = getSession();
		int userId = session.getUser().getId();

		try {
			if (validate) {
				session.getVanillaApi().getCommentService().validate(validation, userId, session.getCurrentGroup().getId(), session.getCurrentRepository().getId());
			}
			else {
				session.getVanillaApi().getCommentService().unvalidate(validation, userId, session.getCurrentGroup().getId(), session.getCurrentRepository().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add the comments for the item " + item.getName());
		}

		return getRestitutionComments(session, item, validation, false);
	}

	@Override
	public List<CommentValue> addModifyOrDeleteComment(CommentValue comment) throws ServiceException {
		CommonSession session = getSession();
		int userId = session.getUser().getId();
		int repId = session.getCurrentRepository().getId();

		if (comment.getId() > 0) {
			try {
				session.getVanillaApi().getCommentService().addComment(userId, comment, repId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to add the comment");
			}
		}
		else {
			List<CommentValue> comments = new ArrayList<CommentValue>();
			comments.add(comment);
			try {
				session.getVanillaApi().getCommentService().modifyComments(null, userId, comments, session.getCurrentRepository().getId(), false);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("Unable to modify or delete the comment");
			}
		}

		try {
			List<CommentValue> comments = session.getVanillaApi().getCommentService().getComments(comment.getCommentId(), repId, userId);
			return comments;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the last comments for the item.");
		}
	}

	@Override
	public RScriptModel executeMarkdown(LaunchReportInformations itemInfo, String selectedFormat) throws Exception {
		try {
			CommonSession session = getSession();
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			String airRuntimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_RUNTIME_URL);

			RemoteAdminManager manag = new RemoteAdminManager(airRuntimeUrl, null, getLocale());
			String sessionId = manag.connect(session.getUser());
			ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());

			String xml = session.getRepositoryConnection().getRepositoryService().loadModel(session.getRepository().getItem(itemInfo.getItem().getId()));
			Document doc = null;
			doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			String idScript = (String) root.element("idScript").getData();
			String idProject = (String) root.element("idProject").getData();

			AirProject project = manager.getAirProjectbyId(Integer.parseInt(idProject));

			RScriptModel model = manager.getScriptModelbyId(Integer.parseInt(idScript));

			RScript script = manager.getScriptbyId(model.getIdScript());

//			if (project.getLinkedDatasets() != null && !project.getLinkedDatasets().isEmpty()) {
//
//				List<Dataset> dts = manager.getDatasetsbyProject(project);
//				manager.addDatasetstoR(dts);
//			}

			RemoteWorkflowManager smartManager = new RemoteWorkflowManager(vanillaUrl, sessionId, getLocale());

			List<bpm.vanilla.platform.core.beans.resources.Parameter> wparams = (List<bpm.vanilla.platform.core.beans.resources.Parameter>) smartManager.getResources(TypeResource.PARAMETER);

			List<bpm.vanilla.platform.core.beans.resources.Parameter> lovParams = new ArrayList<>();

			for (VanillaGroupParameter groupParam : itemInfo.getGroupParameters()) { // modification des parametres
				for (VanillaParameter param : groupParam.getParameters()) {
					Parameter selectedParam = null;
					for (Parameter p : wparams) { // we search workflow param corresponding
						if (param.getName().equals(p.getName())) {
							selectedParam = p;
							break;
						}
					}
					if (selectedParam != null && selectedParam.getParameterType().equals(TypeParameter.LOV)) { // lov case
						List<ListOfValues> lovs = (List<ListOfValues>) smartManager.getResources(TypeResource.LOV);
						for (ListOfValues lov : lovs) {
							if (lov.getId() == Integer.parseInt(param.getSelectedValues().get(0))) {
								selectedParam.setValueListOfValues(lov);
								lovParams.add(selectedParam);
								break;
							}
						}
					}
					else { // classic case
						model.setScript(model.getScript().replace("{$P_" + param.getName() + "}", param.getSelectedValues().get(0)));
					}

				}
			}
			List<String> outputs = new ArrayList<String>();
			if(!selectedFormat.equals(CommonConstants.FORMAT_AUTO)){
				outputs.add(selectedFormat);
			}
			
//			model.setScript(model.getScript().replace("\n", "\r\n"));		//kmo 02/05/2017
			
			model = manager.renderMarkdown(model.getScript(), script.getName(), outputs, lovParams);

			model.setIdScript(script.getId());

			if (model.getOutputVarstoString().size() > 0) {
				int i = 0;
				for (String output : model.getOutputVarstoString()) {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

					try {
						outputStream.write(Base64.decodeBase64(output.getBytes()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (model.getOutputs().length > i && model.getOutputs()[i].equals("temphtml")) { // html
						session.addStream(script.getName() + i, CommonConstants.FORMAT_HTML, is);
					}
					else if (model.getOutputs().length > i && model.getOutputs()[i].equals("temppdf")) { // pdf
						session.addStream(script.getName() + i, CommonConstants.FORMAT_PDF, is);
					}
					else if (model.getOutputs().length > i && model.getOutputs()[i].equals("tempdoc")) { // doc
						session.addStream(script.getName() + i, CommonConstants.FORMAT_DOCX, is);
					}
					i++;
				}
			}

			return model;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to render markdown.");
		}
	}

	@Override
	public String getForwardUrlForKpi(int themeId, String selectedDate) throws ServiceException {
		CommonSession session = getSession();

		String kpiUserUrl = portalConfig.getFmuserwebUrl();
		logger.info("Using KpiUser base url " + kpiUserUrl);

		String url = "";
		try {
			url += kpiUserUrl;
			String sessionId = session.getVanillaSessionId();
			if (url.contains("?")) {
				url += "&bpm.vanilla.sessionId=" + sessionId;
			}
			else {
				url += "?bpm.vanilla.sessionId=" + sessionId;
			}
			url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
			url += "&bpm.vanilla.repositoryId=" + session.getCurrentRepository().getId();
			url += "&bpm.vanilla.theme.id=" + themeId;
			if (selectedDate != null) {

				url += "&date=" + selectedDate;
			}

			logger.info("forwarding to secured url : " + url);

			return url;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			logger.info(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public void deleteReportBackground(ReportBackground report) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getRepositoryConnection().getReportHistoricService().deleteReportBackground(report);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the background report.");
		}
	}

	@Override
	public List<IRepositoryObject> getBackgroundReports(int limit) throws ServiceException {
		CommonSession session = getSession();

		int userId = session.getUser().getId();
		try {
			List<IRepositoryObject> result = new ArrayList<>();
			
			List<ReportBackground> reports = session.getRepositoryConnection().getReportHistoricService().getReportBackgrounds(userId, limit);
			if (reports != null) {
				for (ReportBackground report : reports) {
					if (report.isRunning()) {
						checkReport(session, report);
					}
					result.add(report);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the background reports.");
		}
	}

	private void checkReport(CommonSession session, ReportBackground report) throws ServiceException {
		ReportingComponent reportingComponent = session.getReportingComponent();
		
		CustomRunIdentifier identifier = new CustomRunIdentifier(report.getTaskKey(), report.getTaskId());
		try {
			boolean isCompleted = reportingComponent.checkRunAsynchState(identifier);
			if (isCompleted) {
				FileInformations fileInfos = reportingComponent.stockReportBackground(identifier);
				report.setPath(fileInfos.getPath());
				report.setSize(fileInfos.getSize());
				report.setRunning(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			report.setRunning(false);
		}
		
		updateReportBackground(report);
	}

	@Override
	public void updateReportBackground(ReportBackground item) throws ServiceException {
		CommonSession session = getSession();

		try {
			session.getRepositoryConnection().getReportHistoricService().addOrUpdateReportBackground(item);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update the background report.");
		}
	}

	@Override
	public String getForwardUrlForKpiMap(PortailRepositoryItem item) throws ServiceException {
		CommonSession session = getSession();
		try {
			String xml = session.getRepositoryConnection().getRepositoryService().loadModel(item.getItem());
			KpiMapBean map = (KpiMapBean) new XStream().fromXML(xml);
			
			String kpiUserUrl = portalConfig.getKpiMapUrl();
			logger.info("Using KpiMap base url " + kpiUserUrl);

			String url = "";
			try {
				url += kpiUserUrl;
				String sessionId = session.getVanillaSessionId();
				if (url.contains("?")) {
					url += "&bpm.vanilla.sessionId=" + sessionId;
				}
				else {
					url += "?bpm.vanilla.sessionId=" + sessionId;
				}
				url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
				url += "&bpm.vanilla.repositoryId=" + session.getCurrentRepository().getId();
				url += "&bpm.kpi.idMap=" + map.getMapId();
				
				//filters
				String levelIds = "";
				boolean first = true;
				for(Integer i : map.getLevelIds()) {
					if(first) {
						first = false;
					}
					else {
						levelIds += ",";
					}
					levelIds += i;
				}
				String metricIds = "";
				first = true;
				for(Integer i : map.getMetricsIds()) {
					if(first) {
						first = false;
					}
					else {
						metricIds += ",";
					}
					metricIds += i;
				}

				url += "&bpm.kpi.levels=" + levelIds;
				url += "&bpm.kpi.metrics=" + metricIds;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if(map.getStartDate() != null) {
					url += "&bpm.kpi.startdate=" + sdf.format(map.getStartDate());	
				}
				if(map.getEndDate() != null) {
					url += "&bpm.kpi.enddate=" + sdf.format(map.getEndDate());
				}
				logger.info("forwarding to secured url : " + url);

				return url;
			} catch (Exception e) {
				String msg = "Failed to generate secured forward url : " + e.getMessage();
				logger.info(msg, e);
				throw new ServiceException(msg);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public CmisInformations getCmisRepositories(CmisInformations cmisInfos, String properties) {
		if (cmisInfos == null) {
			HashMap<String, String> parameters = new HashMap<>();
			
			Properties p = new Properties();
		    try {
				p.load(new StringReader(properties));
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    for (Object key : p.keySet()) {
		    	parameters.put(key.toString(), p.getProperty(key.toString()));
		    }
		    
		    cmisInfos = new CmisInformations(parameters, false);
		}
		
		return CmisHelper.getRepositories(cmisInfos);
	}

	@Override
	public List<CmisItem> getCmisFolders(CmisInformations cmisInfos, String folderId) {
		return CmisHelper.getFolders(cmisInfos, folderId);
	}

	@Override
	public ExportResult shareCmis(InfoShareCmis infoShare) throws ServiceException {
		CommonSession session = getSession();
		String key = infoShare.getKey();
		if (infoShare.getTypeExport() == TypeExport.DASHBOARD) {

			List<String> selectedFolders = new ArrayList<String>();
			for (String folder : infoShare.getSelectedFolders().keySet()) {
				selectedFolders.add(folder);
			}

			key = exportDashboard(key, infoShare.getDashboardUrl(), infoShare.getFormat(), selectedFolders, infoShare.isLandscape());
		}
		
		ByteArrayInputStream stream = null;
		if (infoShare.getTypeExport() == TypeExport.MARKDOWN) {
			stream = session.getStream(infoShare.getItemName() + "0", infoShare.getFormat());
		}
		else {
			ObjectInputStream reportStream = session.getReport(key);
			stream = reportStream.getStream(infoShare.getFormat());
		}
		stream.reset();
		return CmisHelper.createDocument(infoShare, stream);
	}

	@Override
	public String getForwardUrlForDataPreparation(int itemId) throws ServiceException {
		CommonSession session = getSession();

		String faBaseUrl = portalConfig.getDataPreparationUrl();
		logger.info("Using VanillaDataPreparation base url " + faBaseUrl);

		String url = "";
		
		String dataprepId = null;
		try {
			dataprepId = session.getRepositoryConnection().getRepositoryService().loadModel(session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId));
		} catch(Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			logger.info(msg, e);
			throw new ServiceException(msg);
		}

		try {
			url += faBaseUrl;
			String sessionId = session.getVanillaSessionId();
			if (url.contains("?")) {
				url += "&bpm.vanilla.sessionId=" + sessionId;
			}
			else {
				url += "?bpm.vanilla.sessionId=" + sessionId;
			}
			url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
			url += "&bpm.vanilla.repositoryId=" + session.getCurrentRepository().getId();
			url += "&bpm.vanilla.dataprep.id=" + dataprepId;

			logger.info("forwarding to secured url : " + url);

			return url;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			logger.info(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public void setReportValidation(RepositoryItem item, Validation validation, boolean launchValidationProcess) throws ServiceException {
		CommonSession session = getSession();
		
		try {
			validation = session.getRepositoryConnection().getRepositoryService().addOrUpdateValidation(validation);
			if (launchValidationProcess) {
				validate(item, validation, false);
			}
		} catch (Exception e) {
			String msg = "Failed to add validation : " + e.getMessage();
			logger.info(msg, e);
			throw new ServiceException(msg);
		}
	}

	@Override
	public CommentsInformations getValidation(RepositoryItem item) throws ServiceException {
		try {
			CommonSession session = getSession();
			Validation validation = session.getRepositoryConnection().getRepositoryService().getValidation(item.getId());
			
			return getRestitutionComments(session, item, validation, true);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the available validation for the item with id " + item.getId());
		}
	}

	@Override
	public List<Validation> getValidations(boolean includeInactive) throws ServiceException {
		try {
			CommonSession session = getSession();
			return session.getRepositoryConnection().getRepositoryService().getValidations(includeInactive);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the available validations: " + e1.getMessage());
		}
	}

	@Override
	public List<ValidationCircuit> getValidationCircuits() throws ServiceException {
		try {
			CommonSession session = getSession();
			return session.getRepositoryConnection().getRepositoryService().getValidationCircuits();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the available validations: " + e1.getMessage());
		}
	}

	@Override
	public ValidationCircuit manageValidationCircuit(ValidationCircuit circuit) throws ServiceException {
		return manageValidationCircuit(circuit, ManageAction.SAVE_OR_UPDATE);
	}

	@Override
	public ValidationCircuit deleteValidationCircuit(ValidationCircuit circuit) throws ServiceException {
		return manageValidationCircuit(circuit, ManageAction.DELETE);
	}

	private ValidationCircuit manageValidationCircuit(ValidationCircuit circuit, ManageAction action) throws ServiceException {
		try {
			CommonSession session = getSession();
			return session.getRepositoryConnection().getRepositoryService().manageValidationCircuit(circuit, action);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the available validations: " + e1.getMessage());
		}
	}

	@Override
	public List<PortailRepositoryItem> getPendingItemsToComment() throws ServiceException {
		CommonSession session = getSession();
		try {
			RepositoryImpl impl = new RepositoryImpl(session);
			IRepositoryApi sock = session.getRepositoryConnection();
			
			List<RepositoryItem> items = session.getRepositoryConnection().getRepositoryService().getPendingItemsToComment(session.getUser().getId());
			return buildItems(impl, sock, items);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException("Unable to get the pending items to comment for the you");
		}
	}

	private List<PortailRepositoryItem> buildItems(RepositoryImpl r, IRepositoryApi sock, List<RepositoryItem> items) {
		List<PortailRepositoryItem> portalItems = new ArrayList<PortailRepositoryItem>();
		for(RepositoryItem item : items) {
			if(item.isDisplay()) {
				try {
					PortailRepositoryItem ti = r.buildItem(sock, null, item);
					if (ti != null) {
						portalItems.add(ti);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return portalItems;
	}

	@Override
	public void changeValidationNextActor(RepositoryItem item, Validation validation, User user) throws ServiceException {
		CommonSession session = getSession();

		try {
			CommentsInformations informations = getRestitutionComments(session, item, validation, false);
			if (informations != null && informations.getComments() != null) {
				for (CommentInformations comment : informations.getComments()) {
					if (comment instanceof CommentValidationInformations) {
						CommentValidationInformations commentValidation = (CommentValidationInformations) comment;
						
						int oldUserId = commentValidation.getNextCommentator();
						session.getRepositoryConnection().getRepositoryService().updateUserValidation(validation.getId(), oldUserId, user.getId(), UserValidationType.COMMENTATOR);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
}
