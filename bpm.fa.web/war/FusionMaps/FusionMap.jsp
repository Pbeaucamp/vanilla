<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>FusionMap JSP</title>
 		<script language="JavaScript" src="http://localhost:8080/VanillaRuntime/fusionMap/Maps/FusionMaps.js"></script>
	</head>
	<body>
            <div id="mapdiv" align="center">
               FusionMaps.
           </div>
           <script type="text/javascript">
               var map = new FusionMaps("http://localhost:8080/VanillaRuntime/fusionMap/Maps/FCMap_Europe.swf", "Test", "500", "500", "0", "0");
                map.setDataXML("<map numberSuffix='' baseFontSize='9' fillAlpha='70' hoverColor='639ACE'><colorRange></colorRange><data><entity id='005' value='8411.95' /><entity id='012' value='1.2345E-8' /><entity id='013' value='43784.69' /><entity id='014' value='1.2345E-8' /><entity id='039' value='1.2345E-8' /></data></map>");
                map.render("mapdiv");
            </script>
	</body>
</html>
