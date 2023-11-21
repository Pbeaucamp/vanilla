package bpm.vanilla.server.ui.repository.wizards;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RepositoryAddItemPage extends WizardPage{

	private Text itemName;
	private Text author;
	private Text description;
	private Text version;
	private Combo groups;
	
	public static final String P_NAME = "name";
	public static final String P_AUTHOR = "author";
	public static final String P_DESCRIPTION = "desc";
	public static final String P_VERSION = "version";
	public static final String P_GROUP = "group";
	
	
	protected RepositoryAddItemPage(String pageName) {
		super(pageName);

	}
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Name");
		
		itemName = new Text(main, SWT.BORDER);
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		itemName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 3));
		l.setText("Description");
		
		description = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 3));

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Version");
		
		version = new Text(main, SWT.BORDER);
		version.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Author");
		
		author = new Text(main, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Author");
		
		groups = new Combo(main, SWT.READ_ONLY);
		groups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}
		});
		
		setControl(main);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return groups.getSelectionIndex() != -1 && ! "".equals(itemName.getText());
	}
	
	public Properties getProperties(){
		Properties p = new Properties();
		p.setProperty(P_DESCRIPTION, description.getText());
		p.setProperty(P_AUTHOR, author.getText());
		p.setProperty(P_NAME, itemName.getText());
		p.setProperty(P_VERSION, version.getText());
		p.setProperty(P_GROUP, groups.getText());
		return p;
	}

}
