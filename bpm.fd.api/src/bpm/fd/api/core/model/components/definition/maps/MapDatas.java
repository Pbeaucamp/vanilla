package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.vanilla.map.core.design.MapVanilla;

public class MapDatas implements IMapDatas{
	private Integer valueFieldIndex;
	private Integer zoneIdFieldIndex;
	
	private Integer latitudeIndex;
	private Integer longitudeIndex;
	private Integer imgPathIndex;
	private Integer imgSizeIndex;
	private Integer commentIndex;
	
	private MapVanilla mapVanilla;
	
	/**
	 * @param valueFieldIndex the valueFieldIndex to set
	 */
	public void setValueFieldIndex(Integer valueFieldIndex) {
		this.valueFieldIndex = valueFieldIndex;
	}

	/**
	 * @param zoneIdFieldIndex the zoneIdFieldIndex to set
	 */
	public void setZoneIdFieldIndex(Integer zoneIdFieldIndex) {
		this.zoneIdFieldIndex = zoneIdFieldIndex;
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

	public Integer getZoneIdFieldIndex() {
		return zoneIdFieldIndex;
	}

	public IComponentDatas getAdapter(Object o) {
		
		try{
			if (o == MapRenderer.getRenderer(MapRenderer.VANILLA_FUSION_MAP) || o == MapRenderer.getRenderer(MapRenderer.VANILLA_FLASH_MAP) ){
				return this;
			}
			
			else if (o == MapRenderer.getRenderer(MapRenderer.VANILLA_GOOGLE_MAP)){
				GoogleMapDatas datas = new GoogleMapDatas();
				datas.setDataSet(getDataSet());
				datas.setValueFieldIndex(getValueFieldIndex());
				datas.setLabelFieldIndex(getZoneIdFieldIndex());
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
		e.addAttribute("type", MapDatas.class.getName());
		if (getValueFieldIndex() != null){
			e.addAttribute("valueFieldIndex", "" + getValueFieldIndex());
		}
		
		if (getZoneIdFieldIndex() != null){
			e.addAttribute("zoneIdFieldIndex", "" + getZoneIdFieldIndex());
		}
		
		if (getImgPathIndex() != null){
			e.addAttribute("imgPathIndex", "" + getImgPathIndex());
		}
		
		if (getImgSizeIndex() != null){
			e.addAttribute("imgSizeIndex", "" + getImgSizeIndex());
		}
		
		if (getLatitudeIndex() != null){
			e.addAttribute("latitudeIndex", "" + getLatitudeIndex());
		}
		
		if (getLongitudeIndex() != null){
			e.addAttribute("longitudeIndex", "" + getLongitudeIndex());
		}
		
		if (getCommentIndex() != null){
			e.addAttribute("commentIndex", "" + getCommentIndex());
		}
		
		if (dataSet != null){
			e.addElement("dataSet-ref").setText(dataSet.getName());
		}
		
		return e;
	}

	public boolean isFullyDefined() {
		return dataSet != null && valueFieldIndex != null && zoneIdFieldIndex != null;
	}

	public Integer getLatitudeIndex() {
		return latitudeIndex;
	}

	public void setLatitudeIndex(Integer latitudeIndex) {
		this.latitudeIndex = latitudeIndex;
	}

	public Integer getLongitudeIndex() {
		return longitudeIndex;
	}

	public void setLongitudeIndex(Integer longitudeIndex) {
		this.longitudeIndex = longitudeIndex;
	}

	public Integer getImgPathIndex() {
		return imgPathIndex;
	}

	public void setImgPathIndex(Integer imgPathIndex) {
		this.imgPathIndex = imgPathIndex;
	}

	public Integer getImgSizeIndex() {
		return imgSizeIndex;
	}

	public void setImgSizeIndex(Integer imgSizeIndex) {
		this.imgSizeIndex = imgSizeIndex;
	}

	public Integer getCommentIndex() {
		return commentIndex;
	}

	public void setCommentIndex(Integer commentIndex) {
		this.commentIndex = commentIndex;
	}

	@Override
	public IComponentDatas copy() {
		MapDatas copy = new MapDatas();
		
		copy.setCommentIndex(commentIndex);
		copy.setDataSet(dataSet);
		copy.setImgPathIndex(imgPathIndex);
		copy.setImgSizeIndex(imgSizeIndex);
		copy.setLatitudeIndex(latitudeIndex);
		copy.setLongitudeIndex(longitudeIndex);
		copy.setValueFieldIndex(valueFieldIndex);
		copy.setZoneIdFieldIndex(zoneIdFieldIndex);
		
		return copy;
	}

	public MapVanilla getMapVanilla() {
		return mapVanilla;
	}

	public void setMapVanilla(MapVanilla mapVanilla) {
		this.mapVanilla = mapVanilla;
	}

	
	
}
