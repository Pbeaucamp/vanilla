package bpm.es.clustering.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.gef.GefModel;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.es.clustering.ui.zest.ItemFigure;
import bpm.es.clustering.ui.zest.ItemNode;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;

public class VisualMappingComposite extends Composite  {

	public static final String MASTER = Messages.VisualMappingComposite_0;
	public static final String CLUSTERS = Messages.VisualMappingComposite_1;
	public static final String REPOSITORIES = Messages.ModuleViewerContentProvider_0;
	public static final String MODULES_RUNTIME = Messages.VisualMappingComposite_2;
//	public static final String WEBAPPS = Messages.VisualMappingComposite_3;

	private GraphContainer container;
	private Graph graph;
	
	private GefModel gefModel;
	
	public VisualMappingComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
	}
	
	public void setInput(GefModel gefModel) {
		this.gefModel = gefModel;
		createModel(gefModel);
	}
	
	public void refreshModel() throws Exception {
		createModel(gefModel);
	}
	
	private void createModel(GefModel gefModel) {
		if (container != null) {
			container.getGraph().dispose();
			container.dispose();
		}
		graph = new Graph(this, SWT.BORDER);
		graph.setLayout(new GridLayout());
		graph.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		graph.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		graph.setNodeStyle(ZestStyles.NODES_FISHEYE);
		
		container = new GraphContainer(graph, SWT.NONE);
		
		
		buildTree(gefModel);

		graph.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(), true);
		container.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(), true);
		
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

	private void buildTree(GefModel gefModel) {
		ItemFigure master = new ItemFigure(MASTER);
		ItemNode masterNode = new ItemNode(container, SWT.NONE, master);
		
//		ItemFigure webapps = new ItemFigure(WEBAPPS);
//		ItemNode webappsNode = new ItemNode(container, SWT.NONE, webapps);
//		
//		for (Server webapp : gefModel.getClients()) {
//			ItemFigure repFigure = new ItemFigure(webapp);
//			ItemNode repNode = new ItemNode(container, SWT.NONE, repFigure);
//			
//			new GraphConnection(container.getGraph(), SWT.NONE, webappsNode, repNode);
//		}
		
		ItemFigure repositories = new ItemFigure(REPOSITORIES);
		ItemNode repositoriesNode = new ItemNode(container, SWT.NONE, repositories);
		
		for (Repository repository : gefModel.getRepositories()) {
			ItemFigure repFigure = new ItemFigure(repository);
			ItemNode repNode = new ItemNode(container, SWT.NONE, repFigure);
			
			new GraphConnection(container.getGraph(), SWT.NONE, repositoriesNode, repNode);
		}
		
		ItemFigure runtimes = new ItemFigure(MODULES_RUNTIME);
		ItemNode runtimesNode = new ItemNode(container, SWT.NONE, runtimes);
		
		VanillaPlatformModule defaultModule = gefModel.getDefaultModule();
		if(defaultModule.getRegisteredModules() != null) {
			for(Server runtime : defaultModule.getRegisteredModules()) {
				ItemFigure runFigure = new ItemFigure(runtime);
				ItemNode runNode = new ItemNode(container, SWT.NONE, runFigure);
				
				new GraphConnection(container.getGraph(), SWT.NONE, runtimesNode, runNode);
			}
		}
		
		ItemFigure clusters = new ItemFigure(CLUSTERS);
		ItemNode clustersNode = new ItemNode(container, SWT.NONE, clusters);
		
		if(gefModel.getModules() != null) {
			for(VanillaPlatformModule registeredModule : gefModel.getModules()) {

				ItemFigure cluster = new ItemFigure(registeredModule);
				ItemNode clusterNode = new ItemNode(container, SWT.NONE, cluster);
				
				new GraphConnection(container.getGraph(), SWT.NONE, clustersNode, clusterNode);

				ItemFigure runtimesCluster = new ItemFigure(MODULES_RUNTIME);
				ItemNode runtimesClusterNode = new ItemNode(container, SWT.NONE, runtimesCluster);
				
				new GraphConnection(container.getGraph(), SWT.NONE, clusterNode, runtimesClusterNode);
				
				if(registeredModule.getRegisteredModules() != null) {
					for(Server runtime : registeredModule.getRegisteredModules()) {
						ItemFigure runFigure = new ItemFigure(runtime);
						ItemNode runNode = new ItemNode(container, SWT.NONE, runFigure);
						
						new GraphConnection(container.getGraph(), SWT.NONE, runtimesClusterNode, runNode);
					}
				}
			}
		}

//		new GraphConnection(container.getGraph(), SWT.NONE, masterNode, webappsNode);
		new GraphConnection(container.getGraph(), SWT.NONE, masterNode, repositoriesNode);
		new GraphConnection(container.getGraph(), SWT.NONE, masterNode, runtimesNode);
		new GraphConnection(container.getGraph(), SWT.NONE, masterNode, clustersNode);
	}

	public Graph getViewer() {
		return graph;
	}
}
