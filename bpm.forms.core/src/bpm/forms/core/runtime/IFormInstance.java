package bpm.forms.core.runtime;

import java.util.Date;
import java.util.List;

/**
 * Represent a running Form. It is created from a specific IFormDefinition of a Iform.
 * 
 * The IFormInstance has states : 
 *  - submited
 *  - validated
 *  
 *  The FieldMapping allow to store the values submited for each mapped fields
 * @author ludo
 *
 */
public interface IFormInstance {
	
	public long getFormDefinitionId();
	
	public Date getCreationDate();
	
	public int getGroupId();
	
	public List<IFormInstanceFieldState> getFieldsStates() throws Exception;
	
	
	public long getId();
	
	public boolean isSubmited();
	
	public boolean isValidated();
	
	

	public void setIsSubmited(boolean v);

	public void setIsValidated(boolean v);
	
	public String getSubmiterIp();
	
	public void setSubmiterIp(String ip);
	
	public String getValidatorIp();
	
	public void setValidatorIp(String ip);
	
	public boolean hasBeenValidated();
	
	public Date getLastSubmitionDate();
	
	public void setLastSubmitionDate(Date submitDate);
	
	public Date getLastValidationDate();
	
	public void setLastValidationDate(Date validationDate);
	
	public boolean hasBeenSubmited();

	public void setValidatorUserId(Integer id);

	public void setSubmiterUserId(Integer id);
	
	public Date getExpirationDate();
	
	public void setExpirationDate(Date date);

	public void setId(long id);

	public void setCreationDate(Date time);

	public void setGroupId(int groupId);

	public void setFormDefinitionId(long id);

	public void addFieldState(IFormInstanceFieldState iFormInstanceFieldState);
}
