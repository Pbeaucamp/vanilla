/*
 * 
 * 
 * Written by : Sondag Cyrille
 */
package org.fasd.flexview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeObject;
import org.fasd.utils.trees.TreeParent;
import org.fexgenerator.generator.FlexGenerator;
import org.flexgenerator.elements.FlexProperty;
import org.flexgenerator.elements.Node;

public class FasdFlexGenerator extends FlexGenerator {
	private TreeParent model;
	private Node root;

	/**
	 * Instantiates a new fasd flex generator.
	 * 
	 * @param model
	 *            the model
	 */
	public FasdFlexGenerator(TreeParent model) {
		this.model = model;
		setOrientation(0);
		setGraphDepth(1);
		setGraphTitle("View Dimension"); //$NON-NLS-1$
		fill();
	}

	@Override
	public Map<Class<?>, List<FlexProperty>> createExtraAtrributes() {
		return null;
	}

	public void fill() {
		// Get the Base Hierarchy object, it will be the base node of the graph;
		TreeHierarchy root = getHierachyBase(this.model);
		// Than fill if
		fillTreeHierachy(root);
	}

	public void fillTreeHierachy(TreeParent treeRoot) {
		if (treeRoot != null && treeRoot.getChildren().length > 0) {
			// Create Graph Root node
			root = new Node(treeRoot.getName(), "Dimension", "dimension", "16"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// Than set it as the root Node
			setRootNode(this.root);
			// walk in tree
			fillTree(this.root, treeRoot.getChildren());
		}
	}

	public void fillTree(Node parent, Object[] children) {
		// Get TreeObject
		TreeObject[] toChildren = getTreeObject(children);
		if (toChildren != null)
			fillTree(parent, toChildren);
	}

	public void fillTree(Node parent, TreeObject[] children) {
		// Add children to the parent node
		// and get a map of node listed by TreeObjects where they made of.
		Map<TreeObject, Node> childrenMap = addChildren(parent, children);
		for (TreeObject child : children) {
			if (hasChildren(child)) {
				// if tree object contains Children (so it's a TreeParent)
				TreeParent p = getTreeParent(child);
				// create a node
				// its useless to add this node to the graph it's already
				// added when creating children nodes.
				Node parentNode = childrenMap.get(p);
				// Fill the tree
				fillTree(parentNode, p.getChildren());
			}
		}
	}

	private Map<TreeObject, Node> addChildren(Node parent, TreeObject[] children) {
		// create a map of node with key the TreeObject where they have been
		// made of.
		Map<TreeObject, Node> childrenMap = new HashMap<TreeObject, Node>();
		for (TreeObject treeObject : children) {
			Node child = createNode(treeObject);
			// set parent node
			child.addParentNode(parent);
			// add node to the graph.
			addNode(child);
			childrenMap.put(treeObject, child);
		}
		return childrenMap;
	}

	private boolean hasChildren(TreeObject child) {
		// Tree Object's have node only if they are a instance of TreeParent
		if (child instanceof TreeParent && ((TreeParent) child).hasChildren())
			return true;
		return false;
	}

	private TreeObject[] getTreeObject(Object[] children) {
		if (children != null) {
			// Transform object table into TreeObject table.
			ArrayList<TreeObject> tmChildren = new ArrayList<TreeObject>();
			for (Object obj : children) {
				TreeObject tmChild = getTreeObject(obj);
				if (tmChild != null)
					tmChildren.add(tmChild);
			}
			return tmChildren.toArray(new TreeObject[tmChildren.size()]);
		}
		return null;
	}

	private TreeParent getTreeParent(Object treeparent) {
		// A TreeParent is an object who can have TreeObject child.
		if (treeparent instanceof TreeParent) {
			TreeParent parent = (TreeParent) treeparent;
			return parent;
		}
		return null;
	}

	private TreeObject getTreeObject(Object treeMemberObject) {
		// A TreeObject is the base Object of the tree
		if (treeMemberObject instanceof TreeObject) {
			return (TreeObject) treeMemberObject;
		}
		return null;
	}

	private TreeHierarchy getHierachyBase(TreeParent model) {

		TreeHierarchy ret = null;
		if (model.getChildren().length == 1) {
			Object[] roots = model.getChildren();
			if (roots[0] instanceof TreeDim) {
				TreeDim dim = (TreeDim) roots[0];
				if (dim.getChildren().length == 1) {
					Object[] hierarchys = dim.getChildren();
					if (hierarchys[0] instanceof TreeHierarchy) {
						ret = (TreeHierarchy) hierarchys[0];
					}
				}
			}
		}
		return ret;
	}

	// Create a new node with a TreeObject
	public Node createNode(TreeObject tree) {
		return new Node(tree.getName(), "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
