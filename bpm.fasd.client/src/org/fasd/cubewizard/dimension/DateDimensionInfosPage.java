package org.fasd.cubewizard.dimension;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPLevel;
import org.fasd.views.composites.DialogPickCol;

/**
 * The information page dimension name hierarchy name column column type column
 * pattern
 * 
 * @author Marc Lanquetin
 * 
 */
public class DateDimensionInfosPage extends WizardPage {

	private Text txtDimName, txtHieraName, txtPattern, txtColumn;
	private Button btnColumn;
	private Combo cbColumnType;
	private DataObjectItem item;

	private CompositeHelpPattern helpPat;

	private OLAPDimension dimension;

	public DateDimensionInfosPage(String pageName) {
		super(pageName);
	}

	public DateDimensionInfosPage(String pageName, OLAPDimension dimension) {
		super(pageName);
		this.dimension = dimension;
	}

	public void createControl(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblDimName = new Label(mainComposite, SWT.NONE);
		lblDimName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblDimName.setText(LanguageText.DateDimensionInfosPage_0);

		txtDimName = new Text(mainComposite, SWT.BORDER);
		txtDimName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Label lblhieraName = new Label(mainComposite, SWT.NONE);
		lblhieraName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblhieraName.setText(LanguageText.DateDimensionInfosPage_1);

		txtHieraName = new Text(mainComposite, SWT.BORDER);
		txtHieraName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Label lblColumn = new Label(mainComposite, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblColumn.setText(LanguageText.DateDimensionInfosPage_2);

		txtColumn = new Text(mainComposite, SWT.BORDER);
		txtColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		btnColumn = new Button(mainComposite, SWT.PUSH);
		btnColumn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnColumn.setText("..."); //$NON-NLS-1$

		Label lblColumnType = new Label(mainComposite, SWT.NONE);
		lblColumnType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblColumnType.setText(LanguageText.DateDimensionInfosPage_4);

		cbColumnType = new Combo(mainComposite, SWT.READ_ONLY);
		cbColumnType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		cbColumnType.setItems(new String[] { "Date", "Long", "String" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Label lblPattern = new Label(mainComposite, SWT.NONE);
		lblPattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblPattern.setText("Pattern"); //$NON-NLS-1$

		txtPattern = new Text(mainComposite, SWT.BORDER);
		txtPattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtPattern.setEnabled(false);

		Button btnPattern = new Button(mainComposite, SWT.PUSH);
		btnPattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnPattern.setToolTipText(LanguageText.DateDimensionInfosPage_9);
		btnPattern.setText("?"); //$NON-NLS-1$
		btnPattern.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				helpPat.setVisible(!helpPat.isVisible());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		helpPat = new CompositeHelpPattern(mainComposite, SWT.NONE);
		helpPat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		helpPat.setVisible(false);

		setControl(mainComposite);

		setPageComplete(true);

		preFillData(dimension);

		createListeners();
	}

	private void createListeners() {
		txtDimName.addModifyListener(new ModificationListener(this));

		txtHieraName.addModifyListener(new ModificationListener(this));

		txtColumn.addModifyListener(new ModificationListener(this));

		btnColumn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(DateDimensionInfosPage.this.getShell());
				if (dial.open() == Dialog.OK) {
					txtColumn.setText(dial.getItem().getName());
					item = dial.getItem();
					DateDimensionInfosPage.this.getContainer().updateButtons();
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		cbColumnType.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (cbColumnType.getText().equals("String")) { //$NON-NLS-1$
					txtPattern.setEnabled(true);
				} else {
					txtPattern.setEnabled(false);
				}
				DateDimensionInfosPage.this.getContainer().updateButtons();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		txtPattern.addModifyListener(new ModificationListener(this));
	}

	@Override
	public boolean canFlipToNextPage() {
		if (txtDimName.getText() != null && !txtDimName.getText().equals("") //$NON-NLS-1$
				&& txtColumn.getText() != null && !txtColumn.getText().equals("") //$NON-NLS-1$
				&& cbColumnType.getText() != null && !cbColumnType.getText().equals("")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public String getDimensionName() {
		return txtDimName.getText();
	}

	public String getHierarchyName() {
		return txtHieraName.getText();
	}

	public String getColumnType() {
		return cbColumnType.getText();
	}

	public DataObjectItem getColumn() {
		return item;
	}

	public String getPattern() {
		return txtPattern.getText();
	}

	public void preFillData(OLAPDimension dimension) {
		if (dimension != null) {
			txtDimName.setText(dimension.getName());
			txtHieraName.setText(dimension.getHierarchies().get(0).getName() != null ? dimension.getHierarchies().get(0).getName() : ""); //$NON-NLS-1$

			for (OLAPLevel lvl : dimension.getHierarchies().get(0).getLevels()) {
				if (lvl.getItem() != null) {
					item = lvl.getItem();
					txtColumn.setText(lvl.getItem().getName());
					cbColumnType.setText(lvl.getDateColumnType());
					txtPattern.setText(lvl.getDateColumnPattern() != null ? lvl.getDateColumnPattern() : ""); //$NON-NLS-1$
					break;
				}
			}
		}

	}

}
