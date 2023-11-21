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
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.freeolap.FreemetricsPlugin;

public class CompositeDimension {
	private OLAPDimension dimension;
	private Text name, desc;
	private Button isDate, ok, cancel;
	private Button isGeolocalizabe;
	private Combo loadMethod;

	public CompositeDimension(Composite parent, OLAPDimension dim) {
		this.dimension = dim;

		parent.setLayout(new GridLayout(3, false));
		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.CompositeDimension_0);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeDimension_1);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l7 = new Label(parent, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.CompositeDimension_2);

		loadMethod = new Combo(parent, SWT.BORDER);
		loadMethod.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		loadMethod.setItems(new String[] { "cube_startup", "server_startup", "cube_open", "on_demand" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		isDate = new Button(parent, SWT.CHECK);
		isDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		isDate.setText(LanguageText.CompositeDimension_7);

		isGeolocalizabe = new Button(parent, SWT.CHECK);
		isGeolocalizabe.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		isGeolocalizabe.setText(LanguageText.CompositeDimension_8);

		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.CompositeDimension_9);

		ok = new Button(parent, SWT.PUSH);
		ok.setText(LanguageText.CompositeDimension_10);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				dimension.setDate(isDate.getSelection());
				dimension.setDesc(desc.getText());
				dimension.setGeolocalisable(isGeolocalizabe.getSelection());

				dimension.setLoadMethod(loadMethod.getText());

				if (!dimension.getName().equals(name.getText())) {
					dimension.setName(name.getText());
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListDimensions().setChanged();
					for (OLAPCube c : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getCubes()) {
						if (c.getDims().contains(dimension)) {
							FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
							break;
						}
					}
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		cancel = new Button(parent, SWT.PUSH);
		cancel.setText(LanguageText.CompositeDimension_11);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		fillData();
	}

	public void fillData() {
		name.setText(dimension.getName());
		desc.setText(dimension.getDesc());
		isDate.setSelection(dimension.isDate());
		isGeolocalizabe.setSelection(dimension.isGeolocalisable());
		for (int i = 0; i < loadMethod.getItemCount(); i++) {
			if (loadMethod.getItem(i).equals(dimension.getLoadMethod())) {
				loadMethod.select(i);
			}
		}
	}
}
