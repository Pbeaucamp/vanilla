package bpm.vanillahub.web.client.properties.creation;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.beans.activities.SocialNetworkActivity;
import bpm.vanillahub.core.beans.activities.SocialNetworkActivity.SocialNetworkType;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer.FacebookMethod;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.vanillahub.web.shared.YoutubeTopic;
import bpm.vanillahub.web.shared.YoutubeTopic.Topic;
import bpm.vanillahub.web.shared.YoutubeTopic.TopicParent;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class SocialNetworkActivityProperties extends PropertiesPanel<Activity> implements IManager<SocialNetworkServer> {

	private ResourceManager resourceManager;
	private SocialNetworkActivity activity;

	private PropertiesListBox lstResources, lstMethods, lstParentTopics, lstTopics;
	private VariablePropertiesText txtQuery;
	
	private List<SocialNetworkServer> resources;
	
	public SocialNetworkActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, SocialNetworkActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.activity = activity;
		this.resourceManager = (ResourceManager) resourceManager;
		
		setNameChecker(creationPanel);
		setNameChanger(item);
		
		this.resources = this.resourceManager.getSocialServers();
		
		addVariableText(LabelsCommon.lblCnst.OutputFileName(), activity.getOutputFileVS(), WidgetWidth.PCT, null);
		
		List<ListItem> actions = new ArrayList<ListItem>();
		int i = 0;
		int selectedActionIndex = -1;
		for (SocialNetworkType type : SocialNetworkType.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getSocialNetworkType() == type) {
				selectedActionIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(Labels.lblCnst.SelectSocialType(), actions, WidgetWidth.PCT, changeType, null);
		if (selectedActionIndex != -1) {
			lstType.setSelectedIndex(selectedActionIndex);
		}
		else {
			activity.setSocialNetworkType(SocialNetworkType.FACEBOOK);
		}
		
		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if (resources != null) {
			i = 0;
			for (SocialNetworkServer resource : resources) {
				items.add(new ListItem(resource.getName(), resource.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstResources = addList(Labels.lblCnst.SelectSocialServer(), items, WidgetWidth.PCT, changeResource, refreshHandler);
		lstResources.setSelectedIndex(selectedIndex);
		
		lstMethods = addList(Labels.lblCnst.SelectMethod(), new ArrayList<ListItem>(), WidgetWidth.PCT, changeMethod, null);

		Topic selectedTopic = YoutubeTopic.getTopicByCode(activity.getTopic());
		
		List<ListItem> topicParents = new ArrayList<ListItem>();
		i = 0;
		for (TopicParent parent : TopicParent.values()) {
			topicParents.add(new ListItem(getLabel(parent), parent.getCode() + ""));

			if (selectedTopic != null && selectedTopic.getParent() == parent) {
				selectedIndex = i;
			}
			i++;
		}
		lstParentTopics = addList(Labels.lblCnst.SelectParentTopic(), topicParents, WidgetWidth.PCT, changeTopicParent, null);
		lstParentTopics.setSelectedIndex(selectedIndex);
		lstParentTopics.setVisible(false);
		
		TopicParent parent = selectedTopic != null ? selectedTopic.getParent() : TopicParent.MUSIC_TOPICS;
		
		List<ListItem> topics = new ArrayList<ListItem>();
		i = 0;
		for (Topic topic : YoutubeTopic.getTopicsByParent(parent)) {
			topics.add(new ListItem(getLabel(topic), topic.getCode() + ""));

			if (selectedTopic != null && selectedTopic == topic) {
				selectedIndex = i;
			}
			i++;
		}
		lstTopics = addList(Labels.lblCnst.SelectTopic(), topics, WidgetWidth.PCT, changeTopic, null);
		lstTopics.setSelectedIndex(selectedIndex);
		lstTopics.setVisible(false);
		
		txtQuery = addVariableText(Labels.lblCnst.Query(), activity.getQueryVS(), WidgetWidth.PCT, null);
		
		updateUi(activity.getSocialNetworkType());
	}

	private void updateUi(SocialNetworkType type) {
		List<ListItem> methods = new ArrayList<>();
		if (type == SocialNetworkType.FACEBOOK) {
//			methods.add(new ListItem(Labels.lblCnst.SearchPost(), Constants.FACEBOOK_METHOD_SEARCH));
			methods.add(new ListItem(Labels.lblCnst.SearchPlaces(), FacebookMethod.SEARCH_PLACES.getType()));
			
			lstMethods.setItems(methods);
			lstResources.setVisible(true);
			lstParentTopics.setVisible(false);
			lstTopics.setVisible(false);
		}
		else if (type == SocialNetworkType.TWITTER) {
			methods.add(new ListItem(Labels.lblCnst.Search(), Constants.TWITTER_METHOD_SEARCH));
			methods.add(new ListItem(Labels.lblCnst.GetTimeline(), Constants.TWITTER_METHOD_TIMELINE));
			
			lstMethods.setItems(methods);
			lstResources.setVisible(false);
			lstParentTopics.setVisible(false);
			lstTopics.setVisible(false);
		}
		else if (type == SocialNetworkType.YOUTUBE) {
			methods.add(new ListItem(Labels.lblCnst.Search(), Constants.YOUTUBE_METHOD_SEARCH_KEYWORD));
			methods.add(new ListItem(Labels.lblCnst.SearchByTopic(), Constants.YOUTUBE_METHOD_SEARCH_TOPIC));
			
			lstMethods.setItems(methods);
			lstResources.setVisible(true);
			lstParentTopics.setVisible(false);
			lstTopics.setVisible(false);
		}
		
		if (activity.getMethod() != null && activity.getMethod() < methods.size()) {
			lstMethods.setSelectedIndex(activity.getMethod());
			updateMethodUi(type, activity.getMethod());
		}
		else if (activity.getMethod() == null) {
			int method = Integer.parseInt(lstMethods.getValue(lstMethods.getSelectedIndex()));
			activity.setMethod(method);
		}
	}

	private void updateMethodUi(SocialNetworkType type, int method) {
		if (type == SocialNetworkType.FACEBOOK) {
			txtQuery.setPlaceHolder(Labels.lblCnst.Query());
		}
		else if (type == SocialNetworkType.TWITTER) {
			if (method == Constants.TWITTER_METHOD_SEARCH) {
				txtQuery.setPlaceHolder(Labels.lblCnst.Query());
			}
			else if (method == Constants.TWITTER_METHOD_TIMELINE) {
				txtQuery.setPlaceHolder(Labels.lblCnst.UserIdOrDisplayName());
			}
			else {
				txtQuery.setPlaceHolder(Labels.lblCnst.Query());
			}
		}
		else if (type == SocialNetworkType.YOUTUBE) {
			txtQuery.setPlaceHolder(Labels.lblCnst.Query());
			
			if (method == Constants.YOUTUBE_METHOD_SEARCH_KEYWORD) {
				lstParentTopics.setVisible(false);
				lstTopics.setVisible(false);
			}
			else if (method == Constants.YOUTUBE_METHOD_SEARCH_TOPIC) {
				lstParentTopics.setVisible(true);
				lstTopics.setVisible(true);
			}
		}
	}

	private SocialNetworkServer findResource(int cibleId) {
		if (resources != null) {
			for (SocialNetworkServer resource : resources) {
				if (resource.getId() == cibleId) {
					return resource;
				}
			}
		}
		return null;
	}

	@Override
	public boolean isValid() {
		boolean isValid = true;
		return isValid;
	}

	@Override
	public void loadResources(List<SocialNetworkServer> result) {
		this.resources = result;

		lstResources.clear();
		int selectedIndex = -1;
		if (resources != null) {
			int i = 0;
			for (SocialNetworkServer resource : resources) {
				lstResources.addItem(resource.getName(), String.valueOf(resource.getId()));

				if (activity.getResourceId() > 0 && activity.getResourceId() == resource.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstResources.setSelectedIndex(selectedIndex);
	}
	
	private void loadTopics(TopicParent parent) {
		Topic selectedTopic = YoutubeTopic.getTopicByCode(activity.getTopic());
		
		lstTopics.clear();
		int selectedIndex = -1;
		
		int i = 0;
		for (Topic topic : YoutubeTopic.getTopicsByParent(parent)) {
			lstTopics.addItem(getLabel(topic), topic.getCode());

			if (selectedTopic != null && selectedTopic == topic) {
				selectedIndex = i;
			}
			i++;
		}

		lstTopics.setSelectedIndex(selectedIndex);
	}

	@Override
	public Activity buildItem() {
		return activity;
	}

	private ChangeHandler changeType = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setSocialNetworkType(SocialNetworkType.valueOf(type));
			updateUi(activity.getSocialNetworkType());
		}
	};

	private ChangeHandler changeTopicParent = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			String topicParentId = lst.getValue(lst.getSelectedIndex());
			
			TopicParent parent = YoutubeTopic.getTopicParent(Integer.parseInt(topicParentId));
			loadTopics(parent);
		}
	};

	private ChangeHandler changeTopic = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			String topicId = lst.getValue(lst.getSelectedIndex());
			
			Topic topic = YoutubeTopic.getTopicByCode(topicId);
			activity.setTopic(topic.getCode());
		}
	};

	private ChangeHandler changeMethod = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int method = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setMethod(method);
			updateMethodUi(activity.getSocialNetworkType(), method);
		}
	};

	private ChangeHandler changeResource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int resourceId = Integer.parseInt(lstResources.getValue(lstResources.getSelectedIndex()));
			if (resourceId > 0) {
				activity.setResource(findResource(resourceId));
			}
		}
	};

	private ClickHandler refreshHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadSocialServers(SocialNetworkActivityProperties.this, SocialNetworkActivityProperties.this);
		}
	};

	@Override
	public void loadResources() { }

	//If we want to add I18n someday
	private String getLabel(TopicParent parent) {
		return parent.toString();
	}

	//If we want to add I18n someday
	private String getLabel(Topic topic) {
		return topic.toString();
	}
}
