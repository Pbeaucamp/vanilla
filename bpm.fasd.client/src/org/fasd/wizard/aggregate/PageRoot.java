package org.fasd.wizard.aggregate;

import java.util.HashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.freeolap.FreemetricsPlugin;

public class PageRoot extends WizardPage {

	@Override
	public boolean canFlipToNextPage() {
		if (!table.getText().equals("")) //$NON-NLS-1$
			return true;
		return false;
	}

	private HashMap<String, DataObject> mapTable = new HashMap<String, DataObject>();
	private HashMap<String, DataObjectItem> mapCols = new HashMap<String, DataObjectItem>();
	private Combo table;
	private Combo factCol;
	
	protected PageRoot(String pageName) {
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
		l.setText(LanguageText.PageRoot_1);
		
		table = new Combo(container, SWT.READ_ONLY);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		table.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((AggWizard)getWizard()).aggregateTable = mapTable.get(table.getText());
				getWizard().getContainer().updateButtons();
				
				mapCols.clear();
				for(DataObjectItem i : mapTable.get(table.getText()).getColumns()){
					mapCols.put(i.getName(), i);
				}
				factCol.setItems(mapCols.keySet().toArray(new String[mapCols.size()]));
			}
			
		});
		
		
		//build model for combo
		OLAPCube cube = ((AggWizard)this.getWizard()).cube;
		for(DataObject o : FreemetricsPlugin.getDefault().getFAModel().findDataSource(cube.getDataSource().getId()).getDataObjects()){
			mapTable.put(o.getName(), o);
		}
		table.setItems(mapTable.keySet().toArray(new String[mapTable.size()]));
		
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.PageRoot_2);
		
		factCol = new Combo(container, SWT.READ_ONLY);
		factCol.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		factCol.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((AggWizard)getWizard()).countCol = mapCols.get(factCol.getText());
				getWizard().getContainer().updateButtons();
			}
			
		});
		
	}

}

