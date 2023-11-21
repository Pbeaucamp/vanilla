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
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;
import org.fasd.views.SQLView;
import org.freeolap.FreemetricsPlugin;

public class CompositeTable {
	private Button ok, cancel;
	private DataObject data;
	private Text desc, name;
	private Combo type;

	public CompositeTable(Composite parent, DataObject d, IViewSite s) {
		this.data = d;
		GridLayout gd = new GridLayout(2, false);
		parent.setLayout(gd);

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.CompositeTable_0);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeTable_1);

		type = new Combo(parent, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(DataObject.TYPES);

		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.CompositeTable_2);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (data != null)
			fillData();

		ok = new Button(parent, SWT.PUSH);
		ok.setText(LanguageText.CompositeTable_3);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (data == null)
					data = new DataObject();

				if (!data.getName().equals(name.getText())) {
					data.setName(name.getText());
					FreemetricsPlugin.getDefault().getFAModel().getListDataSource().setChanged();
				}
				data.setDataObjectType(type.getText());
				data.setDesc(desc.getText());

				FreemetricsPlugin.getDefault().getFAModel().setChanged();
				SQLView v = (SQLView) FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SQLView.ID);
				v.refresh(false);
			}
		});

		cancel = new Button(parent, SWT.PUSH);
		cancel.setText(LanguageText.CompositeTable_4);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});
		fillData();

	}

	private void fillData() {
		desc.setText(data.getDesc());
		name.setText(data.getName());

		for (int i = 0; i < type.getItemCount(); i++) {
			if (data.getDataObjectType().equals(type.getItem(i)))
				type.select(i);
		}

	}
}
