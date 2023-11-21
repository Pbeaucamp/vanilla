package bpm.vanilla.map.core.design;

import java.util.List;

public interface IBuildingFloor {
	
	/**
	 * 
	 * @return the ICell Id
	 */
	public Integer getId();
	public String getLabel();
	public Integer getBuildingId();
	public Integer getLevel();
	public Integer getHeight();
	public IImage getImage();
	public Integer getImageId();
	public List<ICell> getCells();
	
	public void setId(Integer id);
	public void setLabel(String string);
	public void setBuildingId(Integer id);
	public void setLevel(Integer level);
	public void setHeight(Integer height);
	public void setCells(List<ICell> cells);
	public void addCell(ICell cell);
	public void removeCell(ICell cell);
	public void setImage(IImage image);
	public void setImageId(Integer imageId);
}
