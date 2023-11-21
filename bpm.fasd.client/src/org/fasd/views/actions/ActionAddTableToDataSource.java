package org.fasd.views.actions;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.utils.trees.TreeDatabase;
import org.fasd.utils.trees.TreeTable;
import org.fasd.views.SQLView;
import org.fasd.views.dialogs.DialogAddTable;
import org.freeolap.FreemetricsPlugin;

public class ActionAddTableToDataSource extends Action {
	private SQLView view;
	private TreeViewer viewer;

	public ActionAddTableToDataSource(SQLView view, TreeViewer viewer) {
		super(LanguageText.ActionAddTableToDataSource_New_Table);
		this.view = view;
		this.viewer = viewer;
		Image img = new Image(view.getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_folder.png"); //$NON-NLS-1$
		this.setImageDescriptor(ImageDescriptor.createFromImage(img));
	}

	public void run() {
		if (!FreemetricsPlugin.getDefault().isDocOpened()) {
			MessageDialog.openInformation(view.getSite().getShell(), LanguageText.ActionAddTableToDataSource_Information, LanguageText.ActionAddTableToDataSource_Create_Shema);
			return;
		}
		ISelection s = viewer.getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) s;
			Object o = ss.getFirstElement();
			ArrayList<String> tableNames = new ArrayList<String>();

			if (o instanceof TreeDatabase) {
				DataSource dataSource = ((TreeDatabase) o).getDriver();

				for (Object obj : ((TreeDatabase) o).getChildren()) {
					if (obj instanceof TreeTable) {
						tableNames.add(((TreeTable) obj).getTable().getName());
					}
				}
				if (dataSource.getDriver().getType().equals("DataBase")) { //$NON-NLS-1$
					try {
						dataSource.getDriver().connectAll();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				DialogAddTable dial = new DialogAddTable(view.getSite().getShell(), dataSource.getDriver());
				if (dial.open() == Dialog.OK) {
					for (DataObject d : dial.getDataObject()) {
						dataSource.addDataObject(d);
					}
					dial.close();
					view.refresh(true);
					FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				}
			}
		}
	}

}
