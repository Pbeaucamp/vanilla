package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

import org.fasd.olap.aggregation.IMeasureAggregation;

public class OlapDynamicMeasure extends OLAPMeasure {

	private List<IMeasureAggregation> aggregations;

	public List<IMeasureAggregation> getAggregations() {
		return aggregations;
	}

	public void setAggregations(List<IMeasureAggregation> aggregations) {
		this.aggregations = aggregations;
	}
	
	public void addAggregation(IMeasureAggregation aggregation) {
		if(this.aggregations == null) {
			this.aggregations = new ArrayList<IMeasureAggregation>();
		}
		
		IMeasureAggregation toRm = null;
		for(IMeasureAggregation aggreg : this.aggregations) {
			if(aggreg.getLevel().equals(aggregation.getLevel())) {
				toRm = aggreg;
				break;
			}
		}
		if(toRm != null) {
			this.aggregations.remove(toRm);
		}
		
		this.aggregations.add(aggregation);
	}
	
	@Override
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		buf.append("    <Dynamic-Measure-item>\n");
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
		
		if(aggregations != null) {
			for(IMeasureAggregation agg : aggregations) {
				buf.append("		" + agg.getFaXml());
			}
		}
		
		buf.append("    </Dynamic-Measure-item>\n");
		return buf.toString();
	}
}
