package bpm.vanilla.map.core.design.opengis;

public interface IOpenGisCoordinate {

	public int getId();
	public void setId(int id);
	
	public int getEntityId();
	public void setEntityId(int entityId);

	public Double getLocX();
	public void setLocX(Double locX);
	
	public Double getLocY();
	public void setLocY(Double locY);
	
	public Double getLocZ();
	public void setLocZ(Double locZ);
}
