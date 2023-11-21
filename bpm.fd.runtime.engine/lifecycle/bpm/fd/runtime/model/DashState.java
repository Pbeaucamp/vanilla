package bpm.fd.runtime.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.components.definition.slicer.SlicerData;
import bpm.fd.api.core.model.components.definition.slicer.SlicerOptions;

/**
 * use to keep track of a DashInstance components Values
 * @author ludo
 *
 */
public class DashState {

	private HashMap<String, String> componentValues = new HashMap<String, String>();
	private DashInstance instance;
	
	private HashMap<String, SlicerState> slicerStates = new HashMap<String, SlicerState>();
	private HashMap<String, DrillState> drillStates = new HashMap<String, DrillState>();
	private HashMap<String, String> drillColors = new HashMap<String, String>();
	
	public DashState(DashInstance instance){
		this.instance= instance;
		for(ComponentRuntime cr : instance.getDashBoard().getComponents()){
			if (cr instanceof Component && ((Component)cr).getComponentDefinition() instanceof ComponentSlicer){
				int maxLevel = ((SlicerData)((Component)cr).getComponentDefinition().getDatas()).getLevelCategoriesIndex().size();
				slicerStates.put(cr.getName(), new SlicerState(maxLevel, ((SlicerOptions)((Component)cr).getComponentDefinition().getOptions(SlicerOptions.class)).isLevelLinked()));
			}
			if (cr instanceof Component && ((Component)cr).getComponentDefinition() instanceof ComponentChartDefinition){
				int maxLevel = 0;
				Component component = ((Component)cr);
				for (Integer k : ((IChartData)component.getComponentDefinition().getDatas()).getLevelCategoriesIndex()){
					if (k != null && k >= 0 ){
						maxLevel++;
					}
				}
				DrillState drill = new DrillState(maxLevel) ;
				drillStates.put(component.getName(), drill);
			}
			
		}
	}

	public DrillState getDrillState(String componentName) throws Exception{
		
		return  drillStates.get(componentName);
	}
	public SlicerState getSlicerState(String componentName) throws Exception{
		
		return  slicerStates.get(componentName);
	}
	
	public DashInstance getDashInstance(){
		return instance;
	}
	
	public void setComponentValue(String componentName, String parameterValue){
		componentValues.put(componentName, parameterValue);
	}
	public String getComponentValue(String componentName){
		return componentValues.get(componentName);
	}

	public List<String> getStoredComponentNames() {
		return new ArrayList<String>(componentValues.keySet());
	}

	public String getDrillColor(String name) {
		return drillColors.get(name);
	}

	public void setDrillColor(String name, String color) {
		this.drillColors.put(name, color);
	}
}
