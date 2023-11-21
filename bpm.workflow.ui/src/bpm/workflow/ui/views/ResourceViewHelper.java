package bpm.workflow.ui.views;

import org.eclipse.jface.viewers.TreeViewer;

import bpm.workflow.ui.Messages;
import bpm.workflow.ui.viewer.TreeParent;
import bpm.workflow.ui.viewer.TreeStaticObject;

/**
 * Helper for the structure of the resources view part
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ResourceViewHelper {

	protected static TreeStaticObject databaseProvider;
	protected static TreeStaticObject mailProvider;
	protected static TreeStaticObject fileProvider;
	protected static TreeStaticObject variables;
	protected static TreeStaticObject parameters;
	protected static TreeStaticObject freemetricsProvider;
	protected static TreeParent root;

	public static void createTree(TreeViewer viewer) {

		root = new TreeParent(""); //$NON-NLS-1$

		freemetricsProvider = new TreeStaticObject(Messages.ResourceViewHelper_1);
		root.addChild(freemetricsProvider);

		fileProvider = new TreeStaticObject(Messages.ResourceViewHelper_2);
		root.addChild(fileProvider);

		databaseProvider = new TreeStaticObject(Messages.ResourceViewHelper_3);
		root.addChild(databaseProvider);

		mailProvider = new TreeStaticObject(Messages.ResourceViewHelper_4);
		root.addChild(mailProvider);

		variables = new TreeStaticObject(Messages.ResourceViewHelper_5);
		root.addChild(variables);

		parameters = new TreeStaticObject(Messages.ResourceViewHelper_0);
		root.addChild(parameters);

		viewer.setInput(root);

	}

}
