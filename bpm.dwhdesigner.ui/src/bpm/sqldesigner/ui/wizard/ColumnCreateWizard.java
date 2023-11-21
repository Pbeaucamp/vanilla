package bpm.sqldesigner.ui.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.wizard.composite.ColumnCreateComposite;

public class ColumnCreateWizard extends Wizard {

	public class ColumnCreatePage extends WizardPage {
		private Text textName;
		private Combo comboType;
		private DatabaseCluster cluster;
		private Button checkSize;
		private Text textSize;
		private Button checkPK;
		private Button checkUnsigned;
		private Button checkNullable;
		private Button checkDefault;
		private Text textDefault;
		private Column column = null;

		public ColumnCreatePage(String pageName, DatabaseCluster cluster,
				Column column) {
			super(pageName);
			setTitle(Messages.ColumnCreateWizard_0);
			setDescription(Messages.ColumnCreateWizard_1);
			this.cluster = cluster;
			this.column = column;
		}

		
		public void createControl(Composite parent) {
			ColumnCreateComposite compoMain = new ColumnCreateComposite(parent,
					SWT.NONE, cluster);
			if (column != null)
				compoMain.setColumn(column);
			compoMain.setLayoutData(new GridData(GridData.BEGINNING));

			textName = compoMain.getTextName();
			comboType = compoMain.getComboType();
			checkSize = compoMain.getCheckSize();
			textSize = compoMain.getTextSize();
			checkPK = compoMain.getCheckPK();
			checkUnsigned = compoMain.getCheckUnsigned();
			checkNullable = compoMain.getCheckNullable();
			checkDefault = compoMain.getCheckDefault();
			textDefault = compoMain.getTextDefault();

			setControl(compoMain);

		}
	}

	private ColumnCreatePage columnCreatePage;
	private String comboType;
	private boolean checkSize;
	private String textSize;
	private boolean checkDefault;
	private String textDefault;
	private boolean checkNullable;
	private boolean checkUnsigned;
	private boolean checkPK;
	private String name;

	public ColumnCreateWizard(DatabaseCluster cluster) {
		super();
		columnCreatePage = new ColumnCreatePage("OpenWorkspace", cluster, null); //$NON-NLS-1$
		addPage(columnCreatePage);
	}

	public ColumnCreateWizard(DatabaseCluster cluster, Column column) {
		super();
		columnCreatePage = new ColumnCreatePage("OpenWorkspace", cluster, //$NON-NLS-1$
				column);
		addPage(columnCreatePage);
	}

	
	public boolean performFinish() {
		name = columnCreatePage.textName.getText();
		comboType = columnCreatePage.comboType.getText();
		checkSize = columnCreatePage.checkSize.getSelection();
		checkUnsigned = columnCreatePage.checkUnsigned.getSelection();
		checkNullable = columnCreatePage.checkNullable.getSelection();
		checkDefault = columnCreatePage.checkDefault.getSelection();
		checkPK = columnCreatePage.checkPK.getSelection();
		textSize = columnCreatePage.textSize.getText();
		textDefault = columnCreatePage.textDefault.getText();

		if (comboType.equals("") || name.equals("")) //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		return true;
	}

	public String getComboType() {
		return comboType;
	}

	public boolean getCheckSize() {
		return checkSize;
	}

	public String getTextSize() {
		return textSize;
	}

	public boolean getCheckDefault() {
		return checkDefault;
	}

	public String getTextDefault() {
		return textDefault;
	}

	public boolean getCheckNullable() {
		return checkNullable;
	}

	public boolean getCheckUnsigned() {
		return checkUnsigned;
	}

	public boolean getCheckPK() {
		return checkPK;
	}

	public String getName() {
		return name;
	}

}
