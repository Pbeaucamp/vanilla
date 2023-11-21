package org.fasd.olap;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fasd.datasource.DataObjectItem;

import bpm.fasd.expressions.api.model.FasdDimension;
import bpm.fasd.expressions.api.model.FasdMeasure;
import bpm.fasd.expressions.api.model.FasdMeasureMdxConverter;
import bpm.fasd.expressions.api.model.FasdParser;
import bpm.studio.expressions.core.measures.impl.Dimension;
import bpm.studio.expressions.core.measures.impl.DimensionLevel;
import bpm.studio.expressions.core.measures.impl.Measure;

public class OLAPMeasure extends OLAPElement{
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	protected String formula = "";
	protected String formatstr = "Standard";
	
	protected HashMap<String, String> propExpression = new HashMap<String, String>();
	
	
	protected String originId="";
	
	protected String columnName = "";
	
	protected DataObjectItem origin;
	protected DataObjectItem label;
	protected OLAPMeasureGroup group;
	
	protected String aggregator = "sum";
	protected String desc = "";
	protected String groupId = "";
	protected String type = "physical";
	protected String caption = "";
	protected int order = 0;
	protected boolean visible = true;
	protected String labelId = "";
	
	protected String dataType = "";
	protected String formatter = "";
	protected String dimensionName = "Measures";
	
	protected String lastTimeDimensionName;
	
	protected HashMap<String, String> expression = new HashMap<String, String>();
	
	
	protected String colorScript;
	
	
	
	/**
	 * @return the colorScript
	 */
	public String getColorScript() {
		return colorScript;
	}

	/**
	 * @param colorScript the colorScript to set
	 */
	public void setColorScript(String colorScript) {
		this.colorScript = colorScript;
	}

	public String getAggregator() {
		return aggregator;
	}

	public void setAggregator(String aggregator) {
		this.aggregator = aggregator;
	}
	
	

	/**
	 * @return the lastTimeDimensionName
	 */
	public String getLastTimeDimensionName() {
		return lastTimeDimensionName;
	}

	/**
	 * @param lastTimeDimensionName the lastTimeDimensionName to set
	 */
	public void setLastTimeDimensionName(String lastTimeDimensionName) {
		this.lastTimeDimensionName = lastTimeDimensionName;
	}

	public DataObjectItem getOrigin() {
		return origin;
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}

	public void setOrigin(DataObjectItem origin) {
		
		this.origin = origin;
		if (origin != null){
			originId = origin.getId();
			type = "physical";
		}
		else{
			originId = "";
		}
	}

	public String getOriginId() {
		return originId;
	}

	public void setOriginId(String originId) {
		this.originId = originId;
		if (!originId.trim().equals(""))
				type = "physical";
	}

	public OLAPMeasure(String name) {
		super(name);
		counter++;
		id = "h" + String.valueOf(counter);
	}
	
	public OLAPMeasure() {
		super("calculatedMeasure");
		counter++;
		id = "h" + String.valueOf(counter);
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
		
//		update(this, formula);
	}

	public String getFormatstr() {
		return formatstr;
	}

	
	
	public void setFormatstr(String formatstr) {
		this.formatstr = formatstr;
//		update(this, formatstr);
	}
	
	
	protected String getXMLForLastAggregator(FAModel faModel, String lastTimeDimensionLevelName){
		aggregator = "sum";
		String baseXml = getXML(faModel);
		aggregator = "last";
		baseXml = baseXml.replace("visible=\"true\"", "visible=\"false\"").replace(" name=\"" + getName(), " name=\"__" + getName());
		
		
		
		String buf = baseXml + "\n        <CalculatedMember name=\"" + super.getName() + 
		"\" dimension=\"" + dimensionName + "\" " + "formula=\"" + "([Measures].[__" + getName() +  "],ClosingPeriod ( [" + lastTimeDimensionName +  "].[" + lastTimeDimensionLevelName + "], [" + lastTimeDimensionName + "].CurrentMember))" ;

		if (!caption.trim().equals(""))
		buf += "\" caption=\"" + caption + "\">\n";
		else
		buf += "\">\n";
			
		
		for(String key : propExpression.keySet()){
		String expr = propExpression.get(key).replaceAll("<", "&lt;");
		expr = expr.replaceAll(">", "&gt;");
		buf += "            <CalculatedMemberProperty name=\"" + key + "\" expression=\"" + expr + "\""  + "/>\n";
		}
		
		buf += "       </CalculatedMember>\n";
//		StringBuffer buf = new StringBuffer(getXML().replace(" + aggregator, newChar));
//		
		return buf.toString();
	}
	
	public String getXML(FAModel model) {
		
		
		if (type.equals("physical")){
			StringBuffer buf = new StringBuffer();
			buf.append("        <Measure name=\"" + getName());
			
			if (!caption.trim().equals(""))
				buf.append("\" caption=\"" + caption);
			
			if (!dataType.trim().equals(""))
				buf.append("\" datatype=\"" + dataType);
			
			if (!formatter.trim().equals(""))
				buf.append("\" formatter=\"" + formatter);
			
			if (origin.getType().equals("physical")){
				buf.append("\" column=\"");
				if (origin != null)
					buf.append(origin.getOrigin() );
				else
					buf.append(columnName);
				
				buf.append("\" aggregator=\"" + aggregator);
				buf.append("\" formatString=\"" + formatstr);
				if (!visible)
					buf.append("\" visible=\"false");
				buf.append("\"/>\n");
			}
			else{
				buf.append("\" aggregator=\"" + aggregator);
				buf.append("\" formatString=\"" + formatstr);
				if (!visible)
					buf.append("\" visible=\"false\"");
				if (!dataType.trim().equals(""))
					buf.append("\" datatype=\"" + dataType);
				
				if (!formatter.trim().equals(""))
					buf.append("\" formatter=\"" + formatter);
				
				if (!visible)
					buf.append("\" visible=\"false\"");
				
				buf.append("\">\n");
				
				buf.append("            <MeasureExpression>\n");
				buf.append("                <SQL dialect=\"generic\">\n");
				buf.append("                " + origin.getOrigin() + "\n");
				buf.append("                </SQL>\n");
				buf.append("            </MeasureExpression>\n");
				
				for(String key : propExpression.keySet()){
					String expr = propExpression.get(key).replaceAll("<", "&lt;");
					expr = expr.replaceAll(">", "&gt;");
					buf.append("            <CalculatedMemberProperty name=\"" + key + "\" expression=\"" + expr + "\""  + "/>\n");
				}
				buf.append("        </Measure>\n");
			}
			
			
			return buf.toString();
		}
		else {
			String buf = "        <CalculatedMember name=\"" + super.getName() + 
											"\" dimension=\"" + dimensionName + "\" " + "formula=\"" + formula ;
			
			if (!caption.trim().equals(""))
				buf += "\" caption=\"" + caption + "\">\n";
			else
				buf += "\">\n";
			//buf += "            <Formula>" + formula + "</Formula>\n";
			//buf += "            <CalculatedMemberProperty name=\"FORMAT_STRING\" value=\"" + formatstr + "\""+ (propExpression.equals("")?"":" expression=\""+propExpression+"\"")  + "/>\n";
			
			
			for(String key : propExpression.keySet()){
				String expr = propExpression.get(key).replaceAll("<", "&lt;");
				expr = expr.replaceAll(">", "&gt;");
				buf += "            <CalculatedMemberProperty name=\"" + key + "\" expression=\"" + expr + "\""  + "/>\n";
			}
			
			
			if (model != null && getColorScript() != null && !getColorScript().equals("")){
				try{
					List<Dimension> dims = new ArrayList<Dimension>();
					List<Measure> mes = new ArrayList<Measure>();
					for(OLAPDimension dim : model.getOLAPSchema().getDimensions()){
						dims.add(new Dimension(new FasdDimension(dim)));
					}
					
					for(OLAPMeasure f : model.getOLAPSchema().getMeasures()){
						mes.add(new FasdMeasure(f));
					}
					
					
					FasdParser parser = new FasdParser(mes, new ArrayList<DimensionLevel>(), dims);
					
					ByteArrayInputStream bos = new ByteArrayInputStream(getColorScript().getBytes());

					String mdx = FasdMeasureMdxConverter.getOperandMdx(parser.readChunk(bos));
					
					buf += "            <CalculatedMemberProperty name=\"FORMAT_STRING\" expression=\"" + mdx.replace("<", "&lt;").replace(">", "&gt;") + "\""  + "/>\n";
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			
			
			buf += "       </CalculatedMember>\n";

			return buf;
		}
		
	}
	
//	public String getFAXML(){
//		StringBuffer buf = new StringBuffer();
//		buf.append("    <CalculatedMeasure>\n");
//		buf.append("        <id>" + getId() + "</id>\n");
//		buf.append("        <name>" + getName() + "</name>\n");
//		buf.append("        <formula>" + formula + "</formula>\n");
//		buf.append("        <FormatString>" + formatstr + "</FormatString>\n");
//		buf.append("    </CalculatedMeasure>\n");
//		return buf.toString();
//	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("    <Measure-item>\n");
		buf.append("        <id>" + getId() + "</id>\n");
		buf.append("        <name>" + getName() + "</name>\n");
		buf.append("        <caption>" + caption + "</caption>\n");
		buf.append("        <description>" + desc + "</description>\n");
		buf.append("        <measure-group-id>" + groupId + "</measure-group-id>\n");
		buf.append("        <type>" + type + "</type>\n");
		buf.append("        <order>" + order + "</order>\n");
		buf.append("        <origin-id>" + originId + "</origin-id>\n");
		buf.append("        <formula>" + formula + "</formula>\n");
		buf.append("        <label>" + labelId + "</label>\n");
		buf.append("        <visible>" + visible + "</visible>\n");
		buf.append("        <aggregator>" + aggregator + "</aggregator>\n");
		buf.append("        <color-rules><![CDATA[" + getColorScript() + "]]></color-rules>\n");
		
		if (("last".equals(aggregator) || "first".equals(aggregator)) && lastTimeDimensionName != null && !"".equals(lastTimeDimensionName)){
			buf.append("        <lastTimeDimensionName>" + lastTimeDimensionName + "</lastTimeDimensionName>\n");
		}
		buf.append("        <formatString>" + formatstr + "</formatString>\n");
		
		
		
		
		for(String s : propExpression.keySet()){
			buf.append("        <property>\n");
			buf.append("            <name>" + s + "</name>\n");
			String expr = propExpression.get(s).replaceAll("<", "&lt;");
			expr = expr.replaceAll(">", "&gt;");
			buf.append("            <expression>" + expr + "</expression>\n");
			buf.append("        </property>\n");
		}
		
		if (!type.trim().equals("physical"))
			buf.append("        <dimensionName>" + dimensionName + "</dimensionName>\n");
		buf.append("    </Measure-item>\n");
		return buf.toString();
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
//		update(this, desc);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
//		update(this, groupId);
	}

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
//		update(this, labelId);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(String order) {
		try{
			this.order = Integer.parseInt(order);
		}
		catch (NumberFormatException e){
			this.order = 0 ;
		}
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
//		update(this, type);
	}

	public DataObjectItem getLabel() {
		return label;
	}

	public void setLabel(DataObjectItem label) {
		this.label = label;
//		update(this, label);
	}
	
	public void setGroup(OLAPMeasureGroup g){
//		if (group != null){
//			group.removeMes(this);
//		}
		if (g != null){
			setGroupId(g.getId());
			group = g;
		}
		else{
			setGroupId("");
			group = null;
		}
		if (group != null)
			group.addMeasure(this);
		
//		update(this, g);
	}
	
	public OLAPMeasureGroup getGroup(){
		return group;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * just used for parser
	 * @param visible
	 */
	public void setVisible(String visible){
		this.visible = Boolean.valueOf(visible);
	}

	public void addExpression(String dialect, String expr){
		expression.put(dialect, expr);
	}
	
	public HashMap<String, String> getExpressions(){
		return expression;
	}
	
	public HashMap<String, String> getPropertiesExpressions(){
		return propExpression;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public void addPropExpression(String propName,String propExpression) {
		this.propExpression.put(propName, propExpression);
	}

}
