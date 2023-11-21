package org.fasd.views.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.fasd.cubewizard.oda.OdaDatasourceWizard;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.views.SQLView;
import org.fasd.views.operations.AddConnectionOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddOdaConnection extends Action {

	private SQLView view;
	private UndoContext undoContext;

	public ActionAddOdaConnection(SQLView view, UndoContext undoContext) {
		super(LanguageText.ActionAddConnectionODA);
		this.view = view;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/add_odads.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddConnection_Information, LanguageText.ActionAddConnection_Create_or_Open_Schema);
			return;
		}

		OdaDatasourceWizard wizard = new OdaDatasourceWizard(new DatasourceOda());

		wizard.init(view.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) view.getSite().getWorkbenchWindow().getSelectionService().getSelection());
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();

		if (dialog.open() == Dialog.OK) {
			try {
				view.getOperationHistory().execute(new AddConnectionOperation(LanguageText.ActionAddConnection_Add_DatasSource, undoContext, FreemetricsPlugin.getDefault().getFAModel(), wizard.getDatasource()), null, null);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}
