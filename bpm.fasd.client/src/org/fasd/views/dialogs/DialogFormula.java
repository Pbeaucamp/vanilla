package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPMeasure;
import org.fasd.views.composites.CompositeFormula;
import org.fasd.views.composites.DialogPickCol;
import org.freeolap.FreemetricsPlugin;

public class DialogFormula extends Dialog {

	private OLAPMeasure formula = null;
	private DataObjectItem oriData, labelData;
	private CompositeFormula container;
	private Composite info;
	private Text format, name, desc, order, origin, label;
	private Combo type, agg;
	private Button b, visible;
	private TabItem itemRecent;

	private Combo dimensionForLast;

	@Override
	protected void initializeBounds() {
		this.getShell().setSize(650, 500);
		this.getShell().setText(LanguageText.DialogFormula_New_Measure);
		super.initializeBounds();

	}

	public DialogFormula(Shell parentShell) {
		super(parentShell);
	}

	public DialogFormula(Shell parentShell, OLAPMeasure formula) {
		super(parentShell);
		this.formula = formula;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, true));

		// tabfolder
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		TabItem itemStart = new TabItem(tabFolder, SWT.NONE, 0);
		itemStart.setText(LanguageText.DialogFormula_Information);
		itemStart.setControl(getTabInfoControl(tabFolder));

		itemRecent = new TabItem(tabFolder, SWT.NONE);
		itemRecent.setText(LanguageText.DialogFormula_Formula);
		itemRecent.setControl(getTabFormulaControl(tabFolder));
		itemRecent.getControl().setEnabled(false);

		tabFolder.setSelection(0);
		return parent;
	}

	private Control getTabFormulaControl(TabFolder tabFolder) {
		container = new CompositeFormula(tabFolder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		if (formula != null)
			container.setFormula(formula.getFormula());
		return container;
	}

	private Control getTabInfoControl(TabFolder tabFolder) {
		info = new Composite(tabFolder, SWT.NONE);
		info.setLayout(new GridLayout(3, false));

		Label lb1 = new Label(info, SWT.NONE);
		lb1.setLayoutData(new GridData());
		lb1.setText(LanguageText.DialogFormula_Name);

		name = new Text(info, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.setText(LanguageText.DialogFormula_New_Measure);

		Label l2 = new Label(info, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogFormula_Descr_);

		desc = new Text(info, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l3 = new Label(info, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogFormula_Type_);

		type = new Combo(info, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		type.setItems(new String[] { "physical", "calculated" }); //$NON-NLS-1$ //$NON-NLS-2$
		type.select(0);
		type.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (type.getText().equals("calculated")) { //$NON-NLS-1$
					origin.setEnabled(false);
					agg.setEnabled(false);
					b.setEnabled(false);
					oriData = null;
					origin.setText(""); //$NON-NLS-1$
					itemRecent.getControl().setEnabled(true);
				} else if (type.getText().equals("physical")) { //$NON-NLS-1$
					origin.setEnabled(true);
					agg.setEnabled(true);
					b.setEnabled(true);
					itemRecent.getControl().setEnabled(false);
					container.clear();
				}
			}

		});

		Label l5 = new Label(info, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.DialogFormula_Origin_);

		origin = new Text(info, SWT.BORDER);
		origin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		b = new Button(info, SWT.PUSH);
		b.setText("..."); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(DialogFormula.this.getShell());
				if (dial.open() == Dialog.OK) {
					origin.setText(dial.getItem().getFullName());
					oriData = dial.getItem();
					name.setText(oriData.getName());

				}
			}
		});

		Label l4 = new Label(info, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.DialogFormula_Order_);

		order = new Text(info, SWT.BORDER);
		order.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l6 = new Label(info, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.DialogFormula_Lbl_);

		label = new Text(info, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Button b1 = new Button(info, SWT.PUSH);
		b1.setText("..."); //$NON-NLS-1$
		b1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(DialogFormula.this.getShell());
				if (dial.open() == Dialog.OK) {
					label.setText(dial.getItem().getFullName());
					labelData = dial.getItem();
				}
			}

		});

		Label l7 = new Label(info, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.DialogFormula_Agg_);

		agg = new Combo(info, SWT.NONE);
		agg.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		agg.setItems(new String[] { "avg", "min", "max", "sum", "count", "distinct-count", "last" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		agg.select(3);
		agg.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				dimensionForLast.setEnabled(agg.getText().equals("last")); //$NON-NLS-1$

			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		l7 = new Label(info, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.DialogFormula_2);

		dimensionForLast = new Combo(info, SWT.NONE);
		dimensionForLast.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		dimensionForLast.setEnabled(false);

		Label lb2 = new Label(info, SWT.NONE);
		lb2.setLayoutData(new GridData());
		lb2.setText(LanguageText.DialogFormula_Format_String_);

		format = new Text(info, SWT.BORDER);
		format.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		format.setText(LanguageText.DialogFormula_Std);

		visible = new Button(info, SWT.CHECK);
		visible.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		visible.setText(LanguageText.DialogFormula_Visible);
		visible.setSelection(true);
		fillData();
		return info;
	}

	private void fillData() {
		List<String> dimensions = new ArrayList<String>();
		for (OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
			dimensions.add(new String(d.getName()));
		}
		dimensionForLast.setItems(dimensions.toArray(new String[dimensions.size()]));

		if (formula != null) {
			dimensionForLast.setEnabled(formula.getAggregator().equals("last")); //$NON-NLS-1$
			if (formula.getLastTimeDimensionName() != null) {
				dimensionForLast.setText(formula.getLastTimeDimensionName());
			}

			name.setText(formula.getName());
			format.setText(formula.getFormatstr());
			desc.setText(formula.getDesc());
			order.setText(String.valueOf(formula.getOrder()));

			visible.setSelection(formula.isVisible());

			label.setText(formula.getLabel().getFullName());

			if (formula.getType().equals("physical")) { //$NON-NLS-1$
				type.select(1);
				origin.setText(formula.getOrigin().getFullName());
				for (int i = 0; i < agg.getItemCount(); i++) {
					if (agg.getItem(i).equals(formula.getAggregator())) {
						agg.select(i);
						break;
					}
				}
			} else {
				type.select(0);
			}
		}

	}

	public OLAPMeasure getFormula() {
		return formula;
	}

	@Override
	protected void okPressed() {

		if (formula == null)
			formula = new OLAPMeasure();

		formula.setDesc(desc.getText());
		formula.setName(name.getText());
		formula.setOrder(order.getText());
		formula.setType(type.getText());

		formula.setFormatstr(format.getText());
		formula.setName(name.getText());
		formula.setLabel(labelData);

		formula.setVisible(visible.getSelection());
		formula.setLastTimeDimensionName(dimensionForLast.getText());
		if (formula.getType().equals("physical")) { //$NON-NLS-1$
			formula.setAggregator(agg.getText());
			formula.setOrigin(oriData);
		} else {
			formula.setFormula(container.getFormula());
		}

		if (formula.getType().equals("calculated") && formula.getFormula().trim().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			MessageDialog.openInformation(this.getShell(), LanguageText.DialogFormula_Warning, LanguageText.DialogFormula_Formula_Not_Def);
			return;
		}

		else if (formula.getType().equals("physical") && formula.getOrigin() == null) { //$NON-NLS-1$
			MessageDialog.openInformation(this.getShell(), LanguageText.DialogFormula_Warning, LanguageText.DialogFormula_MeasureOrigin);
			return;
		}
		super.okPressed();
	}
}
