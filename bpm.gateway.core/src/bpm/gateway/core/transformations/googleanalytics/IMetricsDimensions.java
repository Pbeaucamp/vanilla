package bpm.gateway.core.transformations.googleanalytics;

import java.util.List;

import bpm.gateway.core.Transformation;

public interface IMetricsDimensions extends Transformation {
	
	public static final String[] DIMENSIONS = {
		"ga:visitorType",
		"ga:visitCount",
		"ga:daysSinceLastVisit",
		"ga:visitLength",
		"ga:source",
		"ga:medium",
		"ga:adGroup",
		"ga:adSlotPosition",
		"ga:adDistributionNetwork",
		"ga:adMatchType",
		"ga:adMatchedQuery",
		"ga:adPlacementDomain",
		"ga:adPlacementUrl",
		"ga:adFormat",
		"ga:browser",
		"ga:operatingSystem",
		"ga:isMobile",
		"ga:screenResolution",
		"ga:country",
		"ga:city",
		"ga:networkDomain",
		"ga:networkLocation",
		"ga:keyword",
	};
	
	public static final String[] METRICS = {
		"ga:visitors",
		"ga:newVisits",
		"ga:percentNewVisits",
		"ga:visits",
		"ga:timeOnSite",
		"ga:avgTimeOnSite",
		"ga:impressions",
		"ga:adClicks",
		"ga:adCost",
		"ga:pageviews",
		"ga:timeOnPage"
	};
	
	public List<String> getMetrics();
	public void setMetrics(List<String> metrics);
	public void addMetric(String metric);
	public void removeMetric(String metric);
	
	public List<String> getDimensions();
	public void setDimensions(List<String> dimensions);
	public void addDimension(String dimension);
	public void removeDimension(String dimension);
}
