package bpm.birep.admin.client.views.datalists.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class MultiPageWizardDialog extends WizardDialog{

	private ListViewer pagesViewer;
	
	public MultiPageWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	protected Control createContents(Composite parent) {

		final Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		pagesViewer = new ListViewer(main, SWT.FULL_SELECTION | SWT.BORDER);
		pagesViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true));
		pagesViewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
		});
		pagesViewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IWizardPage)element).getTitle();
			}
			
		});
		

		
		Control page = super.createContents(main);
		page.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		pagesViewer.setInput(getWizard().getPages());
		pagesViewer.setSelection(new StructuredSelection(getWizard().getPages()[0]));
		
		
		pagesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				showPage((IWizardPage)((IStructuredSelection)pagesViewer.getSelection()).getFirstElement());
			}
			
		});
		
	
		
		return main;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (getButton(IDialogConstants.NEXT_ID) != null){
			getButton(IDialogConstants.NEXT_ID).setVisible(false);
		}
		
		if (getButton(IDialogConstants.BACK_ID) != null){
			getButton(IDialogConstants.BACK_ID).setVisible(false);
		}
		
	}

	


}

