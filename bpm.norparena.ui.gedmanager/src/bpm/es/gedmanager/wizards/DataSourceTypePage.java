package bpm.es.gedmanager.wizards;

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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.es.gedmanager.Messages;
import bpm.vanilla.platform.core.beans.data.OdaInput;


public class DataSourceTypePage extends WizardPage{
	private Text name;
	private Text description;
	private ListViewer viewer;
	private OdaInput odaInput;
	
	public DataSourceTypePage(String pageName,  OdaInput odaInput) {
		super(pageName);
		this.odaInput = odaInput;
	
	}
	
	

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite container = new Composite(main, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DataSourceTypePage_0);
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		l2.setText(Messages.DataSourceTypePage_1);
		
		description = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 3));
	
		Label _l = new Label(container, SWT.NONE);
		_l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		_l.setText(""); //$NON-NLS-1$
		
		Label _ll = new Label(container, SWT.NONE);
		_ll.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		_ll.setText(""); //$NON-NLS-1$
		
		viewer = new ListViewer(main, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setLabelProvider(new LabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
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
		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				
				try{
					getContainer().updateButtons();
				}catch(Exception ex){
				
				}
				
			}
			
		});
		
		if (odaInput.getOdaExtensionDataSourceId() != null){
			for(ExtensionManifest m : (ExtensionManifest[])viewer.getInput()){
				if (m.getExtensionID().equals(odaInput.getOdaExtensionDataSourceId() )){
					viewer.setSelection(new StructuredSelection(m));
					break;
				}
			}
		}
		setControl(main);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		
		return !viewer.getSelection().isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
	
		return isPageComplete();
	}

	public ExtensionManifest getExtensionManifest(){
		return (ExtensionManifest)((IStructuredSelection)viewer.getSelection()).getFirstElement();
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		if (super.getNextPage() == null || super.getNextPage() == this){
			((OdaDataSourceWizard)getWizard()).updatePages();
		}
		return super.getNextPage();
	}
	
	public String getSelectedName() {
		return name.getText();
	}
	
	public String getDescription() {
		return description.getText();
	}

}
