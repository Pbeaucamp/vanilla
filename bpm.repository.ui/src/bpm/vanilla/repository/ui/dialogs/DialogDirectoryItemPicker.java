package bpm.vanilla.repository.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.repository.ui.viewers.RepositoryViewerFilter;

public class DialogDirectoryItemPicker extends Dialog{
	private IRepositoryApi sock;
	private CompositeRepositoryItemSelecter viewer;
	private RepositoryItem directoryItem;
	private int type[];
	private int[] subtype;
	
	public DialogDirectoryItemPicker(Shell parentShell, IRepositoryApi sock, int[] type, int[] subtype) {
		super(parentShell);
		this.sock = sock;
		this.type = type;
		this.subtype = subtype;
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new CompositeRepositoryItemSelecter(parent, SWT.BORDER);
		viewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.addViewerFilter(new RepositoryViewerFilter(type, subtype));
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(!viewer.getSelectedItems().isEmpty() && viewer.getSelectedItems().get(0) instanceof RepositoryItem);
				
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
		getShell().setText(Messages.DialogDirectoryItemPicker_0);
		getShell().setSize(600, 400);
	}
	
	@Override
	protected void okPressed() {
		directoryItem = (RepositoryItem)viewer.getSelectedItems().get(0);
		super.okPressed();
	}
	
	public RepositoryItem getDirectoryItem(){
		return directoryItem;
	}
	
		
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogDirectoryItemPicker_1, true).setEnabled(false);
	}
}
