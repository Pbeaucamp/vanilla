package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.workflow.runtime.communication.CommunicatorHelper;
import bpm.workflow.runtime.model.activities.vanilla.MetadataToD4CActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class CkanSection extends AbstractPropertySection {

	private Node node;
	// private Button btnNewPackage;
	// private Button btnSelectPackage;
	// private Button btnNewResource;
	// private Button btnSelectResource;

	// private Text txtNewPackage;
	// private Text txtNewResource;
	private Composite packageComposite;
	private Button btnLoadPackage;
	private ComboViewer cbPackages;
	private ComboViewer cbResources;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());

		Group group = getWidgetFactory().createGroup(parent, Messages.CkanSection_1);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		// btnNewPackage = getWidgetFactory().createButton(group,
		// Messages.CkanSection_0, SWT.RADIO);
		// btnNewPackage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
		// true, false));
		// btnNewPackage.setSelection(true);
		//
		// txtNewPackage = new Text(group, SWT.BORDER);
		// txtNewPackage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
		// true, false));
		// txtNewPackage.addModifyListener(new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		//				((MetadataToD4CActivity) node.getWorkflowObject()).setPackage("null", txtNewPackage.getText(),txtNewPackage.getText()); //$NON-NLS-1$
		// }
		// });
		//
		// Label lblResources = getWidgetFactory().createLabel(group,
		// Messages.CkanSection_2);
		// lblResources.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING,
		// false, false));
		//
		// txtNewResource = new Text(group, SWT.BORDER);
		// txtNewResource.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
		// true, false));
		// txtNewResource.addModifyListener(new ModifyListener() {
		// @Override
		// public void modifyText(ModifyEvent e) {
		// ((MetadataToD4CActivity)
		// node.getWorkflowObject()).setResourceName(txtNewResource.getText());
		// }
		// });
		//
		// btnSelectPackage = getWidgetFactory().createButton(group,
		// Messages.CkanSection_3, SWT.RADIO);
		// btnSelectPackage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
		// true, false));

		packageComposite = getWidgetFactory().createComposite(group);
		packageComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		packageComposite.setLayout(new GridLayout());

		btnLoadPackage = getWidgetFactory().createButton(packageComposite, Messages.CkanSection_4, SWT.PUSH);
		btnLoadPackage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		btnLoadPackage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent ev) {
				MetadataToD4CActivity activity = getActivity();
				loadPackages(activity);
			}
		});

		Label lblPackages = getWidgetFactory().createLabel(packageComposite, Messages.CkanSection_5);
		lblPackages.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

		cbPackages = new ComboViewer(packageComposite, SWT.READ_ONLY | SWT.PUSH);
		cbPackages.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbPackages.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<CkanPackage> l = (List<CkanPackage>) inputElement;
				return l.toArray(new CkanPackage[l.size()]);
			}
		});
		cbPackages.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((CkanPackage) element).getTitle();
			}
		});
		cbPackages.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					cbResources.setInput(((CkanPackage) ((IStructuredSelection) cbPackages.getSelection()).getFirstElement()).getResources());
					((MetadataToD4CActivity) node.getWorkflowObject()).setCkanPackage(((CkanPackage) ((IStructuredSelection) cbPackages.getSelection()).getFirstElement()));
				} catch (Exception e) {
				}
			}
		});

		// btnNewResource = getWidgetFactory().createButton(group,
		// "New Ressource", SWT.RADIO);
		// btnNewResource.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
		// true, false));
		// btnNewResource.setSelection(true);

		// btnSelectResource = getWidgetFactory().createButton(group,
		// "Existing Ressource", SWT.RADIO);
		// btnSelectResource.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
		// true, false));

		Label lblSelResources = getWidgetFactory().createLabel(packageComposite, Messages.CkanSection_6);
		lblSelResources.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));

		cbResources = new ComboViewer(group, SWT.READ_ONLY | SWT.PUSH);
		cbResources.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		cbResources.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<CkanResource> l = (List<CkanResource>) inputElement;
				return l.toArray(new CkanResource[l.size()]);
			}
		});
		cbResources.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((CkanResource) element).getName();
			}
		});

		cbResources.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				CkanResource resource = (CkanResource) ((IStructuredSelection) cbResources.getSelection()).getFirstElement();
				((MetadataToD4CActivity) node.getWorkflowObject()).setResource(resource.getId(), resource.getName());
			}
		});
		// btnNewPackage.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// packageComposite.setEnabled(false);
		// txtNewPackage.setEnabled(true);
		// cbPackages.getCombo().setEnabled(false);
		// btnLoadPackage.setEnabled(false);
		// cbResources.getCombo().setEnabled(false);
		// txtNewResource.setEnabled(true);
		// }
		// });
		// btnSelectPackage.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// packageComposite.setEnabled(true);
		// txtNewPackage.setEnabled(false);
		// cbPackages.getCombo().setEnabled(true);
		// btnLoadPackage.setEnabled(true);
		// cbResources.getCombo().setEnabled(true);
		// txtNewResource.setEnabled(false);
		// }
		// });
		// btnNewResource.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// cbResources.getCombo().setEnabled(false);
		// txtNewResource.setEnabled(true);
		// }
		// });
		// btnSelectResource.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// cbResources.getCombo().setEnabled(true);
		// txtNewResource.setEnabled(false);
		// }
		// });
	}

	protected void loadPackages(MetadataToD4CActivity activity) {
		try {
			String ckanUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_CKAN_URL);

			String jsonStatus = CommunicatorHelper.sendGetMessage(ckanUrl, CkanHelper.GET_PACKAGES);
			if (jsonStatus == null || jsonStatus.isEmpty()) {
				throw new Exception(Messages.CkanSection_7);
			}

			JSONObject jsonObject = new JSONObject(jsonStatus);
			JSONArray packagesJson = !jsonObject.isNull("result") ? jsonObject.getJSONArray("result") : null; //$NON-NLS-1$ //$NON-NLS-2$

			List<CkanPackage> packages = new ArrayList<>();
			if (packagesJson != null) {
				for (int i = 0; i < packagesJson.length(); i++) {
					try {
						String packakgeName = packagesJson.get(i).toString();
						CkanHelper helper = new CkanHelper();
						CkanPackage pack = helper.getCkanPackage(packakgeName);
						if (pack != null) {
							packages.add(pack);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			Collections.sort(packages, new Comparator<CkanPackage>() {
				@Override
				public int compare(CkanPackage o1, CkanPackage o2) {
					return o1.getTitle().compareTo(o2.getTitle());
				}
			});

			cbPackages.setInput(packages);
			cbResources.setInput(packages.get(0).getResources());
			
			CkanPackage selectedPack = activity != null ? activity.getCkanPackage() : null;
			if (selectedPack != null && packages != null) {
				for (CkanPackage pack : packages) {
					if (pack.getId().equals(selectedPack.getId())) {
						cbPackages.setSelection(new StructuredSelection(pack));
						
						String resourceId = activity != null ? activity.getResourceId() : null;
						if (resourceId != null && !resourceId.isEmpty() && pack.getResources() != null) {
							for (CkanResource resource : pack.getResources()) {
								if (resource.getId().equals(resourceId)) {
									cbResources.setSelection(new StructuredSelection(resource));
									break;
								}
							}
						}
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openWarning(getPart().getSite().getShell(), Messages.GedSection_4, ex.getMessage());
		}

	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();

	}

	@Override
	public void refresh() {

		MetadataToD4CActivity activity = getActivity();
		if (activity.getCkanPackage() != null) {
			//			if (act.getCkanPackage().getId().startsWith("new_")) { //$NON-NLS-1$
			// txtNewPackage.setText(act.getCkanPackage().getName());
			// } else {
			// btnSelectPackage.setSelection(true);
			// btnNewPackage.setSelection(false);
			loadPackages(activity);
			// }
			//
			//
		}

		super.refresh();
	}

	private MetadataToD4CActivity getActivity() {
		return (MetadataToD4CActivity) node.getWorkflowObject();
	}
}
