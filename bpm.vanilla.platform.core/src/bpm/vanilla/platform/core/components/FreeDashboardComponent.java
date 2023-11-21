package bpm.vanilla.platform.core.components;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.components.fd.IFdFormRuntimeConfig;

public interface FreeDashboardComponent {
	
	/**
	 * actionName with the header to deploy a FD from a Repository
	 */
	public static final String ACTION_DEPLOY = "bpm.vanilla.platform.core.components.FreeDashboardComponent.deploy";
	/**
	 * actionName with the header to deploy a FD from a Zio
	 */
	public static final String ACTION_DEPLOY_ZIP = "bpm.vanilla.platform.core.components.FreeDashboardComponent.deployZip";
	
	/**
	 * actionName with the header to deploy a FDForm from a Repository
	 */
	public static final String ACTION_DEPLOY_FORM = "bpm.vanilla.platform.core.components.FreeDashboardComponent.deployForm";
	
	public static final String ACTION_DEPLOY_VALIDATION_FORM= "bpm.vanilla.platform.core.components.FreeDashboardComponent.deployValidationForm";
	
	public static final String ACTION_RUN_ODA_REPORT = "bpm.vanilla.platform.core.components.FreeDashboardComponent.runOdaReport";
	
	public static final String ACTION_GET_FOLDER_PAGES = "bpm.vanilla.platform.core.components.FreeDashboardComponent.getFolderPages";
	
	public static final String ACTION_GET_ACTUAL_FOLDER_PAGE = "bpm.vanilla.platform.core.components.FreeDashboardComponent.getActualFolderPage";
	
//	public static enum ActionType  implements IXmlActionType{
//		RUN_FROM_ODA_DATASOURCE 
//	}

	
	public static final String SERVLET_DEPLOY = "/freedashboardRuntime/deploy";
//	public static final String SERVLET_PREGENERATE_BIRT = "/freedashboardRuntime/";
	public static final String SERVLET_DEPLOY_FORM = "/freedashboardRuntime/deployForm";
	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
	
	public static final String SERVLET_PREGENERATE_BIRT = "/freedashboardRuntime/birtPregeneration";
	public static final String SERVLET_VALIDATE_FORM = "/freedashboardRuntime/vanillaValidationFormDeployer";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/generateFromFaWeb";
	public static final String SERVLET_JSP = "/freedashboardRuntime/*.jsp";
	
	public static final String SERVLET_PARAMETER = "/freedashboardRuntime/setParameter";
	public static final String SERVLET_POPUP_MODEL = "/freedashboardRuntime/popupHTML";
	public static final String SERVLET_COMPONENT = "/freedashboardRuntime/getComponentHTML";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
//	public static final String SERVLET_DEPLOY_ZIP = "/freedashboardRuntime/deployZip";
	public static final String SERVLET_LIST_DIRTY_COMPONENTS = "/freedashboardRuntime/dirtyComponents";
	public static final String SERVLET_DRILL = "/freedashboardRuntime/drillComponent";
	public static final String SERVLET_SLICER = "/freedashboardRuntime/applySlicer";
	public static final String SERVLET_EXPORT = "/freedashboardRuntime/exportDashboard";
	public static final String SERVLET_FOLDER = "/freedashboardRuntime/folderServlet";
	
	public static final String SERVLET_WEB_DASHBOARD = "/freedashboardRuntime/webDashboardServlet";
	
	
	public String deployDashboard(IRuntimeConfig config) throws Exception;
	public String deployDashboard(IRuntimeConfig config, InputStream modelFiles) throws Exception;
	public String deployForm(IFdFormRuntimeConfig config) throws Exception;
	public String deployValidationForm(IFdFormRuntimeConfig config) throws Exception;
	public String pregenerateDashboard(IRuntimeConfig config) throws Exception;
	
	public List<String> getFolderPages(String uid) throws Exception;
	public String getActualFolderPage(String uid) throws Exception;
	
	public InputStream runOdaReport(IRuntimeConfig config, InputStream odaInputModelDefinition) throws Exception;
}
