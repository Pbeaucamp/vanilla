package org.fasd.views.dialogs;

import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.ServerConnection;
import org.freeolap.FreemetricsPlugin;

public class DialogDataObject extends Dialog {
	private DataObject data;
	private Text transName, desc, fileName, name, select;
	private Combo type, server;
	private HashMap<String, ServerConnection> map = new HashMap<String, ServerConnection>();

	public DialogDataObject(Shell parentShell, DataObject data) {
		super(parentShell);
		this.data = data;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		this.getShell().setText(LanguageText.DialogDataObject_New);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogDataObject_Name_);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogDataObject_Server_);

		server = new Combo(parent, SWT.BORDER);
		server.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (ServerConnection c : FreemetricsPlugin.getDefault().getSecurity().getServerConnections()) {
			map.put(c.getName(), c);
		}
		server.setItems(map.keySet().toArray(new String[map.size()]));
		if (data.getDataSource().getDriver().getDataSourceLocation().equals("local")) { //$NON-NLS-1$
			server.setEnabled(false);
		}

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogDataObject_Type_);

		type = new Combo(parent, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		type.setItems(DataObject.TYPES);

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.DialogDataObject_Select_Statement);

		select = new Text(parent, SWT.BORDER | SWT.MULTI);
		select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		select.setEnabled(false);

		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.DialogDataObject_TTrans_Name_);

		transName = new Text(parent, SWT.BORDER);
		transName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l6 = new Label(parent, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.DialogDataObject_File_Name_);

		fileName = new Text(parent, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.DialogDataObject_Descr_);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (data != null)
			fillData();

		return parent;
	}

	public DataObject getDataObject() {
		return data;
	}

	@Override
	protected void okPressed() {
		if (data == null)
			data = new DataObject();

		data.setName(name.getText());
		data.setDataObjectType(type.getText());
		data.setDesc(desc.getText());
		data.setFileName(fileName.getText());
		data.setSelectStatement(select.getText());
		data.setServer(map.get(server.getText()));
		data.setTransName(transName.getText());
		super.okPressed();
	}

	private void fillData() {
		transName.setText(data.getTransName());
		desc.setText(data.getDesc());
		fileName.setText(data.getFileName());
		name.setText(data.getName());
		select.setText(data.getSelectStatement());

		for (int i = 0; i < type.getItemCount(); i++) {
			if (data.getDataObjectType().equals(type.getItem(i)))
				type.select(i);
		}
		if (data.getDataSource().getDriver().getDataSourceLocation().equals("server")) { //$NON-NLS-1$
			for (int i = 0; i < server.getItemCount(); i++) {
				if (map.keySet().contains(server.getItem(i))) {
					server.select(i);
					break;
				}
			}
		}
	}

}
