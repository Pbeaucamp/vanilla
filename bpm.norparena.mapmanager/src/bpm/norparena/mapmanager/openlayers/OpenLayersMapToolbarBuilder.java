package bpm.norparena.mapmanager.openlayers;

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
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.design.ui.dialogs.DialogAddOpenLayersMap;
import bpm.vanilla.map.model.openlayers.impl.OpenLayersMapObject;

public class OpenLayersMapToolbarBuilder {

	private TableViewer viewer;
	
	public OpenLayersMapToolbarBuilder(TableViewer viewer) {
		this.viewer = viewer;
	}

	private IAction addOpenLayersMap = new Action(Messages.OpenLayersMapToolbarBuilder_0) {
		public void run() {
			DialogAddOpenLayersMap dial = new DialogAddOpenLayersMap(viewer.getControl().getShell(), new OpenLayersMapObject());
			if (dial.open() == WizardDialog.OK){
				try {
					viewer.setInput(Activator.getDefault().getDefinitionService().getOpenLayersMapObjects());
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(viewer.getControl().getShell(), Messages.OpenLayersMapToolbarBuilder_1, Messages.OpenLayersMapToolbarBuilder_2 + e.getMessage());
				}
				viewer.refresh();
			}
		};
	};
	
	private IAction refresh = new Action(Messages.OpenLayersMapToolbarBuilder_3) {
		public void run() {
			try{
				viewer.setInput(Activator.getDefault().getDefinitionService().getOpenLayersMapObjects());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(viewer.getControl().getShell(), Messages.OpenLayersMapToolbarBuilder_4, Messages.OpenLayersMapToolbarBuilder_5 + ex.getMessage());
			}
		};
	};
	
	private IAction delete = new Action(Messages.OpenLayersMapToolbarBuilder_6) {
		public void run() {
			try{
				for(Object o : ((IStructuredSelection)viewer.getSelection()).toList()){
					if (o instanceof IOpenLayersMapObject){
						Activator.getDefault().getDefinitionService().deleteOpenLayersMap((IOpenLayersMapObject)o);
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(viewer.getTable().getShell(), Messages.OpenLayersMapToolbarBuilder_7, ex.getMessage());
			}
			refresh.run();
		};
	};
	
	private IAction edit = new Action(Messages.OpenLayersMapToolbarBuilder_8) {
		public void run() {
			
			IOpenLayersMapObject obj = (IOpenLayersMapObject) ((IStructuredSelection)viewer.getSelection()).getFirstElement();
			
			DialogAddOpenLayersMap dial = new DialogAddOpenLayersMap(viewer.getControl().getShell(), obj);
			if (dial.open() == WizardDialog.OK){
				try {
					viewer.setInput(Activator.getDefault().getDefinitionService().getOpenLayersMapObjects());
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(viewer.getControl().getShell(), Messages.OpenLayersMapToolbarBuilder_9, Messages.OpenLayersMapToolbarBuilder_10 + e.getMessage());
				}
				viewer.refresh();
			}
		};
	};
	
	
	public ToolBarManager createToolbarManager() {
		ToolBarManager manager = new ToolBarManager();
		manager.add(addOpenLayersMap);
		manager.add(edit);
		manager.add(delete);
		manager.add(refresh);
		return manager;
	}
	
}
