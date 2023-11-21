package bpm.fd.runtime.engine;

public class JavaScriptCommonGenerator {
	
	
	public static final String FUNCTION_FILER_SUBMIT = "filterSelection()";
	
	public static String generateJavaScriptFunctions(String spacing){
		StringBuffer buf = new StringBuffer();
		
//		buf.append(spacing + "  function filterSelection(){\n");
//		buf.append(spacing + "    document.forms[\"dashboard\"].submit();\n");
//		buf.append(spacing + "  }\n\n");
		
		buf.append(spacing + "  function replaceParameter(url, pName, pValue){\n");
//		buf.append(spacing + "  	var bufferedUrl = url;\n");
//		buf.append(spacing + "  	var start = bufferedUrl.indexOf(pName);\n");
//		buf.append(spacing + "  	var stop = location.href.indexOf('&', start+2);\n");
//		buf.append(spacing + "  	if (start > 0){\n");
//		buf.append(spacing + "  		bufferedUrl = location.href.substring(0, start) + pName + '=' + pValue;\n");					
//		buf.append(spacing + "  	}\n");
//		buf.append(spacing + "  	else{\n");
//		buf.append(spacing + "  		var pI = bufferedUrl.indexOf('?');\n");
//		buf.append(spacing + "  		if (pI >= 0){\n");
//		buf.append(spacing + "  			bufferedUrl += '&' + pName + '=' + pValue;\n");
//		buf.append(spacing + "  		}\n");
//		buf.append(spacing + "  		else{\n");
//		buf.append(spacing + "  			bufferedUrl += '?' + pName + '=' + pValue;\n");
//		buf.append(spacing + "  		}\n");
//		buf.append(spacing + "  	}\n");
		buf.append(spacing + "  return URL.setParameter(url,pName,pValue);\n");
//		buf.append(spacing + "  	return bufferedUrl;\n");
		buf.append(spacing + "  }\n");
		
		
		
		
		buf.append(spacing + "  function replaceParameter2(url, pName, pValue){\n");
//		buf.append(spacing + "  	if (pValue=='null'){return url;}\n");
//		buf.append(spacing + "  	var bufferedUrl = url;\n");
//		buf.append(spacing + "  	var start = bufferedUrl.indexOf(pName);\n");
//		buf.append(spacing + "  	var stop = bufferedUrl.indexOf('&', start+2);\n");
//		buf.append(spacing + "  	if (start > 0){\n");
//		buf.append(spacing + "  	    if (stop > 0){\n");
//		buf.append(spacing + "  		    bufferedUrl = bufferedUrl.substring(0, start) + pName + '=' + pValue + bufferedUrl.substring(stop);\n");
//		buf.append(spacing + "  	    }else{\n");
//		buf.append(spacing + "  		    bufferedUrl = bufferedUrl.substring(0, start) + pName + '=' + pValue;\n");					
//		buf.append(spacing + "  	    }\n");
//		buf.append(spacing + "  	}\n");
//		buf.append(spacing + "  	else{\n");
//		buf.append(spacing + "  		var pI = bufferedUrl.indexOf('?');\n");
//		buf.append(spacing + "  		if (pI >= 0){\n");
//		buf.append(spacing + "  			bufferedUrl += '&' + pName + '=' + pValue;\n");
//		buf.append(spacing + "  		}\n");
//		buf.append(spacing + "  		else{\n");
//		buf.append(spacing + "  			bufferedUrl += '?' + pName + '=' + pValue;\n");
//		buf.append(spacing + "  		}\n");
		
//		buf.append(spacing + "  	}\n");
//		buf.append(spacing + "  	if (stop > 0){\n");
//		buf.append(spacing + "  	bufferedUrl  += location.href.substring(stop);\n");
//		buf.append(spacing + "  	}\n");
//		buf.append(spacing + "  	return bufferedUrl;\n");
		buf.append(spacing + "  return URL.setParameter(url,pName,pValue);\n");
		buf.append(spacing + "  }\n");
		
		buf.append(spacing + "  function setParameter(pName, pValue){\n");
		buf.append(spacing + "  	if(pValue == '%') {\n");
		buf.append(spacing + "  		pValue = '%2525';\n");
		buf.append(spacing + "  	}\n");
		buf.append(spacing + "  	parameters[pName]=encodeURI(pValue);\n");
		buf.append(spacing + "  }\n\n\n");
		
		buf.append(spacing + "function setLocation(){\n");
		buf.append(spacing + "	var url = location.href;\n");
//		buf.append(spacing + "  	if (url.indexOf('?') >=0){\n");
//		buf.append(spacing + "  		url = url.substring(0, url.indexOf('?'));\n");
//		buf.append(spacing + "  	}\n");
//		buf.append(spacing + "  	else{\n");
//		buf.append(spacing + "  		url+='?';\n");
//		buf.append(spacing + "  	}\n");
		buf.append(spacing + "  	for(name in parameters){\n");
		buf.append(spacing + "  		if(parameters[name] == '%25') {\n");
		buf.append(spacing + "  			parameters[name] = '%252525';\n");
		buf.append(spacing + "  		}\n");
		buf.append(spacing + "  		url = replaceParameter(url, name, parameters[name]);\n");
		buf.append(spacing + "  	}\n");
			
		buf.append(spacing + "  	location.href=url;\n");

		buf.append(spacing + "}\n");
		
		buf.append(spacing + "function openUrl(url){\n");
		buf.append(spacing + "  	for(name in parametersOut){\n");
		buf.append(spacing + "  		url = replaceParameter2(url, parametersOut[name], parameters[name]);\n");
		buf.append(spacing + "  	}\n");
//		buf.append("alert(url);");
		
		buf.append(spacing + "  	window.open(url);\n");

		buf.append(spacing + "}\n");

		
		
		buf.append("function zoomChart(elementId, width, height, name){\n");
		buf.append("    var htmlDiv = document.getElementById(elementId).innerHTML;\n");
		buf.append("    var backup = document.getElementById(elementId).innerHTML;\n");
		
		
		
		
		
		
		
		
		/*
		 * var start = htmlDiv.indexOf('chartWidth=');
		 * var end = htmlDiv.indexOf('&amp;', start);
		 * htmlDiv = htmlDiv.substring(0,start) + "chartWidth=" + width + htmlDiv.substring(end); 
		 * 
		 * start = htmlDiv.indexOf('chartHeight=');
		 * end = htmlDiv.indexOf('&amp;', start);
		 * htmlDiv = htmlDiv.substring(0,start) + "chartHeight=" + height + htmlDiv.substring(end); 
		 * 
		 * start = htmlDiv.indexOf('height=');
		 * end = htmlDiv.indexOf('\'', start);
		 * htmlDiv = htmlDiv.substring(0,start) + 'height=\'' + height + '\''+ htmlDiv.substring(end); 
		 * 
		 * start = htmlDiv.indexOf('height=', start);
		 * end = htmlDiv.indexOf('\'', start);
		 * htmlDiv = htmlDiv.substring(0,start) + 'height=\'' + height + '\''+ htmlDiv.substring(end); 
		 * 
		 * start = htmlDiv.indexOf('width=');
		 * end = htmlDiv.indexOf('\'', start);
		 * htmlDiv = htmlDiv.substring(0,start) + 'width=\'' + width + '\''+ htmlDiv.substring(end); 
		 * 
		 * start = htmlDiv.indexOf('width='), start;
		 * end = htmlDiv.indexOf('\'', start);
		 * htmlDiv = htmlDiv.substring(0,start) + 'width=\'' + width + '\''+ htmlDiv.substring(end); 
		 */
		
		
		
		
		 buf.append("    var start = htmlDiv.indexOf('chartWidth=');\n");
		 buf.append("    var end = htmlDiv.indexOf('&amp;', start);\n");
		 buf.append("    htmlDiv = htmlDiv.substring(0,start) + 'chartWidth=' + width + htmlDiv.substring(end);\n"); 
		 
		 buf.append("    start = htmlDiv.indexOf('chartHeight=');\n");
		 buf.append("    end = htmlDiv.indexOf('&amp;', start);\n");
		 buf.append("    htmlDiv = htmlDiv.substring(0,start) + 'chartHeight=' + height + htmlDiv.substring(end);\n"); 
		  
		 buf.append("    start = htmlDiv.indexOf('height=');\n");
		 buf.append("    end = htmlDiv.indexOf('\\'', start + 8) + 1;\n");
		 buf.append("    htmlDiv = htmlDiv.substring(0,start) + 'height=\\'' + height + '\\''+ htmlDiv.substring(end);\n"); 
		  
		 buf.append("    start = htmlDiv.indexOf('width=');\n");
		 buf.append("    end = htmlDiv.indexOf('\\'', start+ 8)+ 1;\n");
		 buf.append("    htmlDiv = htmlDiv.substring(0,start) + 'width=\\'' + width + '\\''+ htmlDiv.substring(end);\n"); 

		 buf.append("    start = htmlDiv.indexOf('width=', start+1);\n");
		 buf.append("    end = htmlDiv.indexOf('\"', start+ 8)+ 1;\n");
		 buf.append("    htmlDiv = htmlDiv.substring(0,start) + 'width=\\'' + width + '\\''+ htmlDiv.substring(end);\n"); 

		 
		 buf.append("    start = htmlDiv.indexOf('height=', start);\n");
		 buf.append("    end = htmlDiv.indexOf('\"', start+ 8)+ 1;\n");
		 buf.append("    htmlDiv = htmlDiv.substring(0,start) + 'height=\\'' + height + '\\''+ htmlDiv.substring(end);\n"); 
		  
				  
				 
		
		 buf.append("    var html = '<html>';\n");
			buf.append("    html += '    <head>';\n");
			buf.append("    html += '    <META http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">';\n");
			//buf.append("    html += '" + JSPGenerator.getImportedResources("").replace("\n", "") + "';\n");
			buf.append("    html += '    </head>';\n");
			buf.append("    html += '    <body>';\n");
			buf.append("    html += '        <div id=\"chart\">';\n");
			buf.append("    html += htmlDiv;\n");
			buf.append("    html += '        </div>';\n");
			buf.append("    html += '    </body>';\n");
			buf.append("    html += '</html>';\n\n\n");
		
		buf.append("    var generator= window.open('',name,'height=' + height + ',width=' + width);\n");
		buf.append("    generator.document.write(html);\n");
		buf.append("    generator.document.close();\n");

		buf.append("}\n");
		
		
		
		
		

		
		
		return buf.toString();
	}
}	
