package metadata.client.model.dialog;

import java.text.SimpleDateFormat;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.DocumentProperties;
import bpm.metadata.MetaData;

public class DialogDocumentProperties extends Dialog{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
	
	private Text author, description, creation, modification, version;
	private Text name, projectVersion, projectName;
	private Combo fmdtType;
	
	public DialogDocumentProperties(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogDocumentProperties_1);
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		
		Label l5 = new Label(c, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.DialogDocumentProperties_2);
		
		version = new Text(c, SWT.BORDER);
		version.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		version.setEnabled(false);
		
		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 3));
		l3.setText(Messages.DialogDocumentProperties_3);
		
		description = new Text(c, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogDocumentProperties_4);
		
		author = new Text(c, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		
		Label l4 = new Label(c, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.DialogDocumentProperties_5);
		
		creation = new Text(c, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);
		
		Label l6 = new Label(c, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.DialogDocumentProperties_6);
		
		modification = new Text(c, SWT.BORDER);
		modification.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		modification.setEnabled(false);
		
		Label l7 = new Label(c, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.DialogDocumentProperties_7);
		
		fmdtType = new Combo(c, SWT.READ_ONLY);
		fmdtType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fmdtType.setItems(DocumentProperties.FMDT_TYPE_NAME);
		
		Label l8 = new Label(c, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText(Messages.DialogDocumentProperties_8);
		
		projectName = new Text(c, SWT.BORDER);
		projectName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l9 = new Label(c, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l9.setText(Messages.DialogDocumentProperties_9);
		
		projectVersion = new Text(c, SWT.BORDER);
		projectVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		return c;
	}
	
	
	private void fillData(){
		MetaData model = Activator.getDefault().getModel();
		DocumentProperties p = model.getProperties();
		
		author.setText(p.getAuthor());
		name.setText(p.getName());
		description.setText(p.getDescription());
		modification.setText(sdf.format(p.getModification()));
		creation.setText(sdf.format(p.getCreation()));
		version.setText(p.getVersion());
		fmdtType.select(p.getFmdtType());
		projectName.setText(p.getProjectName());
		projectVersion.setText(p.getProjectVersion());
		
	}


	@Override
	protected void okPressed() {
		MetaData model = Activator.getDefault().getModel();
		DocumentProperties p = model.getProperties();
		
		p.setAuthor(author.getText());
		p.setDescription(description.getText());
		p.setName(name.getText());
		p.setFmdtType(fmdtType.getSelectionIndex());
		p.setProjectName(projectName.getText());
		p.setProjectVersion(projectVersion.getText());
		super.okPressed();
	}


	@Override
	protected void initializeBounds() {
		fillData();
		getShell().setSize(400, 600);
		getShell().setText(Messages.DialogDocumentProperties_10);
	}
	
	

}

