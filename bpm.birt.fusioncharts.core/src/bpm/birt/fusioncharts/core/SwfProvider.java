package bpm.birt.fusioncharts.core;

/*
 * This class determines witch way we can use to call the chart
 * 
 * The first way is to call directly in the resource of the plug-in
 * The other way is to call charts present on the vanilla server
 * 
 * We first have to determine if the vanilla server is up. If it is, we can call charts 
 */
public class SwfProvider {

	private static SwfProvider instance;
	
	private String vanillaChartUrl;
	
	private boolean isInit = false;
	
	public static SwfProvider getInstance() {
		if(instance == null) {
			instance = new SwfProvider();
		}
		return instance;
	}
	
	public SwfProvider(){
		init();
	}
	
	public void init(){
		String vanillaRuntimeUrl = Activator.getDefault().getVanillaRuntimeUrl();
		if(vanillaRuntimeUrl != null){
			vanillaChartUrl = vanillaRuntimeUrl + "/Charts/";
			isInit = true;
		}
	}
	
	public String getSwfUrl(String chartName){
		if(isInit){
			return vanillaChartUrl + chartName;
		}
		else{
			return null;
		}
	}
	
	public String getJQueryJS() {
		if(isInit){
			return vanillaChartUrl + "jquery.min.js";
		}
		else{
			return null;
		}
	}
	
	public String getFusionChartsHCJS() {
		if(isInit){
			return vanillaChartUrl + "FusionCharts.HC.js";
		}
		else{
			return null;
		}
	}
	
	public String getFusionChartsHCChartJS() {
		if(isInit){
			return vanillaChartUrl + "FusionCharts.HC.Charts.js";
		}
		else{
			return null;
		}
	}
	
	public String getFusionChartsJS(){
		if(isInit){
			return vanillaChartUrl + "FusionCharts.js";
		}
		else{
			return null;
		}
	}
	
}
