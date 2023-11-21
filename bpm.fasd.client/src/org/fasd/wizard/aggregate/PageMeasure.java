package org.fasd.wizard.aggregate;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.aggregate.AggMeasure;

public class PageMeasure extends WizardPage {

	private Combo measure, column;
	private ListViewer viewer;
	private HashMap<String, OLAPMeasure> mes = new HashMap<String, OLAPMeasure>();
	private HashMap<String, DataObjectItem> col = new HashMap<String, DataObjectItem>();
	
	protected PageMeasure(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
//		 create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);

	}
	
	private void createPageContent(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.PageMeasure_Measure);
		
		measure = new Combo(container, SWT.READ_ONLY);
		measure.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.PageMeasure_Agg_Col);
		
		column = new Combo(container, SWT.READ_ONLY);
		column.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		add.setText(LanguageText.PageMeasure_Add);
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (column.getText().equals("") || measure.getText().equals("")) //$NON-NLS-1$ //$NON-NLS-2$
					return;
				AggMeasure agg = new AggMeasure();
				agg.setColumn(col.get(column.getText()));
				agg.setMes(mes.get(measure.getText()));
				
				((AggWizard)getWizard()).aggMes.add(agg);
				refresh();
				getWizard().getContainer().updateButtons();
				
			}
			
		});
		
		Button del = new Button(container, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		del.setText(LanguageText.PageMeasure_Remove);
		del.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();
				IStructuredSelection ss = (IStructuredSelection)s;
				Object o = ss.getFirstElement();
				
				if (o != null){
					((AggWizard)getWizard()).aggMes.remove((AggMeasure)o);
					refresh();
					getWizard().getContainer().updateButtons();
				}
				
			}

			
		});
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false, 2, 1));
		l3.setText(LanguageText.PageMeasure_Matching);
		
		viewer = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2,1));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof AggMeasure[]){
					return (AggMeasure[])inputElement;
				}
				return null;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}

				
		});
	
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof AggMeasure){
					AggMeasure m = (AggMeasure)element;
					return m.getMes().getName() + " on " + m.getColumn().getName(); //$NON-NLS-1$
				}
				return "";	 //$NON-NLS-1$
			}
			
		});
	}
	
	protected void fillData(){
		OLAPCube cube = ((AggWizard)getWizard()).cube;
		DataObject table = ((AggWizard)getWizard()).aggregateTable;
		
		for(OLAPMeasure m : cube.getMes()){
			mes.put(m.getName(), m);
		}
		
		for(DataObjectItem i : table.getColumns()){
			col.put(i.getName(), i);
		}
		
		measure.setItems(mes.keySet().toArray(new String[mes.size()]));
		column.setItems(col.keySet().toArray(new String[col.size()]));
		
		viewer.setInput(((AggWizard)getWizard()).aggMes.toArray(new AggMeasure[((AggWizard)getWizard()).aggMes.size()]));
	}

	@Override
	public boolean canFlipToNextPage() {
		if (((AggMeasure[])viewer.getInput()).length ==  0){
			return false;
		}
		return true;
	}
	
	protected void refresh(){
		List<AggMeasure> l = ((AggWizard)getWizard()).aggMes;
		viewer.setInput(l.toArray(new AggMeasure[l.size()]));
	}
}
