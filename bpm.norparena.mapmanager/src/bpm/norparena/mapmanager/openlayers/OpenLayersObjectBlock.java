package bpm.norparena.mapmanager.openlayers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.FusionMapEntitiesDetails;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.model.openlayers.impl.OpenLayersMapObject;

public class OpenLayersObjectBlock extends MasterDetailsBlock {

	private TableViewer viewer;
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		

		Section main = toolkit.createSection(parent, Section.EXPANDED);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(toolkit.createTable(main, SWT.H_SCROLL | SWT.V_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION));
		viewer.getTable().setLayout(new GridLayout());
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.setContentProvider(new ArrayContentProvider());
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.OpenLayersObjectBlock_0);
		col.getColumn().setWidth(50);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IOpenLayersMapObject)element).getId() + ""; //$NON-NLS-1$
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.OpenLayersObjectBlock_2);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IOpenLayersMapObject)element).getName();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.OpenLayersObjectBlock_3);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IOpenLayersMapObject)element).getType();
			}
		});
		
		main.setClient(viewer.getControl());
		
		final SectionPart sectionPart = new SectionPart(main);
		managedForm.addPart(sectionPart);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(sectionPart, viewer.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		OpenLayersMapToolbarBuilder builder = new OpenLayersMapToolbarBuilder(viewer);
		managedForm.getForm().setHeadClient(builder.createToolbarManager().createControl(managedForm.getForm().getForm().getHead()));
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(OpenLayersMapObject.class, new FusionMapEntitiesDetails());
	}

	@Override
	protected void applyLayout(Composite parent) {
		super.applyLayout(parent);
		
		if (viewer != null && viewer.getInput() == null){
			try{
				viewer.setInput(Activator.getDefault().getDefinitionService().getOpenLayersMapObjects());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(parent.getShell(), Messages.OpenLayersObjectBlock_4, Messages.OpenLayersObjectBlock_5 + ex.getMessage());
			}
		}
	}
}
