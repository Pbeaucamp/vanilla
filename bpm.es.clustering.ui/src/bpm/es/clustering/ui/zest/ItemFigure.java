package bpm.es.clustering.ui.zest;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import bpm.es.clustering.ui.composites.VisualMappingComposite;
import bpm.es.clustering.ui.icons.Icons;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;

public class ItemFigure extends RoundedRectangle {

	private Label serverName;
	private TextFlow started;

	private Object content;

	public ItemFigure(Object element) {
		this.content = element;

		this.setLineWidth(1);
		this.setOpaque(true);
		this.setLineStyle(Graphics.LINE_SOLID);
		this.setLayoutManager(new GridLayout());

		if (element instanceof String) {
			buildStringItem((String) element);
		}
		else if (element instanceof Repository) {
			buildRepositoryItem((Repository) element);
		}
		else if (element instanceof VanillaPlatformModule) {
			buildVanillaPlatformModuleItem((VanillaPlatformModule) element);
		}
		else if (element instanceof Server) {
			buildServerItem((Server) element);
		}
		else if (element instanceof IVanillaServerManager) {
			buildServerManagerItem((IVanillaServerManager) element);
		}
	}

	private void buildStringItem(String part) {
		serverName = new Label(part);
		serverName.setLabelAlignment(PositionConstants.CENTER);
		serverName.setIconAlignment(PositionConstants.LEFT);

		// Label image = new Label (shell, SWT.BORDER);
		// image.setIcon(findIcon(part, false));
		// image.setTextAlignment(align)

		serverName.setIcon(findIcon(part, false));
		serverName.setTextAlignment(PositionConstants.CENTER);
		this.add(serverName);
	}

	private void buildServerManagerItem(IVanillaServerManager serverManager) {
		if (serverManager instanceof ReportingComponent) {
			serverName = new Label(ServerType.REPORTING.getTypeName());
			serverName.setLabelAlignment(PositionConstants.CENTER);
			serverName.setIconAlignment(PositionConstants.LEFT);

			serverName.setIcon(findIcon(serverManager, false));
			this.add(serverName);
		}
		else if (serverManager instanceof GatewayComponent) {
			serverName = new Label(ServerType.GATEWAY.getTypeName());
			serverName.setLabelAlignment(PositionConstants.CENTER);
			serverName.setIconAlignment(PositionConstants.LEFT);

			serverName.setIcon(findIcon(serverManager, false));
			this.add(serverName);
		}
	}

	private void buildServerItem(Server server) {
		serverName = new Label(server.getName());
		serverName.setLabelAlignment(PositionConstants.CENTER);
		serverName.setIconAlignment(PositionConstants.LEFT);
		if (server.getComponentStatus() != null) {
			serverName.setIcon(findIcon(server, true));
			this.add(serverName);

			started = new TextFlow(server.getComponentStatus());
			FlowPage fp = new FlowPage();
			fp.add(started);
			this.add(fp);
		}
		else {
			serverName.setIcon(findIcon(server, false));
			this.add(serverName);
		}
	}

	private void buildVanillaPlatformModuleItem(VanillaPlatformModule platformModule) {
		serverName = new Label(platformModule.getName());
		serverName.setLabelAlignment(PositionConstants.CENTER);
		serverName.setIconAlignment(PositionConstants.LEFT);

		serverName.setIcon(findIcon(platformModule, false));
		this.add(serverName);
	}

	private void buildRepositoryItem(Repository repository) {
		serverName = new Label(repository.getName());
		serverName.setLabelAlignment(PositionConstants.CENTER);
		serverName.setIconAlignment(PositionConstants.LEFT);

		serverName.setIcon(findIcon(repository, false));
		this.add(serverName);
	}

	private Image findIcon(Object element, boolean isStarted) {
		ImageRegistry reg = bpm.es.clustering.ui.Activator.getDefault().getImageRegistry();
		if (element instanceof VanillaPlatformModule) {
			return reg.get(Icons.SERVER);
		}
		else if (element instanceof Server) {
			Server s = (Server) element;
			if (VanillaComponentType.COMPONENT_FREEANALYSISWEB.equals(s.getComponentNature())) {
				return reg.get(Icons.FAWEB);
			}
			else if (VanillaComponentType.COMPONENT_FREEWEBREPORT.equals(s.getComponentNature())) {
				return reg.get(Icons.FWR);
			}
			else if (VanillaComponentType.COMPONENT_WORKFLOW.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.WORKFLOW);
				}
				else {
					return reg.get(Icons.WORKFLOW_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_SEARCH.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.SEARCH);
				}
				else {
					return reg.get(Icons.SEARCH_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus()))
					return reg.get(Icons.BIG);
				else
					return reg.get(Icons.BIG_STOP);
			}
			else if (VanillaComponentType.COMPONENT_FREEDASHBOARD.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.FD);
				}
				else {
					return reg.get(Icons.FD_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_VANILLA_FORMS.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.FORM);
				}
				else {
					return reg.get(Icons.FORM_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_GED.equals(s.getComponentNature())) {
				if (Status.STARTED.equals(s.getComponentStatus()))
					return reg.get(Icons.GED);
				else
					return reg.get(Icons.GED_STOP);
			}
			else if (VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus())) {
					return reg.get(Icons.REPORT);
				}
				else {
					return reg.get(Icons.REPORT_STOP);
				}
			}
			else if (VanillaComponentType.COMPONENT_FREEMETRICS.equals(s.getComponentNature())) {
				return reg.get(Icons.FM);
			}
			else if (VanillaComponentType.COMPONENT_UNITEDOLAP.equals(s.getComponentNature())) {
				if (Status.STARTED.getStatus().equals(s.getComponentStatus()))
					return reg.get(Icons.FAWEB);
				else
					return reg.get(Icons.FAWEB_STOP);
			}
			else {
				if (s.getName().equals("Orbeon")) { //$NON-NLS-1$
					return reg.get(Icons.ORBEON);
				}
				else if (s.getName().equals("BirtViewer")) { //$NON-NLS-1$
					return reg.get(Icons.BIRT);
				}
			}

		}
		else if (element instanceof Repository) {
			return reg.get(Icons.REPOSITORY);
		}
		else if (element instanceof IVanillaServerManager) {
			if ((IVanillaServerManager) element instanceof ReportingComponent) {
				return reg.get(Icons.REPORT);
			}
			else if ((IVanillaServerManager) element instanceof GatewayComponent) {
				return reg.get(Icons.BIG);
			}
		}
		else if (element instanceof String) {
			if (((String) element).equals(VisualMappingComposite.CLUSTERS) || ((String) element).equals(VisualMappingComposite.MASTER)) {
				return reg.get(Icons.MASTER);
			}
			else if (((String) element).equals(VisualMappingComposite.MODULES_RUNTIME) || ((String) element).equals(VisualMappingComposite.REPOSITORIES)) {// ((String) element).equals(VisualMappingComposite.WEBAPPS) ) {
				return reg.get(Icons.ITEMS);
			}
		}

		return reg.get(Icons.DEFAULT_TREE);
	}

	public Object getContent() {
		return content;
	}

}
