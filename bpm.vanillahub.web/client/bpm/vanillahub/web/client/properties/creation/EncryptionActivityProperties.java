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
import bpm.vanillahub.core.beans.activities.EncryptionActivity;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class EncryptionActivityProperties extends PropertiesPanel<Activity> implements IManager<Certificat> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private EncryptionActivity activity;

	private List<Certificat> certificats;
	private PropertiesListBox lstCertificats;

	public EncryptionActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, EncryptionActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), false, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);
		
		this.certificats = this.resourceManager.getCertificats();
		
		List<ListItem> items = new ArrayList<ListItem>();
		int selectedIndex = -1;
		if(certificats != null) {
			int i = 0;
			for(Certificat certificat : certificats) {
				items.add(new ListItem(certificat.getName(), certificat.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == certificat.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCertificats = addList(Labels.lblCnst.SelectCertificat(), items, WidgetWidth.PCT, changeCertificat, refreshHandler);
		lstCertificats.setSelectedIndex(selectedIndex);
		
		addCheckbox(Labels.lblCnst.Encryption(), activity.isEncryption(), encryptionChangeHandler);
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
	}

	private Certificat findCertificat(int certificatId) {
		if (certificats != null) {
			for (Certificat certificat : certificats) {
				if (certificat.getId() == certificatId) {
					return certificat;
				}
			}
		}
		return null;
	}

	private ChangeHandler changeCertificat = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			int certificatId = Integer.parseInt(lstCertificats.getValue(lstCertificats.getSelectedIndex()));
			if (certificatId > 0) {
				activity.setResource(findCertificat(certificatId));
			}
		}
	};
	
	private ClickHandler refreshHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			resourceManager.loadCertificats(EncryptionActivityProperties.this, EncryptionActivityProperties.this);
		}
	};

	private ValueChangeHandler<Boolean> encryptionChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setEncryption(event.getValue());
		}
	};

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	@Override
	public void loadResources(List<Certificat> result) {
		this.certificats = result;
	
		lstCertificats.clear();
		int selectedIndex = -1;
		if(certificats != null) {
			int i = 0;
			for(Certificat certificat : certificats) {
				lstCertificats.addItem(certificat.getName(), String.valueOf(certificat.getId()));
				
				if (activity.getResourceId() > 0 && activity.getResourceId() == certificat.getId()) {
					selectedIndex = i;
				}
				i++;
			}
		}

		lstCertificats.setSelectedIndex(selectedIndex);
	}

	@Override
	public void loadResources() { }

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
}
