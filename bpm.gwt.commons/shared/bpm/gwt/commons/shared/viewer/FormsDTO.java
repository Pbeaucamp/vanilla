package bpm.gwt.commons.shared.viewer;

import java.util.Date;

import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author ere
 * added to support Forms in portal.
 * dto class for info holding
 *
 */
public class FormsDTO implements IRepositoryObject, IsSerializable {

	private static final long serialVersionUID = 1L;

	public static enum PortalFormType{
		Workflow, VanillaFormSubmission, VanillaFormValidation
	}
	
	private PortalFormType type;
	private String formName;
	private Date createdOn;
	private String htmlFormUrl;
	private String originName;
	
	public FormsDTO() {
		
	}

	public FormsDTO(PortalFormType type, String formName, Date createdOn,
			String htmlFormUrl, String originName) {
		this.type = type;
		this.formName = formName;
		this.createdOn = createdOn;
		this.htmlFormUrl = htmlFormUrl;
		this.originName = originName;
	}

	public PortalFormType getType() {
		return type;
	}

	@Override
	public String getName() {
		return formName;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public String getHtmlFormUrl() {
		return htmlFormUrl;
	}

	public String getOriginName() {
		return originName;
	}

	public boolean isVanillaFormSubmit() {
		if (type == PortalFormType.VanillaFormSubmission) 
			return true;
		
		return false;
	}
	
	public boolean isVanillaFormValidate() {
		if (type == PortalFormType.VanillaFormValidation) 
			return true;
		
		return false;
	}
	
	public boolean isWorkflowForm() {
		if (type == PortalFormType.Workflow) 
			return true;
		
		return false;
	}

	@Override
	public int getId() {
		return 0;
	}
}
