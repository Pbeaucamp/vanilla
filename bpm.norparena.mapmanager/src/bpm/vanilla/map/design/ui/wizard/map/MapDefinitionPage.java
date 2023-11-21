package bpm.vanilla.map.design.ui.wizard.map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.wizard.WizardFusionMapObject;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;

public class MapDefinitionPage extends WizardPage  {
	
	private IMapDefinition mapDefinition;
	
	private Text label;
	private Text description;
//	private Combo cbMapType;
//	private Text kmlUrl;
	private ComboViewer fusionMaps;
	
	private ComboViewer comboMapType;
	
	private boolean edit;
	
//	private Button addKml;
	private Button addFusionMaps;
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
	
	protected MapDefinitionPage(String pageName, IMapDefinition mapDef, boolean bo) {
		super(pageName);
		this.mapDefinition = mapDef;
		this.edit = bo;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label lab = new Label(main, SWT.NONE);
		lab.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lab.setText(Messages.MapDefinitionPage_0);
		
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
		lblDesc.setText(Messages.MapDefinitionPage_1);
		
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
		fusionMaps.setText(Messages.MapDefinitionPage_2);
		
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
					
					Object o = ((IStructuredSelection)MapDefinitionPage.this.fusionMaps.getSelection()).getFirstElement();
					
					mapDefinition.setFusionMapObject((IFusionMapObject)o);
					mapDefinition.setFusionMapObjectId(((IFusionMapObject)o).getId());
				}
				getContainer().updateButtons();
			}
		});
		
		try {
			this.fusionMaps.setInput(Activator.getDefault().getFusionMapRegistry().getFusionMapObjects());
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageDialog.openError(getShell(), Messages.MapDefinitionPage_3, Messages.MapDefinitionPage_4 + e1.getMessage());
		}
		
		
		addFusionMaps = new Button(main, SWT.PUSH);
		addFusionMaps.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		addFusionMaps.setText(Messages.MapDefinitionPage_5);
		addFusionMaps.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				WizardFusionMapObject wiz = new WizardFusionMapObject();
				
				WizardDialog d = new WizardDialog(getShell(), wiz);
				d.setMinimumPageSize(800, 600);
				if (d.open() == WizardDialog.OK){
					try {
						MapDefinitionPage.this.fusionMaps.setInput(Activator.getDefault().getFusionMapRegistry().getFusionMapObjects());
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					
				}
				
				
				getContainer().updateButtons();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		


		
		if(edit){
			label.setText(mapDefinition.getLabel());
			description.setText(mapDefinition.getDescription());
		}
		
		// page setting
		setControl(main);
		setPageComplete(false);

	}

//	private void createKmlObject(File file) {
//		URL url = null;
//		InputStream kmlInputStream = null;
//		try {
//			url = file.toURI().toURL();
//			kmlInputStream = url.openStream();
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		IFactoryKml factoryKml = null;
//		try {
//			factoryKml = Activator.getDefault().getFactoryKml();
//		} catch (Exception e2) {
//			
//			e2.printStackTrace();
//		}
//		
//		IKmlObject kmlObject = null;
//		try {
//			kmlObject = factoryKml.createKmlObject();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		kmlObject.setKmlFileName(file.getName());
//		
//		KmlParser kmlParser = new KmlParser(file, kmlObject, factoryKml);
//		kmlParser.prepareKmlSpecificationEntities();
//		
//		try {
//			kmlObject = Activator.getDefault().getKmlRegistry().addKmlObject(kmlObject, kmlInputStream);
//			mapDefinition.setKmlObject(kmlObject);
//			mapDefinition.setKmlObjectId(kmlObject.getId());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private void createFusionMapObject(File file) {
//		URL url = null;
//		InputStream fusionMapInputStream = null;
//		try {
//			url = file.toURI().toURL();
//			fusionMapInputStream = url.openStream();
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		IFusionMapObject fusionMapObject = null;
//		try {
//			fusionMapObject = Activator.getDefault().getFactoryFusionMap().createFusionMapObject();
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		fusionMapObject.setSwfFileName(file.getName());
//		
//		try {
//			fusionMapObject = Activator.getDefault().getFusionMapRegistry().addFusionMapObject(fusionMapObject, fusionMapInputStream);
//			mapDefinition.setFusionMapObject(fusionMapObject);
//			mapDefinition.setFusionMapObjectId(fusionMapObject.getId());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public boolean isPageComplete() {
		if(!(label.getText().trim().equals("")) && (!(fusionMaps.getSelection().isEmpty()))){ //$NON-NLS-1$
			return true;
		}
		return false;
	}
}
