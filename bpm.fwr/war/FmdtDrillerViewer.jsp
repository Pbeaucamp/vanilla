<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.io.FileReader"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="bpm.repository.api.model.IRepositoryConnection"%>
<%@page import="bpm.repository.api.model.FactoryRepository"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.util.Collection"%>
<%@page import="bpm.metadata.layer.business.IBusinessModel"%>
<%@page import="bpm.metadata.MetaDataReader"%>
<%@page import="java.util.Iterator"%>
<%@page import="bpm.metadata.layer.business.IBusinessPackage"%>
<%@page import="bpm.fmdtDrillerFlex.generator.FmdtDrillerFlexGenerator"%>
<%@page import="bpm.repository.api.model.IDirectoryItem"%>
<%@page import="org.apache.commons.io.IOUtils"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<% 			// get parameter
			//String groupName = (String) request.getParameter("grp");
			//String busiModName = (String) request.getParameter("bmod");
			//String pkgName = (String) request.getParameter("pkg");
			try {
				IBusinessPackage pkg = (IBusinessPackage) request.getSession().getAttribute("_current_pack");
				String groupName = (String) request.getSession().getAttribute("_current_group");
				
				FmdtDrillerFlexGenerator generator = new FmdtDrillerFlexGenerator(groupName, pkg);
				String contextPath = getServletContext().getContextPath();
				String filename = "driller_model_" + new Object().hashCode() + ".xml";
				String filepath = getServletContext().getRealPath("/temp/" + filename);
				File f = new File(filepath);
				f.createNewFile();
				String dataUrl = request.getScheme()+ "://" +
								 request.getServerName()+ ":"+
								 request.getServerPort()+
								 request.getContextPath()+
								 "/temp/" + filename;
				
				generator.generate(filepath);
				response.sendRedirect("FlexViewer/FMDTViewer.jsp?data_url="+dataUrl);
				
			
			
			} catch (Exception e) {
				e.printStackTrace();
			}
    %>
</body>
</html>