package bpm.fd.design.ui.editor.commands;


import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.structure.IContainer;

public class LayoutCommand extends Command{
	private Rectangle layout;
	private IContainer container;
	
	public void setLayout(Rectangle rect){
		this.layout = rect;
	}
	public void setCell(IContainer container){
		this.container = container;
	}
	
	@Override
	public void execute() {
		container.setPosition(layout.x, layout.y);
		container.setSize(layout.width, layout.height);
	}
	
	
}
