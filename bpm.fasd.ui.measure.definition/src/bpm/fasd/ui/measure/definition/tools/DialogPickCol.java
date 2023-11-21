package bpm.fasd.ui.measure.definition.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.olap.FAModel;

import bpm.fasd.ui.measure.definition.Messages;


public class DialogPickCol extends Dialog {
	private Combo table, col, sources;
	private HashMap<String, DataObject> mapT = new HashMap<String, DataObject>();
	private HashMap<String, DataObjectItem> mapI = new HashMap<String, DataObjectItem>();
	private HashMap<String, DataSource> mapD = new HashMap<String, DataSource>();
	
	private DataObjectItem objItem;
	private Class filter = Object.class ;
	private FAModel model;
	
	public DialogPickCol(Shell parentShell, Class filterClass) {
		super(parentShell);
		this.filter = filterClass;
	}
	
	public DialogPickCol(Shell parentShell, FAModel model) {
		super(parentShell);
		this.model = model;
		
	}
	
	protected Control createDialogArea(Composite parent) {
		for(DataSource d : model.getDataSources()){
			mapD.put(d.getDSName(), d);
		}
		
		Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout)composite.getLayout()).numColumns = 2;
		
		Label l = new Label(composite, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogPickCol_0);
		
		sources = new Combo(composite, SWT.BORDER);
		sources.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sources.setItems(mapD.keySet().toArray(new String[mapD.size()]));
		sources.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				mapT.clear();
				for(DataObject o : mapD.get(sources.getText()).getDataObjects()){
					mapT.put(o.getName(), o);
				}
				table.setItems(mapT.keySet().toArray(new String[mapT.size()]));
			}
			
		});
		
		
		Label lb1 = new Label(composite, SWT.NONE);
		lb1.setLayoutData(new GridData());
		lb1.setText(Messages.DialogPickCol_1);
		
		table = new Combo(composite, SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		table.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapI.clear();
				
				ArrayList<DataObjectItem> items = mapT.get(table.getText()).getColumns();
				
				if(items != null && !items.isEmpty()) {
					Collections.sort(items, new Comparator<DataObjectItem>() {
						@Override
						public int compare(DataObjectItem o1, DataObjectItem o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});
				}
				
				for(DataObjectItem i : items){
					mapI.put(i.getName(), i);
				}
				col.setItems(mapI.keySet().toArray(new String[mapI.size()]));
			}
		});
		
		
		Label lb2 = new Label(composite, SWT.NONE);
		lb2.setLayoutData(new GridData());
		lb2.setText(Messages.DialogPickCol_2);
		
		col = new Combo(composite, SWT.BORDER);
		col.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		col.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				objItem = mapI.get(col.getText());
			}
		});
		
		

		return composite;	
	 }
	

	public DataObjectItem getItem(){
		return objItem;
	}

	
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
				
		shell.setLocation(x, y);
//		shell.setSize(400, 170);
		shell.setText(Messages.DialogPickCol_3);
	}
}