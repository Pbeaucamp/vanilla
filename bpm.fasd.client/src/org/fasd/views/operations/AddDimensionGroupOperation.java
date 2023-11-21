package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPSchema;
import org.freeolap.FreemetricsPlugin;


public class AddDimensionGroupOperation extends AbstractOperation {

	private OLAPDimensionGroup target, element;
	
	public AddDimensionGroupOperation(String string, UndoContext undoContext, OLAPDimensionGroup group, OLAPDimensionGroup group2) {
		super(string);
		target = group;
		element = group2;
		addContext(undoContext);
	}

	public AddDimensionGroupOperation(String string, UndoContext undoContext, OLAPDimensionGroup group) {
		super(string);
		target = null;
		element = group;
		addContext(undoContext);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (target == null){
			OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
			sch.addDimensionGroup(element);
			
		}
		else{
			target.addChild(element);
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimGroup().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (target == null){
			OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
			sch.removeDimensionGroup(element);
		}
		else{
			target.removeChild(element);
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimGroup().setChanged();
		return Status.OK_STATUS;
	}

	@Override
	public boolean canRedo() {
		return !canUndo();
	}

	@Override
	public boolean canUndo() {
		if (target == null){
			OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
			return sch.getDimensionGroups().contains(element);
		}
		else{
			return target.inGroup(target);
		}

	}

	
}
