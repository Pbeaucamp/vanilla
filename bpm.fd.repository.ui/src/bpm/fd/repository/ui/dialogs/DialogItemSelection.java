package bpm.fd.repository.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

import bpm.fd.repository.ui.Activator;
import bpm.fd.repository.ui.Messages;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryTreeViewer;


public class DialogItemSelection extends Dialog{
	
	private TreeViewer viewer;
	private RepositoryItem item;
	private int type = -1;
	
	public DialogItemSelection(Shell parentShell, int itemType) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.type = itemType;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new RepositoryTreeViewer(main, SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
	
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				getButton(IDialogConstants.OK_ID).setEnabled(!ss.isEmpty() && (ss.getFirstElement() instanceof RepositoryItem));
				if (getButton(IDialogConstants.OK_ID).isEnabled()){
					item = (RepositoryItem)ss.getFirstElement();
				}
				else{
					item = null;
				}
			}
			
		});
		
		Repository rep;
		try {
			rep = new Repository(Activator.getDefault().getRepositoryConnection(), type);
			viewer.setInput(rep);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return main;
	}
	
	public RepositoryItem getDirectoryItem(){
		return item;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogItemSelection_0);
		getShell().setSize(600, 400);
	}
	
	
}
