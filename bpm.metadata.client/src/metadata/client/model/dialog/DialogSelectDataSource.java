package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.IDataSource;

public class DialogSelectDataSource extends Dialog {

	
	private Combo combo;
	private IDataSource dataSource;
	
	public DialogSelectDataSource(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

		
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 200);
		getShell().setText(Messages.DialogSelectDataSource_0); 
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogSelectDataSource_1);
		
		combo = new Combo(c, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		createInput();
		
		return c;
	}
	
	private void createInput(){
		List<String> l = new ArrayList<String>();
		
		for(IDataSource ds : Activator.getDefault().getModel().getDataSources()){
			l.add(ds.getName());
		}
		
		combo.setItems(l.toArray(new String[l.size()]));
	}
	
	public IDataSource getDataSource(){
		return dataSource;
	}

	@Override
	protected void okPressed() {
		dataSource = Activator.getDefault().getModel().getDataSource(combo.getText());
		super.okPressed();
	}
	
	

}
