package bpm.vanilla.server.ui.views.composite;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

public class ConfigComposite implements IPropertyChangeListener {

	private Text nbTasks;
	private Text modelPoolSize;
	private Text historizationFolder;

	private Text homeFolder, tmpFolder;
	private Text birtLogs, birtGeneration, birtImageUri;

	private FormToolkit toolkit;

	private Composite compositeSection;
	private Composite actualClient;

	private Section section;

	private ServerType type;
	private ServerConfigInfo config;

	private Composite buttonsBar;
	private Button resetConfig, cancel;

	private IPropertyChangeListener configChangeListener;

	private ModifyListener txtListener = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent e) {
			resetConfig.setEnabled(true);
			cancel.setEnabled(true);

		}
	};

	public ConfigComposite(FormToolkit toolkit, Form form, IPropertyChangeListener listener) {
		this.toolkit = toolkit;
		this.configChangeListener = listener;
		createBaseServerConfigSection(form.getBody());

	}

	private void bind(boolean add) {
		if (add) {
			nbTasks.addModifyListener(txtListener);
			modelPoolSize.addModifyListener(txtListener);
			historizationFolder.addModifyListener(txtListener);

			if (type == ServerType.REPORTING) {
				birtGeneration.addModifyListener(txtListener);
				birtImageUri.addModifyListener(txtListener);
				birtLogs.addModifyListener(txtListener);
			}

			if (type == ServerType.GATEWAY) {
				homeFolder.addModifyListener(txtListener);
				tmpFolder.addModifyListener(txtListener);
			}

		}
		else {
			nbTasks.removeModifyListener(txtListener);
			modelPoolSize.removeModifyListener(txtListener);
			historizationFolder.removeModifyListener(txtListener);

			if (type == ServerType.REPORTING) {
				birtGeneration.removeModifyListener(txtListener);
				birtImageUri.removeModifyListener(txtListener);
				birtLogs.removeModifyListener(txtListener);
			}

			if (type == ServerType.GATEWAY) {
				homeFolder.removeModifyListener(txtListener);
				tmpFolder.removeModifyListener(txtListener);
			}
		}
	}

	public void setInput(ServerType type, ServerConfigInfo config) {
		bind(false);
		if (type == null || config == null) {
			if(birtGeneration != null && !birtGeneration.isDisposed()) {
				birtGeneration.setText(""); //$NON-NLS-1$
			}
			if(birtLogs != null && !birtLogs.isDisposed()) {
				birtLogs.setText(""); //$NON-NLS-1$
			}
			if(birtImageUri != null && !birtImageUri.isDisposed()) {
				birtImageUri.setText(""); //$NON-NLS-1$
			}
			if(homeFolder != null && !homeFolder.isDisposed()) {
				homeFolder.setText(""); //$NON-NLS-1$
			}
			if(tmpFolder != null && !tmpFolder.isDisposed()) {
				tmpFolder.setText(""); //$NON-NLS-1$
			}
			
			nbTasks.setText(""); //$NON-NLS-1$
			modelPoolSize.setText(""); //$NON-NLS-1$
			historizationFolder.setText(""); //$NON-NLS-1$
			this.type = type;
			this.config = null;
			compositeSection.setEnabled(false);
		}
		else {
			compositeSection.setEnabled(true);
		}
		if (type != this.type) {

			if (actualClient != null && !actualClient.isDisposed()) {
				actualClient.dispose();
			}
			this.type = type;
			switch (type) {
				case REPORTING:
					actualClient = createBirtComposite();
					birtGeneration.setText(config.getValue("generationFolder") + ""); //$NON-NLS-1$ //$NON-NLS-2$
					birtLogs.setText(config.getValue("birtReportEngineLogs") + ""); //$NON-NLS-1$ //$NON-NLS-2$
					birtImageUri.setText(config.getValue("imageUri") + ""); //$NON-NLS-1$ //$NON-NLS-2$
					break;
				case GATEWAY:
					actualClient = createBigComposite();
					homeFolder.setText(config.getValue("homeFolder") + ""); //$NON-NLS-1$ //$NON-NLS-2$
					tmpFolder.setText(config.getValue("tempFolder") + ""); //$NON-NLS-1$ //$NON-NLS-2$
					break;
			}

		}

		if(config != null) {
			nbTasks.setText(config.getValue("maximumRunningTasks") + ""); //$NON-NLS-1$ //$NON-NLS-2$
			modelPoolSize.setText(config.getValue("reportPoolSize") + ""); //$NON-NLS-1$ //$NON-NLS-2$
			historizationFolder.setText(config.getValue("historizationfolder") + ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		this.config = config;

		if (buttonsBar != null && !buttonsBar.isDisposed()) {
			buttonsBar.dispose();
		}
		createButtons();

		section.layout(true);

		bind(true);
	}

	private void createBaseServerConfigSection(Composite parent) {
		section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.ConfigComposite_24);
		section.setDescription(Messages.ConfigComposite_25);
		section.setLayout(new GridLayout());
		section.setExpanded(false);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL));

		compositeSection = toolkit.createComposite(section);
		compositeSection.setLayout(new GridLayout());
		// section.setClient(compositeSection);

		Composite composite = toolkit.createComposite(compositeSection);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		Label l = toolkit.createLabel(composite, Messages.ConfigComposite_26);
		l.setLayoutData(new GridData());

		nbTasks = toolkit.createText(composite, ""); //$NON-NLS-1$
		nbTasks.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = toolkit.createLabel(composite, Messages.ConfigComposite_28);
		l.setLayoutData(new GridData());

		modelPoolSize = toolkit.createText(composite, ""); //$NON-NLS-1$
		modelPoolSize.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = toolkit.createLabel(composite, Messages.ConfigComposite_30);
		l.setLayoutData(new GridData());

		historizationFolder = toolkit.createText(composite, "", SWT.WRAP | SWT.MULTI); //$NON-NLS-1$
		historizationFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		section.setClient(compositeSection);
	}

	private Composite createBirtComposite() {
		Composite birtComposite = toolkit.createComposite(compositeSection);
		birtComposite.setLayout(new GridLayout(2, false));
		birtComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

		Label l = toolkit.createLabel(birtComposite, Messages.ConfigComposite_32);
		l.setLayoutData(new GridData());

		birtLogs = toolkit.createText(birtComposite, "", SWT.WRAP | SWT.MULTI); //$NON-NLS-1$
		birtLogs.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		l = toolkit.createLabel(birtComposite, Messages.ConfigComposite_34);
		l.setLayoutData(new GridData());

		birtGeneration = toolkit.createText(birtComposite, "", SWT.WRAP | SWT.MULTI); //$NON-NLS-1$
		birtGeneration.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		l = toolkit.createLabel(birtComposite, Messages.ConfigComposite_36);
		l.setLayoutData(new GridData());

		birtImageUri = toolkit.createText(birtComposite, "", SWT.WRAP | SWT.MULTI); //$NON-NLS-1$
		birtImageUri.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		return birtComposite;
	}

	private Composite createBigComposite() {
		Composite composite = toolkit.createComposite(compositeSection);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

		Label l = toolkit.createLabel(composite, Messages.ConfigComposite_38);
		l.setLayoutData(new GridData());

		homeFolder = toolkit.createText(composite, "", SWT.WRAP | SWT.MULTI); //$NON-NLS-1$
		homeFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		l = toolkit.createLabel(composite, Messages.ConfigComposite_40);
		l.setLayoutData(new GridData());

		tmpFolder = toolkit.createText(composite, "", SWT.WRAP | SWT.MULTI); //$NON-NLS-1$
		tmpFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));

		return composite;
	}

	private void createButtons() {
		buttonsBar = toolkit.createComposite(compositeSection);
		buttonsBar.setLayout(new GridLayout(2, true));
		buttonsBar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		resetConfig = toolkit.createButton(buttonsBar, Messages.ConfigComposite_42, SWT.PUSH);
		resetConfig.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		resetConfig.setEnabled(false);

		cancel = toolkit.createButton(buttonsBar, Messages.ConfigComposite_43, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setEnabled(false);

		resetConfig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					updateConfig();
					// Activator.getDefault().getServerRemote().resetServerConfig(config);
					cancel.setEnabled(false);
					resetConfig.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(compositeSection.getShell(), Messages.ConfigComposite_44, Messages.ConfigComposite_45 + e1.getMessage());
				}
			}
		});

		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ServerConfigInfo config = Activator.getDefault().getRemoteServerManager().getServerConfig();
					setInput(type, config);
					cancel.setEnabled(false);
					resetConfig.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(compositeSection.getShell(), Messages.ConfigComposite_46, Messages.ConfigComposite_47 + e1.getMessage());
				}
			}
		});

	}

	private void updateConfig() {

		config.setValue("maximumRunningTasks", nbTasks.getText()); //$NON-NLS-1$
		config.setValue("historizationfolder", historizationFolder.getText()); //$NON-NLS-1$
		config.setValue("reportPoolSize", modelPoolSize.getText()); //$NON-NLS-1$

		if (type == ServerType.GATEWAY) {
			config.setValue("homeFolder", homeFolder.getText()); //$NON-NLS-1$
			config.setValue("tempFolder", tmpFolder.getText()); //$NON-NLS-1$
		}
		else if (type == ServerType.REPORTING) {
			config.setValue("generationFolder", birtGeneration.getText()); //$NON-NLS-1$
			config.setValue("imageUri", birtImageUri.getText()); //$NON-NLS-1$
			config.setValue("birtReportEngineLogs", birtLogs.getText()); //$NON-NLS-1$
		}

		try {
			Activator.getDefault().getRemoteServerManager().resetServerConfig(config);
			configChangeListener.propertyChange(new PropertyChangeEvent(this, "config", null, config)); //$NON-NLS-1$
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(compositeSection.getShell(), Messages.ConfigComposite_57, Messages.ConfigComposite_58 + ex.getMessage());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getNewValue() instanceof ServerConfigInfo) {
			setInput(type, (ServerConfigInfo) event.getNewValue());
		}

	}
}
