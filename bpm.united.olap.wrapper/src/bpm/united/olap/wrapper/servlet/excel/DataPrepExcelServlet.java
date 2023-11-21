package bpm.united.olap.wrapper.servlet.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import bpm.united.olap.wrapper.UnitedOlapWrapperComponent;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.utils.IOWriter;

public class DataPrepExcelServlet extends HttpServlet {

	private UnitedOlapWrapperComponent component;
	
	public DataPrepExcelServlet(UnitedOlapWrapperComponent unitedOlapWrapperComponent) {
		this.component = unitedOlapWrapperComponent;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		DataPrepSession session = null;
		String result = null;
		
		try {
			//Get the session back or create a new one
			Object objSess = req.getSession(true).getAttribute("excelSession");
			if(objSess != null) {
				session = (DataPrepSession) objSess;
			}
			else {
				session = new DataPrepSession();
			}
			
			String requestType =null;
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			if (ServletFileUpload.isMultipartContent(req))
	        {
				 List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
				 map = returnParameters(items);
				 requestType = map.get(DataPrepExcelConstants.REQUEST_TYPE_PARAMETER).toString();
				
	        } else
	        	requestType = req.getParameter(DataPrepExcelConstants.REQUEST_TYPE_PARAMETER);

			
			if(requestType.equals(DataPrepExcelConstants.REQUEST_LOADCONSUP)) {
				session = new DataPrepSession();
				result = ConnectionHelper.loadConSup(req.getParameter(DataPrepExcelConstants.PARAMETER_USER), req.getParameter(DataPrepExcelConstants.PARAMETER_PASS), session);
			}
			else
			if(requestType.equals(DataPrepExcelConstants.REQUEST_SAVEDATAPREP)) {
				result = ConnectionHelper.saveDataPrep(req.getParameter(DataPrepExcelConstants.PARAMETER_USER), req.getParameter(DataPrepExcelConstants.PARAMETER_PASS), session);
			}
			else
			if(requestType.equals(DataPrepExcelConstants.REQUEST_CONNECT)) {
				result = ConnectionHelper.connect(req.getParameter(DataPrepExcelConstants.PARAMETER_USER), req.getParameter(DataPrepExcelConstants.PARAMETER_PASS), session);
			}
			else
			if(requestType.equals(DataPrepExcelConstants.REQUEST_LOADDATA)) {	
				InputStream out = ConnectionHelper.loadData(req.getParameter(DataPrepExcelConstants.PARAMETER_USER), req.getParameter(DataPrepExcelConstants.PARAMETER_PASS), req.getParameter(DataPrepExcelConstants.PARAMETER_CONTRACTID), session);
				IOWriter.write(out, resp.getOutputStream(), true, true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (session==null )
			result="Error";
		req.getSession(true).setAttribute("excelSession", session);
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
	            	map.put(DataPrepExcelConstants.PARAMETER_FILE, item.getInputStream());
	            }
	        }
	      return map;  
	      
		}catch (Exception e) {
			e.printStackTrace();
			//logger.error("returnParameters function failed", e);
			return null;		
		}	
	}
}
