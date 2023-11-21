package bpm.es.sessionmanager.views.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.api.UserWrapper;
import bpm.es.sessionmanager.zest.ClientFigure;
import bpm.es.sessionmanager.zest.ClientNode;
import bpm.es.sessionmanager.zest.ServerFigure;
import bpm.es.sessionmanager.zest.ServerNode;

public class VisualMappingComposite extends Composite  {
	private SessionManager manager;
	
	private GraphViewer viewer;
	private Graph graph;
	
	private GraphContainer container;
	
	public VisualMappingComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		//this.setBackground(ColorConstants.blue);
		this.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
	}
	
	
	
	public void setInput(SessionManager manager) throws Exception{
		createModel(manager);
	}
	
	public void refreshModel() throws Exception {
		createModel(manager);
	}
	
	private void createModel(SessionManager manager) throws Exception {
		if (container != null) {
			container.getGraph().dispose();
			container.dispose();
		}
		graph = new Graph(this, SWT.BORDER);
		graph.setLayout(new GridLayout());
		graph.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		graph.setNodeStyle(ZestStyles.NODES_FISHEYE);
		
		container = new GraphContainer(graph, SWT.NONE);
		
		//for server
		ServerFigure figure = new ServerFigure(manager.getVanillaServer());
		ServerNode serverNode = new ServerNode(container, SWT.NONE, figure);
		
		//for clients
		for (UserWrapper user : manager.getUsers()) {
			ClientFigure clFigure = new ClientFigure(user);
			ClientNode clNode = new ClientNode(container, SWT.NONE, clFigure);
			
			if (user.isConnected())
				new GraphConnection(container.getGraph(), SWT.NONE, clNode, serverNode);
		}

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);
		container.setLayoutAlgorithm(new SpringLayoutAlgorithm(), true);
		
		graph.getLayout();
		//graph.getLayoutAlgorithm().getEntityAspectRatio()
		
		List<Object> nodes = new ArrayList<Object>();
		nodes.addAll(graph.getNodes());
		
		for (int i = 0; i < nodes.size(); i++) {
			Object o = nodes.get(i);
			
			if (o instanceof GraphContainer) {
				((GraphContainer)graph.getNodes().get(i)).dispose();
			}
		}
		
		this.layout();
	}
}
