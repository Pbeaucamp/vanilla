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
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.cube.AndroidMeasureGroup;
import bpm.android.vanilla.core.beans.cube.MeasureAndCol;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class ChangeMeasureServlet
 */
public class ChangeMeasureServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChangeMeasureServlet(ComponentAndroidWrapper component) {
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
			component.getLogger().error("Error when changing measure - " + ex.getMessage(), ex);
			resp = ex.toString();
		}

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}

	private String prepareResponse(String req, SessionContent session) {
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		List<MeasureAndCol> selectedMeas = parseRequest(req);

		String response = "";
		try {
			AndroidCube cube = manager.changeMeasures(null, selectedMeas);
			if (!cube.getHtml().equals("")) {
				response = prepareXml(cube.getHtml(), "");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			prepareXml("", e.toString());
		}

		return response;
	}

	private List<MeasureAndCol> parseRequest(String req) {
		Document document = null;
		List<MeasureAndCol> selectedMeas = new ArrayList<MeasureAndCol>();
		try {
			document = DocumentHelper.parseText(req);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("userEntry")) {
			for (Element g : (List<Element>) e.elements("cube")) {
				if (g.element("selectedmeasures") != null) {
					for (Element h : (List<Element>) g.elements("selectedmeasures")) {
						for (Element k : (List<Element>) h.elements("measure")) {
							String uniqueName = "";
							String measureGroupName = "";
							Boolean isCol = false;
							Boolean toAdd = false;
							Boolean toRemove = false;
							if (k.element("uniquename") != null) {
								Element d = k.element("uniquename");
								if (d != null) {
									uniqueName = d.getStringValue();
								}
							}
							if (k.element("iscol") != null) {
								Element d = k.element("iscol");
								if (d != null) {
									isCol = Boolean.parseBoolean(d.getStringValue());
								}
							}
							if (k.element("toadd") != null) {
								Element d = k.element("toadd");
								if (d != null) {
									toAdd = Boolean.parseBoolean(d.getStringValue());
								}
							}
							if (k.element("toremove") != null) {
								Element d = k.element("toremove");
								if (d != null) {
									toRemove = Boolean.parseBoolean(d.getStringValue());
								}
							}
							if (k.element("measuregroup") != null) {
								Element d = k.element("measuregroup");
								if (d != null) {
									measureGroupName = d.getStringValue();
								}
							}
							AndroidMeasureGroup measure = new AndroidMeasureGroup(measureGroupName, measureGroupName);
							MeasureAndCol meaAndCol = new MeasureAndCol(uniqueName, uniqueName, isCol, true, measure);
							meaAndCol.setToAdd(toAdd);
							meaAndCol.setToRemove(toRemove);
							selectedMeas.add(meaAndCol);
						}
					}
				}
			}
		}
		return selectedMeas;
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
