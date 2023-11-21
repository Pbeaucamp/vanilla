package bpm.vanillahub.web.client.properties.resources;

import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.web.client.I18N.Labels;

public class XSDResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private FileXSD cible;

	private FileUploadWidget fileUpload;

	private boolean isNameValid = true;

	public XSDResourceProperties(NameChecker dialog, IResourceManager resourceManager, FileXSD myFileXSD) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.LARGE_PX, myFileXSD != null ? myFileXSD.getId() : 0, myFileXSD != null ? myFileXSD.getName() : Labels.lblCnst.FileXSD(), false, true);
		this.cible = myFileXSD != null ? myFileXSD : new FileXSD(Labels.lblCnst.FileXSD());

		setNameChecker(dialog);
		setNameChanger(this);

		fileUpload = addFileUpload(CommonConstants.HUB_UPLOAD_SERVLET, Labels.lblCnst.File(), cible.getFile(), WidgetWidth.LARGE_PX);

		checkName(getTxtName(), cible.getName());
	}

	@Override
	public boolean isValid() {
		return isNameValid && fileUpload.getFileName() != null && !fileUpload.getFileName().isEmpty();
	}

	@Override
	public Resource buildItem() {
		String file = fileUpload.getFileName();
		
		cible.setFile(file);
		return cible;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		cible.setName(value);
	}
}
