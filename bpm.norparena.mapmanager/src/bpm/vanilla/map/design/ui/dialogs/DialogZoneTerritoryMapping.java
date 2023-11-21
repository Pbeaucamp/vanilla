package bpm.vanilla.map.design.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.FactoryManagerException;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.utils.Tools;
import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IZoneTerritoryMapping;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;
import bpm.vanilla.map.model.impl.ZoneTerritoryMapping;

/**
 * A dialog in which we can create mapping between map zone and FM territories (applications)
 * 
 * @author Marc Lanquetin
 *
 */
public class DialogZoneTerritoryMapping extends Dialog {

	private IMapDefinition mapDef;
	
	private Text txtUser, txtPass;
	private Button btnConnect;
	
	private ComboViewer comboGroups;
	
	private TableViewer mappingTable;
	
	private IManager fmManager;
	private List<Group> fmGroups;
	private List<Application> fmApplications;
	
	private List<IZoneTerritoryMapping> existingsMapping;
	
	private ComboBoxCellEditor territoryEditor;
	
	private HashMap<Object, Integer> selections = new HashMap<Object, Integer>();
	
	public DialogZoneTerritoryMapping(Shell parentShell, IMapDefinition mapDef) {
		super(parentShell);
		
		this.mapDef = mapDef;
		this.setShellStyle(SWT.SHELL_TRIM);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblUser = new Label(mainComposite, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUser.setText(Messages.DialogZoneTerritoryMapping_0);
		
		txtUser = new Text(mainComposite, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblPass = new Label(mainComposite, SWT.NONE);
		lblPass.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblPass.setText(Messages.DialogZoneTerritoryMapping_1);
		
		txtPass = new Text(mainComposite, SWT.PASSWORD|SWT.BORDER);
		txtPass.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		btnConnect = new Button(mainComposite, SWT.PUSH);
		btnConnect.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		btnConnect.setText(Messages.DialogZoneTerritoryMapping_2);
		btnConnect.addSelectionListener(new ConnectionListener());
		
		Label lblGroups = new Label(mainComposite, SWT.NONE);
		lblGroups.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblGroups.setText(Messages.DialogZoneTerritoryMapping_3);
		
		comboGroups = new ComboViewer(mainComposite, SWT.DROP_DOWN);
		comboGroups.getControl().setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));
		comboGroups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				Group grp = (Group) element;
				return grp.getName();
			}
			
		});
		comboGroups.setContentProvider(new GroupComboContentProvider());
		comboGroups.addSelectionChangedListener(new GroupSelectionListener());
		
		mappingTable = new TableViewer(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		mappingTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		mappingTable.getTable().setHeaderVisible(true);
		mappingTable.getTable().setLinesVisible(true);
		mappingTable.getTable().setSize(550, 400);
		mappingTable.setContentProvider(new MappingTableContentProvider());
		
		
		
		TableViewerColumn zoneColumn = new TableViewerColumn(mappingTable, SWT.LEFT);
		zoneColumn.getColumn().setText(Messages.DialogZoneTerritoryMapping_4);
		zoneColumn.getColumn().setWidth(250);
		zoneColumn.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof IFusionMapSpecificationEntity) {
					return ((IFusionMapSpecificationEntity) element).getFusionMapLongName();
				}
				else {
					return ((IOpenLayersMapSpecificationEntity) element).getLongName();
				}
			}
			
		});
		
		TableViewerColumn territoryColumn = new TableViewerColumn(mappingTable, SWT.LEFT);
		territoryColumn.getColumn().setText(Messages.DialogZoneTerritoryMapping_5);
		territoryColumn.getColumn().setWidth(250);
		territoryColumn.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if(selections.get(element) != null) {
					return territoryEditor.getItems()[selections.get(element)];
				}
				return "-NONE-"; //$NON-NLS-1$
			}
			
		});
		
		territoryEditor = new ComboBoxCellEditor(mappingTable.getTable(), new String[]{""}); //$NON-NLS-1$
		
		territoryColumn.setEditingSupport(new EditingSupport(mappingTable) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof IFusionMapSpecificationEntity) {
					selections.put((IFusionMapSpecificationEntity) element, (Integer)value);
				}
				else {
					selections.put((IOpenLayersMapSpecificationEntity) element, (Integer)value);
				}
				mappingTable.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				if(selections.get(element) != null) {
					return selections.get(element);
				}
				return 0;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return territoryEditor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		return mainComposite;
	}

	@Override
	protected void okPressed() {
		
		List<IZoneTerritoryMapping> mappings = new ArrayList<IZoneTerritoryMapping>();
		List<IZoneTerritoryMapping> toRemove = new ArrayList<IZoneTerritoryMapping>();
		
		if(mapDef.getFusionMapObject() != null) {
		
			for(Object e : selections.keySet()) {
				IFusionMapSpecificationEntity ent = (IFusionMapSpecificationEntity) e;
				if(selections.get(ent) == 0) {
					if(existingsMapping != null) {
						for(IZoneTerritoryMapping ma : existingsMapping) {
							if(ma.getMapZoneId() == ent.getId()) {
								toRemove.add(ma);
								break;
							}
						}
					}
				}
				else {
					IZoneTerritoryMapping mapp = new ZoneTerritoryMapping();
					mapp.setMapZoneId((int)ent.getId());
					mapp.setMapId(mapDef.getId());
					Application appli = fmApplications.get(selections.get(ent) - 1);
					mapp.setTerritoryId(appli.getId());
					mappings.add(mapp);
				}
			}
		}
		
		else {
			for(Object e : selections.keySet()) {
				IOpenLayersMapSpecificationEntity ent = (IOpenLayersMapSpecificationEntity) e;
				if(selections.get(ent) == 0) {
					if(existingsMapping != null) {
						for(IZoneTerritoryMapping ma : existingsMapping) {
							if(ma.getMapZoneId() == ent.getId()) {
								toRemove.add(ma);
								break;
							}
						}
					}
				}
				else {
					IZoneTerritoryMapping mapp = new ZoneTerritoryMapping();
					mapp.setMapZoneId(ent.getId());
					mapp.setMapId(mapDef.getId());
					Application appli = fmApplications.get(selections.get(ent) - 1);
					mapp.setTerritoryId(appli.getId());
					mappings.add(mapp);
				}
			}
		}
		
		try {
			Activator.getDefault().getDefinitionService().saveZoneTerritoryMappings(mappings);
			if(toRemove.size() > 0) {
				Activator.getDefault().getDefinitionService().deleteZoneTerritoryMappings(toRemove);
			}
			MessageDialog.openInformation(getShell(), Messages.DialogZoneTerritoryMapping_8, Messages.DialogZoneTerritoryMapping_9);
			super.okPressed();
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.DialogZoneTerritoryMapping_10, Messages.DialogZoneTerritoryMapping_11 + e.getMessage());
			e.printStackTrace();
		}
		
		
	}

	
	
	private class MappingTableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			List l = (List) inputElement;
			if(l.get(0) instanceof IFusionMapSpecificationEntity) {
			
				List<IFusionMapSpecificationEntity> mappings = (List<IFusionMapSpecificationEntity>) inputElement;
				IFusionMapSpecificationEntity[] mappingArray = mappings.toArray(new IFusionMapSpecificationEntity[mappings.size()]);
				
				return mappingArray;
			
			}
			else {
				List<IOpenLayersMapSpecificationEntity> mappings = (List<IOpenLayersMapSpecificationEntity>) inputElement;
				IOpenLayersMapSpecificationEntity[] mappingArray = mappings.toArray(new IOpenLayersMapSpecificationEntity[mappings.size()]);
				
				return mappingArray;
			}
		}

		@Override
		public void dispose() {
			
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
		
	}
	
	
	private class GroupComboContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			List<Group> grps = (List<Group>) inputElement;
			return grps.toArray(new Group[grps.size()]);
		}
		
	}
	
	private class GroupSelectionListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Group grp = (Group) ((IStructuredSelection)comboGroups.getSelection()).getFirstElement();
			
			fmApplications = fmManager.getApplicationsForGroup(grp.getId());
			
			try {
				//Retrieve mapping for edition
				existingsMapping = Activator.getDefault().getDefinitionService().getZoneTerritoryMappingByMapId(mapDef.getId());
				for(IZoneTerritoryMapping mapp : existingsMapping) {
					
					if(mapDef.getFusionMapObject() != null) {
					
						IFusionMapSpecificationEntity ent = null;
						for(IFusionMapSpecificationEntity e : mapDef.getFusionMapObject().getSpecificationsEntities()) {
							if(e.getId() == mapp.getMapZoneId()) {
								int appId = mapp.getTerritoryId();
								int i = 1;
								for(Application app : fmApplications) {
									if(app.getId() == appId) {
										selections.put(e, i);
										break;
									}
									i++;
								}
								break;
							}
						}
					}
					else {
						IOpenLayersMapSpecificationEntity ent = null;
						for(IOpenLayersMapSpecificationEntity e : mapDef.getOpenLayersMapObject().getEntities()) {
							if(e.getId() == mapp.getMapZoneId()) {
								int appId = mapp.getTerritoryId();
								int i = 1;
								for(Application app : fmApplications) {
									if(app.getId() == appId) {
										selections.put(e, i);
										break;
									}
									i++;
								}
								break;
							}
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			//set inputs for the combo
			String[] appliInput = new String[fmApplications.size() + 1];
			appliInput[0] = "-NONE-"; //$NON-NLS-1$
			int i = 1;
			for(Application app : fmApplications) {
				appliInput[i] = app.getName();
				i++;
			}
			
			territoryEditor.setItems(appliInput);
			
			//set input for the table
			try {
				if(mapDef.getFusionMapObject() != null) {
				
					mappingTable.setInput(mapDef.getFusionMapObject().getSpecificationsEntities());
				}
				else if(mapDef.getOpenLayersMapObject() != null) {
					mappingTable.setInput(mapDef.getOpenLayersMapObject().getEntities());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ConnectionListener implements SelectionListener {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			String user = txtUser.getText();
			String pass = txtPass.getText();
			
			if(fmManager == null) {
				if(Platform.getOS().equals(Platform.OS_WIN32)) {
					FactoryManager.init(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Tools.OS_TYPE_WINDOWS);
				}
				else if(Platform.getOS().equals(Platform.OS_LINUX)) {
					FactoryManager.init(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Tools.OS_TYPE_LINUX);
				}
				else if(Platform.getOS().equals(Platform.OS_MACOSX)) {
					FactoryManager.init(bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl(), Tools.OS_TYPE_MAC);
				}
				try {
					fmManager = FactoryManager.getManager();
				} catch (FactoryManagerException e1) {
					e1.printStackTrace();
				}
			}
			
			boolean allowed = fmManager.authentify(user, pass, fmManager);
			if(!allowed) {
				MessageDialog.openError(getShell(), Messages.DialogZoneTerritoryMapping_13, Messages.DialogZoneTerritoryMapping_14);
			}
			
			else {
				 FmUser fmUser = null;
				 try {
				 	fmUser = fmManager.getUserByNameAndPass(user, pass);
				 } catch (Exception e1) {
					 MessageDialog.openError(getShell(), Messages.DialogZoneTerritoryMapping_15, Messages.DialogZoneTerritoryMapping_16 + e1.getMessage());
				 }
				 fmGroups = fmManager.getGroupsForUser(fmUser.getId());
				 
				 comboGroups.setInput(fmGroups);
			}
		}
		
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 700);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(Messages.DialogZoneTerritoryMapping_17);
	}
	
	
}
