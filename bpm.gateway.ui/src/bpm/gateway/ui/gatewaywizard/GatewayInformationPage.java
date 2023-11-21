package bpm.gateway.ui.gatewaywizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;

public class GatewayInformationPage extends WizardPage {

	private Text name, fileName, description, author;
	private Button b;
	private List<Button> buttons = new ArrayList<Button>();

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			if (e.getSource() == name) {
				String gatewayName = name.getText();
				
				String filePath = fileName.getText();
				if (filePath == null || filePath.isEmpty()) {
					String platformInstallationPath = String.valueOf(Platform.getInstallLocation().getURL());
					//We remove 'file:/' from the path
					platformInstallationPath = platformInstallationPath.substring(6);
					
					String newFilePath = platformInstallationPath + gatewayName + ".gateway"; //$NON-NLS-1$
					fileName.setText(newFilePath);
				}
				else {
					if (filePath.contains("/")) { //$NON-NLS-1$
						filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1); //$NON-NLS-1$
					}
					else if (filePath.contains("\\")) { //$NON-NLS-1$
						filePath = filePath.substring(0, filePath.lastIndexOf("\\") + 1); //$NON-NLS-1$
					}
					else {
						String platformInstallationPath = String.valueOf(Platform.getInstallLocation().getURL());
						//We remove 'file:/' from the path
						filePath = platformInstallationPath.substring(6);
					}
					
					String newFilePath = filePath + gatewayName + ".gateway"; //$NON-NLS-1$
					fileName.setText(newFilePath);
				}
			}
			getContainer().updateButtons();
		}
	};

	public GatewayInformationPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(false);

		if (getWizard() instanceof GatewayWizard) {
			fill(((GatewayWizard) getWizard()).input);
		}
	}

	private void createPageContent(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.GatewayInformationPage_0);

		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(listener);

		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.GatewayInformationPage_1);

		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));

		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.GatewayInformationPage_2);

		author = new Text(main, SWT.BORDER);
		author.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Composite fileBar = new Composite(main, SWT.NONE);
		fileBar.setLayout(new GridLayout(3, false));
		fileBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l4 = new Label(fileBar, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.GatewayInformationPage_3);

		fileName = new Text(fileBar, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fileName.addModifyListener(listener);
		fileName.setEnabled(false);

		b = new Button(fileBar, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setFilterExtensions(new String[] { "*.gateway", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

				String path = fd.open();

				if (fd != null) {
					if (!path.toLowerCase().contains(".gateway")) { //$NON-NLS-1$
						path += ".gateway"; //$NON-NLS-1$
					}
					
					String gatewayName = ""; //$NON-NLS-1$
					if (path.contains("/")) { //$NON-NLS-1$
						gatewayName = path.substring(path.lastIndexOf("/") + 1, path.indexOf(".gateway")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else if (path.contains("\\")) { //$NON-NLS-1$
						gatewayName = path.substring(path.lastIndexOf("\\") + 1, path.indexOf(".gateway")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else {
						gatewayName = path.substring(0, path.indexOf(".gateway")); //$NON-NLS-1$
					}

					name.setText(gatewayName);
					fileName.setText(path);
					getContainer().updateButtons();
				}
			}

		});

		Group group = new Group(main, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL, GridData.END, true, true, 2, 1));

		for (int i = 0; i < DocumentGateway.TRANSFORMATION_TYPE.length; i++) {
			Button b = new Button(group, SWT.RADIO);
			b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			b.setText(DocumentGateway.TRANSFORMATION_TYPE[i]);
			buttons.add(b);
		}

		buttons.get(0).setSelection(true);
	}

	@Override
	public boolean isPageComplete() {
		return !(name.getText().trim().equals("") || fileName.getText().equals("")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Properties getProperties() {
		Properties p = new Properties();
		p.setProperty("name", name.getText()); //$NON-NLS-1$
		p.setProperty("description", description.getText()); //$NON-NLS-1$
		p.setProperty("author", author.getText()); //$NON-NLS-1$
		p.setProperty("fileName", fileName.getText()); //$NON-NLS-1$

		for (Button b : buttons) {
			if (b.getSelection()) {
				p.setProperty("mode", buttons.indexOf(b) + ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return p;
	}

	public void fill(GatewayEditorInput input) {

		if (input != null) {
			fileName.setEnabled(false);
			b.setEnabled(false);

			name.setText(input.getDocumentGateway().getName());
			description.setText(input.getDocumentGateway().getDescription());
			author.setText(input.getDocumentGateway().getAuthor());
			fileName.setText(input.getName());

			for (Button b : buttons) {
				b.setSelection(input.getDocumentGateway().getMode() == buttons.indexOf(b));
			}
		}
	}
}
