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
import org.fasd.cubewizard.DataSourceWizard;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;
import org.fasd.views.SQLView;
import org.fasd.views.operations.AddConnectionOperation;
import org.freeolap.FreemetricsPlugin;

public class ActionAddConnection extends Action {
	private SQLView view;
	private UndoContext undoContext;

	public ActionAddConnection(SQLView view, UndoContext undoContext) {
		super(LanguageText.ActionAddConnection_New_Connection);
		this.view = view;
		this.undoContext = undoContext;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/add.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddConnection_Information, LanguageText.ActionAddConnection_Create_or_Open_Schema);
			return;
		}

		DataSourceWizard wizard = new DataSourceWizard();
		wizard.init(view.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) view.getSite().getWorkbenchWindow().getSelectionService().getSelection());
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();

		if (dialog.open() == Dialog.OK) {
			try {
				view.getOperationHistory().execute(new AddConnectionOperation(LanguageText.ActionAddConnection_Add_DatasSource, undoContext, FreemetricsPlugin.getDefault().getFAModel(), wizard.getDataSource()), null, null);

				for (OLAPRelation r : wizard.getRelations()) {

					// we clean the relations to be sure that the DataObjects
					// are the right references
					// because the wizard is a total mess in the objects
					// references
					DataObjectItem left = null;
					DataObjectItem right = null;
					for (DataObject t : wizard.getDataSource().getDataObjects()) {
						if (t.getName().equals(r.getLeftObject().getName())) {
							for (DataObjectItem i : t.getColumns()) {
								if (i.getName().equals(r.getLeftObjectItem().getName())) {
									left = i;
									break;
								}
							}

						}
						if (t.getName().equals(r.getRightObject().getName())) {
							for (DataObjectItem i : t.getColumns()) {
								if (i.getName().equals(r.getRightObjectItem().getName())) {
									right = i;
									break;
								}
							}

						}

					}
					r.setLeftObjectItem(left);
					r.setRightObjectItem(right);

					FreemetricsPlugin.getDefault().getFAModel().addRelation(r);
				}

				FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();

				view.refresh(true);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

	}

}
