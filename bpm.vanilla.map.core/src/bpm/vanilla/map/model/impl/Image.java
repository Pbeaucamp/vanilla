package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.IImage;

public class Image implements IImage{
	
	private Integer id;
	private Integer imageItemId;
	private Integer imageRepositoryId;
	
	public Image(){
		
	}

	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public Integer getImageItemId() {
		return imageItemId;
	}
	
	public void setImageItemId(Integer imageItemId) {
		this.imageItemId = imageItemId;
	}
	
	@Override
	public Integer getImageRepositoryId() {
		return imageRepositoryId;
	}
	
	public void setImageRepositoryId(Integer imageRepositoryId) {
		this.imageRepositoryId = imageRepositoryId;
	}
}
