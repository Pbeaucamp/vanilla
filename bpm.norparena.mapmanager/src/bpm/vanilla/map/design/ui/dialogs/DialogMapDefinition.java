package bpm.vanilla.map.design.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.wizard.WizardFusionMapObject;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.design.ui.wizard.map.MapDefinitionPage;

public class DialogMapDefinition extends Dialog {
	private List<IMapDefinition> mapDefinitions;
	private IMapDefinition mapDefinition;
	
	private List<IFusionMapObject> fusionMapObjects;
	
	private Text label;
	private Text description;
//	private Combo cbMapType;
//	private Text kmlUrl;
	private ComboViewer fusionMaps;
	
	private ComboViewer comboMapType;
	
	private boolean edit;
	
//	private Button addKml;
	private Button addFusionMaps;
	private Button selectMapDefinitionParent;
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			try{
				if (e.getSource().equals(label)) {
					mapDefinition.setLabel(label.getText());
				}
				else if (e.getSource().equals(description)) {
					mapDefinition.setDescription(description.getText());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	};
	
	public DialogMapDefinition(Shell parentShell, IMapDefinition mapDef, 
			List<IMapDefinition> mapDefinitions, boolean bo) {
		super(parentShell);
		this.mapDefinition = mapDef;
		this.mapDefinitions = mapDefinitions;
		this.edit = bo;		
	}
	
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label lab = new Label(main, SWT.NONE);
		lab.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lab.setText(Messages.DialogMapDefinition_0);
		
		label = new Text(main, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		label.addModifyListener(listener);

//		Label lblCbMapType = new Label(main, SWT.NONE);
//		lblCbMapType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		lblCbMapType.setText("Type of Map: ");
//		
//		cbMapType = new Combo(main, SWT.PUSH);
//		cbMapType.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
//		cbMapType.setItems(IMapDefinition.MAP_DEFINITION_TYPES);
//		cbMapType.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				mapDefinition.setMapType(cbMapType.getText());
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//				
//			}
//		});
		
		Label lblDesc = new Label(main, SWT.NONE);
		lblDesc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblDesc.setText(Messages.DialogMapDefinition_1);
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		description.addModifyListener(listener);
		
//		Label kml = new Label(main, SWT.NONE);
//		kml.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		kml.setText("Kml: ");
//		
//		kmlUrl = new Text(main, SWT.BORDER);
//		kmlUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		kmlUrl.setEditable(false);	
		
//		addKml = new Button(main, SWT.PUSH);
//		addKml.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		addKml.setText("Add Kml");
//		addKml.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
//		        fd.setText("Open ");
//		        fd.setFilterPath("C:/");
//		        String[] filterExt = { "*.kml", "*.*" };
//		        fd.setFilterExtensions(filterExt);
//		        String selected = fd.open();
//		        
//		        if(selected != null && !selected.isEmpty()){
//			        		
//				    kmlUrl.setText(selected);
//				    File file = new File(selected);
//					createKmlObject(file);
//					
//					System.out.println("Kml save on the server");
//		        }					
//				getContainer().updateButtons();
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//			}
//		});
		
		Label fusionMaps = new Label(main, SWT.NONE);
		fusionMaps.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		fusionMaps.setText(Messages.DialogMapDefinition_2);
		
		this.fusionMaps = new ComboViewer(main, SWT.BORDER);
		this.fusionMaps.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		this.fusionMaps.setContentProvider(new ArrayContentProvider());
		this.fusionMaps.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IFusionMapObject)element).getName();
			}
		});
		this.fusionMaps.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (mapDefinition != null){
					
					Object o = ((IStructuredSelection)DialogMapDefinition.this.fusionMaps.getSelection()).getFirstElement();
					
					mapDefinition.setFusionMapObject((IFusionMapObject)o);
					mapDefinition.setFusionMapObjectId(((IFusionMapObject)o).getId());
				}
			}
		});
		
		try {
			fusionMapObjects = Activator.getDefault().getFusionMapRegistry().getFusionMapObjects();
			this.fusionMaps.setInput(fusionMapObjects);
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogMapDefinition_3, Messages.DialogMapDefinition_4 + e1.getMessage());
		}
		
		
		addFusionMaps = new Button(main, SWT.PUSH);
		addFusionMaps.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		addFusionMaps.setText(Messages.DialogMapDefinition_5);
		addFusionMaps.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				WizardFusionMapObject wiz = new WizardFusionMapObject();
				
				WizardDialog d = new WizardDialog(getShell(), wiz);
				d.setMinimumPageSize(800, 600);
				if (d.open() == WizardDialog.OK){
					try {
						fusionMapObjects = Activator.getDefault().getFusionMapRegistry().getFusionMapObjects();
						DialogMapDefinition.this.fusionMaps.setInput(fusionMapObjects);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label lblMapType = new Label(main, SWT.NONE);
		lblMapType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblMapType.setText(Messages.DialogMapDefinition_6);
		
		this.comboMapType = new ComboViewer(main, SWT.BORDER);
		this.comboMapType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		this.comboMapType.setContentProvider(new ArrayContentProvider());
		this.comboMapType.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		this.comboMapType.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (mapDefinition != null){
					
					Object o = ((IStructuredSelection)DialogMapDefinition.this.comboMapType.getSelection()).getFirstElement();
					
					mapDefinition.setMapType((String) o);
				}
			}
		});
		
		String[] types = new String[]{IMapDefinition.MAP_TYPE_CLASSIC, IMapDefinition.MAP_TYPE_FM};
		
		this.comboMapType.setInput(types);
		if(mapDefinition != null) {
			if(mapDefinition.getMapType() != null && mapDefinition.getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
				this.comboMapType.getCombo().select(1);
			}
			else {
				this.comboMapType.getCombo().select(0);
			}
		}
		
		

		
		
		selectMapDefinitionParent = new Button(main, SWT.PUSH);
		selectMapDefinitionParent.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		selectMapDefinitionParent.setText(Messages.DialogMapDefinition_7);
		selectMapDefinitionParent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectMapDefinitionParent dial = new DialogSelectMapDefinitionParent(getShell(), 
						mapDefinitions, mapDefinition, edit);
				if(dial.open() == Dialog.OK){
					
				}
			}
		});
		
		if(edit){
			label.setText(mapDefinition.getLabel());
			if(mapDefinition.getDescription() != null){
				description.setText(mapDefinition.getDescription());
			}
			if(mapDefinition.getFusionMapObject() != null){
				int i = 0;
				for(IFusionMapObject fusionMap : fusionMapObjects){
					if(fusionMap.getName().equals(mapDefinition.getFusionMapObject().getName())){
						this.fusionMaps.getCombo().select(i);
					}
					i++;
				}
			}
		}
		
		return main;
	}	
	
	@Override
	protected void okPressed() {
		if(!(label.getText().trim().equals("")) && (!(fusionMaps.getSelection().isEmpty()))){ //$NON-NLS-1$
			if(edit){
				try{
					
					Activator.getDefault().getDefinitionService().update(mapDefinition);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogMapDefinition_9, Messages.DialogMapDefinition_10 + ex.getMessage());
				}
			}
			else{
				try{
					
					Activator.getDefault().getDefinitionService().saveMapDefinition(mapDefinition);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogMapDefinition_11, Messages.DialogMapDefinition_12 + ex.getMessage());
				}
			}
			super.okPressed();
		}
	}


}
