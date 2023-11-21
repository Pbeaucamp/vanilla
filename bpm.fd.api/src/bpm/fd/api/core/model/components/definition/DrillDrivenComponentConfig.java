package bpm.fd.api.core.model.components.definition;

import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;


public class DrillDrivenComponentConfig extends ComponentConfig{
	public static final String NOT_BINDED = "Not Binded";

	private IComponentDefinition  controllerComponent;
	private DrillDrivenStackableCell drillDrivenCell;
	public DrillDrivenComponentConfig(DrillDrivenStackableCell drillDrivenCell, IComponentDefinition componentDef) {
		super(drillDrivenCell, componentDef);
		this.drillDrivenCell = drillDrivenCell;
	}
	
	public DrillDrivenStackableCell getDrillDrivenCell(){
		return drillDrivenCell;
	}
	
	/**
	 * should be called only when a componentDef is added on a DrillDrivenStackableCell
	 * 
	 * @param def
	 */
	public void setController(IComponentDefinition def){
		controllerComponent = def;
	}
	
	public IComponentDefinition getController(){
		return controllerComponent;
	}


}
