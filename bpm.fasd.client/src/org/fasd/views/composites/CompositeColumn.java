package org.fasd.views.composites;

import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;

public class CompositeColumn {
	private DataObjectItem item = null;
	private Combo type, attribut;
	private Text desc, name, parent, classe, origin;
	private Button ok, cancel;

	private IViewSite site;

	public CompositeColumn(Composite container, DataObjectItem d, IViewSite s) {
		this.item = d;
		this.site = s;

		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.CompositeColumn_0);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setEnabled(false);

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeColumn_1);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeColumn_2);

		this.parent = new Text(parent, SWT.BORDER);
		this.parent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		this.parent.setEnabled(false);

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeColumn_3);

		classe = new Text(parent, SWT.BORDER);
		classe.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		classe.setEnabled(false);

		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.CompositeColumn_4);

		type = new Combo(parent, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(new String[] { "calculated", "physical" }); //$NON-NLS-1$ //$NON-NLS-2$

		Label l6 = new Label(parent, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.CompositeColumn_7);

		attribut = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		attribut.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		attribut.setItems(new String[] { "Dimension", "Measure", "Property", "Undefined" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 2));
		l7.setText(LanguageText.CompositeColumn_12);

		origin = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		origin.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		if (item.getType().equals("physical")) //$NON-NLS-1$
			origin.setEnabled(false);

		ok = new Button(parent, SWT.NONE);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.setText(LanguageText.CompositeColumn_14);
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!item.getName().equals(name.getText())) {
					String itemName = name.getText();
					
					if(Pattern.matches("^[\\S]*$", itemName)) { //$NON-NLS-1$
						itemName.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					item.setName(name.getText());
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				}
				item.setAttribut(attribut.getText().substring(0, 1));
				item.setDesc(desc.getText());

				item.setOrigin(origin.getText());
				item.setType(type.getText());

				FreemetricsPlugin.getDefault().getFAModel().setChanged();

			}

		});

		cancel = new Button(parent, SWT.NONE);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(LanguageText.CompositeColumn_15);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}

		});

		if (item != null)
			fillData();
	}

	public void fillData() {
		name.setText(item.getName());
		desc.setText(item.getDesc());
		try {
			classe.setText(Class.forName(item.getClasse()).getSimpleName());
		} catch (ClassNotFoundException e) {
			MessageDialog.openError(site.getShell(), LanguageText.CompositeColumn_16, LanguageText.CompositeColumn_17 + item.getId() + " " + item.getName() + LanguageText.CompositeColumn_19); //$NON-NLS-1$
			e.printStackTrace();
		}
		this.parent.setText(item.getParent().getName());

		for (int i = 0; i < type.getItemCount(); i++) {
			if (item.getType().equals(type.getItem(i))) {
				type.select(i);
			}
		}

		for (int i = 0; i < attribut.getItemCount(); i++) {
			if (item.getAttribut().equals(attribut.getItem(i).substring(0, 1))) {
				attribut.select(i);
			}
		}

		origin.setText(item.getOrigin());
	}
}
