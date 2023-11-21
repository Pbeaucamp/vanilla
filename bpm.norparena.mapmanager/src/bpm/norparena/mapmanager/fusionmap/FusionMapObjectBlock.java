package bpm.norparena.mapmanager.fusionmap;

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
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.model.fusionmap.impl.FusionMapObject;




public class FusionMapObjectBlock extends MasterDetailsBlock {

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
		col.getColumn().setText(Messages.FusionMapObjectBlock_0);
		col.getColumn().setWidth(50);
		col.setLabelProvider(new FusionMapLabelProvider(FusionMapAttribute.Id));
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapObjectBlock_1);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FusionMapLabelProvider(FusionMapAttribute.Name));
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapObjectBlock_2);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FusionMapLabelProvider(FusionMapAttribute.Description));
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapObjectBlock_3);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FusionMapLabelProvider(FusionMapAttribute.SwfFileName));
	
		
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
		ToolbarBuilder builder = new ToolbarBuilder(viewer);
		managedForm.getForm().setHeadClient(builder.createToolbarManager().createControl(managedForm.getForm().getForm().getHead()));

		
	}
	
	@Override
	protected void applyLayout(Composite parent) {
		super.applyLayout(parent);
		
		if (viewer != null && viewer.getInput() == null){
			try{
				viewer.setInput(Activator.getDefault().getFusionMapRegistry().getFusionMapObjects());
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(parent.getShell(), Messages.FusionMapObjectBlock_4, Messages.FusionMapObjectBlock_5 + ex.getMessage());
			}
		}
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		
		detailsPart.registerPage(FusionMapObject.class, new FusionMapEntitiesDetails());
		
	}

	private static enum FusionMapAttribute{
			Id, Name, Description, SwfFileName
	}
	private static class FusionMapLabelProvider extends ColumnLabelProvider{
		
		private FusionMapAttribute attribute;
		
		public FusionMapLabelProvider(FusionMapAttribute attribute){
			this.attribute = attribute;
		}
		
		@Override
		public String getText(Object element) {
			IFusionMapObject fm = (IFusionMapObject)element;
			String value = null;
			switch(attribute){
			case Id:
				value = fm.getId() + ""; //$NON-NLS-1$
				break;
			case Description:
				value = fm.getDescription();
				break;
			case Name:
				value = fm.getName();
				break;
			case SwfFileName:
				value = fm.getSwfFileName();
				break;
			}
			if (value == null){
				return ""; //$NON-NLS-1$
			}
			
			return value;
		}
	}
	
}
