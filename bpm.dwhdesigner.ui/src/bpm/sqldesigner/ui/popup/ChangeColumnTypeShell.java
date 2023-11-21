package bpm.sqldesigner.ui.popup;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.ui.i18N.Messages;

public class ChangeColumnTypeShell {

	private CanValidate cmd;
	private Shell shell;
	private ArrayList<String> types;
	private Text textDefault;
	private Button checkDefault;
	private Button checkNullable;
	private Button checkUnsigned;
	private Text textSize;
	private Button checkSize;
	private Combo comboType;

	public ChangeColumnTypeShell(Display parent, int style,
			CanValidate command, Column column) {
		shell = new Shell(parent, style);
		initPopup(command, column);
	}

	public void initPopup(CanValidate command, Column column) {
		cmd = command;

		shell.setLayout(new GridLayout(1, false));
		shell.setLayoutData(new GridData(GridData.BEGINNING));

		Group compoMain = new Group(shell, SWT.NONE);
		compoMain.setLayout(new GridLayout(3, false));
		compoMain.setLayoutData(new GridData(GridData.BEGINNING));

		compoMain.setText(Messages.ChangeColumnTypeShell_0);

		KeyListener enterListener = new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR)
					cmd.validate();
			}

			public void keyReleased(KeyEvent e) {
			}

		};

		/***********************************************************************
		 * Type
		 **********************************************************************/
		Composite compoType = new Composite(compoMain, SWT.NONE);
		compoType.setLayout(new GridLayout(5, false));
		compoType.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelType = new Label(compoType, SWT.NONE);
		labelType.setText(Messages.ChangeColumnTypeShell_1);

		comboType = new Combo(compoType, SWT.BORDER);

		comboType.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 1, 1));
		comboType.addKeyListener(enterListener);

		types = new ArrayList<String>();

		for (String typeName : column.getCluster().getTypesLists().getTypes()
				.keySet()) {
			if (!typeName.contains("UNSIGNED")) //$NON-NLS-1$
				types.add(typeName);
		}

		Collections.sort(types);

		int i = 0;
		for (String typeName : types) {
			comboType.add(typeName);
			if (typeName.equals(column.getType().getName()))
				comboType.select(i);
			i++;
		}

		/***********************************************************************
		 * Size
		 **********************************************************************/

		checkSize = new Button(compoType, SWT.CHECK);

		Label labelSize = new Label(compoType, SWT.NONE);
		labelSize.setText(Messages.ChangeColumnTypeShell_3);

		textSize = new Text(compoType, SWT.BORDER);

		checkSize.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				textSize.setEnabled(checkSize.getSelection());
			}
		});

		textSize.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 1, 1));
		textSize.setEnabled(false);

		if (column.needsSize()) {
			checkSize.setSelection(true);
			textSize.setText(String.valueOf(column.getSize()));
			textSize.setEnabled(true);
		}

		/***********************************************************************
		 * Unsigned
		 **********************************************************************/
		Composite compoUnsigned = new Composite(compoMain, SWT.NONE);
		compoUnsigned.setLayout(new GridLayout(2, false));
		compoUnsigned.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelUnsigned = new Label(compoUnsigned, SWT.NONE);
		labelUnsigned.setText(Messages.ChangeColumnTypeShell_4);

		checkUnsigned = new Button(compoUnsigned, SWT.CHECK);

		if (column.isUnsigned()) {
			checkUnsigned.setSelection(true);
		}

		/***********************************************************************
		 * Nullable
		 **********************************************************************/
		Composite compoNullable = new Composite(compoMain, SWT.NONE);
		compoNullable.setLayout(new GridLayout(2, false));
		compoNullable.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelNullable = new Label(compoNullable, SWT.NONE);
		labelNullable.setText(Messages.ChangeColumnTypeShell_5);

		checkNullable = new Button(compoNullable, SWT.CHECK);

		if (column.isNullable()) {
			checkNullable.setSelection(true);
		}

		/***********************************************************************
		 * Default
		 **********************************************************************/
		Composite compoDefault = new Composite(compoMain, SWT.NONE);
		compoDefault.setLayout(new GridLayout(3, false));
		compoDefault.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		checkDefault = new Button(compoDefault, SWT.CHECK);

		Label labelDefault = new Label(compoDefault, SWT.NONE);
		labelDefault.setText(Messages.ChangeColumnTypeShell_6);

		textDefault = new Text(compoDefault, SWT.BORDER);

		checkDefault.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				textDefault.setEnabled(checkDefault.getSelection());
			}
		});

		textDefault.setLayoutData(new GridData(GridData.FILL,
				GridData.BEGINNING, true, false, 1, 1));
		textDefault.setEnabled(false);

		if (column.gotDefault()) {
			checkDefault.setSelection(true);
			textDefault.setText(column.getDefaultValue());
			textDefault.setEnabled(true);
		}

		/***********************************************************************
		 * Button OK
		 **********************************************************************/
		Button button = new Button(compoMain, SWT.PUSH);
		button.setText(Messages.ChangeColumnTypeShell_7);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gd.horizontalSpan = 3;
		button.setLayoutData(gd);
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				cmd.validate();
			}
		});

	}

	public Shell getShell() {
		return shell;
	}

	public Text getTextDefault() {
		return textDefault;
	}

	public Button getCheckDefault() {
		return checkDefault;
	}

	public Button getCheckNullable() {
		return checkNullable;
	}

	public Button getCheckUnsigned() {
		return checkUnsigned;
	}

	public Text getTextSize() {
		return textSize;
	}

	public Button getCheckSize() {
		return checkSize;
	}

	public Combo getComboType() {
		return comboType;
	}

}
