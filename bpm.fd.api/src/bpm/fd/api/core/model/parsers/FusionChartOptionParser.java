package bpm.fd.api.core.model.parsers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillReportOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.LineCombinationOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.api.core.model.components.definition.gauge.GaugeOptions;

public class FusionChartOptionParser {

	private static List<Exception> exceptions = new ArrayList<Exception>();
	
	
	public static List<Exception> getExceptions(){
		return exceptions;
	}
	
	public static IComponentOptions parseOption(Element root){
		exceptions.clear();
		if (root.getName().equals("genericOptions")){
			
			return parseGenericOptions(root);
		}
		else if (root.getName().equals("genericNonPieOptions")){
			
			return parseGenericNonPieOptions(root);
		}
		else if (root.getName().equals("numberFormatOptions")){
			
			return parseNumberFormatOptions(root);
		}
		else if (root.getName().equals("lineCombinationOptions")){
			
			return parseLineCombinationOptions(root);
		}
		else if (root.getName().equals("drillReportOptions")){
			
			return parseDrillReportOptions(root);
		}
		else if (root.getName().equals("gaugeOptions")){
			
			try {
				return parseGaugeOptions(root);
			} catch (Exception e) {
				return new GaugeOptions();
			}
		}
		else if (root.getName().equals("pieGenericOptions")){
			try{
				return parsePieGenericOptions(root);
			}catch(Exception e){
				exceptions.add(new Exception("cannot restore FusionChart PieGenericOptions, replaced by GenericOptions", e));
				return parseGenericOptions(root);
			}
			
		}
		return null;
	}
	
	
	private static GenericOptions parseGenericOptions(Element root){
		GenericOptions opt = new GenericOptions();
		opt.setCaption(root.attributeValue("caption"));
		opt.setSubCaption(root.attributeValue("subCaption"));
		opt.setShowValues(Boolean.parseBoolean(root.attributeValue("showValues")));
		opt.setShowLabel(Boolean.parseBoolean(root.attributeValue("showLabels")));
		if(root.attributeValue("multiLineLabels") != null) {
			opt.setMultiLineLabels(Boolean.parseBoolean(root.attributeValue("multiLineLabels")));
		}
		if(root.element("baseFontColor") != null) {
			int r = Integer.parseInt(root.element("baseFontColor").attributeValue("red"));
			int g = Integer.parseInt(root.element("baseFontColor").attributeValue("green"));
			int b = Integer.parseInt(root.element("baseFontColor").attributeValue("blue"));
			Color color = new Color(r, g, b);
			opt.setBaseFontColor(color);
		}
		if(root.attributeValue("baseFontSize") != null) {
			opt.setBaseFontSize(Integer.parseInt(root.attributeValue("baseFontSize")));
		}
		
		if(root.attributeValue("DynamicLegend") != null) {
			opt.setDynamicLegend(Boolean.parseBoolean(root.attributeValue("DynamicLegend")));
		}
		if(root.attributeValue("exportEnable") != null) {
			opt.setExportEnable(Boolean.parseBoolean(root.attributeValue("exportEnable")));
		}
		if(root.attributeValue("LabelSize") != null) {
			opt.setLabelSize(Integer.parseInt(root.attributeValue("LabelSize")));
		}
		
		return opt;
	}
	
	private static ChartDrillReportOption parseDrillReportOptions(Element root){
		ChartDrillReportOption opt = new ChartDrillReportOption();
		opt.setChartDataDetails(root.attributeValue("chartDataDetails"));
		return opt;
	}
	
	private static LineCombinationOption parseLineCombinationOptions(Element root){
		LineCombinationOption opt = new LineCombinationOption();
		opt.setLineSerieName(root.attributeValue("lineSerieName"));
		
		return opt;
	}
	
	private static NumberFormatOptions parseNumberFormatOptions(Element root){
		NumberFormatOptions opt = new NumberFormatOptions();
		opt.setFormatNumber(Integer.parseInt(root.attributeValue("formatNumber")) != 0);
		opt.setFormatNumberScale(Integer.parseInt(root.attributeValue("formatNumberScale")) != 0);
		opt.setNumberPrefix(root.attributeValue("numberPrefix"));
		opt.setNumberSuffix(root.attributeValue("numberSuffix"));
		opt.setDecimalSeparator(root.attributeValue("decimalSeparator"));
		opt.setThousandSeparator(root.attributeValue("thousandSeparator"));
		opt.setDecimals(Integer.parseInt(root.attributeValue("decimals")));
		opt.setForceDecimal(Integer.parseInt(root.attributeValue("forceDecimal")) != 0);
		return opt;
	}
	
	private static GenericNonPieOptions parseGenericNonPieOptions(Element root){
		GenericNonPieOptions opt = new GenericNonPieOptions();
		opt.setRotateLabels(Integer.parseInt(root.attributeValue("rotateLabels")) != 0);
		opt.setSlantLabels(Integer.parseInt(root.attributeValue("slantLabels")) != 0);
		opt.setRotateValues(Integer.parseInt(root.attributeValue("rotateValues")) != 0);
		opt.setPlaceValuesInside(Integer.parseInt(root.attributeValue("placeValuesInside")) != 0);
		opt.setRotateYAxisName(Integer.parseInt(root.attributeValue("rotateYAxisName")) != 0);
		
		try{
			opt.setPYAxisName(root.attributeValue("PYAxisName"));
		}catch(Exception ex){
			
		}
		try{
			opt.setSYAxisName(root.attributeValue("SYAxisName"));
		}catch(Exception ex){
			
		}
		try{
			opt.setxAxisName(root.attributeValue("xAxisName"));
		}catch(Exception ex){
			
		}
		
		return opt;
	}
	
	private static PieGenericOptions parsePieGenericOptions(Element root) throws Exception{
		GenericOptions _opt = parseGenericOptions(root);
		PieGenericOptions opt = (PieGenericOptions)_opt.getAdapter(ChartNature.getNature(ChartNature.PIE));
		try{
			opt.setSlicingDistance(Integer.parseInt(root.attributeValue("slicingDistance")));
		}catch(NumberFormatException ex){
			exceptions.add(new Exception("bad attribute value for slicingDistance" , ex));
		}
		
		try{
			opt.setPieSliceDepth(Integer.parseInt(root.attributeValue("pieSliceDepth")));
		}catch(NumberFormatException ex){
			exceptions.add(new Exception("bad attribute value for pieSliceDepth" , ex));
		}
		
		try{
			opt.setPieRadius(Integer.parseInt(root.attributeValue("pieRadius")));
		}catch(NumberFormatException ex){
			exceptions.add(new Exception("bad attribute value for pieRadius" , ex));
		}
		
		return opt;
	}
	
	private static GaugeOptions parseGaugeOptions(Element root) throws Exception{
		GaugeOptions opt = new GaugeOptions();
		try{
			Element e = root.element("backgroundColor");
			opt.setBgColor(new Color(Integer.parseInt(e.attributeValue("red")), Integer.parseInt(e.attributeValue("green")), Integer.parseInt(e.attributeValue("blue"))));
		}catch(Exception ex){
			
		}
		
		try{
			opt.setBgAlpha(Integer.parseInt(root.element("backgroundAlpha").getStringValue()));
		}catch(Exception ex){
			
		}
		try{
			opt.setBulb(Boolean.parseBoolean(root.element("bulb").getStringValue()));
		}catch(Exception ex){
			
		}
		
		try{
			Element e = root.element("colorBadValue");
			opt.setColorBadValue(new Color(Integer.parseInt(e.attributeValue("red")), Integer.parseInt(e.attributeValue("green")), Integer.parseInt(e.attributeValue("blue"))));
		}catch(Exception ex){
			
		}
		try{
			Element e = root.element("colorMediumValue");
			opt.setColorMediumValue(new Color(Integer.parseInt(e.attributeValue("red")), Integer.parseInt(e.attributeValue("green")), Integer.parseInt(e.attributeValue("blue"))));
		}catch(Exception ex){
			
		}
		try{
			Element e = root.element("colorGoodValue");
			opt.setColorGoodValue(new Color(Integer.parseInt(e.attributeValue("red")), Integer.parseInt(e.attributeValue("green")), Integer.parseInt(e.attributeValue("blue"))));
		}catch(Exception ex){
			
		}
		try{
			opt.setInnerRadius(Integer.parseInt(root.element("innerRadius").getStringValue()));
		}catch(Exception ex){
			
		}
		try{
			opt.setOuterRadius(Integer.parseInt(root.element("outerRadius").getStringValue()));
		}catch(Exception ex){
			
		}
		try{
			opt.setShowValues(Boolean.parseBoolean(root.element("showValues").getStringValue()));
		}catch(Exception ex){
			
		}
		try{
			opt.setStartAngle(Integer.parseInt(root.element("startAngle").getStringValue()));
		}catch(Exception ex){
			
		}
		try{
			opt.setStopAngle(Integer.parseInt(root.element("stopAngle").getStringValue()));
		}catch(Exception ex){
			
		}

		return opt;
	}
}

