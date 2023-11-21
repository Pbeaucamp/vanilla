package bpm.studio.expressions.core.model;

import java.util.ArrayList;
import java.util.List;

public abstract class StructureDimension{
	private String name = "newDimension";
	
	private List<IField> levels = new ArrayList<IField>();
	
		
	public String getName() {
		return name;
	}
	
	

	/**
	 * use the given to rebuild the dimensionStructure
	 * @param model
	 */
	public abstract void build(Object model);
	
	
	public void setName(String name){
		this.name = name;
	}
	
	
	
	public void addLevel(IField element) {
		if (element == null){
			return ;
		}
		if (!levels.contains(element) ){
			levels.add(element);
		}

	}
	
	public void removeLevel(IField element){
		levels.remove(element);
	}
	
	public List<IField> getLevels(){
		return new ArrayList<IField>(levels);
	}
	
	public void swapLevels(IField lvl1, IField l2){
		int i1 = levels.indexOf(lvl1);
		int i2 = levels.indexOf(l2);
		
		if (i1 == -1 || i2 == -1){
			return;
		}
		
		levels.set(i1, l2);
		levels.set(i2, lvl1);
	}
	
}
