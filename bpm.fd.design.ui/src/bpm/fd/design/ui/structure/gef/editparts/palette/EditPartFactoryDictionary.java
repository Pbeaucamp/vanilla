package bpm.fd.design.ui.structure.gef.editparts.palette;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.ui.parts.TreeViewer;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.internal.FdComponentType;
import bpm.fd.design.ui.structure.gef.editparts.DictionaryTreePart;

public class EditPartFactoryDictionary implements EditPartFactory{

	private TreeViewer viewer;
	public EditPartFactoryDictionary(TreeViewer viewer){
		this.viewer = viewer;
	}
	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart p = null;
		if (model instanceof Dictionary){
			p = new DictionaryTreePart();
			
			
			
		}
		else if (model instanceof  IComponentDefinition){
			p = new ComponentTreeEditPart();
		}
		else if (model == Table.class){
			p = new PaletteTablePart();
		}
		else if (model == Folder.class){
			p = new PaletteFolderPart();
		}
		else if(model == StackableCell.class) {
			p = new PaletteStackableCellPart();
		}
		else if(model == DrillDrivenStackableCell.class) {
			p = new PaletteDrillDrivenCellPart();
		}
		
		if (model instanceof String){
		
			for(int k = 0; k < DictionaryTreePart.nodes.length; k++){
				if (DictionaryTreePart.nodes[k].equals(model)){
					p = new StaticTreePart((String)model,  k == DictionaryTreePart.nodes.length - 1);
					break;
				}
			}
			
		}
		else if (model instanceof FdComponentType){
			p = new StaticTreePart(((FdComponentType)model).getName(), false);
		}
		
		p.setModel(model);
		return p;
	}

}
