package bpm.faweb.shared;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OpenLayer implements IsSerializable{
	
	private String baseLayerUrl;
	private String baseLayerName;
	private String projection;
	private String bounds;
	private String vectorLayerUrl;
	private String featureType;
	private String layers;
	private String type;
	private HashMap<Integer, String> addressChild = new HashMap<Integer, String>();
	private int width;
	private int height;
	
	public String getBaseLayerUrl() {
		return baseLayerUrl;
	}
	public void setBaseLayerUrl(String baseLayerUrl) {
		this.baseLayerUrl = baseLayerUrl;
	}
	public String getBaseLayerName() {
		return baseLayerName;
	}
	public void setBaseLayerName(String baseLayerName) {
		this.baseLayerName = baseLayerName;
	}
	public String getProjection() {
		return projection;
	}
	public void setProjection(String projection) {
		this.projection = projection;
	}
	public String getBounds() {
		return bounds;
	}
	public void setBounds(String bounds) {
		this.bounds = bounds;
	}
	
	public String getVectorLayerUrl() {
		return vectorLayerUrl;
	}
	public void setVectorLayerUrl(String vectorLayerUrl) {
		this.vectorLayerUrl = vectorLayerUrl;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
	public String getLayers() {
		return layers;
	}
	public void setLayers(String layers) {
		this.layers = layers;
	}
	public HashMap<Integer, String> getAddressChild() {
		return addressChild;
	}
	public void setAddressChild(HashMap<Integer, String> addressChild) {
		this.addressChild = addressChild;
	}
	
	public void addAddressChild(Integer id, String name){
		this.addressChild.put(id, name);
	}
	
}
