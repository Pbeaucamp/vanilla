package org.fasd.views.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.exceptions.HierarchyException;
import org.freeolap.FreemetricsPlugin;



public class AddLevelOperation extends AbstractOperation {
	private OLAPHierarchy hiera;
	private OLAPLevel level;

	public AddLevelOperation(String label, IUndoContext undoContext, OLAPHierarchy hiera, OLAPLevel level) {
		super(label);
		addContext(undoContext);
		this.hiera = hiera;
		this.level = level;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			hiera.addLevel(level);
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		} catch (HierarchyException e) {
			Shell shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openInformation(shell, LanguageText.AddLevelOperation_Unable_Add_Lvl, LanguageText.AddLevelOperation_Containing_Only_one_Lvl);
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			hiera.addLevel(level);
			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		} catch (HierarchyException e) {
			Shell shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openInformation(shell, LanguageText.AddLevelOperation_Unable_Add_Lvl, LanguageText.AddLevelOperation_Containing_Only_one_Lvl);
			e.printStackTrace();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		hiera.removeLevel(level);
		FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
		return Status.OK_STATUS;
	}

}
