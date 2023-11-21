package bpm.gateway.ui.dialogs.database;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.icons.IconsNames;

public class DataBaseColumns extends Composite {

	private Combo type;
	private List<String> typeName;
	private Text textColumn;
	private Button btnPk, btnNn, btnAi;
	private Text textPrecision;

	private String driverName;
	private Display display;
	private DataBaseColumns selectedComposite;

	public DataBaseColumns(Composite parent, int style, String driverName) {
		super(parent, style);

		display = Display.getCurrent();
		setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		setLayout(new GridLayout(7, false));

		this.driverName = driverName;

		fillListType(this.driverName);

		textColumn = new Text(this, SWT.BORDER);
		GridData gd_textColumn = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textColumn.minimumWidth = 150;
		textColumn.setLayoutData(gd_textColumn);

		type = new Combo(this, SWT.NONE);
		GridData gd_type = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_type.minimumWidth = 150;
		type.setLayoutData(gd_type);
		type.setItems(typeName.toArray(new String[typeName.size()]));
		type.setVisibleItemCount(15);

		textPrecision = new Text(this, SWT.BORDER);
		GridData gd_Precision = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_Precision.minimumWidth = 55;
		textPrecision.setLayoutData(gd_Precision);

		btnPk = new Button(this, SWT.CHECK);

		GridData gd_btnPk = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPk.widthHint = 20;
		btnPk.setLayoutData(gd_btnPk);
		btnPk.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		btnNn = new Button(this, SWT.CHECK);

		GridData gd_btnNn = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNn.widthHint = 20;
		btnNn.setLayoutData(gd_btnNn);
		btnNn.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		btnAi = new Button(this, SWT.CHECK);

		GridData gd_btnAi = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAi.widthHint = 20;
		btnAi.setLayoutData(gd_btnAi);
		btnAi.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		Button btndel = new Button(this, SWT.PUSH);
		btndel.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.del_16));
		btndel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				dispose();
				getParent().getParent().layout();
			}

		});

	}

	public DataBaseColumns(Composite parent, int style, String clmnName, String selectedType, String driverName) {
		this(parent, style, driverName);

		setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		textColumn.setText(clmnName);

		type.setText(getType(selectedType, driverName));

	}

	private String getType(String selectedType, String driverName) {
		FileInputStream fis;
		Properties typeProperties = new Properties();

		try {
			fis = new FileInputStream("resources/bpm_sql_data_type.properties"); //$NON-NLS-1$
			typeProperties.load(fis);
		} catch(Exception e) {
			e.printStackTrace();
		}
		String name = null;
		for(Object o : typeProperties.keySet()) {
			if(((String) o).contains(driverName.toLowerCase()) && ((String) o).contains(selectedType) && ((String) o).contains("convers")) { //$NON-NLS-1$

				name = typeProperties.getProperty((String) o);
				break;
			}

		}
		return name;
	}

	public String getSql() {
		if(textPrecision.getText().isEmpty()) {
			return textColumn.getText() + " " + type.getText() + getPK() + getAI() + getNN(); //$NON-NLS-1$
		}
		else {
			return textColumn.getText() + " " + type.getText() + "(" + textPrecision.getText() + ")" + getPK() + getAI() + getNN(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

	}

	private String getNN() {
		if(btnNn.getSelection() == true) {
			return " NOT NULL "; //$NON-NLS-1$
		}
		else {
			return ""; //$NON-NLS-1$
		}
	}

	private String getAI() {
		if(btnAi.getSelection() == true) {
			return " AUTO_INCREMENT "; //$NON-NLS-1$
		}
		else {
			return ""; //$NON-NLS-1$
		}
	}

	private String getPK() {
		if(btnPk.getSelection() == true) {
			return " PRIMARY KEY "; //$NON-NLS-1$
		}
		else {
			return " "; //$NON-NLS-1$
		}

	}

	private void fillListType(String driverName) {
		FileInputStream fis;
		Properties typeProperties = new LinkedProperties();

		try {
			fis = new FileInputStream("resources/bpm_sql_data_type.properties"); //$NON-NLS-1$
			typeProperties.load(fis);
		} catch(Exception e) {
			e.printStackTrace();
		}
		typeName = new ArrayList<String>();
		for(Object o : typeProperties.keySet()) {
			if(((String) o).contains(driverName.toLowerCase()) && !((String) o).contains("convers")) { //$NON-NLS-1$
				String name = (String) o;
				typeName.add(typeProperties.getProperty(name));

			}
		}
	}

	public DataBaseColumns getSelectedComposite() {
		return selectedComposite;
	}

}
