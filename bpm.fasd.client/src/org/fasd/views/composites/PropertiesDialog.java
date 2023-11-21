package org.fasd.views.composites;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.DocumentProperties;
import org.freeolap.FreemetricsPlugin;

public class PropertiesDialog extends Dialog {

	private Label tCreation, tModification;
	private Text tName, tAuthor, tDescription, tVersion, tIcon, tFlyOver;
	private DocumentProperties properties;

	public DocumentProperties getDocProperty() {
		return properties;
	}

	public PropertiesDialog(Shell parentShell, DocumentProperties properties) {
		super(parentShell);
		this.properties = properties;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			super.buttonPressed(buttonId);
		} else {
			super.buttonPressed(buttonId);
		}

	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(LanguageText.PropertiesDialog_0);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);

		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// fill Name
		Label f1 = new Label(composite, SWT.NONE);
		f1.setLayoutData(new GridData());
		f1.setText(LanguageText.PropertiesDialog_1);

		tName = new Text(composite, SWT.BORDER);
		tName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		// fill Author
		Label f2 = new Label(composite, SWT.NONE);
		f2.setLayoutData(new GridData());
		f2.setText(LanguageText.PropertiesDialog_2);

		tAuthor = new Text(composite, SWT.BORDER);
		tAuthor.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		// fill Description
		Label f3 = new Label(composite, SWT.NONE);
		f3.setLayoutData(new GridData());
		f3.setText(LanguageText.PropertiesDialog_3);

		tDescription = new Text(composite, SWT.BORDER);
		tDescription.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		// fill creation
		Label f4 = new Label(composite, SWT.NONE);
		f4.setLayoutData(new GridData());
		f4.setText(LanguageText.PropertiesDialog_4);

		tCreation = new Label(composite, SWT.BORDER);
		tCreation.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
		tCreation.setText(sdf.format(FreemetricsPlugin.getDefault().getFAModel().getDocumentProperties().getCreation()));

		// fill modification
		Label f5 = new Label(composite, SWT.NONE);
		f5.setLayoutData(new GridData());
		f5.setText(LanguageText.PropertiesDialog_6);

		tModification = new Label(composite, SWT.BORDER);
		tModification.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		// fill version
		Label f6 = new Label(composite, SWT.NONE);
		f6.setLayoutData(new GridData());
		f6.setText(LanguageText.PropertiesDialog_7);

		tVersion = new Text(composite, SWT.BORDER);
		tVersion.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		// fill iconPath
		Label f7 = new Label(composite, SWT.NONE);
		f7.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		f7.setText(LanguageText.PropertiesDialog_8);

		tIcon = new Text(composite, SWT.BORDER);
		tIcon.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		Button b3 = new Button(composite, SWT.PUSH);
		b3.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false, 1, 1));
		b3.setText("..."); //$NON-NLS-1$
		b3.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(PropertiesDialog.this.getShell());
				fd.setFilterExtensions(new String[] { "*.bmp", "*.jpg", "*.png", "*.gif" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				tIcon.setText(fd.open());
			}

		});

		// fill iconflyoVERPath
		Label f8 = new Label(composite, SWT.NONE);
		f8.setLayoutData(new GridData());
		f8.setText(LanguageText.PropertiesDialog_14);

		tFlyOver = new Text(composite, SWT.BORDER);
		tFlyOver.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		Button b4 = new Button(composite, SWT.PUSH);
		b4.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false, 1, 1));
		b4.setText("..."); //$NON-NLS-1$
		b4.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(PropertiesDialog.this.getShell());
				fd.setFilterExtensions(new String[] { "*.bmp", "*.jpg", "*.png", "*.gif" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				tFlyOver.setText(fd.open());
			}

		});

		FreemetricsPlugin plugin = FreemetricsPlugin.getDefault();

		if (plugin.getFAModel() != null) {
			properties = plugin.getFAModel().getDocumentProperties();
			tName.setText(properties.getName());
			tAuthor.setText(properties.getAuthor());
			tDescription.setText(properties.getDescription());
			tVersion.setText(properties.getVersion());
			tModification.setText(sdf.format(properties.getModification()));

		}

		applyDialogFont(composite);
		return composite;
	}

	public Composite setupDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);

		return composite;
	}

	@Override
	protected void okPressed() {

		properties.setName(tName.getText());
		properties.setAuthor(tAuthor.getText());
		properties.setVersion(tVersion.getText());
		properties.setDescription(tDescription.getText());
		try {
			properties.setCreation(tCreation.getText());
			properties.setModification(tModification.getText());
		} catch (ParseException e) {
			MessageDialog.openError(this.getShell(), LanguageText.PropertiesDialog_20, LanguageText.PropertiesDialog_21);
		}

		FreemetricsPlugin.getDefault().getFAModel().setChanged();
		super.okPressed();

	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);
		shell.setSize(400, 300);

	}
}