package bpm.fd.runtime.engine.chart.fusion.generator;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.runtime.engine.chart.jfree.generator.chart.MultiSeriesHelper;
import bpm.fd.runtime.engine.chart.jfree.generator.chart.MultiSeriesHelper.Measure;



public abstract class AsbtractFusionMSXmlGenerator {

	protected String ROOT = "chart";
	protected String CATEGORIES = "categories";
	protected String CATEGORIE = "category";
	protected String CATEGORIE_LABEL = "label";
	protected String DATA_SET = "dataset";
	protected String DATA_SET_SERIESNAME = "seriesName";
	protected String DATA_SET_RENDER_AS = "renderAs";
	protected String DATA_SET_PARENTYAXIS = "parentYAxis";
	protected String DATA_SET_COLOR = "color";
	
	protected String DATA_SET_SET = "set";
	protected String DATA_SET_SET_VALUE = "value";
	
	protected String DRILL = "link";
	protected boolean oldWay;
	public AsbtractFusionMSXmlGenerator(boolean oldWay){
		this.oldWay = oldWay;
	}

	protected List<String> parseCategories(Document doc){
		List<String> categories = new ArrayList<String>();
		
		
		for(Element e : (List<Element>) doc.getRootElement().element(CATEGORIES).elements(CATEGORIE)){
			categories.add(e.attributeValue(CATEGORIE_LABEL));
		}
		return categories;
	}
	
	public String appendSplitedSeriesXml(
			Properties chartProperties, 
			List<List<Object>> values, 
			int measureNumber, 
			String[] measureNames, 
			String[][] colorSeries, 
			String lineSerieName, Properties drillProperties, String xml, int measureNameOffset, Palette palette) throws Exception{
		Document doc = DocumentHelper.parseText(xml);
		List<String> categories = parseCategories(doc);
		
		/*
		 * we check if there are some others categories
		 */
		List<String[]> categorieNames = MultiSeriesHelper.getCategories(values, measureNumber);
		Collections.sort(categorieNames, new Comparator<String[]>() {

			public int compare(String[] l1, String[] l2) {
				if (l1.length == 0 || l2.length == 0){
					return -1;
				}
				else if (l1[0] == null){
					return 1;
				}
				else if (l2[0] == null){
					return -1;
				}
				return l1[0].compareTo(l2[0]);
				
			}
		});
		Element categorie = doc.getRootElement().element(CATEGORIES);
		for(String[] s : categorieNames){
			boolean found = false;
			for(Element e : (List<Element>)categorie.elements(CATEGORIE)){
				if (e.attributeValue(CATEGORIE_LABEL).equals(s[1])){
					found = true;
					break;
				}
			}
			if (!found){
				categorie.addElement(CATEGORIE).addAttribute(CATEGORIE_LABEL, s[1]);
				categories.add(s[1]);
			}
			
		}
		List<Measure> measures = MultiSeriesHelper.getMeasures(values, measureNumber, measureNames, colorSeries, true, lineSerieName, measureNameOffset);
		HashMap<Measure, String[]> dataSets = new HashMap<Measure, String[]>();
		for(List<Object> row : values){
			String currentCategorie = row.get(measureNumber).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
			int catPos = -1;
			for(int i = 0; i < categories.size(); i++){
				if (categories.get(i).equals(currentCategorie)){
					catPos = i;
					break;
				}
			}
			
			if (catPos == -1){
				for(int i = 0; i < categorieNames.size(); i++){
					if (categorieNames.get(i)[0].equals(currentCategorie)){
						catPos = i;
						break;
					}
				}
			}
						
			Measure m = null;
			for(Measure _m : measures){
				if (_m.label.equals(row.get(measureNumber + 1))){
					m = _m;
					break;
				}
			}
			if (dataSets.get(m) == null){
				dataSets.put(m, new String[categories.size()]);
			}
			dataSets.get(m)[catPos] = row.get(m.index).toString();
		}
		//extract dual and combination opts
		String[] dualAxis = chartProperties.getProperty("_dualYPrimaries").split(";");		
		String[] rendering = chartProperties.getProperty("_combinationRenderings").split(";");

		for(Measure m : measures){
			
			Element datasetNode = doc.getRootElement().addElement(DATA_SET).addAttribute(DATA_SET_SERIESNAME, m.getName());
			
			//lineSerie
			if (m.isLineSerie){
				datasetNode.addAttribute(DATA_SET_RENDER_AS, "Line");
				datasetNode.addAttribute(DATA_SET_PARENTYAXIS, "S");
			}
			else{
				for(String r : rendering){
					if (r.startsWith(m.name+ ":")){
						datasetNode.addAttribute(DATA_SET_RENDER_AS, r.split(":")[1]);
					}
				}
				for(String r : dualAxis){
					if (r.startsWith(m.name + ":")){
						datasetNode.addAttribute(DATA_SET_PARENTYAXIS, r.split(":")[1]);
					}
				}
			}
			
			
			//color
			boolean colorApplyed = false;
			
			if (palette != null){
				for(String s : palette.getKeys()){
					if (palette.getColor(s) != null && m.label.toLowerCase().contains(s.toLowerCase())){
						datasetNode.addAttribute(DATA_SET_COLOR, "#" + palette.getColor(s));
						colorApplyed = true;
						break;
					}
				}
			}
			
			if (!colorApplyed && m.color != null){
				datasetNode.addAttribute(DATA_SET_COLOR, m.color);
			}
			
			
			//values
			if (dataSets.get(m) != null){
				for(String s : dataSets.get(m)){
					Element e = datasetNode.addElement(DATA_SET_SET);
					int categrieIndex = datasetNode.indexOf(e);
					if (s != null){
						e.addAttribute(DATA_SET_SET_VALUE, s);
						boolean added = false;
						for(String[] l : categorieNames){
							if (categories.get(categrieIndex).equals(l[1])){
								addDrillDatas(e, drillProperties, s,l[2]);
								added = true;
								break;
							}
						}
						if (! added){
							System.err.println("no drill added? wtf...");
						}
						
						
//						addDrillDatas(e, drillProperties, s, categories.get(categrieIndex));
					}
//					else{
//						addDrillDatas(e, drillProperties, "", categories.get(categrieIndex));
//					}
					
				}
			}
			
			
		}

//		String bom = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
//		return bom + doc.asXML().replace("&amp;", "&").replace("\"", "'").replace("\n", "");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputFormat f = OutputFormat.createCompactFormat();
		f.setAttributeQuoteCharacter('\'');
		XMLWriter writer = new XMLWriter(bos, f);

		writer.setEscapeText(false);
		writer.write(doc.getRootElement());
		writer.close();
		bos.toString();
//		return /*bom + */doc.asXML()/*.replace("&amp;", "&").replace("\"", "'")*/.replace("\n", "");
		
		if (oldWay){
			return bos.toString("UTF-8").replace("&apos;", "\\\"").replace("\n", "");
		}
		else{
			return bos.toString("UTF-8");//.replace("&apos;", "\"").replace("\n", "");
		}
		
		

		
	}
	
	public  String createMultiSerieXml(List<List<Object>> values, Properties chartProperties, int valuesSeriesNumber, String[] measureNames, String[][] colorSeries, String lineSerieName, Properties drillProperties) throws Exception{
		
		List<String[]> categorieNames = MultiSeriesHelper.getCategories(values, valuesSeriesNumber);
		Collections.sort(categorieNames, new Comparator<String[]>() {

			public int compare(String[] l1, String[] l2) {
				if (l1.length == 0 || l2.length == 0){
					return -1;
				}
				else if (l1[0] == null){
					return 1;
				}
				else if (l2[0] == null){
					return -1;
				}
				return l1[0].compareTo(l2[0]);
				
			}
		});
		
		boolean[] _splits = new boolean[valuesSeriesNumber];
		for(int i = 0; i < valuesSeriesNumber; i++){
			_splits[i] = false;
		}
		
		List<Measure> measures = MultiSeriesHelper.getMeasures(values, valuesSeriesNumber, measureNames, colorSeries, false, lineSerieName, null);

		
		
		HashMap<Measure, String[]> dataSets = new HashMap<Measure, String[]>();
		
		for(List<Object> row : values){
			
			String currentCategorie = row.get(valuesSeriesNumber).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
			
			for (int i = 0; i < categorieNames.size(); i++){
				if(currentCategorie.equals(categorieNames.get(i)[0])){
					for(int mNum = 0; mNum < valuesSeriesNumber; mNum++){
						Measure m = null;
						m = MultiSeriesHelper.getMeasure(measures, mNum, row.get(valuesSeriesNumber).toString(),false);
						
						if (m != null) {
							if (dataSets.get(m) == null){
								dataSets.put(m, new String[categorieNames.size()]);
							}
							
							dataSets.get(m)[i] = row.get(mNum ) != null ? row.get(mNum ).toString() : null;
						}
						
											
					}
				}
			}
		}
		
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(ROOT);
		//root.addAttribute("legendAllowDrag", "1");
		Element style = root.addElement("styles");
		Element myStyle = style.addElement("definition").addElement("style");
		myStyle.addAttribute("name", "mine").addAttribute("type", "font");
		style.addElement("application").addElement("apply")
			.addAttribute("toObject", "DataValues")
			.addAttribute("styles", "mine");
		
		if (chartProperties != null){
			overrideProperties(chartProperties);
			for(Object o : chartProperties.keySet()){
				if (((String)o).startsWith("_style_")){
					if (((String)o).endsWith("_bold")){
						myStyle.addAttribute("bold", chartProperties.getProperty((String)o));
					}
					if (((String)o).endsWith("_italic")){
						myStyle.addAttribute("italic", chartProperties.getProperty((String)o));
					}
					if (((String)o).endsWith("_underline")){
						myStyle.addAttribute("underline", chartProperties.getProperty((String)o));
					}
					if (((String)o).endsWith("_background")){
						myStyle.addAttribute("bgColor", chartProperties.getProperty((String)o));
					}
					if (((String)o).endsWith("fontColor")){
						myStyle.addAttribute("color", chartProperties.getProperty((String)o));
					}
					 
				}
				root.addAttribute((String)o, chartProperties.getProperty((String)o));
			}
		}
		
		Element categorie = root.addElement(CATEGORIES);
		for(String[] s : categorieNames){
			categorie.addElement(CATEGORIE).addAttribute(CATEGORIE_LABEL, s[1]);
		}
		
		//extract dual and combination opts
		String[] dualAxis = chartProperties.getProperty("_dualYPrimaries") == null ? new String[]{} : chartProperties.getProperty("_dualYPrimaries").split(";");		
		String[] rendering = chartProperties.getProperty("_combinationRenderings") == null ? new String[]{} : chartProperties.getProperty("_combinationRenderings").split(";");
		
		for(Measure m : measures){
			
			Element datasetNode = root.addElement(DATA_SET).addAttribute(DATA_SET_SERIESNAME, m.getName());
			
			
			
			
			//lineSerie
			if (m.isLineSerie){
				datasetNode.addAttribute(DATA_SET_RENDER_AS, "Line");
				datasetNode.addAttribute(DATA_SET_PARENTYAXIS, "S");
			}
			else{
				for(String r : rendering){
					if (r.startsWith(m.name+ ":")){
						datasetNode.addAttribute(DATA_SET_RENDER_AS, r.split(":")[1]);
					}
				}
				for(String r : dualAxis){
					if (r.startsWith(m.name + ":")){
						datasetNode.addAttribute(DATA_SET_PARENTYAXIS, r.split(":")[1]);
					}
				}
			}
			
			
			//color
			if (m.color != null){
				datasetNode.addAttribute(DATA_SET_COLOR, m.color);
			}
			
			
			//values
			for(String s : dataSets.get(m)){
				Element e = datasetNode.addElement(DATA_SET_SET);
				int categrieIndex = datasetNode.indexOf(e);
				if (s != null){
					e.addAttribute(DATA_SET_SET_VALUE, s);
					addDrillDatas(e, drillProperties, s, categorieNames.get(categrieIndex)[0]);
				}
				else{
					e.addAttribute(DATA_SET_SET_VALUE, "null");
					addDrillDatas(e, drillProperties, "", categorieNames.get(categrieIndex)[0]);
				}
				
			}
			
		}
//		new XMLWriter(System.out, OutputFormat.createPrettyPrint()).write(doc);
//		String bom = new String(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
//		return /*bom +*/ doc.asXML()/*.replace("&amp;", "&")*/.replace("\"", "'").replace("\n", "");
	
	
	
		OutputFormat f = OutputFormat.createCompactFormat();
		f.setAttributeQuoteCharacter('\'');

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		XMLWriter writer = new XMLWriter(bos, f);

		writer.setEscapeText(false);
		writer.write(doc.getRootElement());
		writer.close();
		bos.toString();
//		return /*bom + */doc.asXML()/*.replace("&amp;", "&").replace("\"", "'")*/.replace("\n", "");
		//return bos.toString("UTF-8").replace("&apos;", "\\\"").replace("\n", "");
		if (oldWay){
			return bos.toString("UTF-8").replace("&apos;", "\\\"").replace("\n", "");
		}
		else{
			return bos.toString("UTF-8");//.replace("&apos;", "\\\"").replace("\n", "");
		}

	}
		
		
		
	private  void addDrillDatas(Element setElement, Properties drillProperties, String value, String categorie)throws Exception{
		if (drillProperties != null){
			String url = drillProperties.getProperty("url");
			String pName = drillProperties.getProperty("pName");
			String categorieAsValue = drillProperties.getProperty("categoryAsValue");
			String drillJs = drillProperties.getProperty("drillJs");
			String link = "";
			String event = drillProperties.getProperty("event");

			
			String zoomDivName = drillProperties.getProperty("zoomDivName");
			if (zoomDivName != null ){
				if (setElement.getParent().getParent().attribute("clickURL") == null){
					String zoomWidth = drillProperties.getProperty("zoomWidth");
					String zoomHeight = drillProperties.getProperty("zoomHeight");;
					setElement.getParent().getParent().addAttribute("clickURL",  "javascript:zoomChart(\\\"" +zoomDivName + "\\\", " + zoomWidth + "," + zoomHeight + ",\\\"zoom\\\")");//   "j-zoomChart-" + zoomDivName + ",200,200,zoom");;
				}
				return;
			}
			
			
			//replace pName='XXX' by the value
			if (url == null && pName == null){
				return;
			}
			else if (pName == null){
				link = url;
				if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
					link += value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
					
				}
				else{
					link += categorie.toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
					
					
				}
			}
			else if (pName != null){
				if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
					if (oldWay){
						link +=" setParameter('" + pName + "', '" + URLEncoder.encode( value, "UTF-8") + "', true);";
					}
					else{
						link +=" setParameter('" + pName + "', '" + value + "', true);";
					}
					
				}
				else{
					if (oldWay){
						link +=" setParameter('" + pName + "', '" + URLEncoder.encode( categorie, "UTF-8") + "', true);";
					}
					else{
						link +=" setParameter('" + pName + "', '" + categorie+ "', true);";
					}
					
				}
				
				
				link += event;
//				int start = url.indexOf(pName + "=");
//				int end = url.indexOf("&", start);
//				
//				if (categorieAsValue == null|| false == Boolean.parseBoolean(categorieAsValue)){
//
//					if (start == -1){
//						if (url.contains("?")){
//							link = url + "&";// + pName + "=" + value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
//						}
//						else{
//							link = url + "?";// + pName + "=" + value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
//						}
//						link += URLEncoder.encode(pName, "UTF-8");
//						link += "=";
//						link += URLEncoder.encode(value, "UTF-8");
//
//					}
//					else{
//						link = url.substring(0, start);// + (pName + "=").length());
//						link += URLEncoder.encode(pName, "UTF-8");
//						link += "=";
//						link += URLEncoder.encode(value, "UTF-8");
//
//						if (end != -1){
//							link += url.substring(end);
//						}
//					}
//					
//				}
//				else{
//					if (start == -1){
//						if (url.contains("?")){
//							link = url + "&";// + pName + "=" + categorie.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
//						}
//						else{
//							link = url + "?";// + pName + "=" + categorie.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
//						}
//						link += URLEncoder.encode(pName, "UTF-8");
//						link += "=";
//						link += URLEncoder.encode(categorie, "UTF-8");
//
//					}
//					else{
//						link = url.substring(0, start);// + (pName + "=").length());
//						link += URLEncoder.encode(pName, "UTF-8");
//						link += "=";
//						link += URLEncoder.encode(categorie, "UTF-8");
//						if (end != -1){
//							link += url.substring(end);
//						}
//					}
//					
//				}
			}
			if (drillJs != null){
//					setElement.addAttribute("link", "JavaScript:openUrl(&#39;" + URLEncoder.encode(link, "UTF-8") +"&#39;)");
				setElement.addAttribute(DRILL, "JavaScript:openUrl(&#39;" + /*URLEncoder.encode(link, "UTF-8")*/link +"&#39;)");
			}
			else{
//				setElement.addAttribute(DRILL, link);//URLEncoder.encode( link , "UTF-8"));
				setElement.addAttribute(DRILL, "JavaScript:" + link);
			}
			
			
		}
	}
		
		
		
		
		
		
		
		

	
	/**
	 * Override the propertyName with specific ones
	 * called just before generating XML
	 */
	abstract protected void overrideProperties(Properties prop);
}
