package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;


public class HTMLErrorWriter {

	
	public static String getErrorHtml(String consoleName, Throwable error,  boolean includeTitle){
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append("    <head>\n");

		buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../images/vanillaExternalCss.css\"/>\n");
		
		
		buf.append("         <style>\n");
		buf.append("             #round_corners_table { color:black;clear:both; border-color:black; border: 2px; border-collapse: collapse;}\n");
		buf.append("             #top-row {height: 20px; border: none; }\n");
		buf.append("             .empty {border: none; background-color: #E0E0F8; }\n");
		
		buf.append("             .st {background-color: #E0E0F8; }\n");
//		buf.append("             #logo {background-image: url(../images/Vanilla_Logo.gif); }\n");
		buf.append("             #bottom-row {height: 20px; border: none; }\n");
		buf.append("             #tl { border: none; width: 18px; background-image: url(../images/corner_tl.png); }\n");
		buf.append("             #tr { border: none;  width: 18px; background-image: url(../images/corner_tr.png); }\n");
		buf.append("             #bl {  border: none; background-image: url(../images/corner_bl.png); }\n");
		buf.append("             #br {  border: none; background-image: url(../images/corner_br.png); }\n");
		buf.append("         </style>\n");
		buf.append("    </head>\n");
		buf.append("    <body>\n");
		
		if (includeTitle){
			buf.append("<div class=\"centre\">\n");
			//buf.append("    <div style=\"float:left; margin-top=auto;\"><img src=\"../images/Vanilla_Logo.gif\" /></div>\n");
			buf.append("    <div class=\"entete\"><p>" + consoleName + "</p></div>\n");
		}
		
		buf.append("<p class=\"error\">\n");
		Throwable t = error;
		buf.append(error.getMessage() + "<br>");
		while( (t = t.getCause()) != null){
			buf.append(t.getMessage() + "<br>");
		}
		buf.append("</p>\n");
		
		if (includeTitle){
			buf.append("</div>\n");
		}
		
		
		
		buf.append("    </body>\n");
		buf.append("</html>\n");
		
		return buf.toString();
	}
}
