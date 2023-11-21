package bpm.vanilla.repository.ui.viewers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

public class RepositoryTreeViewer extends TreeViewer{

	public RepositoryTreeViewer(Composite parent, int style) {
		super(parent, style);
		initProviders();
	}

	
	protected void initProviders(){
		this.setLabelProvider(new RepositoryLabelProvider());
		this.setContentProvider(new RepositoryContentProvider());
//		this.setComparator(new RepositoryViewerComparator());
	}
}
