package bpm.mdm.ui.model.composites;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.internal.jobs.JobManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.AbstractRowException.OperationType;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;

public class DialogBrowseEntityErrors extends DialogBrowseEntity{

	private static final Status noErrorStatus = new Status(IStatus.INFO, Activator.PLUGIN_ID, Messages.DialogBrowseEntityErrors_0);
	
	public DialogBrowseEntityErrors(Shell parentShell, IEntityStorage store,
			Entity entity) {
		super(parentShell, store, entity);
		
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);

		loadRows();
		
	}
	
	protected void updateTitle(){
		getShell().setText(Messages.DialogBrowseEntityErrors_1 + getEntity().getName() + Messages.DialogBrowseEntityErrors_2);
	}
	
	
	protected void loadRows(){
		final Realm realm = Realm.getDefault();
		Job job = new Job(Messages.DialogBrowseEntityErrors_3 + getEntity().getName() + Messages.DialogBrowseEntityErrors_4) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.DialogBrowseEntityErrors_5, IProgressMonitor.UNKNOWN);
				
				List<Row> rows = null;
				try{
					rows = getStore().getInvalidRows();
				}catch(Exception e){
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DialogBrowseEntityErrors_6, e);
				}
				HashMap<Row, Row>  dirtyRowsMapping = new HashMap<Row, Row>();
				
				for(Object o : rows){
					for(Object oo : getDirtyOjects()){
						if (((Row)oo).match(getEntity().getAttributesId(), (Row)o)){
							dirtyRowsMapping.put((Row)o, (Row)oo);
							break;
						}
					}
				}
				for(Row r : dirtyRowsMapping.keySet()){
					Collections.replaceAll(rows, r, dirtyRowsMapping.get(r));
				}
				
				//we add the new row to the end from the store
				//to be able to edit it
				for(Object o : getDirtyOjects()){
					if (getStore().getType((Row)o) == OperationType.CREATE){
						rows.add((Row)o);
					}
				}
				
				if (rows.isEmpty()){
					
					return noErrorStatus;
				}
				
				try{
					setInputViewer(new WritableList(realm,  rows, Row.class));
				}catch(Exception e){
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DialogBrowseEntityErrors_7, e);
				}
				
				
				
				Display.getDefault().asyncExec(new Runnable(){public void run(){fillViewers();}});
				return Status.OK_STATUS;
			}

			
		};
		job.setUser(true);
		job.schedule();

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		service.showInDialog(getShell(), job);
		
		try {
			job.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		if (job.getResult() == noErrorStatus){
			MessageDialog.openInformation(getShell(), getShell().getText(), Messages.DialogBrowseEntityErrors_8);
			close();
		}
		else if (job.getResult().getSeverity() == IStatus.ERROR){
			MessageDialog.openError(getShell(), getShell().getText(), Messages.DialogBrowseEntityErrors_9 + job.getResult().getMessage());
			close();
		}
		
	}

	protected void createRowToolbar(ToolBar bar, boolean forErrors){
		super.createRowToolbar(bar, true);
	}
	
}
