package bpm.fd.design.ui.editor.style;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to parse a cssClass from csss content or write css from CssClass and an original css content
 * @author ludo
 *
 */
public class CssHelper {

	public static CssClass readCssClass(String classname, String cssCode) {
		int index = cssCode.indexOf("." + classname.trim());
		if (index == -1) {
			index = cssCode.indexOf("#" + classname.trim());
		}
		CssClass css = new CssClass(classname);
		if (index == -1) {
			return css;
		}

		String toParse = cssCode.substring(cssCode.indexOf("{", index) + 1 ,cssCode.indexOf("}", index));
		
		String[] properties = toParse.split(";");
		
		for(String p : properties){
			if (p.trim().isEmpty()){continue;}
			String pName = p.substring(0, p.indexOf(":")).trim();
			String pValue = p.substring(p.indexOf(":") + 1).trim();
			
			for(CssConstants k : CssConstants.values()){
				if (pName.trim().equals(k.getName())){
					css.setValue(pName, pValue);
				}
			}
			
		}
		
		
		return css;
	}
	
	public static String writeCssClass(CssClass classname, String originalCode ){
		StringBuffer buf = new StringBuffer();
		
		int start = originalCode.indexOf("." + classname.getName());
		if (start == -1){
			//TODO : write fully the new css class
			buf.append(originalCode);
			buf.append("\n");
			buf.append("." + classname.getName() + "{");
			for(CssConstants k : CssConstants.values()){
				if (classname.getValue(k.getName()) != null){
					buf.append(k.getName() + ":" + classname.getValue(k.getName()) + ";");
				}
			}
			buf.append("}\n");
		}
		else{
			
			
			start = originalCode.indexOf("{", start) + 1;
			int end = originalCode.indexOf("}",start);
			
			buf.append(originalCode.substring(0, start));
			
			String[] properties = originalCode.substring(start, end).split(";");
			
			List<String> writtenP = new ArrayList<String>();
			
			for(String p : properties){
				if (!p.contains(":")){
					continue;
				}
				String pName = p.substring(0, p.indexOf(":")).trim();
				
				boolean written = false;
				for(CssConstants k : CssConstants.values()){
					if (pName.trim().equals(k.getName())){
						written = true;
						if (classname.getValue(pName) != null){
							buf.append(pName + ": " + classname.getValue(pName) + ";\n");
							
							writtenP.add(pName.trim());
							break;
						}
						
						
					}
				}
				if (!written){
					buf.append(p);buf.append(";\n");
				}
			}
			for(String s : classname.getProperties()){
				if (classname.getValue(s) != null && !writtenP.contains(s)){
					buf.append(s + ": " + classname.getValue(s) + ";");
				}
			}
			
//			for(CssConstants k : CssConstants.values()){
//				if (classname.getValue(k.getName()) != null && !writtenP.contains(k.getName())){
//					buf.append(k.getName() + ": " + classname.getValue(k.getName()) + ";");
//				}
//			}
			
			buf.append(originalCode.substring(end));
		}
		
		
		return buf.toString();
	}
}
