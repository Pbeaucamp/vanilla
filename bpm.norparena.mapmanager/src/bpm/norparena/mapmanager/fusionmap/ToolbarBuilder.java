package bpm.norparena.mapmanager.fusionmap;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.wizard.WizardFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;

public class ToolbarBuilder {

	
	private TableViewer viewer;
	
	ToolbarBuilder(TableViewer viewer){
		this.viewer = viewer;
	}
	
	private IAction addFusionMap = new Action(Messages.ToolbarBuilder_0) {
		@Override
		public void run() {
			WizardFusionMapObject wiz = new WizardFusionMapObject();
			
			WizardDialog dial = new WizardDialog(viewer.getControl().getShell(), wiz);
			dial.setBlockOnOpen(false);
			dial.setPageSize(800, 600);
			dial.setBlockOnOpen(true);
			if (dial.open() == WizardDialog.OK){
				((Collection)viewer.getInput()).add(wiz.getFusionMap());
				viewer.refresh();
			}
		}
		
	};
	
	private IAction refresh = new Action(Messages.ToolbarBuilder_1) {
		@Override
		public void run() {
			
			try{
				viewer.setInput(Activator.getDefault().getFusionMapRegistry().getFusionMapObjects());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(viewer.getControl().getShell(), Messages.ToolbarBuilder_2, Messages.ToolbarBuilder_3 + ex.getMessage());
			}
			
		}
	};
	
	private IAction delete = new Action(Messages.ToolbarBuilder_4){
		public void run(){
			try{
				for(Object o : ((IStructuredSelection)viewer.getSelection()).toList()){
					if (o instanceof IFusionMapObject){
						Activator.getDefault().getFusionMapRegistry().removeFusionMapObject((IFusionMapObject)o);
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(viewer.getTable().getShell(), Messages.ToolbarBuilder_5, ex.getMessage());
			}
			refresh.run();
			
		}
	};
	
	public ToolBarManager createToolbarManager(){
		ToolBarManager tb = new ToolBarManager();
		
		tb.add(addFusionMap);
		tb.add(delete);
		tb.add(refresh);
		return tb;
	}
	

	


	
}
