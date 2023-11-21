package bpm.fd.runtime.engine.components;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.DropDownOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.components.definition.filter.MultipleValuesOptions;
import bpm.fd.api.core.model.components.definition.filter.SliderOptions;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.engine.datas.JSPDataGenerator;

import com.ibm.icu.text.SimpleDateFormat;

public class FilterGenerator {

	public static String generateJspBlock(int offset, ComponentFilterDefinition filter, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		
		/*
		 * check the filter controll a DrillDrivenStackableCell
		 */
		
		List<String> drivenCellsId = new ArrayList<String>();
		
		for(ComponentConfig c : configs){
			if (c instanceof DrillDrivenComponentConfig){
				
				if (((DrillDrivenComponentConfig)c).getController() == filter){
					drivenCellsId.add(((DrillDrivenComponentConfig)c).getDrillDrivenCell().getId());
				}
				
			}
		}
		
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		
		if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX)){
			if (((FilterData)filter.getDatas()).getColumnOrderIndex() != - 1){
				return generateDropDown2(filter,
						buf.toString(), 
						filter.getDatas().getDataSet().getId() + "Query",
						((FilterData)filter.getDatas()).getColumValueIndex(), 
						((FilterData)filter.getDatas()).getColumLabelIndex(),
						((FilterData)filter.getDatas()).getColumnOrderIndex(),
						outputParameterName,
						drivenCellsId);
			}
			else{
				return generateDropDown(filter,
						buf.toString(), 
						filter.getDatas().getDataSet().getId() + "Query",
						((FilterData)filter.getDatas()).getColumValueIndex(), 
						((FilterData)filter.getDatas()).getColumLabelIndex(),
						outputParameterName,
						drivenCellsId);
			}
			
		}
		else if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.SLIDER)){
			return generateSlider(filter,
					buf.toString(), 
					filter.getDatas().getDataSet().getId() + "Query",
					((FilterData)filter.getDatas()).getColumValueIndex(), 
					((FilterData)filter.getDatas()).getColumLabelIndex(),
					((FilterData)filter.getDatas()).getColumnOrderIndex(),
					outputParameterName,
					drivenCellsId);
		}
		else if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.RADIOBUTTON)){
			return generateRadio2(filter,
					buf.toString(), 
					filter.getDatas().getDataSet().getId() + "Query",
					((FilterData)filter.getDatas()).getColumValueIndex(), 
					((FilterData)filter.getDatas()).getColumLabelIndex(),
					((FilterData)filter.getDatas()).getColumnOrderIndex(),
					outputParameterName,
					drivenCellsId);
			
		}
		else if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.MENU)){
			return generateMenu(filter,
					buf.toString(), 
					filter.getDatas().getDataSet().getId() + "Query",
					((FilterData)filter.getDatas()).getColumValueIndex(), 
					((FilterData)filter.getDatas()).getColumLabelIndex(),
					outputParameterName);
		}
		else if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.TEXT_FIELD)){
			return generateText(filter,
					buf.toString(), 
					outputParameterName);
		}
		else if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.DYNAMIC_TEXT)){
			return generateDynamicText(filter,
					buf.toString(), 
					outputParameterName);
		}
		else if (filter.getRenderer() == FilterRenderer.getRenderer(FilterRenderer.DATE_PIKER)){
			return generateDatePicker(filter,
					buf.toString(), 
					outputParameterName);
		}
		
		
		
		return buf.toString();
	}
	
	/***
	 * 
	 * @param filter
	 * @return the <select HTML with the call to JS onChange if requested
	 */
	private static String getSelect(String spacing, final ComponentFilterDefinition filter, String parameterName, List<String> drivenCellsId){
		StringBuffer buf = new StringBuffer();
		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
		
		
		
		String event = "";
		
		final StringBuffer drivenComponentJS = new StringBuffer();
		for(String s : drivenCellsId){
			drivenComponentJS.append("setParameter('"+ s + "', '" + filter.getId() + "');");
		}
		
		if (o.isSubmitOnChange()){
			event = generateEvents(filter, new LinkedHashMap<ElementsEventType, String>(){{put(ElementsEventType.onChange, 
					drivenComponentJS.toString() + "setParameter('"+ filter.getId() + "', this.value);setLocation();");}}, true);
			
		}
		else{
			event = generateEvents(filter, new LinkedHashMap<ElementsEventType, String>(){{put(ElementsEventType.onChange, 
					drivenComponentJS.toString() + "setParameter('"+ filter.getId() + "', this.value);");}}, true);
		}
		
//		if (o.isSubmitOnChange()){
			buf.append(spacing + "    <select id=\"" + filter.getId() + "\" name=\"" + filter.getId() + "\" " + event);
//		}
//		else{
//			buf.append(spacing + "    <select id=\"" + filter.getId() + "\" name=\"" + filter.getId() + "\" " + event);
//		}
		
		if (filter.getOptions(DropDownOptions.class) != null){
			IComponentOptions opt = filter.getOptions(DropDownOptions.class); 
			if (Integer.parseInt(opt.getValue(opt.getNonInternationalizationKeys()[DropDownOptions.KEY_SIZE])) > 0){
				buf.append(" size=\"" + opt.getValue(opt.getNonInternationalizationKeys()[DropDownOptions.KEY_SIZE]) + "\" ");
			}
		}
		

		if (filter.getOptions(MultipleValuesOptions.class) != null){
			IComponentOptions opt = filter.getOptions(MultipleValuesOptions.class); 
			if (Boolean.parseBoolean(opt.getValue(opt.getNonInternationalizationKeys()[MultipleValuesOptions.KEY_MULTIPLE]))){
				buf.append(" multiple=\"true\" ");
			}
		}
		
		if (filter.getCssClass() != null && ! "".equals(filter.getCssClass())){
			buf.append(" class=\"" + filter.getCssClass() + "\" ");
		}
//		buf.append(" " + event);
		buf.append(" >\n");
		
		if (o.isSelectFirstValue() && o.getDefaultValue() != null){
			buf.append(spacing + "        <option>" + o.getDefaultValue() + "</option>\n");
		}
		
		return buf.toString();
	}
	
//	private static String generateRadio(final ComponentFilterDefinition filter, String spacing, String query, int valueIndex, int labelIndex, String outputParameterName, List<String> drivenCellsId){
//		String resultSet = filter.getId() + "ResultSet";
//		
//		StringBuffer buf = new StringBuffer();
//		buf.append(spacing + " <%\n");
//		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
//		buf.append(spacing + "     try{\n");
//		
//		buf.append(JSPDataGenerator.prepareQuery(spacing, query, filter));
//		
//		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
//		buf.append(spacing + "     }catch(Exception e){\n");
//		buf.append(spacing + "         e.printStackTrace();\n");
//		buf.append(spacing + "     }\n");
//		buf.append(spacing + "%>\n\n");
//		
//
//		buf.append(spacing + "        <%\n");
//		buf.append(spacing + "            out.write(\"<p>\");\n");
//		buf.append(spacing + "            while(" + resultSet + " != null && " + resultSet + ".next()){\n");
//		buf.append(spacing + "               try{\n");
//		
//		
//		
//		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
//		
//		String event = null;
//		final StringBuffer drivenComponentJS = new StringBuffer();
//		for(String s : drivenCellsId){
//			drivenComponentJS.append("setParameter('"+ s + "', '" + filter.getId() + "');");
//		}
//		if (o.isSubmitOnChange()){
//			event = generateEvents(filter, new LinkedHashMap<ElementsEventType, String>(){{put(ElementsEventType.onClick, 
//					drivenComponentJS.toString() + "setParameter('"+ filter.getId() + "', this.value);setLocation();");}}, true);
//			
//		}
//		else{
//			event = generateEvents(filter, new LinkedHashMap<ElementsEventType, String>(){{put(ElementsEventType.onClick, 
//					drivenComponentJS.toString() + "setParameter('"+ filter.getId() + "', this.value);");}}, true);
//		}
////		if (o.isSubmitOnChange()){
////			event = "onClick=\\\"setParameter('"+ filter.getId() + "', this.value);setLocation();\\\"";
////		}
////		else{
////			event = "onClick=\\\"setParameter('"+ filter.getId() + "', this.value);\\\"";
////		}
////			
//		
//		
//		if(outputParameterName != null){
//			
//			buf.append(spacing + "                   if (" + outputParameterName + " == null){\n");
//			buf.append(spacing + "                       " + outputParameterName + " = " + resultSet + ".getString(" + valueIndex + ");\n");
//			buf.append(spacing + "                       out.write(\"        <input type='radio' name='" + filter.getId() + "' checked=\\\"checked\\\" value='\" +" + resultSet + ".getString(" + valueIndex);
//			
//			
//			
//			buf.append(") + \"'" + event + " >\" + " + resultSet + ".getString(" + labelIndex + ") + \"</input><br>\");\n");
//			buf.append("out.write(\"<script type=\\\"text/javascript\\\">parameters[\\\"" + filter.getId() + "\\\"]='\"+" + resultSet + ".getString(" + valueIndex + ") + \"';</script>\");\n");
//			buf.append(spacing + "                   }\n");
//			
//			buf.append(spacing + "                   else if (" + outputParameterName + " != null && " + outputParameterName + ".equals(" + resultSet + ".getString(" + valueIndex + ")" + ")){\n");
//			buf.append(spacing + "                       out.write(\"        <input type='radio' name='" + filter.getId() + "' checked=\\\"checked\\\" value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"' " + event + " >\" + " + resultSet + ".getString(" + labelIndex + ") + \"</input><br>\");\n");
//			buf.append(spacing + "                   }else{\n");
//			buf.append(spacing + "                       out.write(\"        <input type='radio' name='" + filter.getId() + "' value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'" + event + " >\" + " + resultSet + ".getString(" + labelIndex + ") + \"</input><br>\");\n");
//			buf.append(spacing + "                   }\n");
//			
//		}
//		else{
//			buf.append(spacing + "                   out.write(\"        <input  type='radio' name='" + filter.getId() + "' value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'" + event + " >\" + " + resultSet + ".getString(" + labelIndex + ") + \"</input><br>\");\n");
//		}
//		
//		
//		
//		buf.append(spacing + "               }catch(Exception e){\n");
//		buf.append(spacing + "                   e.printStackTrace(errorWriter);\n");
//		buf.append(spacing + "               }\n");
//		buf.append(spacing + "            }\n");
//		buf.append(spacing + "           try{\n");
//		buf.append(spacing + "               " + resultSet + ".close();\n");
//		buf.append(spacing + "           }catch(Exception e){\n");
//		buf.append(spacing + "               e.printStackTrace();\n");
//		buf.append(spacing + "           }\n");
//		buf.append(spacing + "            out.write(\"</p>\");\n");
//		buf.append(spacing + "        %>\n");
//		buf.append(spacing + "    </select>\n\n");
//		
//		return buf.toString();
//	}
	
	
	private static String generateDropDown(
			ComponentFilterDefinition filter, 
			String spacing, 
			String query, int valueIndex, int labelIndex, 
			String outputParameterName,
			List<String> drivenCellsId){
		String resultSet = filter.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		buf.append(spacing + "     try{\n");
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, filter));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		buf.append(spacing + "     }catch(Exception e){\n");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
		buf.append(spacing + getSelect(spacing, filter, outputParameterName, drivenCellsId));

		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            while(" + resultSet + " != null && " + resultSet + ".next()){\n");
		buf.append(spacing + "               try{\n");
		
		if(outputParameterName != null){
			
			buf.append(spacing + "                   if (" + outputParameterName + " == null){\n");
			buf.append(spacing + "                       " + outputParameterName + " = " + resultSet + ".getString(" + valueIndex + ");\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			
			buf.append("out.write(\"<script type=\\\"text/javascript\\\">parameters[\\\"" + filter.getId() + "\\\"]=" + resultSet + ".getString(" + valueIndex + ");</script>\");\n");

			buf.append(spacing + "                   }\n");
			
			buf.append(spacing + "                   else if (" + outputParameterName + " != null && " + outputParameterName + ".equals(" + resultSet + ".getString(" + valueIndex + ")" + ")){\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			buf.append(spacing + "                   }else{\n");
			buf.append(spacing + "                       out.write(\"        <option value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			buf.append(spacing + "                   }\n");
			
		}
		else{
//			buf.append(spacing + "                   out.write(\"        <option value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			
			
			
			buf.append(spacing + "                   if (request.getParameter(\"" + filter.getId() + "\") == null){\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			buf.append("out.write(\"<script type=\\\"text/javascript\\\">parameters[\\\"" + filter.getId() + "\\\"]='\"+" + resultSet + ".getString(" + valueIndex + ") + \"';</script>\");\n");

			buf.append(spacing + "                   }\n");
			
			buf.append(spacing + "                   else if (request.getParameter(\"" + filter.getId() + "\") != null && request.getParameter(\"" + filter.getId() + "\").equals(" + resultSet + ".getString(" + valueIndex + ")" + ")){\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			buf.append(spacing + "                   }else{\n");
			buf.append(spacing + "                       out.write(\"        <option value='\" +" + resultSet + ".getString(" + valueIndex + ") + \"'>\" + " + resultSet + ".getString(" + labelIndex + ") + \"</option>\");\n");
			buf.append(spacing + "                   }\n");

			
		}
		
		
		
		buf.append(spacing + "               }catch(Exception e){\n");
		buf.append(spacing + "                   e.printStackTrace();\n");
		buf.append(spacing + "               }\n");
		buf.append(spacing + "            }\n");
		buf.append(spacing + "           try{\n");
		buf.append(spacing + "               " + resultSet + ".close();\n");
		buf.append(spacing + "           }catch(Exception e){\n");
		buf.append(spacing + "               e.printStackTrace();\n");
		buf.append(spacing + "           }\n");
		buf.append(spacing + "        %>\n");
		buf.append(spacing + "    </select>\n\n");
		
		return buf.toString();
	}
	
	/**
	 * generate the STring for events
	 * 
	 * @param element
	 * @param defaultEvents : contains the default behavior for the given Type
	 * @return
	 */
	private static String generateEvents(IBaseElement element, HashMap<ElementsEventType, String> defaultEvents, boolean pureHtml){
		StringBuffer buf = new StringBuffer();
		
		for(ElementsEventType type : element.getEventsType()){
			String sc = element.getJavaScript(type);
			if (sc != null && !"".equals(sc.trim())){
				buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\"") + sc.replace("\r\n", "").replace("\n", ""));
				
				if (defaultEvents == null || defaultEvents.get(type) == null){
					buf.append((pureHtml ? "\"" : "\\\"") + " ");
					continue;
				}
				
			}
			if (defaultEvents != null && defaultEvents.get(type) != null){
				if (sc != null && !"".equals(sc.trim())){
					buf.append(";" );
				}
				else{
					buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\""));
				}
				//whats the fuck
				buf.append(defaultEvents.get(type));
				buf.append((pureHtml ? "\"" : "\\\"") + " ;");
				
			}
			
		}
		
		return buf.toString();
	}
	
	private static String generateText(final ComponentFilterDefinition filter, String spacing,String outputParameterName){
		
		StringBuffer buf = new StringBuffer();
				

		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            out.write(\"<p>\");\n");
		
		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
		String event = "";
		
		if (o.isSubmitOnChange()){
			event = generateEvents(filter, new HashMap<ElementsEventType, String>(){{put(ElementsEventType.onChange, "setParameter('"+ filter.getId() + "', this.value);setLocation();");}}, false);
			
		}
		else{
			event = generateEvents(filter, new HashMap<ElementsEventType, String>(){{put(ElementsEventType.onChange, "setParameter('"+ filter.getId() + "', this.value);");}}, false);
		}
				
		String css = "";
		if (filter.getCssClass() != null && !filter.getCssClass().equals("")&& !filter.getCssClass().equals("null")){
			css= " class='" + filter.getCssClass() + "' ";
		}
		
		String inputType = o.isHidden() ? "hidden" : "text";
		if(outputParameterName != null){
			
			buf.append(spacing + "            if (" + outputParameterName + " != null){\n");
			
			buf.append(spacing + "                out.write(\"        <input type='" + inputType + "' id='" + filter.getId()+ "' name='" + filter.getId() + "' value='\" +" + outputParameterName + "+ \"' " + css + event + " />\");\n");
				
			buf.append(spacing + "            }else{\n");
			buf.append(spacing + "                out.write(\"        <input type='" + inputType + "' id='" + filter.getId() + "' name='" + filter.getId() + "' value='" + o.getDefaultValue() + "' " +css +  event + " />\");\n");
			buf.append(spacing + "            }\n");
			
		}
		else{
			buf.append(spacing + "            if (request.getParameter(\"" + filter.getId() + "\") != null){\n");
			buf.append(spacing + "                out.write(\"        <input type='text' id='" + filter.getId()+ "' name='" + filter.getId()  + "' value='\"+ request.getParameter(\"" + filter.getId() + "\") +\"' " + css + event + " />\");\n");
			buf.append(spacing + "            }else{\n");
			buf.append(spacing + "                out.write(\"        <input type='text' id='" + filter.getId()+ "' name='" + filter.getId()  + "' value='" + o.getDefaultValue() + "' " + css + event + " />\");\n");
			buf.append(spacing + "            }\n");
		}

		buf.append(spacing + "            out.write(\"</p>\");\n");
		buf.append(spacing + "        %>\n");
		
		return buf.toString();
	}
	
	
	
	
	
	private static String generateDatePicker(ComponentFilterDefinition filter, String spacing,String outputParameterName){
		
		StringBuffer buf = new StringBuffer();
				

		buf.append(spacing + "        <%\n");
		
		String events = generateEvents(filter, null, false); 
		if (filter.getCssClass() != null && !"".equals(filter.getCssClass())){
			buf.append(spacing + "            out.write(\"<div id=\\\"" + filter.getId() + "\\\" class=\\\"" + filter.getCssClass() + "\\\" "+ events +" >\");\n");
		}
		else{
			buf.append(spacing + "            out.write(\"<div id=\\\"" + filter.getId() + "\\\" " + events + ">\");\n");
		}
		
		buf.append(spacing + "            out.write(\"</div>\");\n");
		
		
		buf.append(spacing + "            out.write(\"<script language='JavaScript'>\");\n");
		
		
		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
		
		String event = "";
		
		
		
		//test if defaultValue is Ok
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date defaultDate = null;
		try{
			defaultDate = sdf.parse(o.getDefaultValue());
		}catch(ParseException ex){
			
		}
		
		if (o.isSubmitOnChange()){
			event = "\\\"setParameter('"+ filter.getId() + "', " + filter.getId() + "Picker.getFormatedDate());setLocation();\\\"";
		}
		else{
			event = "\\\"setParameter('"+ filter.getId() + "', " + filter.getId() + "Picker.getFormatedDate());\\\"";
		}
				
		if(outputParameterName != null){
			
			
			
			buf.append(spacing + "            if (" + outputParameterName + " != null){\n");
			buf.append("out.write(\" var " + filter.getId() + "_value=parameters[\\\""+ filter.getId() + "\\\"];\");\n");
//			buf.append("out.write(\" var " + filter.getId() + "_value='\"+" + outputParameterName + "+\"';\");\n");
//			buf.append("out.write(\"alert('value=' + " + filter.getId() + "_value);\");\n");
			
			buf.append("out.write(\" var " + filter.getId() + "_index=" + filter.getId() + "_value.indexOf('-');\");\n");
//			buf.append("out.write(\"alert('index=' + " + filter.getId() + "_index);\");\n");
			
			
			buf.append("out.write(\" var " + filter.getId() + "_year=" + filter.getId() + "_value.substring(0," + filter.getId() + "_index );\");\n");
//			buf.append("out.write(\"alert('year=' + " + filter.getId() + "_year);\");\n");
			buf.append("out.write(\" var " + filter.getId() + "_month=" + filter.getId() + "_value.substring(1+" + filter.getId() + "_index, "+ filter.getId() + "_value.lastIndexOf('-') );\");\n");
//			buf.append("out.write(\"alert('month=' + " + filter.getId() + "_month);\");\n");
			buf.append("out.write(\"" + filter.getId() + "_month--;\");\n");
			buf.append("out.write(\"if (" + filter.getId() + "_month==-1) " + filter.getId() + "_month=11;\");\n");
			buf.append("out.write(\"if (" + filter.getId() + "_month==12) " + filter.getId() + "_month=0;\");\n");
			buf.append("out.write(\" var " + filter.getId() + "_day=" + filter.getId() + "_value.substring(1+" + filter.getId() + "_value.lastIndexOf('-'));\");\n");
//			buf.append("out.write(\"alert('day=' + " + filter.getId() + "_day);\");\n");

			buf.append(spacing + "            out.write(\"var " + filter.getId() + "Picker = new Calendar('" + filter.getId() + "Picker', '"+ filter.getId() + "', " + event);// + ");\");\n");
			buf.append(", " +filter.getId() + "_year, " + filter.getId() + "_month, " + filter.getId() + "_day");
			buf.append(");\");\n");
			
			buf.append(spacing + "            }else{\n");
			buf.append(spacing + "            out.write(\"var " + filter.getId() + "Picker = new Calendar('" + filter.getId() + "Picker', '"+ filter.getId() + "', " + event);
			
			Calendar cal = Calendar.getInstance();
//			
			
			if (defaultDate != null){
			cal.setTime(defaultDate);
				buf.append(",'" + cal.get(Calendar.YEAR) + "','" +cal.get(Calendar.MONTH) + "','"+ cal.get(Calendar.DAY_OF_MONTH) + "');\");\n");
			}
			else{
				buf.append(");\");\n");
			}
			
			
			buf.append(spacing + "            }\n");
			buf.append(spacing + "            out.write(\"" + filter.getId()  + "Picker.Draw();\");\n");
		}
		else{
			buf.append(spacing + "            out.write(\"var " + filter.getId() + "Picker = new Calendar('" + filter.getId() + "Picker', '"+ filter.getId() + "', " + event);
//			
			Calendar cal = Calendar.getInstance();
//			
//			
			if (defaultDate != null){
				cal.setTime(defaultDate);
				buf.append(",'" + cal.get(Calendar.YEAR) + "','" + cal.get(Calendar.MONTH) + "','"+ cal.get(Calendar.DAY_OF_MONTH) + "');\");\n");
			}
			else{
				buf.append(");\");\n");
			}
			buf.append(spacing + "            out.write(\"" + filter.getId()  + "Picker.Draw();\");\n");
		}
		
		buf.append(spacing + "            out.write(\"</script>\");\n");
		

		
		buf.append(spacing + "        %>\n");
		
		return buf.toString();
	}
	
	
	
	private static String generateMenu(ComponentFilterDefinition filter, String spacing, String query, int valueIndex, int labelIndex, String outputParameterName){
		String resultSet = filter.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		
		//generate javascript creation Menu
		
		MenuOptions mOpt = (MenuOptions)filter.getOptions(MenuOptions.class);
		
		buf.append("<script type='text/javascript'>\n");
		buf.append("    var menu_" + filter.getId() + " = new Menu('menu_" + filter.getId() + "',");
		buf.append("'menu_container_" +filter.getId() + "',");
		buf.append( mOpt.getIsVertical() + ",");
		buf.append(mOpt.getSize() + ",");   
		buf.append("new Array(");
		//datas
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		buf.append(spacing + "     try{\n");
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, filter));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		buf.append(spacing + "     }catch(Exception e){\n");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
		
		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            boolean _isFirst_" + filter.getId() + " = true;\n");
		buf.append(spacing + "            while(" + resultSet + " != null && " + resultSet + ".next()){\n");
		buf.append(spacing + "               try{\n");
		buf.append(spacing + "                   if(_isFirst_" + filter.getId() + "){\n");
		buf.append(spacing + "                      _isFirst_" + filter.getId() + "=false;");
		buf.append(spacing + "                   }\n");
		buf.append(spacing + "                   else{\n");
		buf.append(spacing + "                      out.write(\",\");\n");
		buf.append(spacing + "                   }\n");
		buf.append(spacing + "                   out.write(\"'\"+" + resultSet + ".getString(" + valueIndex + ") + \"'\");\n");
		
		
		buf.append(spacing + "               }catch(Exception e){\n");
		buf.append(spacing + "                   e.printStackTrace();\n");
		buf.append(spacing + "               }\n");
		buf.append(spacing + "            }\n");
		buf.append(spacing + "           try{\n");
		buf.append(spacing + "               " + resultSet + ".close();\n");
		buf.append(spacing + "           }catch(Exception e){\n");
		buf.append(spacing + "               e.printStackTrace();\n");
		buf.append(spacing + "           }\n");
		buf.append(spacing + "            out.write(\"),\");\n");
		buf.append(spacing + "        %>\n");
		
		//onclick
		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
		
		String event = null;
		
		if (o.isSubmitOnChange()){
			event = "setParameter('"+ filter.getId() + "',  menu_" + filter.getId() + ".currentValue);setLocation();";
		}
		else{
			event = "setParameter('"+ filter.getId() + "',  menu_" + filter.getId() + ".currentValue);";
		}
		buf.append("\"" + event + "\",");
		buf.append(mOpt.getWidth());//width
		
		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            if (" + outputParameterName+ " != null){"+ "\n");
		buf.append(spacing + "                out.write(\",\" + \"'\" + " + outputParameterName + " + \"'\");\n");
		buf.append(spacing + "            }\n");
		buf.append(spacing + "        %>\n");
		
		buf.append(");");
		
		buf.append("</script>\n");
		
		
		
		

		
		
		String events = generateEvents(filter, null, false);
		
		buf.append("<p id='menu_container_" + filter.getId() + "'");
		
		if (filter.getCssClass() != null && !"".equals(filter.getCssClass())){
			buf.append(" class='" + filter.getCssClass() + "'");
		}
		
		buf.append(" " + events + " >\n");
		buf.append("    <script type='text/javascript'>menu_" + filter.getId() + ".render();</script>\n");
		buf.append("</p>\n");
		return buf.toString();
	}
	
	

	/*
	 * new implementation to use the FilterData.columnOrder info
	 * the function will throw an Exception if the orderIndex <= 0
	 */
	private static String generateDropDown2(
			ComponentFilterDefinition filter, 
			String spacing, 
			String query, 
			int valueIndex, 
			int labelIndex, 
			int orderIndex, 
			String outputParameterName,
			List<String> drivenCellsId){
		String resultSet = filter.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		buf.append(spacing + "     List<List<Object>> " + resultSet + "Values = null;\n");
		buf.append(spacing + "     try{\n");
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, filter));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		buf.append(spacing + "        " + resultSet + "Values = Sorter.sort(" + resultSet + ", " + orderIndex +");\n");
		buf.append(spacing + "     }catch(Exception e){\n");
//		buf.append(spacing + "         e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
		buf.append(spacing + getSelect(spacing, filter, outputParameterName, drivenCellsId));
		
		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            Iterator<List<Object>> " + resultSet + "Iter = " + resultSet + "Values.iterator();\n");
		buf.append(spacing + "            while(" + resultSet + "Iter != null && " + resultSet + "Iter.hasNext()){\n");
		buf.append(spacing + "               List<Object> row = " + resultSet + "Iter.next();\n");
		buf.append(spacing + "               try{\n");
		
		if(outputParameterName != null){
			
			buf.append(spacing + "                   if (" + outputParameterName + " == null){\n");

			buf.append(spacing + "                       " + outputParameterName + " = row.get(" + (valueIndex -1 )+ ") != null ? row.get(" + (valueIndex -1 )+ ").toString() : null;\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" + URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"'>\" + row.get(" + (labelIndex -1 ) + ") + \"</option>\");\n");
			
			buf.append("out.write(\"<script type=\\\"text/javascript\\\">parameters[\\\"" + filter.getId() + "\\\"]='\"+URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\")+\"';</script>\");\n");

			buf.append(spacing + "                   }\n");
			
			buf.append(spacing + "                   else if (row.get(" + (valueIndex -1 ) + ") != null && " + outputParameterName + " != null && " + outputParameterName + ".equals(row.get(" + (valueIndex -1 ) + ").toString()" + ")){\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" + URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"'>\" + row.get(" + (labelIndex -1 ) + ") + \"</option>\");\n");
			buf.append(spacing + "                   }else{\n");
			buf.append(spacing + "                       out.write(\"        <option value='\" +URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"'>\" + row.get(" + (labelIndex -1 ) + ") + \"</option>\");\n");
			buf.append(spacing + "                   }\n");
			
		}
		else{
			buf.append(spacing + "                   if (request.getParameter(\"" + filter.getId() + "\") == null){\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" +URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"'>\" + row.get(" + (labelIndex -1 ) + ") + \"</option>\");\n");
			buf.append("out.write(\"<script type=\\\"text/javascript\\\">parameters[\\\"" + filter.getId() + "\\\"]='\"+URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"';</script>\");\n");

			buf.append(spacing + "                   }\n");
			
			buf.append(spacing + "                   else if (row.get(" + (valueIndex -1 ) + ") != null && request.getParameter(\"" + filter.getId() + "\") != null && request.getParameter(\"" + filter.getId() + "\").equals(row.get(" + (valueIndex -1 ) + ").toString()" + ")){\n");
			buf.append(spacing + "                       out.write(\"        <option selected=\\\"selected\\\" value='\" +URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"'>\" + row.get(" + (labelIndex -1 ) + ") + \"</option>\");\n");
			buf.append(spacing + "                   }else{\n");
			buf.append(spacing + "                       out.write(\"        <option value='\" +URLEncoder.encode(row.get(" + (valueIndex -1 ) + ").toString(), \"UTF-8\") + \"'>\" + row.get(" + (labelIndex -1 ) + ") + \"</option>\");\n");
			buf.append(spacing + "                   }\n");

			
		}
		
		
		
		buf.append(spacing + "               }catch(Exception e){\n");
//		buf.append(spacing + "                   e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "                   e.printStackTrace();\n");
		buf.append(spacing + "               }\n");
		buf.append(spacing + "            }\n");
		buf.append(spacing + "           try{\n");
		buf.append(spacing + "               " + resultSet + ".close();\n");
		buf.append(spacing + "           }catch(Exception e){\n");
		buf.append(spacing + "               e.printStackTrace();\n");
		buf.append(spacing + "           }\n");
		buf.append(spacing + "        %>\n");
		buf.append(spacing + "    </select>\n\n");
		
		return buf.toString();
	}
	
	/*
	 * new implementation to use the FilterData.columnOrder info
	 * the function will throw an Exception if the orderIndex <= 0
	 */
	private static String generateRadio2(final ComponentFilterDefinition filter, String spacing, String query, int valueIndex, int labelIndex, int orderIndex, String outputParameterName, List<String> drivenCellsId){
		String resultSet = filter.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		buf.append(spacing + "     List<List<Object>> " + resultSet + "Values = null;\n");
		buf.append(spacing + "     try{\n");
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, filter));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		if (orderIndex != -1){
			buf.append(spacing + "        " + resultSet + "Values = Sorter.sort(" + resultSet + ", " + orderIndex +");\n");
		}
		
		buf.append(spacing + "     }catch(Exception e){\n");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		

		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            out.write(\"<p>\");\n");
		buf.append(spacing + "            Iterator<List<Object>> " + resultSet + "Iter = " + resultSet + "Values.iterator();\n");
		buf.append(spacing + "            while(" + resultSet + "Iter != null && " + resultSet + "Iter.hasNext()){\n");
		buf.append(spacing + "               List<Object> row = " + resultSet + "Iter.next();\n");
		buf.append(spacing + "               try{\n");
		
		
		
		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
		
		String event = null;
		
		final StringBuffer drivenComponentJS = new StringBuffer();
		for(String s : drivenCellsId){
			drivenComponentJS.append("setParameter('"+ s + "', '" + filter.getId() + "');");
		}
		if (o.isSubmitOnChange()){
			event = generateEvents(filter, new LinkedHashMap<ElementsEventType, String>(){{put(ElementsEventType.onChange, 
					drivenComponentJS.toString() + "setParameter('"+ filter.getId() + "', this.value);setLocation();");}}, true);
			
		}
		else{
			event = generateEvents(filter, new LinkedHashMap<ElementsEventType, String>(){{put(ElementsEventType.onChange, 
					drivenComponentJS.toString() + "setParameter('"+ filter.getId() + "', this.value);");}}, true);
		}
	
		if(outputParameterName != null){
			
			buf.append(spacing + "                   if (" + outputParameterName + " == null){\n");
			buf.append(spacing + "                       " + outputParameterName + " = row.get(" + (valueIndex -1 )+ ") != null ? row.get(" + (valueIndex -1 )+ ").toString() : null;\n");
			buf.append("%>\n");
			buf.append(spacing + "                       <input type='radio' name='" + filter.getId() + "' checked='checked' value='<%= row.get(" + (valueIndex - 1));
			buf.append(")%>'" + event + " ><%=row.get(" + (labelIndex - 1) + ")%> </input><br>\n");
			buf.append("<%\n");
			buf.append("out.write(\"<script type=\\\"text/javascript\\\">parameters[\\\"" + filter.getId() + "\\\"]='\"+ row.get(" + (valueIndex - 1) + ") + \"';</script>\");\n");
			buf.append(spacing + "                   }\n");
			
			buf.append(spacing + "                   else if (row.get(" + (valueIndex -1 ) + ") != null && " + outputParameterName + " != null && " + outputParameterName + ".equals(row.get(" + (valueIndex - 1) + ").toString()" + ")){\n");
			buf.append("%>\n");
			buf.append("<input type='radio' name='" + filter.getId() + "' checked='checked' value='<%= row.get(" + (valueIndex - 1) + ")%>' " + event + " ><%= row.get(" + (labelIndex - 1) + ")%></input><br>\n");
			buf.append("<%\n");
			buf.append(spacing + "                   }else{\n");
			buf.append("%>\n");
			buf.append("<input type='radio' name='" + filter.getId() + "' value='<%=row.get(" + (valueIndex - 1) + ")%>' " + event + " ><%=row.get(" + (labelIndex - 1) + ")%></input><br>\n");
			buf.append("<%\n");
			buf.append(spacing + "                   }\n");
			
		}
		else{
			buf.append("%>\n");
			buf.append("<input  type='radio' name='" + filter.getId() + "' value='<%=row.get(" + (valueIndex - 1) + ")%>' " + event + " ><%=row.get(" + (labelIndex - 1) + ")%></input><br>\n");
			buf.append("<%\n");
		}
		
		
		
		buf.append(spacing + "               }catch(Exception e){\n");
//		buf.append(spacing + "                   e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "                   e.printStackTrace();\n");
		buf.append(spacing + "               }\n");
		buf.append(spacing + "            }\n");
		buf.append(spacing + "           try{\n");
		buf.append(spacing + "               " + resultSet + ".close();\n");
		buf.append(spacing + "           }catch(Exception e){\n");
		buf.append(spacing + "               e.printStackTrace();\n");
		buf.append(spacing + "           }\n");
		buf.append(spacing + "            out.write(\"</p>\");\n");
		buf.append(spacing + "        %>\n");
		buf.append(spacing + "    </select>\n\n");
		
		return buf.toString();
	}
	
	private static String generateDynamicText(ComponentFilterDefinition filter, String spacing,String outputParameterName){
		
		

//		<script type="text/javascript">
//		lavariable = "MaValeur";
//		document.write ('<input type="text" name="champ" value="' + lavariable + '">');
//		</script>

		String event = generateEvents(filter, null, false);
		
		
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + "<script type=\"text/javascript\">\n");
		buf.append(spacing + "document.write ('<input id=\"" + filter.getId() + "\" type=\"text\" name=\"" +  filter.getId() + "\" value=\"' + eval(\"" + filter.getValueJavaScript().replace("\r\n", "").replace("\"", "\\\"") + "\") + '\" disabled=\"true\" />\\n');\n");
		buf.append(spacing + "</script>\n");
//		buf.append(spacing + "<%\n");
//		buf.append(spacing + "out.write (\"<input type=\\\"text\\\" name=\\\"" +  filter.getId() + "\\\" value=\\\"eval(\\\"" + filter.getValueJavaScript().replace("\r\n", "").replace("\"", "\\\"") + "\\\")\\\" disabled=\\\"true\\\" " + event + "/>\\n\");\n");
//		buf.append(spacing + "%>\n");
		
		return buf.toString();
	}
	
	
	
	
	
	
	
	private static String generateSlider(ComponentFilterDefinition filter, String spacing, String query, int valueIndex, int labelIndex, int orderIndex, String outputParameterName, List<String> drivenCellsId){
		String resultSet = filter.getId() + "ResultSet";
		
		
		
		
		StringBuffer buf = new StringBuffer();
		
		buf.append(spacing + "<div id=\"" + filter.getName() + "Slider\"></div>\n");
		buf.append(spacing + "<script language=\"javascript\">\n");
//		buf.append(spacing + "    var \n");
		
		
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     String " + filter.getName() + "SliderValues = \"new Array(\";\n");
		buf.append(spacing + "     String " + filter.getName() + "SliderLabels = \"new Array(\";\n");
		buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		buf.append(spacing + "     List<List<Object>> " + resultSet + "Values = null;\n");
		buf.append(spacing + "     try{\n");
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, filter));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		buf.append(spacing + "        " + resultSet + "Values = Sorter.sort(" + resultSet + ", " + orderIndex +");\n");
		buf.append(spacing + "     }catch(Exception e){\n");
//		buf.append(spacing + "         e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
//		buf.append(spacing + getSelect(spacing, filter, outputParameterName));
		
		buf.append(spacing + "        <%\n");
		buf.append(spacing + "            Iterator<List<Object>> " + resultSet + "Iter = " + resultSet + "Values.iterator();\n");
		buf.append(spacing + "            boolean _first = true;\n");
		buf.append(spacing + "            while(" + resultSet + "Iter != null && " + resultSet + "Iter.hasNext()){\n");
		buf.append(spacing + "               List<Object> row = " + resultSet + "Iter.next();\n");
		buf.append(spacing + "               try{\n");
		buf.append(spacing + "                   if (_first){_first=false;}else{\n");
		buf.append(spacing + "                       " +filter.getName()  + "SliderValues+=\",\";\n");
		buf.append(spacing + "                       " +filter.getName()  + "SliderLabels+=\",\";\n");
		buf.append(spacing + "                   }\n");
		buf.append(spacing + "                   " + filter.getName() + "SliderValues+=\"\\\"\"+ row.get( " + (valueIndex  - 1)+ ") + \"\\\"\";\n");
		buf.append(spacing + "                   " + filter.getName() + "SliderLabels+=\"\\\"\"+ row.get( " + (labelIndex - 1) + ") + \"\\\"\";\n");
	
		
		buf.append(spacing + "               }catch(Exception e){\n");
//		buf.append(spacing + "                   e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "                   e.printStackTrace();\n");
		buf.append(spacing + "               }\n");
		buf.append(spacing + "            }\n");
		buf.append(spacing + "           try{\n");
		buf.append(spacing + "               " + resultSet + ".close();\n");
		buf.append(spacing + "           }catch(Exception e){\n");
//		buf.append(spacing + "               e.printStackTrace(errorWriter);\n");
		buf.append(spacing + "               e.printStackTrace();\n");
		buf.append(spacing + "           }\n");
		buf.append(spacing + "     " + filter.getName() + "SliderValues+= \")\";\n");
		buf.append(spacing + "     " + filter.getName() + "SliderLabels+= \")\";\n");
		buf.append(spacing + "        %>\n");

		
		SliderOptions opt = (SliderOptions)filter.getOptions(SliderOptions.class);
		final StringBuffer drivenComponentJS = new StringBuffer();
		for(String s : drivenCellsId){
			drivenComponentJS.append("setParameter('"+ s + "', '" + filter.getId() + "');");
		}		
		String	event = "if (!_slider" + filter.getName() + ".dragging){";
		event += drivenComponentJS.toString();
		event += "setParameter('"+ filter.getId() + "', _slider" + filter.getName() + ".getSelectedValue());";
		if (((FilterOptions)filter.getOptions(FilterOptions.class)).isSubmitOnChange()){
			
			
			event += "setLocation();";
		}
		event += "}";;
		
		
		

		
		
		
		String defaultValue = "";
		if (((FilterOptions)filter.getOptions(FilterOptions.class)).getDefaultValue() != null && !"".equals(((FilterOptions)filter.getOptions(FilterOptions.class)).getDefaultValue())){
			defaultValue = ((FilterOptions)filter.getOptions(FilterOptions.class)).getDefaultValue();
		}
		
		
		buf.append(spacing + "        <%\n");
		buf.append(spacing + "             if (" + outputParameterName + " == null){\n");
		buf.append(spacing + "        %>\n");
		
		buf.append(spacing + "    var _slider"  + filter.getName() + " = new SimpleSlider('" + filter.getName() + "Slider'");
		buf.append(", " + opt.getWidth() + ", " + opt.getHeight() + ", " + opt.getDelay() + ", '" + opt.getBarColor() + "', '" + opt.getSliderColor() + "', ");
		buf.append("<%= " + filter.getName() + "SliderValues%>, <%=" + filter.getName() + "SliderLabels%>, '" + defaultValue + "', " + (opt.isAutoRun() ? 1 : 0) + ");\n");
		buf.append(spacing + "    _slider" + filter.getName() + ".onNewPosition = function(){" + event + "}\n");
		
		if ( opt.isAutoRun()){
			buf.append(spacing + "    _slider"  + filter.getName() + ".run();\n");
		}
		buf.append(spacing + "        <%\n");
		buf.append(spacing + "             }\n");
		buf.append(spacing + "             else{\n");
		buf.append(spacing + "        %>\n");
		
		if (!(outputParameterName == null || "null".equalsIgnoreCase(outputParameterName))){
			buf.append(spacing + "    var _slider"  + filter.getName() + " = new SimpleSlider('" + filter.getName() + "Slider'");
			buf.append(", " + opt.getWidth() + ", " + opt.getHeight() + ", " + opt.getDelay() + ", '" + opt.getBarColor() + "', '" + opt.getSliderColor() + "', ");
			buf.append("<%= " + filter.getName() + "SliderValues%>, <%=" + filter.getName() + "SliderLabels%>,");
			buf.append("'<%= " + outputParameterName + "%>', " + (opt.isAutoRun() ? 1 : 0));
			buf.append(");\n");
			buf.append(spacing + "    _slider" + filter.getName() + ".onNewPosition = function(){" + event + "}\n");

			
			if ( opt.isAutoRun()){
				buf.append(spacing + "    _slider"  + filter.getName() + ".run();\n");
			}
			buf.append(spacing + "        <%\n");
			buf.append(spacing + "             }\n");
			buf.append(spacing + "        %>\n");
		}
		else{
			buf.append(spacing + "        <%\n");
			buf.append(spacing + "             }\n");
			buf.append(spacing + "        %>\n");
		}
		
		
		
		
		
		
		
//		buf.append(", 500, 10, 'silver', '#666666', ");
//		buf.append("<%= " + filter.getName() + "SliderValues%>, <%=" + filter.getName() + "SliderLabels%>);\n");
//		buf.append(spacing + "    _slider" + filter.getName() + ".onNewPosition = function(){" + event + "}\n");
		buf.append(spacing + "</script>\n");
		return buf.toString();
	}
}
