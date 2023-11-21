package bpm.united.olap.runtime.tools;

import java.util.Properties;

import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.runtime.model.improver.FactoryLevelImprover;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class OdaInputOverrider {

	public static OdaInput override(OdaInput odaInput, IRuntimeContext context){
		
		OdaInput input = new OdaInput();
		input.setDatasetPrivateProperties((Properties)odaInput.getDatasetPrivateProperties().clone());
		input.setDatasetPublicProperties((Properties)odaInput.getDatasetPublicProperties().clone());
		
		input.setDatasourcePrivateProperties((Properties)odaInput.getDatasourcePrivateProperties().clone());
		input.setDatasourcePublicProperties((Properties)odaInput.getDatasourcePublicProperties().clone());
		
		input.setOdaExtensionDataSourceId(odaInput.getOdaExtensionDataSourceId());
		input.setName(odaInput.getOdaExtensionDataSourceId());
		input.setOdaExtensionId(odaInput.getOdaExtensionId());
		input.setQueryText(odaInput.getQueryText());
		
		//TODO ovveride
		if (input.getOdaExtensionDataSourceId().equals(FactoryLevelImprover.FMDT) && context != null){
			input.getDatasourcePublicProperties().setProperty("USER", context.getLogin());
			input.getDatasourcePublicProperties().setProperty("PASSWORD", context.getPassword());
			input.getDatasourcePublicProperties().setProperty("GROUP_NAME", context.getGroupName());
			input.getDatasourcePublicProperties().setProperty("GROUP_ID", context.getGroupId() + "");
			
		}
		if (context != null && input.getDatasourcePublicProperties() != null && input.getOdaExtensionDataSourceId().startsWith("bpm")){
			input.getDatasourcePublicProperties().setProperty("VANILLA_URL", ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
		}
		
		
		return input;
		
	}
}
