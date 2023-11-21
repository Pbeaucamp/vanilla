package bpm.es.pack.manager.imp;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.es.pack.manager.provider.PackageContentProvider;
import bpm.es.pack.manager.provider.PackageLabelProvider;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class CompositePackageDetails extends Composite{
	
	private TreeViewer viewer;
	
	public CompositePackageDetails(Composite parent, int style){
		super(parent, style);
		buildContent(parent);
	}
	
	private void buildContent(Composite parent){

		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		viewer = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new PackageContentProvider());
		viewer.setLabelProvider(new PackageLabelProvider());
	}

	public void loadDetails(VanillaPackage vanillaPackage) {
		viewer.setInput(vanillaPackage);
		viewer.refresh();
	}
}
