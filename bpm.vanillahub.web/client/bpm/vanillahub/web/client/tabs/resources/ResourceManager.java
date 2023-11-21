package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.core.beans.resources.Connector;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.web.client.services.ResourcesService;
import bpm.workflow.commons.resources.Cible;

public class ResourceManager implements IResourceManager {

	private List<User> users;
	private List<Cible> cibles;
	private List<Certificat> certificats;
	private List<Source> sources;
	private List<FileXSD> fileXSDs;
	private List<ServerMail> serverMails;
	private List<Variable> variables;
	private List<Parameter> parameters;
	private List<DatabaseServer> databaseServers;
	private List<Connector> connectors;
	private List<SocialNetworkServer> socialServers;
	private List<ListOfValues> listOfValues;
	private List<ApplicationServer> applicationServers;

	public List<User> getUsers() {
		return users;
	}

	@Override
	public List<Cible> getCibles() {
		return cibles;
	}

	public List<Certificat> getCertificats() {
		return certificats;
	}

	public List<Source> getSources() {
		return sources;
	}

	public List<FileXSD> getFileXSDs() {
		return fileXSDs;
	}

	public List<ServerMail> getServerMails() {
		return serverMails;
	}

	@Override
	public List<Variable> getVariables() {
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	@Override
	public List<DatabaseServer> getDatabaseServers() {
		return databaseServers;
	}
	
	public List<ListOfValues> getListOfValues() {
		return listOfValues;
	}
	
	public List<Connector> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<Connector> connectors) {
		this.connectors = connectors;
	}

	public List<SocialNetworkServer> getSocialServers() {
		return socialServers;
	}

	public List<ApplicationServer> getApplicationServers() {
		return applicationServers;
	}

	public List<VanillaServer> getVanillaServers(List<ApplicationServer> applicationServers) {
		List<VanillaServer> servers = new ArrayList<VanillaServer>();
		if (applicationServers != null) {
			for (ApplicationServer appServer : applicationServers) {
				if (appServer instanceof VanillaServer) {
					servers.add((VanillaServer) appServer);
				}
			}
		}
		return servers;
	}

	public List<AklaboxServer> getAklaboxServers(List<ApplicationServer> applicationServers) {
		List<AklaboxServer> servers = new ArrayList<AklaboxServer>();
		if (applicationServers != null) {
			for (ApplicationServer appServer : applicationServers) {
				if (appServer instanceof AklaboxServer) {
					servers.add((AklaboxServer) appServer);
				}
			}
		}
		return servers;
	}

	public List<LimeSurveyServer> getLimeSurveyServers(List<ApplicationServer> applicationServers) {
		List<LimeSurveyServer> servers = new ArrayList<LimeSurveyServer>();
		if (applicationServers != null) {
			for (ApplicationServer appServer : applicationServers) {
				if (appServer instanceof LimeSurveyServer) {
					servers.add((LimeSurveyServer) appServer);
				}
			}
		}
		return servers;
	}

	public void loadUsers(IWait waitPanel, final IManager<User> manager) {
		waitPanel.showWaitPart(true);

		ResourcesService.Connect.getInstance().getUsers(TypeResource.USER, new GwtCallbackWrapper<List<User>>(waitPanel, true) {

			@Override
			public void onSuccess(List<User> result) {
				users = (List<User>) result;

				if (manager != null) {
					manager.loadResources(users);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void loadCibles(IWait waitPanel, final IManager<Cible> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.CIBLE, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				cibles = (List<Cible>) result;

				if (manager != null) {
					manager.loadResources(cibles);
				}
			}
		}.getAsyncCallback());
	}

	public void loadCertificats(IWait waitPanel, final IManager<Certificat> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.CERTIFICAT, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				certificats = (List<Certificat>) result;

				if (manager != null) {
					manager.loadResources(certificats);
				}
			}
		}.getAsyncCallback());
	}

	public void loadSources(IWait waitPanel, final IManager<Source> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.SOURCE, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				sources = (List<Source>) result;

				if (manager != null) {
					manager.loadResources(sources);
				}
			}
		}.getAsyncCallback());
	}

	public void loadXSDs(IWait waitPanel, final IManager<FileXSD> manager) {
		waitPanel.showWaitPart(true);
		
		WorkflowsService.Connect.getInstance().getResources(TypeResource.XSD, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				fileXSDs = (List<FileXSD>) result;

				if (manager != null) {
					manager.loadResources(fileXSDs);
				}
			}
		}.getAsyncCallback());
	}

	public void loadServerMails(IWait waitPanel, final IManager<ServerMail> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.MAIL, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				serverMails = (List<ServerMail>) result;

				if (manager != null) {
					manager.loadResources(serverMails);
				}
			}
		}.getAsyncCallback());
	}
	
	public void loadSocialServers(IWait waitPanel, final IManager<SocialNetworkServer> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.SOCIAL_SERVER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				socialServers = (List<SocialNetworkServer>) result;

				if (manager != null) {
					manager.loadResources(socialServers);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void loadVariables(IWait waitPanel, final IManager<Variable> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.VARIABLE, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				variables = (List<Variable>) result;

				if (manager != null) {
					manager.loadResources(variables);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void loadParameters(IWait waitPanel, final IManager<Parameter> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.PARAMETER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				parameters = (List<Parameter>) result;

				if (manager != null) {
					manager.loadResources(parameters);
				}
			}
		}.getAsyncCallback());
	}

	public void loadDatabaseServers(IWait waitPanel, final IManager<DatabaseServer> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.DATABASE_SERVER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				databaseServers = (List<DatabaseServer>) result;

				if (manager != null) {
					manager.loadResources(databaseServers);
				}
			}
		}.getAsyncCallback());
	}

	public void loadNetworkSocialServers(IWait waitPanel, final IManager<SocialNetworkServer> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.SOCIAL_SERVER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				socialServers = (List<SocialNetworkServer>) result;

				if (manager != null) {
					manager.loadResources(socialServers);
				}
			}
		}.getAsyncCallback());
	}
	
	public void loadApplicationServers(IWait waitPanel, final IManager<ApplicationServer> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.APPLICATION_SERVER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				applicationServers = (List<ApplicationServer>) result;

				if (manager != null) {
					manager.loadResources(applicationServers);
				}
			}
		}.getAsyncCallback());
	}

	public void loadListOfValues(IWait waitPanel, final IManager<ListOfValues> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.LOV, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				listOfValues = (List<ListOfValues>) result;

				if (manager != null) {
					manager.loadResources(listOfValues);
				}
			}
		}.getAsyncCallback());
	}
}
