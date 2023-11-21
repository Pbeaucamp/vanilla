package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPRelation;
import org.freeolap.FreemetricsPlugin;


public class DeleteConnectionOperation extends AbstractOperation {
	private FAModel model;
	private DataSource ds;
	private OLAPRelation r;
	private DataObjectItem col;
	
	public DeleteConnectionOperation(String label, IUndoContext undoContext, FAModel model, DataSource ds) {
		super(label);
		addContext(undoContext);
		this.model = model;
		this.ds = ds;	
	}
	
	public DeleteConnectionOperation(String label, IUndoContext undoContext, FAModel model, OLAPRelation r) {
		super(label);
		addContext(undoContext);
		this.model = model;
		this.r = r;	
	}
	
	public DeleteConnectionOperation(String label, IUndoContext undoContext, FAModel model, DataObjectItem col) {
		super(label);
		addContext(undoContext);
		this.model = model;
		this.col = col;	
	}
	

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (ds != null)
			model.removeSQLDriver(ds);
		if( r!= null)
			model.removeRelation(r);
		if (col != null){
			DataObject table = col.getParent();
			table.removeItem(col);
		}
			
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (ds != null)
			model.addDataSource(ds);
		if(r != null)
			model.addRelation(r);
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
		return Status.OK_STATUS;
	}
	
	public boolean canUndo(){
		if (ds != null)
			return !model.getDataSources().contains(ds);
		else
			return !model.getRelations().contains(r);
	}
	
	public boolean canRedo(){
		return !canUndo();
	}
}
