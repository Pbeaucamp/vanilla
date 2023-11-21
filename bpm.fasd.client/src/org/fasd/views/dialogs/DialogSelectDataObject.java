package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;


public class DialogSelectDataObject extends Dialog {
	private List<DataObject> list = new ArrayList<DataObject>();
	private Combo cbo;
	private DataObject data;
	private String[] filter;
	public DialogSelectDataObject(Shell parentShell, String[] filter) {
		super(parentShell);
		this.filter = filter;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogSelectDataObject_Data_Obj);
		
		cbo = new Combo(parent, SWT.BORDER);
		cbo.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		for(DataSource ds : FreemetricsPlugin.getDefault().getFAModel().getDataSources()){
			for(DataObject o : ds.getDataObjects()){
				if (filter == null){
					cbo.add(o.getName());
					list.add(o);
				}
				else{
					for(String s : filter){
						if(o.getDataObjectType().equals(s)){
							cbo.add(o.getName());
							list.add(o);
						}
					}
					
				}
				
			}
		}
		return parent;
	}

	@Override
	protected void okPressed() {
		data = list.get(cbo.getSelectionIndex());		
		
		super.okPressed();
	}
	
	public DataObject getDataObject(){
		return data;
	}

}
