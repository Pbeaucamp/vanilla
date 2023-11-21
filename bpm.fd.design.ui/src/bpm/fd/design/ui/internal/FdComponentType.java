package bpm.fd.design.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.internal.ILabelable;
import bpm.fd.design.ui.wizard.IWizardComponent;
/**
 * internal class to find all class implementing IComponentDefintion
 * those classes must are foudn by analyzing the NewWizardRegistry
 * it find a ComponentDef class if there is a Wizard implementing IWizardComponent
 * belonging to the WizardCategory bpm.fd.design.ui.freedashComponentCategory
 * returning the ComponentDef class
 * 
 * It is used to add componentsdefinition in an easier way, just need to create the IComponentDefinition class,
 * its wizardn to register it and everythoing shoul be working without need to change a piece of code
 * (exception the PictureHelper to provide the BIG picture in the design Mode
 * 
 * @author ludo
 *
 */
public class FdComponentType implements ILabelable{

	private Class<? extends IComponentDefinition> componentClass;
	private String name;
	private ImageDescriptor image;
		

	
	private static List<FdComponentType> types;
	
	
	public static List<FdComponentType> getComponentsTypes(){
		if (types == null){
			types = new ArrayList<FdComponentType>();
			
			for(IWizardCategory c : NewWizardRegistry.getInstance().getRootCategory().getCategories()){
				if (c.getId().equals("bpm.fd.design.ui.freedashComponentCategory")){ //$NON-NLS-1$
					for(IWizardDescriptor d : c.getWizards()){
						
						
						try{
							FdComponentType type = new FdComponentType();
							type.image= d.getImageDescriptor();
							type.name = d.getLabel();
							type.componentClass = ((IWizardComponent)d.createWizard()).getComponentClass();
							types.add(type);
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
					}			
				}
				
			}
		}
		return types;
	}


	/**
	 * @return the componentClass
	 */
	public Class<? extends IComponentDefinition> getComponentClass() {
		return componentClass;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the image
	 */
	public ImageDescriptor getImage() {
		return image;
	}

	public String getLabel(){
		return getName();
	}


	public static FdComponentType getComponentsType(
			Class<ComponentChartDefinition> class1) {
		for(FdComponentType t : getComponentsTypes()){
			if (t.getComponentClass() == class1){
				return  t;
			}
		}
		return null;
	}
}
