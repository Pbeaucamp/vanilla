package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.runtime.model.DashState;

public class DrillDrivenCellRenderer implements IHTMLRenderer<DrillDrivenStackableCell>{
	public String getHTML(Rectangle layout, DrillDrivenStackableCell cell, DashState state, IResultSet datas, boolean refresh){
		String componentToDraw = null;
		String controllerName = state.getComponentValue(cell.getName());
		try {
			IComponentDefinition controler = null;
			try {
				controler = ((IComponentDefinition)state.getDashInstance().getDashBoard().getComponent(controllerName).getElement());
			} catch(Exception e) {
				//If no value just take the first one and do something !
				controler = ((DrillDrivenComponentConfig)cell.getConfigs().iterator().next()).getController();
			}
			for(ComponentConfig conf : cell.getConfigs()){
				
				if (conf instanceof DrillDrivenComponentConfig){
					if (((DrillDrivenComponentConfig)conf).getController() == controler){
						componentToDraw = ((DrillDrivenComponentConfig)conf).getTargetComponent().getName();
						break;
					}
				}
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return componentToDraw;
	}

	@Override
	public String getJavaScriptFdObjectVariable(
			DrillDrivenStackableCell definition) {
		
		return "";
	}
	

}
