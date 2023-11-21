package bpm.vanilla.platform.core.runtime.components;

import java.util.List;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IVanillaWebServiceComponent;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.core.runtime.dao.service.VanillaWebServiceDAO;

public class VanillaWebServiceComponent extends AbstractVanillaManager implements IVanillaWebServiceComponent {

	private VanillaWebServiceDAO vanillaWebServiceDAO;

	@Override
	public void activate(ComponentContext ctx) {

		try {
			super.activate(ctx);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {
		getLogger().info("VanillaWebServiceComponent inited");
		vanillaWebServiceDAO = getDao().getWebServiceDAO();
	}

	@Override
	public void addWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception {
		vanillaWebServiceDAO.saveServiceDefinition(definition);
	}

	@Override
	public void deleteWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception {
		vanillaWebServiceDAO.deleteServiceDefinition(definition.getId());
	}

	@Override
	public void deleteWebServiceDefinition(int definitionId) throws Exception {
		vanillaWebServiceDAO.deleteServiceDefinition(definitionId);
	}

	@Override
	public ServiceTransformationDefinition getWebServiceDefinition(int defintionId) throws Exception {
		return vanillaWebServiceDAO.getServiceDefinition(defintionId);
	}

	@Override
	public List<ServiceTransformationDefinition> getWebServiceDefinitions() throws Exception {
		return vanillaWebServiceDAO.getServiceDefinitions();
	}

	@Override
	public void updateWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception {
		vanillaWebServiceDAO.updateServiceDefinition(definition);
	}

}
