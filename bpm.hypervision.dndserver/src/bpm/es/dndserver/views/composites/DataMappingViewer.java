package bpm.es.dndserver.views.composites;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.DNDOProject;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.tools.OurLogger;
import bpm.es.dndserver.views.dialogs.DialogResult;
import bpm.es.dndserver.views.providers.DependentContentProvider;
import bpm.es.dndserver.views.providers.DependentLabelProvider;
import bpm.es.dndserver.views.providers.MigrationContentProvider;
import bpm.es.dndserver.views.providers.MigrationLabelProvider;
import bpm.vanilla.platform.core.IRepositoryApi;

public class DataMappingViewer extends Composite{

	
	public static final Color remapErrorColor = new Color(Display.getDefault(), 250, 25, 120);
	public static final Color remapColor = new Color(Display.getDefault(), 25, 250 , 25);
	public static final Font fontRemap = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$
	
	private DNDOProject project;

	private AxisDirectoryItemWrapper selectedExportItem;
	
	private TreeViewer viewer;
	
	private TreeViewer viewerDependencies;
	
	private MetadataViewer viewerCurrentFMDT;
	private MetadataViewer viewerNewFMDT;
	
//	private Label lerror;
	private Button bCopy, bRedo;
	
	private FormToolkit toolkit;
	private Form form;
	
	private Button bShowErrors;
	
	public DataMappingViewer(Composite parent, int style, FormToolkit toolkit, Form form) {
		super(parent, style);
	
		this.form = form;
		this.toolkit = toolkit;
		
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createLeftTree(toolkit);
		createRightView(toolkit);
	}

	private void createRightView(FormToolkit toolkit) {
		Section section = toolkit.createSection(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setLayout(new GridLayout());
		section.setText(Messages.DataMappingViewer_1);
		
		Composite main = toolkit.createComposite(section);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout());	
		
//		lerror = new Label(main, SWT.NONE);
//		lerror.setText("No problems detected");
//		lerror.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		bCopy = new Button(main, SWT.RADIO);
		bCopy.setText(Messages.DataMappingViewer_2);
		bCopy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bCopy.setSelection(true);
		bCopy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (bCopy.getSelection() == true) {
					viewerDependencies.getTree().setEnabled(false);
					viewerNewFMDT.setEnabled(false);
					selectedExportItem.setRemaped(false);
					viewer.refresh();
				} 
				else {
				}
			}
		});
		
		bRedo = new Button(main, SWT.RADIO);
		bRedo.setText(Messages.DataMappingViewer_3);
		bRedo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bRedo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (bRedo.getSelection() == true) {
					viewerDependencies.getTree().setEnabled(true);
					selectedExportItem.setRemaped(true);
					viewerNewFMDT.setEnabled(true);
					viewer.refresh();
				} 
				else {
				}
			}
		});

		bCopy.setEnabled(false);
		bRedo.setEnabled(false);
		
		viewerDependencies = new TreeViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewerDependencies.getTree().setLayout(new GridLayout());
		viewerDependencies.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewerDependencies.setContentProvider(new DependentContentProvider());
		viewerDependencies.addFilter(new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof AxisDirectoryItemWrapper){
					return ((AxisDirectoryItemWrapper)element).getAxisItem().getType() == IRepositoryApi.FMDT_TYPE;
				}
				return false;
			}
			
		});
		viewerDependencies.setLabelProvider(new DependentLabelProvider());
		viewerDependencies.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) viewerDependencies.getSelection();
				
				Object o = ss.getFirstElement();
				
				if (o instanceof AxisDirectoryItemWrapper &&
						(((AxisDirectoryItemWrapper)o).getAxisItem().getType() == 
							IRepositoryApi.FMDT_TYPE)) {
					
					AxisDirectoryItemWrapper exportedItem = selectedExportItem;
					AxisDirectoryItemWrapper metadataItem = (AxisDirectoryItemWrapper) o;
					
					try {
						viewerCurrentFMDT.setProject(project, metadataItem, exportedItem);
						viewerNewFMDT.setProject(project, metadataItem, exportedItem);
					} catch (Exception e) {
						OurLogger.error(Messages.DataMappingViewer_4, e);
						MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.DataMappingViewer_5, e.getMessage());
					}
				}
				else {
					viewerCurrentFMDT.setEnabled(false);
					viewerNewFMDT.setEnabled(false);
				}
			}
		});
		
		viewerDependencies.getTree().setEnabled(false);
		
		viewerCurrentFMDT = new MetadataOriginViewer(main, SWT.NONE, toolkit, 
				MetadataViewer.TYPE_SOURCE);
		viewerCurrentFMDT.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		viewerNewFMDT = new MetadataViewer(main, SWT.NONE, toolkit, 
				MetadataViewer.TYPE_TARGET);
		viewerNewFMDT.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewerNewFMDT.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				viewer.refresh();
				if (project.getMessenger().hasError())
					showErrors();
				else if (project.getMessenger().hasWarnings())
					showWarnings();
				else 
					clearErrors();
			}
		});
		section.setClient(main);
	}
	
	private void createLeftTree(FormToolkit toolkit) {
		Section section = toolkit.createSection(this, Section.TITLE_BAR);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setLayout(new GridLayout());
		section.setText(Messages.DataMappingViewer_6);
		
		viewer = new TreeViewer(section, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.getTree().setLayout(new GridLayout());
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new MigrationContentProvider());
		viewer.setLabelProvider(new MigrationLabelProvider());		
		viewer.setLabelProvider(new DecoratingLabelProvider(new MigrationLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()){
			@Override
			public Color getForeground(Object element) {
				if (element instanceof AxisDirectoryItemWrapper){
					if (((AxisDirectoryItemWrapper)element).isRemaped()){
						
						if (((AxisDirectoryItemWrapper)element).remapFullyDefined()){
							return remapColor;
						}
						else{
							return remapErrorColor;
						}
						
						
					}
				}
				return super.getForeground(element);
			}
			
			@Override
			public Font getFont(Object element) {
				if (element instanceof AxisDirectoryItemWrapper){
					if (((AxisDirectoryItemWrapper)element).isRemaped()){
						return fontRemap;
					}
				}
				return super.getFont(element);
			}
		});
		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				
				if (!ss.isEmpty() && ss.getFirstElement() instanceof AxisDirectoryItemWrapper) {
					selectedExportItem = (AxisDirectoryItemWrapper) ss.getFirstElement();
					
					//selectedExportItem.getDependencies()
					
					viewerDependencies.setInput(selectedExportItem);
					viewerDependencies.expandAll();
					
					if (!selectedExportItem.hasMetaData()) {
//						lerror.setText("Error : " 
//								+ selectedExportItem.getAxisItem().getName() 
//								+ " has no metadata associated and will be ignored");
						bCopy.setEnabled(false);
						bRedo.setEnabled(false);
						viewerDependencies.getTree().setEnabled(false);
					}
					else {
//						lerror.setText("No problems detected");
						bCopy.setEnabled(true);
						bRedo.setEnabled(true);
						if (selectedExportItem.isRemaped()) {
							bCopy.setSelection(false);
							bRedo.setSelection(true);
							viewerDependencies.getTree().setEnabled(true);
							viewerNewFMDT.setEnabled(true);
							selectedExportItem.setRemaped(true);
						}
						else {
							bCopy.setSelection(true);
							bRedo.setSelection(false);			
							viewerDependencies.getTree().setEnabled(false);
							viewerNewFMDT.setEnabled(false);
							selectedExportItem.setRemaped(false);
						}
							
							
						//viewerDependencies.getTree().setEnabled(false);
					}
				}
			}
		});
		
		section.setClient(viewer.getControl());		
	}

	public void setProject(DNDOProject project) {
		this.project = project;
		
		try {
			project.loadMigrationObjects();
		} catch(Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.DataMappingViewer_7, "" + e.getMessage()); //$NON-NLS-1$
		}
		
		if (project.getMessenger().hasError())
			showErrors();
		else if (project.getMessenger().hasWarnings())
			showWarnings();
		else 
			clearErrors();
		
		viewer.setInput(project.getOutputRepository());
		viewerDependencies.setInput(null);
		viewerCurrentFMDT.showMetadata(null);
		viewerNewFMDT.showMetadata(null);
		viewer.expandAll();
	}
	
	public void clearErrors() {
		if (bShowErrors != null) {
			bShowErrors.dispose();
			form.setHeadClient(null);
		}
	}
	
	public void showWarnings() {
		bShowErrors = 
			toolkit.createButton(form.getHead(), Messages.DataMappingViewer_9, SWT.PUSH);
		
		bShowErrors.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DialogResult res = new DialogResult(Display.getCurrent().getActiveShell(), 
						project.getMessenger().getAllMessages());
				
				res.open();
			}
		});
		
		form.setHeadClient(bShowErrors);
		//form.re
	}
	
	public void showErrors() {
		Button bShowErrors = 
			toolkit.createButton(form.getHead(), Messages.DataMappingViewer_10, SWT.PUSH);
		
		bShowErrors.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DialogResult res = new DialogResult(Display.getCurrent().getActiveShell(), 
						project.getMessenger().getAllMessages());
				
				res.open();
			}
		});
		
		form.setHeadClient(bShowErrors);
		//form.re
	}
}
