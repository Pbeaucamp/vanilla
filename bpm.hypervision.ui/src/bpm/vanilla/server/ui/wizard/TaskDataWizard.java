package bpm.vanilla.server.ui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.tasks.TaskList;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.ui.utils.HypervisionConstant;
import bpm.vanilla.server.ui.wizard.list.RepositoryConnectionPage;

public class TaskDataWizard extends RunReportWizard {
	
	private static final String CONNECTION_PAGE = "connection"; //$NON-NLS-1$
	private static final String CONTENT_PAGE = "content"; //$NON-NLS-1$
	private static final String PARAMETERS_PAGE = "parameters"; //$NON-NLS-1$
	private static final String RUN_PAGE = "run"; //$NON-NLS-1$
	
	private TaskList list;
	
	public TaskDataWizard(TaskList list) {
		this.list = list;
	}
	
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

		runPage = new RunningOptionPage(RUN_PAGE);
		addPage(runPage);
	}

	@Override
	public boolean performFinish() {
		Properties connectionProperties = connectionPage.getProperties();
		Properties runProperties = runPage.getRunProperties();

		HashMap<Parameter, String> params = parameterPage.getParametersValues();
		
		RepositoryItem it = (RepositoryItem)contentPage.getDirectoryItem().get(0);
		
		int directoryItemId = it.getId();

		String alternateConnections = null;
		String reportLocale = null;
		String repositoryId = connectionProperties.getProperty(HypervisionConstant.REPOSITORY_ID);
		String vanillaGroupId = connectionProperties.getProperty(HypervisionConstant.GROUP);

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

		if (it.getType() == IRepositoryApi.GTW_TYPE){
			GatewayRuntimeConfiguration conf = new GatewayRuntimeConfiguration();
			conf.setObjectIdentifier(new ObjectIdentifier(Integer.parseInt(repositoryId), directoryItemId));
			if (vanillaGroupId != null) {
				conf.setVanillaGroupId(Integer.parseInt(vanillaGroupId));
			}
			conf.setParameters(vParam);
			list.addTask(conf);
		}
		else {
			ReportRuntimeConfig conf = new ReportRuntimeConfig();
			conf.setOutputFormat(outputFormat);
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
			conf.setParameters(vParam);
			list.addTask(conf);
		}
		
		return true;
	}

}
