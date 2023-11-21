package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.SimpleKPIGenerationInformations;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class TestClass {

	public static void main(String[] args) {
		buildSimpleKpiHub();
	}
	
	private static void buildSimpleKpiHub() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		IVanillaContext vanillaCtx = new BaseVanillaContext(url, login, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		
		SimpleKPIGenerationInformations infos = new SimpleKPIGenerationInformations();
		infos.setSourceOrganisation("databfc-observatoire-demo");
		infos.setSourceDatasetId("1011_nb_antenne_epci");
		
		infos.setD4cUrl("http://localhost:1001");
		infos.setD4cLogin("user_api");
		infos.setD4cPassword("FJijyY9sfrck8QdE");
		
		infos.setTargetOrganisation("databfc-observatoire-demo");
		infos.setTargetDatasetName("20221103_join_and_kpi_v1");
		infos.setTargetDatasetDescription("Description test");

//		infos.setMetadata(metadata);
//		infos.setValidationSchemas(validationSchemas);
		
		infos.setSqlQuery("SELECT join1.id, join1.nom_epci, join1.adm_lb_nom, join1.generation, join2._id, join2.connexion, join2.description, join1.id + join2._id as kpi1, join1.id / 10 as kpi2\r\n" + 
				"FROM \"66539587-2737-4c70-bf0f-f944cedc26f4\" as join1\r\n" + 
				"LEFT JOIN \"06d1aa93-50a5-4d36-8265-5ffddb427cc1\" as join2 ON join1.generation = join2.connexion;");
		infos.setFolderTargetId(103);

		IRepositoryContext ctx = getRepositoryContext(vanillaCtx, 4, 1);
		try {
//			vanillaApi.getResourceManager().generateSimpleKPI(ctx, infos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void buildKpiHub() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		IVanillaContext vanillaCtx = new BaseVanillaContext(url, login, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
		
		KPIGenerationInformations infos = new KPIGenerationInformations();
		infos.setObservatory("observatoire_test_v1");
		infos.setTheme("theme_test_v1");
		
//		infos.setD4cUrl("http://localhost:1001");
		infos.setDatasetId("0824_test_v1");
		infos.setResourceId("4a1399a3-28a2-4e19-aa8f-dfb41a69660f");
//		infos.setResourceUrl("http://localhost:1001/sites/default/files/dataset/2022/08/30/8963c2d6-1a8d-4280-8936-6a83717f89c5/arnia_nb_antennes_epci_bfc_telco_v5.csv");
		
		List<String> axes = new ArrayList<String>();
		axes.add("nom_epci");
		axes.add("adm_lb_nom");
		axes.add("generation");
		infos.setAxes(axes);
		
		List<String> metrics = new ArrayList<String>();
		metrics.add("id");
		infos.setMetrics(metrics);
		
		IRepositoryContext ctx = getRepositoryContext(vanillaCtx, 1, 1);
		try {
			vanillaApi.getResourceManager().generateKPI(ctx, infos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static IRepositoryContext getRepositoryContext(IVanillaContext vanillaContext, int groupId, int repositoryId) {
		Group group = new Group();
		group.setId(groupId);

		Repository repository = new Repository();
		repository.setId(repositoryId);
		
		return new BaseRepositoryContext(vanillaContext, group, repository);
	}
}
