package org.fasd.views.operations;



import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.freeolap.FreemetricsPlugin;



public class AddHieraOperation extends AbstractOperation {
	private OLAPDimension dim;
	private OLAPHierarchy hiera;
	
	public AddHieraOperation(String label, IUndoContext undoContext, OLAPDimension dim, OLAPHierarchy hiera) {
		super(label);
		addContext(undoContext);
		this.dim = dim;
		this.hiera = hiera;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		dim.addHierarchy(hiera);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		dim.addHierarchy(hiera);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		dim.removeHierarchy(hiera);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		return Status.OK_STATUS;
	}

}
