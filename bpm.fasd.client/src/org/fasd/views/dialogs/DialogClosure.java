package org.fasd.views.dialogs;

import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPLevel;
import org.freeolap.FreemetricsPlugin;


public class DialogClosure extends Dialog {

	private OLAPLevel lvl;
	private Combo table, tableParent, tableChild;
	
	
	private HashMap<String, DataObjectItem> mapTableCol = new HashMap<String, DataObjectItem>();;
	private HashMap<String, DataObject> mapTable = new HashMap<String, DataObject>();
	
	public DialogClosure(Shell parentShell, OLAPLevel level) {
		super(parentShell);
		lvl = level;
		init();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
			
				
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogClosure_Close_tbl_);
		
		table = new Combo(parent, SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		table.setItems(mapTable.keySet().toArray(new String[mapTable.size()]));
		
		table.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				mapTableCol = new HashMap<String, DataObjectItem>();
				for(DataObjectItem i : mapTable.get(table.getText()).getColumns()){
					mapTableCol.put(i.getName(), i);
				}
				tableParent.setItems(mapTableCol.keySet().toArray(new String[mapTableCol.size()]));
				tableChild.setItems(mapTableCol.keySet().toArray(new String[mapTableCol.size()]));
			}
			
		});
		
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.DialogClosure_Tbl_Par_Col);
		
		tableParent = new Combo(parent, SWT.BORDER);
		tableParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableParent.setItems(mapTableCol.keySet().toArray(new String[mapTableCol.size()]));
		
		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.DialogClosure_tbl_Child_Col);
		
		tableChild = new Combo(parent, SWT.BORDER);
		tableChild.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		tableChild.setItems(mapTableCol.keySet().toArray(new String[mapTableCol.size()]));
		fillData();
		
		return parent;
	}

	
	private void init(){
			
		for(DataSource ds : FreemetricsPlugin.getDefault().getFAModel().getDataSources()){
			for(DataObject o : ds.getDataObjects()){
					mapTable.put(o.getName(), o);
			}
		}
		
		if (lvl.getClosureTable() != null){
			mapTableCol = new HashMap<String, DataObjectItem>();
			for(DataObjectItem i : lvl.getClosureTable().getColumns()){
				mapTableCol.put(i.getName(), i);
			}
		}
	}

	@Override
	protected void okPressed() {
		lvl.setClosureTable(mapTable.get(table.getText()));
		lvl.setClosureParentCol(mapTableCol.get(tableParent.getText()));
		lvl.setClosureChildCol(mapTableCol.get(tableChild.getText()));
		super.okPressed();
	}
	
	private void fillData(){
		
		if (lvl.getClosureTable() != null){
			for(int i=0; i<table.getItemCount(); i++){
				if (table.getItem(i).equals(lvl.getClosureTable().getName())){
					table.select(i);
					break;
				}
				
			}
		}
		if (lvl.getClosureParentCol() != null){
			for(int i=0; i<tableParent.getItemCount(); i++){
				if (tableParent.getItem(i).equals(lvl.getClosureParentCol().getName())){
					tableParent.select(i);
					break;
				}
				
			}
		}
		if (lvl.getClosureChildCol() != null){
			for(int i=0; i<tableChild.getItemCount(); i++){
				if (tableChild.getItem(i).equals(lvl.getClosureChildCol().getName())){
					tableChild.select(i);
					break;
				}
				
			}
		}
		
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogClosure_def_Closure);
		super.initializeBounds();
	}
}
