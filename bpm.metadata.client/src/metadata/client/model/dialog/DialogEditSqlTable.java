package metadata.client.model.dialog;

import java.util.HashMap;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.misc.MetaDataChecker;
import bpm.metadata.resource.IFilter;

public class DialogEditSqlTable extends Dialog {
	
	private IDataStream table;
	private Text name;
	private Text description;
	private Text query;
	private String value;

	public DialogEditSqlTable(Shell parentShell, IDataStream table) {
		super(parentShell);
		
		this.table = table;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.PageQuery_0); //$NON-NLS-1$
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				
			}
			
		});
		
		name.setEnabled(false);
		name.setText(table.getName());
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 2));
		l2.setText(Messages.PageQuery_1); //$NON-NLS-1$
		
		description = new Text(container, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		description.setText(table.getDescription());
		
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		l3.setText(Messages.PageQuery_2); //$NON-NLS-1$
		
		query = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		query.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		query.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				value = query.getText();
				
			}
			
		});
		query.setText(((SQLDataStream)table).getSql());
		
		query.setFocus();
		
		return container;
	}

	@Override
	protected void okPressed() {
		AbstractDataSource ds = (AbstractDataSource)table.getDataSource();
		
		String name = this.name.getText();
		
		IDataStream oldTable = ds.getDataStreamNamed(name);
		
		try {
			ds.addTableFromQuery(name, value);
			
			IDataStream newTable = ds.getDataStreamNamed(name);
			
			HashMap<Object, String> errors = new HashMap<Object, String>();
			
			for(IFilter f : oldTable.getFilters()) {
				boolean finded = false;
				if(newTable.getElementNamed(f.getOrigin().getName()) != null) {
					newTable.addFilter(f);
					finded = true;
					break;
				}
				if(!finded) {
					errors.put(f, "Cannot find the origin of the filter " + f.getName());
				}
			}
			
			errors.putAll(MetaDataChecker.checkModel(ds.getMetaDataModel()));
			
			if(errors != null && errors.size() > 0) {
				
				StringBuilder buf = new StringBuilder();
				buf.append("Some objects won't work after the table update : \n\n");
				for(String s : errors.values()) {
					buf.append(s + "\n");
				}
				
				buf.append("\nDo you want to update it anyway ?");
				
				
				if(MessageDialog.openConfirm(getShell(), "Warning", buf.toString())) {
					super.okPressed();
				}
				else {
					ds.add(oldTable);
				}
			}
			
			else {
				super.okPressed();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", "An error occured while updating the table : " + e.getMessage());
		}
		
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
	
}
