package bpm.vanilla.repository.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;

public class DialogDirectoryPicker extends Dialog{
	private IRepositoryApi sock;
	private CompositeRepositoryItemSelecter viewer;
	private RepositoryDirectory directory;
	
	public DialogDirectoryPicker(Shell parentShell, IRepositoryApi sock) {
		super(parentShell);
		this.sock = sock;
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new CompositeRepositoryItemSelecter(parent, SWT.BORDER);
		viewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.addViewerFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof RepositoryDirectory;
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(!viewer.getSelectedItems().isEmpty());
				
			}
		});
		
		try {
			viewer.setInput(new Repository(sock));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return viewer;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogDirectoryPicker_0);
		getShell().setSize(600, 400);
	}
	
	@Override
	protected void okPressed() {
		directory = (RepositoryDirectory)viewer.getSelectedItems().get(0);
		super.okPressed();
	}
	
	public RepositoryDirectory getDirectory(){
		return directory;
	}
	
	public int getDirectoryId(){
		return directory.getId();
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogDirectoryPicker_1, true).setEnabled(false);
	}
}
