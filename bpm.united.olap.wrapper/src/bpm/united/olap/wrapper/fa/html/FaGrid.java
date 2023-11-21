package bpm.united.olap.wrapper.fa.html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;


public class FaGrid implements HtmlElement {

	private List<List<HtmlElement>> elements = new ArrayList<List<HtmlElement>>();
	private HashMap<String, String> properties = new HashMap<String, String>();
	
	public String getHtml() {
		
		return null;
	}
	
	public void setWidget(int i, int j,  HtmlElement element) {
		if(i >= elements.size()) {
			elements.add(new ArrayList<HtmlElement>());
		}
		elements.get(i).add(element);
	}

	public void addStyleName(String style) {
		
		
	}

	public HtmlElement getWidget(int i, int j) {
		return elements.get(i).get(j);
	}

	public String getStyleName() {
		
		return null;
	}

	public String getHTML() {
		
		return null;
	}

	public void setHTML(String string) {
		
		
	}
	public void setProperty(String name, String value) {
		properties.put(name, value);
	}
	
	public String getGridHtml() {return "";}

	public String getGridHtml(IRepositoryContext ctx, String sessionId, String viewName, Integer fasdItemId, String cubeName) {
		StringBuffer buf = new StringBuffer();
		
//		buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
		
		buf.append("<html>\n");
		buf.append("<head>\n");
		createCss(buf);
		buf.append("</head>\n");
		buf.append("<body>\n");
		
		buf.append("<table class=\"cubeView\" cellpadding=\"0\" cellspacing=\"0\"");
		
		if(properties.size() > 0) {
			buf.append(" style=\"");
			for(String name : properties.keySet()) {
				String val = properties.get(name);
				buf.append(name + ": " + val + ";");
			}
			buf.append("\"");
		}
		
		buf.append(">\n");
		buf.append("<tbody>\n");
		
		for(int i = 0 ; i < elements.size() ; i++) {
			
			List<HtmlElement> line = elements.get(i);
			buf.append("	<tr>\n");
			
			for(int j = 0 ; j < line.size() ; j++) {
				HtmlElement elem = line.get(j);
				buf.append("		<td class=\"cubeView ");
				
				if(elem.getStyleName().contains("draggableGridItem")) {
					buf.append("draggableGridItem");
				}
				if(elem.getStyleName().contains("gridTotalItem")) {
					buf.append("gridTotalItem");
				}
				
				buf.append("\"");
				
				if(elem.getProperties().size() > 0) {
					buf.append(" style=\"height: 30px;");
					for(String name : elem.getProperties().keySet()) {
						if(name.contains("td-")) {
							String val = elem.getProperties().get(name);
							String na = name.substring(3);
							buf.append(na + ": " + val + ";");
						}
					}
					buf.append("\"");
				}
				buf.append(">\n");
				
				buf.append("			" + elem.getGridHtml());
				buf.append("		</td>\n");
			}
			
			buf.append("	</tr>\n");
		}
		
		buf.append("</tbody>\n");
		buf.append("</table>\n");
		
		try {
			String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);
			url += "?bpm.vanilla.sessionId=" + sessionId;
			url += "&bpm.vanilla.groupId=" + ctx.getGroup().getId();
			url += "&bpm.vanilla.repositoryId=" + ctx.getRepository().getId();
			url += "&bpm.vanilla.fasd.id=" + fasdItemId;
			if (cubeName != null) {
				url += "&bpm.vanilla.cubename=" + URLEncoder.encode(cubeName, "UTF-8");
				if (viewName != null) {
					url += "&bpm.vanilla.viewname=" + URLEncoder.encode(viewName, "UTF-8");
				}
			}
			buf.append("<a href=\""+url+"\" target=\"_blank\">Open in VanillaAnalysis</a>");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		buf.append("</body>\n");
		buf.append("</html>");
		
		return buf.toString();
	}

	private void createCss(StringBuffer buf) {
		buf.append("<style>\n");
		
		buf.append(".cubeView {\n"+
					"vertical-align: middle;\n"+
					"table-layout: fixed;\n"+
				"}\n"+
				".gridItemValueBold {\n"+
					"font-weight: bold;\n"+
				"}\n"+
				".gridItemNull {\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"text-align: center;\n"+
				"}\n"+
				".gridItemValue {\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: white;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
					"cursor: pointer;\n"+
					"overflow: hidden;\n"+
					"white-space: nowrap;\n"+
				"}\n"+
				".gridItemBorder {\n"+
					"border-top: 2px groove;\n"+
					"border-left: 2px groove;\n"+
				"}\n"+
				".leftGridSpanItemBorder {\n"+
					"border-left: 2px groove;\n"+
				"}\n"+
				".rightGridSpanItemBorder {\n"+
					"border-top: 2px groove;\n"+
				"}\n"+
				".lastRowItemBorder {\n"+
					"border-bottom: 2px groove;\n"+
				"}\n"+
				".lastColItemBorder {\n"+
					"border-right: 2px groove;\n"+
				"}\n"+
				".draggableGridItem {\n"+
					"table-layout: fixed;\n"+
					"margin: auto;\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: #C3DAF9;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
					"overflow: hidden;\n"+
					"white-space: nowrap;\n"+
				"}\n"+
				".measureGridItem {\n"+
					"table-layout: fixed;\n"+
					"margin: auto;\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: #FFCC66;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
					"overflow: hidden;\n"+
					"white-space: nowrap;\n"+
				"}\n"+
				".gridTotalItem {\n"+
					"margin: auto;\n"+
					"display: table-cell;\n"+
					"font-family: Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;\n"+
					"font-size: 11px;\n"+
					"background-color: #C3DAF9;\n"+
					"text-align: center;\n"+
					"vertical-align: middle;\n"+
				"}\n");
		
		buf.append("</style>\n");
	}

	public List<String> getStyles() {
		
		return null;
	}

	public HashMap<String, String> getProperties() {
		
		return null;
	}

}
