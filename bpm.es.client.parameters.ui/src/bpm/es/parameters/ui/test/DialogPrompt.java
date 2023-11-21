package bpm.es.parameters.ui.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.es.parameters.ui.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogPrompt extends Dialog {

	private IRuntimeConfig config;

	private Composite composite = null;
	private Composite root = null;
	private ComboViewer groupName;
	private RepositoryItem currentItem;

	private HashMap<Combo, VanillaParameter> cbo = new HashMap<Combo, VanillaParameter>();
	private RemoteVanillaParameterComponent api;

	public DialogPrompt(Shell parentShell, RepositoryItem currentItem) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.currentItem = currentItem;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		root = main;
		Label _l = new Label(main, SWT.NONE);
		_l.setLayoutData(new GridData());
		_l.setText(Messages.DialogPrompt_0);

		groupName = new ComboViewer(main, SWT.READ_ONLY);
		groupName.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupName.setContentProvider(new ArrayContentProvider());
		groupName.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Group) {
					return ((Group) element).getName();
				}
				return super.getText(element);
			}
		});

		groupName.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				init();

			}
		});

		try {
			groupName.setInput(adminbirep.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return main;
	}

	private void init() {
		Repository rep = adminbirep.Activator.getDefault().getCurrentRepository();
		IRepositoryApi soc = adminbirep.Activator.getDefault().getRepositoryApi();

		api = new RemoteVanillaParameterComponent(soc.getContext().getVanillaContext());

		try {
			if (currentItem.getType() == IRepositoryApi.FWR_TYPE || (currentItem.getType() == IRepositoryApi.CUST_TYPE && currentItem.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE)) {
				config = new ReportRuntimeConfig(new ObjectIdentifier(rep.getId(), currentItem.getId()), Collections.EMPTY_LIST, ((Group) ((IStructuredSelection) groupName.getSelection()).getFirstElement()).getId());
			}
			else {
				config = new RuntimeConfiguration(((Group) ((IStructuredSelection) groupName.getSelection()).getFirstElement()).getId(), new ObjectIdentifier(rep.getId(), currentItem.getId()), Collections.EMPTY_LIST);
			}

			try {
				List<VanillaGroupParameter> parmas = api.getParameters(config);
				if (currentItem.getType() == IRepositoryApi.FWR_TYPE || (currentItem.getType() == IRepositoryApi.CUST_TYPE && currentItem.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE)) {
					config = new ReportRuntimeConfig(config.getObjectIdentifier(), parmas, config.getVanillaGroupId());
				}
				else {
					config = new RuntimeConfiguration(config.getVanillaGroupId(), config.getObjectIdentifier(), parmas);
				}

				if (composite == null) {
					buildContent();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.DialogPrompt_1, true);
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText(Messages.DialogPrompt_2);
	}

	private void buildContent() {
		composite = new Composite(root, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		for (VanillaGroupParameter p : config.getParametersValues()) {
			for (VanillaParameter vp : ((VanillaGroupParameter) p).getParameters()) {
				Label l = new Label(composite, SWT.NONE);
				l.setText(vp.getName());
				l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));

				Combo cbo = new Combo(composite, SWT.READ_ONLY);
				cbo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				List<String> c = new ArrayList<String>(vp.getValues().keySet());
				Collections.sort(c);

				cbo.setItems(c.toArray(new String[c.size()]));
				this.cbo.put(cbo, vp);
				final VanillaGroupParameter group = (VanillaGroupParameter) p;
				cbo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Combo cb = (Combo) e.widget;
						VanillaParameter p = DialogPrompt.this.cbo.get(cb);

						List<String> vals = new ArrayList<String>();
						vals.add(cb.getText());
						p.setSelectedValues(vals);

						// refresh
						refreshGroup(cb, group);

					}
				});
			}
		}

		root.layout(true);
	}

	private void refreshGroup(Combo cb, VanillaGroupParameter group) {

		int index = group.getParameters().indexOf(cbo.get(cb));
		if (index < 0) {
			return;
		}
		index++;
		VanillaParameter p = group.getParameters().get(index);

		try {
			VanillaParameter n = api.getReportParameterValues(config, p.getName());

			Combo toUpdate = null;
			for (Combo c : cbo.keySet()) {
				if (cbo.get(c).getVanillaParameterId() == p.getVanillaParameterId()) {
					toUpdate = c;
					break;
				}
			}

			if (toUpdate == null) {
				return;
			}

			cbo.get(toUpdate).setValues(n.getValues());

			List<String> c = new ArrayList<String>(n.getValues().keySet());
			Collections.sort(c);

			toUpdate.setItems(c.toArray(new String[c.size()]));

			for (int i = index + 1; i < group.getParameters().size(); i++) {
				group.getParameters().get(i).setValues(new LinkedHashMap<String, String>());
				group.getParameters().get(i).setSelectedValues(new ArrayList<String>());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
