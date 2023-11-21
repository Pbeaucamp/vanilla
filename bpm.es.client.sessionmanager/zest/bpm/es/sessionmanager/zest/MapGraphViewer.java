package bpm.es.sessionmanager.zest;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;

import bpm.es.sessionmanager.api.server.VanillaServer;

public class MapGraphViewer extends GraphViewer {

	public MapGraphViewer(Composite composite, int style) {
//		this.graph = new Graph(composite, style);
//		hookControl(this.graph);
		super(composite, style);
	}

//	protected GraphConnection addGraphModelConnection(Object element,
//			GraphNode source, GraphNode target) {
//		return super.addGraphModelConnection(element, source, target);
//	}
	
//	@SuppressWarnings("unchecked")
//	protected GraphNode addGraphModelNode(Object element, IFigure figure) {
////		if (element instanceof VanillaServer) {
////			GraphNode testNode = getGraphModelNode(element);
////			if (testNode != null)
////				return testNode;//we dont want to add if already existing
////			
////			ServerNode node = new ServerNode((Graph)getControl(), SWT.NONE, element);
////			getNodesMap().put(element, node);
////			node.setData(element);
////			
////			return node;
////		}
//		return super.addGraphModelNode(element, figure);
//	}
	
//	protected GraphConnection getGraphModelConnection(Object obj) {
//		return super.getGraphModelConnection(obj);
//	}
	
//	/*
//	 * Only to see if present?
//	 */
//	protected GraphNode getGraphModelNode(Object obj) {
//		System.out.println("getGraphModelNode on " + obj.getClass().getName());
//		
//		return super.getGraphModelNode(obj);
//	}
}
