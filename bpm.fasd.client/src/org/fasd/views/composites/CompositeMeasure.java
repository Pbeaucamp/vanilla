package org.fasd.views.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewSite;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPMeasure;
import org.fasd.views.dialogs.DialogCombo;
import org.freeolap.FreemetricsPlugin;

public class CompositeMeasure {

	private Button ok, cancel, visible;
	private OLAPMeasure formula = null;
	private DataObjectItem oriData, labelData;
	private CompositeFormula container;
	private Composite info;
	private Text format, name, desc, order, origin, label;
	private Combo type, agg;
	private Button b;
	private TabItem itemRecent, properties;

	private ListViewer listExpr;
	private Text expression;
	private HashMap<String, String> map = new HashMap<String, String>();

	private Combo dimensionForLast;

	public CompositeMeasure(final Composite parent, OLAPMeasure m, IViewSite s) {
		parent.setLayout(new GridLayout(2, false));

		formula = m;
		// tabfolder
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);

		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		TabItem itemStart = new TabItem(tabFolder, SWT.NONE, 0);
		itemStart.setText(LanguageText.CompositeMeasure_0);

		itemRecent = new TabItem(tabFolder, SWT.NONE, 1);
		itemRecent.setText(LanguageText.CompositeMeasure_1);
		itemRecent.setControl(getTabFormulaControl(tabFolder));

		properties = new TabItem(tabFolder, SWT.NONE, 2);
		properties.setText(LanguageText.CompositeMeasure_2);
		properties.setControl(getTabPropertiesControl(tabFolder));

		itemStart.setControl(getTabInfoControl(tabFolder));
		tabFolder.setSelection(0);

		ok = new Button(parent, SWT.PUSH);
		ok.setText(LanguageText.CompositeMeasure_3);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean needRefresh = false;

				if (formula == null)
					formula = new OLAPMeasure();

				formula.setDesc(desc.getText());

				if (!formula.getName().equals(name.getText()) || !formula.getType().equals(type.getText())) {
					needRefresh = true;
				}

				formula.setOrder(order.getText());
				formula.setType(type.getText());
				formula.setFormatstr(format.getText());
				formula.setName(name.getText());
				formula.setVisible(visible.getSelection());

				formula.setLastTimeDimensionName(dimensionForLast.getText());

				IStructuredSelection ss = (IStructuredSelection) listExpr.getSelection();
				if (!ss.isEmpty()) {
					String s = (String) ss.getFirstElement();
					formula.addPropExpression(s, expression.getText());
				}

				if (formula.getType().equals("physical")) { //$NON-NLS-1$
					formula.setAggregator(agg.getText());
					if (oriData != null)
						formula.setOrigin(oriData);
				} else {
					formula.setFormula(container.getFormula());
					formula.setOrigin(null);
					formula.setAggregator(""); //$NON-NLS-1$
				}
				if (formula.getType().equals("calculated") && formula.getFormula().trim().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					MessageDialog.openInformation(parent.getShell(), LanguageText.CompositeMeasure_8, LanguageText.CompositeMeasure_9);
					return;
				}

				else if (formula.getType().equals("physical") && formula.getOrigin() == null) { //$NON-NLS-1$
					MessageDialog.openInformation(parent.getShell(), LanguageText.CompositeMeasure_11, LanguageText.CompositeMeasure_12);
					return;
				}

				if (needRefresh) {
					FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
					for (OLAPCube c : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getCubes()) {
						if (c.getMes().contains(formula)) {
							FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListCube().setChanged();
							break;
						}
					}
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		cancel = new Button(parent, SWT.PUSH);
		cancel.setText(LanguageText.CompositeMeasure_13);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		fillData();
	}

	private Control getTabFormulaControl(TabFolder tabFolder) {
		container = new CompositeFormula(tabFolder, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		if (formula != null)
			container.setFormula(formula.getFormula());
		container.pack();
		return container;
	}

	private Control getTabInfoControl(final TabFolder tabFolder) {
		info = new Composite(tabFolder, SWT.NONE);
		info.setLayout(new GridLayout(3, false));

		Label lb1 = new Label(info, SWT.NONE);
		lb1.setLayoutData(new GridData());
		lb1.setText(LanguageText.CompositeMeasure_14);

		name = new Text(info, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));
		name.setText(LanguageText.CompositeMeasure_15);

		Label l2 = new Label(info, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.CompositeMeasure_16);

		desc = new Text(info, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));

		Label l3 = new Label(info, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.CompositeMeasure_17);

		type = new Combo(info, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));
		type.setItems(new String[] { "physical", "calculated" }); //$NON-NLS-1$ //$NON-NLS-2$

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
		l5.setText(LanguageText.CompositeMeasure_23);

		origin = new Text(info, SWT.BORDER);
		origin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 1, 1));

		b = new Button(info, SWT.PUSH);
		b.setText("..."); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(tabFolder.getShell());
				if (dial.open() == Dialog.OK) {
					origin.setText(dial.getItem().getName());
					oriData = dial.getItem();
				}
			}

		});

		Label l4 = new Label(info, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.CompositeMeasure_25);

		order = new Text(info, SWT.BORDER);
		order.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 2, 1));

		Label l6 = new Label(info, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(LanguageText.CompositeMeasure_26);

		label = new Text(info, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Button b1 = new Button(info, SWT.PUSH);
		b1.setText("..."); //$NON-NLS-1$
		b1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(tabFolder.getShell());
				if (dial.open() == Dialog.OK) {
					label.setText(dial.getItem().getName());
					labelData = dial.getItem();
				}
			}

		});

		Label l7 = new Label(info, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.CompositeMeasure_28);

		agg = new Combo(info, SWT.READ_ONLY);
		agg.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		if (FreemetricsPlugin.getDefault().getFAModel().getDataSources().get(0) instanceof DatasourceOda) {
			agg.setItems(new String[] { "avg", "min", "max", "sum", "count", "distinct-count", "last", "first" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		} else {
			agg.setItems(new String[] { "avg", "min", "max", "sum", "count", "distinct-count", "last" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		}
		agg.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				dimensionForLast.setEnabled(agg.getText().equals("last") || agg.getText().equals("first")); //$NON-NLS-1$ //$NON-NLS-2$

			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		l7 = new Label(info, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(LanguageText.CompositeMeasure_46);

		dimensionForLast = new Combo(info, SWT.NONE);
		dimensionForLast.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label lb2 = new Label(info, SWT.NONE);
		lb2.setLayoutData(new GridData());
		lb2.setText(LanguageText.CompositeMeasure_47);

		format = new Text(info, SWT.BORDER);
		format.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		format.setText(LanguageText.CompositeMeasure_48);

		visible = new Button(info, SWT.CHECK);
		visible.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		visible.setText(LanguageText.CompositeMeasure_49);

		info.pack();

		fillData();
		return info;
	}

	private void fillData() {
		if (formula != null) {
			name.setText(formula.getName());
			format.setText(formula.getFormatstr());
			desc.setText(formula.getDesc());
			order.setText(String.valueOf(formula.getOrder()));

			if (formula.isVisible())
				visible.setSelection(true);
			else
				visible.setSelection(false);

			if (formula.getType().equals("physical")) { //$NON-NLS-1$
				type.select(0);
				itemRecent.getControl().setEnabled(false);
				if (formula.getOrigin() != null) {
					origin.setText(formula.getOrigin().getFullName());
				}

				for (int i = 0; i < agg.getItemCount(); i++) {
					if (agg.getItem(i).equals(formula.getAggregator())) {
						agg.select(i);
						break;
					}
				}
			} else {
				type.select(1);
				itemRecent.getControl().setEnabled(true);
			}

			// expressions
			for (String s : formula.getPropertiesExpressions().keySet()) {
				map.put(s, formula.getPropertiesExpressions().get(s));
			}
			List<String> l = new ArrayList<String>();
			l.addAll(map.keySet());

			listExpr.setInput(l);

			List<String> dimensions = new ArrayList<String>();
			for (OLAPDimension d : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimensions()) {
				dimensions.add(new String(d.getName()));
			}
			dimensionForLast.setItems(dimensions.toArray(new String[dimensions.size()]));
			dimensionForLast.setEnabled(formula.getAggregator().equals("last") || formula.getAggregator().equals("first")); //$NON-NLS-1$ //$NON-NLS-2$

			if (formula.getLastTimeDimensionName() != null) {
				dimensionForLast.setText(formula.getLastTimeDimensionName());
			}

		}

	}

	private Control getTabPropertiesControl(final TabFolder tabFolder) {
		Composite c = new Composite(tabFolder, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		c.setLayout(new GridLayout(2, true));

		ToolBar toolbar = new ToolBar(c, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		ToolItem addProp = new ToolItem(toolbar, SWT.PUSH);
		addProp.setToolTipText(LanguageText.CompositeMeasure_53);
		addProp.setImage(new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/add.png")); //$NON-NLS-1$
		addProp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> l = new ArrayList<String>();
				l.add("FORMAT_STRING"); //$NON-NLS-1$
				l.add("DATATYPE"); //$NON-NLS-1$
				l.add("SOLVE_ORDER"); //$NON-NLS-1$
				DialogCombo d = new DialogCombo(tabFolder.getShell(), l);

				if (d.open() == DialogCombo.OK) {
					((List<String>) listExpr.getInput()).add(d.getValue());
					map.put(d.getValue(), ""); //$NON-NLS-1$
					formula.addPropExpression(d.getValue(), ""); //$NON-NLS-1$
					listExpr.refresh();

				}
			}

		});

		ToolItem delProp = new ToolItem(toolbar, SWT.PUSH);
		delProp.setToolTipText(LanguageText.CompositeMeasure_60);
		delProp.setImage(new Image(Display.getCurrent(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png")); //$NON-NLS-1$
		delProp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) listExpr.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				for (Object o : ss.toList()) {
					map.remove(o);
					formula.getPropertiesExpressions().remove(o);
				}
				List l = new ArrayList<String>();
				l.addAll(map.keySet());
				listExpr.setInput(l);
				expression.setText(""); //$NON-NLS-1$

			}

		});

		listExpr = new ListViewer(c, SWT.BORDER | SWT.V_SCROLL);
		listExpr.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		listExpr.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>) inputElement;

				return l.toArray(new String[l.size()]);
			}

		});
		listExpr.setLabelProvider(new LabelProvider());
		listExpr.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) listExpr.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				String key = (String) ss.getFirstElement();
				expression.setText(map.get(key));

			}

		});

		expression = new Text(c, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		expression.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		expression.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				IStructuredSelection ss = (IStructuredSelection) listExpr.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				map.put((String) ss.getFirstElement(), expression.getText());

			}

		});

		c.pack();
		return c;
	}
}
