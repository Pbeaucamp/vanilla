package bpm.fa.api.olap.unitedolap;


public class UnitedOlapLoaderFactory {

	private static UnitedOlapLoader loader;
	
	public static UnitedOlapLoader createLoader() {
		return new UnitedOlapLoader();
	}
	
	public static UnitedOlapLoader getLoader() {
		if(loader == null) {
			loader = new UnitedOlapLoader();
		}
		return loader;
	}
	
}
