package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.styledtext.StyledTextOptions;
import bpm.fd.runtime.model.DashState;

public class FormatedTextRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentStyledTextInput>{
	
	public String getJavaScriptFdObjectVariable(ComponentStyledTextInput text){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + text.getName() + "\"]= new FdFormatedText(\"" + text.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getHTML(Rectangle layout, ComponentStyledTextInput text, DashState state, IResultSet datas, boolean refresh){
		StyledTextOptions opt = (StyledTextOptions)text.getOptions(StyledTextOptions.class);
		
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, text));
		buf.append("<textarea id='" + text.getName() + "_ftext' name='" + text.getName() + "_ftext' cols='" + opt.getColumnsNumber());
		buf.append("' rows='" + opt.getRowsNumber() + "' ");
		buf.append(">");
		
		if (state.getComponentValue(text.getName()) == null){
			buf.append(opt.getInitialValue() + "</textarea>\n");
		}
		else{
			buf.append(state.getComponentValue(text.getName()) + "</textarea>\n");
		}
		
		
		//buf.append("</div>\n");
		buf.append(getComponentDefinitionDivEnd());

		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + text.getName() + "\"]= new FdFormatedText(\"" + text.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
