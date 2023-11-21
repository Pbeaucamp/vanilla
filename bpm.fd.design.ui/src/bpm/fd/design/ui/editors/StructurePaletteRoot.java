package bpm.fd.design.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.IWizardComponent;


public class StructurePaletteRoot extends PaletteRoot implements PropertyChangeListener{

	private PaletteDrawer manipGroup, structureGroup;
	private PaletteGroup componentsGroup;
	private PaletteGroup nonUsedcomponentsGroup;
//	private PaletteDrawer filterGroup, chartGroup, reportGroup, staticGroup, jspGroup, dataGridGroup;
	private HashMap<Object, ToolEntry> tools = new HashMap<Object, ToolEntry>();
	
	private FactoryStructure factory;
	
	public StructurePaletteRoot(Dictionary dictionary, FactoryStructure factory){
		this.factory = factory;
		createTools();
		if (dictionary != null){
			for(IComponentDefinition c : dictionary.getComponents()){
//				createToolEntry(c);
			}
		}
	}
	
	private void createTools(){
		manipGroup = new PaletteDrawer(Messages.StructurePaletteRoot_0);
		PanningSelectionToolEntry selectionToolEntry = new PanningSelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		
		add(new PaletteSeparator());
		structureGroup = new PaletteDrawer(Messages.StructurePaletteRoot_1);
		
		
		CreationToolEntry cr = new CreationToolEntry(Messages.StructurePaletteRoot_2, Messages.StructurePaletteRoot_3,
                 new StructureElementCreationFactory(factory, Table.class),
                 null,
                 null);
        structureGroup.add(cr);
        tools.put(Table.class, cr);
        
        cr = new CreationToolEntry(Messages.StructurePaletteRoot_4, Messages.StructurePaletteRoot_5, 
        		new StructureElementCreationFactory(factory, StackableCell.class), 
        		null, 
        		null);
        structureGroup.add(cr);
        tools.put(StackableCell.class, cr);
        
        cr = new CreationToolEntry("DrillDrivenStackableCell", "DrillDrivenStackableCell", 
        		new StructureElementCreationFactory(factory, DrillDrivenStackableCell.class), 
        		null, 
        		null);
        structureGroup.add(cr);
        tools.put(DrillDrivenStackableCell.class, cr);
        
        if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
        	cr = new CreationToolEntry(Messages.StructurePaletteRoot_6, Messages.StructurePaletteRoot_7,
                    new StructureElementCreationFactory(factory, Folder.class),
                    null,
                    null);
           structureGroup.add(cr);
           tools.put(Folder.class, cr);
           
           
        }
        
        
		add(new PaletteSeparator());		
		componentsGroup = new PaletteGroup(Messages.StructurePaletteRoot_8);
		
//		filterGroup = new PaletteDrawer("Filters");
//		chartGroup = new PaletteDrawer("Charts");
//		reportGroup = new PaletteDrawer("Reports");
//		staticGroup = new PaletteDrawer("Static Component");
//		jspGroup = new PaletteDrawer("Jsp Component");
//		dataGridGroup = new PaletteDrawer("DataGrid");
		
//		componentsGroup.add(filterGroup);
		componentsGroup.add(new PaletteSeparator());
//		componentsGroup.add(chartGroup);
		nonUsedcomponentsGroup = new PaletteGroup(Messages.StructurePaletteRoot_9);
		
		for(IWizardCategory c : NewWizardRegistry.getInstance().getRootCategory().getCategories()){
			if (c.getId().equals("bpm.fd.design.ui.freedashComponentCategory")){ //$NON-NLS-1$
				for(IWizardDescriptor d : c.getWizards()){
					try {
						IWorkbenchWizard _w = d.createWizard();
						
						if (_w instanceof IWizardComponent){
							cr = new CreationToolEntry(d.getLabel(), Messages.StructurePaletteRoot_11 + d.getLabel(),
					                new ComponentFactory(((IWizardComponent)_w).getComponentClass()),
					                null,
					                null);
							componentsGroup.add(cr);
							tools.put(((IWizardComponent)_w).getComponentClass(), cr);
							
							
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}			
			}
			
		}
       
       
		componentsGroup.add(new PaletteSeparator());
//		componentsGroup.add(reportGroup);
//		componentsGroup.add(new PaletteSeparator());
//		componentsGroup.add(staticGroup);
//		componentsGroup.add(new PaletteSeparator());
//		componentsGroup.add(jspGroup);
//		componentsGroup.add(new PaletteSeparator());
//		componentsGroup.add(dataGridGroup);
//		componentsGroup.add(new PaletteSeparator());
		
		
		
		manipGroup.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
		add(manipGroup);
		add(structureGroup);
		add(componentsGroup);
		add(nonUsedcomponentsGroup);
		setDefaultEntry(selectionToolEntry);
		
		
		
		for(IComponentDefinition def : Activator.getDefault().getProject().getAvailableComponents()){
			
			createToolEntry(def);
			
		}
		
	}
	
	
	public ToolEntry getToolEntry(Object def){
		return tools.get(def);
	}
	
	private void createToolEntry(IComponentDefinition def){
		ToolEntry entry = new CreationToolEntry(def.getName(),def.getName(),
                new ComponentCreationfactory(def),
                null,
                null);
		
		tools.put(def, entry);
		nonUsedcomponentsGroup.add(entry);
//		
//		if (def instanceof ComponentFilterDefinition){
//			filterGroup.add(entry);
//		}
//		else if ( def instanceof ComponentChartDefinition){
//			chartGroup.add(entry);
//		}
//		else if ( def instanceof LabelComponent){
//			staticGroup.add(entry);
//		}
//		else if ( def instanceof ComponentJsp){
//			jspGroup.add(entry);
//		}
//		else if ( def instanceof ComponentDataGrid){
//			dataGridGroup.add(entry);
//		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		
		
		for(IComponentDefinition def : Activator.getDefault().getProject().getAvailableComponents()){
			
				createToolEntry(def);
			
		}
		
		
	}
	

}
