package bpm.fd.runtime.engine;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdVanillaFormModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.FileJavaScript;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.runtime.engine.datas.JSPDataGenerator;
import bpm.fd.runtime.engine.html.JSPStructureGenerator;
import bpm.fd.runtime.model.ui.jsp.HtmlStructureGenerator;

public class JSPGenerator {

//	private JSPDataGenerator dataGen;
	
	/**
	 * 
	 * @param project : project to generate
	 * @param vanillaProfil : Vanilla Context info to securises FMDT connections
	 * @param path : the folder path where the project file will be generated(for
	 * I18N behavior, this value can be null in preview mode from the FD Designer) 
	 * @return
	 * @throws Exception
	 */
	public String generateJSP(FdProject project, VanillaProfil vanillaProfil, String path, String formsUrl, HashMap<String, String> hiddenFieldsNames, GenerationContext generationContext) throws Exception{
		Logger.getLogger(getClass()).info("Generate JSP for item=" + vanillaProfil.getDirectoryItemId() + " projectName=" + project.getProjectDescriptor().getProjectName() + " dictionaryName=" + project.getDictionary().getName() + "/" + project.getProjectDescriptor().getDictionaryName());
		JSPDataGenerator dataGen = new JSPDataGenerator(project.getDictionary());

		List<String> pnames = dataGen.generateJSPCode(project.getFdModel().getComponents(), vanillaProfil);
		HashMap<IComponentDefinition, ComponentConfig> componentsMap = new HashMap<IComponentDefinition, ComponentConfig>();
		if (project instanceof MultiPageFdProject){
			for(FdModel m : ((MultiPageFdProject)project).getPagesModels()){
				HashMap<IComponentDefinition, ComponentConfig> map = m.getComponents();
				
				for(IComponentDefinition key : map.keySet()){
					componentsMap.put(key, map.get(key));
				}
				
			}
			
			
		}
		HashMap<IComponentDefinition, ComponentConfig> map = project.getFdModel().getComponents();
		for(IComponentDefinition key : map.keySet()){
			componentsMap.put(key, map.get(key));
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("<%@page contentType=\"text/html;charset=UTF-8\"%>\n");
		//buf.append("<%@ taglib uri=\"../../tld/fd-taglib.tld\" prefix=\"fd\" %>\n");
		buf.append("<%@ page pageEncoding=\"UTF-8\" %>\n");
		buf.append("<%@page language=\"java\" \n");
	
		buf.append("import=\"" + dataGen.getImports() + "\"%>\n");
		
		buf.append("<%\n");
		
		buf.append("java.io.PrintWriter errorWriter = new java.io.PrintWriter(out);\n");
		buf.append("/*******************************\n");
		buf.append("*           I18N\n");
		buf.append("********************************/\n");
		
		
		
		
		buf.append("    HashMap<String, Properties> localizedFiles = new HashMap<String, Properties>();\n");
		buf.append("    Properties _localizedProp = null;\n");
		
		for(IResource r : project.getResources()){
			
			if (r instanceof FileProperties){
				FileProperties f = (FileProperties)r;
				
				String name = f.getLocaleName() == null ? "Default" : f.getLocaleName();
				buf.append("    _localizedProp = new Properties();\n");
				buf.append("    try{\n");
				if (path != null){
					buf.append("        _localizedProp .load(new FileInputStream(\""+ path.replace("\\", "/") + "\" + \"" + project.getProjectDescriptor().getProjectName() + "/" + f.getName()  + "\"));\n");
				}
				else{
					buf.append("        _localizedProp .load(new FileInputStream(session.getServletContext().getRealPath(\"./\") + \"" + project.getProjectDescriptor().getProjectName() + "/" + f.getName()  + "\"));\n");
				}
				
				buf.append("        localizedFiles.put(\""  + name + "\", _localizedProp);\n");
				buf.append("    }catch(Exception e){\n");
				buf.append("        e.printStackTrace();\n");
				buf.append("    }\n\n");

			}
		}
		buf.append("    // utils for getting label values\n");
		buf.append("    I18NReader i18nReader = new I18NReader(localizedFiles);\n");
		buf.append("    Locale clientLocale = request.getLocale();\n\n\n\n");
		
		buf.append("/*******************************\n");
		buf.append("*           Datas\n");
		buf.append("********************************/\n");
		
		buf.append(dataGen.getJSPBlock());
		
		
		buf.append("%>\n");
		
		buf.append("<html>\n");
		buf.append("    <head>\n");
		buf.append("        <META http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">\n");  
		
		
		//check if google is requested
		boolean googleRequired = false;
		for(IComponentDefinition def : project.getFdModel().getComponents().keySet()){
			if (def instanceof ComponentMap){
				if (((ComponentMap)def).getRenderer() != null && ((ComponentMap)def).getRenderer().getRendererStyle() == MapRenderer.VANILLA_GOOGLE_MAP){
					googleRequired = true;
					break;
				}
			}
		}
		
		buf.append(genarateJavaScript("    ", googleRequired));
		
		
	
		for(IResource r : project.getResources()){
			if (r instanceof FileCSS){
				buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + r.getName() + "\"/>\n");
			}
			if (r instanceof FileJavaScript){
				buf.append("        <script language=\"Javascript\" src=\"" + r.getName() + "\"></script>\n");
				
			}
		}
		
		//javascript parameters
		buf.append("     <script type=\"text/javascript\">\n");
		buf.append("         var parameters = new Array();\n");
		buf.append("         var parametersOut = new Array();\n");
		buf.append("    	 <% \n" );
		buf.append(dataGen.getParameterJavascript() + "\n");
		
		/*
		 * add a parameter for the folders if there is one
		 */
		for(IBaseElement el : project.getFdModel().getContent()){
			if (el instanceof Folder){
				
				String folderValue = "_folder_" + el.getId(); 
				buf.append("String " + folderValue + " = request.getParameter(\"" + folderValue + "\");\n") ;
				buf.append("if (" + folderValue + " == null){\n");
				buf.append(folderValue + " = \"./" + ((FdModel)((FolderPage)((Folder)el).getContent().get(0)).getContent().get(0)).getName() + ".jsp\";\n");
				buf.append("}\n");
				
//				buf.append("               out.write(\"parameters['_folder_" + el.getId() + "'] = " + folderValue + "\");\n");
				buf.append("               out.write(\"parameters[\\\"_folder_" + el.getId() + "\\\"] = '\"+" + folderValue + "+\"';\");\n");
			}
		}
		
		buf.append("    	%>\n");
		if (project.getFdModel().getClass() == FdVanillaFormModel.class && generationContext != null){
			
			buf.append("\n");
			buf.append("    function submitForm(submissionUrl){\n");
			buf.append("        document.mainForm.action=submissionUrl;\n");
			buf.append("        document.mainForm.submit();");
			buf.append("    }\n");
			
			
		}
		buf.append("     </script>\n\n");
		
		buf.append("    </head>\n\n");
		
//		buf.append("    <body>\n");
		
		JSPStructureGenerator structureGenerator = new HtmlStructureGenerator(project.getProjectDescriptor().getInternalApiDesignVersion());
		
		buf.append("    <body " + structureGenerator.generateEvents(project.getFdModel()) + ">\n");
		/*
		 * if FdModel is a FdVanillaForm
		 */
		if (project.getFdModel().getClass() == FdVanillaFormModel.class){
			buf.append("        <form method=\"POST\" action=\"" + formsUrl + "\" name=\"mainForm\">\n");
			
//			/*
//			 * we add hidden field for the VanillaProfil informations(user/password/group/forminstanceId
//			 */
//			buf.append("            <input type=\"hidden\" name=\"" + FormsUIInternalConstants.VANILLA_CTX_LOGIN + "\" />\n");
//			buf.append("            <input type=\"hidden\" name=\"" + FormsUIInternalConstants.VANILLA_CTX_PASSWORD + "\" />\n");
//			buf.append("            <input type=\"hidden\" name=\"" + FormsUIInternalConstants.VANILLA_CTX_GROUP_ID + "\" />\n");
//			buf.append("            <input type=\"hidden\" name=\"" + FormsUIInternalConstants.VANILLA_FORM_INSTANCE_ID + "\" />\n");
		}
		buf.append(structureGenerator.getContent(project.getFdModel(), vanillaProfil) + "\n");
		
		
		if (project.getFdModel().getClass() == FdVanillaFormModel.class){
			
			/*
			 * generate hiddenfields to add some parameters needed for the 
			 * sumitDestination
			 */
			if (hiddenFieldsNames != null){
				for(String s : hiddenFieldsNames.keySet()){
					buf.append("            <input type=\"hidden\" name=\"" + s + "\" value=\"" + (hiddenFieldsNames.get(s) != null ? hiddenFieldsNames.get(s) : "") + "\" />\n");
				}
			}
			
			
			if (generationContext != null){
				buf.append("            <input type=\"button\" value=\"Validate\" onClick=\"submitForm('"+ generationContext.getValidationUrl() + "')\" />\n");
				buf.append("            <input type=\"button\" value=\"Invalidate\" onClick=\"submitForm('" + generationContext.getInvalidationUrl() + "')\" />\n");

			}
			else {
				//check if a submit button is present, if not we generate one
				boolean found = false;
				for(IComponentDefinition def : project.getFdModel().getComponents().keySet()){
					if (def instanceof ComponentButtonDefinition){
						found = true;
						break;
					}
				}
				
				if (!found){
					buf.append("            <input value=\"Submit\" type=\"submit\"/>\n");
				}
			}
			
			buf.append("        </form>\n");
		}
		
		buf.append("    </body>\n");
		buf.append("</html>");
		
		buf.append("<%\n");
		buf.append(dataGen.getCloseQueries(vanillaProfil));
		buf.append("%>");
		return buf.toString();
	}
	
	
	public static String getImportedResources(String spacing, boolean useGoogle){
		StringBuffer buf = new StringBuffer();
		//buf.append(spacing + "	  <script><jsp:include page=\"../../FusionCharts/FusionCharts.js\" flush=\"true\" /></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../FusionCharts/FusionCharts.js\"></script>\n");
//		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../FusionCharts/FusionCharts.HC.js\"></script>\n");
//		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../FusionCharts/FusionCharts.HC.Charts.js\"></script>\n");
//		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../FusionCharts/FusionCharts.jqueryplugin.js\"></script>\n");
//		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../FusionCharts/jquery.min.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../js/url.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../datepicker/datepicker.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../menu/menu.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../datagrid/datagrid.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../ckeditor/ckeditor.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../timer/timer.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../slider/slider.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../ofc/js/swfobject.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../ofc/js/json/json2.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../stackableCell/stackableCell.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../comment/comment.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../comment/listcommentpart.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../FusionMaps/FusionMaps.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../OpenLayers/OpenLayers.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../VanillaMap/VanillaMap.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../fmmap/FMMap.js\"></script>\n");
		buf.append(spacing + "    <script language=\"Javascript\" src=\"../../fmmap/MetricElement.js\"></script>\n");
		//buf.append(spacing + "    <script language=\"Javascript\" src=\"../../googlemap/tooltips.js\"></script>\n");
		
		if (useGoogle){
			buf.append(spacing + "    <script src=\"http://maps.google.com/maps/api/js?sensor=false\" type=\"text/javascript\"></script>\n");
			buf.append(spacing + "    <script src=\"http://gmaps-utility-library-dev.googlecode.com/svn/tags/mapiconmaker/1.1/src/mapiconmaker.js\" type=\"text/javascript\"></script>\n");	
		}
		 
		
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../datagrid/datagrid.css\"/>\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../datepicker/datepicker.css\"/>\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../menu/menu.css\"/>\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../timer/timer.css\" />\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../slider/slider.css\" />\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../stackableCell/stackableCell.css\" />\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../comment/comment.css\" />\n");
		buf.append(spacing + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"../../fmmap/fmmap.css\" />\n");
		
		return buf.toString();
	}
	
	
	private static String genarateJavaScript(String spacing, boolean useGoogle){
		StringBuffer buf = new StringBuffer();
		buf.append(getImportedResources(spacing, useGoogle));		
		
		
		buf.append(spacing + "    <script type=\"text/javascript\">\n");
		buf.append(spacing + "         FusionCharts.setCurrentRenderer('javascript');\n");
		buf.append(JavaScriptCommonGenerator.generateJavaScriptFunctions(spacing + "        "));
		buf.append(spacing + "    </script>\n\n\n");
		
		return buf.toString();
	}
	
	
	
	public String generateJSP(FdModel model, VanillaProfil vanillaProfil, String path, String formsUrl, HashMap<String, String> hiddenFieldsNames) throws Exception{

		JSPDataGenerator dataGen = new JSPDataGenerator(model.getProject().getDictionary());
		HashMap<IComponentDefinition, ComponentConfig> componentsMap = new HashMap<IComponentDefinition, ComponentConfig>();
		if (model.getProject() instanceof MultiPageFdProject){
			for(FdModel m : ((MultiPageFdProject)model.getProject()).getPagesModels()){
				HashMap<IComponentDefinition, ComponentConfig> map = m.getComponents();
				
				for(IComponentDefinition key : map.keySet()){
					componentsMap.put(key, map.get(key));
				}
				
			}
			
			
		}
		HashMap<IComponentDefinition, ComponentConfig> map = model.getComponents();
		for(IComponentDefinition key : map.keySet()){
			componentsMap.put(key, map.get(key));
		}
		
		
		List<String> pnames = dataGen.generateJSPCode(componentsMap, vanillaProfil);
		
		StringBuffer buf = new StringBuffer();
		buf.append("<%@page contentType=\"text/html;charset=UTF-8\"%>\n");
		//buf.append("<%@ taglib uri=\"../../tld/fd-taglib.tld\" prefix=\"fd\" %>\n");
		buf.append("<%@page language=\"java\" \n");
		buf.append("import=\"" + dataGen.getImports() + "\"%>\n");
		
		buf.append("<%\n");
		buf.append("java.io.PrintWriter errorWriter = new java.io.PrintWriter(out);\n");
		buf.append("/*******************************\n");
		buf.append("*           I18N\n");
		buf.append("********************************/\n");
		
		
		
		
		buf.append("    HashMap<String, Properties> localizedFiles = new HashMap<String, Properties>();\n");
		buf.append("    Properties _localizedProp = null;\n");
		
		for(IResource r : model.getProject().getResources()){
			
			if (r instanceof FileProperties){
				FileProperties f = (FileProperties)r;
				
				String name = f.getLocaleName() == null ? "Default" : f.getLocaleName();
				buf.append("    _localizedProp = new Properties();\n");
				buf.append("    try{\n");
				if (path != null){
					buf.append("        _localizedProp .load(new FileInputStream(\""+ path.replace("\\", "/") + "\" + \"" + model.getProject().getProjectDescriptor().getProjectName() + "/" + f.getName()  + "\"));\n");
				}
				else{
					buf.append("        _localizedProp .load(new FileInputStream(session.getServletContext().getRealPath(\"./\") + \"" + model.getProject().getProjectDescriptor().getProjectName() + "/" + f.getName()  + "\"));\n");
				}
				
				buf.append("        localizedFiles.put(\""  + name + "\", _localizedProp);\n");
				buf.append("    }catch(Exception e){\n");
				buf.append("        e.printStackTrace();\n");
				buf.append("    }\n\n");

			}
		}
		buf.append("    // utils for getting label values\n");
		buf.append("    I18NReader i18nReader = new I18NReader(localizedFiles);\n");
		buf.append("    Locale clientLocale = request.getLocale();\n\n\n\n");
		
		buf.append("/*******************************\n");
		buf.append("*           Datas\n");
		buf.append("********************************/\n");
		
		buf.append(dataGen.getJSPBlock());
		
		
		buf.append("%>\n");
		
		buf.append("<html>\n");
		buf.append("    <head>\n");
		buf.append("        <META http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\">\n");  
		
		
		//check if google is requested
		boolean googleRequired = false;
		for(IComponentDefinition def : model.getComponents().keySet()){
			if (def instanceof ComponentMap){
				if (((ComponentMap)def).getRenderer() != null && ((ComponentMap)def).getRenderer().getRendererStyle() == MapRenderer.VANILLA_GOOGLE_MAP){
					googleRequired = true;
					break;
				}
			}
		}
		
		
		buf.append(genarateJavaScript("    ", googleRequired));
		
		
	
		for(IResource r : model.getProject().getResources()){
			if (r instanceof FileCSS){
				buf.append("        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + r.getName() + "\"/>\n");
			}
		}
		
		//javascript parameters
		buf.append("     <script type=\"text/javascript\">\n");
		buf.append("         var parameters = new Array();\n");
		buf.append("         var parametersOut = new Array();\n");
		buf.append("    	 <% \n" );
		buf.append(dataGen.getParameterJavascript() + "\n");
		
				
		/*
		 * add a parameter for the folders if there is one
		 */
		for(IBaseElement el : model.getProject().getFdModel().getContent()){
			if (el instanceof Folder){
				
				String folderValue = "_folder_" + el.getId(); 
				buf.append("String " + folderValue + " = request.getParameter(\"" + folderValue + "\");\n") ;
				buf.append("if (" + folderValue + " == null){\n");
				buf.append(folderValue + " = \"./" + ((FdModel)((FolderPage)((Folder)el).getContent().get(0)).getContent().get(0)).getName() + ".jsp\";\n");
				buf.append("}\n");
				
				buf.append("               out.write(\"parameters[\\\"_folder_" + el.getId() + "\\\"] = '\"+" + folderValue + " + \"';\");\n");
			}
		}
		
		buf.append("    	%>\n");
		buf.append("     </script>\n\n");
		
		buf.append("    </head>\n\n");
		
		JSPStructureGenerator structureGenerator = new HtmlStructureGenerator(model.getProject().getProjectDescriptor().getInternalApiDesignVersion());
		
		buf.append("    <body " + structureGenerator.generateEvents(model) + ">\n");
		
		/*
		 * if FdModel is a FdVanillaForm
		 */
		if (model.getClass() == FdVanillaFormModel.class){
			buf.append("        <form method=\"GET\" action=\"" + formsUrl + "\" >\n");
		}
		buf.append(structureGenerator.getContent(model, vanillaProfil) + "\n");
		
		
		if (model.getClass() == FdVanillaFormModel.class){
			
			/*
			 * generate hiddenfields to add some parameters needed for the 
			 * sumitDestination
			 */
			if (hiddenFieldsNames != null){
				for(String s : hiddenFieldsNames.keySet()){
					buf.append("            <input type=\"hidden\" name=\"" + s + "\" value=\"" + hiddenFieldsNames.get(s) + "\" />\n");
				}
			}
			
			
			buf.append("        </form>\n");
		}
		
		buf.append("    </body>\n");
		buf.append("</html>");
		
		buf.append("<%\n");
		buf.append(dataGen.getCloseQueries(vanillaProfil));
		buf.append("%>");
		return buf.toString();
	}
}

