package bpm.fa.api.olap.query;

public class MissingLastTimeDimensionException extends Exception{

	private String measureUname;
	private String dimensionUname;
	
	public MissingLastTimeDimensionException(String message, String measure, String dimension) {
		super(message);
		this.measureUname = measure;
		this.dimensionUname = dimension;
	}
	
	public String getMeasure() {
		return measureUname;
	}
	
	public String getDimension() {
		return dimensionUname;
	}

}
