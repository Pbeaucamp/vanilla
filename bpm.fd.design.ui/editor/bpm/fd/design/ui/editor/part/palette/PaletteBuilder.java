package bpm.fd.design.ui.editor.part.palette;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
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
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.StructureElementCreationFactory;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.internal.FdComponentType;

public class PaletteBuilder extends PaletteRoot{
	
	private class ModelComponentListener implements PropertyChangeListener{
		
		private void addAvailableFromDeletedStructure(IStructureElement element){
			for(IBaseElement e : element.getContent()){
				if (e instanceof IComponentDefinition){
					addAvailable((IComponentDefinition)e);
				}
				else if (e instanceof IStructureElement){
					addAvailableFromDeletedStructure((IStructureElement)e);
				}
			}
		}
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName() == IStructureElement.P_CONTENT_ADDED){
				if (evt.getNewValue() instanceof IStructureElement){
					for(IBaseElement e : ((IStructureElement)evt.getNewValue()).getContent()){
						if (e instanceof IComponentDefinition){
							removeAvailable((IComponentDefinition)e);
						}
					}
				}
				else if (evt.getNewValue() instanceof IComponentDefinition){
					removeAvailable((IComponentDefinition)evt.getNewValue());
				}
				
				
			}
			else if (evt.getPropertyName() == IStructureElement.P_CONTENT_REMOVED){
				if (evt.getNewValue() instanceof IStructureElement){
					addAvailableFromDeletedStructure((IStructureElement)evt.getNewValue());
				}
				else if (evt.getNewValue() instanceof IComponentDefinition){
					addAvailable((IComponentDefinition)evt.getNewValue());
				}
			}
			viewer.getContents().refresh();
		}
	}
	
	private PaletteDrawer availableGroup;
	private HashMap<IComponentDefinition, PaletteEntry> availablesEntries = new HashMap<IComponentDefinition, PaletteEntry>();
	
	private Dictionary dico;
	private FdModel model;
	private PaletteViewer viewer;
	private CreationToolEntry folderEntry;
	private PaletteDrawer structuresGroup;
	private FdModel currentEditedModel;
	
	public PaletteBuilder(PaletteViewer paletteViewer, Dictionary dico, FdModel model){
		this.dico = dico;
		this.model = model;
		this.viewer = paletteViewer;
		build();
		refresh(model.getProject());
		dico.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				
				
				if (evt.getPropertyName() ==Dictionary.PROPERTY_COMPONENT_ADDED){
					addAvailable((IComponentDefinition)evt.getNewValue());
				}
				else if (evt.getPropertyName() ==Dictionary.PROPERTY_COMPONENT_REMOVED){
					removeAvailable((IComponentDefinition)evt.getNewValue());
				}
				viewer.getContents().refresh();
				
			}
		});
	
		model.addPropertyChangeListener(new ModelComponentListener());
		
		if (model.getProject() instanceof MultiPageFdProject){
			for(FdModel m : ((MultiPageFdProject)model.getProject()).getPagesModels()){
				m.addPropertyChangeListener(new ModelComponentListener());
			}
		}
	}
	
	public Dictionary getDictionary(){
		return dico;
	}

	private void build(){
		//tools
		PaletteDrawer manipGroup = new PaletteDrawer("Tools");
		PanningSelectionToolEntry selectionToolEntry = new PanningSelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		manipGroup.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
		add(manipGroup);
		
		//Widgets
		Class[] widgetsClass = new Class[]{
				ComponentChartDefinition.class, ComponentFilterDefinition.class, ComponentSlicer.class,
				ComponentDataGrid.class,
				ComponentGauge.class, ComponentReport.class, ComponentKpi.class, 
				ComponentJsp.class,
				ComponentFaView.class, ComponentMap.class, ComponentMapWMS.class, ComponentOsmMap.class,
				ComponentMarkdown.class, ComponentD4C.class, ComponentFlourish.class
		};
		PaletteDrawer widgetsGroup = new PaletteDrawer("Widgets");
		add(widgetsGroup);
		for(Class c : widgetsClass){
			FdComponentType type = FdComponentType.getComponentsType(c);
			
			try {
				widgetsGroup.add(new CreationToolEntry(
						type.getName(), 
						type.getLabel(), 
						new ComponentCreationFactory(c), 
						type.getImage(), 
						null));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		//Text
		PaletteDrawer textGroup = new PaletteDrawer("Text");
		add(textGroup);
		Class[] textClass = new Class[]{
				LabelComponent.class, ComponentPicture.class, ComponentLink.class
		};
		for(Class c : textClass){
			FdComponentType type = FdComponentType.getComponentsType(c);
			
			textGroup.add(new CreationToolEntry(
					type.getName(), 
					type.getLabel(), 
					new ComponentCreationFactory(c), 
					type.getImage(), 
					null));
		}
		
		//Misc
		PaletteDrawer controlsGroup = new PaletteDrawer("Contols");
		add(controlsGroup);
		Class[] miscClass = new Class[]{
				ComponentButtonDefinition.class, ComponentTimer.class, 
				ComponentStyledTextInput.class,
				ComponentComment.class
		};
		for(Class c : miscClass){
			FdComponentType type = FdComponentType.getComponentsType(c);
			
			controlsGroup.add(new CreationToolEntry(
					type.getName(), 
					type.getLabel(), 
					new ComponentCreationFactory(c), 
					type.getImage(), 
					null));
		}
		
		//Structure
		structuresGroup = new PaletteDrawer("Containers");
		add(structuresGroup);
		
		folderEntry = new CreationToolEntry(
				"Folder", 
				"Create a Folder", 
				new StructureElementCreationFactory(this.model.getStructureFactory(), Folder.class), 
				Activator.getDefault().getImageRegistry().getDescriptor(Icons.folder), 
				null);
		if (this.model.getProject().getProjectDescriptor().getInternalApiDesignVersion() == null){
			

			structuresGroup.add(new CreationToolEntry(
					"Table", 
					"Create a Table Layout", 
					new StructureElementCreationFactory(this.model.getStructureFactory(), Table.class), 
					Activator.getDefault().getImageRegistry().getDescriptor(Icons.table), 
					null));
		}
		else {
			structuresGroup.add(new CreationToolEntry(
					"Div", 
					"Create a div", 
					new StructureElementCreationFactory(this.model.getStructureFactory(), DivCell.class), 
					Activator.getDefault().getImageRegistry().getDescriptor(Icons.table),
					null));
		}
		structuresGroup.add(new CreationToolEntry(
				"StackableCell", 
				"Create a StackableCell", 
				new StructureElementCreationFactory(this.model.getStructureFactory(), StackableCell.class), 
				Activator.getDefault().getImageRegistry().getDescriptor(Icons.table), 
				null));
		structuresGroup.add(new CreationToolEntry(
				"DrillStackableCell", 
				"Create a DrillStackableCell", 
				new StructureElementCreationFactory(this.model.getStructureFactory(), DrillDrivenStackableCell.class), 
				Activator.getDefault().getImageRegistry().getDescriptor(Icons.table), 
				null));
			
			if (this.model.getProject() instanceof MultiPageFdProject && ((MultiPageFdProject)this.model.getProject()).getFdModel() == model ){
				structuresGroup.add(folderEntry);	
			}

		
		
		//existing
		availableGroup = new PaletteDrawer("Availables");
		add(availableGroup);
	}


	public void refresh(FdModel model){
		if (model != currentEditedModel){
			currentEditedModel = model;
		}
		else{
			return;
		}
		if (model.getProject() instanceof MultiPageFdProject){
			if (model.getProject().getFdModel() == model){
				structuresGroup.remove(folderEntry);
				structuresGroup.add(folderEntry);
			}
			else{
				structuresGroup.remove(folderEntry);
			}
		}
		else{
			structuresGroup.remove(folderEntry);	
		}
	}
	
	private void refresh(FdProject project){
		
		for(IComponentDefinition def : project.getAvailableComponents()){
			FdComponentType type = FdComponentType.getComponentsType((Class<ComponentChartDefinition>) def.getClass());
			PaletteEntry entry = new CreationToolEntry(
					def.getName(), 
					"Use the component", 
					new ComponentCreationFactory(def), 
					type.getImage(), 
					null);
			availablesEntries.put(def, entry);
			
			availableGroup.add(entry);
			
		}
		
	}
	private void addAvailable(IComponentDefinition def) {
		FdComponentType type = FdComponentType.getComponentsType((Class<ComponentChartDefinition>) def.getClass());
		PaletteEntry entry = new CreationToolEntry(
				def.getName(), 
				"Use the component", 
				new ComponentCreationFactory(def), 
				type.getImage(), 
				null);
		availablesEntries.put(def, entry);
		
		availableGroup.add(entry);
		
	}
	private void removeAvailable(IComponentDefinition def) {
		PaletteEntry entry = availablesEntries.get(def);
		if (def != null){
			availableGroup.remove(entry);
		}
		
		
	}
}
