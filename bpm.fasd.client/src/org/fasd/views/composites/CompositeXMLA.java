package org.fasd.views.composites;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
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
import org.fasd.i18N.LanguageText;
import org.fasd.olap.ICube;
import org.fasd.xmla.XMLACube;
import org.fasd.xmla.XMLADataSourceConnection;
import org.fasd.xmla.XMLASchema;
import org.freeolap.FreemetricsPlugin;

import bpm.fa.api.connection.XMLAConnection;
import bpm.fa.api.olap.xmla.XMLADiscover;

public class CompositeXMLA extends Composite {

	private Text url, user, password;
	private Combo schema, catalog, provider;
	private ListViewer viewer, available;
	private List<XMLACube> input = new ArrayList<XMLACube>();

	private HashMap<String, List<String>> map = new HashMap<String, List<String>>();

	private XMLASchema xmla;

	public CompositeXMLA(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new GridLayout(2, false));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.CompositeXMLA_0);

		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.CompositeXMLA_1);

		user = new Text(this, SWT.BORDER);
		user.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeXMLA_2);

		password = new Text(this, SWT.BORDER);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l0 = new Label(this, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(LanguageText.CompositeXMLA_3);

		provider = new Combo(this, SWT.FILL);
		provider.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		provider.setItems(new String[] { XMLAConnection.HyperionProvider, XMLAConnection.MicrosoftProvider, XMLAConnection.MondrianProvider, XMLAConnection.QuartetFsProvider, XMLAConnection.IcCube });

		Button connect = new Button(this, SWT.PUSH);
		connect.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 3, 1));
		connect.setText(LanguageText.CompositeXMLA_4);
		connect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (url.getText().equals("")) //$NON-NLS-1$
					return;

				try {
					map.clear();
					viewer.setInput(null);

					XMLAConnection xmlaSock = new XMLAConnection();
					xmlaSock.setProvider(provider.getText());
					xmlaSock.setPass(password.getText());
					xmlaSock.setUser(user.getText());
					xmlaSock.setUrl(url.getText());

					List<String> l = new ArrayList<String>();
					for (String s : XMLADiscover.discoverDataSources(new URL(url.getText()), user.getText(), password.getText())) {
						l.add(s);
					}
					schema.setItems(l.toArray(new String[l.size()]));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}

		});

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeXMLA_6);

		schema = new Combo(this, SWT.READ_ONLY);
		schema.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		schema.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				XMLAConnection xmlaSock = new XMLAConnection();
				xmlaSock.setProvider(provider.getText());
				xmlaSock.setPass(password.getText());
				xmlaSock.setUser(user.getText());
				xmlaSock.setUrl(url.getText());

				xmlaSock.setDatasource(schema.getText());
				try {

					catalog.setItems(XMLADiscover.discoverSchema(new URL(url.getText()), xmlaSock));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeXMLA_7);

		catalog = new Combo(this, SWT.READ_ONLY);
		catalog.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		catalog.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				XMLAConnection xmlaSock = new XMLAConnection();
				xmlaSock.setProvider(provider.getText());
				xmlaSock.setPass(password.getText());
				xmlaSock.setUser(user.getText());
				xmlaSock.setUrl(url.getText());
				xmlaSock.setSchema(catalog.getText());
				xmlaSock.setDatasource(schema.getText());
				try {
					List<XMLACube> l = new ArrayList<XMLACube>();

					for (String s : XMLADiscover.discoverCubes(new URL(url.getText()), xmlaSock)) {
						XMLACube c = new XMLACube();
						c.setName(s);
						l.add(c);
					}

					available.setInput(l.toArray(new XMLACube[l.size()]));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});

		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label l5 = new Label(container, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		l5.setText(LanguageText.CompositeXMLA_8);

		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.CompositeXMLA_9);

		available = new ListViewer(container, SWT.BORDER);
		available.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		available.setContentProvider(new ListContentProvider());
		available.setLabelProvider(new ListLabelProvider());

		Button add = new Button(container, SWT.PUSH);
		add.setText(LanguageText.CompositeXMLA_10);
		add.setLayoutData(new GridData(GridData.CENTER, GridData.END, false, true));
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) available.getSelection();

				if (ss.isEmpty())
					return;

				Object o = ss.getFirstElement();
				if (o instanceof XMLACube) {
					input.add((XMLACube) o);
					viewer.setInput(input.toArray(new XMLACube[input.size()]));
					available.remove(o);
				}
			}

		});

		viewer = new ListViewer(container, SWT.BORDER);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		viewer.setContentProvider(new ListContentProvider());
		viewer.setLabelProvider(new ListLabelProvider());

		Button sub = new Button(container, SWT.PUSH);
		sub.setText(LanguageText.CompositeXMLA_11);
		sub.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, true));
		sub.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty())
					return;

				Object o = ss.getFirstElement();
				if (o instanceof XMLACube) {
					input.remove(o);
					viewer.setInput(input.toArray(new XMLACube[input.size()]));
					available.add(o);
				}
			}

		});

		Button ok = new Button(this, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		ok.setText(LanguageText.CompositeXMLA_12);
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				XMLADataSourceConnection con = new XMLADataSourceConnection();
				con.setUser(user.getText());
				con.setUrl(url.getText());
				con.setSchema(schema.getText());
				con.setPass(password.getText());
				con.setCatalog(catalog.getText());
				con.setType(provider.getText());
				xmla.setConnection(con);

				for (XMLACube c : (XMLACube[]) viewer.getInput()) {
					xmla.addCube(c);
				}

				if (xmla.getICubes() != null && xmla.getICubes().size() > 0) {

					MessageDialog.openInformation(getShell(), LanguageText.CompositeXMLA_5, LanguageText.CompositeXMLA_14);
				} else {
					MessageDialog.openWarning(getShell(), LanguageText.CompositeXMLA_15, LanguageText.CompositeXMLA_16);
				}
			}

		});

		Button cancel = new Button(this, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		cancel.setText(LanguageText.CompositeXMLA_13);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				user.setText(xmla.getConnection().getUser());
				url.setText(xmla.getConnection().getUrl());
				catalog.setText(xmla.getConnection().getCatalog());
				schema.setText(xmla.getConnection().getSchema());
				password.setText(xmla.getConnection().getPass());
				provider.setText(xmla.getConnection().getType());

				viewer.setInput(xmla.getICubes().toArray(new XMLACube[xmla.getICubes().size()]));
			}

		});
		fillData();
	}

	public void fillData() {
		xmla = FreemetricsPlugin.getDefault().getFAModel().getXMLASchema();
		if (xmla == null) {
			xmla = new XMLASchema();
			FreemetricsPlugin.getDefault().getFAModel().setXMLASchema(xmla);
		}
		url.setText(xmla.getConnection().getUrl());
		user.setText(xmla.getConnection().getUser());
		password.setText(xmla.getConnection().getPass());
		schema.setText(xmla.getConnection().getSchema());
		catalog.setText(xmla.getConnection().getCatalog());

		List<XMLACube> l = new ArrayList<XMLACube>();
		for (ICube c : xmla.getICubes()) {
			l.add((XMLACube) c);
		}

		viewer.setInput(l.toArray(new XMLACube[l.size()]));

	}

	private class ListContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return (XMLACube[]) inputElement;
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

	}

	private class ListLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			return ((XMLACube) element).getName();
		}

	}
}
