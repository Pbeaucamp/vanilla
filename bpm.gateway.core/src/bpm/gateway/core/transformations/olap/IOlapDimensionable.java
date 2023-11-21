package bpm.gateway.core.transformations.olap;

public interface IOlapDimensionable extends IOlap{
	public String getDimensionName();
	public void setDimensionName(String dimensionName);
	public void setHierarchieName(String hierarchieName);
	public String getHierarchieName() ;
}
