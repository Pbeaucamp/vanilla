package bpm.gateway.ui.views.property.sections.database;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.dialogs.utils.DialogRelation;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.IDataStreamElement.SubType;
import bpm.metadata.layer.logical.IDataStreamElement.Type;
import bpm.metadata.layer.logical.sql.FactorySQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;

public class DBOutputFmdtSection extends AbstractPropertySection {

	private Text folderDestination;
	private Text fmdtName;
	private Button browseFolder;
	private CheckboxTreeViewer groups;

	private Text txtTableName, txtPackName;
	private Button btnPackNew, btnPackSelect;

	private ComboViewer lstCols, lstTypes, lstSubTypes, lstPacks;

	private Node node;

	private IRepositoryObject repositoryObject;
	private DataBaseOutputStream transfo;

	private Map<StreamElement, ElementType> colTypes = new HashMap<>();
	private StreamElement previousColumn;

	public DBOutputFmdtSection() {

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createGroup(parent, Messages.DBOutputFmdtSection_0);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		CLabel l2 = getWidgetFactory().createCLabel(composite, Messages.DBOutputFmdtSection_3);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		folderDestination = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		folderDestination.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		folderDestination.setEnabled(false);

		browseFolder = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browseFolder.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browseFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (Activator.getDefault().getRepositoryContext() == null) {
					DirectoryDialog dd = new DirectoryDialog(getPart().getSite().getShell());

					String path = dd.open();
					if (path != null) {
						((DataBaseOutputStream) node.getGatewayModel()).setDestinationFolder(path, false);
						folderDestination.setText(path);
					}
				}
				else {
					DialogRepositoryObject d = new DialogRepositoryObject(getPart().getSite().getShell(), IRepositoryApi.FMDT_TYPE);

					if (d.open() == DialogRepositoryObject.OK) {
						boolean isRepositoryItem = false;

						repositoryObject = d.getRepositoryDirectory();
						if (repositoryObject == null) {
							repositoryObject = d.getRepositoryItem();
							fmdtName.setText(repositoryObject.getName());
							fmdtName.setEnabled(false);

							btnPackSelect.setEnabled(true);
							try {
								String modelXml = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel((RepositoryItem) repositoryObject);
								MetaData metadata = null;
								MetaDataDigester dig = new MetaDataDigester(IOUtils.toInputStream(modelXml, "UTF-8"), new MetaDataBuilder(Activator.getDefault().getRepositoryConnection())); //$NON-NLS-1$
								metadata = dig.getModel(Activator.getDefault().getRepositoryConnection(), Activator.getDefault().getRepositoryConnection().getContext().getGroup().getName());

								for (IBusinessModel mod : metadata.getBusinessModels()) {
									for (IBusinessPackage p : mod.getBusinessPackages("none")) { //$NON-NLS-1$
										lstPacks.add(p);
									}
								}

							} catch (Exception e1) {
								e1.printStackTrace();
							}

							isRepositoryItem = true;
						}
						else {
							fmdtName.setEnabled(true);
						}
						((DataBaseOutputStream) node.getGatewayModel()).setDestinationFolder("" + repositoryObject.getId(), isRepositoryItem); //$NON-NLS-1$
						folderDestination.setText(repositoryObject.getName());

					}

				}
			}

		});

		CLabel l3 = getWidgetFactory().createCLabel(composite, Messages.DBOutputFmdtSection_9);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		fmdtName = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		fmdtName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		fmdtName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				((DataBaseOutputStream) node.getGatewayModel()).setDestinationName(fmdtName.getText());

			}

		});

		CLabel lblTableName = getWidgetFactory().createCLabel(composite, Messages.DBOutputFmdtSection_8);
		lblTableName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		txtTableName = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		txtTableName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		btnPackNew = getWidgetFactory().createButton(composite, Messages.DBOutputFmdtSection_10, SWT.RADIO);
		btnPackNew.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		btnPackNew.setSelection(true);
		btnPackNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtPackName.setEnabled(true);
				lstPacks.getCombo().setEnabled(false);
			}
		});

		txtPackName = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		txtPackName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		btnPackSelect = getWidgetFactory().createButton(composite, Messages.DBOutputFmdtSection_14, SWT.RADIO);
		btnPackSelect.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		btnPackSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtPackName.setEnabled(false);
				lstPacks.getCombo().setEnabled(true);
			}
		});
		btnPackSelect.setEnabled(false);

		lstPacks = new ComboViewer(composite, SWT.BORDER);
		lstPacks.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		lstPacks.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IBusinessPackage) element).getName();
			}
		});
		lstPacks.setContentProvider(new ArrayContentProvider());
		lstPacks.getCombo().setEnabled(false);

		CLabel lblColName = getWidgetFactory().createCLabel(composite, Messages.DBOutputFmdtSection_15);
		lblColName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		CLabel lblTypeName = getWidgetFactory().createCLabel(composite, Messages.DBOutputFmdtSection_16);
		lblTypeName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		CLabel lblSubtypeName = getWidgetFactory().createCLabel(composite, Messages.DBOutputFmdtSection_17);
		lblSubtypeName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		lstCols = new ComboViewer(composite, SWT.BORDER);
		lstCols.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		lstCols.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((StreamElement) element).name;
			}
		});
		lstCols.setContentProvider(new ArrayContentProvider());
		lstCols.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// set info for previous
				ElementType eType;
				try {
					eType = new ElementType();

					eType.setType((Type) ((IStructuredSelection) lstTypes.getSelection()).getFirstElement());
					switch (eType.getType()) {
					case DATE:
					case GEO:
					case MEASURE:
					case UNDEFINED:
						eType.setSubType((SubType) ((IStructuredSelection) lstSubTypes.getSelection()).getFirstElement());
						break;
					case DIMENSION:
						eType.setSubType(SubType.DIMENSION);
						eType.setColParent((StreamElement) ((IStructuredSelection) lstSubTypes.getSelection()).getFirstElement());
						break;
					case PROPERTY:
						eType.setSubType(SubType.DIMENSION);
						eType.setColParent((StreamElement) ((IStructuredSelection) lstSubTypes.getSelection()).getFirstElement());
						break;
					}

					colTypes.put(previousColumn, eType);
				} catch (Exception e) {
					lstTypes.setSelection(new StructuredSelection(Type.UNDEFINED), true);
					lstSubTypes.setSelection(new StructuredSelection(SubType.UNDEFINED), true);
				}

				// get info for new column
				previousColumn = (StreamElement) ((IStructuredSelection) lstCols.getSelection()).getFirstElement();
				if (previousColumn == null) {
					lstTypes.setSelection(new StructuredSelection(Type.UNDEFINED), true);
					lstSubTypes.setSelection(new StructuredSelection(SubType.UNDEFINED), true);
				}
				else {
					eType = colTypes.get(previousColumn);
					if (eType != null) {
						lstTypes.setSelection(new StructuredSelection(eType.getType()), true);
						switch (eType.getType()) {
						case DATE:
						case GEO:
						case MEASURE:
						case UNDEFINED:
							lstSubTypes.setSelection(new StructuredSelection(eType.getSubType()), true);
							break;
						case DIMENSION:
						case PROPERTY:
							lstSubTypes.setSelection(new StructuredSelection(eType.getColParent()), true);
							break;
						}
					}
				}

			}
		});

		lstTypes = new ComboViewer(composite, SWT.BORDER);
		lstTypes.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		lstTypes.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Type) element).toString();
			}
		});
		lstTypes.setContentProvider(new ArrayContentProvider());
		lstTypes.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Type type = (Type) ((IStructuredSelection) lstTypes.getSelection()).getFirstElement();
				switch (type) {
				case DATE:
				case GEO:
				case MEASURE:
				case UNDEFINED:
					lstSubTypes.setInput(type.getSubtypes());
					break;
				case DIMENSION:
				case PROPERTY:
					try {
						lstSubTypes.setInput(transfo.getDescriptor(null).getStreamElements());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});
		List<Type> types = new ArrayList<>();
		types.add(Type.UNDEFINED);
		types.add(Type.DIMENSION);
		types.add(Type.GEO);
		types.add(Type.DATE);
		types.add(Type.MEASURE);
		types.add(Type.PROPERTY);
		lstTypes.setInput(types);

		lstSubTypes = new ComboViewer(composite, SWT.BORDER);
		lstSubTypes.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		lstSubTypes.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof SubType) {
					return ((SubType) element).toString();
				}
				else {
					return ((StreamElement) element).name;
				}
			}
		});
		lstSubTypes.setContentProvider(new ArrayContentProvider());
		List<SubType> subtypes = new ArrayList<>();
		subtypes.add(SubType.UNDEFINED);
		lstSubTypes.setInput(subtypes);

		/*
		 * Security
		 */
		Composite scomposite = getWidgetFactory().createGroup(parent, Messages.DBOutputFmdtSection_11);
		scomposite.setLayout(new GridLayout());
		scomposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		groups = new CheckboxTreeViewer(scomposite, SWT.FLAT | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		groups.getControl().setBackground(scomposite.getBackground());
		groups.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				return new String((String) element);
			}

		});
		groups.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof Secu) {
					return ((Secu) parentElement).getChilds().toArray(new String[((Secu) parentElement).getChilds().size()]);
				}
				return null;
			}

			public Object getParent(Object element) {
				if (element instanceof String) {
					return groups.getInput();
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				if (element instanceof Secu) {
					return true;
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				return ((Secu) inputElement).getChilds().toArray(new String[((Secu) inputElement).getChilds().size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});

		groups.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					((DataBaseOutputStream) node.getGatewayModel()).addSecurizedGroup(((String) event.getElement()));
				}
				else {
					((DataBaseOutputStream) node.getGatewayModel()).removeSecurizedGroup(((String) event.getElement()));
				}

			}

		});

		Button generateFmdt = getWidgetFactory().createButton(parent, Messages.DBOutputFmdtSection_12, SWT.PUSH);
		generateFmdt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		generateFmdt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String tableName = txtTableName.getText();
					boolean newPack = btnPackNew.getSelection();
					String packName = txtPackName.getText();
					
					if (tableName == null || tableName.isEmpty()) {
						MessageDialog.openError(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_20, Messages.DBOutputFmdtSection_21);
						return;
					}
					if (newPack && (packName == null || packName.isEmpty())) {
						MessageDialog.openError(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_22, Messages.DBOutputFmdtSection_25);
						return;
					}
					
					TypeSave typeSave = generateFmdt();

					String title = Messages.DBOutputFmdtSection_6;
					String message = null;

					switch (typeSave) {
					case FILE:
						message = Messages.DBOutputFmdtSection_7;
						break;
					case SAVE_ON_REPOSITORY:
						message = Messages.DBOutputFmdtSection_18;
						break;
					case UPDATE_ON_REPOSITORY:
						message = Messages.DBOutputFmdtSection_19;
						break;
					case FAILED:
					default:
						break;
					}

					if (message != null) {
						MessageDialog.openInformation(getPart().getSite().getShell(), title, message);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openWarning(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_13, e1.getMessage());
				}
			}

		});
		
		Button addRelation = getWidgetFactory().createButton(parent, Messages.DBOutputFmdtSection_26, SWT.PUSH);
		addRelation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		addRelation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(metadataId != null) {
					try {
						RepositoryItem item = Activator.getDefault().getRepositoryConnection().getRepositoryService().getDirectoryItem(metadataId);
						String modelXml = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel(item);
						MetaData metadata = null;
						
						MetaDataDigester dig = new MetaDataDigester(IOUtils.toInputStream(modelXml, "UTF-8"), new MetaDataBuilder(Activator.getDefault().getRepositoryConnection())); //$NON-NLS-1$
						metadata = dig.getModel(Activator.getDefault().getRepositoryConnection(), Activator.getDefault().getRepositoryConnection().getContext().getGroup().getName());

						SQLDataSource sds = (SQLDataSource) metadata.getDataSources().iterator().next();
							
						DialogRelation rel = new DialogRelation(getPart().getSite().getShell(), sds);
						if(rel.open() == Dialog.OK) {
							sds.addRelation(rel.getRelation());
							LOOP: for (IBusinessModel mod : metadata.getBusinessModels()) {
								for (IBusinessPackage p : mod.getBusinessPackages("none")) { //$NON-NLS-1$
									if(lstPacks.getCombo().isEnabled()) {
										if (p.getName().equals(((IBusinessPackage) ((IStructuredSelection) lstPacks.getSelection()).getFirstElement()).getName())) {
											((BusinessModel)mod).addRelation(rel.getRelation());
											break LOOP;
										}
									}
									else {
										if (p.getName().equals(txtPackName.getText())) {
											((BusinessModel)mod).addRelation(rel.getRelation());
											break LOOP;
										}
									}
								}
							}
							Activator.getDefault().getRepositoryConnection().getRepositoryService().updateModel(item, metadata.getXml(false));
						}
						String title = Messages.DBOutputFmdtSection_6;
						String message = Messages.DBOutputFmdtSection_19;
						MessageDialog.openInformation(getPart().getSite().getShell(), title, message);
						
					} catch(Exception e1) {
						e1.printStackTrace();
						MessageDialog.openWarning(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_13, e1.getMessage());
					}
				}
			}
		});

		initSelectables();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.node = (Node) ((NodePart) input).getModel();
		this.transfo = (DataBaseOutputStream) node.getGatewayModel();
	}

	@Override
	public void refresh() {

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				initSelectables();
				DataBaseOutputStream tr = (DataBaseOutputStream) node.getGatewayModel();

				if (Activator.getDefault().getRepositoryContext() != null) {
					if (tr.getDestinationFolder() != null) {

						IRepositoryService service = Activator.getDefault().getRepositoryConnection().getRepositoryService();
						try {
							if (tr.isRepositoryItem()) {
								repositoryObject = service.getDirectoryItem(Integer.parseInt(tr.getDestinationFolder()));
							}
							else {
								repositoryObject = service.getDirectory(Integer.parseInt(tr.getDestinationFolder()));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						folderDestination.setText(repositoryObject.getName());
					}
					else {
						folderDestination.setText(""); //$NON-NLS-1$
					}

					fmdtName.setText(tr.getDestinationName() != null ? tr.getDestinationName() : ""); //$NON-NLS-1$
				}
				else {
					MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_1, Messages.DBOutputFmdtSection_2);
				}

				for (String s : ((Secu) groups.getInput()).getChilds()) {
					for (String _s : tr.getSecurizedGroup()) {
						if (s.equals(_s)) {
							groups.setChecked(s, true);

						}
					}
				}
				// groups.getCheckedElements();
				groups.refresh();

				try {
					lstCols.setInput(transfo.getDescriptor(null).getStreamElements());
					previousColumn = transfo.getDescriptor(null).getStreamElements().get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	private void initSelectables() {

		Secu groupNames = new Secu();

		// String prefUrl =
		// ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE,
		// ResourceManager.VAR_PREFERENCES_SERVER).getValueAsString();
		try {
			IVanillaContext c = Activator.getDefault().getVanillaContext();
			if (c != null) {
				IVanillaAPI vanillaApi = new RemoteVanillaPlatform(c);
				for (Group g : vanillaApi.getVanillaSecurityManager().getGroups()) {
					groupNames.add(g.getName());
				}
			}
			else {
				MessageDialog.openInformation(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_4, Messages.DBOutputFmdtSection_5);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		groups.setInput(groupNames);

	}

	private class Secu {
		private List<String> name = new ArrayList<String>();

		public List<String> getChilds() {
			return name;
		}

		public void add(String name) {
			this.name.add(name);
		}
	}

	private Integer metadataId;
	
	private TypeSave generateFmdt() throws Exception {

		ElementType eType = new ElementType();

		eType.setType((Type) ((IStructuredSelection) lstTypes.getSelection()).getFirstElement());
		try {
			switch (eType.getType()) {
			case DATE:
			case GEO:
			case MEASURE:
			case UNDEFINED:
				eType.setSubType((SubType) ((IStructuredSelection) lstSubTypes.getSelection()).getFirstElement());
				break;
			case DIMENSION:
				eType.setSubType(SubType.DIMENSION);
				eType.setColParent((StreamElement) ((IStructuredSelection) lstSubTypes.getSelection()).getFirstElement());
				break;
			case PROPERTY:
				eType.setSubType(SubType.DIMENSION);
				eType.setColParent((StreamElement) ((IStructuredSelection) lstSubTypes.getSelection()).getFirstElement());
				break;
			}
		} catch (Exception e) {
			eType.setType(Type.UNDEFINED);
			eType.setSubType(SubType.UNDEFINED);
		}

		colTypes.put(previousColumn, eType);

		DataBaseOutputStream t = (DataBaseOutputStream) node.getGatewayModel();

		String fmdtXml = generateFmdtXml();

		if (Activator.getDefault().getRepositoryContext() == null) {
			String fileName = t.getDocument().getStringParser().getValue(t.getDocument(), t.getDestinationFolder()) + "/" + t.getDocument().getStringParser().getValue(t.getDocument(), t.getDestinationName() + ".freemetadata"); //$NON-NLS-1$ //$NON-NLS-2$

			PrintWriter pw = new PrintWriter(fileName, "UTF-8"); //$NON-NLS-1$
			pw.write(fmdtXml);
			pw.close();

			return TypeSave.FILE;
		}
		else {

			IRepositoryApi sock = Activator.getDefault().getRepositoryConnection();
			RepositoryDirectory dir = null;
			if (repositoryObject instanceof RepositoryItem) {
				dir = sock.getRepositoryService().getDirectory(((RepositoryItem) repositoryObject).getDirectoryId());
			}
			else {
				dir = sock.getRepositoryService().getDirectory(Integer.parseInt(t.getDestinationFolder()));
			}

			if (t.getSecurizedGroup().isEmpty()) {
				MessageDialog.openWarning(getPart().getSite().getShell(), Messages.DBOutputFmdtSection_23, Messages.DBOutputFmdtSection_24);
				return TypeSave.FAILED;
			}

			if (repositoryObject instanceof RepositoryItem) {
				metadataId = repositoryObject.getId();
				sock.getRepositoryService().updateModel((RepositoryItem) repositoryObject, fmdtXml);
				return TypeSave.UPDATE_ON_REPOSITORY;
			}
			else {
				RepositoryItem item = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FMDT_TYPE, -1, dir, t.getDestinationName(), "", //$NON-NLS-1$
						"", //$NON-NLS-1$
					"", //$NON-NLS-1$
						fmdtXml, true);
				metadataId = item.getId();

				return TypeSave.SAVE_ON_REPOSITORY;
			}
		}
	}

	private enum TypeSave {
		FAILED, FILE, SAVE_ON_REPOSITORY, UPDATE_ON_REPOSITORY
	}

	private String generateFmdtXml() throws Exception {
		DataBaseOutputStream tr = (DataBaseOutputStream) node.getGatewayModel();

		String tableName = txtTableName.getText();

		if (repositoryObject instanceof RepositoryItem) {

			String modelXml = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel((RepositoryItem) repositoryObject);
			MetaData metadata = null;
			try {
				MetaDataDigester dig = new MetaDataDigester(IOUtils.toInputStream(modelXml, "UTF-8"), new MetaDataBuilder(Activator.getDefault().getRepositoryConnection())); //$NON-NLS-1$
				metadata = dig.getModel(Activator.getDefault().getRepositoryConnection(), Activator.getDefault().getRepositoryConnection().getContext().getGroup().getName());
				IBusinessPackage ipack = null;
				SQLDataSource sds = (SQLDataSource) metadata.getDataSources().iterator().next();
				IDataStream str = sds.add(sds.getConnection().getTable(tr.getTableName()));

				if (btnPackSelect.getSelection()) {
					LOOP: for (IBusinessModel mod : metadata.getBusinessModels()) {
						for (IBusinessPackage p : mod.getBusinessPackages("none")) { //$NON-NLS-1$
							if (p.getName().equals(((IBusinessPackage) ((IStructuredSelection) lstPacks.getSelection()).getFirstElement()).getName())) {
								ipack = p;
								break LOOP;
							}
						}
					}

					SQLBusinessTable bt = createBusinessTable(tr, str, tableName);
					((BusinessModel) ipack.getBusinessModel()).addBusinessTable(bt);
					ipack.addBusinessTable(bt);
					
					for (String s : tr.getSecurizedGroup()) {
						bt.setGranted(s, true);

						for (IDataStreamElement e : bt.getOrders()) {
							e.setGranted(s, true);
							e.setVisible(s, true);
						}
					}

				}
				else {
					BusinessModel model = createModel(str, tr, tableName);

					metadata.addBusinessModel(model);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			return metadata.getXml(false);
		}
		else {
			DataBaseServer ds = (DataBaseServer) tr.getServer();
			DataBaseConnection dbc = (DataBaseConnection) ds.getCurrentConnection(null);

			SQLConnection scon = new SQLConnection();
			scon.setDataBaseName(dbc.getDataBaseName());
			scon.setDriverName(dbc.getDriverName());
			scon.setHost(dbc.getHost());
			scon.setName(dbc.getName());
			scon.setPassword(dbc.getPassword());
			scon.setPortNumber(dbc.getPort());
			scon.setUsername(dbc.getLogin());

			MetaData fmdt = new MetaData();

			SQLDataSource sds = FactorySQLDataSource.getInstance().createDataSource(scon);
			for (String s : tr.getSecurizedGroup()) {
				sds.securizeConnection(dbc.getName(), s, true);
			}
			sds.setName(ds.getName());
			sds.getConnection().connect();
			fmdt.addDataSource(sds);
			IDataStream str = sds.add(sds.getConnection().getTable(tr.getTableName()));

			BusinessModel model = createModel(str, tr, tableName);

			fmdt.addBusinessModel(model);

			return fmdt.getXml(false);
		}

	}

	private BusinessModel createModel(IDataStream str, DataBaseOutputStream tr, String tableName) throws Exception {
		BusinessModel model = new BusinessModel();
		model.setName(txtPackName.getText());

		SQLBusinessTable bt = createBusinessTable(tr, str, tableName);

		model.addBusinessTable(bt);

		BusinessPackage p = new BusinessPackage();
		p.setName(txtPackName.getText());
		p.addBusinessTable(bt);
		model.addBusinessPackage(p);

		for (String s : tr.getSecurizedGroup()) {
			p.setGranted(s, true);
			model.setGranted(s, true);
			bt.setGranted(s, true);

			for (IDataStreamElement e : bt.getOrders()) {
				e.setGranted(s, true);
				e.setVisible(s, true);
			}
		}

		return model;
	}

	private SQLBusinessTable createBusinessTable(DataBaseOutputStream tr, IDataStream str, String tableName) throws Exception {
		SQLBusinessTable bt = new SQLBusinessTable(tableName);

		for (StreamElement col : tr.getDescriptor(null).getStreamElements()) {
			for (IDataStreamElement e : str.getElements()) {
				if (e.getOrigin().getName().equals(col.name) || e.getOrigin().getName().endsWith("." + col.name)) { //$NON-NLS-1$
					for (StreamElement k : colTypes.keySet()) {
						if (k.name.equals(col.name)) {
							ElementType t = colTypes.get(k);
							switch (t.getType()) {
							case DATE:
							case GEO:
							case MEASURE:
							case UNDEFINED:
								e.setType(t.getSubType());
								break;
							case DIMENSION:
								e.setType(t.getSubType());
								if (t.getColParent() != null) {
									e.setParentDimension(t.getColParent().name);
								}
								break;
							case PROPERTY:
								e.setType(t.getSubType());
								if (t.getColParent() != null) {
									e.setParentDimension(t.getColParent().name);
								}
								break;
							}
							break;
						}
					}

					bt.addColumn(e);
					break;
				}
			}
		}
		return bt;
	}

	public class ElementType {
		private Type type;
		private SubType subType;
		private StreamElement colParent;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public SubType getSubType() {
			return subType;
		}

		public void setSubType(SubType subType) {
			this.subType = subType;
		}

		public StreamElement getColParent() {
			return colParent;
		}

		public void setColParent(StreamElement colParent) {
			this.colParent = colParent;
		}
	}
}
