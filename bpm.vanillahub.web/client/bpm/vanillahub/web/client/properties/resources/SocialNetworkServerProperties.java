package bpm.vanillahub.web.client.properties.resources;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.web.client.I18N.Labels;

public class SocialNetworkServerProperties extends PropertiesPanel<Resource> implements NameChanger {

	private SocialNetworkServer cible;

	private boolean isNameValid = true;
	
	public SocialNetworkServerProperties(NameChecker dialog, IResourceManager resourceManager, SocialNetworkServer socialServer) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, socialServer != null ? socialServer.getId() : 0, socialServer != null ? socialServer.getName() : Labels.lblCnst.SocialServer(), true, true);
		
		this.cible = socialServer != null ? socialServer : new SocialNetworkServer(Labels.lblCnst.SocialServer());
		
		setNameChecker(dialog);
		setNameChanger(this);
		
		addVariableText(Labels.lblCnst.Token(), cible.getTokenVS(), WidgetWidth.SMALL_PX, null);
		
		checkName(getTxtName(), cible.getName());
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		cible.setName(value);
	}

	@Override
	public Resource buildItem() {
		return cible;
	}

	@Override
	public boolean isValid() {
		return isNameValid;
	}
}
