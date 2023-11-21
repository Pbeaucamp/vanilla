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
import org.fasd.olap.OLAPElement;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.olap.virtual.VirtualCube;
import org.fasd.olap.virtual.VirtualDimension;
import org.fasd.olap.virtual.VirtualMeasure;
import org.freeolap.FreemetricsPlugin;


public class DeleteCubeOperation extends AbstractOperation {
	private OLAPElement target;
	private OLAPElement element;
	
	public DeleteCubeOperation(String label, IUndoContext undoContext, OLAPElement target, OLAPElement el) {
		super(label);
		addContext(undoContext);
		this.target = target;
		this.element = el;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		if (target instanceof OLAPSchema){
			if(element instanceof OLAPCube){
				((OLAPSchema)target).removeCube((OLAPCube)element);
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
				return Status.OK_STATUS;
			}
			else if (element instanceof VirtualCube){
				((OLAPSchema)target).removeVirtualCube((VirtualCube)element);
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
				return Status.OK_STATUS;
			}
			else
				return Status.CANCEL_STATUS;
		}
		else if (target instanceof OLAPCube){
			if(element instanceof OLAPDimension)
				((OLAPCube)target).removeDim((OLAPDimension)element);
			else if (element instanceof OLAPMeasure)
				((OLAPCube)target).removeMes((OLAPMeasure)element);
			else if (element instanceof AggregateTable){
				((OLAPCube)target).removeAggTable((AggregateTable)element);
			}
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
			FreemetricsPlugin.getDefault().getFAModel().setChanged();
			return Status.OK_STATUS;
		}
		else if (target instanceof VirtualCube){
			if (element instanceof VirtualMeasure)
				((VirtualCube)target).removeVirtualMeasure((VirtualMeasure)element);
			else if (element instanceof VirtualDimension)
				((VirtualCube)target).removeVirtualDimension((VirtualDimension)element);
			
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
			FreemetricsPlugin.getDefault().getFAModel().setChanged();
			return Status.OK_STATUS;
		}
		
		else
			return Status.CANCEL_STATUS;
		
		
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		if (target instanceof OLAPSchema){
			if(element instanceof OLAPCube){
				((OLAPSchema)target).addCube((OLAPCube)element);
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
				return Status.OK_STATUS;
			}
			else if (element instanceof VirtualCube){
				((OLAPSchema)target).addVirtualCube((VirtualCube)element);
				FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
				return Status.OK_STATUS;
			}
			else
				return Status.CANCEL_STATUS;
		}
		else if (target instanceof OLAPCube){
			if(element instanceof OLAPDimension)
				((OLAPCube)target).addDim((OLAPDimension)element);
			else if (element instanceof OLAPMeasure)
				((OLAPCube)target).addMes((OLAPMeasure)element);
			else if (element instanceof AggregateTable)
				((OLAPCube)target).addAggTable((AggregateTable)element);
			
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
			FreemetricsPlugin.getDefault().getFAModel().setChanged();
			return Status.OK_STATUS;
		}
		
		else
			return Status.CANCEL_STATUS;
	}

	@Override
	public boolean canUndo() {
		if (target instanceof OLAPSchema){
			if(element instanceof OLAPCube){
				return !((OLAPSchema)target).getCubes().contains((OLAPCube)element);
			}
			if(element instanceof VirtualCube){
				return !((OLAPSchema)target).getVirtualCubes().contains((VirtualCube)element);
			}
		}
		else if (target instanceof OLAPCube){
			if(element instanceof OLAPDimension)
				return !((OLAPCube)target).getDims().contains((OLAPDimension)element);
			else if (element instanceof OLAPMeasure)
				return !((OLAPCube)target).getMes().contains((OLAPMeasure)element);
			else if (element instanceof AggregateTable)
				return !((OLAPCube)target).getAggTables().contains((AggregateTable)element);

		}
		
		return false;
	}
	
	@Override
	public boolean canRedo(){
		return !canUndo();
	}

}
