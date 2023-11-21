package bpm.fd.design.ui.edition;

import org.eclipse.datatools.connectivity.oda.OdaException;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.design.ui.Messages;

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
		Button b = createButton(parent, IDialogConstants.PROCEED_ID, Messages.MultiPageWizardDialog_0, false);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getWizard() instanceof EditionDataSetWizard){
					try {
						((EditionDataSetWizard)getWizard()).finishDataSetDesign();
					} catch (OdaException e1) {
						
						e1.printStackTrace();
					}
				}
				
			}
		});
		
		
		
		
		
	}

	


}

