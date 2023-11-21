package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPSchema;
import org.fasd.security.SecurityDim;
import org.freeolap.FreemetricsPlugin;





public class AddDimensionOperation extends AbstractOperation {
	private OLAPSchema schema = null;
	private OLAPDimension dim = null;

	public AddDimensionOperation(String label, IUndoContext undoContext, OLAPSchema schema, OLAPDimension dim) {
		super(label);
		addContext(undoContext);
		this.schema = schema;
		this.dim = dim;
	}



	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		schema.addDimension(dim);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		
		SecurityDim sd = new SecurityDim();
		sd.setName(dim.getName() + " view"); //$NON-NLS-1$
		sd.setDim(dim);
		schema.addDimView(sd);
		
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
		schema.removeDimension(dim);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		
		for(SecurityDim sd : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimViews()){
			if (sd.getDim() == dim){
				schema.removeDimView(sd);
				break;
			}
		}
		
		
		return Status.OK_STATUS;
	}

}
