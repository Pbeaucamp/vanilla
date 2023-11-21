package bpm.es.clustering.ui.view;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.es.clustering.ui.icons.Icons;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;

public class ModuleViewerLabelProvider extends LabelProvider{

	@Override
	public String getText(Object element) {
		if (element instanceof Repository){
			return ((Repository)element).getName();
		}
		else if (element instanceof VanillaPlatformModule){
			return ((VanillaPlatformModule)element).getName();
		}
		else  if (element instanceof Server){
			return ((Server)element).getName();
		}
		else if (element instanceof IVanillaServerManager){
			if((IVanillaServerManager)element instanceof ReportingComponent) {
				return ServerType.REPORTING.getTypeName();
			}
			else if((IVanillaServerManager)element instanceof GatewayComponent) {
				return ServerType.GATEWAY.getTypeName();
			}
			else {
				return ""; //$NON-NLS-1$
			}
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		ImageRegistry reg = bpm.es.clustering.ui.Activator.getDefault().getImageRegistry();
		if (element instanceof Server){
			Server s = (Server)element;
			if (VanillaComponentType.COMPONENT_FREEANALYSISWEB.equals(s.getComponentNature())){
				return reg.get(Icons.FAWEB);
			}
			else if (VanillaComponentType.COMPONENT_FREEWEBREPORT.equals(s.getComponentNature())){
				return reg.get(Icons.FWR);
			}
			else if (VanillaComponentType.COMPONENT_WORKFLOW.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.WORKFLOW);
				}
				else {
					return reg.get(Icons.WORKFLOW_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_SEARCH.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.SEARCH);
				}
				else {
					return reg.get(Icons.SEARCH_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus()))
					return reg.get(Icons.BIG);
				else 
					return reg.get(Icons.BIG_STOP);
			}
			else if (VanillaComponentType.COMPONENT_FREEDASHBOARD.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.FD);
				}
				else {
					return reg.get(Icons.FD_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_VANILLA_FORMS.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.FORM);
				}
				else {
					return reg.get(Icons.FORM_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_GED.equals(s.getComponentNature())){
				if (Status.STARTED.equals(s.getComponentStatus()))
					return reg.get(Icons.GED);
				else 
					return reg.get(Icons.GED_STOP);
			}
			else if (VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.REPORT);
				}
				else {
					return reg.get(Icons.REPORT_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_FREEMETRICS.equals(s.getComponentNature())){
				return reg.get(Icons.FM);
			}
			else if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(s.getComponentNature())){
				if (Status.STARTED.getStatus().equals(s.getComponentStatus()))
					return reg.get(Icons.FAWEB);
				else 
					return reg.get(Icons.FAWEB_STOP);
			}
			else {
				if(s.getName().equals("Orbeon")) { //$NON-NLS-1$
					return reg.get(Icons.ORBEON);
				}
				else if(s.getName().equals("BirtViewer")) { //$NON-NLS-1$
					return reg.get(Icons.BIRT);
				}
			}
			
		}
		else if (element instanceof Repository){
			return reg.get(Icons.REPOSITORY);
		}
		else if (element instanceof VanillaPlatformModule){
			
		}
		else if (element instanceof IVanillaServerManager){
			if((IVanillaServerManager)element instanceof ReportingComponent) {
				return reg.get(Icons.REPORT);
			}
			else if((IVanillaServerManager)element instanceof GatewayComponent) {
				return reg.get(Icons.BIG);
			}
		}
		
		
		return reg.get(Icons.DEFAULT_TREE);
	}
}
