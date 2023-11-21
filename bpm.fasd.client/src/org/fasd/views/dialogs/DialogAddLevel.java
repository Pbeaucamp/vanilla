package org.fasd.views.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPRelation;
import org.fasd.views.composites.DialogPickCol;
import org.freeolap.FreemetricsPlugin;

public class DialogAddLevel extends Dialog {
	private Text name, desc;
	private Button b4;
	private Label l8, l9, l2;
	private Text closure, label, descit, nullParent;
	private Text item, parentColumn;
	private DataObjectItem it, labIt, descIt;
	private OLAPLevel level = new OLAPLevel();

	@Override
	protected void okPressed() {
		level.setName(name.getText());
		level.setDesc(desc.getText());
		level.setItem(it);
		level.setLabel(labIt);
		level.setItDesc(descIt);

		super.okPressed();
	}

	public DialogAddLevel(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite par) {
		par.setLayout(new GridLayout());
		Composite parent = new Composite(par, SWT.NONE);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout(3, false));

		Label lb1 = new Label(parent, SWT.NONE);
		lb1.setLayoutData(new GridData());
		lb1.setText(LanguageText.DialogAddLevel_Name_);

		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.setText(LanguageText.DialogAddLevel_DefaultLvl);
		name.selectAll();

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(LanguageText.DialogAddLevel_Descr_);

		desc = new Text(parent, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.DialogAddLevel_Col_Item_);

		item = new Text(parent, SWT.BORDER);
		item.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Button b = new Button(parent, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(DialogAddLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					item.setText(dial.getItem().getName());
					it = dial.getItem();
					boolean clos = false;

					for (OLAPRelation r : FreemetricsPlugin.getDefault().getFAModel().getRelations()) {
						if (r.isUsingItem(it) && r.isReflexive()) {
							clos = true;
							break;
						}
					}

					if (clos) {
						l8.setVisible(true);
						closure.setVisible(true);
						b4.setVisible(true);
						l9.setVisible(true);
						parentColumn.setVisible(true);
						l2.setVisible(true);
						nullParent.setVisible(true);

					} else {
						l8.setVisible(false);
						closure.setVisible(false);
						l9.setVisible(false);
						parentColumn.setVisible(false);
						b4.setVisible(false);
						l2.setVisible(false);
						nullParent.setVisible(false);

					}
				}
			}

		});

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.DialogAddLevel_Label_Item_);

		label = new Text(parent, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Button b1 = new Button(parent, SWT.PUSH);
		b1.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b1.setText("..."); //$NON-NLS-1$
		b1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(DialogAddLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					label.setText(dial.getItem().getFullName());
					labIt = dial.getItem();
				}
			}

		});

		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.DialogAddLevel_Desc_Item_);

		descit = new Text(parent, SWT.BORDER);
		descit.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Button b2 = new Button(parent, SWT.PUSH);
		b2.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		b2.setText("..."); //$NON-NLS-1$
		b2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(DialogAddLevel.this.getShell());
				if (dial.open() == DialogPickCol.OK) {
					descit.setText(dial.getItem().getFullName());
					descIt = dial.getItem();
				}
			}

		});

		l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(LanguageText.DialogAddLevel_Null_Parent_);
		l2.setVisible(false);

		nullParent = new Text(parent, SWT.BORDER);
		nullParent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		nullParent.setVisible(false);

		l8 = new Label(parent, SWT.NONE);
		l8.setLayoutData(new GridData());
		l8.setText(LanguageText.DialogAddLevel_Close_DataObj);
		l8.setVisible(false);

		closure = new Text(parent, SWT.BORDER);
		closure.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		closure.setVisible(false);
		closure.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				DialogClosure dial = new DialogClosure(DialogAddLevel.this.getShell(), level);
				if (dial.open() == DialogClosure.OK) {
					closure.setText(level.getClosureTable().getName());
				}
			}

		});

		return parent;
	}

	public OLAPLevel getLevel() {
		return level;
	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogAddLevel_new);
		super.initializeBounds();
		this.getShell().setSize(450, 350);
	}

}
