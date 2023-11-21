package bpm.sqldesigner.ui.wizard.composite;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.i18N.Messages;

public class ColumnCreateComposite extends Composite {

	private Text textName;
	private Combo comboType;
	private ArrayList<String> types;
	private DatabaseCluster cluster;
	private Button checkSize;
	private Text textSize;
	private Button checkPK;
	private Button checkUnsigned;
	private Button checkNullable;
	private Button checkDefault;
	private Text textDefault;

	public ColumnCreateComposite(Composite parent, int style,
			DatabaseCluster cluster) {
		super(parent, style);
		this.cluster = cluster;
		buildContent();
	}

	private void buildContent() {
		this.setLayout(new GridLayout(1, false));

		Group groupMain = new Group(this, SWT.NONE);
		groupMain.setLayout(new GridLayout(3, false));
		groupMain.setLayoutData(new GridData(GridData.BEGINNING));

		groupMain.setText(Messages.ColumnCreateComposite_0);

		/***********************************************************************
		 * Name
		 **********************************************************************/
		Label label = new Label(groupMain, SWT.NONE);
		label.setText(Messages.ColumnCreateComposite_1);

		textName = new Text(groupMain, SWT.BORDER);

		textName.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 2, 1));

		/***********************************************************************
		 * Type
		 **********************************************************************/
		Composite compoType = new Composite(groupMain, SWT.NONE);
		compoType.setLayout(new GridLayout(5, false));
		compoType.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelType = new Label(compoType, SWT.NONE);
		labelType.setText(Messages.ColumnCreateComposite_2);

		comboType = new Combo(compoType, SWT.BORDER);

		comboType.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 1, 1));

		types = new ArrayList<String>();

		for (String typeName : cluster.getTypesLists().getTypes().keySet()) {
			if (!typeName.contains("UNSIGNED")) //$NON-NLS-1$
				types.add(typeName);
		}

		Collections.sort(types);

		for (String typeName : types)
			comboType.add(typeName);

		comboType.select(0);

		/***********************************************************************
		 * Size
		 **********************************************************************/

		checkSize = new Button(compoType, SWT.CHECK);

		Label labelSize = new Label(compoType, SWT.NONE);
		labelSize.setText(Messages.ColumnCreateComposite_4);

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

		/***********************************************************************
		 * Primary Key
		 **********************************************************************/
		Composite compoPK = new Composite(groupMain, SWT.NONE);
		compoPK.setLayout(new GridLayout(2, false));
		compoPK.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelPK = new Label(compoPK, SWT.NONE);
		labelPK.setText(Messages.ColumnCreateComposite_5);

		checkPK = new Button(compoPK, SWT.CHECK);

		/***********************************************************************
		 * Unsigned
		 **********************************************************************/
		Composite compoUnsigned = new Composite(groupMain, SWT.NONE);
		compoUnsigned.setLayout(new GridLayout(2, false));
		compoUnsigned.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelUnsigned = new Label(compoUnsigned, SWT.NONE);
		labelUnsigned.setText(Messages.ColumnCreateComposite_6);

		checkUnsigned = new Button(compoUnsigned, SWT.CHECK);

		/***********************************************************************
		 * Nullable
		 **********************************************************************/
		Composite compoNullable = new Composite(groupMain, SWT.NONE);
		compoNullable.setLayout(new GridLayout(2, false));
		compoNullable.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		Label labelNullable = new Label(compoNullable, SWT.NONE);
		labelNullable.setText(Messages.ColumnCreateComposite_7);

		checkNullable = new Button(compoNullable, SWT.CHECK);
		checkNullable.setSelection(true);

		/***********************************************************************
		 * Default
		 **********************************************************************/
		Composite compoDefault = new Composite(groupMain, SWT.NONE);
		compoDefault.setLayout(new GridLayout(3, false));
		compoDefault.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.BEGINNING, false, false, 2, 1));

		checkDefault = new Button(compoDefault, SWT.CHECK);

		Label labelDefault = new Label(compoDefault, SWT.NONE);
		labelDefault.setText(Messages.ColumnCreateComposite_8);

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
	}

	public Text getTextName() {
		return textName;
	}

	public Combo getComboType() {
		return comboType;
	}

	public Button getCheckSize() {
		return checkSize;
	}

	public Text getTextSize() {
		return textSize;
	}

	public Button getCheckPK() {
		return checkPK;
	}

	public Button getCheckUnsigned() {
		return checkUnsigned;
	}

	public Button getCheckNullable() {
		return checkNullable;
	}

	public Button getCheckDefault() {
		return checkDefault;
	}

	public Text getTextDefault() {
		return textDefault;
	}

	public void setColumn(Column column) {
		textName.setText(column.getName());
		comboType.select(getTypeItemIndex(column.getType().getName()));

		if (column.needsSize()) {
			checkSize.setSelection(true);
			textSize.setEnabled(true);
			textSize.setText(String.valueOf(column.getSize()));
		}
		checkPK.setSelection(column.isPrimaryKey());
		checkUnsigned.setSelection(column.isUnsigned());
		checkNullable.setSelection(column.isNullable());
		if (column.gotDefault()) {
			textDefault.setEnabled(true);
			checkDefault.setSelection(true);
			textDefault.setText(column.getDefaultValue());
		}
	}

	private int getTypeItemIndex(String string) {
		boolean found = false;

		String[] items = comboType.getItems();

		int i = 0;
		while (i < items.length && !found) {
			if (items[i].equals(string))
				found = true;
			else
				i++;
		}

		if (found)
			return i;
		else
			return -1;

	}
}
