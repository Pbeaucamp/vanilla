<html>
	<head>
		<title>validation</title>
		<link rel="stylesheet" type="text/css" href="biPortal.css">
	</head>
	
<body align="center" >

	
	<br />
	<center>
	<table width="600px" align="center" class="text" border="0">
		<tr>
			<td> <h2>Validation succeed</h2></td>
		</tr>
		<tr>
			<td>
				<%if (request.getParameter("interfaceName") != null) {
					out.write("The activity " + request.getParameter("interfaceName")  + " has been finish without error.");
				}
				%>
			</td>
		</tr>
		<tr>
			<td>
				<%if (request.getParameter("workflowname") != null) {
					out.write("The workflow " + request.getParameter("workflowname")  + " proceed .");
				}
				%>
			</td>
		</tr>
			
	</table>
	</center>
	
</body>
</html>