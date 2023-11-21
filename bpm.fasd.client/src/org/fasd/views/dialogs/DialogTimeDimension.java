package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.exceptions.HierarchyException;

public class DialogTimeDimension extends Dialog {

	private Combo yearC, monthC, quarterC, dayC;
	private Button year, month, quarter, day;
	private List<String> cols = new ArrayList<String>();
	private Text yearT, monthT, quarterT, dayT, table;
	private OLAPHierarchy hiera;

	private DataObject dataObject;

	public DialogTimeDimension(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(LanguageText.DialogTimeDimension_Dim_table);

		table = new Text(container, SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectDataObject dial = new DialogSelectDataObject(getShell(), null);
				if (dial.open() == Dialog.OK) {
					dataObject = dial.getDataObject();
					table.setText(dataObject.getName());
					cols.clear();

					for (DataObjectItem i : dataObject.getColumns())
						cols.add(i.getName());

					yearC.setItems(cols.toArray(new String[cols.size()]));
					quarterC.setItems(cols.toArray(new String[cols.size()]));
					monthC.setItems(cols.toArray(new String[cols.size()]));
					dayC.setItems(cols.toArray(new String[cols.size()]));
				}
			}
		});

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogTimeDimension_Lvl_Type);

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l2.setText(LanguageText.DialogTimeDimension_Col);

		year = new Button(container, SWT.CHECK);
		year.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		year.setText(LanguageText.DialogTimeDimension_Year);
		year.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (year.getSelection()) {
					yearC.setEnabled(true);
					yearT.setEnabled(true);
				} else {
					yearC.setEnabled(false);
					yearT.setEnabled(false);
				}
			}

		});

		yearC = new Combo(container, SWT.READ_ONLY);
		yearC.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		yearC.setEnabled(false);

		quarter = new Button(container, SWT.CHECK);
		quarter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		quarter.setText(LanguageText.DialogTimeDimension_Quarter);
		quarter.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (quarter.getSelection()) {
					quarterC.setEnabled(true);
					quarterT.setEnabled(true);
				} else {
					quarterC.setEnabled(false);
					quarterT.setEnabled(false);
				}
			}

		});

		quarterC = new Combo(container, SWT.READ_ONLY);
		quarterC.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		quarterC.setEnabled(false);

		month = new Button(container, SWT.CHECK);
		month.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		month.setText(LanguageText.DialogTimeDimension_Month);
		month.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (month.getSelection()) {
					monthC.setEnabled(true);
					monthT.setEnabled(true);
				} else {
					monthC.setEnabled(false);
					monthT.setEnabled(false);
				}
			}

		});

		monthC = new Combo(container, SWT.READ_ONLY);
		monthC.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		monthC.setEnabled(false);
		//		

		day = new Button(container, SWT.CHECK);
		day.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		day.setText(LanguageText.DialogTimeDimension_Day);
		day.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (day.getSelection()) {
					dayC.setEnabled(true);
					dayT.setEnabled(true);
				} else {
					dayC.setEnabled(false);
					dayT.setEnabled(false);
				}
			}

		});

		dayC = new Combo(container, SWT.READ_ONLY);
		dayC.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		dayC.setEnabled(false);
		//		

		return parent;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(LanguageText.DialogTimeDimension_Time_Dim);
		getShell().setSize(300, 400);
	}

	@Override
	protected void okPressed() {
		hiera = new OLAPHierarchy();
		hiera.setAllMember("All Times"); //$NON-NLS-1$

		List<OLAPLevel> list = new ArrayList<OLAPLevel>();

		if (year.getSelection()) {
			OLAPLevel l = new OLAPLevel();
			l.setName("Year"); //$NON-NLS-1$
			l.setItem(dataObject.findItemNamed(yearC.getText()));
			l.setLevelType("TimeYears"); //$NON-NLS-1$
			list.add(l);
		}

		if (quarter.getSelection()) {
			OLAPLevel l = new OLAPLevel();
			l.setName("Quarter"); //$NON-NLS-1$
			l.setItem(dataObject.findItemNamed(quarterC.getText()));
			l.setLevelType("TimeQuarters"); //$NON-NLS-1$
			list.add(l);
		}

		if (month.getSelection()) {
			OLAPLevel l = new OLAPLevel();
			l.setName("Month"); //$NON-NLS-1$
			l.setItem(dataObject.findItemNamed(monthC.getText()));
			l.setLevelType("TimeMonths"); //$NON-NLS-1$
			list.add(l);
		}
		if (day.getSelection()) {
			OLAPLevel l = new OLAPLevel();
			l.setName("Day"); //$NON-NLS-1$
			l.setItem(dataObject.findItemNamed(dayC.getText()));
			l.setLevelType("TimeDays"); //$NON-NLS-1$
			list.add(l);
		}

		for (OLAPLevel l : list) {
			try {
				hiera.addLevel(l);
			} catch (HierarchyException e) {
				e.printStackTrace();
			}
		}

		super.okPressed();
	}

	public OLAPHierarchy getHierarchy() {
		return hiera;
	}
}
