package org.fasd.views.actions;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.DocumentProperties;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPSchema;
import org.fasd.utils.ResetCounterFactory;
import org.fasd.views.DetailView;
import org.fasd.views.composites.PropertiesDialog;
import org.freeolap.FreemetricsPlugin;

public class ActionNewSchema extends Action {
	private Shell sh;
	private boolean openPropertiesDialog = false;

	public ActionNewSchema(Shell sh, boolean openPropertiesDialog) {
		super(LanguageText.ActionNewSchema_New_Schema);
		this.sh = sh;
		this.openPropertiesDialog = openPropertiesDialog;
	}

	public void run() {
		Shell shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell();

		if (shell.getText().contains("*")) { //$NON-NLS-1$
			if (MessageDialog.openQuestion(shell, LanguageText.ActionNewSchema_New_Schema, LanguageText.ActionNewSchema_Save_Schema)) {
				FileDialog dd = new FileDialog(shell, SWT.SAVE);
				dd.setFilterExtensions(new String[] { "*.fasd" }); //$NON-NLS-1$
				dd.setText(LanguageText.ActionNewSchema_Save_Proj_As___);
				FreemetricsPlugin.getDefault().setMondrianImport(false);
				String path = dd.open();
				if (path != null) {
					FAModel model = FreemetricsPlugin.getDefault().getFAModel();

					if (!path.endsWith(".fasd")) //$NON-NLS-1$
						path += ".fasd"; //$NON-NLS-1$

					File file = new File(path);
					try {
						FileWriter fw = new FileWriter(file);
						fw.write(model.getFAXML());
						fw.close();
						if (FreemetricsPlugin.getDefault().isMondrianImport())
							FreemetricsPlugin.getDefault().setMondrianImport(false);

						shell.setText("Free Analysis Schema Designer - " + path); //$NON-NLS-1$
						FreemetricsPlugin.getDefault().setPath(path);

					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(shell.getShell(), LanguageText.ActionNewSchema_Error, LanguageText.ActionNewSchema_Failed_Write + ex.getMessage());
					}
				}
			}

		}

		FAModel model;
		try {
			// put all counter to 0
			ResetCounterFactory.getInstance().resetAllCounter();

			model = new FAModel(new OLAPSchema(), ""); //$NON-NLS-1$
			model.setSecurity(FreemetricsPlugin.getDefault().getSecurity());
			FreemetricsPlugin.getDefault().setFAModel(model);
			// to disable update on rep
			FreemetricsPlugin.getDefault().getSessionSourceProvider().setDirectoryItemId(null);
			FreemetricsPlugin.getDefault().getSessionSourceProvider().setModelOpened(true);
			FreemetricsPlugin.getDefault().getSessionSourceProvider().setCheckedIn(false);
			FreemetricsPlugin.getDefault().setDocOpened(true);
			IViewPart view = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DetailView.ID);
			// clear the details view
			FreemetricsPlugin.getDefault().setPath(""); //$NON-NLS-1$
			shell.setText("Free Analysis Schema Designer - *"); //$NON-NLS-1$
			IPerspectiveDescriptor perspective = FreemetricsPlugin.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("org.freeolap.perspective");; //$NON-NLS-1$
			IWorkbenchPage page = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.setPerspective(perspective);
			FreemetricsPlugin.getDefault().refreshSQLView();
			DocumentProperties prop = model.getDocumentProperties();

			if (openPropertiesDialog) {
				PropertiesDialog dialog = new PropertiesDialog(shell.getShell(), prop);
				if (dialog.open() == Dialog.OK) {
					if (dialog.getDocProperty() != null) {
						model.setDocumentProperties(dialog.getDocProperty());
					}

				}
			}
			if (view != null)
				((DetailView) view).loadData();
		} catch (Exception e) {
//			MessageDialog.openError(sh, LanguageText.ActionNewSchema_Error, LanguageText.ActionNewSchema_Unable_Load);
			e.printStackTrace();
		}

	}
}
