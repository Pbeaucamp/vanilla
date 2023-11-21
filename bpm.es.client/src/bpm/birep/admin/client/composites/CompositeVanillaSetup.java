package bpm.birep.admin.client.composites;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.SearchReplaceSetup;
import bpm.vanilla.platform.core.beans.VanillaSetup;

public class CompositeVanillaSetup extends Composite {

	private Button update;
	private Button searchReplace;
	private Text analysis, dash, webrep, metrics, fasd, birt, work, security, sched, bvpath, vanilla, path;
	private Text orbeonPath;
	private Text orbeonUrl;
	private Text gatewayServerUrl;
	private Text googleKey;
	private Text reportEngine;
	private Text vanillaFiles;
	private Text vanillaRuntimeServersUrl;

	private VanillaSetup vanillaSetup;

	public CompositeVanillaSetup(Composite parent, int style, VanillaSetup vanillaSetup) {
		super(parent, style);
		this.vanillaSetup = vanillaSetup;
		buildContent();
		fillDatas();
	}

	private void fillDatas() {
		if (vanillaSetup == null) {
			return;
		}

		if (vanillaSetup.getFreeAnalysisServer() != null) {
			analysis.setText(vanillaSetup.getFreeAnalysisServer());
		}
		if (vanillaSetup.getFreeWebReportServer() != null) {
			webrep.setText(vanillaSetup.getFreeWebReportServer());
		}
		if (vanillaSetup.getFasdWebServer() != null) {
			fasd.setText(vanillaSetup.getFasdWebServer());
		}
		if (vanillaSetup.getFreeMetricsServer() != null) {
			metrics.setText(vanillaSetup.getFreeMetricsServer());
		}
		if (vanillaSetup.getSchedulerServer() != null) {
			sched.setText(vanillaSetup.getSchedulerServer());
		}
		if (vanillaSetup.getVanillaRuntimeServersUrl() != null) {
			dash.setText(vanillaSetup.getVanillaRuntimeServersUrl());
		}
		if (vanillaSetup.getWorkbook() != null) {
			work.setText(vanillaSetup.getWorkbook());
		}
		if (vanillaSetup.getSecurityServer() != null) {
			security.setText(vanillaSetup.getSecurityServer());
		}
		if (vanillaSetup.getVanillaServer() != null) {
			vanilla.setText(vanillaSetup.getVanillaServer());
		}
		if (vanillaSetup.getPath() != null) {
			path.setText(vanillaSetup.getPath());
		}
		if (vanillaSetup.getBirtViewer() != null) {
			birt.setText(vanillaSetup.getBirtViewer());
		}
		if (vanillaSetup.getBirtViewerPath() != null) {
			bvpath.setText(vanillaSetup.getBirtViewerPath());
		}
		if (vanillaSetup.getOrbeonPath() != null) {
			orbeonPath.setText(vanillaSetup.getOrbeonPath());
		}
		if (vanillaSetup.getOrbeonUrl() != null) {
			orbeonUrl.setText(vanillaSetup.getOrbeonUrl());
		}
		if (vanillaSetup.getGatewayServerUrl() != null) {
			gatewayServerUrl.setText(vanillaSetup.getGatewayServerUrl());
		}
		if (vanillaSetup.getGoogleKey() != null) {
			googleKey.setText(vanillaSetup.getGoogleKey());
		}
		if (vanillaSetup.getReportEngine() != null) {
			reportEngine.setText(vanillaSetup.getReportEngine());
		}
		if (vanillaSetup.getVanillaFiles() != null) {
			vanillaFiles.setText(vanillaSetup.getVanillaFiles());
		}
		if (vanillaSetup.getVanillaRuntimeServersUrl() != null) {
			vanillaRuntimeServersUrl.setText(vanillaSetup.getVanillaRuntimeServersUrl());
		}
	}

	private void buildContent() {
		this.setLayout(new GridLayout(2, false));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l2.setText(Messages.CompositeVanillaSetup_2);

		analysis = new Text(this, SWT.BORDER);
		analysis.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l5.setText(Messages.CompositeVanillaSetup_3);

		fasd = new Text(this, SWT.BORDER);
		fasd.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l9 = new Label(this, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l9.setText(Messages.CompositeVanillaSetup_4);

		webrep = new Text(this, SWT.BORDER);
		webrep.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l4.setText(Messages.CompositeVanillaSetup_5);

		dash = new Text(this, SWT.BORDER);
		dash.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l6.setText(Messages.CompositeVanillaSetup_6);

		metrics = new Text(this, SWT.BORDER);
		metrics.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l7.setText(Messages.CompositeVanillaSetup_7);

		security = new Text(this, SWT.BORDER);
		security.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l8 = new Label(this, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l8.setText(Messages.CompositeVanillaSetup_8);

		sched = new Text(this, SWT.BORDER);
		sched.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label _l4 = new Label(this, SWT.NONE);
		_l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		_l4.setText(Messages.CompositeVanillaSetup_9);

		vanillaRuntimeServersUrl = new Text(this, SWT.NONE);
		vanillaRuntimeServersUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l10 = new Label(this, SWT.NONE);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l10.setText(Messages.CompositeVanillaSetup_10);

		work = new Text(this, SWT.BORDER);
		work.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l11 = new Label(this, SWT.NONE);
		l11.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l11.setText(Messages.CompositeVanillaSetup_11);

		vanilla = new Text(this, SWT.BORDER);
		vanilla.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l12 = new Label(this, SWT.NONE);
		l12.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l12.setText(Messages.CompositeVanillaSetup_12);

		path = new Text(this, SWT.BORDER);
		path.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l3.setText(Messages.CompositeVanillaSetup_13);

		birt = new Text(this, SWT.BORDER);
		birt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l14 = new Label(this, SWT.NONE);
		l14.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l14.setText(Messages.CompositeVanillaSetup_14);

		bvpath = new Text(this, SWT.BORDER);
		bvpath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label lblOrbeonUrl = new Label(this, SWT.NONE);
		lblOrbeonUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblOrbeonUrl.setText(Messages.CompositeVanillaSetup_15);

		orbeonUrl = new Text(this, SWT.BORDER);
		orbeonUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label lblOrbeonPath = new Label(this, SWT.NONE);
		lblOrbeonPath.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblOrbeonPath.setText(Messages.CompositeVanillaSetup_16);

		orbeonPath = new Text(this, SWT.BORDER);
		orbeonPath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label lblgatewayUrl = new Label(this, SWT.NONE);
		lblgatewayUrl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblgatewayUrl.setText(Messages.CompositeVanillaSetup_17);

		gatewayServerUrl = new Text(this, SWT.BORDER);
		gatewayServerUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label lblgoogle = new Label(this, SWT.NONE);
		lblgoogle.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblgoogle.setText(Messages.CompositeVanillaSetup_18);

		googleKey = new Text(this, SWT.BORDER);
		googleKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label lblre = new Label(this, SWT.NONE);
		lblre.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblre.setText(Messages.CompositeVanillaSetup_19);

		reportEngine = new Text(this, SWT.BORDER);
		reportEngine.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label lblvf = new Label(this, SWT.NONE);
		lblvf.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblvf.setText(Messages.CompositeVanillaSetup_20);

		vanillaFiles = new Text(this, SWT.BORDER);
		vanillaFiles.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		update = new Button(c, SWT.PUSH);
		update.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		update.setText(Messages.CompositeVanillaSetup_21);
		update.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				vanillaSetup.setBirtViewer(birt.getText());
				vanillaSetup.setBirtViewerPath(bvpath.getText());
				vanillaSetup.setFasdWebServer(fasd.getText());
				vanillaSetup.setFreeAnalysisServer(analysis.getText());
				vanillaSetup.setVanillaRuntimeServersUrl(dash.getText());
				vanillaSetup.setFreeMetricsServer(metrics.getText());
				vanillaSetup.setFreeWebReportServer(webrep.getText());
				vanillaSetup.setSecurityServer(security.getText());
				vanillaSetup.setSchedulerServer(sched.getText());
				vanillaSetup.setWorkbook(work.getText());
				vanillaSetup.setVanillaServer(vanilla.getText());
				vanillaSetup.setPath(path.getText());
				vanillaSetup.setOrbeonPath(orbeonPath.getText());
				vanillaSetup.setOrbeonUrl(orbeonUrl.getText());
				vanillaSetup.setGatewayServerUrl(gatewayServerUrl.getText());
				vanillaSetup.setGoogleKey(googleKey.getText());
				vanillaSetup.setReportEngine(reportEngine.getText());
				vanillaSetup.setVanillaFiles(vanillaFiles.getText());
				vanillaSetup.setVanillaRuntimeServersUrl(vanillaRuntimeServersUrl.getText());
				try {
					Activator.getDefault().getVanillaApi().getVanillaSystemManager().updateVanillaSetup(vanillaSetup);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openWarning(getShell(), Messages.CompositeVanillaSetup_22, e1.getMessage() + Messages.CompositeVanillaSetup_23);
					try {
						vanillaSetup = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup();
						fillDatas();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			}
		});

		searchReplace = new Button(c, SWT.PUSH);
		searchReplace.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		searchReplace.setText(Messages.CompositeVanillaSetup_24);
		searchReplace.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				vanillaSetup.setBirtViewer(birt.getText());
				vanillaSetup.setBirtViewerPath(bvpath.getText());
				vanillaSetup.setFasdWebServer(fasd.getText());
				vanillaSetup.setFreeAnalysisServer(analysis.getText());
				vanillaSetup.setVanillaRuntimeServersUrl(dash.getText());
				vanillaSetup.setFreeMetricsServer(metrics.getText());
				vanillaSetup.setFreeWebReportServer(webrep.getText());
				vanillaSetup.setSecurityServer(security.getText());
				vanillaSetup.setSchedulerServer(sched.getText());
				vanillaSetup.setWorkbook(work.getText());
				vanillaSetup.setVanillaServer(vanilla.getText());
				vanillaSetup.setPath(path.getText());
				vanillaSetup.setOrbeonPath(orbeonPath.getText());
				vanillaSetup.setOrbeonUrl(orbeonUrl.getText());
				vanillaSetup.setGatewayServerUrl(gatewayServerUrl.getText());
				vanillaSetup.setGoogleKey(googleKey.getText());
				vanillaSetup.setReportEngine(reportEngine.getText());
				vanillaSetup.setVanillaFiles(vanillaFiles.getText());
				vanillaSetup.setVanillaRuntimeServersUrl(vanillaRuntimeServersUrl.getText());

				SearchReplaceSetup dial = new SearchReplaceSetup(getShell());
				if (dial.open() == Dialog.OK) {
					try {
						vanillaSetup = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup();
						Activator.getDefault().getVanillaApi().getVanillaSystemManager().updateVanillaSetup(vanillaSetup);
						fillDatas();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openWarning(getShell(), Messages.CompositeVanillaSetup_25, e1.getMessage() + Messages.CompositeVanillaSetup_26);
						try {
							vanillaSetup = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup();
							fillDatas();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}
		});
	}

}
