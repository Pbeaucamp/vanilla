package bpm.oda.driver.reader.wizards.pages;

import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.icons.IconsName;
import bpm.oda.driver.reader.model.datasource.DataSource;


public class DataSourceTypePage extends WizardPage{
	private ListViewer viewer;
	private DataSource dataSource;
	public DataSourceTypePage(String pageName) {
		super(pageName);
	}
	
	public DataSourceTypePage(String pageName, DataSource dataSource) {
		super(pageName);
		this.dataSource = dataSource;
	}

	public void createControl(Composite parent) {
		PGroup main = new PGroup(parent, SWT.SMOOTH);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setText("Types");
		main.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DATASOURCE));
		
	
		viewer = new ListViewer(main, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((ExtensionManifest)element).getDataSourceDisplayName();
			}
			
		});
		
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
		});
		
		
		viewer.setInput(ManifestExplorer.getInstance().getExtensionManifests());
		
		if (dataSource != null){
			for(ExtensionManifest ext : (ExtensionManifest[])viewer.getInput()){
				if (ext.getDataSourceElementID().equals(dataSource.getOdaExtensionDataSourceId())){
					
					viewer.setSelection(new StructuredSelection(ext));
					break;
				}
			}
		}
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
			
		});
		setControl(main);
	}
	
	

	@Override
	public boolean isPageComplete() {
		
		return !viewer.getSelection().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
	
		return isPageComplete();
	}

	public ExtensionManifest getExtensionManifest(){
		return (ExtensionManifest)((IStructuredSelection)viewer.getSelection()).getFirstElement();
	}

}
