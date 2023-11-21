package bpm.gateway.ui.views.property.sections.files.shape;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.views.property.sections.files.FileSelectionSection;

public class FileShapeGeneralSection extends AbstractPropertySection {

	private FileInputShape transfo;
	
	private FileSelectionSection fileSelectionComposite;

//	private Composite compNorparena, compNewMap, compExistingMap;
//	private Button insertInNorparena, existingMap, newMap;
//	private Text txtNewMap;
//	private ComboViewer cbMaps;
	
	public FileShapeGeneralSection() { }

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());

		this.fileSelectionComposite = new FileSelectionSection(composite, getWidgetFactory(), SWT.NONE);
		
//		Composite composite = getWidgetFactory().createComposite(parent);
//		composite.setLayout(new GridLayout(2, false));
//		
//		insertInNorparena = getWidgetFactory().createButton(composite, Messages.FileShapeGeneralSection_0, SWT.CHECK);
//		insertInNorparena.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		insertInNorparena.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Button source = (Button)e.getSource();
//				if (source.getSelection()) {
//					if (Activator.getDefault().getRepositoryContext() == null){
//						MessageDialog.openInformation(getPart().getSite().getShell(), 
//								Messages.FileShapeGeneralSection_1, Messages.FmdtInputSection_17);
//						source.setSelection(false);
//						return;
//					}
//					showNorparenaPart(true);
//				}
//				else {
//					showNorparenaPart(false);
//				}
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) { }
//		});
//		
//		compNorparena = getWidgetFactory().createComposite(composite);
//		compNorparena.setLayout(new GridLayout(2, false));
//		compNorparena.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
//		compNorparena.setVisible(false);
//		
//		newMap = getWidgetFactory().createButton(compNorparena, Messages.FileShapeGeneralSection_2, SWT.RADIO);
//		newMap.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		newMap.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Button source = (Button)e.getSource();
//				if (source.getSelection()){
//					showNewMapPart(true);
//					
//					transfo.setFromNewMap(true);
//				}
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) { }
//		});
//		newMap.setSelection(true);
//		
//		existingMap = getWidgetFactory().createButton(compNorparena, Messages.FileShapeGeneralSection_3, SWT.RADIO);
//		existingMap.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
//		existingMap.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Button source = (Button)e.getSource();
//				if (source.getSelection()){
//					showNewMapPart(false);
//					
//					transfo.setFromNewMap(false);	
//				}
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) { }
//		});
//		
//		compNewMap = getWidgetFactory().createComposite(compNorparena);
//		compNewMap.setLayout(new GridLayout(2, false));
//		compNewMap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//		
//		Label lblNewMap = getWidgetFactory().createLabel(compNewMap, Messages.FileShapeGeneralSection_4);
//		lblNewMap.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
//		
//		txtNewMap = getWidgetFactory().createText(compNewMap, ""); //$NON-NLS-1$
//		txtNewMap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		txtNewMap.addModifyListener(modifyNewMapListener);
//
//		
//		compExistingMap = getWidgetFactory().createComposite(compNorparena);
//		compExistingMap.setLayout(new GridLayout(3, false));
//		compExistingMap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//		compExistingMap.setVisible(false);
//		
//		Label lblExistingMap = getWidgetFactory().createLabel(compExistingMap, Messages.FileShapeGeneralSection_6);
//		lblExistingMap.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
//		
//		cbMaps = new ComboViewer(getWidgetFactory().createCCombo(compExistingMap, SWT.READ_ONLY));
//		cbMaps.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		cbMaps.setContentProvider(new ArrayContentProvider());
//		cbMaps.setLabelProvider(new LabelProvider(){
//			@Override
//			public String getText(Object element) {
//				return ((IMapDefinition)element).getLabel();
//			}
//		});
//		cbMaps.addSelectionChangedListener(new ISelectionChangedListener() {
//			
//			public void selectionChanged(SelectionChangedEvent event) {
//				IMapDefinition mapDef = (IMapDefinition)((IStructuredSelection)cbMaps.getSelection()).getFirstElement();
//				transfo.setExistingMapId(mapDef.getId());
//			}
//		});
//		
//		Button btnLoadMaps = getWidgetFactory().createButton(compExistingMap, Messages.FileShapeGeneralSection_7, SWT.PUSH);
//		btnLoadMaps.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, true));
//		btnLoadMaps.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				loadMaps();
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) { }
//		});
	}
	
	@Override
	public void refresh() {
		try {
			fileSelectionComposite.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void aboutToBeHidden() {
		fileSelectionComposite.aboutToBeHidden();
		super.aboutToBeHidden();
	}

	@Override
	public void aboutToBeShown() {
		fileSelectionComposite.aboutToBeShown();
		super.aboutToBeShown();
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Node node = (Node)((NodePart) input).getModel();
        this.transfo = (FileInputShape)(node).getGatewayModel();
		
        fileSelectionComposite.setNode(node);
	}
	
//	private void showNorparenaPart(boolean show){
//		transfo.setSaveInNorparena(show);
//		compNorparena.setVisible(show);
//	}
//	
//	private void showNewMapPart(boolean show){
//		compNewMap.setVisible(show);
//		compExistingMap.setVisible(!show);
//	}
//	
//	private List<IMapDefinition> loadMaps() {
//		if (Activator.getDefault().getRepositoryContext() == null){
//			MessageDialog.openInformation(getPart().getSite().getShell(), 
//					Messages.FileShapeGeneralSection_8, Messages.FmdtInputSection_17);
//			return new ArrayList<IMapDefinition>();
//		}
//		
//		try {
//			List<IMapDefinition> maps = Activator.getDefault().getMapDefinitionService().getMapDefinition(MapType.OPEN_GIS);
//			if(maps == null){
//				maps = new ArrayList<IMapDefinition>();
//			}
//			cbMaps.setInput(maps);
//			
//			return maps;
//		} catch (Exception e) {
//			e.printStackTrace();
//			MessageDialog.openInformation(getPart().getSite().getShell(), 
//					Messages.FileShapeGeneralSection_9, Messages.FileShapeGeneralSection_10 + e.getMessage());
//			return new ArrayList<IMapDefinition>();
//		}
//	}
//	
//	@Override
//	public void setInput(IWorkbenchPart part, ISelection selection) {
//		super.setInput(part, selection);
//        Assert.isTrue(selection instanceof IStructuredSelection);
//        Object input = ((IStructuredSelection) selection).getFirstElement();
//        Assert.isTrue(input instanceof NodePart);
//        this.transfo = (FileInputShape)((Node)((NodePart) input).getModel()).getGatewayModel();
//	}
//	
//	@Override
//	public void refresh() {
//		if(transfo != null){
//			if(transfo.saveInNorparena()){
//				insertInNorparena.setSelection(true);
//				showNorparenaPart(true);
//				
//				if(transfo.isFromNewMap()){
//					showNewMapPart(true);
//					
//					newMap.setSelection(true);
//					existingMap.setSelection(false);
//					txtNewMap.setText(transfo.getNewMapName());
//				}
//				else {
//					showNewMapPart(false);
//
//					newMap.setSelection(false);
//					existingMap.setSelection(true);
//					
//					List<IMapDefinition> maps = loadMaps();
//					if(transfo.getExistingMapId() != null){
//						for(IMapDefinition mapDef : maps){
//							if(mapDef.getId().equals(transfo.getExistingMapId())){
//								List<IMapDefinition> selectedMap = new ArrayList<IMapDefinition>();
//								selectedMap.add(mapDef);
//								StructuredSelection selection = new StructuredSelection(selectedMap);
//								cbMaps.setSelection(selection);
//								break;
//							}
//						}
//					}
//				}
//			}
//			else {
//				showNorparenaPart(false);
//			}
//		}
//	}
//	
//	private ModifyListener modifyNewMapListener = new ModifyListener() {
//		
//		@Override
//		public void modifyText(ModifyEvent e) {
//			Text source = (Text)e.getSource();
//			transfo.setNewMapName(source.getText());
//		}
//	};
}
