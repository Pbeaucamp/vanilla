package org.fasd.wizard.inline;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;

public class PageStructure extends WizardPage {

	private Text colsName, tableName;
	private Combo colsType;
	private HashMap<String, String> type = new HashMap<String, String>();
	private ListViewer viewer;
	
	protected PageStructure(String pageName) {
		super(pageName);
		type.put("String", "java.lang.String"); //$NON-NLS-1$ //$NON-NLS-2$
		type.put("Numeric", "java.lang.Number"); //$NON-NLS-1$ //$NON-NLS-2$
		type.put("Integer", "java.lang.Integer"); //$NON-NLS-1$ //$NON-NLS-2$
		type.put("Boolean", "java.lang.Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
		type.put("Date", "java.util.Date"); //$NON-NLS-1$ //$NON-NLS-2$
		
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(LanguageText.PageStructure_Table_Name);
		
		tableName = new Text(container, SWT.BORDER);
		tableName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.PageStructure_Col_Name);
		
		colsName = new Text(container, SWT.BORDER);
		colsName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.PageStructure_Col_Type);
		
		colsType = new Combo(container, SWT.READ_ONLY);
		colsType.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		colsType.setItems(type.keySet().toArray(new String[type.keySet().size()]));
		
		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		add.setText(LanguageText.PageStructure_Add);
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataObjectItem it = new DataObjectItem();
				it.setName(colsName.getText());
				it.setOrigin(colsName.getText());
				it.setClasse(type.get(colsType.getText()));
				it.setSqlType(colsType.getText());
				it.setAttribut("U"); //$NON-NLS-1$

				((InlineWizard)getWizard()).inlineTable.addDataObjectItem(it);
				
				List<DataObjectItem> l = ((InlineWizard)getWizard()).inlineTable.getColumns(); 
				
				viewer.setInput(l.toArray(new DataObjectItem[l.size()]));
				viewer.refresh();
				getWizard().getContainer().updateButtons();
			}
			
		});
		
		Button del = new Button(container, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		del.setText(LanguageText.PageStructure_Remove);
		del.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				if (ss.isEmpty())
					return;
				
				DataObjectItem it = (DataObjectItem)ss.getFirstElement();
				
				((InlineWizard)getWizard()).inlineTable.removeItem(it);
				List<DataObjectItem> l = ((InlineWizard)getWizard()).inlineTable.getColumns(); 

				viewer.setInput(l.toArray(new DataObjectItem[l.size()]));
				viewer.refresh();
				getWizard().getContainer().updateButtons();
			}
			
		});
		
		viewer = new ListViewer(container, SWT.BORDER);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				DataObjectItem it = (DataObjectItem)element;
				
				return it.getName() + " as " + it.getClasse(); //$NON-NLS-1$
			}
			
		});
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return (DataObjectItem[])inputElement;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}

			
			
		});

		this.setControl(container);
	}

	@Override
	public boolean canFlipToNextPage() {
		if (tableName.getText().equals("") || ((DataObjectItem[])viewer.getInput()).length == 0) //$NON-NLS-1$
			return false;
		
		return true;
	}

	@Override
	protected boolean isCurrentPage() {
		return super.isCurrentPage();
	}

	@Override
	public IWizardPage getNextPage() {
		((InlineWizard)getWizard()).inlineTable.setName(tableName.getText());
		return super.getNextPage();
	}
	
}
