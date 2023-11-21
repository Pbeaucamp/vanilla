package bpm.fd.runtime.engine.html;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.utils.StackableCellComponentType;

public abstract class JSPStructureGenerator {
	protected StringBuffer html = new StringBuffer();
	protected HashMap<IComponentDefinition, String> pOuts = null;
	
	private String runtimeApiVersion ;
	
	public JSPStructureGenerator(String runtimeApiVersion){
		this.runtimeApiVersion = runtimeApiVersion;
	}
	
	protected boolean isFreeLayout(){
		return this.runtimeApiVersion != null && this.runtimeApiVersion.equals(FdProjectDescriptor.API_DESIGN_VERSION);
	}
	public String getContent(FdModel model, VanillaProfil vanillaProfil){
		
		
		pOuts = model.getComponentsOutputVariables();
		
		html = new StringBuffer();
		generateStructure(0, model, vanillaProfil);
		return html.toString();
	}
	
	public String generateEvents(IBaseElement element){
		StringBuffer buf = new StringBuffer();
		
		for(ElementsEventType type : element.getEventsType()){
			String sc = element.getJavaScript(type);
			if (sc != null && !"".equals(sc.trim())){
				buf.append(" " + type.name() + "=\"" + sc.replace("\r\n", "").replace("\n", "") + "\" ");
			}
		}
		
		return buf.toString();
	}
	
	protected void generateStructure(int offset, IBaseElement parent, VanillaProfil vanillaProfil){
		if (parent instanceof IStructureElement){
			for(IBaseElement baseE : ((IStructureElement)parent).getContent()){

				if (baseE instanceof Table){
					generateTable(offset + 1, (Table)baseE, vanillaProfil);
				}
				else if (baseE instanceof Folder){
					generateFolder(offset + 1, (Folder)baseE, vanillaProfil);
				}
				else if(baseE instanceof DrillDrivenStackableCell) {
					generateDrillDrivenStackableCell(offset + 1, (DrillDrivenStackableCell)baseE, vanillaProfil);
				}
				else if(baseE instanceof StackableCell) {
					generateStackableCell(offset + 1, (StackableCell)baseE, vanillaProfil);
				}
				else if(baseE instanceof DivCell) {
					generateDivCell(offset + 1, (DivCell)baseE, vanillaProfil);
				}
				else if (baseE instanceof Cell){
					generateCell(offset + 1, (Cell)baseE, vanillaProfil);
				}
				
				else if (baseE instanceof IComponentDefinition){
					try {
						generateComponent(offset + 1, (IComponentDefinition)baseE, pOuts.get((IComponentDefinition)baseE), vanillaProfil);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		else{
			try {
				generateComponent(offset + 1, (IComponentDefinition)parent, pOuts.get((IComponentDefinition)parent), vanillaProfil);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	private void generateDivCell(int offset, DivCell cell, VanillaProfil vanillaProfil) {

		StringBuffer off = new StringBuffer();
		for(int i = 0; i < offset * 4; i++){
			off.append(" ");
		}
		
		String spacing = off.toString();
		
		String css = " class='";
		if(cell.getCssClass() != null) {
			css += cell.getCssClass();
		}
		css += " cell' ";
		
		Rectangle layout = new Rectangle((int)cell.getPosition().getX(), (int)cell.getPosition().getY(), (int)cell.getSize().getX(), (int)cell.getSize().getY());

		StringBuilder layoutS = new StringBuilder();
		if(layout != null) {
			layoutS.append(" style='height:");
			layoutS.append(layout.height);
			layoutS.append("px;");
			layoutS.append("width:");
			layoutS.append(layout.width);
			layoutS.append("px;");
			layoutS.append("position:absolute;");
			layoutS.append("top:");
			layoutS.append(layout.y);
			layoutS.append("px;");
			layoutS.append("left:");
			layoutS.append(layout.x);
			layoutS.append("px'");

		}
		
		html.append(spacing + "<div id='" + cell.getName() + "' name='" + cell.getId() + "'" + css + layoutS.toString());
		html.append(generateEvents(cell));
		html.append(">\n");
		
		generateStructure(offset + 1, cell, vanillaProfil);
		
		html.append(spacing + "            </div>\n");
		
	}

	private void generateTable(int offset, Table table, VanillaProfil vanillaProfil){
		StringBuffer off = new StringBuffer();
		for(int i = 0; i < offset * 4; i++){
			off.append(" ");
		}
		
		String spacing = off.toString();
		
		html.append(spacing+ "    <table id=\"" + table.getId() +"\"" );
		if (table.getCssClass() != null){
			html.append(" class=\"" + table.getCssClass() + "\"");	
		}
		html.append(generateEvents(table));
		html.append(">\n");
		for(List<Cell> row : table.getDetailsRows()){
			html.append(spacing + "        <tr>\n");
			
			for(Cell cell : row){
				if (cell != null){
					generateCell(offset, cell, vanillaProfil);
				}
				
			}
			html.append(spacing + "        </tr>\n");
		}
		html.append(spacing + "    </table>\n");
	}
	
	
	
	
	protected void generateFolder(int offset, Folder folder, VanillaProfil vanillaProfil){
		StringBuffer off = new StringBuffer();
		for(int i = 0; i < offset * 4; i++){
			off.append(" ");
		}
		
		String spacing = off.toString();
		
		
		html.append(spacing+ "    <ul  id=\"" + folder.getId() +"\"" );
		if (folder.getCssClass() != null){
			html.append(" class=\"" + folder.getCssClass() + "\"");	
		}
		html.append(generateEvents(folder));
		html.append(">\n");
		
		String folderValue = "_folder_" + folder.getId();
		html.append("<% String " + folderValue + "LiCss = \"\"; \n");
		html.append(" String " + folderValue + "ACss = \"\"; \n%>\n");
		for(Object o : folder.getContent()){
			FolderPage page = (FolderPage) o;
			
			String url = "";
			if (!page.getContent().isEmpty() ){
				String jspName = ((FdModel)page.getContent().get(0)).getName().replace(" ", "_");
				url = "./" + jspName + ".jsp";
			}
			else{
				url = "#";
			}
			
			
			html.append("<%\nif (" + folderValue + ".equals(\"" + url + "\")){");
			html.append("    " + folderValue +"LiCss= \"class=\\\"" + folder.getComponentStyle().getStyleFor(Folder.CSS_ACTIVE_LI) + "\\\" \";" );
			html.append("    " + folderValue +"ACss= \"class=\\\"" + folder.getComponentStyle().getStyleFor(Folder.CSS_ACTIVE_A) + "\\\" \";" );
			
			html.append("}\n");
			html.append("else{" + folderValue +"LiCss=\"\";" + folderValue +"ACss=\"\";}");
				
			
			html.append("%>\n");
			String title = "";
			
			try{
				title = new String(page.getTitle().getBytes());
			}catch(Exception ex){
				title = page.getTitle();
			}
			html.append(spacing + "        <li <%= " + folderValue + "LiCss %> ><a <%= " + folderValue + "ACss %> href=\"javascript:setParameter('" + folderValue + "', '" +  url + "');setLocation();\" >" + title + "</a></li>\n");

		}
		html.append(spacing + "</ul>\n");
		html.append("<jsp:include page=\"<%= " + folderValue + " %>\" flush=\"true\" />\n");
		
	
	}

	protected void generateDrillDrivenStackableCell(int offset, DrillDrivenStackableCell cell, VanillaProfil vanillaProfil){

		html.append("<%\n");
		boolean first = true;
		for(IBaseElement e : cell.getContent()){
			DrillDrivenComponentConfig conf = (DrillDrivenComponentConfig)cell.getConfig((IComponentDefinition)e);
			if (conf.getController() != null){
				
				if (first){
					first = false;
				}
				else{
					html.append("\nelse ");
				}
				html.append("if (\"" + conf.getController().getId() +  "\".equals(" + cell.getId() + ")){\n");
				html.append("%>\n");
				try {
					generateComponent(offset + 1, (IComponentDefinition)e, pOuts.get((IComponentDefinition)e), vanillaProfil);
				} catch (Exception e1) {
					html.append("");
					e1.printStackTrace();
				}
				html.append("<%\n");
				html.append("}");
				
			}
			
			
		}
		
		
		html.append("%>\n");
	}
	
	private void generateCell(int offset, Cell cell, VanillaProfil vanillaProfil){
		
		if (cell instanceof DrillDrivenStackableCell){
			generateDrillDrivenStackableCell(offset, (DrillDrivenStackableCell)cell, vanillaProfil);
			return;
		}
		else if (isFreeLayout()){
			generateStructure(offset + 1, cell, vanillaProfil);
			return;
		}
		
		StringBuffer off = new StringBuffer();
		for(int i = 0; i < offset * 4; i++){
			off.append(" ");
		}
		
		String spacing = off.toString();
		
		html.append(spacing + "            <td id=\"" + cell.getId() + "\" rowspan=\"" + cell.getRowSpan() + "\" colspan=\"" + cell.getColSpan() + "\"");
		if (cell.getCssClass() != null){
			html.append(" class=\"" + cell.getCssClass() + "\"");	
		}
		html.append(generateEvents(cell));
		html.append(">\n");
		
		generateStructure(offset + 1, cell, vanillaProfil);
		
		
		html.append(spacing + "            </td>\n");
	}
	
	protected void generateStackableCell(int offset, StackableCell stackCell, VanillaProfil vanillaProfil) {
		StringBuffer off = new StringBuffer();
		for(int i = 0; i < offset * 4; i++){
			off.append(" ");
		}
		
		String spacing = off.toString();
		
		html.append(spacing + "				<div id=\"" + stackCell.getId() + "\"");
		if(stackCell.getCssClass() != null) {
			html.append(" class=\"" + stackCell.getCssClass() + "\"");	
		}
		html.append(generateEvents(stackCell));
		html.append(">\n");
		
		String componentsType = "[";
		String componentsName = "[";
		
		int i = 0;
		for(IBaseElement element : stackCell.getContent()) {
			if(element instanceof IComponentDefinition) {
				if(i != 0) {
					componentsType += ",";
					componentsName += ",";
				}
				
				IComponentDefinition compo = (IComponentDefinition) element;
				componentsType += "\"" +StackableCellComponentType.getTypeByComponent(compo) + "\"";
				componentsName += "\"" + compo.getName() + "\"";
				
				html.append(spacing + "					<div id=\"" + compo.getName() + "\" ");
				html.append(">\n");
				
				try {
					generateComponent(offset + 1, compo, pOuts.get(compo), vanillaProfil);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//put the js after the closing div (for msie)
				
				String script = "";
				if(compo.getDatas() != null && compo.getDatas().getDataSet() != null) {
					int start = html.lastIndexOf("<script");
					if(start == -1) {
						start = html.lastIndexOf("<script type=\"text/javascript\"");
					}
					if (start != -1){
						int end = html.lastIndexOf("</script>");
						if (end != -1){
							script = html.substring(start, end ) + "</script>";
							html = html.replace(start, end, "");
						}
						else{

						}
						
					}
					
					
					
				}
				
				html.append(spacing + "					</div>\n");
				html.append(script);
			}
			i++;
		}
		
		componentsType += "]";
		componentsName += "]";
		
		html.append(spacing + "				</div>\n");
		html.append(spacing + "				<script type=\"text/javascript\">\n");
		
		html.append(spacing + "					var " + stackCell.getId() + " = new StackableCell(\"" + stackCell.getId() + "\", " + componentsType + ", " + componentsName + ", 0, " + "\"" + stackCell.getType() + "\");\n");
		html.append(spacing + "					" + stackCell.getId() + ".draw();\n");
		html.append(spacing + "				</script>\n");
		
	}
	
	
	protected abstract void generateComponent(int offset, IComponentDefinition def, String outputParameterName, VanillaProfil vanillaProfil) throws Exception;
	
}
