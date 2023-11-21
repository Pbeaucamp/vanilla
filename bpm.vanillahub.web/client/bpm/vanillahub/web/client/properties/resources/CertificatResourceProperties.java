package bpm.vanillahub.web.client.properties.resources;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

import bpm.gwt.commons.client.panels.upload.FileUploadWidget;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.NameChanger;
import bpm.gwt.workflow.commons.client.NameChecker;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesText;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.core.beans.resources.Certificat.TypeCertificat;
import bpm.vanillahub.web.client.I18N.Labels;

public class CertificatResourceProperties extends PropertiesPanel<Resource> implements NameChanger {

	private Certificat certificat;

	private FileUploadWidget fileUpload;
	private VariablePropertiesText txtTechnicalName;
	private PropertiesText txtPrivateKey;

	private boolean isNameValid = true;

	public CertificatResourceProperties(NameChecker nameChecker, IResourceManager resourceManager, Certificat myCertificat) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.SMALL_PX, myCertificat != null ? myCertificat.getId() : 0, myCertificat != null ? myCertificat.getName() : Labels.lblCnst.Certificat(), true, true);
		this.certificat = myCertificat != null ? myCertificat : new Certificat(Labels.lblCnst.Certificat());

		setNameChecker(nameChecker);
		setNameChanger(this);

		List<ListItem> items = new ArrayList<ListItem>();
		int i = 0;
		int selectedIndex = -1;
		for (TypeCertificat type : TypeCertificat.values()) {
			items.add(new ListItem(type.toString(), type.getType()));

			if (certificat.getTypeCertificat() == type) {
				selectedIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(null, items, WidgetWidth.SMALL_PX, changeTypeCertificat, null);
		if (selectedIndex != -1) {
			lstType.setSelectedIndex(selectedIndex);
		}

		fileUpload = addFileUpload(CommonConstants.HUB_UPLOAD_SERVLET, Labels.lblCnst.File(), certificat.getFile(), WidgetWidth.SMALL_PX);
		txtTechnicalName = addVariableText(Labels.lblCnst.TechnicalName(), certificat.getTechnicalNameVS(), WidgetWidth.SMALL_PX, null);

		txtPrivateKey = addText(Labels.lblCnst.PasswordAlsoUsedForPrivateKey(), certificat.getPassword(), WidgetWidth.SMALL_PX, true);

		updateUi(certificat.getTypeCertificat());
		checkName(getTxtName(), certificat.getName());
	}

	private void updateUi(TypeCertificat typeCertificat) {
		if (typeCertificat == TypeCertificat.OPEN_PGP) {
			fileUpload.setVisible(true);
			txtTechnicalName.setVisible(true);
		}
		else if (typeCertificat == TypeCertificat.PRIVATE_KEY) {
			fileUpload.setVisible(false);
			txtTechnicalName.setVisible(false);
		}
	}

	private ChangeHandler changeTypeCertificat = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			certificat.setTypeCertificat(TypeCertificat.valueOf(type));
			updateUi(certificat.getTypeCertificat());
		}
	};

	@Override
	public boolean isValid() {
		boolean isValid = true;
		if (certificat.getTypeCertificat() == TypeCertificat.OPEN_PGP) {
			if (fileUpload.getFileName() == null || fileUpload.getFileName().isEmpty()) {
				isValid = false;
				fileUpload.setError(Labels.lblCnst.NeedUploadFile());
			}
			else {
				fileUpload.setError("");
			}

			if (txtTechnicalName.getText().isEmpty()) {
				isValid = false;
				txtTechnicalName.setTxtError(Labels.lblCnst.TechnicalNameIsNeeded());
			}
			else {
				txtTechnicalName.setTxtError("");
			}
		}
		else if (certificat.getTypeCertificat() == TypeCertificat.PRIVATE_KEY) {
			if (txtPrivateKey.getText().isEmpty()) {
				isValid = false;
				txtPrivateKey.setTxtError(Labels.lblCnst.PrivateKeyIsNeeded());
			}
			else {
				txtPrivateKey.setTxtError("");
			}
		}
		return isNameValid && isValid;
	}

	@Override
	public void changeName(String value, boolean isValid) {
		this.isNameValid = isValid;
		certificat.setName(value);
	}

	@Override
	public Resource buildItem() {
		String file = fileUpload.getFileName();
		VariableString technicalName = txtTechnicalName.getVariableText();

		String password = txtPrivateKey.getText();

		certificat.setFile(file);
		certificat.setTechnicalName(technicalName);
		certificat.setPassword(password);

		return certificat;
	}
}
