package org.fasd.actions;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.freeolap.FreemetricsPlugin;

public class ActionSaveAs extends Action {
	private Shell shell;
	private File file;

	public ActionSaveAs(Shell sh) {
		this.shell = sh;
		this.setText(LanguageText.ActionSaveAs_SaveAs);
		Image img = new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/Save_As.png"); //$NON-NLS-1$
		setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		FileDialog dd = new FileDialog(shell, SWT.SAVE);
		dd.setFilterExtensions(new String[] { "*.fasd" }); //$NON-NLS-1$
		dd.setText(LanguageText.ActionSaveAs_SaveTheProjectAs___);

		String path = dd.open();
		if (path != null) {
			FAModel model = FreemetricsPlugin.getDefault().getFAModel();

			if (!path.endsWith(".fasd")) //$NON-NLS-1$
				path += ".fasd"; //$NON-NLS-1$

			file = new File(path);
			try {
				FileWriter fw = new FileWriter(file);
				fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"); //$NON-NLS-1$
				fw.write(model.getFAXML());
				fw.close();
				if (FreemetricsPlugin.getDefault().isMondrianImport())
					FreemetricsPlugin.getDefault().setMondrianImport(false);

				shell.setText("Free Analysis Schema Designer - " + path); //$NON-NLS-1$
				FreemetricsPlugin.getDefault().setPath(path);
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(shell, LanguageText.ActionSaveAs_Error, LanguageText.ActionSaveAs_FailedToWriteData + ex.getMessage());
			}
		}
	}

	public File getFile() {
		return file;
	}
}
