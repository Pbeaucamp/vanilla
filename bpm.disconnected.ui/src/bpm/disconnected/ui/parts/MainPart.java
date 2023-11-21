/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package bpm.disconnected.ui.parts;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteServerManager;

public class MainPart {

	private static final String PATH = "VanillaDisconnected";

	@Inject
	Shell shell;

	private Button btnLaunch, btnStop, btnImportProject, btnOpenViewer;

	private String packagePath;

	private IVanillaContext ctx;
	private IVanillaAPI vanillaApi;

	private boolean isConnected = false;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		btnLaunch = new Button(parent, SWT.PUSH);
		btnLaunch.setLayoutData(new GridData(SWT.FILL, GridData.BEGINNING, true, false));
		btnLaunch.setText("Launch Disconnected Application");
		btnLaunch.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					startDisconnected();
				} catch (IOException | InterruptedException | InvocationTargetException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnStop = new Button(parent, SWT.PUSH);
		btnStop.setLayoutData(new GridData(SWT.FILL, GridData.BEGINNING, true, false));
		btnStop.setText("Stop Disconnected Application");
		btnStop.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					stopDisconnected();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnStop.setEnabled(false);

		btnImportProject = new Button(parent, SWT.PUSH);
		btnImportProject.setLayoutData(new GridData(SWT.FILL, GridData.BEGINNING, true, false));
		btnImportProject.setText("Import Package");
		btnImportProject.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(shell);
				dial.setFilterExtensions(new String[] { "*.zip" });

				String file = dial.open();

				if (file != null && !file.isEmpty()) {

					File fileSource = new File(file);
					File fileDest = new File(packagePath + "/" + fileSource.getName());

					boolean newFile = false;
					try {
						newFile = fileDest.createNewFile();
					} catch (IOException e2) {
						e2.printStackTrace();
						MessageDialog.openError(shell, "Informations", "A problem happend during file import : " + e2.getMessage());
						return;
					}

					if (newFile) {
						try {
							FileUtils.copyFile(fileSource, fileDest);
						} catch (IOException e1) {
							e1.printStackTrace();
							MessageDialog.openError(shell, "Informations", "A problem happend during file import : " + e1.getMessage());
						}

						MessageDialog.openInformation(shell, "Informations", "The package has been successfully imported.");
					}
					else {
						MessageDialog.openInformation(shell, "Informations", "The file already exist.");
					}
				}
			}
		});
		btnImportProject.setEnabled(false);

		btnOpenViewer = new Button(parent, SWT.PUSH);
		btnOpenViewer.setLayoutData(new GridData(SWT.FILL, GridData.BEGINNING, true, false));
		btnOpenViewer.setText("Open Vanilla Viewer");
		btnOpenViewer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					openViewer();
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnOpenViewer.setEnabled(false);

		this.vanillaApi = initVanillaApi();
		this.packagePath = PATH + File.separator + ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_DISCO_PACKAGE_FOLDER);
	}

	@PreDestroy
	public void preDestroy() {
		if(isConnected) {
			try {
				stopDisconnected();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private IVanillaAPI initVanillaApi() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String vanillaUrl = config.getProperty(VanillaConfiguration.P_VANILLA_URL);

		ctx = new BaseVanillaContext(vanillaUrl, login, password);

		return new RemoteVanillaPlatform(ctx);
	}

	private void startDisconnected() throws IOException, InterruptedException, InvocationTargetException {
		if (Program.launch("LaunchDisco.cmd")) {
			ProgressMonitorDialog dial = new ProgressMonitorDialog(shell);
			dial.run(true, true, new CheckState());
		}
	}

	private boolean checkState() {
		List<Server> servers = new ArrayList<>();
		try {
			servers = vanillaApi.getVanillaSystemManager().getServerNodes(true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		boolean ready = false;
		for (Server s : servers) {
			if (VanillaComponentType.COMPONENT_FREEANALYSISWEB.equals(s.getComponentNature())) {
				if (s.getComponentStatus().equals(Status.STARTED.getStatus())) {
					ready = true;
				}
				else {
					ready = false;
				}
			}
			else if (VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature())) {
				RemoteServerManager remoteServer = new RemoteReportRuntime(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
				boolean isStarted;
				try {
					isStarted = remoteServer.isStarted();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				if (s.getComponentStatus().equals(Status.STARTED.getStatus()) && isStarted) {
					ready = true;
				}
				else {
					ready = false;
				}
			}
			else if (VanillaComponentType.COMPONENT_FREEDASHBOARD.equals(s.getComponentNature())) {
				if (s.getComponentStatus().equals(Status.STARTED.getStatus())) {
					ready = true;
				}
				else {
					ready = false;
				}
			}
		}

		return ready;
	}

	private void stopDisconnected() throws IOException, InterruptedException {
		Program.launch("StopDisco.cmd");
	}

	private void openViewer() throws IOException, URISyntaxException {
		String viewerUrl = getViewerUrl();
		Program.launch(viewerUrl);
	}

	private String getViewerUrl() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String protocole = config.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PROTOCOLE);
		String ip = config.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_IP);
		String port = config.getProperty(VanillaConfiguration.P_VANILLA_LOCAL_SERVER_PORT);
		return protocole + ip + ":" + port + "/VanillaViewer";
	}

	private void update(boolean success) {
		this.isConnected = success;

		btnLaunch.setEnabled(!success);
		btnStop.setEnabled(success);
		btnOpenViewer.setEnabled(success);
		btnImportProject.setEnabled(success);
	}

	class CheckState implements IRunnableWithProgress {

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Waiting for launch to complete...", IProgressMonitor.UNKNOWN);

			boolean successTmp = true;

			try {
				while (!checkState() && successTmp) {
					if (!monitor.isCanceled()) {
						Thread.sleep(5000);
					}
					else {
						monitor.done();
						successTmp = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				successTmp = false;
			}

			monitor.done();

			final boolean success = successTmp;

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					update(success);
				}
			});
		}
	}
}