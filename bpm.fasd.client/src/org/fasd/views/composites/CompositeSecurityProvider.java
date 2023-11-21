package org.fasd.views.composites;

import java.util.HashMap;

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
import org.fasd.olap.SecurityProvider;
import org.fasd.olap.ServerConnection;
import org.freeolap.FreemetricsPlugin;

public class CompositeSecurityProvider {
	private Text name, desc, url, user, password;
	private Combo type, serv;
	private Button ok, cancel;
	private SecurityProvider prov;
	private HashMap<String, ServerConnection> map = new HashMap<String, ServerConnection>();

	public CompositeSecurityProvider(Composite parent, SecurityProvider s, IViewSite si) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		Label lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.NONE));
		lbl.setText(LanguageText.CompositeSecurityProvider_0);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label lb2 = new Label(container, SWT.NONE);
		lb2.setLayoutData(new GridData(SWT.NONE));
		lb2.setText(LanguageText.CompositeSecurityProvider_1);

		desc = new Text(container, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label lb3 = new Label(container, SWT.NONE);
		lb3.setLayoutData(new GridData(SWT.NONE));
		lb3.setText(LanguageText.CompositeSecurityProvider_2);

		type = new Combo(container, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(new String[] { "ldap", "mmsad" }); //$NON-NLS-1$ //$NON-NLS-2$

		Label lb4 = new Label(container, SWT.NONE);
		lb4.setLayoutData(new GridData(SWT.NONE));
		lb4.setText(LanguageText.CompositeSecurityProvider_5);

		url = new Text(container, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(SWT.NONE));
		l1.setText(LanguageText.CompositeSecurityProvider_6);

		serv = new Combo(container, SWT.BORDER);
		serv.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		for (ServerConnection c : FreemetricsPlugin.getDefault().getSecurity().getServerConnections()) {
			map.put(c.getName(), c);
		}
		serv.setItems(map.keySet().toArray(new String[map.size()]));

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(SWT.NONE));
		l2.setText(LanguageText.CompositeSecurityProvider_7);

		user = new Text(container, SWT.BORDER);
		user.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(SWT.NONE));
		l3.setText(LanguageText.CompositeSecurityProvider_8);

		password = new Text(container, SWT.BORDER);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		ok = new Button(container, SWT.PUSH);
		ok.setText(LanguageText.CompositeSecurityProvider_9);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				prov.setName(name.getText());
				prov.setDescription(desc.getText());
				prov.setUrl(url.getText());
				prov.setPassword(password.getText());
				prov.setType(type.getText());
				prov.setUser(user.getText());
				prov.setServer(map.get(serv.getText()));

				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		cancel = new Button(container, SWT.PUSH);
		cancel.setText(LanguageText.CompositeSecurityProvider_10);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		this.prov = s;

		fillData();
	}

	public void fillData() {
		name.setText(prov.getName());
		desc.setText(prov.getDescription());
		url.setText(prov.getUrl());

		user.setText(prov.getUser());
		password.setText(prov.getPassword());

		if (prov.getServer() != null) {
			for (int i = 0; i < serv.getItemCount(); i++) {
				if (map.get(serv.getItem(i)).getId().equals(prov.getServer().getId())) {
					serv.select(i);
					break;
				}
			}
		}

		for (int i = 0; i < type.getItemCount(); i++) {
			if (type.getItem(i).equals(prov.getType())) {
				type.select(i);
				break;
			}
		}

	}
}
