package bpm.es.datasource.analyzer.ui.remapper;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class ViewRemapObjects extends ViewPart implements ISelectionListener{
	public static final String ID = "bpm.es.datasource.analyzer.ui.remapper.ViewRemapObjects"; //$NON-NLS-1$
	private CompositeRemapper remapper;
	
	public ViewRemapObjects() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
		
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		remapper = new CompositeRemapper();
		remapper.createContent(parent).setLayoutData(new GridData(GridData.FILL_BOTH));

	}

	@Override
	public void setFocus() {
		

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object o = ((IStructuredSelection)selection).getFirstElement();
		
		if (o instanceof RepositoryItem){
			remapper.setData((RepositoryItem)o);
		}
		else if (o instanceof TreeItem){
			remapper.setData((((TreeItem)o).getItem()));
		}
		
		
	}

}
