package bpm.gateway.ui.views.property.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.composites.CompositeFmdtQuery;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FmdtInputSection extends AbstractPropertySection {

	private Button browseRepository;
	private Text directoryItemName;
	private CCombo queries;
	private CCombo businessModels;
	private CCombo businessPackages;
	private CCombo connectionNames;

	private CompositeFmdtQuery queryComposite;

	private Node node;
	private RepositoryItem item;
	private Collection<IBusinessModel> fmdtModels;
	private Collection<IBusinessPackage> fmdtPackages;

	private IBusinessPackage pack;
	
	private Map<String, SavedQuery> savedQueries = new HashMap<>();
	
	private PropertyChangeListener listener = new PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(CompositeFmdtQuery.QUERY_CHANGED)) {

				IQuery q = queryComposite.getQuery();
				((FMDTInput) node.getGatewayModel()).setDefinition(q.getXml());

				((FMDTInput) node.getGatewayModel()).setPromptValues(queryComposite.getPromptValues());
			}

		}

	};

	public FmdtInputSection() {
		
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.FmdtInputSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		directoryItemName = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		directoryItemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		browseRepository = getWidgetFactory().createButton(composite, Messages.FmdtInputSection_2, SWT.PUSH);
		browseRepository.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browseRepository.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogRepositoryObject dial = new DialogRepositoryObject(getPart().getSite().getShell(), IRepositoryApi.FMDT_TYPE);

				if (dial.open() == Dialog.OK) {

					// queryComposite.setVisible(false);
					item = dial.getRepositoryItem();
					directoryItemName.setText(item.getItemName());
					((FMDTInput) node.getGatewayModel()).setRepositoryItemId(item.getId());
					fillComboModel();

				}

			}

		});

		Label lQuery = getWidgetFactory().createLabel(composite, Messages.FmdtInputSection_19);
		lQuery.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		queries = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		queries.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		queries.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//XXX
				
				 SavedQuery q = savedQueries.get(queries.getText());

				 LOOP:for (IBusinessModel m : fmdtModels) {
					 for(IBusinessPackage p : m.getBusinessPackages("none")) { //$NON-NLS-1$
						 for(SavedQuery qu : p.getSavedQueries()) {
							 if(qu.getName().equals(q.getName())) {
								 pack = p;
								 break LOOP;
							 }
						 }
					 }
				 }
				 try {
					QuerySql sql = q.loadQuery("none", pack); //$NON-NLS-1$
					queryComposite.populateTree(pack);
					queryComposite.setQuery(sql, ((FMDTInput) node.getGatewayModel()).getPromptValues());
					directoryItemName.setText(item.getItemName());
					businessModels.select(new ArrayList<>(fmdtModels).indexOf(pack.getBusinessModel()));
					for (IBusinessModel m : fmdtModels) {
						if (m.getName().equals(businessModels.getText())) {
							fmdtPackages = m.getBusinessPackages("none"); //$NON-NLS-1$
							break;
						}
					}
					businessPackages.select(new ArrayList<>(fmdtPackages).indexOf(pack));
					
					IQuery qa = queryComposite.getQuery();
					((FMDTInput) node.getGatewayModel()).setBusinessModelName(businessModels.getText());
					((FMDTInput) node.getGatewayModel()).setBusinessPackageName(pack.getName());
					((FMDTInput) node.getGatewayModel()).setConnectionName("Default"); //$NON-NLS-1$
					((FMDTInput) node.getGatewayModel()).setRepositoryItemId(item.getId());
					((FMDTInput) node.getGatewayModel()).setDefinition(qa.getXml());
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}

		});
		
		Label l2 = getWidgetFactory().createLabel(composite, Messages.FmdtInputSection_3);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		businessModels = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		businessModels.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		businessModels.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((FMDTInput) node.getGatewayModel()).setBusinessModelName(businessModels.getText());
				fillPackageModel();
			}

		});

		Label l3 = getWidgetFactory().createLabel(composite, Messages.FmdtInputSection_4);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		businessPackages = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		businessPackages.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		businessPackages.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				for (IBusinessPackage p : fmdtPackages) {
					if (p.getName().equals(businessPackages.getText())) {
						pack = p;
						queryComposite.populateTree(p);
						queryComposite.setVisible(true);
						((FMDTInput) node.getGatewayModel()).setBusinessPackageName(pack.getName());
						try {
							fillConnections();
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getPart().getSite().getShell(), Messages.FmdtInputSection_1, Messages.FmdtInputSection_8 + ex.getMessage());
						}
						break;
					}
				}

			}

		});

		Label l8 = getWidgetFactory().createLabel(composite, Messages.FmdtInputSection_5);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		connectionNames = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		connectionNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		connectionNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((FMDTInput) node.getGatewayModel()).setConnectionName(connectionNames.getText());
			}
		});

		queryComposite = new CompositeFmdtQuery(composite, SWT.NONE);
		queryComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		queryComposite.setVisible(true);
		queryComposite.addListener(listener);

		Composite compButton = new Composite(composite, SWT.NONE);
		compButton.setLayout(new GridLayout(2, true));
		compButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		Button browse = new Button(compButton, SWT.PUSH);
		browse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		browse.setText(Messages.FmdtInputSection_6);
		browse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FMDTInput transfo = (FMDTInput) node.getGatewayModel();

				IBusinessPackage pack = null;
				
				IQuery query = transfo.getQueryFmdt();

				try {
					for (IBusinessPackage p : fmdtPackages) {
						if (p.getName().equals(transfo.getBusinessPackageName())) {
							pack = p;
							break;
						}
					}

					List<List<String>> promptValues = new ArrayList<List<String>>();

					for (Prompt p : query.getPrompts()) {
						List<String> v = new ArrayList<String>();
						for (String s : transfo.getPromptValues(p.getName())) {
							v.add(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), s));
						}

						promptValues.add(v);

					}
					query.setLimit(100);

					List<List<Object>> fmdtValues = (List) pack.executeQuery(null, null, transfo.getConnectioName(), query, promptValues);
					DialogBrowseContent dial = new DialogBrowseContent(getPart().getSite().getShell(), fmdtValues, transfo.getDescriptor(transfo).getStreamElements());
					dial.open();
					
					query.setLimit(0);
				} catch (Exception e1) {
					query.setLimit(0);
					
					e1.printStackTrace();
					MessageDialog.openError(getPart().getSite().getShell(), Messages.FmdtInputSection_7, e1.getMessage());
				}
			}
		});

		Button browseDistinct = new Button(compButton, SWT.PUSH);
		browseDistinct.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		browseDistinct.setText(Messages.FmdtInputSection_18);
		browseDistinct.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FMDTInput transfo = (FMDTInput) node.getGatewayModel();
				Shell sh = getPart().getSite().getShell();

				try {
					DialogFieldsValues dial = new DialogFieldsValues(sh, transfo, fmdtPackages, transfo.getDescriptor(transfo).getStreamElements());
					dial.open();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getPart().getSite().getShell(), Messages.FmdtInputSection_7, e1.getMessage());
				}
			}
		});
	}

	@Override
	public void refresh() {
		final FMDTInput transfo = (FMDTInput) node.getGatewayModel();

		if (Activator.getDefault().getRepositoryContext() == null) {
			MessageDialog.openInformation(getPart().getSite().getShell(), Messages.FmdtInputSection_15, Messages.FmdtInputSection_17);
			return;
		}

		if (transfo.getRepositoryItemId() == null) {

			businessModels.setItems(new String[] {});
			businessPackages.setItems(new String[] {});
			connectionNames.setItems(new String[] {});
			directoryItemName.setText(""); //$NON-NLS-1$
			item = null;
			pack = null;
			queryComposite.populateTree(pack);
			queryComposite.setQuery(((FMDTInput) node.getGatewayModel()).getQueryFmdt(), ((FMDTInput) node.getGatewayModel()).getPromptValues());

			return;
		}

		/*
		 * load the Repository Item
		 */
		IRunnableWithProgress r = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (item == null || transfo.getRepositoryItemId() != item.getId()) {
					try {
						monitor.setTaskName(Messages.FmdtInputSection_9);
						monitor.beginTask(Messages.FmdtInputSection_10, 3);

						monitor.subTask(Messages.FmdtInputSection_11);
						IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();
						item = sock.getRepositoryService().getDirectoryItem(transfo.getRepositoryItemId());

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								directoryItemName.setText(item.getItemName());
							}
						});

						if (transfo.getBusinessModelName() != null) {
							monitor.subTask(Messages.FmdtInputSection_12);
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									setBusinessModel();
								}
							});

							if (transfo.getBusinessPackageName() != null) {
								monitor.subTask(Messages.FmdtInputSection_13);
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										setBusinessPackage();
									}
								});
							}

							if (transfo.getDefinition() != null) {
								monitor.subTask(Messages.FmdtInputSection_14);
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										setQuery();
									}
								});
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					/*
					 * fillComboModel
					 */
					if (transfo.getBusinessModelName() != null && !transfo.getBusinessModelName().equals(businessModels.getText())) {
						setBusinessModel();

						if (transfo.getBusinessPackageName() != null && !transfo.getBusinessPackageName().equals(businessPackages.getText())) {
							setBusinessPackage();
						}

						if (transfo.getDefinition() != null) {
							setQuery();
						}
					}

				}
			}
		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		// try {
		// service.run(true, false, r);
		// } catch (InvocationTargetException e) {
		// // Operation was canceled
		// } catch (InterruptedException e) {
		// // Handle the wrapped exception
		// }

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {

					if ((item == null && transfo.getRepositoryItemId() != null) || (item != null && item.getId() != transfo.getRepositoryItemId().intValue())) {
						IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();

						item = sock.getRepositoryService().getDirectoryItem(transfo.getRepositoryItemId());
					}

					directoryItemName.setText(item.getItemName());
					setBusinessModel();
					setBusinessPackage();
					setQuery();
				} catch (Exception e) {

				}

			}
		});

	}

	private void setQuery() {
		FMDTInput transfo = (FMDTInput) node.getGatewayModel();

		if (pack != null && transfo.getDefinition() != null) {

			try {
				QuerySql q = new SqlQueryDigester(IOUtils.toInputStream(transfo.getDefinition()), "none", pack).getModel(); //$NON-NLS-1$
				queryComposite.setQuery(q, transfo.getPromptValues());
			} catch (Exception e) {
				
				e.printStackTrace();
			}

		}

	}

	private void setBusinessPackage() {
		fillPackageModel();
		FMDTInput transfo = (FMDTInput) node.getGatewayModel();

		int i = 0;
		for (String s : businessPackages.getItems()) {
			if (s.equals(transfo.getBusinessPackageName())) {
				businessPackages.select(i);

				for (IBusinessPackage pp : fmdtPackages) {
					if (pp.getName().equals(s)) {
						pack = pp;
						try {
							fillConnections();
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						break;
					}
				}
				break;

			}
			else {
				i++;
			}
		}

	}

	private void setBusinessModel() {
		FMDTInput transfo = (FMDTInput) node.getGatewayModel();

		if (fmdtModels == null) {
			fillComboModel();
		}

		int i = 0;
		for (String s : businessModels.getItems()) {
			if (s.equals(transfo.getBusinessModelName())) {
				businessModels.select(i);
			}
			else {
				i++;
			}
		}

	}

	private void fillComboModel() {
		try {
			savedQueries.clear();
			fmdtModels = node.getGatewayModel().getDocument().getFMDTHelper().getFmdtModel((FMDTInput) node.getGatewayModel());
			List<String> modelsNames = new ArrayList<String>();
			for (IBusinessModel m : fmdtModels) {
				modelsNames.add(m.getName());
				for(IBusinessPackage p : m.getBusinessPackages(node.getGatewayModel().getDocument().getRepositoryContext().getGroup().getName())) {
					for(SavedQuery q : p.getSavedQueries()) {
						savedQueries.put(q.getName(), q);
					}
 				}
			}

			businessModels.setItems(modelsNames.toArray(new String[modelsNames.size()]));
			businessPackages.setItems(new String[] {});
			queries.setItems(savedQueries.keySet().toArray(new String[savedQueries.size()]));
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getPart().getSite().getShell(), Messages.FmdtInputSection_16, e.getMessage());
		}
	}

	private void fillConnections() throws Exception {
		FMDTInput transfo = (FMDTInput) node.getGatewayModel();
		IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();
		String g = vanillaApi.getVanillaSecurityManager().getGroups().iterator().next().getName();

		List<String> l = pack.getConnectionsNames(g);
		connectionNames.setItems(l.toArray(new String[l.size()]));

		for (int j = 0; j < connectionNames.getItemCount(); j++) {
			if (connectionNames.getItem(j).equals(transfo.getConnectioName())) {
				connectionNames.select(j);
				break;
			}
		}
	}

	private void fillPackageModel() {

		for (IBusinessModel m : fmdtModels) {
			if (m.getName().equals(businessModels.getText())) {
				fmdtPackages = m.getBusinessPackages("none"); //$NON-NLS-1$
				break;
			}
		}

		List<String> names = new ArrayList<String>();

		for (IBusinessPackage p : fmdtPackages) {
			names.add(p.getName());
			if (p.getName().equals(((FMDTInput) node.getGatewayModel()).getBusinessPackageName())) {
				pack = p;
				try {
					fillConnections();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				queryComposite.populateTree(pack);

				if (((FMDTInput) node.getGatewayModel()).getDefinition() != null && !((FMDTInput) node.getGatewayModel()).equals("")) { //$NON-NLS-1$

					queryComposite.setQuery(((FMDTInput) node.getGatewayModel()).getQueryFmdt(), ((FMDTInput) node.getGatewayModel()).getPromptValues());
				}

				queryComposite.setVisible(true);

			}
		}

		businessPackages.setItems(names.toArray(new String[fmdtPackages.size()]));

	}
}
