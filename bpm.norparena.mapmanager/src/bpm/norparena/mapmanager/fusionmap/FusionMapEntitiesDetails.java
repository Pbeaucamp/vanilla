package bpm.norparena.mapmanager.fusionmap;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.viewers.FusionMapEntityLabelProvider;
import bpm.norparena.mapmanager.fusionmap.viewers.FusionMapEntityLabelProvider.FusionMapEntityAttribute;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;

public class FusionMapEntitiesDetails implements IDetailsPage{

	
	
	private TableViewer viewer;
	private IManagedForm managedForm;
	private FormToolkit toolkit;
	
	public FusionMapEntitiesDetails(){}
	public FusionMapEntitiesDetails(FormToolkit toolkit){
		this.toolkit = toolkit;
	}
	
	
	public TableViewer getViewer(){
		return viewer;
	}
	
	@Override
	public void createContents(Composite parent) {
		if (parent.getLayout() == null){
			parent.setLayout(new GridLayout());
			parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		
		
		FormToolkit toolkit = managedForm == null ? this.toolkit : managedForm.getToolkit();
		
//		Section main = toolkit.createSection(parent, Section.EXPANDED);
//		main.setLayout(new GridLayout());
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(toolkit.createTable(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION));
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		main.setClient(viewer.getTable());
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapEntitiesDetails_0);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.InternalId));
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapEntitiesDetails_1);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.LongName));
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapEntitiesDetails_2);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.ShortName));
		
		
	}

	@Override
	public void commit(boolean onSave) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void initialize(IManagedForm form) {
		this.managedForm = form;
		
	}

	@Override
	public boolean isDirty() {
		
		return false;
	}

	@Override
	public boolean isStale() {
		
		return false;
	}

	@Override
	public void refresh() {
		
		
	}

	@Override
	public void setFocus() {
		
		
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (selection.isEmpty()){
			viewer.setInput(new ArrayList<Object>());
			return;
		}
		
		try{
			
			Object sel = ((IStructuredSelection)selection).getFirstElement();
			if(sel instanceof IFusionMapObject) {
				IFusionMapObject fusionMap = (IFusionMapObject)sel;
				viewer.setInput(fusionMap.getSpecificationsEntities());
			}
			else if(sel instanceof IOpenLayersMapObject) {
				IOpenLayersMapObject olMap = (IOpenLayersMapObject)sel;
				viewer.setInput(olMap.getEntities());
			}
			
			viewer.getTable().getParent().layout(true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}

}
