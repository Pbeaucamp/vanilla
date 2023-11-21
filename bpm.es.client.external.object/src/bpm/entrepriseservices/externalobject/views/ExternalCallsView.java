package bpm.entrepriseservices.externalobject.views;

import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import adminbirep.Activator;
import bpm.birep.admin.client.content.views.ViewActionRefresh;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.entrepriseservices.externalobject.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ExternalCallsView extends ViewTree {
	public static final String ID = "bpm.entrepriseservices.externalobject.ExternalCallsView"; //$NON-NLS-1$

	public static Object selectedObject;

	public ExternalCallsView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTree(container);
		getSite().setSelectionProvider(viewer);

		createContextMenu();

		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(new ViewActionRefresh(this));

		createInput();
	}

	public void createInput() {

		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		Repository rep = null;

		try {

			rep = new Repository(Activator.getDefault().getRepositoryApi());
			for (RepositoryDirectory d : rep.getRootDirectories()) {
				TreeDirectory tp = new TreeDirectory(d);
				root.addChild(tp);
				buildChilds(rep, tp, null);
				for (RepositoryItem it : rep.getItems(d)) {
					createTreeItem(rep, tp, it);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openWarning(getSite().getShell(), Messages.ExternalCallsView_2 + Activator.getDefault().getRepositoryApi().getContext().getRepository().getUrl(), e.getMessage());

		}

		viewer.setInput(root);
		tableViewer.setInput(root);
	}

	private void createTreeItem(Repository rep, TreeDirectory tp, RepositoryItem it) {
		if (canHaveAPublicUrl(it)) {
			TreeItem ti = new TreeItem(it);
			tp.addChild(ti);
			rep.add(it);
		}
	}

	private boolean canHaveAPublicUrl(RepositoryItem it) {

		if (it.getType() == IRepositoryApi.BIW_TYPE || it.getType() == IRepositoryApi.CUST_TYPE || it.getType() == IRepositoryApi.FAV_TYPE || it.getType() == IRepositoryApi.FD_TYPE || it.getType() == IRepositoryApi.FWR_TYPE || it.getType() == IRepositoryApi.GTW_TYPE || it.getType() == IRepositoryApi.MAP_TYPE || it.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
			return true;
		}

		return false;
	}

	protected void buildChilds(Repository rep, TreeDirectory parent, List<RepositoryItem> callable) throws Exception {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		for (RepositoryDirectory d : rep.getChildDirectories(dir)) {
			TreeDirectory td = new TreeDirectory(d);
			parent.addChild(td);
			rep.add(d);
			for (RepositoryItem it : rep.getItems(d)) {
				createTreeItem(rep, td, it);
			}
			buildChilds(rep, td);

		}

	}
}
