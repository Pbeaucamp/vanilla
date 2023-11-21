package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.google.gson.Gson;

import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.ValidationSchemaResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.MdmInputActivity;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.workflow.commons.beans.Result;

public class MdmInputActivityRunner extends ActivityRunner<MdmInputActivity> {

	private List<ApplicationServer> vanillaServers;
	
	public MdmInputActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, MdmInputActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<ApplicationServer> vanillaServers) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.vanillaServers = vanillaServers;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(vanillaServers);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(vanillaServers);
	}

	@Override
	protected void run(Locale locale) {
		try {
			VanillaServer server = (VanillaServer) activity.getResource(vanillaServers);
			boolean validateData = activity.validateData();

			String login = server.getLogin(getParameters(), getVariables());
			String password = server.getPassword(getParameters(), getVariables());
			String vanillaUrl = server.getUrl(getParameters(), getVariables());
			int groupId = Integer.parseInt(server.getGroupId().getString(getParameters(), getVariables()));
			int repositoryId = Integer.parseInt(server.getRepositoryId().getString(getParameters(), getVariables()));

			BaseVanillaContext vctx = new BaseVanillaContext(vanillaUrl, login, password);
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vctx);
			IGedComponent gedComponent = new RemoteGedComponent(vctx);
			IMdmProvider mdmComponent = new MdmRemote(login, password, vanillaUrl);

			User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(login);
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
			Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
			
			IRepositoryContext repositoryCtx = new BaseRepositoryContext(vctx, group, repository);
			IRepositoryApi repositoryApi = new RemoteRepositoryApi(repositoryCtx);

			Contract contract = null;
			if (activity.isFromMeta()) {
				
				List<MetaValue> values = new ArrayList<MetaValue>();
				if (activity.getMetaDispatch() != null) {
					for (MetaDispatch metaDispatch : activity.getMetaDispatch()) {
						values.add(new MetaValue(metaDispatch.getMetaKey(), metaDispatch.getValue(parameters, variables)));
					}
				}
				List<Integer> contractIds = repositoryApi.getMetaService().getItemsByMeta(TypeMetaLink.ARCHITECT, values);
				if (contractIds == null || contractIds.isEmpty()) {
					StringBuffer buf = new StringBuffer();
					buf.append(Labels.getLabel(locale, Labels.UnableToFindContractsCorrespondingTheFollowingFilters) + " (");
					if (values != null && !values.isEmpty()) {
						boolean first = true;
						for (MetaValue value : values) {
							if (!first) {
								buf.append(", ");
							}
							first = false;
							buf.append("'" + value.getMetaKey() + "': '" + value.getValue() + "'");
						}
					}
					else {
						buf.append(Labels.getLabel(locale, Labels.NoFilterDefine));
					}
					buf.append(")");

					throw new Exception(buf.toString());
				}
				
				int contractId = contractIds.get(0);
				contract = mdmComponent.getContract(contractId);		
				if (contract == null) {
					throw new Exception("Unable to find contract with id '" + contractId + "'");
				}
				
				addInfo(Labels.getLabel(locale, Labels.Found) + " " + contractIds.size() + " " + Labels.getLabel(locale, Labels.contractsAndSelectingTheFirst) + " '" + contract.getName() + "'");
			}
			else {			
				int contractId = activity.getContractId();
				contract = mdmComponent.getContract(contractId);		
				if (contract == null) {
					throw new Exception("Unable to find contract with id '" + contractId + "'");
				}
			}
			
			GedDocument doc = null;
			if (contract.getDocId() != null) {
				doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
				if (doc != null) {
					contract.setFileVersions(doc);
				}
			}

			doc = contract.getFileVersions();

			DocumentVersion version = doc.getLastVersion();
			String format = version.getFormat();
			
			
			// Metadata part
			
			IResultInformation infos = result.getInfosComp().get(TypeResultInformation.META_LINKS);
			List<MetaLink> links = infos != null && infos instanceof MetaLinksResultInformation ? ((MetaLinksResultInformation) infos).getLinks() : new ArrayList<MetaLink>();
			
//			 Checking is now done in the Cible activity as we validate against the data in the datastore and not the file from Architect
//			if (validateData) {
//				try {
//					// TODO: Improve CSV separator
//					String csvSeparator = contract.getType() == ContractType.LIMESURVEY ? ";" : ",";
//					
//					List<DocumentSchema> schemas = mdmComponent.getDocumentSchemas(contract.getId());
//					ValidationDataResult result = gedComponent.validateData(contract.getId(), doc, schemas, user.getId(), csvSeparator);
//					List<String> details = result.getDetails();
//					if (details != null) {
//						for (String detail : details) {
//							addInfo(detail);
//						}
//					}
//					
//					//Checking if there is RGPD data
//					if (result != null) {
//						if (result.getSchemaValidationResults() != null) {
//							for (ValidationSchemaResult schema : result.getSchemaValidationResults()) {
//								if (schema.getSchema().contains("rgpd")) {
//									Meta metaRgpd = new Meta();
//									metaRgpd.setKey("data_rgpd");
//									
//									MetaLink linkRgpd = new MetaLink(metaRgpd);
//									linkRgpd.setValue(new MetaValue("data_rgpd", schema.getNbLinesError() > 0 ? "true" : "false"));
//									
//									links.add(linkRgpd);
//								}
//							}
//						}
//						
//						// We had the validation result to the metadata
//						Gson gson = new Gson();
//						String dataValidation = gson.toJson(result);
////						JSONObject item = new JSONObject(result);
//						
//						Meta metaRgpd = new Meta();
//						metaRgpd.setKey("data_validation");
//						
//						MetaLink linkRgpd = new MetaLink(metaRgpd);
//						linkRgpd.setValue(new MetaValue("data_validation", dataValidation));
//						
//						links.add(linkRgpd);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					addError(e.getMessage());
//					//TODO: Add error of check in metadata
//				}
//			}
			

			//Getting meta bound to the contract
			List<MetaLink> contractMeta = repositoryApi.getMetaService().getMetaLinks(contract.getId(), TypeMetaLink.ARCHITECT, true);
			logger.info("Found " + (contractMeta != null ? contractMeta.size() : 0) + " associated meta");
			links.addAll(contractMeta != null ? contractMeta : new ArrayList<MetaLink>());
			
			Meta metaContract = new Meta();
			metaContract.setKey("vanilla_contract");

			MetaLink linkContractId = new MetaLink(metaContract);
			linkContractId.setValue(new MetaValue("vanilla_contract", String.valueOf(contract.getId())));
			links.add(linkContractId);
			result.putInfoComp(TypeResultInformation.META_LINKS, new MetaLinksResultInformation(links));
			
			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(doc, user.getId(), version.getVersion());

			InputStream is = gedComponent.loadGedDocument(config);
			byte currentXMLBytes[] = IOUtils.toByteArray(is);

			String outputFileName = doc.getName().endsWith(format) ? doc.getName() : doc.getName() + "." + format;
			ByteArrayInputStream bis = new ByteArrayInputStream(currentXMLBytes);

			result.setFileName(outputFileName);
			result.setInputStream(bis);

			clearResources();
			result.setResult(Result.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			result.setResult(Result.ERROR);
		}
	}

	@Override
	protected void clearResources() {

	}

}
