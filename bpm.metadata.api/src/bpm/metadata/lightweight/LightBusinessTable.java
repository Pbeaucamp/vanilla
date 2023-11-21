package bpm.metadata.lightweight;

import java.util.Collection;
import java.util.List;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IFilter;

public class LightBusinessTable implements IBusinessTable{

	/**
	 * do nothing
	 */
	public void addFilter(String groupName, IFilter f) {}

	public IBusinessTable getChildNamed(String name, String groupName) {
		
		return null;
	}

	public List<IBusinessTable> getChilds(String groupName) {
		
		return null;
	}

	public IDataStreamElement getColumn(String groupName, String colName)throws GrantException {
		
		return null;
	}

	public Collection<IDataStreamElement> getColumns(String groupName) {
		
		return null;
	}

	public List<IDataStreamElement> getColumnsOrdered(String groupName) {
		
		return null;
	}

	public String getDescription() {
		
		return null;
	}

	public List<IFilter> getFilterFor(String groupName) {
		
		return null;
	}

	public List<IFilter> getFilters() {
		
		return null;
	}

	public BusinessModel getModel() {
		
		return null;
	}

	public String getName() {
		
		return null;
	}

	public IBusinessTable getParent() {
		
		return null;
	}

	public int getPositionX() {
		return 0;
	}

	public int getPositionY() {
		return 0;
	}
	/**
	 * @return null
	 */
	public String getXml() {
		return null;
	}

	public boolean isDrillable() {
		return false;
	}

	public boolean isGrantedFor(String s) {
		
		return false;
	}
	/**
	 * do nothing
	 */
	public void removeColumn(IDataStreamElement dataStreamElement) {}

	public void removeFilter(String groupName, IFilter f) {}

	public void removeFilter(IFilter f) {}

	public void setBusinessModel(BusinessModel model) {
		
		
	}

	public void setDescription(String text) {
		
		
	}

	public void setGranted(String groupName, boolean b) {
		
		
	}

	public void setIsDrillable(boolean isDrillable) {
		
		
	}

	public void setName(String name) {
		
		
	}

	public void setPositionX(int positionX) {
		
		
	}

	public void setPositionX(String positionX) {
		
		
	}

	public void setPositionY(int positionY) {
		
		
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public void setEditable(boolean editable) {
		
	}

	
}
