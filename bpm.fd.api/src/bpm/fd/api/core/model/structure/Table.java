package bpm.fd.api.core.model.structure;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.events.ElementsEventType;

public class Table implements IStructureElement{
	
	public static final String P_HORIZONTAL_MERGE = "bpm.fd.api.core.model.structure.table.horizontalcellmerge";
	public static final String P_VERTICAL_MERGE = "bpm.fd.api.core.model.structure.table.verticalcellmerge";
	public static final String P_COLUMN_CHANGED = "bpm.fd.api.core.model.structure.table.column";
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private IStructureElement parentStructureElement;
	
	private List<List<Cell>> detailsRows = new ArrayList<List<Cell>>();
	
	
	private int columnNumber = 1;
	private int rowNumber = 1;
	
	private String name = "Table";
	private String id;
	private String cssClass;
	
	
	private HashMap<ElementsEventType, String> eventScript = new HashMap<ElementsEventType, String>();
	/**
	 * @return the cssClass
	 */
	public String getCssClass() {
		return cssClass;
	}

	/**
	 * @param cssClass the cssClass to set
	 */
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	protected Table(String id, String name){
		setName(name);
		this.id = id;
		
		for(ElementsEventType e : ElementsEventType.getAvailablesFor(this.getClass())){
			eventScript.put(e, "");
		}
	}
	
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		listeners.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public boolean addDetailsRow(List<Cell> newRow){

		for(Cell c : newRow){
			if ( c != null){
				c.setParentStructureElement(this);
			}
			
		}
		detailsRows.add(newRow);
		rowNumber ++ ;
		
		int maxWidth = 0;
		for(List<Cell> r : detailsRows){
			int cur = 0;
			for(Cell c : r){
				if (c != null){
					cur += c.getColSpan();
				}
			}
			if (maxWidth < cur){
				maxWidth = cur;
			}
		}
			
		columnNumber = maxWidth;
		
		return true;
	}
	
	
	
	public boolean addDetailsRow(FactoryStructure factory){
		List<Cell> row = new ArrayList<Cell>();
		for(int i = 0; i < columnNumber; i++){
			Cell c  = factory.createCell("Cell", 1, 1);
			c.setParentStructureElement(this);
			row.add(c);
		}
		boolean b =  detailsRows.add(row);
		if (b){
			
			if (rowNumber < detailsRows.size()){
				rowNumber++;
			}
			listeners.firePropertyChange(P_CONTENT_ADDED, null, row);
		}
		return b;
	}
	
	
	
	public boolean removeDetailsRow(Row newRow){
		boolean b = false;
		for(List<Cell> r : getDetailsRows()){
			if (r.size() == newRow.getCells().size()){
				boolean flag = true;
				for(Cell c : r){
					if (!newRow.getCells().contains(c)){
						flag = false;
						break;
					}
				}
				
				if (flag){
					b = detailsRows.remove(r);
					break;
				}
			}
		}

		if (b){
		
			listeners.firePropertyChange(P_CONTENT_REMOVED, null, newRow);
		}
		return b;
		
	}
	
	
	
	/**
	 * @return the columnNumber
	 */
	public int getColumnNumber() {
		return columnNumber;
	}
	/**
	 * @param columnNumber the columnNumber to set
	 */
	protected void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}
	/**
	 * @return the rowNumber
	 */
	public int getRowNumber() {
		return rowNumber;
	}
	/**
	 * @param rowNumber the rowNumber to set
	 */
	protected void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return "table_"  + id;
	}
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("table");
		e.addAttribute("id", getId().substring("table_".length()));
		e.addAttribute("name", getName());
		if (getCssClass() != null){
			e.addAttribute("cssClass", getCssClass());
		}
		
		Element events = e.addElement("events");
		for(ElementsEventType evt : getEventsType()){
			String sc = getJavaScript(evt);
			if ( sc != null && !"".equals(sc)){
				Element _e = events.addElement("event").addAttribute("type", evt.name());
				_e.addCDATA(sc);
			}
		}
		
		for(List<Cell> rows : getDetailsRows()){
			Element row = e.addElement("row");
			for(Cell c : rows){
				if (c !=null){
					row.add(c.getElement());
				}
				else{
					row.addElement("empty");
				}
				
			}
		}
		
		return e;
	}



	/**
	 * @return the detailsRows : there can be some null Cell because of the spanning
	 */
	public List<List<Cell>> getDetailsRows() {
		List<List<Cell>> l = new ArrayList<List<Cell>>();
		l.addAll(detailsRows);
		return l;
	}

	public IStructureElement getParentStructureElement() {
		return parentStructureElement;
	}
	
	public void setParentStructureElement(IStructureElement parent){
		this.parentStructureElement = parent;
	}

	

	private void removeCell(Cell c){
		for(List<Cell> l : detailsRows){
			l.remove(c);
		}
	}
	
	public boolean removeFromContent(IStructureElement element) {
		if (element instanceof Row){
			for(Cell c : ((Row)element).getCells()){
				removeCell(c);
			}
		}
		else if (element instanceof List){
			for(Cell c : (List<Cell>)element){
				removeCell(c);
			}
		}
		else if (element instanceof Cell){
			removeCell((Cell)element);
			
		}
		listeners.firePropertyChange(P_CONTENT_REMOVED, null, element);
		return true;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}



	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}

	public void initSize(FactoryStructure factory, int cols, int rows) {
		this.columnNumber = cols;
		this.rowNumber = rows;
		
		// create Content
		for(int i = 0 ; i < rowNumber; i++){
			addDetailsRow(factory);
		}
		
	}
	
	/**
	 * 
	 * @param c
	 * @return the position of the Cell inside the Table
	 */
	public Point getPosition(Cell c){
		for(int i = 0; i < detailsRows.size(); i++){
			for(int k = 0; k< detailsRows.get(i).size(); k++){
				if (detailsRows.get(i).get(k) == c){
					return new Point(k, i);
				}
			}
		}
		return null;
	}
	
	/**
	 * merge the cells, their content will be on the resultingCell
	 * @param cells
	 */
	public void mergeHorizontalCells(FactoryStructure factory, int rowNumber, List<Cell> cells){
		List<IBaseElement> content = new ArrayList<IBaseElement>();
		
		int colSpan = 0;
		int rowSpan = 0;
		for(Cell c : cells){
			colSpan += c.getColSpan();
			rowSpan = c.getRowSpan();
		}
		Cell merged = factory.createCell("Cell", colSpan, rowSpan);
		merged.setParentStructureElement(this);
		
		
		Integer minX = null;
		
		for(Cell c : cells){
			Point p = getPosition(c);
			if (minX == null || minX.intValue() > p.x){
				minX = p.x;
				
			}
			for(IBaseElement e : c.getContent()){
				content.add(e);
				merged.addBaseElementToContent(e);
				if(e instanceof IComponentDefinition) {
					
					for(ComponentParameter param : ((IComponentDefinition)e).getParameters()) {
					
						String n = c.getConfig((IComponentDefinition)e).getComponentNameFor(param);
						if(n != null && !n.equals("")) {
							merged.getConfig((IComponentDefinition)e).setParameterOrigin(param, n);
						}
					}
					
				}
				c.removeBaseElementToContent(e);
			}
			detailsRows.get(rowNumber).set(p.x, null);
			
		}
		
		detailsRows.get(rowNumber).set(minX, merged);
		
		firePropertyChange(P_HORIZONTAL_MERGE, null, merged);
		
	}
	
	
	/**
	 * merge the cells, their content will be on the resultingCell
	 * @param cells
	 */
	public void mergeVerticalCells(FactoryStructure factory, int colNumber, List<Cell> cells){
		List<IBaseElement> content = new ArrayList<IBaseElement>();
		
		int rowSpan = 0;
		int colSpan = 1;
		for(Cell c : cells){
			rowSpan += c.getRowSpan();
			colSpan = c.getColSpan();
		}
		Cell merged = factory.createCell("Cell", colSpan, rowSpan);
		merged.setParentStructureElement(this);

		
		Integer minY = null;
		
		for(Cell c : cells){
			Point p = getPosition(c);
			if (minY == null || minY.intValue() > p.y){
				minY = p.y;
				
			}
			for(IBaseElement e : c.getContent()){
				content.add(e);
				merged.addBaseElementToContent(e);
				if(e instanceof IComponentDefinition) {
					
					for(ComponentParameter param : ((IComponentDefinition)e).getParameters()) {
					
						String n = c.getConfig((IComponentDefinition)e).getComponentNameFor(param);
						if(n != null && !n.equals("")) {
							merged.getConfig((IComponentDefinition)e).setParameterOrigin(param, n);
						}
					}
					
				}
				c.removeBaseElementToContent(e);
			}
			
			detailsRows.get(p.y).set(p.x, null);
			
			
			
		}
		
		detailsRows.get(minY).set(colNumber, merged);
		
		firePropertyChange(P_VERTICAL_MERGE, null, merged);
		
	}
	
	
	public int getRowPosition(Cell cell) {
		int i = 0;
		for(List<Cell > l : detailsRows){
			for(Cell c : l){
				if (c == cell){
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	public boolean addToContent(IStructureElement element) {
		return false;
	}
	
	public void addColumn(FactoryStructure factory){
		List<Cell> cells = new ArrayList<Cell>();
//		firePropertyChange(P_COLUMN_CHANGED, null, detailsRows);
		for(List<Cell> l : detailsRows){
			Cell c = factory.createCell("Cell", 1, 1);
			c.setParentStructureElement(this);
			l.add(c);
			cells.add(c);
		}
		columnNumber ++ ;
//		firePropertyChange(P_CONTENT_ADDED, null, cells);
		firePropertyChange(P_COLUMN_CHANGED, null, detailsRows);
//		firePropertyChange(P_CONTENT_ADDED, null, cells);
		
	}

	public List<IBaseElement> getContent() {
		List<IBaseElement> l = new ArrayList<IBaseElement>();
		for(List<Cell> r : detailsRows){
			for(Cell c : r){
				if (c != null){
					l.add(c);
				}
			}
		}
		return l;
	}

	public int getColPosition(Cell c) {
		for(List<Cell> r :  getDetailsRows()){
			for(int i = 0; i < r.size(); i++){
				if (r.get(i) == c){
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param colNum
	 * @return the list of cell representing the colNum columns content
	 */
	public List<Cell> getDetailsColumns(int colNum) {
		
		List<Cell> l = new ArrayList<Cell>();
		
		for(List<Cell> r :  getDetailsRows()){
			l.add(r.get(colNum));
		}
		return l;
	}
	
	public int getColumnSize(int colNum){
		int i = 0;
		for(List<Cell> r :  getDetailsRows()){
			if (r.get(colNum) != null){
				i += r.get(colNum).getRowSpan();
			}
			
			
		}
		return i;
	}

	public void removeDetailsColumn(int colNum) {
		boolean b = true;
		for(List<Cell> r :  detailsRows){
			try{
				Cell c = r.remove(colNum);
				b = b && (c != null);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		if (b){
			this.columnNumber--;
			listeners.firePropertyChange(P_COLUMN_CHANGED, null, detailsRows);
		}
		
		
	}

	public void removeComponent(IComponentDefinition component) {
		for(List<Cell> l : getDetailsRows()){
			for(Cell c : l){
				if (c != null){
					c.removeComponent(component);
				}
				
			}
		}
		
	}

	public void insertRow(FactoryStructure factory, int selectedIndex) {
		List<Cell> row = new ArrayList<Cell>();
		for(int i = 0; i < columnNumber; i++){
			Cell c  = factory.createCell("Cell", 1, 1);
			c.setParentStructureElement(this);
			row.add(c);
		}
		detailsRows.add(selectedIndex+1, row);
			
		if (rowNumber < detailsRows.size()){
			rowNumber++;
		}
		listeners.firePropertyChange(P_CONTENT_ADDED, null, row);
		
		
	}
	
	public Collection<ElementsEventType> getEventsType() {
		return eventScript.keySet();
	}

	public String getJavaScript(ElementsEventType type) {
		return eventScript.get(type);
	}

	@Override
	public HashMap<ElementsEventType, String> getEventScript() {
		return eventScript;
	}

	public void setJavaScript(ElementsEventType type, String script) {
		if (script != null && eventScript.keySet().contains(type)){
			eventScript.put(type, script);
		}
		firePropertyChange(IBaseElement.EVENTS_CHANGED, null, script);
	}
	
	public boolean hasEvents() {
		for(ElementsEventType e : eventScript.keySet()){
			if (eventScript.get(e) != null && !"".equals(eventScript.get(e))){
				return true;
			}
		}
		return false;
	}

	public List<Cell> getCellsUsingParameterProviderNames(String oldComponentName) {
		List<Cell> l = new ArrayList<Cell>();
		
		
		for(List<Cell> lc : detailsRows){
			for(Cell c : lc){
				l.addAll(c.getCellsUsingParameterProviderNames(oldComponentName));
			}
		}
		return l;
	}

	
}
