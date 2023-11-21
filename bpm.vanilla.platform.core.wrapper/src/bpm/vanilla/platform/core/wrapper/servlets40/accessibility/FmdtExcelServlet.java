package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import bpm.office.core.ExcelFunctionsUtils;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.wrapper.servlets40.helper.FmdtHelper;
import bpm.vanilla.platform.core.wrapper.servlets40.helper.Session;

public class FmdtExcelServlet extends HttpServlet {
	private Session testSession;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Boolean sendResult = true;
		Session session = (Session) req.getSession().getAttribute("_Session");
		String result = null;
		try {
			// Get the session back or create a new one
			Object objSess = session;// req.getSession(true).getAttribute("excelSession");
			if (objSess != null) {
				session = (Session) objSess;
			} else {
				session = new Session();
			}

			String requestType = null;
			HashMap<String, Object> map = new HashMap<String, Object>();

			if (ServletFileUpload.isMultipartContent(req)) {
				List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
				map = FmdtHelper.returnParameters(items);
				requestType = map.get(VanillaConstantsForFMDT.REQUEST_TYPE_PARAMETER).toString();

			} else
				requestType = req.getParameter(VanillaConstantsForFMDT.REQUEST_TYPE_PARAMETER);

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADGRPREPANDREPOSITORIES)) {
				session = new Session();
				result = FmdtHelper.loadGroupAndRepositories(req.getParameter(VanillaConstantsForFMDT.PARAMETER_USER), req.getParameter(VanillaConstantsForFMDT.PARAMETER_PASS), session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETLANGUAGE)) {
				result = FmdtHelper.getlocales();
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_SAVEGRPREP)) {
				result = FmdtHelper.saveGroupAndRepositories(req.getParameter(VanillaConstantsForFMDT.PARAMETER_REPID), req.getParameter(VanillaConstantsForFMDT.PARAMETER_GROUPID), req.getParameter(VanillaConstantsForFMDT.PARAMETER_LANGUAGE), session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_SAVEMODELPACKAGE)) {
				result = FmdtHelper.saveModelPackage(req.getParameter(VanillaConstantsForFMDT.PARAMETER_METADATAID), req.getParameter(VanillaConstantsForFMDT.PARAMETER_MODELNAME), req.getParameter(VanillaConstantsForFMDT.PARAMETER_PACKAGENAME), session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETSERVERCONNECTION)) {
				result = FmdtHelper.getServerConnection(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADMETADATA)) {
				result = FmdtHelper.loadMetadata(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADMODEL)) {
				result = FmdtHelper.loadBusinessModel(req.getParameter(VanillaConstantsForFMDT.PARAMETER_METADATAID), session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE)) {
				result = FmdtHelper.loadBusinessPackage(req.getParameter(VanillaConstantsForFMDT.PARAMETER_MODELNAME), req.getParameter(VanillaConstantsForFMDT.PARAMETER_METADATAID), session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADTABLENAME)) {
				result = FmdtHelper.loadTableName(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADPROMPTNAME)) {
				result = FmdtHelper.loadPrompt(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADCOMPLEXFILTERS)) {
				result = FmdtHelper.loadComplexfilter(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADSQLFILTERS)) {
				result = FmdtHelper.loadSQLfilter(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADFILTERS)) {
				result = FmdtHelper.loadfilter(session);
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADCOLUMNNAME)) {
				result = FmdtHelper.loadColumns(req.getParameter(VanillaConstantsForFMDT.PARAMETER_TABLENAME), session);

			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_TREATFORMULAELEMENT)) {
				result = FmdtHelper.treatFormulasElement(req.getParameter(VanillaConstantsForFMDT.PARAMETER_FORMULA), session);

			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETPROMPTVALUES)) {
				result = FmdtHelper.getPromptValue(req.getParameter(VanillaConstantsForFMDT.PARAMETER_LISTCOLUMNS), req.getParameter(VanillaConstantsForFMDT.PARAMETER_TABLENAME), req.getParameter(VanillaConstantsForFMDT.PARAMETER_VALUE), session);

			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_EXECUTEQUERY)) {
				result = FmdtHelper.buildQuery(req.getParameter(VanillaConstantsForFMDT.PARAMETER_LISTCOLUMNS), req.getParameter(VanillaConstantsForFMDT.PARAMETER_LISTAGGREGATES), req.getParameter(VanillaConstantsForFMDT.PARAMETER_LISTPROMPT), req.getParameter(VanillaConstantsForFMDT.PARAMETER_LISTFILTERS), req.getParameter(VanillaConstantsForFMDT.PARAMETER_LISTCONDITIONS), req.getParameter(VanillaConstantsForFMDT.PARAMETER_FORMULA), req.getParameter(VanillaConstantsForFMDT.PARAMETER_ORDONABLE), req.getParameter(VanillaConstantsForFMDT.PARAMETER_DISTINCT), req.getParameter(VanillaConstantsForFMDT.PARAMETER_LIMIT), session);

			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETDRIVERS)) {
				result = FmdtHelper.getDrivers();
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETTABLES)) {
				result = FmdtHelper.getTables(mapToHashMap(req.getParameterMap()));
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETCOLUMNS)) {
				result = FmdtHelper.getColumns(mapToHashMap(req.getParameterMap()));
			} else

			if (requestType.equals(VanillaConstantsForFMDT.REQUEST_CONNECTSERVER)) {
				result = FmdtHelper.testConnectionServer(mapToHashMap(req.getParameterMap()));
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_CREATETABLE)) {
				result = FmdtHelper.createTable(req.getParameter(VanillaConstantsForFMDT.PARAMETER_SCRIPT), mapToHashMap(req.getParameterMap()));
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_GETCOLUMNSTYPE)) {
				result = FmdtHelper.getColumnType(mapToHashMap(req.getParameterMap()));
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADEXCEL)) {

				HashMap<String, String> documentParameters = ExcelFunctionsUtils.objectToString(map);

				InputStream contractFile = (InputStream) ((req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) != null) ? req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) : map.get(VanillaConstantsForFMDT.PARAMETER_FILE));

				result = FmdtHelper.AddContract(documentParameters, contractFile, session);
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADFILE)) {
				InputStream out = FmdtHelper.downloadFile(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), session);
				IOWriter.write(out, resp.getOutputStream(), true, true);
				sendResult = false;
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_DISPLAYDIR)) {
				result = FmdtHelper.generateXML(req.getParameter(VanillaConstantsForFMDT.PARAMETER_DOCTYPE), session);
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_CREATEDIR)) {
				result = FmdtHelper.createDir(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILENAME), session);
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_DELETEDIR)) {
				result = FmdtHelper.deleteDir(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), session);
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_DELETEDOCUMENT)) {
				result = FmdtHelper.deleteItem(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), session);
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_UPLOADFILE)) {
				HashMap<String, String> documentParameters = ExcelFunctionsUtils.objectToString(map);

				InputStream contractFile = (InputStream) ((req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) != null) ? req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) : map.get(VanillaConstantsForFMDT.PARAMETER_FILE));

				String name = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILENAME);

				String itemId = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_ITEMID);

				result = FmdtHelper.addDocument(itemId, name, contractFile, session);
			} else if (requestType.equals(VanillaConstantsForFMDT.REQUEST_UPDATEFILE)) {
				HashMap<String, String> documentParameters = ExcelFunctionsUtils.objectToString(map);

				InputStream contractFile = (InputStream) ((req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) != null) ? req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) : map.get(VanillaConstantsForFMDT.PARAMETER_FILE));

				String name = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILENAME);

				String itemId = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_ITEMID);

				result = FmdtHelper.updateDocument(itemId, name, contractFile, session);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (session == null)
			result = "Error";

		if (sendResult) {
			req.getSession().setAttribute("_Session", session);
			ServletOutputStream outputStream = resp.getOutputStream();
			outputStream.print(result);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private HashMap<String, String> mapToHashMap(Map<String, String[]> map) {
		HashMap<String, String> documentParameters = new HashMap<String, String>();

		for (String key : map.keySet()) {
			if (map.get(key) != null)
				documentParameters.put(key, map.get(key)[0]);
			else
				documentParameters.put(key, null);
		}
		return documentParameters;
	}

}
