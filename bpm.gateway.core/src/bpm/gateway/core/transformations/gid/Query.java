package bpm.gateway.core.transformations.gid;

import java.util.ArrayList;
import java.util.List;

public class Query {
	private List<String> select = new ArrayList<String>();
	private List<String> from = new ArrayList<String>();
	private List<String> where = new ArrayList<String>();
	private List<String> groups = new ArrayList<String>();
	private List<String> order = new ArrayList<String>();
	
	
	public Query(){}
	
	public Query(Query... queries){
		for(int i = 0; i < queries.length; i++){
			
			for(String s : queries[i].getSelect()){
				addSelect(s);
			}
			
			for(String s : queries[i].getFrom()){
				addFrom(s);
			}
			
			for(String s : queries[i].getWhere()){
				addWhere(s);
			}
			
			for(String s : queries[i].getGroups()){
				addGroup(s);
			}
		}
	}
	
	/**
	 * @return the select
	 */
	public List<String> getSelect() {
		return select;
	}
	
	public void addSelect(String select){
		for(String s : getSelect()){
			if (s.equalsIgnoreCase(select)){
				return;
			}
		}
		this.select.add(select);
	}
	
	public void addFrom(String from){
		for(String s : getFrom()){
			if (s.equalsIgnoreCase(from)){
				return;
			}
		}
		this.from.add(from);
	}
	
	public void addWhere(String where){
		for(String s : getWhere()){
			if (s.equalsIgnoreCase(where)){
				return;
			}
		}
		this.where.add(where);
	}
	public void addGroup(String group){
		for(String s : getGroups()){
			if (s.equalsIgnoreCase(group)){
				return;
			}
		}
		this.groups.add(group);
	}
	
	/**
	 * @return the from
	 */
	public List<String> getFrom() {
		return from;
	}
	/**
	 * @return the where
	 */
	public List<String> getWhere() {
		return where;
	}

	/**
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	public String dump() {
		StringBuffer buf = new StringBuffer();
		
		boolean first = true;
		for(String s : getSelect()){
			if (first){
				first = false;
				buf.append("SELECT \n");
			}
			else{
				buf.append(",\n");
			}
			buf.append(s);
		}
		
		
		first = true;
		for(String s : getFrom()){
			if (first){
				first = false;
				buf.append("\n FROM \n");
			}
			else{
				buf.append(",\n");
			}
			buf.append(s);
		}
		
		
		first = true;
		for(String s : getWhere()){
			if (first){
				first = false;
				buf.append("\n WHERE \n");
			}
			else{
				buf.append(" AND \n");
			}
			buf.append("(" + s + ")");
		}
		
		
		first = true;
		for(String s : getGroups()){
			if (first){
				first = false;
				buf.append("\n GROUP BY \n");
			}
			else{
				buf.append(",\n");
			}
			buf.append(s);
		}
		
		first = true;
		for(String s : getOrders()){
			if (first){
				first = false;
				buf.append("\n ORDER BY \n");
			}
			else{
				buf.append(",\n");
			}
			buf.append(s);
		}
		
		return buf.toString();
	}

	public void addOrder(String ord) {
		for(String s : getOrders()){
			if (s.equalsIgnoreCase(ord)){
				return;
			}
		}
		this.order.add(ord);
		
	}
	public List<String> getOrders(){
		return order;
	}
	
}
