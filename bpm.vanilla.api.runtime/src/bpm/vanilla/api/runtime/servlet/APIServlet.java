package bpm.vanilla.api.runtime.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import bpm.vanilla.api.core.IAPIManager;
import bpm.vanilla.api.core.IAPIManager.IAPIType;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.runtime.ComponentAPI;
import bpm.vanilla.api.runtime.service.FWRService;
import bpm.vanilla.api.runtime.service.KPIService;
import bpm.vanilla.api.runtime.service.MetadataService;
import bpm.vanilla.api.runtime.service.OLAPService;
import bpm.vanilla.api.runtime.service.RepositoriesService;
import bpm.vanilla.api.runtime.service.ViewerService;

public class APIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ComponentAPI component;
	private MetadataService metadataService;
	private RepositoriesService repositoriesService;
	private ViewerService viewerService;
	private OLAPService olapService;
	private FWRService fwrService;
	private KPIService kpiService;

	public APIServlet(ComponentAPI component) {
		this.component = component;
		this.metadataService = new MetadataService();
		this.repositoriesService = new RepositoriesService();
		this.viewerService = new ViewerService();
		this.olapService = new OLAPService();
		this.fwrService = new FWRService();
		this.kpiService = new KPIService();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");

			JSONObject jsonObject = extractJson(req);
			IAPIType type = !jsonObject.isNull(IAPIManager.PARAM_TYPE_API) ? IAPIType.valueOf(jsonObject.getString(IAPIManager.PARAM_TYPE_API)) : null;
			String method = !jsonObject.isNull(IAPIManager.PARAM_METHOD_API) ? jsonObject.getString(IAPIManager.PARAM_METHOD_API) : null;
			JSONArray parameters = !jsonObject.isNull(IAPIManager.PARAM_PARAMETERS_API) ? jsonObject.getJSONArray(IAPIManager.PARAM_PARAMETERS_API) : null;

			if (type == null) {
				// TODO: Manage error
			}

			String jsonResult = null;
			switch (type) {
			case USER:
				// TODO
				break;
			case KPI:
				jsonResult = kpiService.dispatchAction(method, parameters);
				break;
			case METADATA:
				jsonResult = metadataService.dispatchAction(method, parameters);
				break;
			case REPOSITORIES:
				jsonResult = repositoriesService.dispatchAction(method, parameters);
				break;
			case VIEWER:
				jsonResult = viewerService.dispatchAction(method, parameters);
				break;
			case OLAP:
				jsonResult = olapService.dispatchAction(method, parameters);
				break;
			case FWR:
				jsonResult = fwrService.dispatchAction(method, parameters);
				break;
			}

			PrintWriter out = resp.getWriter();
			out.print(jsonResult);
			out.flush();
			out.close();
		} catch (VanillaApiException e) {
			component.getLogger().error(e.getMessage(), e);

			// TODO: Manage exceptions
			String jsonResult = "{\"error\": " + e.getErrorCode() + "}";
			// String jsonResult = "error";

			PrintWriter out = resp.getWriter();
			out.print(jsonResult);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();

			component.getLogger().error(e.getMessage(), e);

			// TODO: Manage exceptions
			String jsonResult = "{\"error\" : 42}"; // Unknwon error code
			// String jsonResult = "error";

			PrintWriter out = resp.getWriter();
			out.print(jsonResult);
			out.flush();
			out.close();
		}
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
}
