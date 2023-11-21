package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;

public class GoogleMapDatas implements IMapDatas{

	private Integer valueFieldIndex;
	private Integer latitudeFieldIndex;
	private Integer longitudeFieldIndex;
	private Integer labelFieldIndex;
	
	/**
	 * @param valueFieldIndex the valueFieldIndex to set
	 */
	public void setValueFieldIndex(Integer valueFieldIndex) {
		this.valueFieldIndex = valueFieldIndex;
	}

	

	/**
	 * @return the latitudeFieldIndex
	 */
	public Integer getLatitudeFieldIndex() {
		return latitudeFieldIndex;
	}



	/**
	 * @param latitudeFieldIndex the latitudeFieldIndex to set
	 */
	public void setLatitudeFieldIndex(Integer latitudeFieldIndex) {
		this.latitudeFieldIndex = latitudeFieldIndex;
	}



	/**
	 * @return the longitudeFieldIndex
	 */
	public Integer getLongitudeFieldIndex() {
		return longitudeFieldIndex;
	}



	/**
	 * @param longitudeFieldIndex the longitudeFieldIndex to set
	 */
	public void setLongitudeFieldIndex(Integer longitudeFieldIndex) {
		this.longitudeFieldIndex = longitudeFieldIndex;
	}



	/**
	 * @return the labelFieldIndex
	 */
	public Integer getLabelFieldIndex() {
		return labelFieldIndex;
	}



	/**
	 * @param labelFieldIndex the labelFieldIndex to set
	 */
	public void setLabelFieldIndex(Integer labelFieldIndex) {
		this.labelFieldIndex = labelFieldIndex;
	}



	/**
	 * @param dataSet the dataSet to set
	 */
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	private DataSet dataSet;
	
	public Integer getValueFieldIndex() {
		return valueFieldIndex;
	}

	

	public IComponentDatas getAdapter(Object o) {
		try{
			if (o == MapRenderer.getRenderer(MapRenderer.VANILLA_GOOGLE_MAP)){
				return this;
			}
			
			else if (o == MapRenderer.getRenderer(MapRenderer.VANILLA_FUSION_MAP)){
				MapDatas datas = new MapDatas();
				datas.setDataSet(getDataSet());
				datas.setValueFieldIndex(getValueFieldIndex());
				datas.setZoneIdFieldIndex(getLabelFieldIndex());
				return datas;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return this;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("mapData");
		e.addAttribute("type", GoogleMapDatas.class.getName());
		if (getValueFieldIndex() != null){
			e.addAttribute("valueFieldIndex", "" + getValueFieldIndex());
		}
		
		if (getLabelFieldIndex() != null){
			e.addAttribute("labelFieldIndex", "" + getLabelFieldIndex());
		}
		if (getLatitudeFieldIndex() != null){
			e.addAttribute("latitudeFieldIndex", "" + getLatitudeFieldIndex());
		}
		if (getLongitudeFieldIndex() != null){
			e.addAttribute("longitudeFieldIndex", "" + getLongitudeFieldIndex());
		}
		
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		
		return e;
	}

	public boolean isFullyDefined() {
		return dataSet != null && valueFieldIndex != null && longitudeFieldIndex != null&& latitudeFieldIndex != null && labelFieldIndex != null;
	}



	@Override
	public IComponentDatas copy() {
		GoogleMapDatas copy = new GoogleMapDatas();
		
		copy.setDataSet(dataSet);
		copy.setLabelFieldIndex(labelFieldIndex);
		copy.setLatitudeFieldIndex(latitudeFieldIndex);
		copy.setLongitudeFieldIndex(longitudeFieldIndex);
		copy.setValueFieldIndex(valueFieldIndex);
		
		return copy;
	}
	
}
