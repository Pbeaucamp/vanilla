<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		
		<!--  BEGIN Browser History required section -->
		<link rel="stylesheet" type="text/css" href="history/history.css" />
		<!--  END Browser History required section -->
		
		<title></title>
		<script src="AC_OETags.js" language="javascript"></script>
		
		<!--  BEGIN Browser History required section -->
		<script src="history/history.js" language="javascript"></script>
		<!--  END Browser History required section -->
		
		<style>
			body { margin: 0px; overflow:hidden }
		</style>
		<script language="JavaScript" type="text/javascript">
			<!--
			// -----------------------------------------------------------------------------
			// Globals
			// Major version of Flash required
			var requiredMajorVersion = 9;
			// Minor version of Flash required
			var requiredMinorVersion = 0;
			// Minor version of Flash required
			var requiredRevision = 124;
			// -----------------------------------------------------------------------------
			// -->
		</script>
	</head>
	<body >
		<script language="JavaScript" type="text/javascript">
		<!--
		// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
		var hasProductInstall = DetectFlashVer(6, 0, 65);
		
		// Version check based upon the values defined in globals
		var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
		
		var DataUrl = "<%= request.getParameter("data_url")%>";
		
		if ( hasProductInstall && !hasRequestedVersion ) {
			// DO NOT MODIFY THE FOLLOWING FOUR LINES
			// Location visited after installation is complete if installation is required
			var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
			var MMredirectURL = encodeURI(window.location);
		    document.title = document.title.slice(0, 47) + " - Flash Player Installation";
		    var MMdoctitle = document.title;
			AC_FL_RunContent(
				"src", "playerProductInstall",
				"FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&dataurl='+DataUrl+
				'&MMdoctitle='+MMdoctitle+"",
				"width", "900",
				"height", "500",
				"align", "middle",
				"id", "FMDTViewer",
				"quality", "high",
				"bgcolor", "#869ca7",
				"name", "FMDT Driller Viewer",
				"allowScriptAccess","sameDomain",
				"type", "application/x-shockwave-flash",
				"pluginspage", "http://www.adobe.com/go/getflashplayer"
			);
		} else if (hasRequestedVersion) {
			// if we've detected an acceptable version
			// embed the Flash Content SWF when all tests are passed
			AC_FL_RunContent(
					"src", "FMDTViewer",
					"FlashVars", "dataurl="+DataUrl,
					"width", "900",
					"height", "500",
					"align", "middle",
					"id", "FMDTViewer",
					"quality", "high",
					"bgcolor", "#869ca7",
					"name", "FMDT Driller Viewer",
					"allowScriptAccess","sameDomain",
					"type", "application/x-shockwave-flash",
					"pluginspage", "http://www.adobe.com/go/getflashplayer"
			);
		  } else {  // flash is too old or we can't detect the plugin
		    var alternateContent = 'Alternate HTML content should be placed here. '
		  	+ 'This content requires the Adobe Flash Player. '
		   	+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a>';
		    document.write(alternateContent);  // insert non-flash content
		  }
		// -->
		<%
		String dataUrl = (String) request.getParameter("data_url");
		%>
		</script>
		<noscript>
		  	<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
					id="FMDTViewer" width="100%" height="100%"
					codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
					<param name="movie" value="FMDTViewer.swf" />
					<param name="quality" value="high" />
					<param name="bgcolor" value="#869ca7" />
					<param name="allowScriptAccess" value="sameDomain" />
					<param name="flashVars" value="dataurl=<%= dataUrl%>"/>
					<embed src="FMDTViewer.swf" 
						falshVars="dataurl=<%= dataUrl%>"
					    quality="high" bgcolor="#869ca7"
						width="900" height="500" name="FMDT Driller Viewer" align="middle"
						play="true"
						loop="false"
						quality="high"
						allowScriptAccess="sameDomain"
						allowFullScreen="true" 
						type="application/x-shockwave-flash"
						FlashVars="dataurl=<%= request.getParameter("data_url")%>"
						pluginspage="http://www.adobe.com/go/getflashplayer">
					</embed>
			</object>
		</noscript>
	</body>
</html>