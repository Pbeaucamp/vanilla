package bpm.oda.driver.reader.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.oda.driver.reader.Activator;

public class MainDataSetPage extends WizardPage{
	
	private Combo comboType;
	private Text fieldName;

	public MainDataSetPage(String pageName) {
		super(pageName);
		
		setTitle("New Data Set");
		setMessage("Define the data set's name, source and type.");
		
	}

	public void createControl(Composite parent) {
		setControl( createPageControl(parent));
		
		getShell().setSize(500, 500);
	}
	
	private Control createPageControl(Composite pParent) {
		
		Composite parent = new Composite(pParent, SWT.NONE);
		

		
	//Data Set type
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		l.setText("Data Set type :");
		
		comboType = new Combo(parent, SWT.READ_ONLY);
		comboType.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
	//Data set name
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		l.setText("Data Set Name :");
		
		fieldName = new Text(parent, SWT.BORDER);
		fieldName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		fieldName.setText("Data Set " + (Activator.getInstance().getListDataSet().size() + 1));




		
		
		return parent;
	}

}
