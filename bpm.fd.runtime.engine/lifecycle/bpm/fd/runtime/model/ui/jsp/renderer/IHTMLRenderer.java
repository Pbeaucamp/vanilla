package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.runtime.model.DashState;

public interface IHTMLRenderer<T extends IBaseElement> {

	public String getHTML(Rectangle layout, T definition, DashState state, IResultSet datas, boolean refresh);
	
	public String getJavaScriptFdObjectVariable(T definition);
}
