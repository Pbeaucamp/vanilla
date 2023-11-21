package bpm.fd.runtime.model;

import java.util.ArrayList;
import java.util.List;

public class SlicerState {
	private List<String>[] levelValues ;
	private int maxLevel;
	private boolean linkedLevels;
	public SlicerState(int maxLevel, boolean linkedLevels){
		this.linkedLevels = linkedLevels;
		levelValues = new ArrayList[maxLevel + 1];
		this.maxLevel = maxLevel;
		for(int i =0; i <= maxLevel; i++){
			levelValues[i] = new ArrayList<String>();
		}
	}
	public boolean isLinkedLevels(){
		return linkedLevels;
	}
	public List<String> getLevelValues(int levelNumber){
		if (levelNumber < levelValues.length){
			return levelValues[levelNumber];
		}
		return new ArrayList<String>();
	}
	
	public void addLevelValue(int i , String value){
		if (levelValues[i] == null){
			levelValues[i] = new ArrayList<String>();
		}
		levelValues[i].add(value);
	}
	
	public void clear() {
		levelValues = new ArrayList[maxLevel];
		
	}
}
