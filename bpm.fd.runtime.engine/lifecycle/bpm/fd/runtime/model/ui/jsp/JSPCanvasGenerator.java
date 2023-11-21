package bpm.fd.runtime.model.ui.jsp;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.runtime.model.DashBoardMeta;
import bpm.fd.runtime.model.DashInstance;

public class JSPCanvasGenerator {
	/**
	 * header to store the DashInstance within the HttpSession
	 */
	public static final String HTTP_HEADER_INSTANCE_UUID = "DASH_INSTANCE_UUID";
	
	public static final String REQUEST_PARAM_UUID = "uuid";
	
	private FdModel model;
	private DashBoardMeta meta;
	
	public JSPCanvasGenerator(DashBoardMeta meta, FdModel model){
		this.model = model;
		this.meta = meta;
	}
	
	protected FdModel getModel(){
		return model;
	}
	protected DashBoardMeta getDashMeta(){
		return this.meta;
	}
	
	private String generateJSPMeta(){
		StringBuffer buf = new StringBuffer();
		buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">\n");
		buf.append("<%@page contentType=\"text/html;charset=UTF-8\"%>\n");
		buf.append("<%@ page pageEncoding=\"UTF-8\" %>\n");
		buf.append("<%@page language=\"java\" import=\"\n");
		buf.append("java.util.Properties, \n");
		buf.append("java.util.List, \n");
		buf.append("java.util.Iterator, \n");
		buf.append("java.util.HashMap, \n");
		buf.append("java.util.Locale, \n");
		buf.append("java.io.FileInputStream, \n");
		
		
		buf.append("org.apache.log4j.Logger, \n");
		buf.append("java.net.*, \n");
		
		buf.append("bpm.fd.runtime.model.*, \n");
		buf.append("bpm.fd.runtime.model.controler.*, \n");
		buf.append("java.io.PrintWriter, \n");
		buf.append("java.io.InputStream \n");
		//buf.append("org.eclipse.datatools.connectivity.oda.IQuery\n");
		buf.append("\"%>\n\n");
		buf.append("<%\n");
//		buf.append(" String dashInstanceUuid = request.getHeader(\"" + HTTP_HEADER_INSTANCE_UUID + "\");\n" );
//		buf.append(" DashInstance dashInstance = Controler.getInstance().getInstance(null, null, dashInstanceUuid);\n" );
//		buf.append(" if (dashInstanceUuid == null){dashInstanceUuid = dashInstance.getUuid();}\n");
//		buf.append(" response.setHeader(\"" + HTTP_HEADER_INSTANCE_UUID + "\", dashInstanceUuid);\n");
//		buf.append(" if (session.getAttribute(\"" + HTTP_HEADER_INSTANCE_UUID + "\") == null){session.setAttribute(\"" + HTTP_HEADER_INSTANCE_UUID + "\", dashInstanceUuid);}\n");
		buf.append(" DashInstance dashInstance = null;\n" );
		buf.append(" if (dashInstance == null){String dashInstanceUuid = request.getParameter(\"" + REQUEST_PARAM_UUID + "\");dashInstance = Controler.getInstance().getInstance(dashInstanceUuid);}");
		buf.append("%>\n");
		
		return buf.toString();
	}
	
	private String generateHTMLHeader(boolean isPopup){
		StringBuffer buf = new StringBuffer();
		buf.append("    <head>\n");
		buf.append("        <meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>\n");  
		
		//jQuery
		buf.append("        <link type=\"text/css\" href=\"../../freedashboardRuntime/jquery/ui/css/smoothness/jquery-ui-1.8.17.custom.css\" rel=\"Stylesheet\" />\n");
		buf.append("        <link rel=\"Stylesheet\" href=\"../../freedashboardRuntime/jquery/slider/ui.slider.extras.css\" type=\"text/css\" />\n");
		buf.append("        <link type=\"text/css\" rel=\"stylesheet\" href=\"../../freedashboardRuntime/jquery/accordion/css/jquery-ui-1.8.9.custom/jquery-ui-1.8.9.custom.css\" />\n");
		
		buf.append("        <link rel=\"Stylesheet\" href=\"../../freedashboardRuntime/chartjs/Chart.css\" type=\"text/css\" />\n");
		
		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/jquery-1.7.2.min.js\"></script>\n");
		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/ui/js/jquery-ui-1.8.21.custom.min.js\"></script>\n");
		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/slider/selectToUISlider.jQuery.js\"></script>\n");	
		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/accordion/jquery-ui-1.8.13.custom.min.js\"></script>\n");
		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/accordion/jquery.multi-accordion-1.5.3.js\"></script>\n");
		
//		buf.append("        <link type=\"text/css\" href=\"../../freedashboardRuntime/jquery/css/ui-lightness/jquery-ui-1.10.3.custom.css\" rel=\"Stylesheet\" />\n");
//		buf.append("        <link rel=\"Stylesheet\" href=\"../../freedashboardRuntime/jquery/development-bundle/themes/ui-lightness/jquery.ui.slider.css\" type=\"text/css\" />\n");
//		buf.append("        <link type=\"text/css\" rel=\"stylesheet\" href=\"../../freedashboardRuntime/jquery/development-bundle/themes/ui-lightness/jquery.ui.accordion.css\" />\n");
//				
//		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/js/jquery-1.10.2.min.js\"></script>\n");
//		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/js/jquery-ui-1.10.3.custom.min.js\"></script>\n");
//		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/development-bundle/ui/jquery.ui.slider.js\"></script>\n");	
//		buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/development-bundle/ui/jquery.ui.accordion.js\"></script>\n");
		//buf.append("        <script type=\"text/javascript\" src=\"../../freedashboardRuntime/jquery/accordion/jquery.multi-accordion-1.5.3.js\"></script>\n");
		
//		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/HighCharts/highcharts.js\"></script>\n");
		
		//buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/FusionCharts.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/js/fusioncharts.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/js/fusioncharts.charts.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/js/fusioncharts.powercharts.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/js/fusioncharts.widgets.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/js/themes/fusioncharts.theme.fint.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionCharts/js/fusioncharts.maps.js\"></script>\n");
//		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/chartjs/chart.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/chartjs/Chart.bundle.js\"></script>\n");
//		buf.append("        <script language=\"Javascript\" src=\"https://www.chartjs.org/dist/2.8.0/Chart.min.js\"></script>\n");
		
		
		
		
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/url.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/html2canvas.js\"></script>\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/wait/wait.css\"/>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/olap.js\"></script>\n");

		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/grid.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/folder.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/stackablecell.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/dynamicstackablecell.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/dashlet.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/styledtext.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/text.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/report.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/markdown.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/chart.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/map.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/filter.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/js/ajax.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/datepicker/datepicker.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/menu/menu.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/datagrid/datagrid.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/ckeditor/ckeditor.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/timer/timer.js\"></script>\n");

		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/comment/comment.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/comment/listcommentpart.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/FusionMaps/FusionMaps.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/OpenLayers/OpenLayers.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/OpenLayers/OpenStreetMap.js\"></script>\n");	
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/VanillaMap/VanillaMap.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/fmmap/FMMap.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/fmmap/MetricElement.js\"></script>\n");
		
//		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/Nimes/dgasecurity.js\"></script>\n");
		
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/datagrid/datagrid.css\"/>\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/dynamiclabel.css\"/>\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/datepicker/datepicker.css\"/>\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/menu/menu.css\"/>\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/timer/timer.css\" />\n");
		
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/stackableCell/stackableCell.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/comment/comment.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/fmmap/fmmap.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/wait/internal.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/ariane/ariane.css\" />\n");
		
		buf.append("        <script language=\"Javascript\" src=\"../../freedashboardRuntime/jquery/popup/jquery.simplemodal.js\"></script>\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/jquery/popup/basic.css\" />\n");
		
		//force FusionChart HTML 5
		//buf.append("        <script type='text/javascript'>FusionCharts.setCurrentRenderer('javascript');\n$.support.cors = true;</script>\n");
		
		//Add support for DataGrid
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/bootstrap/css/bootstrap.min.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/animate/animate.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/select2/select2.min.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/perfect-scrollbar/perfect-scrollbar.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/datagrid/util.css\" />\n");
		buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"../../freedashboardRuntime/js/datagrid/main.css\" />\n");
		
		
		String relativeProjectFolder = "../../generation/" + meta.getIdentifierString() + "/";
		for(IResource r : model.getProject().getResources()){
			
			if (r instanceof FileCSS){
				
				buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + relativeProjectFolder + r.getName() + "\"/>\n");
			}
			if (r instanceof FileJavaScript){
				buf.append("        <script language=\"Javascript\" src=\"" + relativeProjectFolder + r.getName() + "\"></script>\n");
				
			}
		}
		if(!isPopup) {
			buf.append("        <%out.write(\"<script type='text/javascript'>var _vanillagroup_='\"+dashInstance.getGroup().getName()+\"';\");\nout.write(\"var _isExport_='\"+request.getParameter(\"_isExport_\")+\"';</script>\");%>\n");
		}
		buf.append("    </head>\n");
		
		return buf.toString();
	}
	
	
	protected String generateBodyContent(){
		StringBuffer buf = new StringBuffer();
		buf.append("<div id='basic-modal-content'></div>\n");
		HtmlStructureGenerator gen = new HtmlStructureGenerator(model.getProject().getProjectDescriptor().getInternalApiDesignVersion());
		buf.append(gen.getContent(model, null));
		return buf.toString();
	}
	
	protected String generateHTMLBody(){
		StringBuffer buf = new StringBuffer();
		buf.append("<body");
		if (model.getCssClass() != null){
			buf.append(" class='" + model.getCssClass() + "' ");
		}
		
		boolean onLoadAdded = false;
		
		if(model.hasEvents()) {
			for(ElementsEventType type : model.getEventsType()) {
				String sc = model.getJavaScript(type);
				if (sc != null && !"".equals(sc.trim())){
					
					if(type == ElementsEventType.onLoad) {
						buf.append(" " + type.name() + "=\"" + sc.replace("\r\n", "").replace("\n", "") + 
								"if('<%out.write(request.getParameter(\"folder\"));%>' != ''){fdObjects['folder'].render('<%out.write(request.getParameter(\"folder\"));%>');}" + "\"");
						onLoadAdded = true;
					}
					else {
						buf.append(" " + type.name() + "=\"" + sc.replace("\r\n", "").replace("\n", "") + "\"");
					}
				}
			}
		}
		if(!onLoadAdded) {
			buf.append(" " + ElementsEventType.onLoad.name() + "=\"" + "if('<%out.write(request.getParameter(\"folder\"));%>' != ''){fdObjects['folder'].render('<%out.write(request.getParameter(\"folder\"));%>');}\"");//document.getElementById('<%out.write(request.getParameter(\"folder\"));%>').style.display='block';}" + "\"");
		}
		buf.append(">\n");
		
		//FIXME : WE NEED TO CHANGE THAT JUST FOR TEST
		//IT SHOWS A BUTTON AT THE TOP OF THE PAGE
//		buf.append("<input type=\"button\" onclick=\"exportDashboard();return false;\" value=\"test export\" style=\"top:0;left:0;position:absolute;width:100px;height:30px;\">");
		
		buf.append(generateBodyContent());
		
		
		
		buf.append("</body>\n");
		return buf.toString();
	}
	
	public String getJsp(){
		StringBuffer buf = new StringBuffer();
		buf.append(generateJSPMeta());
		buf.append("<html xmlns:svg=\"http://www.w3.org/2000/svg\">\n");
		buf.append(generateHTMLHeader(false));
		buf.append(generateHTMLBody());
		buf.append("</html>");
		return buf.toString();
	}
	
	public String getHtml(DashInstance instance){
		StringBuffer buf = new StringBuffer();

		buf.append("<html xmlns:svg=\"http://www.w3.org/2000/svg\">\n");
		buf.append(generateHTMLHeader(true));
	
		buf.append("<body>\n");
		
		HtmlStructureGenerator gen = new PopupHtmlStructureGenerator(instance,model.getProject().getProjectDescriptor().getInternalApiDesignVersion());
		buf.append(gen.getContent(model, null));
		buf.append("</body>\n");
		buf.append("</html>");
		return buf.toString();
	}
	public String getZoomedHtml(DashInstance instance, String componentName,
			int width, int height) throws Exception{
		StringBuffer buf = new StringBuffer();

		buf.append("<html xmlns:svg=\"http://www.w3.org/2000/svg\">\n");
		buf.append(generateHTMLHeader(true));
	
		buf.append("<body>\n");
		
		PopupHtmlStructureGenerator gen = new PopupHtmlStructureGenerator(instance,model.getProject().getProjectDescriptor().getInternalApiDesignVersion());
		buf.append(gen.zoom(instance.getDashBoard().getComponent(componentName), width, height));
		buf.append("</body>\n");
		buf.append("</html>");
		return buf.toString();
	}
}
