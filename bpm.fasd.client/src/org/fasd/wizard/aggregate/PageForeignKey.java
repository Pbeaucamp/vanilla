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
import org.fasd.olap.OLAPRelation;

public class PageForeignKey extends WizardPage {

	@Override
	protected boolean isCurrentPage() {
		
		return super.isCurrentPage();
	}

	private HashMap<String, DataObjectItem> agg = new HashMap<String, DataObjectItem>();
	private HashMap<String, DataObjectItem> fact = new HashMap<String, DataObjectItem>();
	
	private ListViewer viewer;
	private Combo factCol, aggCol;
	
	protected PageForeignKey(String pageName) {
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
		
		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		l1.setText(LanguageText.PageForeignKey_0);
		
		factCol = new Combo(container, SWT.READ_ONLY);
		factCol.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		l2.setText(LanguageText.PageForeignKey_1);
		
		aggCol = new Combo(container, SWT.READ_ONLY);
		aggCol.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		
		
		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		add.setText(LanguageText.PageForeignKey_2);
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (aggCol.getSelectionIndex() == -1  ||
					factCol.getSelectionIndex() == -1){
					
					return;
				}
				
				OLAPRelation r= new OLAPRelation();
				r.setLeftObjectItem(agg.get(aggCol.getText()));
				r.setRightObjectItem(fact.get(factCol.getText()));
				
				((AggWizard)getWizard()).relations.add(r);
				refresh();
				getWizard().getContainer().updateButtons();
			}
			
		});
		
		Button del = new Button(container, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		del.setText(LanguageText.PageForeignKey_3);
		del.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();
				IStructuredSelection ss = (IStructuredSelection)s;
				Object o = ss.getFirstElement();
				
				if (o != null){
					((AggWizard)getWizard()).relations.remove((OLAPRelation)o);
					refresh();
					getWizard().getContainer().updateButtons();
				}
			}

			
		});
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		l.setText(LanguageText.PageForeignKey_4);
		
		viewer = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 3));
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof OLAPRelation){
					return ((OLAPRelation)element).getName();
				}
				return null;
			}
		});
		viewer.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof DataObjectItem[]){
					return (DataObjectItem[])inputElement;
				}
				if (inputElement instanceof OLAPRelation[]){
					return (OLAPRelation[])inputElement;
				}
				return null;
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
		});
		
	}

	
	protected void fillData(){
		fact.clear();
		agg.clear();
		
		OLAPCube cube = ((AggWizard)getWizard()).cube;
		DataObject table = ((AggWizard)getWizard()).aggregateTable;
		
		for(DataObjectItem i : cube.getFactDataObject().getColumns()){
			fact.put(i.getName(), i);
		}
		
		for(DataObjectItem i : table.getColumns()){
			agg.put(i.getName(), i);
		}
		
		factCol.setItems(fact.keySet().toArray(new String[fact.size()]));
		aggCol.setItems(agg.keySet().toArray(new String[agg.size()]));

	}
	
	protected void refresh(){
		List<OLAPRelation> l = ((AggWizard)getWizard()).relations;
		viewer.setInput(l.toArray(new OLAPRelation[l.size()]));
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	
	
}

