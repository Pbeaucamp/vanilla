package bpm.fd.design.ui.properties.views;

import java.util.HashMap;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.comment.ComponentComment;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.components.definition.jsp.ComponentD4C;
import bpm.fd.api.core.model.components.definition.jsp.ComponentFlourish;
import bpm.fd.api.core.model.components.definition.jsp.ComponentJsp;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.olap.ComponentFaView;
import bpm.fd.api.core.model.components.definition.report.ComponentKpi;
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.components.definition.styledtext.ComponentStyledTextInput;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.timer.ComponentTimer;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.editparts.CellWrapper;
import bpm.fd.design.ui.editor.editparts.ComponentPart;
import bpm.fd.design.ui.editor.editparts.FolderPart;
import bpm.fd.design.ui.editor.editparts.ModelPart;
import bpm.fd.design.ui.editor.editparts.StackableCellPart;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.properties.model.chart.DatasChartEditor;
import bpm.fd.design.ui.properties.model.editors.ButtonEditor;
import bpm.fd.design.ui.properties.model.editors.CommentEditor;
import bpm.fd.design.ui.properties.model.editors.D4CEditor;
import bpm.fd.design.ui.properties.model.editors.DataGridEditor;
import bpm.fd.design.ui.properties.model.editors.DrivenStackedCellEditor;
import bpm.fd.design.ui.properties.model.editors.FlourishEditor;
import bpm.fd.design.ui.properties.model.editors.FolderEditor;
import bpm.fd.design.ui.properties.model.editors.GaugeEditor;
import bpm.fd.design.ui.properties.model.editors.HyperLinkEditor;
import bpm.fd.design.ui.properties.model.editors.IComponentEditor;
import bpm.fd.design.ui.properties.model.editors.IPropertyEditor;
import bpm.fd.design.ui.properties.model.editors.JspEditor;
import bpm.fd.design.ui.properties.model.editors.LabelEditor;
import bpm.fd.design.ui.properties.model.editors.MapEditor;
import bpm.fd.design.ui.properties.model.editors.MapOsmEditor;
import bpm.fd.design.ui.properties.model.editors.MapWmsEditor;
import bpm.fd.design.ui.properties.model.editors.MarkdownEditor;
import bpm.fd.design.ui.properties.model.editors.OlapViewEditor;
import bpm.fd.design.ui.properties.model.editors.PictureEditor;
import bpm.fd.design.ui.properties.model.editors.ReportEditor;
import bpm.fd.design.ui.properties.model.editors.SlicerEditor;
import bpm.fd.design.ui.properties.model.editors.StructureEditor;
import bpm.fd.design.ui.properties.model.editors.StyledTextEditor;
import bpm.fd.design.ui.properties.model.editors.TimerEditor;
import bpm.fd.design.ui.properties.model.filter.FilterEditor;
import bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart;
import bpm.fd.design.ui.structure.gef.editparts.CellPart;
import bpm.fd.design.ui.structure.gef.editparts.StructureEditPart;

public class PropertiesViewPart extends ViewPart implements ISelectionListener{
	private Color EXPAND_BAR_COLOR = new Color(Display.getDefault(), 121, 121 ,121);
	private HashMap<Class<?>, IComponentEditor> bars = new HashMap<Class<?>, IComponentEditor>();
	private HashMap<Class<?>, StructureEditor> structureBars = new HashMap<Class<?>, StructureEditor>();
	private Composite main;
	private StackLayout stack;
	
	private IPropertyEditor currentEditor;
	
		
	public PropertiesViewPart() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setBackground(EXPAND_BAR_COLOR);
		
		ToolBar tb = new ToolBar(c, SWT.HORIZONTAL);
		tb.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		tb.setBackground(EXPAND_BAR_COLOR);
		
		ToolItem expand = new ToolItem(tb, SWT.PUSH);
		expand.setToolTipText("Expand All");
		expand.setImage(Activator.getDefault().getImageRegistry().get(Icons.EXPAND));
		expand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (stack.topControl != null && stack.topControl instanceof ExpandBar){
					ExpandBar bar = (ExpandBar)stack.topControl;
					for(ExpandItem i : bar.getItems()){
						i.setExpanded(true);
						i.getControl().pack(true);
						Point size = i.getControl().computeSize(bar.getClientArea().width,
								SWT.DEFAULT);
								
						i.getControl().pack(true);
						i.setHeight(size.y);
					}
				}
			}
		});
		
		ToolItem collapse = new ToolItem(tb, SWT.PUSH);
		collapse.setImage(Activator.getDefault().getImageRegistry().get(Icons.COLLAPSE));
		collapse.setToolTipText("Collapse All");
		collapse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (stack.topControl != null && stack.topControl instanceof ExpandBar){
					ExpandBar bar = (ExpandBar)stack.topControl;
					for(ExpandItem i : bar.getItems()){
						i.setExpanded(false);
						i.setHeight(0);
					}
				}
			}
		});

		Composite p = new Composite(c, SWT.NONE);
		p.setBackground(EXPAND_BAR_COLOR);
		p.setLayout(new GridLayout(2, false));
		p.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		final Text filter = new Text(p, SWT.BORDER);
	
		filter.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		final Button b = new Button(p, SWT.PUSH);
		b.setBackground(EXPAND_BAR_COLOR);
		b.setEnabled(false);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setImage(Activator.getDefault().getImageRegistry().get(Icons.FILTER_ON));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentEditor.next(filter.getText());
			}
		});
		filter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				b.setEnabled(!filter.getText().trim().isEmpty());
				
			}
		});
		/*
		filter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
							
			}
		});*/
		
		main = new Composite(c, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		main.setLayout(stack = new StackLayout());
		main.setBackground(EXPAND_BAR_COLOR);
	}
	
	

	@Override
	public void setFocus() {
		

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)){
			stack.topControl = null;
			currentEditor = null;
			return;
		}
		else{
			Object o = ((IStructuredSelection)selection).getFirstElement();
			IComponentDefinition def = null;
			ComponentConfig conf = null;
			//component case
			if (o instanceof ComponentPart){
				
				def = ((ComponentPart)o).getComponent();
				conf = ((CellWrapper)((ComponentPart)o).getModel()).getCell().getConfig(def);
				
			}
			else if (o instanceof bpm.fd.design.ui.structure.gef.editparts.ComponentPart){
				def = (IComponentDefinition)((bpm.fd.design.ui.structure.gef.editparts.ComponentPart)o).getModel();
				conf = ((Cell)((EditPart)o).getParent().getModel()).getConfig(def);
			}
			if (!(o instanceof EditPart)){
				return;
			}
			if (((EditPart) o).getParent() instanceof CellPart) {
				Cell cell = (Cell) ((CellPart) ((EditPart) o).getParent())
						.getModel();
				conf = cell.getConfig(def);
			} else if (((EditPart) o).getParent() instanceof StackableCellPart) {
				StackableCell cell = (StackableCell) ((StackableCellPart) ((EditPart) o)
						.getParent()).getModel();
				conf = cell.getConfig(def);
			} else if (((EditPart) o).getParent() instanceof bpm.fd.design.ui.editor.editparts.StackableCellPart) {
				StackableCell cell = (StackableCell) ((bpm.fd.design.ui.editor.editparts.StackableCellPart) ((EditPart) o)
						.getParent()).getModel();
				conf = cell.getConfig(def);
			} else if (o instanceof ModelPart){
				
			} else if (o instanceof FolderPart || o instanceof bpm.fd.design.ui.structure.gef.editparts.FolderPart){
				
			}
			
			if (def != null){
				if (bars.get(def.getClass()) == null){
					IComponentEditor editor = createEditor(def.getClass()) ;
					if (editor == null){
						return;
					}
					bars.put(def.getClass(), editor);
				}
				
				currentEditor = bars.get(def.getClass());
				stack.topControl = bars.get(def.getClass()).getControl();
				
				bars.get(def.getClass()).setInput((EditPart)o, conf, def);
				main.layout();

				return;
			}
			
			//structureElement selection
			IStructureElement struc = null;
			if (o instanceof StructureEditPart){
				struc = (IStructureElement)((StructureEditPart)o).getModel();
			}
			else if (o instanceof AbstractStructureElementEditPart){
				if (((AbstractStructureElementEditPart)o).getFdObject() instanceof IStructureElement){
					struc = (IStructureElement)((AbstractStructureElementEditPart)o).getFdObject();
				}
			}
			else if (o instanceof IFdObjectProvider){
				struc = (IStructureElement)((IFdObjectProvider)o).getFdObject();
			}
			
			
			if (struc != null){
				if (structureBars.get(struc.getClass()) == null){
					StructureEditor editor = createStuctureEditor(struc.getClass()) ;
					if (editor == null){
						return;
					}
					structureBars.put(struc.getClass(), editor);
				}
				currentEditor = structureBars.get(struc.getClass());
				stack.topControl = structureBars.get(struc.getClass()).getControl();
				structureBars.get(struc.getClass()).setInput((EditPart)o, struc);
				main.layout();
				return;
			}
			
		}
		
	}

	private StructureEditor createStuctureEditor(
			Class<? extends IStructureElement> class1) {
		if (class1 == DrillDrivenStackableCell.class){
			return new DrivenStackedCellEditor(main);
		}
		if (class1 == Folder.class){
			return new FolderEditor(main);
		}
		return new StructureEditor(main);
	}

	private IComponentEditor createEditor(Class<? extends IComponentDefinition> class1) {
		if (class1 == ComponentChartDefinition.class){
			return new DatasChartEditor(main);
		}
		if (class1 == ComponentFilterDefinition.class){
			return new FilterEditor(main);
		}
		if (class1 == LabelComponent.class){
			return new LabelEditor(main);
		}
		if (class1 == ComponentPicture.class){
			return new PictureEditor(main);
		}
		if (class1 == ComponentLink.class){
			return new HyperLinkEditor(main);
		}
		if (class1 == ComponentButtonDefinition.class){
			return new ButtonEditor(main);
		}
		if (class1 == ComponentStyledTextInput.class){
			return new StyledTextEditor(main);
		}
		if (class1 == ComponentTimer.class){
			return new TimerEditor(main);
		}
		if (class1 == ComponentComment.class){
			return new CommentEditor(main);
		}
		if (class1 == ComponentReport.class){
			return new ReportEditor(main);
		}
		if (class1 == ComponentKpi.class){
			return new ReportEditor(main);
		}
		if (class1 == ComponentFaView.class){
			return new OlapViewEditor(main);
		}
		if (class1 == ComponentJsp.class){
			return new JspEditor(main);
		}
		if (class1 == ComponentD4C.class){
			return new D4CEditor(main);
		}
		if (class1 == ComponentMap.class){
			return new MapEditor(main);
		}
		if (class1 == ComponentMapWMS.class){
			return new MapWmsEditor(main);
		}
		if (class1 == ComponentGauge.class){
			return new GaugeEditor(main);
		}
		if (class1 == ComponentDataGrid.class){
			return new DataGridEditor(main);
		}
		if (class1 == ComponentSlicer.class){
			return new SlicerEditor(main);
		}
		if (class1 == ComponentOsmMap.class) {
			return new MapOsmEditor(main);
		}
		if (class1 == ComponentMarkdown.class) {
			return new MarkdownEditor(main);
		}
		if (class1 == ComponentFlourish.class){
			return new FlourishEditor(main);
		}
		return null;
	}

	

}
