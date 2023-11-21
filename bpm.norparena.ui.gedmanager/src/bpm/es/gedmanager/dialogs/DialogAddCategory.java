package bpm.es.gedmanager.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.es.gedmanager.Messages;
import bpm.vanilla.platform.core.beans.ged.Category;

public class DialogAddCategory extends Dialog {
	
	private Text tname;
	private String name;
	//private Display display;
	
	private Category catparent;
	
	public DialogAddCategory(Shell parentShell, Category catparent) {
		super(parentShell);
		
		//this.display = parentShell.getDisplay();
		this.catparent = catparent;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		
		Label lname = new Label(main, SWT.NONE);
		lname.setText(Messages.DialogAddCategory_0);
		
		tname = new Text(main, SWT.BORDER);
		tname.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		tname.setText(""); //$NON-NLS-1$
		
		Label lparent = new Label(main, SWT.NONE);
		lparent.setText(Messages.DialogAddCategory_2);
		
		Text tparent = new Text(main, SWT.BORDER);
		tparent.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
		tparent.setText(catparent == null ? "none" : catparent.getName()); //$NON-NLS-1$
		tparent.setEditable(false);
		
		return main;
	}
	
	protected void okPressed() {	
		name = tname.getText();
		
		super.okPressed();
	}
	
	public Category getCategory() {
		Category category = new Category();
		category.setId(0); //will be set later;
		category.setName(name);
		if (catparent != null)
			category.setParentId(catparent.getId());
		else 
			category.setParentId(0);
		return category;
	}
}

