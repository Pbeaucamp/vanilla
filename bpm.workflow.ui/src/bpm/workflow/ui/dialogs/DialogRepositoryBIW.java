package bpm.workflow.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.viewer.TreeContentProvider;
import bpm.workflow.ui.viewer.TreeDirectory;
import bpm.workflow.ui.viewer.TreeItem;
import bpm.workflow.ui.viewer.TreeLabelProvider;
import bpm.workflow.ui.viewer.TreeParent;

/**
 * Dialof for browsing the workflows which are in the repository
 * 
 * @author admin
 * 
 */
public class DialogRepositoryBIW extends Dialog {

	private TreeViewer treeViewer;
	private RepositoryItem item;

	public DialogRepositoryBIW(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		buildTree();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		ToolBar toolbar = new ToolBar(container, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		// fillToolbar(toolbar);

		// Composite filter = new Composite(parent, SWT.NONE);
		// filter.setLayout(new GridLayout(2, false));
		// filter.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		treeViewer = new TreeViewer(container);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) treeViewer.getSelection();

				if(ss.isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}

				if(ss.getFirstElement() instanceof TreeItem) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}

			}

		});

		return container;
	}

	private void buildTree() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		IRepository rep;

		try {

			rep = new Repository(Activator.getDefault().getRepositoryConnection(), IRepositoryApi.BIW_TYPE);

			for(RepositoryDirectory dir : rep.getRootDirectories()) {
				TreeDirectory td = new TreeDirectory(dir);
				buildChilds(td, rep);

				for(RepositoryItem it : rep.getItems(dir)) {

					TreeItem ti = new TreeItem(it);
					td.addChild(ti);

				}
				root.addChild(td);
			}

		} catch(Exception e) {
			e.printStackTrace();
			this.close();
			MessageDialog.openError(getShell(), Messages.DialogRepositoryBIW_1, e.getMessage());
		}

		this.treeViewer.setInput(root);

	}

	private void buildChilds(TreeDirectory parent, IRepository rep) throws Exception {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		for(RepositoryDirectory d : rep.getChildDirectories(dir)) {
			TreeDirectory td = new TreeDirectory(d);
			parent.addChild(td);
			for(RepositoryItem di : rep.getItems(d)) {
				TreeItem ti = new TreeItem(di);
				td.addChild(ti);

			}

			buildChilds(td, rep);
		}

	}

	@Override
	protected void okPressed() {
		item = ((TreeItem) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement()).getItem();

		super.okPressed();
	}

	/**
	 * 
	 * @return the Idirectoryitem of the selected workflow
	 */
	public RepositoryItem getRepositoryItem() {
		return item;
	}
}
