package bpm.fd.design.ui.editor.part.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.IContainer;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.editparts.CellWrapper;
import bpm.fd.design.ui.icons.Icons;

public class ActionCopy extends Action{

	public ActionCopy() {
		setText("Copy");
		setId("bpm.fd.editor.action.copy");
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.copy_16));
	}
	
	
	@Override
	public void run() {
		ISelectionService s = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		IStructuredSelection ss = (IStructuredSelection)s.getSelection();
		
		for(Object o : ss.toList()){
			if (o instanceof EditPart){
				if (((EditPart)o).getModel() instanceof IContainer){
					IContainer c = (IContainer)((EditPart)o).getModel() ;
					
					if(c instanceof CellWrapper) {
						
						FactoryStructure struct = ((FdModel) ((EditPart) ((EditPart)o).getRoot().getChildren().get(0)).getModel()).getStructureFactory();
						Cell cell = struct.createCell("cell", 1, 1);
						cell.addBaseElementToContent(((IComponentDefinition)((CellWrapper)c).getCell().getContent().get(0)).copy());
						cell.setPosition(((CellWrapper)c).getCell().getPosition().x+50, ((CellWrapper)c).getCell().getPosition().y+50);
						cell.setSize(((CellWrapper)c).getCell().getSize().x, ((CellWrapper)c).getCell().getSize().y);
	
						((FdModel) ((EditPart) ((EditPart)o).getRoot().getChildren().get(0)).getModel()).addToContent(cell);
						((EditPart) ((EditPart)o).getRoot().getChildren().get(0)).refresh();
						
					}
					
				}
			}
		}
	}
}
