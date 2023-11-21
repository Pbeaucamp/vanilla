package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class MapInfo implements IMapInfo {
	private String vanillaRuntimeUrl;
	private Long mapObjectId;
	private int height = 300;
	private int width = 400;
	private String swfFileName;
	private String mapType;
	private int mapId;

	private String fmUser;
	private String fmPassword;
	private boolean isFusionMap = true;
	
	public MapInfo() {
		
	}

	/**
	 * @return the vanillaRuntimeUrl
	 */
	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @param swfFileName
	 *            the swfFileName to set
	 */
	public void setSwfFileName(String swfFileName) {
		this.swfFileName = swfFileName;
	}

	/**
	 * @param vanillaRuntimeUrl
	 *            the vanillaRuntimeUrl to set
	 */
	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl) {
		this.vanillaRuntimeUrl = vanillaRuntimeUrl;
	}

	/**
	 * @return the fusionMapObjectId
	 */
	public Long getMapObjectId() {
		return mapObjectId;
	}

	/**
	 * @param fusionMapObjectId
	 *            the fusionMapObjectId to set
	 */
	public void setMapObjectId(long fusionMapObjectId) {
		this.mapObjectId = fusionMapObjectId;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("mapInfo");
		if(getVanillaRuntimeUrl() != null) {
			e.addAttribute("vanillaRuntimeUrl", getVanillaRuntimeUrl());
		}
		if(getMapObjectId() != null) {
			e.addAttribute("fusionMapId", getMapObjectId() + "");
		}
		if(getSwfFileName() != null) {
			e.addAttribute("swfFileName", getSwfFileName());
		}

		if(getFmPassword() != null) {
			e.addAttribute("fmPassword", getFmPassword());
		}
		if(getFmUser() != null) {
			e.addAttribute("fmUser", getFmUser());
		}
		if(getMapType() != null) {
			e.addAttribute("mapType", getMapType());
		}
		e.addAttribute("isFusionMap", isFusionMap+"");
		e.addAttribute("mapId", getMapId() + "");
		e.addAttribute("width", getWidth() + "");
		e.addAttribute("height", getHeight() + "");

		return e;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public String getSwfFileName() {
		return swfFileName;
	}

	public void updateValues(IMapInfo info) {

		if(info instanceof MapInfo) {
			MapInfo inf = (MapInfo) info;
			setMapObjectId(inf.getMapObjectId());
			setVanillaRuntimeUrl(inf.getVanillaRuntimeUrl());
			setSwfFileName(inf.getSwfFileName());

			if(inf.getFmUser() != null) {
				setFmPassword(inf.getFmPassword());
				setFmUser(inf.getFmUser());
			}
			setMapId(inf.getMapId());
			setMapType(inf.getMapType());
			setFusionMap(((MapInfo) info).isFusionMap());
		}

	}

	public void setMapType(String mapType) {
		this.mapType = mapType;
	}

	public String getMapType() {
		return mapType;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getMapId() {
		return mapId;
	}

	public String getFmUser() {
		return fmUser;
	}

	public void setFmUser(String fmUser) {
		this.fmUser = fmUser;
	}

	public String getFmPassword() {
		return fmPassword;
	}

	public void setFmPassword(String fmPassword) {
		this.fmPassword = fmPassword;
	}

	public void setFusionMap(boolean isFusionMap) {
		this.isFusionMap = isFusionMap;
	}

	public boolean isFusionMap() {
		return isFusionMap;
	}

}
