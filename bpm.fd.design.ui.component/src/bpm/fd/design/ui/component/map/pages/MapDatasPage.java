package bpm.fd.design.ui.component.map.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.api.core.model.components.definition.maps.IMapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.map.CompositeGoogleMapDatas;
import bpm.fd.design.ui.component.map.CompositeMapDatas;
import bpm.fd.design.ui.component.map.ICompositeMapDatas;

public class MapDatasPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.map.pages.MapDatasPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.MapDatasPage_1;
	public static final String PAGE_DESCRIPTION = Messages.MapDatasPage_2;

	private ICompositeMapDatas mapDatasComposite;
	
//	protected ComboViewer dataSourceViewer;
//	protected ComboViewer dataSetViewer;
//	protected ComboViewer valueCol, zoneCol;
	

	public MapDatasPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MapDatasPage(String pageName) {
		super(pageName);
	}
	
	
	public void createControl(Composite parent) {
		mapDatasComposite = new CompositeMapDatas();
		mapDatasComposite.createContent(parent);
		
		setControl(mapDatasComposite.getClient());
		attachListener();
	}
	
	private void attachListener(){
		mapDatasComposite.getClient().addListener(SWT.Modify, new Listener(){

			public void handleEvent(Event event) {
				getContainer().updateButtons();
				
			}
			
		});
	}
	
	
	public void rebuildContent(int mapRendererType){
		Composite parent = mapDatasComposite.getClient().getParent();
		
		if (mapDatasComposite instanceof CompositeMapDatas){
			if (mapRendererType != MapRenderer.VANILLA_FUSION_MAP && mapRendererType != MapRenderer.VANILLA_FLASH_MAP){
				mapDatasComposite.getClient().dispose();
				mapDatasComposite = new CompositeGoogleMapDatas();
				setControl(mapDatasComposite.createContent(parent));
				attachListener();
				parent.layout();
			}
		}
		else {
			if (mapRendererType != MapRenderer.VANILLA_GOOGLE_MAP){
				mapDatasComposite.getClient().dispose();
				mapDatasComposite = new CompositeMapDatas();
				setControl(mapDatasComposite.createContent(parent));
				attachListener();
				parent.layout();
			}
		}
	}
	
//	public void createControl(Composite parent) {
//		Composite main = new Composite(parent, SWT.NONE);
//		main.setLayout(new GridLayout(3, false));
//
//		Label l = new Label(main, SWT.NONE);
//		l.setText("Select DataSource");
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		
//		dataSourceViewer = new ComboViewer(main, SWT.READ_ONLY);
//		dataSourceViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		dataSourceViewer.setContentProvider(new ListContentProvider<DataSource>());
//		dataSourceViewer.setLabelProvider(new DatasLabelProvider());
//		dataSourceViewer.setComparator(new LabelableComparator());
//		dataSourceViewer.addSelectionChangedListener(new SelectionChangedListener());
//		dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
//		
//		Button createDataSource = new Button(main, SWT.PUSH);
//		createDataSource.setText("...");
//		createDataSource.setToolTipText("Create a new DataSource");
//		createDataSource.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		createDataSource.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				try{
//					OdaDataSourceWizard wiz = new OdaDataSourceWizard();
//					
//					WizardDialog dial = new WizardDialog(getShell(), wiz);
//					if (dial.open() == Dialog.OK){
//						dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//			}
//		});
//		
//		Label l2 = new Label(main, SWT.NONE);
//		l2.setText("Select DataSet");
//		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		
//		dataSetViewer = new ComboViewer(main, SWT.READ_ONLY);
//		dataSetViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		dataSetViewer.setContentProvider(new ListContentProvider<DataSet>());
//		dataSetViewer.setLabelProvider(new DatasLabelProvider());
//		dataSetViewer.setComparator(new LabelableComparator());
//		dataSetViewer.addSelectionChangedListener(new SelectionChangedListener());
//
//		Button createDataSet = new Button(main, SWT.PUSH);
//		createDataSet.setText("...");
//		createDataSet.setToolTipText("Create a new DataSet");
//		createDataSet.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		createDataSet.addSelectionListener(new SelectionAdapter(){
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				try{
//					OdaDataSetWizard wiz = new OdaDataSetWizard(null);
//					
//					WizardDialog dial = new WizardDialog(getShell(), wiz);
//					if (dial.open() == Dialog.OK){
//						dataSourceViewer.setSelection(dataSourceViewer.getSelection());
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//			}
//		});
//		
//		l = new Label(main, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("Value Field");
//		
//		
//		valueCol = new ComboViewer(main, SWT.READ_ONLY);
//		valueCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		valueCol.setLabelProvider(new DatasLabelProvider());
//		valueCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
//		valueCol.setComparator(new LabelableComparator());
//		valueCol.addSelectionChangedListener(new SelectionChangedListener());
//
//		valueCol.getControl().setEnabled(false);
//		
//		l = new Label(main, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("ZoneIf Field");
//		
//		
//		zoneCol = new ComboViewer(main, SWT.READ_ONLY);
//		zoneCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		zoneCol.setLabelProvider(new DatasLabelProvider());
//		zoneCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
//		zoneCol.setComparator(new LabelableComparator());
//		zoneCol.getControl().setEnabled(false);
//		zoneCol.addSelectionChangedListener(new SelectionChangedListener());
//		
//		
//		
//		setControl(main);
//		
//	}
	
	public IMapDatas getMapDatas(){
		return mapDatasComposite.getMapDatas();
//		MapDatas datas = null;
//
//		datas = new MapDatas();
//		DataSet ds = (DataSet) ((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement();
//		datas.setDataSet(ds);
//		
//		
//		
//		datas.setValueFieldIndex(((ColumnDescriptor)((IStructuredSelection)valueCol.getSelection()).getFirstElement()).getColumnIndex());
//		datas.setZoneIdFieldIndex(((ColumnDescriptor)((IStructuredSelection)zoneCol.getSelection()).getFirstElement()).getColumnIndex());
//		
//
//		
//		
//		
//		return datas;
	}
	
//	public class SelectionChangedListener implements ISelectionChangedListener{
//
//		public void selectionChanged(SelectionChangedEvent event) {
//			if (event.getSource() == dataSourceViewer){
//				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
//				DataSource ds = (DataSource)ss.getFirstElement();
//
//				dataSetViewer.setInput(ds.getDataSet());
//				valueCol.setInput(new ArrayList<ColumnDescriptor>());
//				valueCol.getControl().setEnabled(false);
//				
//				zoneCol.setInput(new ArrayList<ColumnDescriptor>());
//				zoneCol.getControl().setEnabled(false);
//				
//
//			}
//			else if (event.getSource() == dataSetViewer){
//				IStructuredSelection ss = (IStructuredSelection)dataSetViewer.getSelection();
//				
//				if (ss.isEmpty()){
//					List<ColumnDescriptor> l = new ArrayList<ColumnDescriptor>();
//					valueCol.setInput(l);
//					zoneCol.setInput(l);
//					return;
//
//				}
//				DataSet ds = (DataSet)ss.getFirstElement();
//
//				valueCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
//				zoneCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
//			
//				valueCol.getControl().setEnabled(true);
//				zoneCol.getControl().setEnabled(true);
//				
//				
//			}
//			
//			
//			
//			getContainer().updateButtons();
//			
//			
//		}
//		
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		try {
			return getMapDatas().isFullyDefined();
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		} 
//		return !(dataSourceViewer.getSelection().isEmpty() || valueCol.getSelection().isEmpty() );
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
}
