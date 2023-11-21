package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.datasource.DataSource;
import org.fasd.olap.FAModel;
import org.freeolap.FreemetricsPlugin;




public class AddConnectionOperation extends AbstractOperation {
	private FAModel model;
	private DataSource ds;
	
	public AddConnectionOperation(String label, IUndoContext undoContext, FAModel model, DataSource ds) {
		super(label);
		addContext(undoContext);
		this.model = model;
		this.ds = ds;
	
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		model.addDataSource(ds);
		FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
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
		model.removeSQLDriver(ds);
		FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}
	
	public boolean canUndo(){
		return model.getDataSources().contains(ds);
	}
	
	public boolean canRedo(){
		return !canUndo();
	}

}
