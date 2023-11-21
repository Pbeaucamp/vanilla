package bpm.fd.runtime.engine.chart.jfree.generator.chart;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.runtime.engine.chart.ofc.generator.OpenFlashChartJsonGenerator;

public class MultiSeriesHelper {
	
	static public class Measure{
		public int index;
		public String label = "";
		public String name;
		public String color;
		public boolean isLineSerie = false;
		public boolean isSplitedSerie = false;
		public String getName(){
			return name + " " + label;
		}
	}
		
	public static  List<String[]> getCategories(List<List<Object>> values, int index){
		List<String[]> categorieNames = new ArrayList<String[]>();
		if (values == null){
			return categorieNames;
		}
		for(List<Object> row : values){
			boolean found = false;
			for(String[] s : categorieNames){
				if (row.get(index).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"").equals(s[0])){
					found = true;
					break;
				}
			}
			if (!found){
				categorieNames.add(new String[]{
						row.get(index).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\""),
						row.get(row.size() - 2).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\""),
						row.get(row.size() - 1).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"")});
			}
			
		}
		return categorieNames;
	}
		
	public static  List<Measure> getMeasures(List<List<Object>> values, int number, String[] measureNames, String[][] colors, boolean containsGroupSerie, String lineSerie, Integer namesOffset){
		
				
		List<Measure> l = new ArrayList<Measure>();
		for(List<Object> row : values){
			
			for(int i = 0; i < number; i++){
				boolean found = false;
				for(Measure m : l){
					if ( i == m.index && m.label.equals(row.get(row.size() - 3).toString())){
						found = true;
						break;
					}
				}
				
				if (!found){
					Measure m = new Measure();
					m.index = i;
					int j = i;
	
					m.name = measureNames != null ? (namesOffset != null ? measureNames[j + namesOffset] : measureNames[j]) : "Measure " + i ;
					if (containsGroupSerie){
						m.label = row.get(row.size() - 3).toString();
						
					}
					if (m.name.equals(lineSerie)){
						m.isLineSerie = true;
					}
					m.isSplitedSerie = true;
					l.add(m);

				}
				
			}
			
			if (!containsGroupSerie){
				break;
			}
		}
		
		for(Measure m : l){
			if (namesOffset == null){
				try{
					m.color = colors[m.index][0];
				}catch(Exception ex){
					
									
				}
				
			}
			else{
				try{
					m.color = colors[namesOffset][l.indexOf(m)];
				}catch(Exception ex){
				}
			}
			
			if (m.color == null){
				List<String> cols = new ArrayList<String>();
				for(int i = 0; i < colors.length; i++){
//					if (colors[i] != null){
//						for(int k = 0; k < colors[i].length; k++){
//							cols.add(colors[i][k]);
//						}
//					}
					for(int k = 0; k < colors[i].length; k++){
						cols.add(colors[i][k]);
					}
					
				}
				
				m.color = OpenFlashChartJsonGenerator.generateRandomColor(cols);	
			}
		}
		
		
		return l;
	}
		
	public static  Measure getMeasure(List<Measure> l , int index, String label , boolean containsSerie){
		for(Measure m : l){
			if (containsSerie){
				if ( index == m.index && m.label.equals(label)){
					return m;
				}
			}
			else{
				if ( index == m.index){
					return m;
				}
			}
			
		}
		return null;
	}
	
	

}
