package bpm.vanilla.server.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.server.ui.views.composite.RepositoryViewer;

public class ContentView extends ViewPart {

//	private FormToolkit toolkit;
//	private RepositoryViewer repositoryViewer;

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		Composite main = toolkit.createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		RepositoryViewer repositoryViewer = new RepositoryViewer(main, SWT.NONE, toolkit);
		getSite().setSelectionProvider(repositoryViewer.getRepositoryViewer());
//		getSite().setSelectionProvider(repositoryViewer.getTopTenViewer());
	}

	@Override
	public void setFocus() {
	}

}
