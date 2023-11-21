package bpm.android.vanilla.wrapper.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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

import bpm.android.vanilla.core.IAndroidVanillaManager;
import bpm.android.vanilla.core.beans.AndroidGroup;
import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.reporting.AndroidVanillaManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

/**
 * Servlet implementation class RepositoryServlet
 */
public class RepositoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RepositoryServlet(ComponentAndroidWrapper component) {
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
			component.getLogger().error("Error when Loading Cube Views - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}

	private String prepareResponse(String req) {
		IAndroidVanillaManager manager = new AndroidVanillaManager(component);

		String response = "";
		try {
			AndroidVanillaContext ctx = getVanillaContext(req);

			AndroidVanillaContext ctxWithGroups = manager.getGroupsAndRepositories(ctx);

			response = prepareXml(ctxWithGroups, "");
		} catch (Exception e) {
			e.printStackTrace();
			response = prepareXml(null, e.toString());
		}

		return response;
	}

	private AndroidVanillaContext getVanillaContext(String req) throws Exception {
		String login = null;
		String password = null;

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
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}

		String vanUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);

		AndroidVanillaContext ctx = new AndroidVanillaContext();
		ctx.setVanillaRuntimeUrl(vanUrl);
		ctx.setLogin(login);
		ctx.setPassword(password);
		return ctx;
	}

	private String prepareXml(AndroidVanillaContext ctx, String error) {
		if (error.isEmpty()) {
			String toResp;
			toResp = "<rootElement>\n";
			toResp += "    <groups>\n";
			for (AndroidGroup group : ctx.getAvailableGroups()) {
				toResp += "        <group>\n";
				toResp += "            <name>" + group.getName() + "</name>\n";
				toResp += "        </group>\n";
			}
			toResp += "    </groups>\n";

			toResp += "    <repositories>\n";
			for (AndroidRepository repository : ctx.getAvailableRepositories()) {
				toResp += "        <repository>\n";
				toResp += "            <name>" + repository.getName() + "</name>\n";
				toResp += "        </repository>\n";
			}
			toResp += "    </repositories>\n";
			toResp += "</rootElement>";

			return toResp;
		}
		else {
			String toResp;
			toResp = "<error>" + error + "</error>";

			return toResp;
		}
	}
}
