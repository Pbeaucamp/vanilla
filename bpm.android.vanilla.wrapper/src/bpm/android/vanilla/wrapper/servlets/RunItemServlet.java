package bpm.android.vanilla.wrapper.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.core.beans.report.IRunItem;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class RunItemServlet
 */
public class RunItemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RunItemServlet(ComponentAndroidWrapper component) {
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
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();

			resp = prepareResponse(req, session);
		} catch (Exception ex) {
			component.getLogger().error("Error when running Item - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}

	private String prepareResponse(String req, SessionContent session) {
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		AndroidItem item = getItem(req);
		String outputFormat = getOutputFormat(req);

		String response = "";
		try {
			IRunItem runItem = manager.runItem(item, outputFormat);

			response = prepareXml(runItem, "");
		} catch (Throwable e) {
			e.printStackTrace();
			try {
				prepareXml(null, e.toString());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return response;
	}

	private AndroidItem getItem(String req) {

		int selectedItemId = -1;
		List<Parameter> params = new ArrayList<Parameter>();

		Document document = null;
		try {
			document = DocumentHelper.parseText(req);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("userEntry")) {
			try {

				if (e.element("selectedItem") != null) {
					Element d = e.element("selectedItem").element("id");
					if (d != null) {
						selectedItemId = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("parameters") != null) {
					for (Element g : (List<Element>) e.elements("parameters")) {
						if (g.element("param") != null) {
							for (Element h : (List<Element>) g.elements("param")) {
								String name = "";
								String value = "";
								if (h.element("name") != null) {
									Element d = h.element("name");
									if (d != null) {
										name = d.getStringValue();
									}
								}
								if (h.element("value") != null) {
									Element d = h.element("value");
									if (d != null) {
										value = d.getStringValue();
									}
								}
								if (!name.equals("") && !value.equals("")) {
									Parameter param = new Parameter(name, new LinkedHashMap<String, String>());
									param.setSelectedValue(value);
									
									params.add(param);
								}
							}
						}
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		
		AndroidItem item = new AndroidItem(selectedItemId, "", 0, 0, params);
		return item;
	}

	private String getOutputFormat(String req) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(req);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("userEntry")) {
			try {
				if (e.element("output") != null) {
					Element d = e.element("output");
					if (d != null) {
						return d.getStringValue();
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}
		return "html";
	}

	private String prepareXml(IRunItem runItem, String error) {
		if (error.equals("")) {
			String toResp;
			toResp = "<rootElement>\n";
			toResp += "    <previewitem>\n";
			toResp += "        <htmlpage>\n" + runItem.getHtml() + "        </htmlpage>\n";
			toResp += "        <previewtype>" + runItem.getOutputFormat() + "</previewtype>\n";
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

}
