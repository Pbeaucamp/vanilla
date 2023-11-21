package bpm.es.dndserver.views.composites;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.DNDOProject;
import bpm.es.dndserver.api.Message;
import bpm.es.dndserver.api.fmdt.FMDTDataSource;
import bpm.es.dndserver.api.fmdt.FMDTMigration;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.api.repository.RepositoryWrapper;
import bpm.es.dndserver.tools.OurLogger;
import bpm.es.dndserver.views.providers.MetaDataContentProvider;
import bpm.es.dndserver.views.providers.RepositoryLabelProvider;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataViewer extends Composite{

	public static final int TYPE_SOURCE = 0;
	public static final int TYPE_TARGET = 1;
	
	private int currentType = 0;
	
	protected DNDOProject project;
	
	private TreeViewer viewerMetadatas;
	
	private Combo cModel, cPackage, cConnection;
	
	protected Section main;
	
	private HashMap<String, IBusinessModel> businessModels = 
		new HashMap<String, IBusinessModel>();
	
	private HashMap<String, IBusinessPackage> businessPackages 
		= new HashMap<String, IBusinessPackage>();
	
	private AxisDirectoryItemWrapper selectedItem;
	
	private RepositoryItem selectedFMDT, oldFMDT;
	
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param type
	 */
	public MetadataViewer(Composite parent, int style, FormToolkit toolkit, int type) {
		super(parent, style);
		
		this.currentType = type;
		
		this.setLayout(new GridLayout(1, false));
		
		
		createView(toolkit);
	}

	
	protected Composite createContent(Composite parent, FormToolkit toolkit){
		Composite main = toolkit.createComposite(parent);
		
		main.setLayout(new GridLayout(2, false));	

		viewerMetadatas = new TreeViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewerMetadatas.getTree().setLayout(new GridLayout());
		viewerMetadatas.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewerMetadatas.setContentProvider(new MetaDataContentProvider());
		viewerMetadatas.setLabelProvider(new RepositoryLabelProvider());
		
//		viewerMetadatas.getTree().setEnabled(false);
		
		Composite infoPanel = toolkit.createComposite(main);
		infoPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		infoPanel.setLayout(new GridLayout(2, false));		
		
		Label l1 = toolkit.createLabel(infoPanel, Messages.MetadataViewer_0);
		cModel = new Combo(infoPanel, SWT.READ_ONLY);
		cModel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l2 = toolkit.createLabel(infoPanel, Messages.MetadataViewer_1);
		cPackage = new Combo(infoPanel, SWT.READ_ONLY);
		cPackage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l0 = toolkit.createLabel(infoPanel, Messages.MetadataViewer_2);
		cConnection = new Combo(infoPanel, SWT.READ_ONLY);
		cConnection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (currentType == TYPE_TARGET) {			
			Button bApply = new Button(main, SWT.PUSH);
			bApply.setText(Messages.MetadataViewer_3);
			bApply.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String model = cModel.getText();
					String packag = cPackage.getText();
					String connection = cConnection.getText();
					
					String username = project.getOutputRepository().getSock().getContext().getVanillaContext().getLogin();
					String password = project.getOutputRepository().getSock().getContext().getVanillaContext().getPassword();
					String url = project.getOutputRepository().getSock().getContext().getRepository().getUrl();
					
//					xString groupName = "System";
					int fmdtDirItemId = selectedFMDT.getId();
					
					FMDTDataSource targetDS = new FMDTDataSource();
					targetDS.setDirItemId(fmdtDirItemId);
					targetDS.setBusinessModel(model);
					targetDS.setBusinessPackage(packag);
					targetDS.setConnectionName(connection);
					targetDS.setPass(password);
					targetDS.setUser(username);
					targetDS.setUrl(url);
					targetDS.setGroupName(project.getOutputRepository().getGroupName());
					
					selectedItem.setRemaped(true);
					FMDTMigration migration = selectedItem.getExistingMigration(oldFMDT.getId());
					migration.setTarget(targetDS);
					MetadataViewer.this.notifyListeners(SWT.Modify, new Event());
					
					if (!selectedItem.remapFullyDefined()){
						project.getMessenger().addMessage(new Message(selectedItem.getAxisItem().getId(), selectedItem.getAxisItem().getItemName(), Message.ERROR, Messages.MetadataViewer_4));
					}
					else{
//						xx
					}
				}
			});
			
			Button bCancel = new Button(main, SWT.PUSH);
			bCancel.setText(Messages.MetadataViewer_5);
			bCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					FMDTMigration migration = selectedItem.getExistingMigration(oldFMDT.getId());
					showMetadata(migration.getTarget());
				}
			});
			
			//add tree listener
			viewerMetadatas.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					StructuredSelection ss = (StructuredSelection) viewerMetadatas.getSelection();
					
					Object o = ss.getFirstElement();
					
					if (o instanceof RepositoryItem &&
							(((RepositoryItem)o).getType() == 
								IRepositoryApi.FMDT_TYPE)) {
						try {
							selectedFMDT = (RepositoryItem) o;
							loadMetadataFromRepository((RepositoryItem) o);
							
							cModel.select(-1);
							cPackage.select(-1);
							cConnection.select(-1);
							
						} catch (Exception e) {
							e.printStackTrace();
							OurLogger.error(Messages.MetadataViewer_6, e);
							MessageDialog.openError(Display.getDefault().getActiveShell(), 
									Messages.MetadataViewer_7, Messages.MetadataViewer_8 + e.getMessage());
						}
					}
				}
			});
			
			cModel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String model = cModel.getText();
					Collection<IBusinessPackage> packages = businessModels.get(model).getBusinessPackages("none"); //$NON-NLS-1$
					
					businessPackages.clear();
					
					for (IBusinessPackage pack : packages) {
						businessPackages.put(pack.getName(), pack);
					}
					
					cPackage.setItems(businessPackages.keySet().toArray(new String[businessPackages.keySet().size()]));
					cPackage.select(-1);
					cConnection.select(-1);
					
				}
			});
			
			cPackage.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String pack = cPackage.getText();
					
					List<String> cons = businessPackages.get(pack).getConnectionsNames("none"); //$NON-NLS-1$
					
					cConnection.setItems(cons.toArray(new String[cons.size()]));
					cConnection.select(-1);
				}
			});
		}
		
		return main;
	}
	
	private void createView(FormToolkit toolkit) {
		Section section = toolkit.createSection(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setLayout(new GridLayout());
		if (currentType == TYPE_SOURCE) 
			section.setText(Messages.MetadataViewer_11);
		else 
			section.setText(Messages.MetadataViewer_12);
		
		this.main = section;
		
		Composite main = createContent(section, toolkit);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setClient(main);
		
	}
	
	public void setEnabled(boolean enabled) {
		main.getClient().setEnabled(enabled);
	}
	
	private void loadMetadataFromRepository(RepositoryItem wrap) throws Exception {
		String xmlModel = project.getOutputRepository().getRepositoryClient().getRepositoryService()
			.loadModel(wrap);
		
		Collection<IBusinessModel> models = MetaDataReader.read(null, IOUtils.toInputStream(xmlModel, "UTF-8"), null, true); //$NON-NLS-1$
		
		businessModels = new HashMap<String, IBusinessModel>();
		
		for (IBusinessModel model : models) {
			businessModels.put(model.getName(), model);
		}
		
		if (cModel != null){
			cModel.setItems(businessModels.keySet().toArray(new String[0]));
			cModel.select(0);
		}
		
	}

	
	protected void refreshViewer(RepositoryWrapper rep){
//		if (viewerMetadatas != null){
			viewerMetadatas.setInput(rep);
			viewerMetadatas.expandAll();
//		}
		
	}
	
	public void setProject(DNDOProject project, AxisDirectoryItemWrapper metadataItem,
			AxisDirectoryItemWrapper selectedItem) throws Exception {
		this.project = project;
		this.selectedItem = selectedItem;
		
		if (currentType == TYPE_SOURCE) {
			refreshViewer(project.getInputRepository());
			setEnabled(false);
			//selectedItem.ge
			//FMDTDataSource source = loadExistingMetadatas(metadataItem, selectedItem);
			FMDTMigration migration = selectedItem.getExistingMigration(metadataItem.getAxisItem().getId());
			showMetadata(migration.getSource());
		}
		else {
			refreshViewer(project.getOutputRepository());
		
			
			oldFMDT = metadataItem.getAxisItem();
			
			FMDTMigration migration = selectedItem.getExistingMigration(oldFMDT.getId());
			
			if (migration.getTarget() != null){
				showMetadata(migration.getTarget());
			}
			else{
				showNullMetadata();
			}
			setEnabled(true);
		}
		
		
	}
	
	private void showNullMetadata() {
		cModel.setItems(new String[] {});
		cPackage.setItems(new String[] {});
		cConnection.setItems(new String[] {});
//		viewerMetadatas.setInput(null);
	}
	
	protected void showMetadata(FMDTDataSource source) {
		if (source == null) {
			showNullMetadata();
			return;
		}
		
		cModel.setItems(new String[] {source.getBusinessModel()});
		cModel.select(0);
		cPackage.setItems(new String[] {source.getBusinessPackage()});
		cPackage.select(0);
		cConnection.setItems(new String[] {source.getConnectionName()});
		cConnection.select(0);
		
		
		//sets selection in the tree
		
		ITreeContentProvider cp = (ITreeContentProvider)viewerMetadatas.getContentProvider();
		
		for(Object o : cp.getElements(viewerMetadatas.getInput())){
			Object s = findItemInTree(cp, source.getDirItemId(), o);
			if (s != null){
				viewerMetadatas.setSelection(new StructuredSelection(s));
			}
		}
		
		
		
	}
	
	private Object findItemInTree(ITreeContentProvider cp, int itemId, Object parent){
		
		if (!cp.hasChildren(parent)){
			if (parent instanceof RepositoryItem){
				if (((RepositoryItem)parent).getId() == itemId){
					return parent;
				}
			}
			else if (parent instanceof AxisDirectoryItemWrapper){
				if (((AxisDirectoryItemWrapper)parent).getAxisItem().getId() == itemId){
					return parent;
				}
			}
		}
		else{
			for(Object o : cp.getChildren(parent)){
				Object r = findItemInTree(cp, itemId, o);
				if (r != null){
					return r;
				}
			}
		}
		return null;
	}
	
}
