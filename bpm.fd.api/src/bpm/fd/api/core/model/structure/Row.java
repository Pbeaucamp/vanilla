package bpm.fd.api.core.model.structure;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class Row implements IStructureElement{

	private IStructureElement parentStructureElement;
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<Cell> rowCells = new ArrayList<Cell>();
	private boolean headerRow = false;
	
	
	public Row(){}
	public Row(List<Cell> rowCells){
		this.rowCells.addAll(rowCells);
	}
	
	/**
	 * @return the headerRow
	 */
	public boolean isHeaderRow() {
		return headerRow;
	}

	/**
	 * @param headerRow the headerRow to set
	 */
	public void setHeaderRow(boolean headerRow) {
		this.headerRow = headerRow;
	}

	public boolean addCell(Cell cell){
//		cell.setParentStructureElement(this);
		boolean b = rowCells.add(cell);
//		if (b){
//			listeners.firePropertyChange(P_CONTENT_ADDED, null, cell);
//		}
		return b;
	}
	

	
	
	public IStructureElement getParentStructureElement() {
		return parentStructureElement;
	}
	
	public void setParentStructureElement(IStructureElement parent){
		this.parentStructureElement = parent;
	}
	
	
	

	public boolean addToContent(IStructureElement element) {
		if (element instanceof Cell){
			return addCell((Cell)element);
		}
		return false;
	}

	



	

	public List<Cell> getCells() {
		return new ArrayList<Cell>(rowCells);
	}
	public void insertCellAt(int index, Cell merged) {
		rowCells.add(index, merged);
		
	}

	public List<IBaseElement> getContent() {
		
		return new ArrayList<IBaseElement>(rowCells);
	}

	public String getCssClass() {
		
		return null;
	}

	public boolean removeFromContent(IStructureElement element) {
		
		return false;
	}

	public void setCssClass(String css) {
		
		
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		listeners.firePropertyChange(propertyName, oldValue, newValue);
		
	}

	public Element getElement() {
		
		return null;
	}

	public String getId() {
		
		return getName();
	}

	public String getName() {
		
		return "Row";
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}
	public void removeComponent(IComponentDefinition component) {
		
		
	}
	
	public Collection<ElementsEventType> getEventsType() {
		return new ArrayList<ElementsEventType>();
	}

	public String getJavaScript(ElementsEventType type) {
		return null;
	}

	public void setJavaScript(ElementsEventType type, String script) {
		
	}
	
	public boolean hasEvents() {
		
		return false;
	}
	public List<Cell> getCellsUsingParameterProviderNames(String oldComponentName) {
		return new ArrayList<Cell>();
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return null;
	}
	
}
