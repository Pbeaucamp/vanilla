package org.fasd.olap;


public class OLAPMeasureColoringRules {
	
//	public static class ColorRule{
//		private String condition = "";
//		private Color color = new Color(0,0,0);
//		/**
//		 * @return the condition
//		 */
//		public String getCondition() {
//			return condition;
//		}
//		/**
//		 * @param condition the condition to set
//		 */
//		public void setCondition(String condition) {
//			this.condition = condition;
//		}
//		/**
//		 * @return the color
//		 */
//		public Color getColor() {
//			return color;
//		}
//		/**
//		 * @param color the color to set
//		 */
//		public void setColor(Color color) {
//			this.color = color;
//		}
//		
//	}
	
	private OLAPMeasure measure;
	
	private String colorRule;
	
	
	public String getRules(){
		return colorRule;
	}
	
	public OLAPMeasure getMeasure(){
		return measure;
	}
	
	public void setMeasure(OLAPMeasure measure){
		this.measure = measure;
	}
	
	public void setRule(String rule){
		colorRule = rule;
	}
	
	
	
}
