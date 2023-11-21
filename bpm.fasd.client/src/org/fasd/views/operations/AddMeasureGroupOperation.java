package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPSchema;
import org.freeolap.FreemetricsPlugin;


public class AddMeasureGroupOperation extends AbstractOperation {

	private OLAPMeasureGroup target, element;
	
	public AddMeasureGroupOperation(String string, UndoContext undoContext, OLAPMeasureGroup group, OLAPMeasureGroup group2) {
		super(string);
		target = group;
		element = group2;
		addContext(undoContext);
	}

	public AddMeasureGroupOperation(String string, UndoContext undoContext, OLAPMeasureGroup group) {
		super(string);
		target = null;
		element = group;
		addContext(undoContext);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		if (target == null){
			OLAPSchema sch = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema();
			sch.addMeasureGroup(element);
			
		}
		else{
			target.addChild(element);
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMesGroup().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
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
			FreemetricsPlugin.getDefault().getFAModel().setChanged();
			sch.removeMeasureGroup(element);
		}
		else{
			target.removeChild(element);
		}
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMesGroup().setChanged();
		FreemetricsPlugin.getDefault().getFAModel().setChanged();
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
			return sch.getMeasureGroups().contains(element);
		}
		else{
			return target.inGroup(target);
		}

	}

	
}
