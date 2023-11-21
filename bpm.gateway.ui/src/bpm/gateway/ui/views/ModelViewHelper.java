package bpm.gateway.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.TreeParent;
import bpm.gateway.ui.viewer.TreeStaticObject;


public class ModelViewHelper {
	
	private static TreeViewer viewer;
	protected static TreeStaticObject transformations,links, forms;//,variables, parameters; 
	protected static TreeParent root;
	
	public static void createTree(TreeViewer viewer){
		ModelViewHelper.viewer = viewer;
		
		root = new TreeParent(""); //$NON-NLS-1$
		
		transformations = new TreeStaticObject(Messages.ModelViewHelper_1);
		root.addChild(transformations);
		
		
//		parameters = new TreeStaticObject(Messages.ModelViewHelper_2);
//		root.addChild(parameters);
//		
//		variables = new TreeStaticObject(Messages.ModelViewHelper_3);
//		root.addChild(variables);
		
		links = new TreeStaticObject(Messages.ModelViewHelper_4);
		root.addChild(links);
		
		forms = new TreeStaticObject(Messages.ModelViewHelper_5);
		root.addChild(forms);
				
		viewer.setInput(root);
		
		
	}

	
	
	public static Image getImageFor(Object o){
		
		
		return null;
	}
	
}

