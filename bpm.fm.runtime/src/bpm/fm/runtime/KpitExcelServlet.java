package bpm.fm.runtime;

import java.io.IOException;
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

import com.thoughtworks.xstream.XStream;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.runtime.helper.KpiExcelConstants;
import bpm.fm.runtime.helper.KpiExcelHelper;
import bpm.fm.runtime.helper.Session;
import bpm.vanilla.platform.logging.IVanillaLogger;


public class KpitExcelServlet extends HttpServlet{

	private IVanillaLogger logger;
	
	private KpiExcelHelper helper;
	
	private XStream xstream;
	
	public KpitExcelServlet(FreeMetricsManagerComponent componentProvider, IVanillaLogger logger){
		this.logger = logger;
		//this.component = componentProvider;
		this.helper=new KpiExcelHelper(componentProvider);
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Session session = (Session)req.getSession().getAttribute("_Session");
		String result = null;
		try {
			//Get the session back or create a new one
			Object objSess = session;//req.getSession(true).getAttribute("excelSession");
			if(objSess != null) {
				session = (Session) objSess;
			}
			else {
				session = new Session();
			}
			
			String requestType =null;
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			if (ServletFileUpload.isMultipartContent(req))
	        {
				 List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
				 map = returnParameters(items);
				 requestType = map.get(KpiExcelConstants.REQUEST_TYPE_PARAMETER).toString();
				
	        } else
	        	requestType = req.getParameter(KpiExcelConstants.REQUEST_TYPE_PARAMETER);
			
			
			if(requestType.equals(KpiExcelConstants.REQUEST_LOADGROUP))
			{
				result = helper.loadGroups(req.getParameter(KpiExcelConstants.PARAMETER_USER), req.getParameter(KpiExcelConstants.PARAMETER_PASS), session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_LOADOBSERVATORIES)) {
				result = helper.loadObservatories(req.getParameter(KpiExcelConstants.PARAMETER_GROUPID), session);
			}else
			if(requestType.equals(KpiExcelConstants.REQUEST_LOADTHEME)) {				
				result = helper.loadTheme(req.getParameter(KpiExcelConstants.PARAMETER_OBSERVATORYID), session);
			}else
			if(requestType.equals(KpiExcelConstants.REQUEST_SAVEPAMETERS)) {			
				result = helper.saveParam(req.getParameter(KpiExcelConstants.PARAMETER_GROUPID), req.getParameter(KpiExcelConstants.PARAMETER_OBSERVATORYID), req.getParameter(KpiExcelConstants.PARAMETER_THEMEID), session);
			}
			if(requestType.equals(KpiExcelConstants.REQUEST_GETAXIS)) {				
				result = helper.getAxis(session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_LOADAXIS)) {					
				result = helper.loadAxis(req.getParameter(KpiExcelConstants.PARAMETER_AXISID), session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_UPDATEAXIS)) {					
				result = helper.updateValue(req.getParameter(KpiExcelConstants.PARAMETER_AXISID), req.getParameter(KpiExcelConstants.PARAMETER_LISTDELETE), req.getParameter(KpiExcelConstants.PARAMETER_LISTUPDATE), req.getParameter(KpiExcelConstants.PARAMETER_LISTINSERT), session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_GETMETRICS)) {					
				result = helper.getMetrics(session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_LOADMETRICVALUE)) {					
				result = helper.loadMetricValue(req.getParameter(KpiExcelConstants.PARAMETER_METRICID), req.getParameter(KpiExcelConstants.PARAMETER_DATEOFVALUES), session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_GETLISTMEMBER)) {					
				result = helper.getListMember(req.getParameter(KpiExcelConstants.PARAMETER_AXISID), session);
			}
			else
			if(requestType.equals(KpiExcelConstants.REQUEST_UPDATEMETRICVALUE)) {					
				result = helper.updateMetricValue(req.getParameter(KpiExcelConstants.PARAMETER_METRICID), req.getParameter(KpiExcelConstants.PARAMETER_DATEOFVALUES), req.getParameter(KpiExcelConstants.PARAMETER_METRICVALUES), session);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (session==null )
			result="Error";
		
		req.getSession().setAttribute("_Session", session);
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.print(result);
		
	}
	
	
	
	
	public static HashMap<String, Object> returnParameters (List<FileItem> items) throws IOException{	
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
	        for (FileItem item : items) {
	            if (item.isFormField()) {
	               map.put(item.getFieldName(), item.getString("UTF-8")) ;
	            } 
	            else {
	            	map.put(KpiExcelConstants.PARAMETER_FILE, item.getInputStream());
	            }
	        }
	      return map;  
	      
		}catch (Exception e) {
			e.printStackTrace();
			//logger.error("returnParameters function failed", e);
			return null;		
		}	
	}



	private HashMap<String, String> objectToString(HashMap<String, Object> map ){
		HashMap<String, String> documentParameters =new HashMap<String, String>();
		
		for (String key : map.keySet()){
			if ( map.get(key) instanceof String)
				if (map.get(key)!=null)
					documentParameters.put(key, map.get(key).toString());
				else
					documentParameters.put(key,null);
		}	
		return documentParameters;
	}



	private HashMap<String, String> mapToHashMap(Map<String, String[]> map ){
		HashMap<String, String> documentParameters =new HashMap<String, String>();
		
		for (String key : map.keySet()){
				if (map.get(key)!=null)
					documentParameters.put(key, map.get(key)[0]);
				else
					documentParameters.put(key,null);
		}	
		return documentParameters;
	}
	
}
