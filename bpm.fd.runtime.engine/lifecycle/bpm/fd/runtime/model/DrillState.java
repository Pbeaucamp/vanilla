package bpm.fd.runtime.model;

import java.util.ArrayList;
import java.util.List;

public class DrillState {
	private volatile int currentLevel = 0;
	private volatile int maxLevel = 0;
	private volatile List<String> levelValues = new ArrayList<String>();
	
	public DrillState(int maxLevel){
		this.maxLevel = maxLevel;
	}
	
	public void reset(){
		this.currentLevel = 0;
		levelValues.clear();
	}
	
	public boolean drillDown(String value){
		if (canDrillDown()){
			if (currentLevel >= levelValues.size()){
				levelValues.add(null);
			}
			levelValues.set(currentLevel, value);
			currentLevel++;
			return true;
		}
		return false;
	}
	
	public boolean drillUp(){
		if (canDrillUp()){
			currentLevel--;
			return true;
		}
		return false;
	}
	
	public boolean canDrillDown(){
		return (currentLevel + 1) < maxLevel ;
	}
	public boolean canDrillUp(){
		return currentLevel > 0;
	}
	public int getCurrentLevel() {
		return currentLevel;
	}
	public String getLevelValue(int lvlNumber) {
		if (lvlNumber < 0){
			return "All";
		}
		return levelValues.get(lvlNumber);
	}
}
