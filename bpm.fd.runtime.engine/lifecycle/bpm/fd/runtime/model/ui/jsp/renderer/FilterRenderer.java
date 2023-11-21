package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.components.definition.filter.SliderOptions;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.engine.datas.helper.Sorter;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashState;

public class FilterRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentFilterDefinition> {

	public String getHTML(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		switch(((bpm.fd.api.core.model.components.definition.filter.FilterRenderer) filter.getRenderer()).getRendererStyle()) {
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.DROP_DOWN_LIST_BOX:
				return generateDropDorwn(layout, filter, state, datas, refresh);
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.RADIOBUTTON:
				return generateRadio(layout, filter, state, datas, refresh, false);
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.CHECKBOX:
				return generateRadio(layout, filter, state, datas, refresh, true);
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.SLIDER:
				// return generateSlider(layout, filter, state, datas, refresh);
				return generateSliderJQuery(layout, filter, state, datas, refresh);
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.DATE_PIKER:
				return generateDatePicker(layout, filter, state, datas, refresh);
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.TEXT_FIELD:
				return generateTextField(layout, filter, state, datas, refresh);
			case bpm.fd.api.core.model.components.definition.filter.FilterRenderer.MENU:
				return generateMenu(layout, filter, state, datas, refresh);
			default:
				return generateDropDorwn(layout, filter, state, datas, refresh);
		}

	}

	private String generateMenu(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		FilterData fdatas = (FilterData) filter.getDatas();

		HashMap<ElementsEventType, String> defaultEvents = new HashMap<ElementsEventType, String>();

		String event = generateEvents(filter, defaultEvents, true);

		StringBuffer buf = new StringBuffer();

		buf.append(getComponentDefinitionDivStart(layout, filter));
		if(filter.getLabel() != null) {
			buf.append(filter.getLabel());
		}
		
		MenuOptions mOpt = (MenuOptions)filter.getOptions(MenuOptions.class);
		
		buf.append("<script type='text/javascript'>\n");
		buf.append("    var menu_" + filter.getId() + " = new Menu('menu_" + filter.getId() + "',");
		buf.append("'menu_container_" +filter.getId() + "',");
		buf.append( mOpt.getIsVertical() + ",");
		buf.append(mOpt.getSize() + ",");   
		buf.append("new Array(");
		
		boolean isFirst = true;
		try {
			List<List<Object>> l = Sorter.sort(datas, fdatas.getColumnOrderIndex(), OrderingType.ASC);
			for(List<Object> row : l) {
				String label = row.get(fdatas.getColumnLabelIndex() - 1).toString();
				String value = row.get(fdatas.getColumnValueIndex() - 1).toString();
				
				if(isFirst) {
					isFirst = false;
				}
				else {
					buf.append(",\n");
				}
				buf.append("'"+value+"'");
				
			}
		} catch(OdaException e) {
			e.printStackTrace();
		}
		buf.append(")");
		FilterOptions o = (FilterOptions)filter.getOptions(FilterOptions.class);
		
		String even = null;
		if (o.isSubmitOnChange()){
			even = "setParameter('"+ filter.getId() + "',  menu_" + filter.getId() + ".currentValue);refreshDirtyComponents();";
		}
		else{
			even = "setParameter('"+ filter.getId() + "',  menu_" + filter.getId() + ".currentValue);";
		}

		buf.append(",\"" + even + "\",");
		buf.append(mOpt.getWidth());//width
		
		buf.append(");");

		buf.append("</script>\n");
		
		buf.append("<p id='menu_container_" + filter.getId() + "'");
		
		if (filter.getCssClass() != null && !"".equals(filter.getCssClass())){
			buf.append(" class='" + filter.getCssClass() + "'");
		}
		
		buf.append(" " + event + " >\n");
		buf.append("    <script type='text/javascript'>menu_" + filter.getId() + ".render();</script>\n");
		buf.append("</p>\n");
		
		return buf.toString();
	}

	private String generateDropDorwn(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		FilterData fdatas = (FilterData) filter.getDatas();

		HashMap<ElementsEventType, String> defaultEvents = new HashMap<ElementsEventType, String>();

		String event = generateEvents(filter, defaultEvents, true);

		StringBuffer buf = new StringBuffer();

		buf.append(getComponentDefinitionDivStart(layout, filter));
		if(filter.getLabel() != null) {
			buf.append(filter.getLabel());
		}
		if(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.LIST) {
			buf.append("<select id=\"" + filter.getId() + "\" style=\"height: 100%;width: calc(100% - 60px);\" name=\"" + filter.getId() + "\" " + event + " multiple>\n");
		}
		else {
			buf.append("<select id=\"" + filter.getId() + "\" name=\"" + filter.getId() + "\" " + event + ">\n");
		}

		StringBuffer content = new StringBuffer();
		
		boolean refreshComponents = ((FilterOptions) filter.getOptions(FilterOptions.class)).isSubmitOnChange();
		boolean hasDatas = false;
		String firstData = null;
		try {
			List<List<Object>> l = Sorter.sort(datas, fdatas.getColumnOrderIndex(), fdatas.getOrderType());
			for(List<Object> row : l) {
				hasDatas = true;
				String label = row.get(fdatas.getColumnLabelIndex() - 1).toString();
				String value = row.get(fdatas.getColumnValueIndex() - 1).toString();
				
				if(label.endsWith(".0")) {
					label = label.replace(".0", "");
				}
				if(value.endsWith(".0")) {
					value = value.replace(".0", "");
				}
				
				if(firstData == null) {
					firstData = value;
				}
				
				content.append("<option value='" + value + "' ");

				if(state.getComponentValue(filter.getName()) == null) {
					//XXX
					
					if(firstData != null && !firstData.equals("%")) {
						if(filter.getName().equals("FilterDirection") || filter.getName().equals("Toolbar_Filter_Direction") ) {
							
							ComponentRuntime comp = state.getDashInstance().getDashBoard().getComponent("FilterDirection");
							if(comp == null) {
								comp = state.getDashInstance().getDashBoard().getComponent("Toolbar_Filter_Direction");
							}
							for(ComponentRuntime t : comp.getTargets(true)) {
								if(t.getName().startsWith("DrillDrivenStackableCell")) {
									state.setComponentValue(t.getName(), filter.getName());
								}
							}
						}
						else {
							if(filter.getName().equals("FilterDGA") || filter.getName().equals("Toolbar_Filter_DGA") ) {
								ComponentRuntime comp = state.getDashInstance().getDashBoard().getComponent("FilterDGA");
								if(comp == null) {
									comp = state.getDashInstance().getDashBoard().getComponent("Toolbar_Filter_DGA");
								}
								for(ComponentRuntime t : comp.getTargets(true)) {
															
									if(t.getName().startsWith("DrillDrivenStackableCell")) {	
										if((state.getComponentValue(t.getName())) != null && (state.getComponentValue(t.getName()).equals("FilterDirection") 
												|| state.getComponentValue(t.getName()).equals("Toolbar_Filter_Direction"))) {
											break;
										}
							
										state.setComponentValue(t.getName(), filter.getName());
									}
								}
							}
						}
					}
					
					//XXX
					
					state.setComponentValue(filter.getName(), value);
					content.append(" selected='selected' ");
				}
				else if(label.equals(state.getComponentValue(filter.getName()))) {
					content.append(" selected='selected' ");
				}
				else if(value.equals(state.getComponentValue(filter.getName())) && !refresh) {
					content.append(" selected='selected' ");
				}
				// add
				if(!(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.LIST)) {
					content.append(" onclick=\"" + "setParameter('" + filter.getName() + "',this.value, " + refreshComponents + ");" + "\" ");
				}
				content.append(">" + label + "</option>\n");
			}

		} catch(Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(FilterRenderer.class).error("Failed to generate HTML", ex);
		}
		if(!hasDatas) {
			content.append("<option>No Datas Available</option>\n");
		}

		if(!refresh) {
			buf.append(content.toString());
		}
		else {
			return content.toString();
		}
		buf.append("</select>\n");
		if((filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.LIST)) {
			//create a refresh button
			buf.append("<input type='image' width='16px' height='16px' src='/VanillaRuntime/freedashboardRuntime/js/icon_refresh.png' onclick=\"" + generateListClickFunction(filter) + "\">\n");
		}
		buf.append(getComponentDefinitionDivEnd());
		// buf.append(getComponentDefinitionDivEnd());
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", 'dropDown')" + ";\n");

		//chrome does not support onclick on options by default
		//I put that if here you know to not fucking override every onchange events.
		if(!(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.LIST)) {
			if(!event.contains("onChange=")) {
				buf.append("var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;\n");
				buf.append("if (is_chrome){\n");
				buf.append("	var filterContainer = document.getElementById('" + filter.getName() + "');\n");
				buf.append("	filterContainer.setAttribute('onchange','this.options[this.selectedIndex].onclick();');\n");
				buf.append("}\n");
			}
		}

		buf.append("</script>\n");
		return buf.toString();
	}
	
	private String generateListClickFunction(ComponentFilterDefinition filter) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("var values = '';");
		buf.append("var x=document.getElementById('" + filter.getName() + "');");
		buf.append("for (var i = 0; i < x.options.length; i++) {");
		buf.append("	if(x.options[i].selected ==true){");
		buf.append("		if(values != ''){values += ','}");
		buf.append("		values += x.options[i].value;");
		buf.append("	}");
		buf.append("}");
		buf.append("setParameter('" + filter.getName() + "',values, " + ((FilterOptions) filter.getOptions(FilterOptions.class)).isSubmitOnChange() + ");");
		
		return buf.toString();
	}

	public String getJavaScriptFdObjectVariable(ComponentFilterDefinition filter) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");

		String type = "'radio'";
		if(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.DROP_DOWN_LIST_BOX) {
			type = "'dropDown'";
		}
		if(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.RADIOBUTTON) {
			type = "'radio'";
		}
		if(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.SLIDER) {
			type = "'slider'";
		}
		if(filter.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.DATE_PIKER) {
			type = "'datepicker'";
		}

		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", " + type + ")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	private String generateTextField(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		// FilterData fdatas = (FilterData)filter.getDatas();
		//		
		// String event = " onClick=\"setParameter('" + filter.getName() + "',this.value);\" ";
		HashMap<ElementsEventType, String> defaultEvents = new HashMap<ElementsEventType, String>();

		String event = generateEvents(filter, defaultEvents, true);
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, filter));
		if(filter.getLabel() != null) {
			buf.append(filter.getLabel());
		}
		StringBuffer content = new StringBuffer();
		content.append("<input id='" + filter.getName() + "' type='text' ");
		content.append(" name='" + filter.getName() + "' ");
		content.append(event);
		if(state.getComponentValue(filter.getName()) == null) {
			FilterOptions opts = (FilterOptions) filter.getOptions(FilterOptions.class);
			if(opts != null && opts.getDefaultValue() != null) {
				content.append(" value='" + opts.getDefaultValue() + "' ");
			}
		}
		else {
			content.append(" value='" + state.getComponentValue(filter.getName()) + "' ");
		}
		content.append("/>");

		if(!refresh) {
			buf.append(content.toString());
		}
		else {
			return content.toString();
		}

		buf.append(getComponentDefinitionDivEnd());

		buf.append("<script type=\"text/javascript\">\n");

		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", 'datepicker')" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	private String generateDatePicker(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		// FilterData fdatas = (FilterData)filter.getDatas();
		//		
		// String event = " onClick=\"setParameter('" + filter.getName() + "',this.value);\" ";

		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, filter));

		FilterOptions opts = (FilterOptions) filter.getOptions(FilterOptions.class);
		if(opts != null && opts.getDefaultValue() != null) {
			buf.append("<input id='datepicker_" + filter.getId() + "' type='text' value='" + opts.getDefaultValue() + "'/>\n");
		}
		else {
			buf.append("<input id='datepicker_" + filter.getId() + "' type='text' />\n");
		}
		
		

		buf.append(getComponentDefinitionDivEnd());

		buf.append("<script type=\"text/javascript\">\n");
		boolean refreshComponents = ((FilterOptions) filter.getOptions(FilterOptions.class)).isSubmitOnChange();
		buf.append("$(function() {$( \"#datepicker_" + filter.getId() + "\" ).datepicker({onSelect: function(dateText, inst) {setParameter('" + filter.getName() + "',dateText, " + refreshComponents + "); }});});\n");

		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", 'datepicker')" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	private String generateSliderJQuery(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		FilterData fdatas = (FilterData) filter.getDatas();
		//		
		// String event = " onClick=\"setParameter('" + filter.getName() + "',this.value);\" ";

		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, filter));
		StringBuffer content = new StringBuffer();
		content.append("<select id='slider_" + filter.getName() + "' name='slider_" + filter.getName() + "' style='display:none' >\n");

		boolean refreshComponents = ((FilterOptions) filter.getOptions(FilterOptions.class)).isSubmitOnChange();
		boolean hasDatas = false;
		int countRow = 0;
		try {
			List<List<Object>> l = Sorter.sort(datas, fdatas.getColumnOrderIndex(), OrderingType.ASC);
			for(List<Object> row : l) {
				hasDatas = true;
				String label = row.get(fdatas.getColumnLabelIndex() - 1).toString();
				String value = row.get(fdatas.getColumnValueIndex() - 1).toString();
				content.append("<option value='" + value + "' ");

				if(state.getComponentValue(filter.getName()) == null) {
					state.setComponentValue(filter.getName(), value);
					content.append(" selected='selected' ");
				}
				else if(label.equals(state.getComponentValue(filter.getName()))) {
					content.append(" selected='selected' ");
				}
				// add
				content.append(">" + label + "</option>\n");
				countRow++;
			}

		} catch(Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(FilterRenderer.class).error("Failed to generate HTML", ex);
		}
		if(!hasDatas) {
			content.append("<option>No Datas Available</option>\n");
		}

		if(!hasDatas) {
			content.append("<option>No Datas Available</option>\n");
		}

		content.append("</select>\n");

		if(!refresh) {
			buf.append(content.toString());
		}
		else {
			return content.toString();
		}

		buf.append(getComponentDefinitionDivEnd());

		buf.append("<script type=\"text/javascript\">\n");

		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", 'slider', " + refreshComponents + ")" + ";\n");

		// buf.append("    $('select#slider_" + filter.getName() + "').selectToUISlider().next();\n");
		buf.append("    $('select#slider_" + filter.getName() + "').selectToUISlider({labels:" + countRow + ", sliderOptions:{change:function(event,ui){setParameter('" + filter.getName() + "',$($('select#slider_" + filter.getName() + "').children()[ui.value]).attr('value'), " + refreshComponents + ");}}}).next();\n");

		buf.append("</script>\n");
		return buf.toString();
	}

	private String generateRadio(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh, boolean isCheckBox) {
		FilterData fdatas = (FilterData) filter.getDatas();
		boolean refreshComponents = ((FilterOptions) filter.getOptions(FilterOptions.class)).isSubmitOnChange();
		String event = " onClick=\"setParameter('" + filter.getName() + "',this.value, " + refreshComponents + ");\" ";
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, filter));
		if(filter.getLabel() != null) {
			buf.append(filter.getLabel());
		}
		StringBuffer content = new StringBuffer();

		boolean hasDatas = false;
		try {
			List<List<Object>> l = Sorter.sort(datas, fdatas.getColumnOrderIndex(), OrderingType.ASC);
			for(List<Object> row : l) {
				hasDatas = true;
				String label = row.get(fdatas.getColumnLabelIndex() - 1).toString();
				String value = row.get(fdatas.getColumnValueIndex() - 1).toString();
				if(isCheckBox) {
					content.append("<input type='checkbox' value='" + value + "' ");
				}
				else {
					content.append("<input type='radio' value='" + value + "' ");
				}
				content.append(" name='" + filter.getName() + "' ");
				if(state.getComponentValue(filter.getName()) == null) {
					state.setComponentValue(filter.getName(), value);
					content.append(" checked='checked' ");
				}
				else if(value.equals(state.getComponentValue(filter.getName()))) {
					content.append(" checked='checked' ");
				}
				content.append(event);
				content.append(">" + label + "</input>\n");
			}

		} catch(Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(FilterRenderer.class).error("Failed to generate HTML", ex);
		}
		if(!hasDatas) {
			content.append("<option>No Datas Available</option>\n");
		}

		if(!refresh) {
			buf.append(content.toString());
		}
		else {
			return content.toString();
		}
		buf.append(getComponentDefinitionDivEnd());

		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", 'radio')" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	private String generateSlider(Rectangle layout, ComponentFilterDefinition filter, DashState state, IResultSet datas, boolean refresh) {
		FilterData fdatas = (FilterData) filter.getDatas();

		StringBuffer values = new StringBuffer();
		values.append("new Array(");
		StringBuffer labels = new StringBuffer();
		labels.append("new Array(");

		try {
			List<List<Object>> rows = Sorter.sort(datas, fdatas.getColumnOrderIndex(), OrderingType.ASC);
			boolean first = true;
			for(List<Object> r : rows) {
				Object value = r.get(fdatas.getColumnValueIndex() - 1);
				Object label = r.get(fdatas.getColumnLabelIndex() - 1);
				if(first) {
					first = false;
				}
				else {
					values.append(",");
					labels.append(",");
				}
				if(value == null) {
					values.append("null");
				}
				else {
					values.append("'" + value.toString() + "'");
				}
				if(label == null) {
					labels.append("null");
				}
				else {
					labels.append("'" + label.toString() + "'");
				}

				if(state.getComponentValue(filter.getName()) == null && value != null) {
					state.setComponentValue(filter.getName(), value.toString());
				}
			}
		} catch(Exception ex) {
			Logger.getLogger(getClass()).error("Could not exrtact datas " + ex.getMessage(), ex);
		}

		labels.append(")");
		values.append(")");

		boolean refreshComponents = ((FilterOptions) filter.getOptions(FilterOptions.class)).isSubmitOnChange();

		String event = "if (!_slider" + filter.getId() + ".dragging){";
		event += "setParameter('" + filter.getName() + "', _slider" + filter.getId() + ".getSelectedValue(), " + refreshComponents + ");";
		event += "}";;

		StringBuffer buf = new StringBuffer();
		SliderOptions opt = (SliderOptions) filter.getOptions(SliderOptions.class);
		buf.append(getComponentDefinitionDivStart(layout, filter));
		buf.append(getComponentDefinitionDivEnd());

		StringBuffer content = new StringBuffer();

		content.append("<script type=\"text/javascript\">\n");

		content.append("var _slider" + filter.getId() + "=");
		content.append("new SimpleSlider(");
		content.append("'" + filter.getName() + "',");
		content.append(opt.getWidth() + ",");
		content.append(opt.getHeight() + ",");
		content.append(opt.getDelay() + ",");
		content.append("'" + opt.getBarColor() + "',");
		content.append("'" + opt.getSliderColor() + "',");
		content.append(values + ",");
		content.append(labels + ",");
		content.append("'" + state.getComponentValue(filter.getName()) + "',");
		content.append(opt.isAutoRun() ? "1," : "0);");
		content.append("_slider" + filter.getId() + ".onNewPosition = function(){" + event + "}\n");
		if(opt.isAutoRun()) {
			content.append("    _slider" + filter.getId() + ".run();\n");
		}
		content.append("</script>\n");

		if(!refresh) {
			buf.append(content.toString());
		}
		else {
			return content.toString();
		}

		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdFilter(\"" + filter.getName() + "\", 'radio')" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}

	public String getComponentDefinitionDivStart(Rectangle layout, IComponentDefinition def) {
		StringBuffer buf = new StringBuffer();

		String css = " class='";
		if(def.getCssClass() != null) {
			css += def.getCssClass();
		}
		css += " cell' ";

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

		if(def.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.LIST || def.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.DROP_DOWN_LIST_BOX || def.getRenderer().getRendererStyle() == bpm.fd.api.core.model.components.definition.filter.FilterRenderer.TEXT_FIELD) {
			buf.append("<div " + css + layoutS.toString() + ">\n");
		}
		else {
			buf.append("<div id='" + def.getName() + "' name='" + def.getId() + "'" + css + layoutS.toString() + ">\n");
		}

		// buf.append("<script type=\"text/javascript\">\n");
		// buf.append("$(function() {$( \"#" + def.getName() + "\" ).draggable();});\n");
		// buf.append("</script>\n");

		return buf.toString();
	}

}
