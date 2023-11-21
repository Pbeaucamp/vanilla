<!--<!doctype html>-->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page language="java" 
import="java.util.ArrayList, 
java.io.IOException,
java.util.Collection,
java.util.List,
java.util.Map
"%>
<%
	/*
	if (request.getSession().getAttribute("_session_") == null && request.getSession().getAttribute(CASFilter.CAS_FILTER_USER) == null) {
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}
    else 
		if (request.getSession().getAttribute(CASFilter.CAS_FILTER_USER) == null) {
	*/
	/*
	{
		Map params = request.getParameterMap();
		
		String[] g  = (String[]) params.get("__group");
		

		String group = g[0];

		request.getSession().setAttribute("_group_", group);
		
	}
	*/
	Map user_params = request.getParameterMap();
	String[] login  = (String[]) user_params.get("__login");
	String[] pwd  = (String[]) user_params.get("__pwd");
	if (login != null && pwd != null && !login.equals("")) {
		request.getSession().setAttribute("_user_", login[0]);
		request.getSession().setAttribute("_pwd_", pwd[0]);
	}
	else {
		try{
			String log = (String)request.getSession().getAttribute("__login");
			String pass = (String)request.getSession().getAttribute("__pwd");
			if(log != null && pass != null) {
				request.getSession().setAttribute("_user_", log);
				request.getSession().setAttribute("_pwd_", pass);
				request.getSession().setAttribute("_apachesso_", "true");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
%>
<%@page import="org.jasig.cas.client.util.AbstractCasFilter"%>
<%@page import="org.jasig.cas.client.validation.Assertion"%>
<%@page import="org.jasig.cas.client.util.AssertionHolder"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>


<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:v="urn:schemas-microsoft-com:vml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta http-equiv="Content-Language" content="en">
<meta http-equiv="Content-Author" content="bpm">

<!-- 
  Copyright 2008 BPM-conseil.
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at
  
  http://www.apache.org/licenses/LICENSE-2.0
  
  Unless requestuired by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
  -->

<title>Vanilla</title>
<!--  <meta name="gwt:property" content="locale=<%=request.getLocale()%>">  -->
<%
	String lang = "en_US";
	Map params = request.getParameterMap();
	if (params.get("__lang") != null) {
		lang = ((String[]) params.get("__lang"))[0];
	}
	else {
		request.setAttribute("__lang", lang);
	}
%>
<meta name="gwt:property" content="locale=<%=lang%>">
<!--<meta name="gwt:property" content="locale=fr_FR">-->
<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->
<!--<script language='javascript' src='bpm.vanilla.portal.biPortal.nocache.js'></script>
-->
<!--<meta name='gwt:module' content='com.bpm_conseil.gwt.fmWeb'>
	<meta name='gwt:module' content='com.google.code.p.gwtcsample.GWTCSample'/>
	-->

<style type="text/css">
        
        #loading {
            position: absolute;
            left: 45%;
            top: 40%;
            padding: 2px;
            z-index: 20001;
            height: auto;
            border: 1px solid #ccc;
        }
        #loading a {
            color: #225588;
        }

        #loading .loading-indicator {
            background: white;
            color: #444;
            font: bold 13px tahoma, arial, helvetica;
            padding: 10px;
            margin: 0;
            height: auto;
        }

        #loading-msg {
            font: normal 10px arial, tahoma, sans-serif;
        }
    </style>
    
    	<link rel="icon" type="image/png" href="images/Portal.png" />
		<link type="text/css" rel="stylesheet" href="VanillaPortail/activeTheme.css" id="themeCssElement"> 
    
</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic ui         -->
<!--                                           -->

<body oncontextmenu="return false">
<!--                                            -->
<!-- L'inclusion de gwt.js est obligatoire.     -->
<!-- Elle placer ici pour des raisons de        -->
<!-- preformances.                              -->
<!--                                            -->

		<script type="text/javascript"	src="VanillaPortail/VanillaPortail.nocache.js"></script>

<!--add loading indicator while the app is being loaded-->
<div id="loading">
</div>

<span id="login"></span>

</body>

</html>
