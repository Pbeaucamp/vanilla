package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.FdModel;


public class MapDrillInfo {
	public enum TargetType{
		PopupPage, Parameter, FolderPage;
	}
	private boolean isDrillable = false;
	private boolean sendCategory = false;
	private TargetType target = TargetType.PopupPage;
	
	private ComponentMap componentMap;
	private String modelPageName;
	private FdModel modelPage;
	
	private String folderPageName;
	
	private int width = 400;
	private int height = 300;
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the modelPageName
	 */
	public String getModelPageName() {
		return modelPageName;
	}
	/**
	 * @param modelPageName the modelPageName to set
	 */
	public void setModelPageName(String modelPageName) {
		this.modelPageName = modelPageName;
	}
	/**
	 * @return the modelPage
	 */
	public FdModel getModelPage() {
		return modelPage;
	}
	/**
	 * @param modelPage the modelPage to set
	 */
	public void setModelPage(FdModel modelPage) {
		if (modelPage != null){
			this.modelPageName = modelPage.getName();
			
		}
		else{
			this.modelPageName = null;
		}
		this.modelPage = modelPage;
	}
	public MapDrillInfo(ComponentMap map){
		this.componentMap = map;
	}
	/**
	 * @return the isDrillable
	 */
	public boolean isDrillable() {
		return isDrillable;
	}

	/**
	 * @param isDrillable the isDrillable to set
	 */
	public void setDrillable(boolean isDrillable) {
		this.isDrillable = isDrillable;
	}

	/**
	 * @return the sendCategory
	 */
	public boolean isSendCategory() {
		return sendCategory;
	}

	/**
	 * @param sendCategory the sendCategory to set
	 */
	public void setSendCategory(boolean sendCategory) {
		this.sendCategory = sendCategory;
	}

	/**
	 * @return the target
	 */
	public TargetType getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(TargetType target) {
		this.target = target;
	}

	/**
	 * @return the componentMap
	 */
	public ComponentMap getComponentMap() {
		return componentMap;
	}

	/**
	 * @param componentMap the componentMap to set
	 */
	public void setComponentMap(ComponentMap componentMap) {
		this.componentMap = componentMap;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("drillInfo");
		e.addAttribute("active", isDrillable() + "");
		e.addAttribute("categoryAsValue", isSendCategory() + "");
		e.addAttribute("width", getWidth() + "");
		e.addAttribute("height", getHeight() + "");
		if (getFolderPageName() != null){
			e.addAttribute("folderPageName", getFolderPageName());
		}
//		if (getUrl() != null){
//			e.addElement("url").setText(getUrl());
//		}
		
		if (modelPageName != null){
			e.addElement("targetPage").setText(modelPageName);
		}
		
		return e;
	}
	public String getFolderPageName() {
		return folderPageName;
	}
	public void setFolderPageName(String folderPageName) {
		this.folderPageName = folderPageName;
	}
}
