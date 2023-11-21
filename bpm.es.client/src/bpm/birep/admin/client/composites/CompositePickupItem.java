package bpm.birep.admin.client.composites;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.birep.admin.client.content.providers.RepositoryContentProvider;
import bpm.birep.admin.client.content.providers.RepositoryLabelProvider;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositePickupItem extends Composite {
	
	private TreeViewer viewer;
	
	public CompositePickupItem(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		viewer = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new RepositoryContentProvider());
		viewer.setLabelProvider(new RepositoryLabelProvider());
	}
	
	public void setInput(Repository repository) {
		viewer.setInput(repository);
	}
	
	public RepositoryItem getSelectedItem() {
		final IStructuredSelection ss =  (IStructuredSelection) viewer.getSelection();
		if (ss.isEmpty() || !(ss.getFirstElement() instanceof RepositoryItem)){
			return null;
		}
		
		return (RepositoryItem) ss.getFirstElement();
	}

	public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
		viewer.addSelectionChangedListener(selectionChangedListener);
	}
}
