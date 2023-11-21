package bpm.sqldesigner.ui.action;

import java.sql.SQLException;
import java.util.Iterator;

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.TreeView;
import bpm.sqldesigner.ui.wizard.CompareWizard;

public class CompareAction extends WorkbenchPartAction {

	private Node firstElement;
	private Node secondElement;

	public CompareAction(IWorkbenchPart part) {
		super(part);

		setImageDescriptor(ImageDescriptor.createFromImage(Activator
				.getDefault().getImageRegistry().get("compare"))); //$NON-NLS-1$
		setToolTipText(Messages.CompareAction_1);
	}

	@Override
	protected boolean calculateEnabled() {
		TreeSelection selectionTree = (TreeSelection) ((TreeView) getWorkbenchPart())
				.getSelected();
		if (selectionTree.size() == 2) {
			firstElement = (Node) selectionTree.getFirstElement();
			Iterator<?> it = selectionTree.iterator();
			it.next();
			secondElement = (Node) it.next();
			if (firstElement.getClass().equals(secondElement.getClass()))
				return true;
		}

		return false;
	}

	@Override
	public void run() {
		Shell shell = new Shell(Activator.getDefault().getWorkbench()
				.getDisplay());
		try {
			if (firstElement.isNotFullLoaded())
				ExtractData.extractWhenNotLoaded(firstElement);

			if (secondElement.isNotFullLoaded())
				ExtractData.extractWhenNotLoaded(secondElement);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		CompareWizard wizard = new CompareWizard(firstElement, secondElement);
		WizardDialog dialog = new WizardDialog(shell, wizard);

		dialog.create();
		dialog.getShell().setLayout(new GridLayout());
		dialog.getShell().setSize(600, 500);

		if (dialog.open() == Window.OK) {
		}
	}
}
