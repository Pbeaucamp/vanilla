package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.runtime.model.DashState;

public class FolderRenderer implements IHTMLRenderer<Folder>{
	public String getHTML(Rectangle layout, Folder folder, DashState state, IResultSet datas, boolean refresh){
		String componentToDraw = state.getComponentValue(folder.getName());
		
		return componentToDraw;
	}

	@Override
	public String getJavaScriptFdObjectVariable(Folder definition) {
		
		return "";
	}
	

}
