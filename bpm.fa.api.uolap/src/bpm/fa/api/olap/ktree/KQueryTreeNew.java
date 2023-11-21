package bpm.fa.api.olap.ktree;


import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.projection.Projection;
import bpm.fa.api.olap.projection.ProjectionMeasure;
import bpm.fa.api.olap.projection.ProjectionMeasureCondition;


/*
 * it is the Knowledge TREE
 */

public class KQueryTreeNew {
	private ItemAxis col;
	private ItemAxis row;
//	private Node where;
	private String from;
	
	private NodeWhere where;
	private NodeWithMember withmb;
	
	private boolean showEmpty;
	
	private List<String> percents;
	private List<String> dim;
	
	private Projection projection;
	
	public KQueryTreeNew(boolean showEmpty, List<String> percents, List<String> dim) {
		//
		col = new ItemAxis();
		row = new ItemAxis();
		//
		this.showEmpty = showEmpty;
		where = new NodeWhere();
		withmb = new NodeWithMember();
		this.percents = percents;
		this.dim = dim;
	}
	
	public KQueryTreeNew(boolean showEmpty, List<String> percents, List<String> dim, Projection proj) {
		this(showEmpty, percents, dim);
		projection = proj;
	}
	
	public String getMDX() {
		
////		String query = "\nwith member [Store].[All Stores].[1] as " +
////				"'Aggregate({[Store].[All Stores].[Canada], [Store].[All Stores].[USA].[WA]})'"; 
		String query = "\n";
		
		if (where.hasWithMembers() || !withmb.isEmpty())
			query += "WITH \n";
		
		query += where.getWithMembers();
		
		query += withmb.getMDX();
		
		if(percents != null && percents.size() != 0) {
			if (!(where.hasWithMembers() || !withmb.isEmpty())) {
				query += "with ";
			}
			for(String percent : percents) {
				String[] una = percent.split("\\.");
				String perName = una[1].replaceAll("\\]", "");
				query += "member [Measures]." + perName + " %] as ";
//				if(isMondrian) {
					query += "'" + percent + "/(" + percent;
					for(String d : dim) {
						query += "," + d;
					}
//				}
//				else {
////				"'" + percent + "/(" + percent;// + "," + dim + ")', " +
//					query += "'" + percent + "/Sum(Root()," + percent;// + "," + dim + ")', " +
//				
//				}
//				for(String d : dim) {
//					query += "," + d;
//				}
				query+=")', format_string='Percent'\n";
			}
		}
		
		if(projection != null && projection.getType().equals(Projection.TYPE_WHATIF)) {
			//FIXME : Create the mdx for the projection here
			if (!(where.hasWithMembers() || !withmb.isEmpty())) {
				query += "with ";
			}
			for(ProjectionMeasure mes : projection.getProjectionMeasures()) {
				String[] una = mes.getUname().split("\\.");
				String perName = una[1].replaceAll("\\]", "");
				query += " member [Measures]." + perName + "_"  + projection.getName() + "] as ";
				for(ProjectionMeasureCondition cond : mes.getConditions()) {
					query += " IIF(";
					for(String mem : cond.getMemberUnames()) { 
						query += mem +",";
					}
					query = query.substring(0, query.length() - 1);
					query += "), " + cond.getFormula() + " ," + mes.getUname() + "\n";
				}
			}
		}
		
		query += "\nSELECT \n" + col.getMdx("columns", showEmpty) +",\n"+ row.getMdx("rows", showEmpty) + "\n" + from + "\n" + where.getMDX();
		//System.out.println("###Ktree executing : " + query);
		return query;
		
//		String q = "SELECT " +
//				"NON EMPTY {" +
//					"ORDER " +
//						"(Union " +
//							"(Union " +
//								"(Union " +
//									"(Union " +
//										"(Union " +
//											"(Union (" +
//												"{[Time].[All Times]}, " +
//												"{[Time].[All Times].[1998].[Q3]}" +
//											"), " +
//											"{[Time].[All Times].[1998].[Q1]}" +
//										"), " +
//										"Union " +
//											"({[Time].[All Times].[1998].[Q4]}, " +
//											"{[Time].[All Times].[1998].[Q3]}" +
//										")" +
//									"), " +
//									"Union " +
//										"(Union (" +
//											"{[Time].[All Times].[1998].[Q2]}, " +
//											"{[Time].[All Times].[1998].[Q3]}" +
//										"), " +
//										"{[Time].[All Times].[1998].[Q1]}" +
//									")" +
//								"), " +
//							"Union (Union (Union ({[Time].[All Times].[1997]}, {[Time].[All Times].[1998].[Q3]}), {[Time].[All Times].[1998].[Q1]}), Union ({[Time].[All Times].[1998].[Q4]}, {[Time].[All Times].[1998].[Q3]}))), Union (Union (Union (Union ({[Time].[All Times].[1998]}, {[Time].[All Times].[1998].[Q3]}), {[Time].[All Times].[1998].[Q1]}), Union ({[Time].[All Times].[1998].[Q4]}, {[Time].[All Times].[1998].[Q3]})), Union (Union ({[Time].[All Times].[1998].[Q2]}, {[Time].[All Times].[1998].[Q3]}), {[Time].[All Times].[1998].[Q1]}))), [Measures].[Profit], ASC)} on columns," +
//				"NON EMPTY {Hierarchize(Crossjoin (Union (Union (Union ({[Store].[All Stores]}, {[Store].[All Stores].[USA]}), {[Store].[All Stores].[Canada]}), Union ({[Store].[All Stores].[Mexico]}, {[Store].[All Stores].[USA]})), {[Measures].[Profit]}))} on rows" +
//				"FROM [Sales]";
	}
	
	public void drillOn(String todo) {
//		row.drill(todo);
//		col.drill(todo);
	}
	
	public void addWhere(String str) {
		where.addWhere(str);
	}
	
	public void addWithMember(String str) {
		withmb.addWithMember(str);
	}
	
//	public void setCol(boolean nonempty) {
//		col = new Item(nonempty, "columns");
//	}
//	
//	public void setRow(boolean nonempty) {
//		row = new Item(nonempty, "rows");
//	}
	
	public void addCol(String lvl, Object action) {
		col.add(lvl, action);
		//col_list.add(lvl);
	}
	public void addRow(String lvl, Object action) {
		lvl = findProjectionMeasure(lvl);
		row.add(lvl, action);
		
//		StringTokenizer st = new StringTokenizer("" + lvl, "]");
//		String hiera;
//		
//		hiera = st.nextToken();
//		MessageDialog.openInformation(par.getShell(), "", hiera);
		//row_list.add(lvl);
	}
	/*
	public void deleteCol(String lvl) {
		
	}
	public void deleteCol(String lvl) {
		
	}*/
	
	/**
	 * If lvl is a measure used in the projection, replace it
	 * else just return lvl
	 */
	private String findProjectionMeasure(String lvl) {
		
		if(projection != null && projection.getType().equals(Projection.TYPE_WHATIF)) {
			for(ProjectionMeasure m : projection.getProjectionMeasures()) {
				if(m.getUname().equals(lvl)) {
					String[] una = m.getUname().split("\\.");
					String perName = una[1].replaceAll("\\]", "");
					return "[Measures]." + perName + "_"  + projection.getName() + "]";
				}
			}
		}
		
		return lvl;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	/*
	public void swapAxes() {
		Item tmp;
		tmp = col.getItem();
		col.setItem(row.getItem());
		row.setItem(tmp);
	}*/
	


	
	
	

	
//	public class Leaf extends Item {
//		private String lvl;
//		//private boolean measures = false;
//		private boolean ismenber = false;
//		private Topx topx;
//		private String percent;
//		
//		public Leaf(String todo, Object action) {
//			lvl = todo;
//			if(action instanceof Topx) {
//				this.topx = (Topx)action;
//			}
//			else if(action instanceof String) {
//				percent = action.toString();
//			}
//			if (lvl.charAt(lvl.length() - 1) != ']') {
//				this.ismenber = true;
//			}
//			
//		}
//
//		public String getMDX() {
//			if (!this.ismenber) {
//				if(topx != null) {
//					lvl = "TopCount(" + lvl + "," + topx.getCount() + "," + topx.getElementTarget() + ")";
//				}
//				else if(percent != null) {
//					String[] una = percent.split("\\.");
//					String perName = una[1].replaceAll("\\]", "");
//					if(una[2].equalsIgnoreCase("true")) {
//						lvl = "Union({" + lvl + "}, {[Measures]." + perName + " %]})";
//					}
//					else {
//						lvl = "[Measures]." + perName + " %]";
//					}
//				}
//				return "{" + lvl + "}";
//			}
//			else{
//				if(topx != null) {
//					lvl = "TopCount(" + lvl + "," + topx.getCount() + "," + topx.getElementTarget() + ")";
//				}
//				else if(percent != null) {
//					String[] una = percent.split("\\.");
//					String perName = una[1].replaceAll("\\]", "");
//					if(una[2].equalsIgnoreCase("true")) {
//						lvl = "Union({" + lvl + "}, {[Measures]." + perName + " %]})";
//					}
//					else {
//						lvl = "[Measures]." + perName + " %]";
//					}
//				}
//				return lvl;
//			}
//		}
//		
//		public boolean findHierarchy(String todo, Item toadd) {
//			StringTokenizer st = new StringTokenizer("" + todo, "]");
//			String hiera;
//			
//			//st.nextToken();
//			hiera = st.nextToken();
//			
//			
//			
//			if (lvl.startsWith(hiera+ "]")) {
//				//MessageDialog.openInformation(par.getShell(), "", "hiera = " + hiera + "]\nlevel =" + lvl);
//				return true;
//			}
//			else {
//				//MessageDialog.openInformation(par.getShell(), "", "could not find hiera : " + hiera + " in " + lvl);
//				return false;
//			}
//		}
//		
//		public String getString() {
//			return lvl;
//		}
//	}
	
	
	
	public class NodeWithMember {
		private ArrayList<String> items; 
		
		public NodeWithMember() {
			items = new ArrayList<String>();
		}
		
		public void addWithMember(String str) {
			items.add(str);
		}
		
		public boolean isEmpty() {
			//return items.size() == 0;
			return items.isEmpty();
		}
		
		public String getMDX() {
			String tmp = "";
			
			//XXX
//			if (!items.isEmpty()) {
//				tmp += "with \n";
//			}
			
			for (int i=0; i < items.size(); i++)
				tmp += "    " + items.get(i) + "\n";
			
			return tmp;
		}
	}
	
	public class NodeWhere {
		private ArrayList<String> items; 
		private ArrayList<WereWithMember> nodes;
		
		public NodeWhere() {
			items = new ArrayList<String>();
			nodes = new ArrayList<WereWithMember>();
		}
		
		public void addWhere(String current) {
			/*
			ispresent in withnodes
				add to node
			ispresent in free where's
				delete free where and move to withnodes
			isnotpresent
				add to free where's
			*/
			String[] buf = current.split("].");
			String 	 hiera = buf[0] + "]"; //.replace("[", "");
			String 	 level = buf[1].replace("[", "");
			
//			for (int i=0; i < items.size(); i++) {
//				if (!items.get(i).startsWith(hiera)) {
//					System.out.println("found distinct where, aborting");
//					return;
//				}
//			}
			
			for (int i=0; i < nodes.size(); i++) {
				if (nodes.get(i).isSameHiera(hiera)) {
					nodes.get(i).addWithMember(current);
					return;
				}
			}
			
			for (int i=0; i < items.size(); i++) {
				System.out.print("Trying to add " + current + "to " + items.get(i) + " group,");
				if (items.get(i).startsWith(hiera)) {
					WereWithMember tmp = new WereWithMember(hiera, level);
					tmp.addWithMember(current); //add current
					tmp.addWithMember(items.get(i)); //add one already present
					items.remove(i); //remove from free where's
					items.add(tmp.getWithMemberName()); //add aggregate name
					
					nodes.add(tmp);
					return;
				}
				else {
				}
			}
			
			//found some free'ed but not from the same hiera/level
			//if (items.size() > 1) {
//				WereWithMember tmp = new WereWithMember(hiera, level);
//				tmp.addWithMember(current); //add current
//				nodes.add(tmp);
//				items.add(tmp.getWithMemberName());
			//}
			
			items.add(current);
		}
		
		public boolean isEmpty() {
			return items.size() == 0;
		}
		
		public boolean hasWithMembers() {
			return !nodes.isEmpty();
		}
		
		public String getWithMembers() {
			if (nodes.isEmpty())
				return "";
			
			//XXX
			StringBuffer buf = new StringBuffer("");
			//StringBuffer buf = new StringBuffer("WITH\n");
			
			for (int i=0; i < nodes.size(); i++) {
				buf.append(nodes.get(i).getWithMemberMDX() + "\n");
			}
			
			return buf.toString();
		}
		
		public String getMDX() {
			if (items.size() > 0) {
				String buf = "WHERE (";
				
				for (int i=0; i < items.size(); i++) {
					buf += items.get(i);
					
					buf += (i + 1 == items.size()) ? ")\n" : ",";
				}
				
				return buf;
			}
			else
				return "";
		}
	}
	
	public class WereWithMember {
		private ArrayList<String> items; 
		private String hierarchy;
		private String level;
		//hiera or level???
		
		public WereWithMember(String hiera, String lvl) {
			items = new ArrayList<String>();
			hierarchy = hiera;
			level = lvl;
		}
		
		public boolean isSameHiera(String hiera) {
			return hierarchy.equalsIgnoreCase(hiera); 
		}
		
		public void addWithMember(String str) {
			items.add(str);
		}
		
		public String getWithMemberMDX() {
			//with member [Store].[All Stores].[Aggr] as 'Aggregate({[Store].[All Stores].[Canada], [Store].[All Stores].[USA].[WA]})'
			String tmp = "	member " + getWithMemberName() + " as Aggregate({" ;
			
			for (int i=0; i < items.size(); i++)
				tmp += items.get(i) + (i + 1 != items.size() ? ", " : ""); //add a trailing comma if not last
			
			return tmp + "})";
		}
		
		public String getWithMemberName() {
			return "" + hierarchy + ".[" + level + "].[Aggr]";
		}
	}
}