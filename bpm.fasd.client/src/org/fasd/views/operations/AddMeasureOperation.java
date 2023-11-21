package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.freeolap.FreemetricsPlugin;



public class AddMeasureOperation extends AbstractOperation {
	private OLAPSchema schema;
	private OLAPMeasure measure;
	
	public AddMeasureOperation(String label, IUndoContext undoContext, OLAPSchema schema, OLAPMeasure measure) {
		super(label);
		addContext(undoContext);
		this.schema = schema;
		this.measure = measure;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.addMeasure(measure);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.addMeasure(measure);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.removeMeasure(measure);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		return Status.OK_STATUS;
	}

}
