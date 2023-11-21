package bpm.birep.admin.client.views.datasources;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;

import adminbirep.Activator;
import adminbirep.Messages;

public class DatasourceActionRefresh extends Action {

	private ViewDataSource view = null;

	public DatasourceActionRefresh(IViewPart view) {
		this.view = (ViewDataSource) view;
		this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("refresh")); //$NON-NLS-1$
		this.setText(Messages.ViewActionRefresh_1);
		this.setToolTipText(Messages.ViewActionRefresh_2);
	}

	public void run() {
		try {
			view.createModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
