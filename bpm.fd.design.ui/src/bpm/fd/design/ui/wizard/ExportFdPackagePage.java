package bpm.fd.design.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportFdPackagePage extends WizardPage {

	private FileDialog destinationFile;
	private Text filePath;
	private Button btnFile;
	
	public ExportFdPackagePage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(3, false));
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lbl = new Label(root, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
		lbl.setText("Destination path");
		
		filePath = new Text(root, SWT.BORDER);
		filePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		btnFile = new Button(root, SWT.PUSH);
		btnFile.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		btnFile.setText("...");
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				destinationFile = new FileDialog(getShell());
				destinationFile.setFilterExtensions(new String[]{".fdpackage"});
				String file = destinationFile.open();
				if(file != null) {
					filePath.setText(file);
				}
			}
		});
		setControl(root);
	}
	
	public String getPath() {
		return filePath.getText();
	}
}
