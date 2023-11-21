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
import bpm.android.vanilla.core.beans.report.AndroidDocumentDefinition;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class LoadItemInGedServlet
 */
public class LoadItemInGedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoadItemInGedServlet(ComponentAndroidWrapper component) {
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
			component.getLogger().error("Error when Loading Item from Ged - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}

	private String prepareResponse(String request, SessionContent session) {
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		AndroidDocumentDefinition definition = getAndroidDocument(request);

		String response = "";
		try {
			String req = manager.loadItemInGed(definition);

			response = prepareXml(req, "");
		} catch (Exception e) {
			e.printStackTrace();
			response = prepareXml("", e.toString());
		}
		return response;
	}

	private AndroidDocumentDefinition getAndroidDocument(String request) {
		int documentId = setUsersEntry(request);
		
		AndroidDocumentDefinition doc = new AndroidDocumentDefinition();
		doc.setId(documentId);
		return doc;
	}

	private String prepareXml(String html, String error) {
		if (error.equals("")) {
			String toResp;
			toResp = "<rootElement>\n";
			toResp += "    <previewitem>\n";
			toResp += "        <htmlpage>\n" + html + "        </htmlpage>\n";
			toResp += "    </previewitem>\n";
			toResp += "</rootElement>";

			return toResp;
		}
		else {
			String toResp;
			toResp = "<error>" + error + "</error>";

			return toResp;
		}
	}

	public int setUsersEntry(String req) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(req);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("userEntry")) {
			try {
				if (e.element("documentid") != null) {

					Element d = e.element("documentid");
					if (d != null) {
						return Integer.parseInt(d.getStringValue());
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}
		return -1;
	}

}
