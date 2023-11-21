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
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.aggregate.AggLevel;

public class PageDimension extends WizardPage {

	private Combo dimension, level, column, hierarchie;
	private ListViewer viewer;
	private HashMap<String, OLAPLevel> lvl = new HashMap<String, OLAPLevel>();
	private HashMap<String, OLAPHierarchy> hiera = new HashMap<String, OLAPHierarchy>();
	private HashMap<String, OLAPDimension> dim = new HashMap<String, OLAPDimension>();
	private HashMap<String, DataObjectItem> cols = new HashMap<String, DataObjectItem>();
	
	
	protected PageDimension(String pageName) {
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
		l.setText(LanguageText.PageDimension_Dimenson);
		
		dimension = new Combo(container, SWT.READ_ONLY);
		dimension.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dimension.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				hiera.clear();
				for(OLAPHierarchy h : dim.get(dimension.getText()).getHierarchies()){
					hiera.put(h.getName(), h);
				}
				hierarchie.setItems(hiera.keySet().toArray(new String[hiera.size()]));
			}
			
		});

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.PageDimension_Hiearchy);
		
		hierarchie = new Combo(container, SWT.READ_ONLY);
		hierarchie.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		hierarchie.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				lvl.clear();
				for(OLAPLevel l : hiera.get(hierarchie.getText()).getLevels()){
					lvl.put(l.getName(), l);
				}
				level.setItems(lvl.keySet().toArray(new String[lvl.size()]));
				
			}
			
		});
		
		
		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.PageDimension_Level);
		
		level= new Combo(container, SWT.READ_ONLY);
		level.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.PageDimension_Agg_Col);
		
		column = new Combo(container, SWT.READ_ONLY);
		column.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button add = new Button(container, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		add.setText(LanguageText.PageDimension_Add);
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (column.getSelectionIndex() == -1  ||
					dimension.getSelectionIndex() == -1 ||
					hierarchie.getSelectionIndex() == -1 ||
					level.getSelectionIndex() == -1 ||
					column.getSelectionIndex() == -1){
					
					return;
				}
				
				AggLevel agg = new AggLevel();
				agg.setColumn(cols.get(column.getText()));
				agg.setLvl(lvl.get(level.getText()));
				
				((AggWizard)getWizard()).aggLvl.add(agg);
				refresh();
				getWizard().getContainer().updateButtons();
			}
			
		});
		
		Button del = new Button(container, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		del.setText(LanguageText.PageDimension_Remove);
		del.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				ISelection s = viewer.getSelection();
				IStructuredSelection ss = (IStructuredSelection)s;
				Object o = ss.getFirstElement();
				
				if (o != null){
					((AggWizard)getWizard()).aggLvl.remove((AggLevel)o);
					refresh();
					getWizard().getContainer().updateButtons();
				}
			}

			
		});

	
		viewer = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2,1));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof AggLevel[]){
					return (AggLevel[])inputElement;
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
				if (element instanceof AggLevel){
					AggLevel l = (AggLevel)element;
					
					String s = "[" + l.getLvl().getParent().getParent().getName() + "]."; //$NON-NLS-1$ //$NON-NLS-2$
					if (!l.getLvl().getParent().getName().equals("")) //$NON-NLS-1$
						s += "[" + l.getLvl().getParent().getName() + "]."; //$NON-NLS-1$ //$NON-NLS-2$
					s += "[" + l.getLvl().getName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
					
					return s;
				}
				return "";	 //$NON-NLS-1$
			}
			
		});

	}
	
	protected void fillData(){
		
		dim.clear();
		hiera.clear();
		lvl.clear();
		
		OLAPCube cube = ((AggWizard)getWizard()).cube;
		DataObject table = ((AggWizard)getWizard()).aggregateTable;
		
		for(OLAPDimension d : cube.getDims()){
			dim.put(d.getName(), d);
		}
		
		for(DataObjectItem i : table.getColumns()){
			cols.put(i.getName(), i);
		}

		dimension.setItems(dim.keySet().toArray(new String[dim.size()]));
		column.setItems(cols.keySet().toArray(new String[cols.size()]));
		
		viewer.setInput(((AggWizard)getWizard()).aggLvl.toArray(new AggLevel[((AggWizard)getWizard()).aggLvl.size()]));
	}
	
	protected void refresh(){
		List<AggLevel> l = ((AggWizard)getWizard()).aggLvl;
		viewer.setInput(l.toArray(new AggLevel[l.size()]));
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if (((AggLevel[])viewer.getInput()).length ==  0){
			return false;
		}
		return true;
	}
}
