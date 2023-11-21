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
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.exceptions.HierarchyException;
import org.freeolap.FreemetricsPlugin;



public class DeleteDimsOperation extends AbstractOperation {
	private OLAPElement target;
	private OLAPElement element;
	
	public DeleteDimsOperation(String label, IUndoContext undoContext, OLAPElement target, OLAPElement element) {
		super(label);
		addContext(undoContext);
		this.target = target;
		this.element = element;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		if (target instanceof OLAPSchema){
			if (element instanceof OLAPDimension){
				((OLAPSchema)target).removeDimension((OLAPDimension)element);
				for(OLAPCube cube : ((OLAPSchema)target).getCubes()) {
					cube.removeDim(((OLAPDimension)element));
				}
			}
			else
				return Status.CANCEL_STATUS;
		}
		else if ((target instanceof OLAPDimension) && (element instanceof OLAPHierarchy)){
			((OLAPDimension)target).removeHierarchy((OLAPHierarchy)element);
		}
		else if ((target instanceof OLAPHierarchy) && (element instanceof OLAPLevel)){
			((OLAPHierarchy)target).removeLevel((OLAPLevel)element);
		}
		else{
			return Status.CANCEL_STATUS;
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
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
		if (target instanceof OLAPSchema){
			if (element instanceof OLAPDimension){
				((OLAPSchema)target).addDimension((OLAPDimension)element);
			}
			else
				return Status.CANCEL_STATUS;
		}
		else if ((target instanceof OLAPDimension) && (element instanceof OLAPHierarchy)){
			((OLAPDimension)target).addHierarchy((OLAPHierarchy)element);
		}
		else if ((target instanceof OLAPHierarchy) && (element instanceof OLAPLevel)){
			OLAPLevel lvl = (OLAPLevel)element;
			//OLAPHierarchy h = (OLAPHierarchy)target;
			
			try {
				((OLAPHierarchy)target).addLevel(lvl.getNb(), (OLAPLevel)element);
			} catch (HierarchyException e) {
				
				e.printStackTrace();
			}
		}
		else{
			return Status.CANCEL_STATUS;
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		return Status.OK_STATUS;
	}
	
	@Override
	public boolean canUndo() {
		if (target instanceof OLAPSchema)
			return !((OLAPSchema)target).getDimensions().contains((OLAPDimension)element);
		if (target instanceof OLAPDimension)
			return !((OLAPDimension)target).getHierarchies().contains((OLAPHierarchy)element);
		if (target instanceof OLAPHierarchy)
			return !((OLAPHierarchy)target).getLevels().contains((OLAPLevel)element);
		
		return false;
		
		
	}
	
	@Override
	public boolean canRedo() {
		return !canUndo();
	}
	
}
