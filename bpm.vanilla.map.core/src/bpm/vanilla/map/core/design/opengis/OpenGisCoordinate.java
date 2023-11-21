package bpm.vanilla.map.core.design.opengis;


public class OpenGisCoordinate implements IOpenGisCoordinate {

	private int id;
	private int entityId;
	private Double locX, locY, locZ;
	
	public OpenGisCoordinate(){ }
	
	public OpenGisCoordinate(Double locX, Double locY, Double locZ) {
		setLocX(locX);
		setLocY(locY);
		setLocZ(locZ);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	
	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public Double getLocX() {
		return locX;
	}

	@Override
	public Double getLocY() {
		return locY;
	}

	@Override
	public Double getLocZ() {
		return locZ;
	}

	@Override
	public void setLocX(Double locX) {
		if(locX != null && Double.isNaN(locX)){
			this.locX = null;
		}
		else {
			this.locX = locX;
		}
	}

	@Override
	public void setLocY(Double locY) {
		if(locY != null && Double.isNaN(locY)){
			this.locY = null;
		}
		else {
			this.locY = locY;
		}
	}

	@Override
	public void setLocZ(Double locZ) {
		if(locZ != null && Double.isNaN(locZ)){
			this.locZ = null;
		}
		else {
			this.locZ = locZ;
		}
	}
	
	@Override
	public String toString() {
		return "(" + locX + "," + locY + "," + locZ + ")";
	}
}
