package org.fasd.cubewizard.dimension.preload;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.PreloadConfig;
import org.freeolap.FreemetricsPlugin;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;

public class DialogPreload extends Dialog {

	private CompositePreloadConfig composite;
	private PreloadConfig previousConfig;

	public DialogPreload(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		composite = new CompositePreloadConfig(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}

	@Override
	protected void okPressed() {
		FreemetricsPlugin.getDefault().getFAModel().setPreloadConfig(composite.getConfig());
		super.okPressed();
	}

	@Override
	protected void initializeBounds() {
		List<OLAPHierarchy> l = new ArrayList<OLAPHierarchy>();

		for (OLAPDimension d : ((OLAPSchema) FreemetricsPlugin.getDefault().getFAModel().getSchema()).getDimensions()) {
			l.addAll(d.getHierarchies());
		}

		composite.init(l);
		super.initializeBounds();
	}

	@Override
	protected void createButtonsForButtonBar(Composite main) {
		main.setLayout(new GridLayout(3, true));
		main.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		Button btnOK = new Button(main, SWT.PUSH);
		btnOK.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnOK.setText(LanguageText.DialogPreload_0);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});

		Button btnRun = new Button(main, SWT.PUSH);
		btnRun.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnRun.setText(LanguageText.DialogPreload_1);
		btnRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					UnitedOlapLoader loader = UnitedOlapLoaderFactory.getLoader();
					FAModel model = FreemetricsPlugin.getDefault().getFAModel();

					previousConfig = model.getPreloadConfig();

					PreloadConfig conf = composite.getConfig();
					model.setPreloadConfig(conf);

					IRuntimeContext ctx = null;
					if (FreemetricsPlugin.getDefault().getRepositoryConnection() == null) {
						ctx = new RuntimeContext("system", "system", "System", 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						IRepositoryApi sock = FreemetricsPlugin.getDefault().getRepositoryConnection();
						IVanillaAPI vanillaApi = FreemetricsPlugin.getDefault().getVanillaApi();

						ctx = new RuntimeContext(sock.getContext().getVanillaContext().getLogin(), sock.getContext().getVanillaContext().getPassword(), vanillaApi.getVanillaSecurityManager().getGroupById(sock.getContext().getGroup().getId()).getName(), sock.getContext().getGroup().getId());
					}

					loader.loadModel(model, ctx, null);

					MessageDialog.openInformation(getShell(), LanguageText.DialogPreload_5, LanguageText.DialogPreload_6);

				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), LanguageText.DialogPreload_7, e1.getMessage());
				}
			}
		});

		Button btnCancel = new Button(main, SWT.PUSH);
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnCancel.setText(LanguageText.DialogPreload_8);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (previousConfig != null) {
					FreemetricsPlugin.getDefault().getFAModel().setPreloadConfig(previousConfig);
				}
				cancelPressed();
			}
		});
	}

}
