package bpm.mdm.ui.model.composites;

import java.util.Collections;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.DesignSessionRequest;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DataSetDesignSession;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DataSourceDesignSession;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizard;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.NewDataSourceWizard;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmFactory;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.helper.DataSetDesignConverter;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.RuntimeFactory;
import bpm.mdm.model.runtime.SynchroPerformer;
import bpm.mdm.model.storage.IEntityStorage;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.diff.DialogDiff;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.icons.IconNames;
import bpm.mdm.ui.model.composites.viewer.MdmAdvisor;
import bpm.mdm.ui.model.composites.viewer.MdmContentProvider;
import bpm.mdm.ui.model.composites.viewer.MdmLabelProvider;
import bpm.mdm.ui.model.composites.viewer.MdmObservableFactory;
import bpm.mdm.ui.model.composites.viewer.datasource.DataSourceContentProvider;
import bpm.mdm.ui.model.composites.viewer.datasource.DataSourceLabelProvider;
import bpm.mdm.ui.wizards.oda.DialogOdaDriverSelection;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class ModelMasterDetails extends MasterDetailsBlock{
	
	private TreeViewer modelViewer;
	private TableViewer synchronizerViewer;
	private TreeViewer dataSourceViewer;
	private ViewerPart viewerPart;
	private SectionPart dataSourcePart;
	private SectionPart synchronizerPart;
	
	private WritableList synchronizerInput;
	
	
	
	
	public ModelMasterDetails() {
		Activator.getDefault().getControler().setModelMasterDetails(this);
	}
	

	
	private FormToolkit toolkit;
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
		
		Composite main = toolkit.createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/*
		 * modell
		 */
		Section sctnModel = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sctnModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sctnModel.addExpansionListener(new _ExpansionListener());
		toolkit.paintBordersFor(sctnModel);
		sctnModel.setText(Messages.ModelMasterDetails_0);
		
		Composite sectionClient = toolkit.createComposite(sctnModel);
		sectionClient.setLayout(new GridLayout());
		toolkit.paintBordersFor(sectionClient);
		
		createSectionToolBar(sectionClient);
		
		
		modelViewer = createModelViewer(sectionClient, managedForm);
		toolkit.paintBordersFor(modelViewer.getTree());
		sctnModel.setClient(sectionClient);
		
		
		viewerPart = new ViewerPart(sctnModel, modelViewer);
		managedForm.addPart(viewerPart);

		/*
		 * synchro
		 */
		sctnModel = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sctnModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sctnModel.addExpansionListener(new _ExpansionListener());
		toolkit.paintBordersFor(sctnModel);
		sctnModel.setText(Messages.ModelMasterDetails_1);
		
		Composite syncClient = toolkit.createComposite(sctnModel);
		syncClient.setLayout(new GridLayout());
		toolkit.paintBordersFor(syncClient);
		
		createSynchroToolBar(syncClient);
		synchronizerViewer = createSynchronizerViewer(syncClient, managedForm);
		toolkit.paintBordersFor(synchronizerViewer.getTable());
		sctnModel.setClient(syncClient);
		
		
		synchronizerPart = new SectionPart(sctnModel);
		managedForm.addPart(synchronizerPart);

		/*
		 * dataSource
		 */
		sctnModel = toolkit.createSection(main, Section.TWISTIE | Section.TITLE_BAR);
		sctnModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sctnModel.addExpansionListener(new _ExpansionListener());
		toolkit.paintBordersFor(sctnModel);
		sctnModel.setText(Messages.ModelMasterDetails_2);
		
		Composite dsClient = toolkit.createComposite(sctnModel);
		dsClient.setLayout(new GridLayout());
		toolkit.paintBordersFor(dsClient);
		
		createDataSourceToolBar(dsClient);
		dataSourceViewer = createDataSourceViewer(dsClient, managedForm);
		toolkit.paintBordersFor(dataSourceViewer.getTree());
		sctnModel.setClient(dsClient);
		
		
		dataSourcePart = new SectionPart(sctnModel);
		managedForm.addPart(dataSourcePart);
	}
	
	private void createDataSourceToolBar(final Composite sectionClient) {
		ToolBar bar = new ToolBar(sectionClient, SWT.FLAT);
		bar.setBackground(toolkit.getColors().getBackground());
		bar.setLayoutData(new GridData());
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_3);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
				try{
					DialogOdaDriverSelection dial = new DialogOdaDriverSelection(sectionClient.getShell());
					
					if (dial.open() != DialogOdaDriverSelection.OK){
						return;
					}
					
					String extId = dial.getExtensionId();
					String dataSetId = dial.getDataSetTypeId();
					
					DataSourceDesign dataSourceDesign = null;
					DataSetDesign dataSetDesign = null;
					
					//dataSourceWizard
					DataSourceDesignSession dsDes = DataSourceDesignSession.startNewDesign(extId, "datasource_"  +dial.getName(), //$NON-NLS-1$
							null,
							DesignFactory.eINSTANCE.createDesignSessionRequest());
					

					WizardDialog d = new WizardDialog(sectionClient.getShell(), dsDes.getNewWizard());
					if (d.open() != WizardDialog.OK){
						return;
					}
					
					
					dataSourceDesign = ((NewDataSourceWizard)dsDes.getNewWizard()).getDataSourceDesign();
					DataSetDesignSession designSession = DataSetDesignSession.startNewDesign(
							dial.getName(), dataSetId, dataSourceDesign
							);
				

					DataSetWizard w = (DataSetWizard)designSession.getNewWizard();
					d = new WizardDialog(sectionClient.getShell(), w);
					if (d.open() != WizardDialog.OK){
						return;
					}
					dataSetDesign = w.getResponseSession().getResponseDataSetDesign();

					Activator.getDefault().getMdmProvider().getModel().getDataSources().add(dataSetDesign);
					//dataSourcePart.selectionChanged(dataSourcePart, new StructuredSelection(dataSetDesign));
					dataSourceViewer.refresh();
					dataSourceViewer.setSelection(new StructuredSelection(dataSetDesign));
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
				

			}
		});
		
		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.ModelMasterDetails_5);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)dataSourceViewer.getSelection()).toList()){
					Activator.getDefault().getMdmProvider().getModel().getDataSources().remove(o);
				}
				dataSourceViewer.refresh();
			}
		});
		
		
		ToolItem edit = new ToolItem(bar, SWT.PUSH);
		edit.setToolTipText(Messages.ModelMasterDetails_6);
		edit.setImage(Activator.getDefault().getImageRegistry().get(IconNames.EDIT));
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DataSetDesign dataSetDesign = (DataSetDesign)((IStructuredSelection)dataSourceViewer.getSelection()).getFirstElement();
				
				try{
					if (dataSetDesign.getDataSourceDesign().getName() == null || dataSetDesign.getDataSourceDesign().getName().equals("")){ //$NON-NLS-1$
						dataSetDesign.getDataSourceDesign().setName("datasource_" + dataSetDesign.getName()); //$NON-NLS-1$
					}
					
					
					DesignSessionRequest req = DesignFactory.eINSTANCE.createDesignSessionRequest( dataSetDesign);
					DataSetDesignSession designSession = DataSetDesignSession.startEditDesign(req);
					DataSetWizard w = (DataSetWizard)designSession.getNewWizard();
					WizardDialog d = new WizardDialog(sectionClient.getShell(), w);
					if (d.open() != WizardDialog.OK){
						
						return;
					}
					DataSetDesign dataSetDesign333388res = 
						w.getResponseSession().getResponseDataSetDesign();
					Collections.replaceAll(Activator.getDefault().getMdmProvider().getModel().getDataSources(), dataSetDesign, dataSetDesign333388res);
					dataSourceViewer.refresh();
				}catch(Exception ex){
					ex.printStackTrace();
				}
							
			}
		});
		
		
		ToolItem browse = new ToolItem(bar, SWT.PUSH);
		browse.setToolTipText(Messages.ModelMasterDetails_9);
		browse.setImage(Activator.getDefault().getImageRegistry().get(IconNames.BROWSE));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dataSourceViewer.getSelection().isEmpty()){
					return;
				}
				
				DataSetDesign dsd = (DataSetDesign)((IStructuredSelection)dataSourceViewer.getSelection()).getFirstElement();
				
				OdaInput input = DataSetDesignConverter.convert(dsd);
				IQuery query = null;
				
				try{
					query = QueryHelper.buildquery(input);
					
				}catch(Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(dataSourceViewer.getControl().getShell(), 
							Messages.ModelMasterDetails_10, Messages.ModelMasterDetails_11 + ex.getMessage());
					return;
				}
				
				
				
				DialogBrowseDatas d = new DialogBrowseDatas(dataSourceViewer.getControl().getShell(), query, input.getQueryText());
				d.open();
				
			}
		});
		toolkit.paintBordersFor(bar);
		
	}
	
	private TreeViewer createDataSourceViewer(Composite parent, final IManagedForm managedForm){
		final TreeViewer v = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		v.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		v.setContentProvider(new DataSourceContentProvider());
		v.setLabelProvider(new DataSourceLabelProvider());
		v.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(dataSourcePart, v.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});

		return v;
	}
	
	
	private void createSectionToolBar(Composite sectionClient) {
		ToolBar bar = new ToolBar(sectionClient, SWT.FLAT);
		bar.setBackground(toolkit.getColors().getBackground());
		bar.setLayoutData(new GridData());
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_12);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Entity en = MdmFactory.eINSTANCE.createEntity();
				en.setName(Messages.ModelMasterDetails_13);
				Activator.getDefault().getMdmProvider().getModel().getEntities().add(en);
				viewerPart.selectionChanged(viewerPart, new StructuredSelection(en));
			}
		});
		
		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.ModelMasterDetails_14);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)modelViewer.getSelection()).toList()){
					Activator.getDefault().getMdmProvider().getModel().getEntities().remove(o);
				}
				viewerPart.selectionChanged(viewerPart, null);
			}
		});
		
		
		
		ToolItem browse = new ToolItem(bar, SWT.PUSH);
		browse.setToolTipText(Messages.ModelMasterDetails_15);
		browse.setImage(Activator.getDefault().getImageRegistry().get(IconNames.BROWSE));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Entity entity = (Entity)((IStructuredSelection)modelViewer.getSelection()).getFirstElement();
				
				
				DialogBrowseEntity d = new DialogBrowseEntity(
						viewerPart.getSection().getShell(),
						Activator.getDefault().getMdmProvider().getStore(entity),
						entity);
				d.open();
			}
		});
		
		browse = new ToolItem(bar, SWT.PUSH);
		browse.setToolTipText(Messages.ModelMasterDetails_4);
		browse.setImage(Activator.getDefault().getImageRegistry().get(IconNames.ERROR));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Entity entity = (Entity)((IStructuredSelection)modelViewer.getSelection()).getFirstElement();
				
				
				DialogBrowseEntity d = new DialogBrowseEntityErrors(
						viewerPart.getSection().getShell(),
						Activator.getDefault().getMdmProvider().getStore(entity),
						entity);
				d.open();
			}
		});
		
		toolkit.paintBordersFor(bar);
		
	}
	
	
	private void createSynchroToolBar(final Composite sectionClient) {
		ToolBar bar = new ToolBar(sectionClient, SWT.FLAT);
		bar.setBackground(toolkit.getColors().getBackground());
		bar.setLayoutData(new GridData());
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ModelMasterDetails_16);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Synchronizer s = MdmFactory.eINSTANCE.createSynchronizer();
//				s.setMapper(MdmFactory.eINSTANCE.createMapper());
				s.setName(Messages.ModelMasterDetails_17);
				synchronizerInput.add(s);
				viewerPart.selectionChanged(synchronizerPart, new StructuredSelection(s));
			}
		});
		
		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.ModelMasterDetails_18);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)synchronizerViewer.getSelection()).toList()){
					synchronizerInput.remove(o);
				}
				viewerPart.selectionChanged(synchronizerPart, null);
			}
		});
		
		
		
		final ToolItem diff = new ToolItem(bar, SWT.PUSH);
		diff.setToolTipText(Messages.ModelMasterDetails_19);
		diff.setImage(Activator.getDefault().getImageRegistry().get(IconNames.SYNCHRONIZER));
		diff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Synchronizer sync = (Synchronizer)((IStructuredSelection)synchronizerViewer.getSelection()).getFirstElement();			
				
				
				try {
					IEntityStorage store = Activator.getDefault().getMdmProvider().getStore(sync.getEntity());
					SynchroPerformer perf = RuntimeFactory.eINSTANCE.createSynchroPerformer(store);
					DiffResult res = perf.performDiff(sync);
					DialogDiff d = new DialogDiff(sectionClient.getShell(), 
							store
							, res, sync.getEntity());
					d.open();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			
			}
			
		});
		toolkit.paintBordersFor(bar);
		
	}
	
	private TableViewer createSynchronizerViewer(Composite parent, final IManagedForm managedForm){
		final TableViewer v = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		v.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		v.getTable().setHeaderVisible(true);
		v.getTable().setLinesVisible(true);
	
		TableViewerColumn col = new TableViewerColumn(v, SWT.NONE);
		col.getColumn().setText(Messages.ModelMasterDetails_20);
		col.getColumn().setWidth(200);
		
				
		v.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(synchronizerPart, v.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
		
		
	
		
		
		return v;
	}

	private TreeViewer createModelViewer(Composite parent, final IManagedForm managedForm){
		final TreeViewer v = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
		v.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		v.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(viewerPart, v.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
		v.setLabelProvider(new MdmLabelProvider());
		v.setContentProvider(new MdmContentProvider());
		ObservableListTreeContentProvider cp = new ObservableListTreeContentProvider(
				new MdmObservableFactory(),
				new MdmAdvisor());
		
		v.setContentProvider(cp);
		
		
		
		
		
		IObservableSet knownElements = cp.getKnownElements();
		final IObservableMap firstNames = EMFProperties.value(MdmPackage.Literals.ENTITY__NAME).observeDetail(
				knownElements);
		final IObservableMap lastNames = EMFProperties.value(MdmPackage.Literals.ATTRIBUTE__NAME).observeDetail(
				knownElements);
		
		IObservableMap[] labelMaps = { firstNames, lastNames};
		ILabelProvider labelProvider = new ObservableMapLabelProvider(labelMaps){
			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof Entity){
					return super.getColumnText(element, 0);
				}
				else if (element instanceof Attribute){
					return super.getColumnText(element, 1);
				}
//				PlatformUI.
//				PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.
				return super.getColumnText(element, columnIndex);
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof Entity){
					return Activator.getDefault().getImageRegistry().get(IconNames.ENTITY);
				}
				if (element instanceof Attribute){
					return Activator.getDefault().getImageRegistry().get(IconNames.ATTRIBUTE);
				}
				return super.getImage(element);
			}
		};
		v.setLabelProvider(labelProvider);
//		List l = new ArrayList();
//		l.add(Activator.getDefault().getMdmModel());
//		
//		WritableList input = new WritableList(l, Model.class);
		

		
//		
//		ViewerSupport.bind(v, 
//				Activator.getDefault().getMdmModel(), 
//				EMFProperties.list(MdmPackage.Literals.MODEL__ENTITIES), 
//				EMFProperties.values(MdmPackage.Literals.ENTITY__NAME,
//						MdmPackage.Literals.ATTRIBUTE__NAME));
		
		
		return v;
	}

	public void refresh(){
		modelViewer.setInput(Activator.getDefault().getMdmProvider().getModel());
		dataSourceViewer.setInput(Activator.getDefault().getMdmProvider().getModel().getDataSources());

		synchronizerInput = new WritableList(Activator.getDefault().getMdmProvider().getModel().getSynchronizers(), Synchronizer.class);
		ViewerSupport.bind(
				synchronizerViewer, 
				synchronizerInput, 
				EMFProperties.value(MdmPackage.Literals.SYNCHRONIZER__NAME));

	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		
		detailsPart.registerPage(MdmFactory.eINSTANCE.createEntity().getClass(), new CompositeEntity());
		detailsPart.registerPage(MdmFactory.eINSTANCE.createAttribute().getClass(), new CompositeAttribute());
		detailsPart.registerPage(MdmFactory.eINSTANCE.createSynchronizer().getClass(), new DetailSynchronizer());
	}
	
	
	private static class _ExpansionListener implements IExpansionListener{
		@Override
		public void expansionStateChanging(ExpansionEvent e) {
			((Section)e.getSource()).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, e.getState(), 1, 1));
			
		}
		
		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			((Section)e.getSource()).getParent().layout(true);
			
		}
	}

}
