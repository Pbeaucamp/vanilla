package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelComponent;
import bpm.fd.api.core.model.components.definition.styledtext.DynamicLabelData;
import bpm.fd.runtime.engine.datas.helper.Sorter;
import bpm.fd.runtime.model.DashState;

public class DynamicLabelRenderer extends AsbtractCssApplier implements IHTMLRenderer<DynamicLabelComponent> {

	public String getHTML(Rectangle _layout, DynamicLabelComponent component, DashState state, IResultSet datas, boolean refresh) {
		try {
			DynamicLabelData labelData = (DynamicLabelData) component.getDatas();

			StringBuffer buf = new StringBuffer();
			buf.append(getComponentDefinitionDivStart(_layout, component));

			// generate table
			StringBuilder layoutS = new StringBuilder();
			if (_layout != null) {
				layoutS.append(" style='height:");
				layoutS.append(_layout.height);
				layoutS.append("px;");
				layoutS.append("width:");
				layoutS.append(_layout.width);
				layoutS.append("px;'");
			}

			// generate table
			StringBuffer content = new StringBuffer();
			content.append("<div class=\"dynamicLabelDiv\" " + layoutS.toString() + ">");

			if (component.getLabel() != null && !component.getLabel().isEmpty()) {
				content.append("<p class=\"dynamicLabelTitle\">" + component.getLabel() + "</p>");
			}

			List<List<Object>> values = Sorter.sort(datas, -1, null);
			for (List<Object> row : values) {
				String value = row.get(labelData.getColumnValueIndex() - 1).toString();

				if (value != null) {
					content.append("<div class=\"dynamicLabelValue\">");
					content.append(String.valueOf(value));
					content.append("</div>");
					
					break;
				}
			}
			content.append("</div>");

			content.append("</div>");
			buf.append(content.toString());
			buf.append(this.getComponentDefinitionDivEnd());

			// Javascript
			buf.append("<script type=\"text/javascript\">\n");
			buf.append("    fdObjects[\"" + component.getName() + "\"]= new FdDynamicLabel(\"" + component.getName() + "\")" + ";\n");
			buf.append("</script>\n");

			if (!refresh) {
				return buf.toString();
			}
			else {
				return content.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "<div id=\"" + component.getName() + "\" name=\"" + component.getId() + "\">Failed to render :" + ex.getMessage() + "</div>\n";
		}
	}

	@Override
	public String getJavaScriptFdObjectVariable(DynamicLabelComponent component) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + component.getName() + "\"]= new FdDynamicLabel(\"" + component.getName() + "\")" + ";\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
