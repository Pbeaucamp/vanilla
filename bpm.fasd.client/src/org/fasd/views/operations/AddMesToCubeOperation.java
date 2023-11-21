package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPMeasure;
import org.freeolap.FreemetricsPlugin;


public class AddMesToCubeOperation extends AbstractOperation {

	private OLAPCube cube;
	private OLAPMeasure mes;
	
	public AddMesToCubeOperation(String label, IUndoContext undoContext, OLAPCube cube, OLAPMeasure mes) {
		super(label);
		addContext(undoContext);
		this.cube = cube;
		this.mes = mes;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		cube.addMes(mes);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
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
		cube.removeMes(mes);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}


	@Override
	public boolean canRedo() {
		return !cube.getMes().contains(mes);
	}

	@Override
	public boolean canUndo() {
		return cube.getMes().contains(mes);
	}
}
