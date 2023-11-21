package bpm.fd.runtime.model.ui.jsp;

import org.apache.log4j.Logger;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.html.JSPStructureGenerator;
import bpm.fd.runtime.engine.utils.StackableCellComponentType;
import bpm.fd.runtime.model.ui.jsp.renderer.IHTMLRenderer;

public class HtmlStructureGenerator extends JSPStructureGenerator{


	public HtmlStructureGenerator(String runtimeApiVersion) {
		super(runtimeApiVersion);
		
	}
	@Override
	protected void generateComponent(int offset, IComponentDefinition def,
			String outputParameterName, VanillaProfil vanillaProfil)
			throws Exception {
		
		StringBuffer buf = new StringBuffer();
//		buf.append("<div id='" + def.getId() + "'>\n");
//		buf.append("</div>\n");
		
		buf.append("<%\n");
		buf.append("out.write(dashInstance.renderComponent(\"" + def.getName() + "\", request.getParameter(\"exportdashboard\")));\n");
		buf.append("%>\n");
		
		html.append(buf.toString());
	}
	protected void generateDrillDrivenStackableCell(int offset, DrillDrivenStackableCell cell, VanillaProfil vanillaProfil){
//		StringBuffer buf = new StringBuffer();
		html.append("\n");
		html.append("<div id='" + cell.getName() + "' name='"  + cell.getName() + "'>\n");
		
		for(IBaseElement e : cell.getContent()){
			if (isFreeLayout()){
				DrillDrivenComponentConfig conf = (DrillDrivenComponentConfig)cell.getConfig((IComponentDefinition)e);
				
				if (conf.getController() != null){
					try {
						generateComponent(1, (IComponentDefinition)e, pOuts.get((IComponentDefinition)e), vanillaProfil);
					} catch (Exception e1) {
						html.append("");
						e1.printStackTrace();
					}
				}
			}
			else{
				html.append("<div id='" + e.getName() + "' name='" + e.getId() + "' ></div>\n");
				try{
					html.append(((IHTMLRenderer<IBaseElement>)JSPRenderer.get(e.getClass())).getJavaScriptFdObjectVariable(e));
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			
			
		}
		
		html.append("</div>\n");
		
		String defaultShown = null;
		if (!cell.getContent().isEmpty()){
			defaultShown = cell.getContent().get(0).getName();
		}
		//JavascriptVariable
		html.append("<script type=\"text/javascript\">\n");
		//XXX		
		html.append("    var filterDirection = document.getElementById('Toolbar_Filter_Direction');\n");
		html.append("    if(filterDirection == undefined) {\n");
		html.append("    	filterDirection = document.getElementById('FilterDirection');\n");
		html.append("    }\n");
		
		html.append("    var val = filterDirection.value;\n");
		
		html.append("    if(val != '%') {\n");
		html.append("    	fdObjects[\"" + cell.getName() + "\"]= new FdDynamicStackableCell(\"" + cell.getName() + "\", '" + cell.getContent().get(2).getName() + "');\n");
		html.append("    }\n");
		
		html.append("    else {\n");
		html.append("    	var filterDga = document.getElementById('Toolbar_Filter_DGA');\n");
		html.append("    	if(filterDga == undefined) {\n");
		html.append("    		filterDga = document.getElementById('FilterDGA');\n");
		html.append("    	}\n");
			
		html.append("    	val = filterDga.value;\n");
			
		html.append("    	if(val != '%') {\n");
		html.append("    		fdObjects[\"" + cell.getName() + "\"]= new FdDynamicStackableCell(\"" + cell.getName() + "\", '" + cell.getContent().get(1).getName() + "');\n");
		html.append("    	}\n");
			
		html.append("    	else {	\n");
				
		
		html.append("    fdObjects[\"" + cell.getName() + "\"]= new FdDynamicStackableCell(\"" + cell.getName() + "\", '" + defaultShown + "')" + ";}}\n");
		//XXX
		html.append("</script>\n");
	}
	
	@Override
	protected void generateStackableCell(int offset, StackableCell cell, VanillaProfil vanillaProfil){
//		StringBuffer buf = new StringBuffer();
		html.append("\n");
		
		
		StringBuilder layoutS = new StringBuilder();
		if (isFreeLayout()){
			layoutS.append(" style='height:");
			layoutS.append(cell.getSize().y);
			layoutS.append("px;");
			layoutS.append("width:");
			layoutS.append(cell.getSize().y);
			layoutS.append("px;");
			layoutS.append("position:absolute;");
			layoutS.append("top:");
			layoutS.append(cell.getPosition().y);
			layoutS.append("px;");
			layoutS.append("left:");
			layoutS.append(cell.getPosition().x);
			layoutS.append("px'");
		}
		

		
		html.append("<div id='" + cell.getName() + "' name='"  + cell.getName() + "' " + layoutS + ">\n");
		
		StringBuffer types = new StringBuffer();
		StringBuffer names = new StringBuffer();
		
		for(ComponentConfig e : cell.getConfigs()){
			if (types.toString().length() == 0){
				types.append("[");
				names.append("[");
			}
			else{
				types.append(",");
				names.append(",");
			}
				
			try {
				generateComponent(1, e.getTargetComponent(), pOuts.get(e.getTargetComponent()), vanillaProfil);
				
				types.append("'" + StackableCellComponentType.getTypeByComponent(e.getTargetComponent()) + "'");
				names.append("'" + e.getTargetComponent().getName() + "'");

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		types.append("]");
		names.append("]");
		
		html.append("</div>\n");
		
		//JavascriptVariable
		html.append("<script type=\"text/javascript\">\n");
		if (cell instanceof DrillDrivenStackableCell){
			String defaultComponent = "";
			try{
				defaultComponent = ((DrillDrivenStackableCell)cell).getComponents().get(0).getName();
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("The DrillDriveStackableCell seems to be empty");
			}
			
			html.append("    fdObjects[\"" + cell.getName() + "\"]= new FdDynamicStackableCell(\"" + cell.getName() + "\", '" + defaultComponent + "')" + ";\n");
		}
		else{
			html.append("    fdObjects[\"" + cell.getName() + "\"]= new StackableCell(\"" + cell.getName() + "\", " + types + "," + names + ",0,'" + cell.getType() + "')" + ";\n");
			html.append("    fdObjects[\"" + cell.getName() + "\"].draw();\n");

		}
		html.append("</script>\n");
	}
	
	protected void generateFolder(int offset, Folder folder, VanillaProfil vanillaProfil){
		//generate Position
		StringBuilder styleS = new StringBuilder();
		if (isFreeLayout() && folder.getPosition() != null){
			styleS.append(" style='");
			styleS.append("position:relative;");
			styleS.append("top:");
			styleS.append(folder.getPosition().y);
			styleS.append("px;'");
//			styleS.append("left:");
//			styleS.append(layout.x);
//			styleS.append("px'");
			
		}
		//generate Menu
		html.append("<div id='" + folder.getName() + "' " + styleS.toString() + ">\n");
		
		
		
		
		html.append("<ul");
		if (folder.getCssClass() != null){
			html.append(" class=\"" + folder.getCssClass() + "\"");	
		}
		html.append(">\n");

		for(IBaseElement e : folder.getContent()){
			FolderPage page = ((FolderPage)e);
			
			html.append("<li id='" + page.getName() + "_item'><a id='folder_"+page.getName()+"' href=\"javascript:setParameter('" + folder.getName() + "', '" + page.getName() + "', true);\">" + page.getTitle() + "</a></li>\n");
		}
		html.append("</ul>\n");
		
		//
		String defaultShown = null;
		for(IBaseElement e : folder.getContent()){
			FolderPage page = ((FolderPage)e);
			html.append("<div id='" + page.getName() + "' ");
			if (!page.getContent().isEmpty()){
				FdModel m = (FdModel)page.getContent().get(0);
				
				if (m.getCssClass() != null){
					html.append(" class='" + m.getCssClass() + "' ");
				}
			}
			if (defaultShown == null){
				defaultShown = page.getName();
				html.append(" style='position:relative;' >&nbsp;\n");
			}
			else {
				html.append(" style='position:relative;display:none;' >&nbsp;\n");
			}
			
			if (!page.getContent().isEmpty()){
				for(IBaseElement d : page.getContent()){
					generateStructure(offset, d, vanillaProfil);
				}
			}
			
			
			html.append("</div>\n");
		}
		html.append("</div>");
		//JavascriptVariable
		html.append("<script type=\"text/javascript\">\n");
		html.append("    fdObjects[\"" + folder.getName() + "\"]= new FdFolder(\"" + folder.getName() + "\", '" + defaultShown + "')" + ";\n");
		html.append("</script>\n");
		
	}
}
