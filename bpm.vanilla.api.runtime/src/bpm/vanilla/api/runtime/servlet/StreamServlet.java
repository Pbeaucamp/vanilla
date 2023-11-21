package bpm.vanilla.api.runtime.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.vanilla.api.core.IAPIManager;
import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.runtime.service.FWRService;
import bpm.vanilla.api.runtime.service.ViewerService;
import bpm.vanilla.platform.core.utils.IOWriter;

public class StreamServlet extends HttpServlet {

	private static final long serialVersionUID = -7156481441321370835L;
	
	private ViewerService viewerService;
	private FWRService fwrService;
	
	public StreamServlet() {
		this.viewerService = new ViewerService();
		this.fwrService = new FWRService();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			JSONObject jsonObject = extractJson(req);
			IAPIType type = !jsonObject.isNull(IAPIManager.PARAM_TYPE_API) ? IAPIType.valueOf(jsonObject.getString(IAPIManager.PARAM_TYPE_API)) : null;
			String method = !jsonObject.isNull(IAPIManager.PARAM_METHOD_API) ? jsonObject.getString(IAPIManager.PARAM_METHOD_API) : null;
			JSONArray parameters = !jsonObject.isNull(IAPIManager.PARAM_PARAMETERS_API) ? jsonObject.getJSONArray(IAPIManager.PARAM_PARAMETERS_API) : null;

			if (type == null) {
				//TODO:  Manage error
			}
			
			switch (type) {
			case VIEWER: 
				serializeReport(viewerService.dispatchStreamAction(method, parameters), resp.getOutputStream());
			case KPI:
			case METADATA:
			case REPOSITORIES:
			case USER:
			case OLAP:
			case FWR:
				serializeReport(fwrService.dispatchStreamAction(method, parameters), resp.getOutputStream());
			default:
				break;
			}

			try {
				resp.getWriter().close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();

			InputStream errorStream = createHTMLErrorResponse(e);

			ServletOutputStream out = resp.getOutputStream();
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = errorStream.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			errorStream.close();
			out.close();
		}
	}

	private InputStream createHTMLErrorResponse(Throwable caught) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("	<head></head>");
		buf.append("	<body>");
		buf.append("		<h1>Informations</h1>");
		buf.append("		<p>This document is not available at this time. Sorry for the inconveniance. <br/> The following informations can help you understand the problem :</p>");
		buf.append("		<p style=\"margin: 15px; background-color: #E6E6E6; padding: 20px; font-size: 12px;\">" + ExceptionUtils.getStackTrace(caught).replace("\n", "<br/>") + "</p>");
		buf.append("	</body>");
		buf.append("</html>");
		return IOUtils.toInputStream(buf.toString());
	}

	private JSONObject extractJson(HttpServletRequest req) throws Exception {
		try (InputStream is = req.getInputStream(); BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return new JSONObject(total.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void serializeReport(InputStream is, OutputStream os) throws Exception{
		IOWriter.write(is, os, true, false);
	}
}
