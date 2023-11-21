package bpm.fa.api.olap.ktree;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bpm.fa.api.olap.Topx;

public class Item {
	public static enum Operator {Hierarchize, Crossjoin, Union};
	public static enum Sepcial {TOPX, PERCENT};
	
	private Operator operator;
	
	private List<Item> childs = new ArrayList<Item>();
	private String uname;
	private boolean nonempty;
	private String axisName;
	private Object action;
	
	public Item(boolean nonempty, String axisName){
		this.nonempty = nonempty;
		this.axisName = axisName;
		this.operator = Operator.Hierarchize;

	}
	
	 	
	public Item(Operator operator){
		this.operator = operator;
		
	}
	public Item(String uname, Object action){
		this.uname = uname;
		this.action = action;
	}
	
	

	
	
	
	
	public String getMdx(){
		StringBuilder b = new StringBuilder();
		if (operator != null){
			
			if (operator == Operator.Hierarchize){
				if (!nonempty){
					b.append("NON EMPTY ");
				}
				b.append("{");
			}
			
			b.append(operator.name());
			b.append("(");
			
			boolean first = true;
			for(Item c : childs){
				if (first){first = false;}else{b.append(", ");}
				b.append(c.getMdx());
			}
			
			b.append(")");
			if (operator == Operator.Hierarchize){
				b.append("} on " + axisName);
			}
		}
		else if (uname != null){
			String uname = new String(this.uname);
			
			if (action != null){
				if (action instanceof Topx){
					Topx topx = (Topx)action;
					uname = "TopCount(" + uname + "," + topx.getCount() + "," + topx.getElementTarget() + ")";
				}
				else if (action instanceof String){
					String percent = (String)action;
					String[] una = percent.split("\\.");
					String perName = una[1].replaceAll("\\]", "");
					if(una[2].equalsIgnoreCase("true")) {
						uname = "Union({" + uname + "}, {[Measures]." + perName + " %]})";
					}
					else {
						uname = "[Measures]." + perName + " %]";
					}
				}
			}
			
			
			
			if (uname.endsWith(".children")){
				b.append(uname);
			}
			else{
				b.append("{");
				b.append(uname);
				b.append("}");
			}
			
			return b.toString();
		}
		
		//b.append(" ");
		return b.toString();
		
	}
	
	protected void setOperator(Operator op){
		this.operator = op;
	}



	public void addChild(Item cross) {
		childs.add(cross);
		
	}



	public void addAll(List<Item> l) {
		childs.addAll(l);
		
	}



	public String getUname() {
		return uname;
	}


	public void insertChild(int i, Item union) {
		if(childs != null && childs.size() > i) {
			childs.add(i, union);
		}
	}
}
