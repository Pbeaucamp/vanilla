package bpm.gateway.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.Activator;
import bpm.gateway.core.Transformation;

public class ModelGraphNode {
	private Transformation transfo;

	private ModelGraphNode parent;

	private List<ModelGraphNode> childs = new ArrayList<ModelGraphNode>();

	public ModelGraphNode(Transformation transfo) {
		this.transfo = transfo;
	}

	public void addChild(ModelGraphNode child) {
		childs.add(child);
		setParent(this);
	}

	public void setParent(ModelGraphNode parent) {
		this.parent = parent;
	}

	/**
	 * @return the transfo
	 */
	public Transformation getTransfo() {
		return transfo;
	}

	/**
	 * @return the parent
	 */
	public ModelGraphNode getParent() {
		return parent;
	}

	/**
	 * @return the childs
	 */
	public List<ModelGraphNode> getChilds() {
		return childs;
	}

	private static HashMap<Transformation, Integer> numberOfRefresh;

	public void initNumberOfRefresh() {
		ModelGraphNode.numberOfRefresh = new HashMap<Transformation, Integer>();
	}

	public void initTransfo() {
		if (numberOfRefresh.get(getTransfo()) == null) {
			numberOfRefresh.put(getTransfo(), new Integer(1));
		}
		else if (numberOfRefresh.get(getTransfo()) >= 5) {
			return;
		}
		else {
			numberOfRefresh.put(getTransfo(), new Integer(numberOfRefresh.get(getTransfo()) + 1));
		}

		if (getTransfo() == null) {
			Activator.getLogger().debug("init Gateway");
		}
		else {
			Activator.getLogger().debug("init " + getTransfo().getName());
		}

		for (ModelGraphNode n : getChilds()) {
			n.initTransfo();
		}
		if (getTransfo() != null) {
			Activator.getLogger().debug("init descriptor" + getTransfo().getName());
			getTransfo().initDescriptor();
			// getTransfo().refreshDescriptor();
		}
	}

	public String dump(String offset) {
		StringBuffer buf = new StringBuffer();
		if (getTransfo() == null) {
			buf.append("**** ROOT ****\n");
		}
		else {
			buf.append(getTransfo().getName());
		}

		buf.append("\n");
		for (ModelGraphNode n : getChilds()) {

			buf.append(offset + "    |_ ");
			buf.append(n.dump(offset + "    |"));

		}
		return buf.toString();
	}
}
