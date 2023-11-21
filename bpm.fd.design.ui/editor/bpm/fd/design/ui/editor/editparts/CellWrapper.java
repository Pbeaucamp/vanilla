package bpm.fd.design.ui.editor.editparts;

import java.awt.Point;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.geometry.Rectangle;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ISizeable;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.IContainer;

public class CellWrapper implements IContainer{
	
	
	private Cell cell;
	
	public CellWrapper(Cell cell){
		this.cell = cell;
	}
	
	public void addListener(PropertyChangeListener listener){
		cell.addPropertyChangeListener(listener);
		cell.getContent().get(0).addPropertyChangeListener(listener);
	}
	public void removeListener(PropertyChangeListener listener){
		cell.removePropertyChangeListener(listener);
		if (!cell.getContent().isEmpty()){
			cell.getContent().get(0).removePropertyChangeListener(listener);
		}
		
	}
	
	public Cell getCell(){
		return cell;
	}
	
	private void fire(String n, Object old, Object newValue){
		cell.firePropertyChange(n, old, newValue);
		cell.getContent().get(0).firePropertyChange(n, old, newValue);
	}
	
	public void layout(Rectangle layout){
		Rectangle old = new Rectangle(
				cell.getPosition().x,
				cell.getPosition().y,
				cell.getSize().x,
				cell.getSize().y);
		
		cell.setPosition(layout.x, layout.y);
		cell.setSize(layout.width, layout.height);
		fire(EVENT_LAYOUT, old, layout);
	}

	@Override
	public Point getPosition() {
		return cell.getPosition();
	}

	@Override
	public Point getSize() {
		return cell.getSize();
	}

	@Override
	public void setPosition(int x, int y) {
		Point p = cell.getPosition();
		cell.setPosition(x, y);
		fire(EVENT_LAYOUT, p, cell.getPosition());
	}

	@Override
	public void setSize(int width, int height) {
		Point p = cell.getSize();
		cell.setSize(width, height);
		
		for(IBaseElement e : cell.getContent()){
			if (e instanceof ISizeable){
				((ISizeable)e).setWidth(width);
				((ISizeable)e).setHeight(height);
			}
		}
		fire(EVENT_LAYOUT, p, getSize());
		
	}
}
