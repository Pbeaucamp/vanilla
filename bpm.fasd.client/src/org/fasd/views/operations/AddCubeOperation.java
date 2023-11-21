package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPSchema;
import org.freeolap.FreemetricsPlugin;


public class AddCubeOperation extends AbstractOperation{
	private OLAPSchema schema;
	private OLAPCube cube;
	
	public AddCubeOperation(String label, IUndoContext undoContext, OLAPSchema schema, OLAPCube cube) {
		super(label);
		addContext(undoContext);
		this.schema = schema;
		this.cube = cube;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		schema.addCube(cube);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		schema.addCube(cube);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		schema.removeCube(cube);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}
	
	public boolean canUndo(){
		return schema.getCubes().contains(cube);
	}
	
	public boolean canRedo(){
		return !canUndo();
	}

}
