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
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;
import org.freeolap.FreemetricsPlugin;

public class CompositeDetailRelation extends Composite {

	private Text name;
	private Combo leftObject, rightObject, leftItem, rightItem, leftSource, rightSource;
	private Button ok, cancel;
	private OLAPRelation relation;

	private HashMap<String, DataSource> sources = new HashMap<String, DataSource>();
	private HashMap<String, DataObject> rightMap = new HashMap<String, DataObject>();
	private HashMap<String, DataObject> leftMap = new HashMap<String, DataObject>();
	private HashMap<String, DataObjectItem> rightItMap = new HashMap<String, DataObjectItem>();
	private HashMap<String, DataObjectItem> leftItMap = new HashMap<String, DataObjectItem>();

	public CompositeDetailRelation(Composite parent, int style, OLAPRelation r) {
		super(parent, style);
		relation = r;
		this.setLayout(new GridLayout(2, false));

		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL));
		l1.setText(LanguageText.CompositeDetailRelation_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label ld = new Label(this, SWT.NONE);
		ld.setLayoutData(new GridData());
		ld.setText(LanguageText.CompositeDetailRelation_1);

		for (DataSource ds : FreemetricsPlugin.getDefault().getFAModel().getDataSources()) {
			sources.put(ds.getDSName(), ds);
		}
		for (DataObject o : sources.get(relation.getRightObject().getDataSource().getDSName()).getDataObjects()) {
			rightMap.put(o.getName(), o);
		}
		for (DataObject o : sources.get(relation.getLeftObject().getDataSource().getDSName()).getDataObjects()) {
			leftMap.put(o.getName(), o);
		}

		for (DataObjectItem o : rightMap.get(relation.getRightObject().getName()).getColumns()) {
			rightItMap.put(o.getName(), o);
		}
		for (DataObjectItem o : leftMap.get(relation.getLeftObject().getName()).getColumns()) {
			leftItMap.put(o.getName(), o);
		}

		leftSource = new Combo(this, SWT.BORDER);
		leftSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftSource.setItems(sources.keySet().toArray(new String[sources.size()]));
		leftSource.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				leftMap.clear();

				for (DataObject o : sources.get(leftSource.getText()).getDataObjects()) {
					leftMap.put(o.getName(), o);
				}

				leftObject.setItems(leftMap.keySet().toArray(new String[leftMap.size()]));
			}

		});

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeDetailRelation_2);

		leftObject = new Combo(this, SWT.BORDER);
		leftObject.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftObject.setItems(leftMap.keySet().toArray(new String[leftMap.size()]));
		leftObject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				leftItMap.clear();

				for (DataObjectItem i : leftMap.get(leftObject.getText()).getColumns()) {
					leftItMap.put(i.getName(), i);
				}
				leftItem.setItems(leftItMap.keySet().toArray(new String[leftItMap.size()]));
			}

		});

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeDetailRelation_3);

		leftItem = new Combo(this, SWT.BORDER);
		leftItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		leftItem.setItems(leftItMap.keySet().toArray(new String[leftItMap.size()]));

		Label rd = new Label(this, SWT.NONE);
		rd.setLayoutData(new GridData());
		rd.setText(LanguageText.CompositeDetailRelation_4);

		rightSource = new Combo(this, SWT.BORDER);
		rightSource.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightSource.setItems(sources.keySet().toArray(new String[sources.size()]));
		rightSource.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				rightMap.clear();

				for (DataObject o : sources.get(rightSource.getText()).getDataObjects()) {
					rightMap.put(o.getName(), o);
				}

				rightObject.setItems(rightMap.keySet().toArray(new String[rightMap.size()]));
			}

		});

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeDetailRelation_5);

		rightObject = new Combo(this, SWT.BORDER);
		rightObject.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightObject.setItems(rightMap.keySet().toArray(new String[rightMap.size()]));
		rightObject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				rightItMap.clear();

				for (DataObjectItem i : rightMap.get(rightObject.getText()).getColumns()) {
					rightItMap.put(i.getName(), i);
				}
				rightItem.setItems(rightItMap.keySet().toArray(new String[rightItMap.size()]));
			}

		});

		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.CompositeDetailRelation_6);

		rightItem = new Combo(this, SWT.BORDER);
		rightItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rightItem.setItems(rightItMap.keySet().toArray(new String[rightItMap.size()]));

		Composite comp = new Composite(this, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		comp.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		ok = new Button(comp, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.setText(LanguageText.CompositeDetailRelation_7);
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				relation.setLeftObjectItem(leftItMap.get(leftItem.getText()));
				relation.setRightObjectItem(rightItMap.get(rightItem.getText()));
				relation.setName(name.getText());
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}

		});

		cancel = new Button(comp, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		cancel.setText(LanguageText.CompositeDetailRelation_8);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}

		});

		fillData();
	}

	private void fillData() {
		if (relation != null) {
			name.setText(relation.getName());
			leftObject.setText(relation.getLeftObject().getName());
			leftItem.setText(relation.getLeftObjectItem().getName());
			leftSource.setText(relation.getLeftObject().getDataSource().getDSName());
			rightSource.setText(relation.getRightObject().getDataSource().getDSName());
			rightObject.setText(relation.getRightObject().getName());
			rightItem.setText(relation.getRightObjectItem().getName());
		}

	}

}
