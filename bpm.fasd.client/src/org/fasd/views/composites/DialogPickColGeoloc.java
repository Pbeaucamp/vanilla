package org.fasd.views.composites;

import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.Property;

public class DialogPickColGeoloc extends Dialog {
	private Combo col, type;
	private HashMap<String, DataObjectItem> cols = new HashMap<String, DataObjectItem>();
	private String[] propertyTypes;

	private DataObjectItem objectItem;
	private String propertyType;
	private Property geolocProp;

	public DialogPickColGeoloc(Shell parentShell, HashMap<String, DataObjectItem> cols, String[] propertyTypes) {
		super(parentShell);
		this.propertyTypes = propertyTypes;
		this.cols = cols;
	}

	@Override
	protected void okPressed() {
		if (objectItem != null && propertyType != null) {
			geolocProp = new Property();
			geolocProp.setName(OLAPLevel.GEOLOCALIZABLE_PROPERTY_NAME);
			geolocProp.setColumn(objectItem);
			geolocProp.setColumnId(objectItem.getId());
			geolocProp.setType(propertyType);

			super.okPressed();
		}
	}

	public Property getProperty() {
		return geolocProp;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout) composite.getLayout()).numColumns = 2;

		Label l = new Label(composite, SWT.NONE);
		l.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		l.setText(LanguageText.DialogPickColGeoloc_0);

		col = new Combo(composite, SWT.PUSH);
		col.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		col.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				objectItem = cols.get(col.getText());
			}
		});

		Label lb1 = new Label(composite, SWT.NONE);
		lb1.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lb1.setText(LanguageText.DialogPickColGeoloc_1);

		type = new Combo(composite, SWT.PUSH);
		type.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		type.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				propertyType = type.getText();
			}
		});

		col.setItems(cols.keySet().toArray(new String[cols.size()]));
		type.setItems(propertyTypes);

		return composite;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);
		shell.setSize(400, 200);
		shell.setText(LanguageText.DialogPickColGeoloc_2);
	}
}
