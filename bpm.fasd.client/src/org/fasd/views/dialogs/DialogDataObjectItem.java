package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;


public class DialogDataObjectItem extends Dialog {
	private DataObjectItem item = null;
	private Combo type, attribut;
	private Text desc, name, parent, classe, origin;
	
	public DialogDataObjectItem(Shell parentShell) {
		super(parentShell);
	}
	
	public DialogDataObjectItem(Shell sh, DataObjectItem it){
		super(sh);
		item = it;
	}

	public DataObjectItem getDataObjectItem(){
		return item;
	}
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(LanguageText.DialogDataObjectItem_New);
	}
	protected Control createDialogArea(Composite parent){
		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogDataObjectItem_Name_);
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogDataObjectItem_Descr_);
		
		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogDataObjectItem_Parent_);
		
		this.parent = new Text(parent, SWT.BORDER);
		this.parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.DialogDataObjectItem_Class_);
		
		classe = new Text(parent, SWT.BORDER);
		classe.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.DialogDataObjectItem_Type);
		
		type = new Combo(parent, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		type.setItems(new String[]{"calculated", "physical"}); //$NON-NLS-1$ //$NON-NLS-2$
		
		Label l6 = new Label(parent, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.DialogDataObjectItem_Attr);
		
		attribut = new Combo(parent, SWT.BORDER);
		attribut.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		attribut.setItems(new String[]{"Dimension", "Measure", "Property", "Undefined"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.DialogDataObjectItem_Origin);
		
		origin = new Text(parent, SWT.BORDER);
		origin.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (item != null)
			fillData();
		return parent;
		
	}
	
	private void fillData(){
		name.setText(item.getName());
		desc.setText(item.getDesc());
		classe.setText(item.getClasse());
		this.parent.setText(item.getParent().getName());
		
		for(int i=0; i<type.getItemCount(); i++){
			if (item.getType().equals(type.getItem(i))){
				type.select(i);
			}
		}
		
		for(int i=0; i<attribut.getItemCount(); i++){
			if (item.getType().equals(attribut.getItem(i).substring(1, 1))){
				attribut.select(i);
			}
		}
		
		origin.setText(item.getOrigin());
	}

	@Override
	protected void okPressed() {
		item.setAttribut(attribut.getText().substring(1, 1));
		item.setClasse(classe.getText());
		item.setDesc(desc.getText());
		item.setName(name.getText());
		item.setOrigin(origin.getText());
		item.setType(type.getText());
		super.okPressed();
	}
}
