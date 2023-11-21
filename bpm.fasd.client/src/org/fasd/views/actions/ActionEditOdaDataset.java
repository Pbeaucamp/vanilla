package org.fasd.views.actions;

import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.fasd.cubewizard.oda.OdaDatasetWizard;
import org.fasd.datasource.DataObjectOda;
import org.fasd.i18N.LanguageText;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.SQLView;
import org.freeolap.FreemetricsPlugin;

public class ActionEditOdaDataset extends Action {

	private SQLView view;
	private TreeViewer tree;

	public ActionEditOdaDataset(SQLView view, UndoContext undoContext, TreeViewer tree) {
		super(LanguageText.ActionEditDatasetODA);
		this.view = view;
		this.tree = tree;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/edit_odadset.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddConnection_Information, LanguageText.ActionAddConnection_Create_or_Open_Schema);
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) tree.getSelection();
		Object object = ss.getFirstElement();

		DataObjectOda doOda = (DataObjectOda) ((TreeTable) object).getTable();

		OdaDatasetWizard wizard = new OdaDatasetWizard(doOda);

		wizard.init(view.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) view.getSite().getWorkbenchWindow().getSelectionService().getSelection());
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();

		if (dialog.open() == Dialog.OK) {
			view.refresh(true);
		}
	}

}
