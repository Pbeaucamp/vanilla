package org.fasd.actions;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.freeolap.FreemetricsPlugin;

public class ActionSave extends Action {
	private Shell shell;
	private File file;

	public ActionSave(Shell sh) {
		this.shell = sh;
		Image img = new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/Save.png"); //$NON-NLS-1$
		setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public File getFile() {
		return file;
	}

	public void run() {
		String path = FreemetricsPlugin.getDefault().getPath();
		if (path == null || path.equals("") || path.equals("*")) { //$NON-NLS-1$ //$NON-NLS-2$
			ActionSaveAs asa = new ActionSaveAs(shell);
			asa.run();
			file = asa.getFile();
			return;
		}
		if (FreemetricsPlugin.getDefault().isMondrianImport()) {
			new ActionSaveAs(shell).run();
		}

		if (path != null) {
			FAModel model = FreemetricsPlugin.getDefault().getFAModel();
			file = new File(path);
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
				Date today = new Date();
				model.getDocumentProperties().setModification(sdf.format(today));

				FileWriter fw = new FileWriter(file);
				fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"); //$NON-NLS-1$

				fw.write(model.getFAXML());
				fw.close();
				shell.setText("Free Analysis Schema Designer - " + path); //$NON-NLS-1$

			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(shell, LanguageText.ActionSave_Error, LanguageText.ActionSave_FailedToWriteData);
			}
		}
	}
}
