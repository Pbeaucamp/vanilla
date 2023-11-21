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
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.freeolap.FreemetricsPlugin;



public class DeleteMeasureOperation extends AbstractOperation {
	private OLAPSchema schema;
	private OLAPMeasure mes;
	
	public DeleteMeasureOperation(String label, IUndoContext undoContext, OLAPSchema schema, OLAPMeasure mes) {
		super(label);
		addContext(undoContext);
		this.schema = schema;
		this.mes = mes;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.removeMeasure(mes);
		for(OLAPCube cube : schema.getCubes()) {
			cube.removeMes(mes);
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.removeMeasure(mes);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.addMeasure(mes);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	public boolean canUndo(){
		return !schema.getMeasures().contains(mes);
	}
	
	public boolean canRedo(){
		return !canUndo();
	}
}
