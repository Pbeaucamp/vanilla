package bpm.vanilla.server.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.internal.GedStoreTool;
import bpm.vanilla.server.ui.utils.HypervisionConstant;
import bpm.vanilla.server.ui.wizard.list.RepositoryConnectionPage;

public class RunReportWizard extends Wizard {

	private static final String CONNECTION_PAGE = "connection"; //$NON-NLS-1$
	private static final String CONTENT_PAGE = "content"; //$NON-NLS-1$
	private static final String PARAMETERS_PAGE = "parameters"; //$NON-NLS-1$
	private static final String GROUP_PAGE = "goup"; //$NON-NLS-1$
	private static final String RUN_PAGE = "run"; //$NON-NLS-1$

	protected RepositoryConnectionPage connectionPage;
	protected RepositoryContentWizardPage contentPage;
	protected DirectoryItemParameterPage parameterPage;
	protected VanillaGroupsWizardPage groupPage;
	protected RunningOptionPage runPage;

	protected IRepositoryApi sock;
	protected IRepository repository;
	protected RepositoryItem item;
	protected List<Parameter> params;

	@Override
	public void addPages() {
		try {
			connectionPage = new RepositoryConnectionPage(CONNECTION_PAGE, false);
			addPage(connectionPage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		contentPage = new RepositoryContentWizardPage(CONTENT_PAGE);
		addPage(contentPage);

		parameterPage = new DirectoryItemParameterPage(PARAMETERS_PAGE);
		addPage(parameterPage);

		groupPage = new VanillaGroupsWizardPage(GROUP_PAGE);
		addPage(groupPage);

		runPage = new RunningOptionPage(RUN_PAGE);
		addPage(runPage);

		super.addPages();
	}

	@Override
	public boolean performFinish() {
		Properties connectionProperties = connectionPage.getProperties();
		Properties runProperties = runPage.getRunProperties();
		
		HashMap<Parameter, String> params = parameterPage.getParametersValues();

		RepositoryItem it = (RepositoryItem) contentPage.getDirectoryItem().get(0);
		String repositoryId = connectionProperties.getProperty(HypervisionConstant.REPOSITORY_ID);

		String outputFormat = ""; //$NON-NLS-1$
		if (runProperties.getProperty(HypervisionConstant.OUTPUT_FORMAT) != null){ //$NON-NLS-1$
			outputFormat = runProperties.getProperty(HypervisionConstant.OUTPUT_FORMAT); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// parameters
		List<VanillaGroupParameter> vParam = new ArrayList<VanillaGroupParameter>();
		for (Parameter prm : params.keySet()) {
			if (params.get(prm) != null) {
				VanillaGroupParameter g = new VanillaGroupParameter();
				VanillaParameter p = new VanillaParameter();
				p.setName(prm.getName());
				for (String s : params.get(prm).split("]")) { //$NON-NLS-1$
					p.addSelectedValue(s);
				}
				try {
					g.addParameter(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
				vParam.add(g);
			}
		}

		HashMap<String, Integer> taskIdByGroup = new HashMap<String, Integer>();

		for (Group grp : groupPage.getGroups()) {
			try {
				User user = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getUser();
				runItem(new ObjectIdentifier(Integer.parseInt(repositoryId), it.getId()), outputFormat, vParam, grp.getId(), user);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		String vanillaUrl = ""; //$NON-NLS-1$
		try {
			vanillaUrl = Activator.getDefault().getVanillaUrl();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (runProperties.getProperty(HypervisionConstant.OUTPUT_NAME) != null) {
			if (Activator.getDefault().getServerType() == ServerType.REPORTING) {
				GedStoreTool thread = new GedStoreTool(taskIdByGroup, runProperties.getProperty(HypervisionConstant.OUTPUT_NAME), vanillaUrl, 
						connectionPage.getProperties().getProperty(HypervisionConstant.REPOSITORY_ID), runProperties.getProperty(HypervisionConstant.OUTPUT_FORMAT), 
						(ReportingComponent)Activator.getDefault().getRemoteServerManager(), item, sock);
				thread.start();
			}
		}

		return true;
	}

	private void runItem(IObjectIdentifier objectIdentifier, String outputFormat, List<VanillaGroupParameter> parameters, Integer vanillaGroupId, User user) throws Exception {
		if (Activator.getDefault().getServerType() == ServerType.REPORTING) {
			ReportRuntimeConfig config = new ReportRuntimeConfig();
			config.setObjectIdentifier(objectIdentifier);
			config.setOutputFormat(outputFormat);
			config.setParameters(parameters);

			if (vanillaGroupId != null) {
				config.setVanillaGroupId(vanillaGroupId);
			}
			
			ReportingComponent reportComp = (ReportingComponent) Activator.getDefault().getRemoteServerManager();
			reportComp.runReportAsynch(config, user);
		}
		else if(Activator.getDefault().getServerType() == ServerType.GATEWAY) {
			GatewayRuntimeConfiguration config = new GatewayRuntimeConfiguration();
			config.setObjectIdentifier(objectIdentifier);
			config.setParameters(parameters);

			if (vanillaGroupId != null) {
				config.setVanillaGroupId(vanillaGroupId);
			}
			
			GatewayComponent gatewayComp = (GatewayComponent) Activator.getDefault().getRemoteServerManager();
			gatewayComp.runGatewayAsynch(config, user);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.
	 * IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (!page.isPageComplete()) {
			return super.getNextPage(page);
		}

		if (page == connectionPage) {

			boolean loadRep = false;
			if (sock == null || sock.equals(connectionPage.getRepositoryConnection())) {
				sock = connectionPage.getRepositoryConnection();
				loadRep = true;
			}

			if (loadRep) {

				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

					public void run() {
						try {
							repository = new Repository(sock);
						} catch (Exception e) {
							repository = null;
							e.printStackTrace();
							MessageDialog.openError(getShell(), Messages.RunReportWizard_19, Messages.RunReportWizard_20 + e.getCause().getMessage());

						}
						contentPage.fillContent(repository);
						if (groupPage != null) {
							try {

								List<Group> groups = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
								groupPage.fillContent(groups);
							} catch (Exception ex) {
								ex.printStackTrace();
								MessageDialog.openError(getShell(), Messages.RunReportWizard_21, Messages.RunReportWizard_22 + ex.getMessage());
							}

						}

					}
				});

			}

			if (repository == null) {
				return page;
			}
			return contentPage;
		}

		if (page == contentPage) {
			boolean loadParams = false;

			if (item == null || item != contentPage.getDirectoryItem()) {
				loadParams = true;

				for (Object o : contentPage.getDirectoryItem()) {
					if (o instanceof RepositoryItem) {
						item = (RepositoryItem) o;
					}
				}

				try {
					if (loadParams) {
						params = sock.getRepositoryService().getParameters(item);
						parameterPage.setParameters(params);

					}
					return parameterPage;
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RunReportWizard_23, Messages.RunReportWizard_24 + ex.getCause().getMessage());
					return page;

				}

			}
		}

		return super.getNextPage(page);
	}

}
