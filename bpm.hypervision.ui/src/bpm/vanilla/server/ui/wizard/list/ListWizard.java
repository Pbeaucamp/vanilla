package bpm.vanilla.server.ui.wizard.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskList;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.utils.HypervisionConstant;
import bpm.vanilla.server.ui.wizard.RepositoryContentWizardPage;

public class ListWizard extends Wizard {
	
	private static final String INFO_PAGE = "infoPage"; //$NON-NLS-1$
	private static final String CONTENT_PAGE = "contentPage"; //$NON-NLS-1$
	private static final String LIST_PAGE = "listPage"; //$NON-NLS-1$

	private RepositoryConnectionPage infoPage;
	private RepositoryContentWizardPage contentPage;
	private ListPage listPage;

	@Override
	public void addPages() {
		infoPage = new RepositoryConnectionPage(INFO_PAGE, true);
		addPage(infoPage);

		contentPage = new RepositoryContentWizardPage(CONTENT_PAGE);
		addPage(contentPage);

		listPage = new ListPage(LIST_PAGE);
		addPage(listPage);
	}
	
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		getShell().setText(Messages.ListWizard_3);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == infoPage) {

			IRepositoryApi sock = infoPage.getRepositoryConnection();

			try {
				contentPage.fillContent(new Repository(sock));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (page == contentPage) {
			IRepositoryApi sock = infoPage.getRepositoryConnection();
			List<RepositoryItem> selItems = new ArrayList<RepositoryItem>();

			for (Object o : contentPage.getDirectoryItem()) {
				if (o instanceof RepositoryItem) {
					selItems.add((RepositoryItem) o);
				}
			}

			listPage.setInput(selItems, sock);
		}

		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		TaskList taskList = new TaskList(Activator.getDefault().getServerType());
		boolean isReport = Activator.getDefault().getServerType() == ServerType.REPORTING;
		taskList.setName(infoPage.getListName());
		taskList.setRepositoryId(infoPage.getProperties().getProperty(HypervisionConstant.REPOSITORY_ID)); //$NON-NLS-1$

		List<RepositoryItem> dirIt = (List<RepositoryItem>) contentPage.getDirectoryItem();
		HashMap<RepositoryItem, HashMap<Parameter, String>> params = listPage.getParameters();
		for (RepositoryItem it : dirIt) {

			Properties repositoryProps = infoPage.getProperties();

			int directoryItemId = it.getId();

			String vanillaGroupId = repositoryProps.getProperty(HypervisionConstant.GROUP);
			String reportLocale = repositoryProps.getProperty(HypervisionConstant.LOCALE);
			String repositoryId = repositoryProps.getProperty(HypervisionConstant.REPOSITORY_ID);
			String alternateConnections = repositoryProps.getProperty(HypervisionConstant.ALTERNATE_CONNECTIONS);
			
			String reportFormat = ""; //$NON-NLS-1$
			if (listPage.getRunProperties().getProperty(HypervisionConstant.OUTPUT_FORMAT) != null) { //$NON-NLS-1$
				reportFormat = listPage.getRunProperties().getProperty(HypervisionConstant.OUTPUT_FORMAT); //$NON-NLS-1$
			}

			if (isReport) {
				ReportRuntimeConfig conf = new ReportRuntimeConfig();
				conf.setOutputFormat(reportFormat);
				if (reportLocale != null) {
					for (Locale l : Locale.getAvailableLocales()) {
						if (l.toString().equals(reportLocale)) {
							conf.setLocale(l);
							break;
						}
					}
				}

				conf.setObjectIdentifier(new ObjectIdentifier(Integer.parseInt(repositoryId), directoryItemId));
				if (vanillaGroupId != null) {
					conf.setVanillaGroupId(Integer.parseInt(vanillaGroupId));
				}

				// alternate connections
				if (alternateConnections != null) {
					for (String s : alternateConnections.split(";")) { //$NON-NLS-1$
						String[] keyPair = s.split("/"); //$NON-NLS-1$
						if (keyPair.length == 2) {
							conf.getAlternateConnectionsConfiguration().setConnection(keyPair[0], keyPair[1]);
						}
					}
				}

				// parameters
				List<VanillaGroupParameter> vParam = new ArrayList<VanillaGroupParameter>();
				for (Parameter prm : params.get(it).keySet()) {
					if (params.get(it).get(prm) != null) {
						VanillaGroupParameter g = new VanillaGroupParameter();
						VanillaParameter p = new VanillaParameter();
						p.setName(prm.getName());
						for (String s : params.get(it).get(prm).split("]")) { //$NON-NLS-1$
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
				conf.setParameters(vParam);
				taskList.addTask(conf);
			}
			else {
				GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration();
				conf.setObjectIdentifier(new ObjectIdentifier(Integer.parseInt(repositoryId), directoryItemId));
				if (vanillaGroupId != null) {
					conf.setVanillaGroupId(Integer.parseInt(vanillaGroupId));
				}

				// parameters
				List<VanillaGroupParameter> vParam = new ArrayList<VanillaGroupParameter>();
				for (Parameter prm : params.get(it).keySet()) {
					if (params.get(it).get(prm) != null) {
						VanillaGroupParameter g = new VanillaGroupParameter();
						VanillaParameter p = new VanillaParameter();
						p.setName(prm.getName());
						for (String s : params.get(it).get(prm).split("]")) { //$NON-NLS-1$
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
				conf.setParameters(vParam);
				taskList.addTask(conf);
			}

		}
		Activator.getDefault().getTaskListManager().addList(taskList);

		return true;
	}
}
