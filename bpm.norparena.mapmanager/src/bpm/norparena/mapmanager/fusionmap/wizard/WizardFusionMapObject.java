package bpm.norparena.mapmanager.fusionmap.wizard;

import java.io.FileInputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;

public class WizardFusionMapObject extends Wizard{
	private FusionMapObjectPage page;
	private IFusionMapObject result ;
	@Override
	public void addPages() {
		page = new FusionMapObjectPage("flashMapObjectPage"); //$NON-NLS-1$
		page.setDescription(Messages.WizardFusionMapObject_1);
		page.setTitle(Messages.WizardFusionMapObject_2);
		addPage(page);
		
	}
	@Override
	public boolean performFinish() {
		
		FileInputStream fis = null;
		IFusionMapObject fusionMap = null;
		
		try{
			fusionMap = page.getFusionMap();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.WizardFusionMapObject_3, Messages.WizardFusionMapObject_4 + ex.getMessage());
			return false;
		}
		
		try{
			fis = new FileInputStream(fusionMap.getSwfFileName());
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.WizardFusionMapObject_5, Messages.WizardFusionMapObject_6 + ex.getMessage());
			return false;
		}
		
		
		try{
			result = Activator.getDefault().getFusionMapRegistry().addFusionMapObject(fusionMap, fis);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.WizardFusionMapObject_7, Messages.WizardFusionMapObject_8 + ex.getMessage());
			return false;
		}
		
		return true;
	}

	
	public IFusionMapObject getFusionMap(){
		return result;
	}
}
