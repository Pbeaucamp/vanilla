package org.fasd.views.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.ServerConnection;
import org.freeolap.FreemetricsPlugin;

public class CompositeServerConnection {
	private Text name, desc, adress, user, password;
	private Combo type;
	private Button ok, cancel;
	private ServerConnection serv;

	public CompositeServerConnection(Composite parent, ServerConnection s, IViewSite si) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		Label lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.NONE));
		lbl.setText(LanguageText.CompositeServerConnection_0);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label lb2 = new Label(container, SWT.NONE);
		lb2.setLayoutData(new GridData(SWT.NONE));
		lb2.setText(LanguageText.CompositeServerConnection_1);

		desc = new Text(container, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label lb3 = new Label(container, SWT.NONE);
		lb3.setLayoutData(new GridData(SWT.NONE));
		lb3.setText(LanguageText.CompositeServerConnection_2);

		type = new Combo(container, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(new String[] { "authentification", "datasource", "engine", "sourcecode" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		Label lb4 = new Label(container, SWT.NONE);
		lb4.setLayoutData(new GridData(SWT.NONE));
		lb4.setText(LanguageText.CompositeServerConnection_7);

		adress = new Text(container, SWT.BORDER);
		adress.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(SWT.NONE));
		l1.setText(LanguageText.CompositeServerConnection_8);

		user = new Text(container, SWT.BORDER);
		user.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(SWT.NONE));
		l3.setText(LanguageText.CompositeServerConnection_9);

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		ok = new Button(container, SWT.PUSH);
		ok.setText(LanguageText.CompositeServerConnection_10);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				serv.setName(name.getText());
				serv.setDescription(desc.getText());
				serv.setHost(adress.getText());
				serv.setPassword(password.getText());
				serv.setType(type.getText());
				serv.setUser(user.getText());
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		cancel = new Button(container, SWT.PUSH);
		cancel.setText(LanguageText.CompositeServerConnection_11);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		this.serv = s;

		fillData();
	}

	public void fillData() {
		name.setText(serv.getName());
		desc.setText(serv.getDescription());
		adress.setText(serv.getHost());
		user.setText(serv.getUser());
		password.setText(serv.getPassword());
		for (int i = 0; i < type.getItemCount(); i++) {
			if (type.getItem(i).equals(serv.getType())) {
				type.select(i);
				break;
			}
		}

	}
}
