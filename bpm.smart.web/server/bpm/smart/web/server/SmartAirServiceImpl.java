package bpm.smart.web.server;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.codec.binary.Base64;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.MirrorCran;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScript.ScriptType;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.core.model.UsersProjectsShares;
import bpm.smart.web.client.services.SmartAirService;
import bpm.smart.web.server.security.SmartWebSession;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.ActivityOutput;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SmartAirServiceImpl extends RemoteServiceServlet implements SmartAirService {

	private static final long serialVersionUID = -660218264074823448L;

	private SmartWebSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), SmartWebSession.class);
	}

	@Override
	public List<AirProject> getProjects(int idUser) throws ServiceException {
		SmartWebSession session = getSession();
		List<AirProject> lesProjects;

		try {
			lesProjects = session.getManager().getVisibleProjects(idUser);
			for (AirProject project : lesProjects) {
				String url = project.getUrlIcon();
				if (url != null) {
					if (url.contains("webapps")) {
						url = url.substring(url.indexOf("webapps") + "webapps".length(), url.length());
					}

					String newurl = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName() + ":" + getThreadLocalRequest().getServerPort();
					url = newurl + url;
					project.setUrlIcon(url);
				}
			}
			return lesProjects;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of projects : " + e.getMessage());
		}
	}

	@Override
	public void addOrEditProject(AirProject project) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			if (project.getId() > 0) {
				session.getManager().updateAirProject(project);
			}
			else {
				session.getManager().saveAirProject(project);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the project.", e);
		}

	}

	@Override
	public void deleteProject(AirProject project) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			session.getManager().deleteAirProject(project);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the project.", e);
		}
	}

	@Override
	public String executeProject(AirProject project) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			String result = session.getManager().executeAirProject(project);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute the project.", e);
		}

	}

	@Override
	public RScriptModel executeScript(RScriptModel box, List<Parameter> lovParameters) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			RScriptModel result = session.getManager().executeScriptR(box, lovParameters);
			result.setOutputVars(null); // on envoie pas les variables cote
										// client

			if (result.getOutputFiles() != null) {
				int nbfiles = result.getOutputFiles().length;
				if (nbfiles > 0) {
					for (int i = 0; i < nbfiles; i++) {
						String url = result.getOutputFiles()[i];
						if (url.split(";")[0].equals("txt")) {
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

							try {
								outputStream.write(url.split(";")[1].getBytes());
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

							try {
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

							session.addStream("Help", CommonConstants.FORMAT_TXT, is);
						}
						else if (url.split(";")[0].equals("pdf")) {
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

							try {
								outputStream.write(Base64.decodeBase64(url.substring(4).getBytes()));
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

							try {
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

							session.addStream("Help", CommonConstants.FORMAT_PDF, is);
						}
						else if (url.split(";")[0].equals("html")) {
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

							try {
								outputStream.write(Base64.decodeBase64(url.substring(5).getBytes()));
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

							try {
								outputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}

							session.addStream("Help", CommonConstants.FORMAT_HTML, is);
						}
					}
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute the script.", e);
		}

	}

	@Override
	public List<MirrorCran> loadMirrors() throws ServiceException {
		SmartWebSession session = getSession();

		try {
			List<MirrorCran> result = session.getManager().loadMirrors();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load mirrors.", e);
		}

	}

	@Override
	public SmartAdmin getAdmin(int idUser) throws ServiceException {
		SmartWebSession session = getSession();
		SmartAdmin admin = null;

		try {
			List<SmartAdmin> adminList = session.getManager().getSmartAdminbyUser(idUser);
			if (adminList.size() > 0) {
				admin = adminList.get(0);
			}
			return admin;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Error during the recuperation of Admin Configuration : " + e.getMessage());
		}
	}

	@Override
	public void addOrEditAdmin(SmartAdmin admin) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			if (admin.getId() > 0) {
				session.getManager().updateSmartAdmin(admin);
			}
			else {
				session.getManager().saveSmartAdmin(admin);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit the Admin Configuration.", e);
		}

	}

	@Override
	public void deleteAdmin(SmartAdmin admin) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			session.getManager().deleteSmartAdmin(admin);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the Admin Configuration.", e);
		}
	}

	@Override
	public List<StatDataColumn> getStatsDataset(List<Dataset> datasets) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			List<StatDataColumn> result = session.getManager().loadStatsDataset(datasets);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load stats.", e);
		}
	}

	@Override
	public String getAvatarIconUrl(String avatar) throws ServiceException {
		SmartWebSession session = getSession();
		String customLogo = null;
		try {
			customLogo = session.getManager().getAvatarIconUrl(avatar);

			if (customLogo != null) {
				if (customLogo.contains("webapps")) {
					customLogo = customLogo.substring(customLogo.indexOf("webapps") + "webapps".length(), customLogo.length());
				}

				String url = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName() + ":" + getThreadLocalRequest().getServerPort();
				customLogo = url + customLogo;
			}

		} catch (Exception e) {
			e.printStackTrace();
			customLogo = null;
			throw new ServiceException("Unable to load icon. ", e);
		}

		return customLogo;
	}

	@Override
	public int addorEditScript(RScript script) throws ServiceException {
		SmartWebSession session = getSession();
		int idScript = 0;
		try {
			if (script.getId() > 0) {
				session.getManager().updateRScript(script);
				idScript = script.getId();
			} else {
				idScript = session.getManager().saveRScript(script);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save script.", e);
		}
		return idScript;
	}

	@Override
	public int addorEditScriptModel(RScriptModel script) throws ServiceException {
		SmartWebSession session = getSession();
		int idModel = 0;
		try {
			if (script.getId() > 0) {
				session.getManager().updateRScriptModel(script);
				idModel = script.getId();
			}
			else {
				idModel = session.getManager().saveRScriptModel(script);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add or edit script version.", e);
		}
		return idModel; 
	}

	@Override
	public void deleteRScript(RScript script) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			session.getManager().deleteRScript(script);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the script.", e);
		}
	}

	@Override
	public void deleteRScriptModel(RScriptModel script) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			session.getManager().deleteRScriptModel(script);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the script model.", e);
		}
	}

	@Override
	public List<RScriptModel> getLastScriptModels(List<RScript> scripts) throws ServiceException {
		SmartWebSession session = getSession();
		List<RScriptModel> result = new ArrayList<RScriptModel>();
		try {
			result = session.getManager().getLastScriptModels(scripts);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the scripts models.", e);
		}
		return result;
	}

	@Override
	public List<RScript> loadRScripts(int idProject) throws ServiceException {
		SmartWebSession session = getSession();
		List<RScript> result = new ArrayList<RScript>();
		try {
			result = session.getManager().getRScriptsbyProject(idProject);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load scripts.", e);
		}
		return result;
	}

	@Override
	public List<RScript> loadAllRScripts() throws ServiceException {
		SmartWebSession session = getSession();
		List<RScript> result = new ArrayList<RScript>();
		try {
			result = session.getManager().getAllScripts();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load scripts.", e);
		}
		return result;
	}

	@Override
	public void checkInScript(RScript script) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			session.getManager().checkInScript(script);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to checkin script.", e);
		}
	}

	@Override
	public String checkOutScript(RScript script) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			String result = session.getManager().checkOutScript(script);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to checkout script.", e);
		}
	}

	@Override
	public List<RScriptModel> getModelsbyScript(int id) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			List<RScriptModel> result = session.getManager().getRScriptModelsbyScript(id);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load models.", e);
		}
	}

	public String getTempFileUrl(String filename) throws ServiceException {

		if (filename != null) {
			String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
			path = path.substring(path.indexOf("webapps") + "webapps".length(), path.length());

			String url = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName() + ":" + getThreadLocalRequest().getServerPort();
			filename = url + "/" + path + filename;
		}
		return filename;
	}

	@Override
	public String getTempText(String url) throws ServiceException {
		// File file = new
		// File("C:/Users/KMO/Documents/VanillaAir_Package/vanilla-tomcat-5.0_GA_Air_v1_3/webapps/vanilla_files/temp/1438354705355.txt");
		File file = new File("");
		try {
			file = new File(new URL(url).getPath());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		String res = "";
		try {
			// res = new String(Files.readAllBytes(file.toPath()),
			// StandardCharsets.UTF_8);
			res = new String(Files.readAllBytes(Paths.get(file.getPath())), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	@Override
	public String exportToFile(String name, String content, String type) throws ServiceException {
		CommonSession session = getSession();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			outputStream.write(content.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		ByteArrayInputStream is = new ByteArrayInputStream(outputStream.toByteArray());

		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (type.equals(ScriptType.R.name())) {
			session.addStream(name, CommonConstants.FORMAT_R, is);
		}
		else if (type.equals(ScriptType.MARKDOWN.name())) {
			session.addStream(name, CommonConstants.FORMAT_RMD, is);
		}

		return name;

	}

	@Override
	public void shareProject(int id, List<User> users) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			session.getManager().shareProject(id, users);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to share project.", e);
		}

	}

	@Override
	public List<User> getUsers() throws ServiceException {
		SmartWebSession session = getSession();
		try {
			List<User> result = session.getVanillaApi().getVanillaSecurityManager().getUsers();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load users.", e);
		}
	}

	@Override
	public List<UsersProjectsShares> getSharedProjectsUsersbyIdProject(int idProject) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			List<UsersProjectsShares> result = session.getManager().getSharedProjectsUsersbyIdProject(idProject);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load shared projects.", e);
		}
	}

	@Override
	public String addSessionStream(byte[] stream, String type) throws ServiceException {
		CommonSession session = getSession();

		ByteArrayInputStream is = new ByteArrayInputStream(stream);

		session.addStream(stream.toString(), type, is);
		return stream.toString();
	}

	@Override
	public String zipProject(AirProject project, boolean allVersions) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			ByteArrayInputStream is = (ByteArrayInputStream) session.getManager().zipAirProject(project.getId(), allVersions);
			String name = project.getName().replace(" ", "_");// +
																// ".vanillaair";
			session.addStream(name, "vanillaair", is);
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to zip project.", e);
		}
	}

	@Override
	public RScriptModel addDatasetToR(Dataset dataset) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			RScriptModel script = session.getManager().addDatasettoR(dataset);
			return script;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load dataset to R.", e);
		}
	}

	@Override
//	public RScriptModel generateStatPlot(int col1Id, int col2Id, Dataset dataset, String userREnv) throws ServiceException {
//		CommonSession session = getSession();
//		DataColumn col1, col2;
//		SmartWebSession smartsession = getSession();
//		try {
//			col1 = session.getVanillaApi().getVanillaPreferencesManager().getDataColumnbyId(col1Id);
//			col2 = session.getVanillaApi().getVanillaPreferencesManager().getDataColumnbyId(col2Id);
//
//			RScriptModel script = smartsession.getManager().generateStatRPlot(col1, col2, dataset, userREnv);
//			return script;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ServiceException("Unable to get dataset stats", e);
//		}
//	}
	public RScriptModel generateStatPlot(DataColumn col1, DataColumn col2, Dataset dataset) throws ServiceException {
		SmartWebSession smartsession = getSession();
		try {
			RScriptModel script = smartsession.getManager().generateStatRPlot(col1, col2, dataset);
			return script;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get dataset stats", e);
		}
	}

	@Override
	public RScriptModel renderMarkdown(String script, String name, List<String> outputs, List<Parameter> lovParameters) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			RScriptModel result = session.getManager().renderMarkdown(script, name, outputs, lovParameters);
			if (result.getOutputVarstoString().size() > 0) {
				int i = 0;
				for (String output : result.getOutputVarstoString()) {
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
					if (result.getOutputs().length > i && result.getOutputs()[i].equals("temphtml")) { // html
						session.addStream(name + i, CommonConstants.FORMAT_HTML, is);
					}
					else if (result.getOutputs().length > i && result.getOutputs()[i].equals("temppdf")) { // pdf
						session.addStream(name + i, CommonConstants.FORMAT_PDF, is);
					}
					else if (result.getOutputs().length > i && result.getOutputs()[i].equals("tempdoc")) { // doc
						session.addStream(name + i, CommonConstants.FORMAT_DOCX, is);
					}
					i++;
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to render markdown.", e);
		}
	}

	@Override
	public List<String> getColumnDistinctValues(String datasetName, int columnIndex) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			return session.getManager().getColumnDistinctValues(datasetName, columnIndex);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get column values.", e);
		}
	}

	@Override
	public RScriptModel generateSummaryPlot(Dataset dataset) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			RScriptModel script = session.getManager().generateSummaryRPlot(dataset);
			return script;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get dataset summary", e);
		}
	}

	@Override
	public List<DataColumn> getDatasetColumns(String datasetName) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			return session.getManager().getDatasetColumns(datasetName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get dataset columns.", e);
		}
	}
	
	@Override
	public ActivityLog executeActivity(Activity activity) throws Exception {
		SmartWebSession session = getSession();
		try {
			ActivityLog log = session.getManager().executeActivity(activity);
			//List<String> urls = new ArrayList<String>();
			for (ActivityOutput out : log.getOutputs()) {
				String url = out.getPath();
				if (url != null) {
					if (url.contains("webapps")) {
						url = url.substring(url.indexOf("webapps") + "webapps".length(), url.length());
					}

					String newurl = getThreadLocalRequest().getScheme() + "://" + getThreadLocalRequest().getServerName() + ":" + getThreadLocalRequest().getServerPort();
					url = newurl + url;
					out.setPath(url);
				}
			}
			return log;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute activity.", e);
		}
	}
	
	@Override
	public List<StatDataColumn> calculateRStats(List<DataColumn> columns, Dataset dataset) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			return session.getManager().calculateRStats(columns, dataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to calculate stats.", e);
		}
	}
	
	@Override
	public List<StatDataColumn> createStatsDataset(Dataset dataset) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			return session.getManager().createStatsDataset(dataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create stats of the dataset.", e);
		}
	}
	
	@Override
	public List<Dataset> getDatasetsbyProject(AirProject proj) throws ServiceException {
		SmartWebSession session = getSession();
		try {
			return session.getManager().getDatasetsbyProject(proj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get datasets.", e);
		}
	}
	
	@Override
	public List<RScriptModel> addDatasetsToR(List<Dataset> datasets) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			List<RScriptModel> scripts = session.getManager().addDatasetstoR(datasets);
			return scripts;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add datasets to R.", e);
		}
	}

	@Override
	public void deleteLinkedDatasets(Dataset dataset) throws ServiceException {
		SmartWebSession session = getSession();

		try {
			session.getManager().deleteLinkedDatasets(dataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete linked datasets", e);
		}
	}
	
	@Override
	public String getUrlContent(String urlString) throws ServiceException {
		try {
			URL url = new URL(urlString);
			
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Cookie", "JSESSIONID="+getThreadLocalRequest().getSession().getId());
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(
			                   new InputStreamReader(conn.getInputStream()));
			String inputLine = null;
			StringBuffer sb = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			br.close();
			
			String htmlContent = sb.toString();
			return htmlContent;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get content", e);
		}
	}
	
	@Override
	public byte[] getScreenCapture(String content, String type) throws ServiceException {
		byte[] imageInByte = null;
		try {	
			
			JEditorPane pane = new JEditorPane();
			switch(type){
				case CommonConstants.FORMAT_HTML:
					pane.setContentType("text/html");
					break;
				case CommonConstants.FORMAT_PDF:
					return null;
				case CommonConstants.FORMAT_DOCX:
					return null;
				default:
					pane.setContentType("text/html");
					break;
			}
			
			pane.setText(content);
			//pane.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			pane.setSize(600, 800);
			/*
			 * Create a BufferedImage
			 */
			BufferedImage image = new BufferedImage(pane.getWidth(), pane.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			/*
			 * Have the image painted by SwingUtilities
			 */
			JPanel container = new JPanel();
			SwingUtilities.paintComponent(g, pane, container, 0, 0, image.getWidth(), image.getHeight());
			g.dispose();


			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			

			out.flush();
			imageInByte = Base64.encodeBase64(out.toByteArray());
			out.close();
			
			return imageInByte;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get capture", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get capture", e);
		}
	}

	@Override
	public RScriptModel executeScript(RScriptModel box) throws ServiceException {
		return executeScript(box, null);
	}
}
