package bpm.fd.design.ui.project.views.actions;

import java.util.HashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.edition.EditionDataSetWizard;
import bpm.fd.design.ui.edition.MultiPageWizardDialog;
import bpm.fd.design.ui.editor.palette.DialogPalette;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;

public class EditDataSetAction extends Action{

	private Viewer viewer;
	private DataSet dataSet;
	
	public EditDataSetAction(String label, String id, Viewer viewer){
		super(label);
		setId(id);
		this.viewer = viewer;
		
	}
	@Override
	public boolean isEnabled() {
		
		return super.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if (((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof DataSet){
			dataSet = (DataSet)((IStructuredSelection)viewer.getSelection()).getFirstElement();
			try {
				OdaDataSetWizard wiz = new EditionDataSetWizard(dataSet);
//				wiz.init(Activator.getDefault().getWorkbench(), (IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
				WizardDialog d = new MultiPageWizardDialog(shell, wiz);
				if (d.open() == WizardDialog.OK){
					for(IComponentDefinition def : Activator.getDefault().getProject().getDictionary().getComponents()){
						if (def.getDatas() != null && def.getDatas().getDataSet() == dataSet){
							def.firePropertyChange(IComponentDefinition.PARAMETER_CHANGED, null, dataSet);
						}
					}
					
					Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_DATASET_CHANGED, null, dataSet);				
					
				}
			} catch (OdaException e) {
				e.printStackTrace();
			}
		}
		else if (((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof DataSource){
			DataSource ds= (DataSource)((IStructuredSelection)viewer.getSelection()).getFirstElement();
			try {
				OdaDataSourceWizard wiz = new OdaDataSourceWizard(ds);
//				wiz.init(Activator.getDefault().getWorkbench(), (IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
				WizardDialog d = new WizardDialog(shell, wiz);
				if (d.open() == WizardDialog.OK){
					
					/*
					 * refresh all DataSets
					 */
					HashMap<DataSet, Exception> errors = new HashMap<DataSet, Exception>();
					
					for(DataSet dataSet : ds.getDictionary().getDataSetsFor(ds)){
						try {
							dataSet.buildDescriptor(ds);
						} catch (Exception e) {
							errors.put(dataSet, e);
						}
						// fire event on Component Using this DataSourcee to refresh teh editor
						// Parameters settings
						for(IComponentDefinition def : ds.getDictionary().getComponents()){
							if (def.getDatas() != null && def.getDatas().getDataSet() == dataSet){
								def.firePropertyChange(IComponentDefinition.PARAMETER_CHANGED, null, dataSet);
							}
						}
					}
					
					if (!errors.isEmpty()){
						MultiStatus mStatus = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR, "", null); //$NON-NLS-1$
						for(DataSet dataSet : errors.keySet()){
							mStatus.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.EditDataSetAction_1 + dataSet.getName(), errors.get(dataSet)));
						}
						
					ErrorDialog ed = new ErrorDialog(shell, Messages.EditDataSetAction_2, Messages.EditDataSetAction_3, mStatus,  IStatus.ERROR);
					ed.open();
					}
					
					viewer.refresh();
					
					
					
					
					Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_DATASOURCE_CHANGED, null, ds);				}
				} catch (OdaException e) {
				e.printStackTrace();
			}
		}else if (! viewer.getSelection().isEmpty() && ((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof Palette){
			Palette palette = null;
			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
			if (! ss.isEmpty()){
				if (ss.getFirstElement() instanceof Palette){
					palette = (Palette)ss.getFirstElement();
					DialogPalette d = new DialogPalette(viewer.getControl().getShell(), palette);
					d.open();
					viewer.refresh();
				}
			}

			
			
		}
		
		
		
	}
	
	
}
