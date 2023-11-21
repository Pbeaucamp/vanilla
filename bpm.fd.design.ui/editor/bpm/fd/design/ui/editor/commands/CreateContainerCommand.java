package bpm.fd.design.ui.editor.commands;

import java.awt.Rectangle;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.structure.IContainer;
import bpm.fd.api.core.model.structure.IStructureElement;

public class CreateContainerCommand extends Command{
	private IStructureElement element;
	private EditPart host;
	private Rectangle layout;
	/**
	 * @param element the element to set
	 */
	public void setElement(IStructureElement element) {
		this.element = element;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(EditPart host) {
		this.host = host;
	}
	
	@Override
	public boolean canExecute() {
		
		return this.element != null && host != null && host.getModel() instanceof IStructureElement;
	}
	
	@Override
	public void execute() {
		if (element instanceof IContainer && layout != null){
			((IContainer)element).setPosition(layout.x,layout.y);
			((IContainer)element).setSize(layout.width,layout.height);
		}
		((IStructureElement)host.getModel()).addToContent(element);
		host.refresh();
	}
	public void setLayout(Point location, Dimension size) {
		try{
			layout = new Rectangle(location.x, location.x, size.width, size.height);
		}catch(Exception ex){
			
		}
		
	}
	
}
