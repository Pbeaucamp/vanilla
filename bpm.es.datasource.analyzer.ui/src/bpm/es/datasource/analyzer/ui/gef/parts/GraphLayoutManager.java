package bpm.es.datasource.analyzer.ui.gef.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;

public class GraphLayoutManager extends AbstractLayout{
	private DiagramPart diagram;

	public GraphLayoutManager(DiagramPart part){
		diagram = part;
	}
	
	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		container.validate();
		List children = container.getChildren();
		Rectangle result =	new Rectangle().setLocation(container.getClientArea().getLocation());
		for (int i = 0; i < children.size(); i++){
			result.union(((IFigure)children.get(i)).getBounds());
		}
			
		result.resize(container.getInsets().getWidth(), container.getInsets().getHeight());
		return result.getSize();
	}

	public void layout(IFigure container) {
		CompoundDirectedGraph graph = new CompoundDirectedGraph();
		Map partsToNodes = new HashMap();
		
		new CompoundDirectedGraphLayout().visit(graph);
			
		
		for(Object o : diagram.getChildren()){
			graph.nodes.add(o);
			
			if (o instanceof DataSourcePart){
				
				for(Object _o : diagram.getChildren()){
					if (_o instanceof ItemPart){
						Edge e = new Edge(diagram, (Node)_o, (Node)o);
						e.weight = 2;
						graph.edges.add(e);
					}
				}
			}
		}
		
		
		
	}
	
	
}
