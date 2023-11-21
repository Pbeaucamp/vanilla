package bpm.sqldesigner.ui.action;

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;

import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.view.TreeView;

public class SaveMarkupPointAction extends WorkbenchPartAction {

	private Node selection;

	public SaveMarkupPointAction(IWorkbenchPart part) {
		super(part);

		setImageDescriptor(ImageDescriptor.createFromImage(Activator
				.getDefault().getImageRegistry().get("photo"))); //$NON-NLS-1$
		setToolTipText(Messages.SaveMarkupPointAction_1);
	}

	@Override
	protected boolean calculateEnabled() {
		TreeSelection selection = (TreeSelection) ((TreeView) getWorkbenchPart())
				.getSelected();
		this.selection = (Node) selection.getFirstElement();
		if (this.selection instanceof DatabaseCluster)
			return ((DatabaseCluster) this.selection).getDatabaseConnection() != null;
		return false;
	}

	@Override
	public void run() {
		FileDialog fd = new FileDialog(getWorkbenchPart().getSite().getShell(),
				SWT.SAVE);
		fd.setText(Messages.SaveMarkupPointAction_2);
		fd.setFilterPath("C:/"); //$NON-NLS-1$
		fd.setFilterExtensions(new String[] { "*.mkp", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
		final String selected = fd.open();
		if (SaveData.checkFile(selected)) {
			boolean erase = MessageDialog.openQuestion(getWorkbenchPart()
					.getSite().getShell(), Messages.SaveMarkupPointAction_6, Messages.SaveMarkupPointAction_7
					+ selected + Messages.SaveMarkupPointAction_8);
			if (!erase)
				return;
		}
		final DatabaseCluster databaseCluster = (DatabaseCluster) selection;

		// Thread thread = new Thread(){
		// public void run(){
		try {
			if (databaseCluster.isNotFullLoaded())
				ExtractData.extractWhenNotLoaded(databaseCluster);
			SaveData.saveDatabaseCluster(databaseCluster, selected,
					SaveData.SAVE_PROCEDURES | SaveData.SAVE_VIEWS
							| SaveData.SAVE_LAYOUT);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getWorkbenchPart().getSite().getShell(),
					Messages.SaveMarkupPointAction_9, e.getMessage());
			
		}
		// }
		// };
		// thread.run();
	}
}
