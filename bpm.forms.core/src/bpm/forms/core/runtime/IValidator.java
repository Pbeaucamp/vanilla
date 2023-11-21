package bpm.forms.core.runtime;

import java.util.Properties;

import bpm.vanilla.platform.core.IRepositoryContext;

public interface IValidator {

	public void invalidate(IFormInstance formInstance, Properties properties, IRepositoryContext vanillaContext) throws Exception;
	public void validate(IFormInstance formInstance, Properties fieldValues, IRepositoryContext profil) throws Exception;
	
}
