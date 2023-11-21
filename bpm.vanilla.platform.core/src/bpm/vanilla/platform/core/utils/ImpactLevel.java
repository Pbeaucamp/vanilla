package bpm.vanilla.platform.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to have a level of the "impact chart"
 * Just here to avoid list of list of list of list
 * @author Marc
 *
 */
public class ImpactLevel {

	/**
	 * either items or datasources
	 */
	private List<Object> elements;
	private List<ImpactLevel> children = new ArrayList<ImpactLevel>();

	public List<Object> getElements() {
		return elements;
	}

	public void setElements(List<Object> elements) {
		this.elements = elements;
	}

	public List<ImpactLevel> getChildren() {
		return children;
	}

	public void setChildren(List<ImpactLevel> children) {
		this.children = children;
	}

	public void addChild(ImpactLevel currentLevel) {
		this.children.add(currentLevel);
	}
	
}
