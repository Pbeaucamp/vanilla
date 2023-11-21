package bpm.workflow.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

import bpm.workflow.ui.Messages;
import bpm.workflow.ui.viewer.TreeParent;
import bpm.workflow.ui.viewer.TreeStaticObject;

/**
 * Helper which create the structure of the viewpart
 * 
 * @author CAMUS, MARTIN
 * 
 */
public class ModelViewHelper {

	protected static TreeStaticObject transformations, variables, links, forms;
	protected static TreeParent root;

	public static void createTree(TreeViewer viewer) {

		root = new TreeParent(""); //$NON-NLS-1$

		transformations = new TreeStaticObject(Messages.ModelViewHelper_1);
		root.addChild(transformations);

		variables = new TreeStaticObject(Messages.ModelViewHelper_2);
		root.addChild(variables);

		links = new TreeStaticObject(Messages.ModelViewHelper_3);
		root.addChild(links);

		forms = new TreeStaticObject(Messages.ModelViewHelper_4);
		root.addChild(forms);

		viewer.setInput(root);

	}

	public static Image getImageFor(Object o) {

		return null;
	}

}
