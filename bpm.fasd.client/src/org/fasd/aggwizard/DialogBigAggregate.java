package org.fasd.aggwizard;

import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fasd.i18N.LanguageText;
import org.freeolap.FreemetricsPlugin;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryPicker;

public class DialogBigAggregate extends Dialog {
	public final static String P_IS_FILE = LanguageText.DialogBigAggregate_0;
	public final static String P_FILE_NAME = LanguageText.DialogBigAggregate_1;
	public final static String P_TRANSFO_NAME = LanguageText.DialogBigAggregate_2;
	public static final String P_GROUP_NAME = LanguageText.DialogBigAggregate_3;

	private Button inFile;
	private Button inRepository;

	private Text transformationName;
	private Text fileName;
	private Text comment;
	private Combo groupName;
	private Label groupLabel;

	private Properties prop = new Properties();
	private RepositoryDirectory targetDirectory;

	public DialogBigAggregate(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		createOptions(main);
		createTarget(main);

		return main;
	}

	private void createTarget(Composite parent) {
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(3, false));
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		g.setText(LanguageText.DialogBigAggregate_4);

		inRepository = new Button(g, SWT.RADIO);
		inRepository.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		inRepository.setText(LanguageText.DialogBigAggregate_5);
		inRepository.setEnabled(FreemetricsPlugin.getDefault().getRepositoryConnection() != null);

		inFile = new Button(g, SWT.RADIO);
		inFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		inFile.setText(LanguageText.DialogBigAggregate_6);
		inFile.setSelection(true);

		final Label l = new Label(g, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogBigAggregate_7);

		fileName = new Text(g, SWT.BORDER);
		fileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		final Button b = new Button(g, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (inRepository.getSelection()) {
					DialogDirectoryPicker d = new DialogDirectoryPicker(getShell(), FreemetricsPlugin.getDefault().getRepositoryConnection());
					if (d.open() != DialogDirectoryPicker.OK) {
						return;
					}
					targetDirectory = d.getDirectory();
					fileName.setText(targetDirectory.getName());
				} else {
					FileDialog d = new FileDialog(getShell());
					d.setFilterExtensions(new String[] { "*.gateway" }); //$NON-NLS-1$
					String s = d.open();

					if (s == null) {
						return;
					}
					fileName.setText(s);
				}

			}
		});

		SelectionAdapter adapt = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean flag = inRepository.getSelection();
				fileName.setEnabled(!flag);

				if (flag) {
					l.setText(LanguageText.DialogBigAggregate_10);
					groupLabel.setVisible(true);
					groupName.setVisible(true);

					try {

						if (groupName.getItemCount() > 0) {
							groupName.select(0);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(getShell(), LanguageText.DialogBigAggregate_11, LanguageText.DialogBigAggregate_12 + ex.getMessage());
						return;
					}
				} else {
					l.setText(LanguageText.DialogBigAggregate_13);
					groupLabel.setVisible(false);

					groupName.setVisible(false);
				}

			}
		};
		inRepository.addSelectionListener(adapt);
		inFile.addSelectionListener(adapt);

		groupLabel = new Label(g, SWT.NONE);
		groupLabel.setLayoutData(new GridData());
		groupLabel.setText(LanguageText.DialogBigAggregate_14);
		groupLabel.setVisible(false);

		groupName = new Combo(g, SWT.READ_ONLY);
		groupName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		groupName.setVisible(false);
	}

	private void createOptions(Composite parent) {
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		g.setText(LanguageText.DialogBigAggregate_15);

		Label l = new Label(g, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(LanguageText.DialogBigAggregate_16);

		transformationName = new Text(g, SWT.BORDER);
		transformationName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = new Label(g, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, true, true, 1, 3));
		l.setText(LanguageText.DialogBigAggregate_17);

		comment = new Text(g, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));

	}

	@Override
	protected void okPressed() {
		prop.setProperty(P_IS_FILE, inFile.getSelection() + ""); //$NON-NLS-1$
		prop.setProperty(P_FILE_NAME, fileName.getText());
		prop.setProperty(P_TRANSFO_NAME, transformationName.getText());
		prop.setProperty(P_GROUP_NAME, groupName.getText());
		super.okPressed();
	}

	public Properties getProperties() {
		return prop;
	}

	public RepositoryDirectory getDirectory() {
		return targetDirectory;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(LanguageText.DialogBigAggregate_19);
		getShell().setSize(600, 400);
	}
}