<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<%@ page language="java" 
import="java.util.ArrayList, 
java.io.IOException,
java.util.Collection,
java.util.List,
java.util.Map,
java.io.File,
java.lang.Throwable,
java.security.MessageDigest,
bpm.vanilla.platform.core.config.VanillaConfiguration,
bpm.vanilla.platform.core.config.ConfigurationManager,
bpm.vanilla.platform.core.IVanillaAPI,
bpm.vanilla.platform.core.remote.RemoteVanillaPlatform,
bpm.vanilla.platform.core.beans.*
"%>
<%@page import="org.jasig.cas.client.util.AbstractCasFilter"%>
<%@page import="org.jasig.cas.client.validation.Assertion"%>
<%@page import="org.jasig.cas.client.util.AssertionHolder"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%
	/*
	 * Prevent caching
	 */
	response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
	response.setHeader("Pragma","no-cache"); //HTTP 1.0
	response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
	
	String sessionId = "";
	
	if (session != null) {
		sessionId = (String)session.getAttribute("bpm.vanilla.portal.sessionId");
		if (sessionId != null && !sessionId.isEmpty()) {
			response.sendRedirect("/vanilla/vanilla.jsp");
		}
	}
	String apacheSsoUser = request.getRemoteUser();
	if(apacheSsoUser != null && !apacheSsoUser.equals("") && session.getAttribute("_apachesso_") == null) {

		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = conf.getVanillaServerUrl();
		bpm.vanilla.platform.core.IVanillaAPI api = new RemoteVanillaPlatform(
					conf.getVanillaServerUrl(),
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)
		);
		User ssouser = api.getVanillaSecurityManager().getUserByName(apacheSsoUser);
		request.getSession().setAttribute("__login",ssouser.getLogin());
		request.getSession().setAttribute("__pwd",ssouser.getPassword());
		request.getRequestDispatcher("vanilla.jsp").forward(request,response);

	}
	
	/*
	*
	*/
	String customLogo = null;
	boolean includeFastConnect = true;
	String vanillaUrl = null;
	try{
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		vanillaUrl = conf.getVanillaServerUrl();
		bpm.vanilla.platform.core.IVanillaAPI api = new RemoteVanillaPlatform(
					conf.getVanillaServerUrl(),
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
					conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)
		);
		customLogo = api.getVanillaPreferencesManager().getCustomLogoUrl();
		includeFastConnect = api.getVanillaPreferencesManager().includeFastConnection();
	}catch(Exception ex){
		
	}
	
	
%>



<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Vanilla</title>
<style type="text/css">
body { 
	margin: 0px;
	padding: 0px;
}

.input{
margin: 2%;
padding: 1%;
width: 100%;
}

h3{
font-size: 8px;
}
#welcome {	
	background: #FFFFFF;
    margin-top:1%;
	margin-right:auto;
	margin-left:auto;
	padding:2%;
    width: 70%;
	height: 450px;
	border-radius: 50px;
	border-style: double;
	border-width: 3px;
	border-color: #6B6D82;
}

#top { 
	height: 16%;
	vertical-align: middle;
    border: solid #6B6D82;
    border-width: 0px 0px 2px 0px;
	font-family: palatino;
	font-weight: bold;
	font-size: 24px;
	color:#660099;
}

#topcenter{
	width: 100%;
}

#topright{
	float: right;
}

#logo {   
	margin:10px;
	padding-top: 2%;
	padding-bottom: 2%;
	height:10%;
	text-align: center;
}

#connect {
	height: 10%;
	text-align: center;
	vertical-align : middle;
	margin-top:15%;
}

#help{
	text-align: right;
}

#about {
	border: solid #660099;
	border-width: 0px 0px 0px 0px;
	margin: 0px;
	padding: 5px;
	font-size: 10px;
	font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serifl;
	text-align: center;
}

</style>
<script type='text/javascript' src='./js/vanillaautthentification.js'></script>
<script type='text/javascript' src='./js/MD5_2.js'></script>

<script language="JavaScript">
	function Connect(user, pwd) {
		document.getElementById('__login').value = user;
		document.getElementById('__pwd').value = pwd;
		
		encryptPassword();

		document.getElementById('_login_').submit();
	}

	function encryptPassword() {
		var pwd  = document.getElementById('__pwd').value;
		document.getElementById('__pwd').value = MD5(pwd);
		
		return true;
	}
	
	function GoHelp(){
		newwindow=window.open("http://www.bpm-conseil.com");	
	}
</script>

</head>
<body>
	<div id="welcome">	
		<div id="top">
			<table style="width: 100%;">
				<tr>
					<td id="topleft">
					BPM-Conseil
					</td>
					<%
						if (customLogo != null){
							%>
							<td id="topcenter">
								<center><img src="<%= vanillaUrl + customLogo%>" style="width:95%;height:70px;"/></center>
							</td>
							<%
						}
					
					%>
					
					
					
					<td id="topright">
						<img src="<%= vanillaUrl + "/images/Vanilla_Logo.gif"%>" style="width:110px"/>
					</td>
				</tr>
			</table>
		</div>
		<div id="logo">
			<img src="acceuil/Vanilla_Acceuil.png">
		</div>
		<div id="connect">
				<center>
				<div style="position:relative;margin:auto;width:99%;height:150%">
					
					<%
						if (includeFastConnect){
							%>
							<div style="float:right;">
							<center>
								<table style="border-collapse:collapse">
				
									<tr>
									<td>
										<img src="acceuil/log.png"><a href="#"  onclick="Connect('system','system')">Admin</a>
									</td>
									
									</tr>
									<tr>
									<td>
										<img src="acceuil/log.png"><a href="#" onclick="Connect('designer','designer')">Designer</a>
									</td>
									</tr>
									<tr>
									<td>
										<img  src="acceuil/log.png"><a href="#" onclick="Connect('user','user')">User</a>
									</td>
									
									</tr>
									<tr>
									
									<td>
									<img src="acceuil/Help.png" style='cursor:pointer;margin-top:5px'  onclick="GoHelp()">
									</td>
									</tr>				
								</table>
						</center>
							</div>
						<%
						}
					
					%>
					<div >
						<form id="_login_" method=post action="/vanilla/vanilla.jsp" onSubmit="return encryptPassword()">
						<table style="border-collapse:collapse">
							<tr>
								<td>Login  </td><td><input name="__login" id="__login" type="text" class="input"></td>
							</tr>
							<tr>
								<td>Password  </td><td><input name="__pwd" id="__pwd" type="password" class="input"></td>
							</tr>
							<tr>
								<td>Langage  </td><td>
									<select name="__lang" id="__lang" class="input">
										<option value="en_EN"> English </option>
										<option value="fr_FR"> Français </option>
										<option value="es_ES"> Español </option>
										<option value="hi_IN"> हिन्दू </option>
										<option value="it"> Italiano </option>
										<option value="nl_NL"> Nederlands </option>
										<option value="pt"> Português </option>
										<option value="th_TH"> ภาษาไทย </option>
										<option value="zh_CN"> 中国的 </option>
										<option value="zh_TW"> 中國的 </option>
										<option value="ms_MY"> Melayu </option>
										<option value="ja_JP"> 日本人 </option>
										<option value="pl_PL"> Polski </option>
										<option value="ru_RU"> Pусский </option>
									</select>
								</td>
							</tr>
						</table>
						<center>
							<br/>
							<input type="submit" value="OK">
						</center>
						<br>
						</form>
					</div>
					
				</div>
				
				</center>
		
		
		
		</div>
	</div>
	</div>	
	<div id="about">
	<h3>
	"Supplied free of charge with no support, no certification, no maintenance, no warranty and no indemnity by BPM-conseil or its Certified Partners. &#169; 2005-2012 All rights reserved."
	</h3>
	</div>
</div>
</body>
</html>



