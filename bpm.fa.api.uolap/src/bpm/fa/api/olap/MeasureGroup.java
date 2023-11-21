package bpm.fa.api.olap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MeasureGroup extends Element {
	private HashMap<String, Measure> sons = new HashMap<String, Measure>();
	
	public MeasureGroup(String name, String caption) {
		super(name, name, caption);
	}
	
	public void addElement(Measure e) {
		sons.put(e.getName(), e);
	}
	
	public Collection<Measure> getMeasures() {
		return sons.values();
	}
}
