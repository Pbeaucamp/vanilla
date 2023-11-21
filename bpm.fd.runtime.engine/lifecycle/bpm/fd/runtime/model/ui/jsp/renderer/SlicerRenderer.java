package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.components.definition.slicer.SlicerData;
import bpm.fd.api.core.model.components.definition.slicer.SlicerOptions;
import bpm.fd.api.core.model.components.definition.slicer.SlicerData.Level;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.SlicerState;

public class SlicerRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentSlicer>{

	public String getHTML(Rectangle layout, ComponentSlicer filter, DashState state, IResultSet datas, boolean refresh){
		try{
			return generateSlicer(layout, filter, state, datas, refresh);
		}catch(Exception ex){
			ex.printStackTrace();
			return "<div> error:" + ex.getMessage() + "</div>";
		}
		

	}

	
	
	private String generateSlicer(Rectangle layout, ComponentSlicer component,
			DashState state, IResultSet datas, boolean refresh)throws Exception{
		
		StringBuffer buf = new StringBuffer();
		
		
		SlicerData data = (SlicerData)component.getDatas();
		
		StringBuffer content = new StringBuffer();
		content.append("<div id='" + component.getName() + "_slicer' >\n");
		
		StringBuffer[] lvlsHtml = getDatas2(
				((SlicerOptions)component.getOptions(SlicerOptions.class)).isLevelLinked(),
				state.getSlicerState(component.getName()), component.getName(), data.getLevelCategoriesIndex(), datas);
		for(int i = 0; i < data.getLevelCategoriesIndex().size(); i++){
			content.append("<h3><a href=\"#\">");
			content.append(data.getLevelCategoriesIndex().get(i).getLabel());
		
			content.append("</a>");
			content.append("<input type='checkbox' title='checkall' checked />");
			content.append("</h3>\n");
			content.append("<div>\n");
			//DATAS 
			if (i < lvlsHtml.length && lvlsHtml[i] != null){
				content.append(lvlsHtml[i].toString());
			}
			else{
				content.append("No Values");
			}
			
			
			
			content.append("</div>\n");
		}
		
		
		
		
		content.append("</div>\n");
		
		buf.append(getComponentDefinitionDivStart(layout, component));
		buf.append(content.toString());
		
		buf.append(getComponentDefinitionDivEnd());
		

		buf.append(getJavaScriptFdObjectVariable(component));
		if (!refresh){
			return buf.toString();
		}
		else{
			return content.toString();
		}
		
	}
	
	private static String convertColor(Color color){
		StringBuffer buf = new StringBuffer("#");
		String s = "";
		s = Integer.toHexString(color.getRed());
		if (s.length() < 2){
			s = "0" + s;
		}
		buf.append(s);
		s = Integer.toHexString(color.getGreen());
		if (s.length() < 2){
			s = "0" + s;
		}
		buf.append(s);
		s = Integer.toHexString(color.getBlue());
		if (s.length() < 2){
			s = "0" + s;
		}
		buf.append(s);
		return buf.toString();
	}
	public String getJavaScriptFdObjectVariable(ComponentSlicer component){
		String activeColor = convertColor(((SlicerOptions)component.getOptions(SlicerOptions.class)).getActiveColor());
		String defaultColor = convertColor(((SlicerOptions)component.getOptions(SlicerOptions.class)).getDefaultColor());
	
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    $('#" + component.getName() + "_slicer').multiAccordion();");
		buf.append("    fdObjects[\"" + component.getName() + "\"]= new FdSlicer(\"" + component.getName() + "\", " + 
				((SlicerOptions)component.getOptions(SlicerOptions.class)).isSubmitOnCheck() +
				", " + ((SlicerOptions)component.getOptions(SlicerOptions.class)).isLevelLinked() + ")" + ";\n");
		
		/*
		 * checkAll CallBack
		 */
		buf.append("var el = $('div[id*=_slicer]').children('h3').children('input');" +
				"el.each(function(i){ $(this).click(function(event){event.stopPropagation();fdObjects[\"" + component.getName() + "\"].checkLevel(i);});});\n");
	
		buf.append("el.each(function(){$(this).css('position', 'absolute');$(this).css('top',0);$(this).css('right',0);});\n");
		/*
		 * hack of the jsquery plugin js and css because css classes are applyed by JS on event
		 * and we want to be able to customize those colors
		 */
		//remove the bacckground image on header
		buf.append("    $('div[id*=_slicer]').children('.ui-accordion-header').css('background-image', 'none');\n");
		//set the color for un-expanded headers 
		buf.append("    $('div[id*=_slicer]').children('.ui-accordion-header').css('background-color', '" + defaultColor + "');\n");
		//set the color for expanded headers
		buf.append("    $('div[id*=_slicer]').children('.ui-state-active').filter('.ui-accordion-header').css('background-color', '" + activeColor + "');\n");
		//add a callback when an header is expanded/collapsed to set the right color
		buf.append("    $('div[id*=_slicer]').children('h3').children('a').click(function(){if (!$(this).parent().hasClass('ui-state-active')){$(this).parent().css('background-color','" + activeColor + "');}else{$(this).parent().css('background-color','"+ defaultColor + "');}});\n");
		//TODO: the border could also be customized, but good luck with that, it's really a pain
		
		buf.append("</script>\n");
		return buf.toString();
	}
	
	
	
	
	private class HeaderContent{
		private HashMap<String, String> unames = new HashMap<String, String>();
		public void addUname(String uname, String label){
			unames.put(uname, label);
		}

		public List<String> getKeys() {
			List<String> l = new ArrayList<String>(unames.keySet()
					);
			
			Collections.sort(l, new Comparator<String>(){

				@Override
				public int compare(String o1, String o2) {
					int i1 = o1.lastIndexOf("_");
					int i2 = o2.lastIndexOf("_");
					//no way this occurs
					if (i1 == -1 || i2 == -1){
						return o1.compareTo(o2);
					}
					
					return o1.substring(i1 +1).compareTo(o2.substring(i2 +1));

				}
				
			});
			
			return l;
		}

		public String getLabel(String uname) {
			return unames.get(uname);
		}
	}
	
	private StringBuffer[] getDatas2(boolean isLinkedLevels, SlicerState state, String componentName, List<Level> index, IResultSet datas) throws Exception{
		HeaderContent[] headers = new HeaderContent[index.size()];
		if (datas != null){
			while(datas.next()){
				 for(int i = 0; i < index.size(); i++){
					 if (headers[i] == null){
						 headers[i] = new HeaderContent();
					 }
					 String label = datas.getString(index.get(i).getFieldIndex() + 1);
					 
					 StringBuffer uname = new StringBuffer();
					 for(int k = 0; k <= i; k++){
						 if (uname.length() > 0){
							 uname.append("_");
						 }
						 uname.append(datas.getString(index.get(k).getFieldIndex() + 1));
					 }
					 
					 headers[i].addUname(uname.toString(), label);
				 }
			 }
			datas.close();
		}
		 StringBuffer[] res = new  StringBuffer[index.size()];
		 int i = 0;
		for(HeaderContent h : headers){
			//store unic Values for the nonLinkedOptions and detect double values
			List<String> unicValues = new ArrayList<String>();
			if (res[i] == null) {
				res[i] = new StringBuffer();
			}
			if (h == null){
				res[i].append("No Datas");
				 i++;
				continue;
			}
			for (String uname : h.getKeys()) {
				
				if (!isLinkedLevels){
					boolean present = false;
					for(String s : unicValues){
						if (s.equals( h.getLabel(uname))){
							present = true;
							break;
						}
					}
					if (!present){
						unicValues.add(h.getLabel(uname));
					}
					else{
						continue;
					}
						
				}
				
				String event = "onclick='fdObjects[\"" + componentName + "\"].store(" + i + ",\"" + uname + "\");'";
				 String css = "";
				 String checked = " checked ";
				 
				 state.addLevelValue(i, h.getLabel(uname));
				 res[i].append("<div id='" +componentName + "_" + uname +"' " + css + "><input type=\"checkbox\"  name=\"" + h.getLabel(uname) + "\" value=\"" + h.getLabel(uname) + "\" " + event + checked +  " >" + h.getLabel(uname) + "</div>\n");
				
			}
			 i++;
		}

		 
		 return res;
	}
	
}
