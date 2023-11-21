package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.freeolap.FreemetricsPlugin;


public class AddDimToCubeOperation extends AbstractOperation{
	private OLAPCube cube;
	private OLAPDimension dim;
	
	public AddDimToCubeOperation(String label, IUndoContext undoContext, OLAPCube cube, OLAPDimension dim) {
		super(label);
		addContext(undoContext);
		this.cube = cube;
		this.dim = dim;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		cube.addDim(dim);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		cube.addDim(dim);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		cube.removeDim(dim);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public boolean canRedo() {
		return !cube.getDims().contains(dim);
	}

	@Override
	public boolean canUndo() {
		return !cube.getDims().contains(dim);
	}

}
