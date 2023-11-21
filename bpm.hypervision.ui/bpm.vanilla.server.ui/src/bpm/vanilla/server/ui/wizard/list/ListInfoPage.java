package bpm.vanilla.server.ui.wizard.list;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ListInfoPage extends WizardPage{

	private Text listName;
	
	public ListInfoPage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());
		
		Label l = new Label(main, SWT.NONE);
		l.setText("List Name");
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		listName = new Text(main, SWT.BORDER);
		listName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		setControl(main);
		
	}
	
	public String getListName(){
		return listName.getText();
	}

}
