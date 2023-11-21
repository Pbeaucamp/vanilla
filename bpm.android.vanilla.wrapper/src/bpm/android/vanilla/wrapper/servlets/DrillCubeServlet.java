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

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class DrillCubeServlet
 */
public class DrillCubeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DrillCubeServlet(ComponentAndroidWrapper component) {
		super();
		this.component = component;
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
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();

			resp = prepareResponse(req, session);
		} catch (Exception ex) {
			component.getLogger().error("Error when drilling cube - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}

	private String prepareResponse(String req, SessionContent session) {
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		String uniqueName = parseRequest(req);

		String response = "";
		try {
			String html = manager.drillCube(uniqueName);
			response = prepareXml(html, "");
		} catch (Throwable e) {
			e.printStackTrace();
			prepareXml("", e.toString());
		}

		return response;
	}

	private String parseRequest(String req) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(req);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("userEntry")) {
			if (e.element("uniquename") != null) {
				Element d = e.element("uniquename");
				if (d != null) {
					return d.getStringValue();
				}
			}
		}
		return "";
	}

	private String prepareXml(String html, String error) {
		if (error.equals("")) {
			String toResp;
			toResp = "<rootElement>\n";
			toResp += "    <htmlpage>\n";
			toResp += html;
			toResp += "    </htmlpage>\n";
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
