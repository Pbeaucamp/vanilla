package bpm.forms.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.forms.core.design.IFormUIProperty;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ui.VanillaFdProperties;


public class FormUIFd implements IFormUi{
	

	
	private String name;
	
	private long formDefinitionId;
	private long id;

	
	private List<IFormUIProperty> properties = new ArrayList<IFormUIProperty>();

	
//	/**
//	 * @return the vanillaRepositoryId
//	 */
//	public int getVanillaRepositoryId() {
//		try{
//			String s = getPropertyNamed(PROP_FD_REPOSITORY_ID).getPropertyValue();
//			return Integer.parseInt(s);
//		}catch(Exception ex){
//			return 0;
//		}
//
//	}
//	/**
//	 * @param vanillaRepositoryId the vanillaRepositoryId to set
//	 */
//	public void setVanillaRepositoryId(int vanillaRepositoryId) {
//		FormUIProperty p = (FormUIProperty)getPropertyNamed(PROP_FD_REPOSITORY_ID);
//		
//		if (p == null){
//			p = new FormUIProperty();
//			p.setPropertyName(PROP_FD_REPOSITORY_ID);
//			addProperty(p);
//		}
//		p.setPropertyValue(vanillaRepositoryId + "");
//		
//		if (p.getFormDefinitionId() <= 0){
//			p.setFormDefinitionId(getFormDefinitionId());
//		}
//	}
//	/**
//	 * @return the directoryItemId
//	 */
//	public int getDirectoryItemId() {
//		try{
//			String s = getPropertyNamed(PROP_FD_DIRECTORY_ITEM_ID).getPropertyValue();
//			return Integer.parseInt(s);
//		}catch(Exception ex){
//			return 0;
//		}
//	}
//	/**
//	 * @param directoryItemId the directoryItemId to set
//	 */
//	public void setDirectoryItemId(int directoryItemId) {
//		FormUIProperty p = (FormUIProperty)getPropertyNamed(PROP_FD_DIRECTORY_ITEM_ID);
//		
//		if (p == null){
//			p = new FormUIProperty();
//			p.setPropertyName(PROP_FD_DIRECTORY_ITEM_ID);
//			addProperty(p);
//		}
//		p.setPropertyValue(directoryItemId + "");
//		if (p.getFormDefinitionId() <= 0){
//			p.setFormDefinitionId(getFormDefinitionId());
//		}
//	}
	
	
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
	 * @return the formDefinitionId
	 */
	public long getFormDefinitionId() {
		return formDefinitionId;
	}
	/**
	 * @param formDefinitionId the formDefinitionId to set
	 */
	public void setFormDefinitionId(long formDefinitionId) {
		this.formDefinitionId = formDefinitionId;
		if (getFormDefinitionId() > 0){
			for(IFormUIProperty p : getProperties()){
				((FormUIProperty)p).setFormDefinitionId(getFormDefinitionId());
			}
		}
		
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	

	
	@Override
	public List<IFormUIProperty> getProperties() {
		return new ArrayList<IFormUIProperty>(properties);
	}
	public void addProperty(IFormUIProperty p) {
		if (getId() > 0){
			((FormUIProperty)p).setFormDefinitionId(getId());
		}
		
		properties.add(p);
		
	}
	

//	/**
//	 * @return the vanillaLogin
//	 */
//	public String getVanillaLogin() {
//		try{
//			String s = getPropertyNamed(PROP_VANILLA_LOGIN).getPropertyValue();
//			return s;
//		}catch(Exception ex){
//			return "";
//		}
//	}
//	/**
//	 * @param directoryItemId the vanillaLogin to set
//	 */
//	public void setVanillaLogin(String vanillaLogin) {
//		FormUIProperty p = (FormUIProperty)getPropertyNamed(PROP_VANILLA_LOGIN);
//		
//		if (p == null){
//			p = new FormUIProperty();
//			p.setPropertyName(PROP_VANILLA_LOGIN);
//			properties.add(p);
//		}
//		p.setPropertyValue(vanillaLogin );
//	}
//	
//	/**
//	 * @return the vanillaLogin
//	 */
//	public String getVanillaPassword() {
//		try{
//			String s = getPropertyNamed(PROP_VANILLA_PASSWORD).getPropertyValue();
//			return s;
//		}catch(Exception ex){
//			return "";
//		}
//	}
//	/**
//	 * @param vanillaPassword the vanillaPassword to set
//	 */
//	public void setVanillaPassword(String vanillaPassword) {
//		FormUIProperty p = (FormUIProperty)getPropertyNamed(PROP_VANILLA_PASSWORD);
//		
//		if (p == null){
//			p = new FormUIProperty();
//			p.setPropertyName(PROP_VANILLA_PASSWORD);
//			properties.add(p);
//		}
//		p.setPropertyValue(vanillaPassword );
//	}
	@Override
	public String getPropertyValue(String propertyName) {
		IFormUIProperty p = getProperty(propertyName);
		
		if (p!=null){
			return p.getPropertyValue();
		}
		
		return null;
		
	}
//	
//	/**
//	 * @param vanillaPassword the vanillaPassword to set
//	 */
//	public void setVanillaUrl(String vanillaUrl) {
//		FormUIProperty p = (FormUIProperty)getPropertyNamed(PROP_VANILLA_URL);
//		
//		if (p == null){
//			p = new FormUIProperty();
//			p.setPropertyName(PROP_VANILLA_URL);
//			properties.add(p);
//		}
//		p.setPropertyValue(vanillaUrl );
//	}
//	
//	/**
//	 * @return the vanillaUrl
//	 */
//	public String getVanillaUrl() {
//		try{
//			String s = getPropertyNamed(PROP_VANILLA_URL).getPropertyValue();
//			return s;
//		}catch(Exception ex){
//			return "";
//		}
//	}
	@Override
	public IFormUIProperty getProperty(String propertyName) {
		for(IFormUIProperty p : properties){
			if (p.getPropertyName().equals(propertyName)){
				return p;
			}
		}
		
		FormUIProperty p = new FormUIProperty();
		
		if (propertyName.equals(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID)){
			p.setPropertyName(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID);
			p.setFormDefinitionId(getFormDefinitionId());
			p.setFormUiName(getName());
			properties.add(p);
			return p;
		}
		else if (propertyName.equals(VanillaFdProperties.PROP_FD_REPOSITORY_ID)){
			
			p.setPropertyName(VanillaFdProperties.PROP_FD_REPOSITORY_ID);
			p.setFormDefinitionId(getFormDefinitionId());
			p.setFormUiName(getName());
			properties.add(p);
			return p;
		}
		
		return null;
	}
	@Override
	public void setProperty(String propertyName, String propertyValue) {
		
		IFormUIProperty p = getProperty(propertyName);
		
		if (p!=null){
			p.setPropertyValue( propertyValue);
		}
		
		
	}

}
