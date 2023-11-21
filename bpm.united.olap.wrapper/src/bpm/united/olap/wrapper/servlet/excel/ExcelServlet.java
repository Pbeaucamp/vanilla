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

import bpm.office.core.ExcelFunctionsUtils;
import bpm.united.olap.wrapper.UnitedOlapWrapperComponent;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.utils.IOWriter;

public class ExcelServlet extends HttpServlet {
	
	//FIXME : Remove this, for test only
	//handle the sessions properly
	private ExcelSession testSession;
	
	
	private UnitedOlapWrapperComponent component;
	
	public ExcelServlet(UnitedOlapWrapperComponent unitedOlapWrapperComponent) {
		this.component = unitedOlapWrapperComponent;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ExcelSession session = null;
		String result = null;
		Boolean sendResult =true;
		
		try {
			//Get the session back or create a new one
			
			Object objSess = testSession;//req.getSession(true).getAttribute("excelSession");
			if(objSess != null) {
				session = (ExcelSession) objSess;
			}
			else {
				session = new ExcelSession();
			}
			
			String requestType =null;
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			if (ServletFileUpload.isMultipartContent(req))
	        {
				 List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
				 map = CubeHelper.returnParameters(items);
				 requestType = map.get(VanillaConstantsForFMDT.REQUEST_TYPE_PARAMETER).toString();
				
	        } else
	        	requestType = req.getParameter(VanillaConstantsForFMDT.REQUEST_TYPE_PARAMETER);
			
			
		//	String requestType = req.getParameter(ExcelConstantes.REQUEST_TYPE_PARAMETER);
			
			
			
			//Load group and repositories
			if(requestType.equals(ExcelConstantes.REQUEST_LOADGRPREP)) {
				session = new ExcelSession();
				result = ConnectionHelper.loadGroupAndRepositories(req.getParameter(ExcelConstantes.PARAMETER_USER), req.getParameter(ExcelConstantes.PARAMETER_PASS), session);
			}
			else
			if(requestType.equals(ExcelConstantes.REQUEST_SAVEGRPREP)) {
				result = ConnectionHelper.saveGroupAndRepositories(req.getParameter(ExcelConstantes.PARAMETER_USER), req.getParameter(ExcelConstantes.PARAMETER_PASS), 
						req.getParameter(ExcelConstantes.PARAMETER_REPID), req.getParameter(ExcelConstantes.PARAMETER_GROUPID), session);
			}
	
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADFASD)) {
				result = ConnectionHelper.loadFasdItems(session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADCUBENAME)) {
				result = ConnectionHelper.loadCubeNames(req.getParameter(ExcelConstantes.PARAMETER_FASDID), session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADVIEWNAME)) {
				result = ConnectionHelper.loadViewNames(req.getParameter(ExcelConstantes.PARAMETER_CUBENAME), req.getParameter(ExcelConstantes.PARAMETER_FASDID), session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADCUBE)) {
				result = ConnectionHelper.loadCube(req.getParameter(ExcelConstantes.PARAMETER_CUBENAME),req.getParameter(ExcelConstantes.PARAMETER_FASDID), session);
				
			}
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADVIEW)) {
				result = ConnectionHelper.loadView(req.getParameter(ExcelConstantes.PARAMETER_VIEWID), req.getParameter(ExcelConstantes.PARAMETER_FASDID), req.getParameter(ExcelConstantes.PARAMETER_CUBENAME), session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADFILTERS)) {
				result = ConnectionHelper.loadFilters(session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_GETLISTFILTERS)) {
				result = CubeHelper.getListFilters(session);
			}
			else if(requestType.equals(ExcelConstantes.REQUEST_GETVIEW)) {
				result = CubeHelper.getView(session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADCURRENTVIEW)) {
				result = ConnectionHelper.loadCurrentView(req.getParameter(ExcelConstantes.PARAMETER_VIEW), session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_DRILL)) {
				result = CubeHelper.drillPerUname(req.getParameter(ExcelConstantes.PARAMETER_ITEMUNAME),session.getCube());
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_DRILLALL)) {
					result = CubeHelper.drillAllPerUname(req.getParameter(ExcelConstantes.PARAMETER_ITEMUNAME),session.getCube());
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_CUBEACTION)) {
				result = CubeHelper.executeAction(req.getParameter(ExcelConstantes.REQUEST_ACTION_TYPE_PARAMETER), session, req);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_CUBESTRUCTURE)) {
				result = CubeHelper.getCubeStructure(session);
			}
			else if(requestType.equals(ExcelConstantes.REQUEST_CUBEMEASURES)) {
				result = CubeHelper.getMeasure(session);
			}
			else if(requestType.equals(ExcelConstantes.REQUEST_GETSUBMEMBERS)) {
				result = CubeHelper.getSubMembers(req.getParameter(ExcelConstantes.PARAMETER_ITEMUNAME), session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_CLEARCUBE)) {
				result = CubeHelper.clearCube(session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_REFRESHCUBE)) {
				result = CubeHelper.refreshCube1(session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_SAVE)) {
				ConnectionHelper.saveCubeView(req.getParameter(ExcelConstantes.PARAMETER_VIEWNAME), "", "", "", session.getGroup().getId(), session.getGroup().getName(), session);
			}
			
			else if(requestType.equals(ExcelConstantes.REQUEST_LOADDIRS)) {
				result = ConnectionHelper.loadDirectories(session);
			}
			
			else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_LOADFILE)){
				InputStream out = CubeHelper.downloadFile(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), session);
				IOWriter.write(out, resp.getOutputStream(), true, true);	
				sendResult=false;
		}
		else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_DISPLAYDIR)){
			result= CubeHelper.generateXML(req.getParameter(VanillaConstantsForFMDT.PARAMETER_DOCTYPE), session);
		}
		else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_CREATEDIR)){
			result= CubeHelper.createDir(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILENAME), session);
		}
		else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_DELETEDIR)){
			result= CubeHelper.deleteDir(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), session);
		}
		else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_DELETEDOCUMENT)){
			result= CubeHelper.deleteItem(req.getParameter(VanillaConstantsForFMDT.PARAMETER_ITEMID), session);
		}
		else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_UPLOADFILE)){
			HashMap<String, String> documentParameters =ExcelFunctionsUtils.objectToString(map);
			
			InputStream contractFile = (InputStream) ((req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE)!=null) ? req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) : map.get(VanillaConstantsForFMDT.PARAMETER_FILE)) ;
			
			String name= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILENAME);
			
			String itemId= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_ITEMID);
			
			result= CubeHelper.addDocument(itemId, name, contractFile, session);
		}
		else if(requestType.equals(VanillaConstantsForFMDT.REQUEST_UPDATEFILE)){
			HashMap<String, String> documentParameters =ExcelFunctionsUtils.objectToString(map);
			
			InputStream contractFile = (InputStream) ((req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE)!=null) ? req.getParameter(VanillaConstantsForFMDT.PARAMETER_FILE) : map.get(VanillaConstantsForFMDT.PARAMETER_FILE)) ;
			
			String name= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILENAME);
			
			String itemId= documentParameters.get(VanillaConstantsForFMDT.PARAMETER_ITEMID);
			
			result= CubeHelper.updateDocument(itemId, name, contractFile, session);
		}
			
			//Store the session
			//req.getSession(true).setAttribute("excelSession", session);
			testSession = session;
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (session==null )
			result="Error";
		
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.print(result);
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
}
