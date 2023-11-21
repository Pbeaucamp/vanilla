package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class ImageRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentPicture>{
	public String getHTML(Rectangle layout, ComponentPicture picture, DashState state, IResultSet datas, boolean refresh){
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, picture));
		
		StringBuilder b = new StringBuilder();
		String runtime = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		//XXX A shitty trick for the pictures to render correctly in the preview
		if(state.getDashInstance().getDashBoard().getJspFile() != null && state.getDashInstance().getDashBoard().getJspFile().getPath().contains("freedashboard.war")) { 
			runtime = "http://localhost:9191";
		}
		
		b.append(runtime + "/generation/");
		b.append(state.getDashInstance().getRelativeUrl().substring(0, state.getDashInstance().getRelativeUrl().indexOf("/") + 1));
		b.append(picture.getPictureUrl());
		
//		buf.append("<img id='" + picture.getName() + "' src='" + b.toString() + "' style='height:100%; width:100%;' />\n");
		buf.append("<img style='width: 100%; height:100%; display:block;' id='" + picture.getName() + "' src='" + b.toString() + "' />\n");
		buf.append(getComponentDefinitionDivEnd());

		return buf.toString();
	}
	public String getJavaScriptFdObjectVariable(ComponentPicture label){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("</script>\n");
		return buf.toString();
	}
}
