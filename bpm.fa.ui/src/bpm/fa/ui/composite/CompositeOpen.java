package bpm.fa.ui.composite;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;

import bpm.fa.api.olap.OLAPStructure;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.olap.xmla.XMLAStructure;
import bpm.fa.api.repository.StructureQueryLogger;
import bpm.fa.ui.Messages;
import bpm.united.olap.api.BadFasdSchemaModelTypeException;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeOpen extends Composite {
	private static final int REPOSITORY = 1;
	private static final int CURRENT_MODEL = 2;
	private static final int FILE_SYSTEM = 3;
	
	
	private int origin = FILE_SYSTEM;
	private Text text;
	private UnitedOlapLoader loader ;
	private ComboViewer cubeViewer, cubeViewsViewer;
	private Button browse;
	private IRuntimeContext runtimeContext; 
	
	private IObjectIdentifier currentIdentifier;
	
	public CompositeOpen(Composite parent, int style, UnitedOlapLoader loader,  IRuntimeContext runtimeContext) {
		super(parent, style);
		this.loader = loader;
		this.runtimeContext = runtimeContext;
		setLayout(new GridLayout(1, false));
		
		Group grpModelProvider = new Group(this, SWT.NONE);
		grpModelProvider.setText(Messages.CompositeOpen_0);
		grpModelProvider.setLayout(new GridLayout(1, false));
		grpModelProvider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		final Button btnRadioButton = new Button(grpModelProvider, SWT.RADIO);
		btnRadioButton.setSelection(true);
		btnRadioButton.setText(Messages.CompositeOpen_1);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnRadioButton.getSelection()){
					origin = FILE_SYSTEM;
				}
			}
		});
		
		final Button btCurrentModel = new Button(grpModelProvider, SWT.RADIO);
		btCurrentModel.setEnabled(false);
		btCurrentModel.setText(Messages.CompositeOpen_2);
		try{
			 
			btCurrentModel.setEnabled( bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator() != null && 
					bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryItemType() == IRepositoryApi.FASD_TYPE &&
					bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModel() != null);
			btCurrentModel.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					browse.setEnabled(!btCurrentModel.getSelection());
					if (!browse.getEnabled()){
						origin = CURRENT_MODEL;
					}
				}
			});
		}catch(Throwable t){
			btCurrentModel.setEnabled(false);
		}
		
		final Button btnRadioButton_1 = new Button(grpModelProvider, SWT.RADIO);
		btnRadioButton_1.setText(Messages.CompositeOpen_3);
		btnRadioButton_1.setEnabled(false);
		btnRadioButton_1.setVisible(false);
//		try{
//			btnRadioButton_1.setEnabled(bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryConnection() != null);
//		}catch(Throwable t) {
//			
//		}
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		final Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 55, 15);
		lblNewLabel.setText(Messages.CompositeOpen_4);
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setEditable(false);
		
		browse = new Button(composite, SWT.NONE);
		browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		browse.setText("..."); //$NON-NLS-1$
		browse.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (btnRadioButton_1.getSelection()){
					
					
					
					
				}
				else{
					FileDialog fd = new FileDialog(getShell());
					fd.setFilterExtensions(new String[]{"*.fasd", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
					
					String fName = fd.open();
					if (fName == null){
						return;
					}
					
					text.setText(fName);
				}
				
				
			}
		});

		
		Button openSchema = new Button(composite, SWT.PUSH);
		openSchema.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		openSchema.setText(Messages.CompositeOpen_8);
		openSchema.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					@Override
					public void run() {
						openSchema();
					}
				});
			}
		});
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText(Messages.CompositeOpen_9);
		
		cubeViewer = new ComboViewer(composite, SWT.READ_ONLY);
		cubeViewer.setContentProvider(new ArrayContentProvider());
		cubeViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((OLAPStructure)element).getCubeName();
			}
		});
		cubeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (currentIdentifier != null && !cubeViewer.getSelection().isEmpty()){
					loadCubeViews(currentIdentifier, ((OLAPStructure)((IStructuredSelection)cubeViewer.getSelection()).getFirstElement()).getCubeName());
				}
				else{
					cubeViewsViewer.getControl().setEnabled(false);
				}
				
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		Combo combo = cubeViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblSelectAView = new Label(composite, SWT.NONE);
		lblSelectAView.setBounds(0, 0, 55, 15);
		lblSelectAView.setText(Messages.CompositeOpen_10);
		
		cubeViewsViewer = new ComboViewer(composite, SWT.READ_ONLY);
		cubeViewsViewer.getControl().setEnabled(false);
		cubeViewsViewer.setContentProvider(new ArrayContentProvider());
		cubeViewsViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((RepositoryItem)element).getItemName();
			}
		});
		
		
		Combo combo_1 = cubeViewsViewer.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		new Label(composite, SWT.NONE);

		
		btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button)e.getSource()).getSelection()){
					origin = REPOSITORY;
					lblNewLabel.setText(Messages.CompositeOpen_11);
				}
				else{
//					originRepository = false;
					lblNewLabel.setText(Messages.CompositeOpen_12);
				}
				
			}
		});	
		
	}

	public OLAPStructure getSelectedCube(){
		if (cubeViewer.getSelection().isEmpty()){
			return null;
		}
		
		return (OLAPStructure)((IStructuredSelection)cubeViewer.getSelection()).getFirstElement();
	}
	
	public RepositoryItem getCubeView(){
		if (cubeViewsViewer.getControl().isEnabled() && !cubeViewsViewer.getSelection().isEmpty()){
			return (RepositoryItem)((IStructuredSelection)cubeViewsViewer.getSelection()).getFirstElement();
		}
		
		return null;
	}

	private void openSchema(){
		if (loader == null){
			loader = UnitedOlapLoaderFactory.getLoader();
		}
		String schemaId = null;
		List l = new ArrayList();
		switch(origin){
		case CURRENT_MODEL:
			try {
				
				FAModel model = (FAModel)bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModel();
				
				schemaId = loader.getSchemaId(model);
				
				//If the model came from the repository
				StructureQueryLogger queryLogger = null;
				if (bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId() != null && bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext() != null){
					IRepositoryContext ctx = bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext();
					IVanillaAPI api = new RemoteVanillaPlatform(ctx.getVanillaContext());
					ObjectIdentifier id = new ObjectIdentifier(ctx.getRepository().getId(),bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId());
					IRuntimeContext rctx = new RuntimeContext(ctx.getVanillaContext().getLogin(), 
							ctx.getVanillaContext().getPassword(), 
							ctx.getGroup().getName(), 
							ctx.getGroup().getId());
					queryLogger = new StructureQueryLogger(api, id, rctx);
				}
				
				//refresh the model if it's already loaded
				if(schemaId != null) {
					loader.reloadModel(null,model, runtimeContext, queryLogger);
				}
				else {
					schemaId = loader.loadModel((FAModel)bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModel(), runtimeContext, queryLogger);
				}
				
//				currentIdentifier = null;
//				if (bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId() != null && bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext() != null){
//					currentIdentifier = new ObjectIdentifier(
//							bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext().getRepository().getId(), 
//							bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId());
//				}
//				
//				if(schemaId != null) {
//					if (currentIdentifier != null){
//						Activator.getDefault().getModelService().refreshSchema(/*loader.getSchemaByModel(org.freeolap.FreemetricsPlugin.getDefault().getCurrentModel()),*/ currentIdentifier, runtimeContext);
//					}
//					else{
//						StructureQueryLogger queryLogger = null;
//						try{
//							
//							if (bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId() != null && bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext() != null){
//								IRepositoryContext ctx = bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext();
//								IVanillaAPI api = new RemoteVanillaPlatform(ctx.getVanillaContext());
//								ObjectIdentifier id = new ObjectIdentifier(ctx.getRepository().getId(),bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId());
//								IRuntimeContext rctx = new RuntimeContext(ctx.getVanillaContext().getLogin(), 
//										ctx.getVanillaContext().getPassword(), 
//										ctx.getGroup().getName(), 
//										ctx.getGroup().getId());
//								queryLogger = new StructureQueryLogger(api, id, rctx);
//								
//							}
//						}catch(Exception ex){
//							Logger.getLogger(getClass()).warn("Failed to create StructureQueryLogger for some reason - " + ex.getMessage(), ex);
//						}
//						
//						schemaId = loader.reloadModel(model, runtimeContext, queryLogger);
//						
//						Activator.getDefault().getModelService().refreshSchema(Activator.getDefault().getModelService().getSchema(schemaId), null, runtimeContext);
//						
//					}
//					
//				}
//				else {
//					StructureQueryLogger queryLogger = null;
//					try{
//						
//						if (bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId() != null && bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext() != null){
//							IRepositoryContext ctx = bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryContext();
//							IVanillaAPI api = new RemoteVanillaPlatform(ctx.getVanillaContext());
//							ObjectIdentifier id = new ObjectIdentifier(ctx.getRepository().getId(),bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId());
//							IRuntimeContext rctx = new RuntimeContext(ctx.getVanillaContext().getLogin(), 
//									ctx.getVanillaContext().getPassword(), 
//									ctx.getGroup().getName(), 
//									ctx.getGroup().getId());
//							queryLogger = new StructureQueryLogger(api, id, rctx);
//							
//						}
//					}catch(Exception ex){
//						Logger.getLogger(getClass()).warn("Failed to create StructureQueryLogger for some reason - " + ex.getMessage(), ex);
//					}
//					
//						
//					if (currentIdentifier != null){
//						schemaId = loader.loadModel(currentIdentifier, runtimeContext, queryLogger);
//					}
//					else{
//						schemaId = loader.loadModel((FAModel)bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModel(), runtimeContext, queryLogger);
//						cubeViewsViewer.getControl().setEnabled(false);
//					}
//					
//					
//				}
//				
//				if (model.getUuid() == null){
//					model.setUuid(schemaId);
//				}

			} catch (Exception ex) {
				Logger.getLogger(getClass()).error(Messages.CompositeOpen_13 + ex.getMessage(), ex);
			}


			break;
		case FILE_SYSTEM:
			try{
				
				
				
				DigesterFasd dig = new DigesterFasd(this.text.getText());
				
				schemaId = loader.loadModel(dig.getFAModel(), runtimeContext, null);
				cubeViewsViewer.getControl().setEnabled(false);
			}catch(BadFasdSchemaModelTypeException ex){
				XMLAStructure s = new XMLAStructure(ex.getModel(), ex.getModel().getCubes().get(0).getName());
				
				l.add(s);
			}catch(Exception ex){
				ex.printStackTrace();
				Logger.getLogger(getClass()).error(Messages.CompositeOpen_14 + ex.getMessage(), ex);
				cubeViewer.setInput(Collections.EMPTY_LIST);
			}
			break;
		case REPOSITORY:
			//TODO
			break;
		}
		
		try{
			l.addAll(loader.getStructures(schemaId));
		}catch(Exception ex){
			
		}
		cubeViewer.setInput(l);
		try{
			cubeViewer.setSelection(new StructuredSelection(l.get(0)));	
		}catch(IndexOutOfBoundsException ex){
			MessageDialog.openError(getShell(), Messages.CompositeOpen_15, Messages.CompositeOpen_16);
		}
	}
	
	private void loadCubeViews(IObjectIdentifier id, String cubeName){
		IRepositoryApi sock = bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getRepositoryConnection();
		if (sock == null){
			cubeViewsViewer.getControl().setEnabled(false);
			cubeViewsViewer.setInput(Collections.EMPTY_LIST);
			return;
		}
		
		
		try {
			RepositoryItem it = sock.getRepositoryService().getDirectoryItem(id.getDirectoryItemId());
			
			
			List<RepositoryItem> cubeViews = sock.getRepositoryService().getCubeViews(cubeName, it);
			cubeViewsViewer.setInput(cubeViews);
			cubeViewsViewer.getControl().setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
			cubeViewsViewer.setInput(Collections.EMPTY_LIST);
		}
		
		
		
	}
}
