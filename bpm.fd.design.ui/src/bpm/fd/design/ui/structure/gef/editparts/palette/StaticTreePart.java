package bpm.fd.design.ui.structure.gef.editparts.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.internal.FdComponentType;

public class StaticTreePart  extends AbstractTreeEditPart {
	public static final int AVAILABLECOMPONENTS_NODE = 12;
//	public static final int CHART_NODE = 0;
//	public static final int FILTER_NODE = 1;
//	public static final int REPORT_NODE = 2;
//	public static final int HYPERLINK_NODE = 3;
//	public static final int PICTURE_NODE = 4;
//	public static final int LABEL_NODE = 5;
//	public static final int BUTTONS_NODE = 6;
//	public static final int JSP_NODE = 7;
//	public static final int DATAGRID_NODE = 8;
//	public static final int GAUGE_NODE = 9;
//	public static final int STYLEDTEXT_INPUT = 10;
	
//	private static Class<? extends IComponentDefinition>[] COMPONENT_CLASSES = new Class[]{
//		ComponentChartDefinition.class, ComponentFilterDefinition.class,
//		ComponentReport.class, ComponentLink.class, ComponentPicture.class, LabelComponent.class, ComponentButtonDefinition.class,
//		ComponentJsp.class, ComponentDataGrid.class, ComponentGauge.class, ComponentStyledTextInput.class
//	};
	
	
	private boolean isAvailableComponents = false;
	private String name;
	

	
	public StaticTreePart(String name, boolean isAvailableComponent){
		this.name = name;
		this.isAvailableComponents = isAvailableComponent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
	 */
	@Override
	protected Image getImage() {
		
		if (super.getModel() instanceof FdComponentType){
			if (((FdComponentType)super.getModel()).getImage() != null){
				return ((FdComponentType)super.getModel()).getImage().createImage();
			}
		}
		
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
	
		return reg.get(Icons.table);
		
		
	}

	
	@Override
	protected String getText() {
		return name;
	}



	@Override
	protected List getModelChildren() {
		if (isAvailableComponents){
			return new ArrayList<IComponentDefinition>(Activator.getDefault().getProject().getAvailableComponents());
		}
		else if (super.getModel() instanceof FdComponentType){
			return new ArrayList();
		}
		else{
			List l = new ArrayList();
			l.add(Table.class);
			l.add(StackableCell.class);
			l.add(DrillDrivenStackableCell.class);
			if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
				l.add(Folder.class);
			}
			
			return l;
		}
		
		
//		return super.getModelChildren();
	}

	@Override
	public void setWidget(Widget widget) {
		
		super.setWidget(widget);
		
		
	}
	 
	@Override
	public Object getModel() {
//		if (type < COMPONENT_CLASSES.length){
//			return COMPONENT_CLASSES[type];
//		}
		if (super.getModel() instanceof FdComponentType){
			return ((FdComponentType)super.getModel()).getComponentClass();
		}
		return super.getModel();
	}
	
}
