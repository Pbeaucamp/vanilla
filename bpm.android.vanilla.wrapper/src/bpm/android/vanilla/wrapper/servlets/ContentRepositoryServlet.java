package bpm.android.vanilla.wrapper.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.IAndroidVanillaManager;
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.AndroidGroup;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.reporting.AndroidVanillaManager;
import bpm.android.vanilla.wrapper.tools.SessionContent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

/**
 * Servlet implementation class ContentRepositoryServlet
 */
public class ContentRepositoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ComponentAndroidWrapper component;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ContentRepositoryServlet(ComponentAndroidWrapper component) {
		super();
		this.component = component;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream is = request.getInputStream();
		String req = IOUtils.toString(is, "UTF-8");
		is.close();

		String resp = null;

		try {
			resp = prepareResponse(req);
		} catch (Exception ex) {
			component.getLogger().error("Error when changing measure - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}

	private String prepareResponse(String req) {
		IAndroidVanillaManager vanillaManager = new AndroidVanillaManager(component);

		String response = "";
		try {
			AndroidVanillaContext ctx = getAndroidContext(req);
			
			String sessionId = vanillaManager.connect(ctx);
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();

			IAndroidRepositoryManager repManager = session.getRepositoryManager();
			List<AndroidItem> items = repManager.getRepositoryContent();
			
			response = prepareXml(items, "", session);
		} catch (Exception e) {
			e.printStackTrace();
			response = prepareXml(new ArrayList<AndroidItem>(), e.toString(), null);
		}

		return response;
	}
	
	private AndroidVanillaContext getAndroidContext(String req) throws Exception {
		String login = null;
		String password = null;
		String groupName = null;
		String repositoryName = null;

		Document document = null;
		try {
			document = DocumentHelper.parseText(req);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("userEntry")) {
			try {
				if (e.element("username") != null) {

					Element d = e.element("username");
					if (d != null) {
						login = d.getStringValue();
					}
				}
				if (e.element("password") != null) {

					Element d = e.element("password");
					if (d != null) {
						password = d.getStringValue();
					}
				}

				if (e.element("selectedGroup") != null) {
					Element d = e.element("selectedGroup");
					if (d != null) {
						groupName = d.getStringValue();
					}
				}
				if (e.element("selectedRepository") != null) {
					Element d = e.element("selectedRepository");
					if (d != null) {
						repositoryName = d.getStringValue();
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}

		IVanillaAPI rootApi = getRootApi();
		
		String vanUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);

		Group group = rootApi.getVanillaSecurityManager().getGroupByName(groupName);
		Repository repository = rootApi.getVanillaRepositoryManager().getRepositoryByName(repositoryName);
		
		AndroidGroup androidGroup = new AndroidGroup(group.getId(), group.getName());
		AndroidRepository androidRepository = new AndroidRepository(repository.getId(), repository.getName());
		
		AndroidVanillaContext ctx = new AndroidVanillaContext();
		ctx.setVanillaRuntimeUrl(vanUrl);
		ctx.setLogin(login);
		ctx.setPassword(password);
		ctx.setRepository(androidRepository);
		ctx.setGroup(androidGroup);
		
		return ctx;
	}
	
	private IVanillaAPI getRootApi() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanUrl = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		return new RemoteVanillaPlatform(vanUrl, login, password);
	}

	private String prepareXml(List<AndroidItem> items, String error, SessionContent session) {
		if (error.isEmpty()) {
			StringBuilder buf = new StringBuilder();
			buf.append("<rootElement>\n");
			buf.append("    <sessionId>" + session.getIdentifier() + "</sessionId>\n");
			buf.append("    <items>\n");
			for (AndroidItem item : items) {

				try {
					boolean hasParam = item.getParameters() != null && !item.getParameters().isEmpty();
					boolean isCube = item.getType() == IRepositoryApi.FASD_TYPE;

					if (item.getType() == IRepositoryApi.CUST_TYPE || item.getType() == IRepositoryApi.FWR_TYPE || item.getType() == IRepositoryApi.FASD_TYPE || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FD_DICO_TYPE || item.getType() == IRepositoryApi.DISCONNECTED_PCKG) {
						buf.append("        <item>\n");
						buf.append("            <name>" + item.getName() + "</name>\n");
						buf.append("            <id>" + item.getId() + "</id>\n");
						buf.append("            <type>" + item.getType() + "</type>\n");
						buf.append("            <subtype>" + item.getSubtype() + "</subtype>\n");
						buf.append("            <hasparameters>" + hasParam + "</hasparameters>\n");
						if (hasParam) {
							buf.append("            <parameters>\n");
							for (Parameter param : item.getParameters()) {
								buf.append("                <parameter>\n");
								buf.append("                    <name>" + param.getParamName() + "</name>\n");
								if (param.getParamChild() != null) {
									buf.append("                    <child>" + param.getParamChild().getParamName() + "</child>\n");
								}
								else {
									buf.append("                    <child></child>\n");
								}
								buf.append("                </parameter>\n");
							}
							buf.append("            </parameters>\n");
						}
						buf.append("            <iscube>" + isCube + "</iscube>\n");
						if (isCube) {
							AndroidCube cube = (AndroidCube) item;

							buf.append("            <cubes>\n");
							for (String cubeName : cube.getCubeNames()) {
								buf.append("                <name>" + cubeName + "</name>\n");
							}
							buf.append("            </cubes>\n");
						}
						buf.append("        </item>\n");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
			buf.append("    </items>\n");
			buf.append("</rootElement>");

			return buf.toString();
		}
		else {
			String toResp;
			toResp = "<error>" + error + "</error>";

			return toResp;
		}
	}

}
