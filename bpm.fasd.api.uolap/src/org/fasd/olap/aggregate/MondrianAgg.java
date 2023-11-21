package org.fasd.olap.aggregate;

import java.util.ArrayList;
import java.util.List;


public class MondrianAgg {
	private String name = "";
	private String factCount = "";
	
	private List<MondrianAggLvl> lvl = new ArrayList<MondrianAggLvl>();
	private List<MondrianAggMes> mes = new ArrayList<MondrianAggMes>();
	private List<MondrianAggFKey> fkey = new ArrayList<MondrianAggFKey>();
	
	
	
	public class MondrianAggMes{
		private String column = "";
		private String mesName ="";
		public String getMesName() {
			return mesName;
		}
		public void setMesName(String mesName) {
			this.mesName = mesName;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String colName) {
			this.column = colName;
		}
		
		
	}
	
	public class MondrianAggLvl{
		private String column = "";
		private String levelName ="";
		public String getLevelName() {
			return levelName;
		}
		public void setLevelName(String levelName) {
			this.levelName = levelName;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String name) {
			this.column = name;
		}
		
		
		
	}

	public class MondrianAggFKey{
		private String factCol, aggCol;

		public String getAggCol() {
			return aggCol;
		}

		public void setAggCol(String aggCol) {
			this.aggCol = aggCol;
		}

		public String getFactCol() {
			return factCol;
		}

		public void setFactCol(String factCol) {
			this.factCol = factCol;
		}
		
	}
	
	public String getFactCount() {
		return factCount;
	}

	public void setFactCount(String factCount) {
		this.factCount = factCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MondrianAggLvl> getLvl() {
		return lvl;
	}

	public List<MondrianAggMes> getMes() {
		return mes;
	}
	public List<MondrianAggFKey> getFKey() {
		return fkey;
	}
	
	public void addAggMes(String name, String column){
		MondrianAggMes m = new MondrianAggMes();
		m.setMesName(name);
		m.setColumn(column);
		
		mes.add(m);
	}
	public void addAggLvl(String name, String column){
		MondrianAggLvl l = new MondrianAggLvl();
		l.setLevelName(name);
		l.setColumn(column);
		
		lvl.add(l);
	}
	public void addAggFKey(String fact, String agg){
		MondrianAggFKey f = new MondrianAggFKey();
		f.setAggCol(agg);
		f.setFactCol(fact);
		
		fkey.add(f);
	}
}
