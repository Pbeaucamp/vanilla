package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

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
import org.fasd.olap.FAModel;
import org.freeolap.FreemetricsPlugin;


public class DialogSelectDataObjectItem extends Dialog {
	private List<DataSource> sources = new ArrayList<DataSource>();
	private List<DataObject> objects = new ArrayList<DataObject>();
	private List<DataObjectItem> items = new ArrayList<DataObjectItem>();
	private DataSource ds;
	private DataObject od;
	private DataObjectItem doi;
	
	private Combo src, obj, it;
	
	public DialogSelectDataObjectItem(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogSelectDataObjectItem_DS_);
		
		src = new Combo(parent, SWT.NONE);
		src.setLayoutData(new GridData());
		
		for(DataSource d : model.getDataSources()){
			sources.add(d);
			src.add(d.getDSName());
		}
		
		src.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				obj.setItems(new String[]{});
				for(DataSource d : sources){
					if (d.getDSName().equals(src.getText())){
						ds = d;
						for (DataObject o:ds.getDataObjects()){
							objects.add(o);
							obj.add(o.getName());
						}
						break;
					}
				}
			}
			
		});
		
		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogSelectDataObjectItem_Data_Obj_);
		
		obj = new Combo(parent, SWT.NONE);
		obj.setLayoutData(new GridData());
		obj.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
			it.setItems(new String[]{});
				for(DataObject o : ds.getDataObjects()){
					if (o.getName().equals(obj.getText())){
						od = o;
						for (DataObjectItem i:od.getColumns()){
							items.add(i);
							it.add(i.getName());
						}
						break;
					}
				}
			}
		});
				
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogSelectDataObjectItem_data_Obj_Item);
		
		it = new Combo(parent, SWT.NONE);
		it.setLayoutData(new GridData());
		it.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				for(DataObjectItem i : items){
					if (it.getText().equals(i.getName())){
						doi = i;
					}
				}
			}
		});
		return parent;
	}
	
	public DataObjectItem getDataObjectItem(){
		return doi;
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogSelectDataObjectItem_Select);
		super.initializeBounds();
		
	}
}
